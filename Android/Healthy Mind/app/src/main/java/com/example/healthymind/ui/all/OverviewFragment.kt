package com.example.healthymind.ui.all

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.healthymind.R
import com.example.healthymind.analysis.Recognition
import com.example.healthymind.audio.MFCC
import com.example.healthymind.audio.WavFile
import com.example.healthymind.auth.SessionManager
import com.example.healthymind.auth.SignupActivity
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

//    fun test(intent: Intent){
//        val commandType: Int = intent.getIntExtra("commandType", 0)
//
//        when (commandType) {
//            Constants.STATE_CALL_END -> {
//                Log.d(Constants.TAG, "RecordService STATE_CALL_END")
//                analyzePredictions();
//            }
//        }
//    }


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

        view.edit_contact.setOnClickListener(View.OnClickListener {
            val intent = Intent(getActivity(), ProfileActivity::class.java)
            startActivity(intent)
        })

//         analyzePredictions()

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
