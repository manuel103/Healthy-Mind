package com.example.healthymind.ui.all

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.healthymind.R
import com.example.healthymind.analysis.Recognition
import com.example.healthymind.audio.MFCC
import com.example.healthymind.audio.WavFile
import com.example.healthymind.auth.SessionManager
import com.example.healthymind.ui.onboarding.ContactModel
import com.example.healthymind.ui.onboarding.Model
import com.example.healthymind.ui.onboarding.MyContactListAdapter
import com.example.healthymind.ui.onboarding.MyListAdapter
import com.example.healthymind.ui.profile.ProfileActivity
import com.example.healthymind.util.Constants
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jlibrosa.audio.wavFile.WavFileException
import kotlinx.android.synthetic.main.fragment_overview2.view.*
import org.checkerframework.checker.nullness.qual.NonNull
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.IOException
import java.math.RoundingMode
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.text.DecimalFormat
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OverviewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OverviewFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var buttonOk: Button
    lateinit var listView: ListView
    lateinit var trustedContactsListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

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
        val tfliteModel: @NonNull MappedByteBuffer? =
                activity?.let { FileUtil.loadMappedFile(it, getModelPath()) }
        val tflite: Interpreter

        /** Options for configuring the Interpreter.  */
        val tfliteOptions =
                Interpreter.Options()
        tfliteOptions.setNumThreads(2)
        tflite = tfliteModel?.let { Interpreter(it, tfliteOptions) }!!

        //obtain the input and output tensor size required by the model
        //for urban sound classification, input tensor should be of 1x40x1x1 shape
        val imageTensorIndex = 0
        val imageShape = tflite.getInputTensor(imageTensorIndex).shape()
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
            associatedAxisLabels = activity?.let { FileUtil.loadLabels(it, ASSOCIATED_AXIS_LABELS) }
        } catch (e: IOException) {
            Log.e("tfliteSupport", "Error reading label file", e)
            // Create a map to access the result based on label
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

            val floatMap: Map<String, Float> =
                    labels.getMapWithFloatValue()

            //function to retrieve the top K probability values, in this case 'k' value is 1.
            //retrieved values are stored in 'Recognition' object with label details.
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

        val file_array = arrayOf(fileNames)

        for (file in file_array) {
            val file_size = file.size
            while (i < file_size) {
                val audio_files = file_array[0][i]
                val full_audioPath = audioDirPath + "/" + audio_files
                val predicted_result = predictDepression(full_audioPath)
                println("Depression result is: " + predicted_result)

                // demoRef.setValue(predicted_result)

                val sessionManager = SessionManager(getActivity(), SessionManager.SESSION_REMEMBERME)

                if (sessionManager.checkRememberMe()) {
                    val rememberMeDetails = sessionManager.rememberMeDetailFromSession
                    val username = rememberMeDetails[SessionManager.KEY_SESSIONUSERNAME]
                    // Log.d(username, "Is the current logged in user....")

                    val patientRef = FirebaseDatabase
                            .getInstance()
                            .getReference(Constants.FIREBASE_CHILD_DEPRESSION_LEVELS)
                            .child(username)
                            .child("depression_levels")

                    val pushRef = patientRef.child("prediction_" + i)

                    // val pushId = pushRef.key
                    pushRef.setValue(predicted_result)
                    // Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                }

                // result_text.text = "Predicted Result :" + predicted_result
                i++

            }
        }

    }


    //        Check if user exists in DB
    fun fetch() {

        val sessionManager = SessionManager(getActivity(), SessionManager.SESSION_REMEMBERME)

        if (sessionManager.checkRememberMe()) {
            val rememberMeDetails = sessionManager.rememberMeDetailFromSession
            val username = rememberMeDetails[SessionManager.KEY_SESSIONUSERNAME]

            val ref = FirebaseDatabase
                    .getInstance()
                    .getReference(Constants.FIREBASE_CHILD_DEPRESSION_LEVELS)
                    .child(username)
                    .child("depression_levels")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val value = dataSnapshot.getValue().toString()
                    // val depLevelFromDB = dataSnapshot.child("patients").getValue(String::class.java)
                    // do your stuff here with value

                    println("DB value: " + value)

                }

                override fun onCancelled(databaseError: DatabaseError?) {}
            })
        }


