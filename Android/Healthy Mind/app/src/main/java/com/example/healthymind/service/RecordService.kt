package com.example.healthymind.service

import android.app.Notification
import android.app.Service
import androidx.annotation.RequiresApi
import android.os.Build
import android.media.MediaRecorder
import androidx.documentfile.provider.DocumentFile
import com.example.healthymind.service.WavRecorder
import com.example.healthymind.ui.all.OverviewFragment
import android.content.Intent
import android.os.IBinder
import com.example.healthymind.util.UserPreferences
import com.example.healthymind.App
import android.widget.Toast
import com.example.healthymind.R
import com.example.healthymind.entity.Recording
import com.example.healthymind.util.MySharedPreferences
import com.example.healthymind.service.RecordService
import android.media.AudioManager
import android.os.Environment
import com.example.healthymind.helper.FileHelper
import android.os.ParcelFileDescriptor
import android.text.format.DateFormat
import android.util.Log
import com.example.healthymind.analysis.Recognition
import com.example.healthymind.audio.MFCC
import com.example.healthymind.audio.WavFile
import com.example.healthymind.auth.SessionManager
import com.example.healthymind.util.Constants
import com.google.firebase.database.FirebaseDatabase
import com.jlibrosa.audio.wavFile.WavFileException
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.lang.IllegalStateException
import java.lang.RuntimeException
import java.math.RoundingMode
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(api = Build.VERSION_CODES.O)
class RecordService : Service() {
    private val formatter = SimpleDateFormat("yyyyMMddHHmmss", Locale.US)
    private var recorder: MediaRecorder? = null
    private var phoneNumber: String? = null
    private var file: DocumentFile? = null
    private var onCall = false
    private var recording = false
    private val onForeground = false
    private var idCall: String? = null
//    var path = Environment.getExternalStorageDirectory().toString() + "/audioData/" + System.currentTimeMillis() + ".wav"
    var path = Environment.getExternalStorageDirectory().toString() + "/audioData/" + "recording.wav"
    var wavRecorder = WavRecorder(path)

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(1, Notification())
        //        depression_prediction.analyzePredictions();
    }

    fun predictDepression(audioFilePath: String): String? {

        val mNumFrames: Int
        val mSampleRate: Int
        val mChannels: Int
        var meanMFCCValues: FloatArray = FloatArray(1)

        var predictedResult: String? = "Unknown"

        var wavFile: WavFile? = null
        try {
            wavFile = WavFile.openWavFile(File(audioFilePath))
            mNumFrames = wavFile.numFrames.toInt()
            mSampleRate = wavFile.sampleRate.toInt()
            mChannels = wavFile.numChannels
            val buffer =
                    Array(mChannels) { DoubleArray(mNumFrames) }

            var frameOffset = 0
            val loopCounter: Int = mNumFrames * mChannels / 4096 + 1
            for (i in 0 until loopCounter) {
                frameOffset = wavFile.readFrames(buffer, mNumFrames, frameOffset)
            }

            //trimming the magnitude values to 5 decimal digits
            val df = DecimalFormat("#.#####")
            df.setRoundingMode(RoundingMode.CEILING)
            val meanBuffer = DoubleArray(mNumFrames)
            for (q in 0 until mNumFrames) {
                var frameVal = 0.0
                for (p in 0 until mChannels) {
                    frameVal = frameVal + buffer[p][q]
                }
                meanBuffer[q] = df.format(frameVal / mChannels).toDouble()
            }


            //MFCC java library.
            val mfccConvert = MFCC()
            mfccConvert.setSampleRate(mSampleRate)
            val nMFCC = 40
            mfccConvert.setN_mfcc(nMFCC)
            val mfccInput = mfccConvert.process(meanBuffer)
            val nFFT = mfccInput.size / nMFCC
            val mfccValues =
                    Array(nMFCC) { DoubleArray(nFFT) }

            //loop to convert the mfcc values into multi-dimensional array
            for (i in 0 until nFFT) {
                var indexCounter = i * nMFCC
                val rowIndexValue = i % nFFT
                for (j in 0 until nMFCC) {
                    mfccValues[j][rowIndexValue] = mfccInput[indexCounter].toDouble()
                    indexCounter++
                }
            }

            //code to take the mean of mfcc values across the rows such that
            //[nMFCC x nFFT] matrix would be converted into
            //[nMFCC x 1] dimension - which would act as an input to tflite model
            meanMFCCValues = FloatArray(nMFCC)
            for (p in 0 until nMFCC) {
                var fftValAcrossRow = 0.0
                for (q in 0 until nFFT) {
                    fftValAcrossRow = fftValAcrossRow + mfccValues[p][q]
                }
                val fftMeanValAcrossRow = fftValAcrossRow / nFFT
                meanMFCCValues[p] = fftMeanValAcrossRow.toFloat()
            }


        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: WavFileException) {
            e.printStackTrace()
        }

        predictedResult = loadModelAndMakePredictions(meanMFCCValues)

        return predictedResult

    }

    protected fun loadModelAndMakePredictions(meanMFCCValues: FloatArray): String? {

        var predictedResult: String? = "unknown"

        //load the TFLite model in 'MappedByteBuffer' format using TF Interpreter
        val tfliteModel: MappedByteBuffer =
                FileUtil.loadMappedFile(this, getModelPath())
        val tflite: Interpreter

        /** Options for configuring the Interpreter.  */
        val tfliteOptions =
                Interpreter.Options()
        tfliteOptions.setNumThreads(2)
        tflite = Interpreter(tfliteModel, tfliteOptions)

        //obtain the input and output tensor size required by the model
        //for urban sound classification, input tensor should be of 1x40x1x1 shape
        val imageTensorIndex = 0
        val imageShape =
                tflite.getInputTensor(imageTensorIndex).shape()
        val imageDataType: DataType = tflite.getInputTensor(imageTensorIndex).dataType()
        val probabilityTensorIndex = 0
        val probabilityShape =
                tflite.getOutputTensor(probabilityTensorIndex).shape()
        val probabilityDataType: DataType =
                tflite.getOutputTensor(probabilityTensorIndex).dataType()

        //need to transform the MFCC 1d float buffer into 1x40x1x1 dimension tensor using TensorBuffer
        val inBuffer: TensorBuffer = TensorBuffer.createDynamic(imageDataType)
        inBuffer.loadArray(meanMFCCValues, imageShape)
        val inpBuffer: ByteBuffer = inBuffer.getBuffer()
        val outputTensorBuffer: TensorBuffer =
                TensorBuffer.createFixedSize(probabilityShape, probabilityDataType)
        //run the predictions with input and output buffer tensors to get probability values across the labels
        tflite.run(inpBuffer, outputTensorBuffer.getBuffer())


        //Code to transform the probability predictions into label values
        val ASSOCIATED_AXIS_LABELS = "labels2.txt"
        var associatedAxisLabels: List<String?>? = null
        try {
            associatedAxisLabels = FileUtil.loadLabels(this, ASSOCIATED_AXIS_LABELS)
        } catch (e: IOException) {
            Log.e("tfliteSupport", "Error reading label file", e)
        }

        //Tensor processor for processing the probability values and to sort them based on the descending order of probabilities
        val probabilityProcessor: TensorProcessor = TensorProcessor.Builder()
                .add(NormalizeOp(0.0f, 255.0f)).build()
        if (null != associatedAxisLabels) {
            // Map of labels and their corresponding probability
            val labels = TensorLabel(
                    associatedAxisLabels,
                    probabilityProcessor.process(outputTensorBuffer)
            )

            // Create a map to access the result based on label
            val floatMap: Map<String, Float> =
                    labels.getMapWithFloatValue()

            //function to retrieve the top K probability values, in this case 'k' value is 1.
            //retrieved values are storied in 'Recognition' object with label details.
            val resultPrediction: List<Recognition>? = getTopKProbability(floatMap);

            //get the top 1 prediction from the retrieved list of top predictions
            predictedResult = getPredictedValue(resultPrediction)

        }
        return predictedResult

    }

    fun getPredictedValue(predictedList: List<Recognition>?): String? {
        val top1PredictedValue: Recognition? = predictedList?.get(0)
        return top1PredictedValue?.getTitle()
    }

    fun getModelPath(): String {
        return "model.tflite"
    }

    /** Gets the top-k results.  */
    protected fun getTopKProbability(labelProb: Map<String, Float>): List<Recognition>? {
        // Find the best classifications.
        val MAX_RESULTS: Int = 1
        val pq: PriorityQueue<Recognition> = PriorityQueue(
                MAX_RESULTS,
                Comparator<Recognition> { lhs, rhs -> // Intentionally reversed to put high confidence at the head of the queue.
                    java.lang.Float.compare(rhs.getConfidence(), lhs.getConfidence())
                })
        for (entry in labelProb.entries) {
            pq.add(Recognition("" + entry.key, entry.key, entry.value))
        }
        val recognitions: ArrayList<Recognition> = ArrayList()
        val recognitionsSize: Int = Math.min(pq.size, MAX_RESULTS)
        for (i in 0 until recognitionsSize) {
            recognitions.add(pq.poll())
        }
        return recognitions
    }

    fun analyzePredictions() {
        val externalStorage: File = Environment.getExternalStorageDirectory()
        val audioDirPath = externalStorage.absolutePath + "/audioData";
        val fileNames: MutableList<String> = ArrayList()

        File(audioDirPath).walk().forEach {

            if (it.absolutePath.endsWith(".wav")) {
                fileNames.add(it.name)
            }
        }

        // Loop over all the recordings in the audioDir path &
        // display the depression results

        var i = 0
        var x = 0

        val file_array = arrayOf(fileNames)

        for (file in file_array) {
            val file_size = file.size
            while (i < file_size) {
                val audio_files = file_array[0][i]

                val full_audioPath = audioDirPath + "/" + audio_files
                val predicted_result = predictDepression(full_audioPath)
//                println("Depression result is: " + predicted_result)

                // Store depression result to DB
                val sessionManager = SessionManager(this, SessionManager.SESSION_REMEMBERME)

                if (sessionManager.checkRememberMe()) {
                    val rememberMeDetails = sessionManager.rememberMeDetailFromSession
                    val username = rememberMeDetails[SessionManager.KEY_SESSIONUSERNAME]
                    val patientRef = FirebaseDatabase
                            .getInstance()
                            .getReference(Constants.FIREBASE_CHILD_DEPRESSION_LEVELS)
                            .child(username)
                            .child("depression_levels")

//                        val pushRef = patientRef.child("prediction_" + i)
                    patientRef.push().setValue(predicted_result)
                }
                i++

            }
        }

    }



    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(Constants.TAG, "RecordService onStartCommand")
        //        wavRecorder.getRecordingState();
        if (intent == null) return START_NOT_STICKY
        val commandType = intent.getIntExtra("commandType", 0)
        if (commandType == 0) return START_NOT_STICKY
        val enabled = UserPreferences.getEnabled()
        when (commandType) {
            Constants.RECORDING_ENABLED -> {
                Log.d(Constants.TAG, "RecordService RECORDING_ENABLED")
                if (enabled && phoneNumber != null && onCall && !recording) {
                    Log.d(Constants.TAG, "RecordService STATE_START_RECORDING")
                    idCall = formatter.format(Date())
                    startRecordingBySource()
                }
            }
            Constants.RECORDING_DISABLED -> {
                Log.d(Constants.TAG, "RecordService RECORDING_DISABLED")
                if (onCall && phoneNumber != null && recording) {
                    Log.d(Constants.TAG, "RecordService STATE_STOP_RECORDING")
                    stopAndReleaseRecorder()
                    //                    wavRecorder.stopRecording();
                    recording = false
                }
            }
            Constants.STATE_INCOMING_NUMBER -> {
                Log.d(Constants.TAG, "RecordService STATE_INCOMING_NUMBER")
                if (phoneNumber == null) phoneNumber = intent.getStringExtra("phoneNumber")
            }
            Constants.STATE_CALL_START -> {
                Log.d(Constants.TAG, "RecordService STATE_CALL_START")
                onCall = true
                if (enabled && phoneNumber != null && !recording) {
                    idCall = formatter.format(Date())
                    wavRecorder.startRecording()
                    startRecordingBySource()
                }
            }
            Constants.STATE_CALL_END -> {
                Log.d(Constants.TAG, "RecordService STATE_CALL_END")
                onCall = false
                phoneNumber = null
                recording = false
                wavRecorder.stopRecording()
                stopAndReleaseRecorder()

                analyzePredictions()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(Constants.TAG, "RecordService onDestroy")
        stopAndReleaseRecorder()
        //        wavRecorder.stopRecording();

//        wavRecorder.releaseRecord();
        super.onDestroy()
    }

    /// In case it is impossible to record
    private fun terminateAndEraseFile() {
        Log.d(Constants.TAG, "RecordService terminateAndEraseFile")
        stopAndReleaseRecorder()
        recording = false
        if (file != null) deleteFile()
    }

    private fun deleteFile() {
        Log.d(Constants.TAG, "RecordService deleteFile")
        file!!.delete()
        file = null
    }

    private fun stopAndReleaseRecorder() {
        if (recorder == null) return
        Log.d(Constants.TAG, "RecordService stopAndReleaseRecorder")
        var recorderStopped = false
        var exception = false
        try {
            recorder!!.stop()
            recorderStopped = true
        } catch (e: IllegalStateException) {
            Log.d(Constants.TAG, "RecordService: Failed to stop recorder.  Perhaps it wasn't started?", e)
            exception = true
        } catch (e: RuntimeException) {
            Log.d(Constants.TAG, "RecordService: Failed to stop recorder.  RuntimeException", e)
            exception = true
        }
        recorder!!.reset()
        recorder!!.release()
        recorder = null
        if (exception) {
            App.isOutComming = false
            deleteFile()
        }
        if (recorderStopped) {
            Toast.makeText(this, this.getString(R.string.receiver_end_call),
                    Toast.LENGTH_SHORT)
                    .show()


//            wavRecorder.stopRecording();
            val recording = Recording()
            recording.idCall = idCall
            recording.isOutGoing = App.isOutComming
            App.isOutComming = false
            recording.save()
            file = null
            Log.d(Constants.TAG, "RecordService save")
        }
    }

    private fun startRecordingBySource() {
        var exception = false
        val source = MySharedPreferences.getInstance(this).getInt(MySharedPreferences.KEY_AUDIO_SOURCE, 0)
        Log.d(Constants.TAG, "RecordService source: $source")
        if (source != 0) {
            exception = startRecording(source)
        } else {
//            exception = startRecording(MediaRecorder.AudioSource.VOICE_CALL);
            if (exception) {
                exception = startRecording(MediaRecorder.AudioSource.MIC)
                if (!exception) {
                    audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
                    audioManager!!.setStreamVolume(3, audioManager!!.getStreamMaxVolume(3), 0)
                    audioManager!!.mode = AudioManager.MODE_IN_CALL
                    audioManager!!.isSpeakerphoneOn = false
                }
            }
        }
        if (exception) {
            App.isOutComming = false
            Log.e(Constants.TAG, "Failed to set up recorder.")
            terminateAndEraseFile()
            val toast = Toast.makeText(this,
                    this.getString(R.string.record_impossible),
                    Toast.LENGTH_LONG)
            toast.show()
        }
    }

    private fun startRecording(source: Int): Boolean {
        Log.d(Constants.TAG, "RecordService startRecording")
        if (recorder == null) {
            recorder = MediaRecorder()
        }
        try {
            recorder!!.reset()
            recorder!!.setAudioSource(source)
            //            recorder.setAudioSamplingRate(8000);
//            recorder.setAudioEncodingBitRate(12200);
            recorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            recorder!!.setAudioChannels(1)
            recorder!!.setAudioSamplingRate(44100)
            recorder!!.setAudioEncodingBitRate(1024 * 1024)
            val date = DateFormat.format("yyyyMMddHHmmss", Date()) as String
            //            wavRecorder.getFileName(date);
            if (file == null) {
                file = FileHelper.getFile(this, phoneNumber!!)
            }
            val fd = contentResolver
                    .openFileDescriptor(file!!.uri, "w")
                    ?: throw Exception("Failed open recording file.")
            recorder!!.setOutputFile(fd.fileDescriptor)
            recorder!!.setOnErrorListener { mr: MediaRecorder?, what: Int, extra: Int ->
                Log.e(Constants.TAG, "OnErrorListener $what,$extra")
                terminateAndEraseFile()
            }
            recorder!!.setOnInfoListener { mr: MediaRecorder?, what: Int, extra: Int ->
                Log.e(Constants.TAG, "OnInfoListener $what,$extra")
                terminateAndEraseFile()
            }
            recorder!!.prepare()

            // Sometimes the recorder takes a while to start up
            Thread.sleep(2000)
            recorder!!.start()
            recording = true
            Log.d(Constants.TAG, "RecordService: Recorder started!")
            val toast = Toast.makeText(this,
                    this.getString(R.string.receiver_start_call),
                    Toast.LENGTH_SHORT)
            toast.show()
            return false
        } catch (e: Exception) {
            Log.d(Constants.TAG, "RecordService: Exception!")
        }
        return true
    }

    companion object {
        var audioManager: AudioManager? = null
    }
}