//        var reference: DatabaseReference? = FirebaseDatabase.getInstance().getReference("patients")
//        val sessionManager = SessionManager(getActivity(), SessionManager.SESSION_REMEMBERME)
//
//        if (sessionManager.checkRememberMe()) {
//            val rememberMeDetails = sessionManager.rememberMeDetailFromSession
//            val username = rememberMeDetails[SessionManager.KEY_SESSIONUSERNAME]
//            //        Check if user exists in DB
//            val checkUser: Query = reference!!.orderByChild("username").equalTo(username)
//
//            checkUser.addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    if (dataSnapshot.exists()) {
//
//                        // Get users from database
//                        val depLevelFromDB = dataSnapshot.child("patients").getValue(String::class.java)
//
//                        if(depLevelFromDB == "Phylis"){
//                            println("Frooooom daaaaatabase" + depLevelFromDB)
//                        }
//                    }
//                }
//
//                override fun onCancelled(databaseError: DatabaseError?) {
//                    if (databaseError != null) {
//                        Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//            })
//        }
    }




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_overview2, container, false)


        // Trusted contacts list view population & DB storage
        trustedContactsListView = view.trusted_contactslv
        var contacts_list = mutableListOf<ContactModel>()


        val sessionManager = SessionManager(getActivity(), SessionManager.SESSION_REMEMBERME)
        if (sessionManager.checkRememberMe()) {
            val rememberMeDetails = sessionManager.rememberMeDetailFromSession
            val username = rememberMeDetails[SessionManager.KEY_SESSIONUSERNAME]

            view.home_name.setText(username)
            val ref = FirebaseDatabase
                    .getInstance()
                    .getReference(Constants.FIREBASE_CHILD_DEPRESSION_LEVELS)
                    .child(username)
                    .child("profile")
                    .child("contacts")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot?) {

                    if (dataSnapshot!!.exists()) {
                        contacts_list.clear()
                        for (e in dataSnapshot.children) {
                            val contacts = e.getValue().toString()
                            contacts_list.add(ContactModel(contacts, "contacts to notify", R.drawable.green_circle))
                        }

                        // trustedContactsListView.adapter = getActivity()?.let { MyContactListAdapter(it, R.layout.trusted_contactslv, contacts_list) }
                        val adapter = getActivity()?.let { MyContactListAdapter(it, R.layout.trusted_contactslv, contacts_list) }
                        trustedContactsListView.adapter = adapter
                    } else {
                        view.no_contacts.setVisibility(View.VISIBLE)
                        view.no_contacts.setText("You have not selected any contacts to share your progress with.")
                    }
//                    val value = dataSnapshot.getValue().toString()
//                    // val depLevelFromDB = dataSnapshot.child("patients").getValue(String::class.java)
//                    // do your stuff here with value
//
//                    println("The contact details are: " + value)
//                    contacts_list.add(ContactModel(value,   "+254 785 436 844",   R.drawable.green_circle  ))
//
//                    trustedContactsListView.adapter = getActivity()?.let { MyContactListAdapter(it, R.layout.trusted_contactslv, contacts_list) }
//
//                    trustedContactsListView.setOnItemClickListener{parent, view, position, id ->
//
//                        if (position==0){
//                            Toast.makeText(getActivity(), "Contact One",   Toast.LENGTH_SHORT).show()
//                        }
//                        if (position==1){
//                            Toast.makeText(getActivity(), "Contact Two",   Toast.LENGTH_SHORT).show()
//                        }
//                        if (position==2){
//                            Toast.makeText(getActivity(), "Contact Three", Toast.LENGTH_SHORT).show()
//                        }
//                    }
                }

                override fun onCancelled(databaseError: DatabaseError?) {}
            })
        }


        // Crisis resources list view population
        listView = view.listView
        var list = mutableListOf<Model>()

        list.add(Model("Befrienders", "Befrienders Kenya helps people deal with mental problems, including depression.", R.drawable.green_circle))
        list.add(Model("13 RW", "Find a great repository of resources for dealing with crisis.", R.drawable.green_circle))
        list.add(Model("National Institute of Mental Illness", "Find a vast collection of crisis resources", R.drawable.green_circle))

        listView.adapter = getActivity()?.let { MyListAdapter(it, R.layout.resource_listview, list) }

        listView.setOnItemClickListener { parent, view, position, id ->

            if (position == 0) {

                val intent = Intent(getActivity(), Resource1WebView::class.java)
                startActivity(intent)
                // Toast.makeText(getActivity(), "Item One", Toast.LENGTH_SHORT).show()
            }
            if (position == 1) {
                val intent = Intent(getActivity(), Resource2WebView::class.java)
                startActivity(intent)
                // Toast.makeText(getActivity(), "Item Two", Toast.LENGTH_SHORT).show()
            }
            if (position == 2) {
                val intent = Intent(getActivity(), Resource3WebView::class.java)
                startActivity(intent)
                // Toast.makeText(getActivity(), "Item Three", Toast.LENGTH_SHORT).show()
            }
        }



//        view.classify_button.setOnClickListener(View.OnClickListener {
//
//            // analyzePredictions()
//            fetch()
//
//        })
//         analyzePredictions()

//       val edit_contact = view.edit_contact as TextView

        view.edit_contact.setOnClickListener( View.OnClickListener{
            val intent = Intent(getActivity(), ProfileActivity::class.java)
            startActivity(intent)
        })

        return view

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OverviewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                OverviewFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}