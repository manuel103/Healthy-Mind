package com.example.healthymind.ui.onboarding;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.healthymind.R;
import com.example.healthymind.auth.LoginActivity;
import com.example.healthymind.auth.SessionManager;
import com.example.healthymind.auth.UserHelper;
import com.example.healthymind.ui.MainActivity;
import com.example.healthymind.util.Constants;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Page1Activity extends AppCompatActivity {

    private ViewPager screenPager;
    Button btnGetStarted;
    Button onboarding_next;
    Animation btnAnim;
    FirebaseDatabase database;
    DatabaseReference reference;
    TextView patientName;
    TextInputLayout age;
    RadioButton male, female, highschool, college, graduate, employed, unemployed, student;
    ProfileConstructor profile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page1);

        checkPerms();

        // Initialize views
        highschool = findViewById(R.id.radio_highschool);
        college = findViewById(R.id.radio_college);
        graduate = findViewById(R.id.radio_graduate);
        employed = findViewById(R.id.radio_employed);
        unemployed = findViewById(R.id.radio_undemployed);
        student = findViewById(R.id.radio_student);
        male = findViewById(R.id.radio_male);
        female = findViewById(R.id.radio_female);
        age = findViewById(R.id.age);
        btnGetStarted = findViewById(R.id.finish_onboarding);
        onboarding_next = findViewById(R.id.onboarding_next);
        patientName = findViewById(R.id.patient_name);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_animation);
        profile = new ProfileConstructor();

        // when this activity is about to be launch we need to check if its opened before or not
        if (restorePrefData()) {
            Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainActivity);
            finish();
        }


        // Get Started button click listener
        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //open main activity
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
                // also we need to save a boolean value to storage so next time when the user run the app
                // we could know that he is already checked the intro screen activity
                // i'm going to use shared preferences to that process
                savePrefsData();
                finish();
            }
        });

        // final List<ScreenItem> mList = new ArrayList<>();

        SessionManager sessionManager = new SessionManager(Page1Activity.this, SessionManager.SESSION_REMEMBERME);
        if (sessionManager.checkRememberMe()) {
            HashMap<String, String> rememberMeDetails = sessionManager.getRememberMeDetailFromSession();
            String username = rememberMeDetails.get(SessionManager.KEY_SESSIONUSERNAME);

            // Set patient name in greeting (Hi, p_name)
            patientName.setText(username);

            reference = database.getInstance().getReference(Constants.FIREBASE_CHILD_DEPRESSION_LEVELS).child(username);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        onboarding_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // highschool, college, graduate, employed, unemployed, student
                String p_male = male.getText().toString();
                String p_female = female.getText().toString();
                String p_highschol = highschool.getText().toString();
                String p_college = college.getText().toString();
                String p_graduate = graduate.getText().toString();
                String p_employed = employed.getText().toString();
                String p_unemployed = unemployed.getText().toString();
                String p_student = student.getText().toString();
                String p_age = age.getEditText().getText().toString();

                profile.setAge(p_age);
                reference.child("profile").child("general").setValue(profile);

                if (male.isChecked()) {
                    profile.setGender(p_male);
                    reference.child("profile").child("general").setValue(profile);
                } else {
                    profile.setGender(p_female);
                    reference.child("profile").child("general").setValue(profile);
                }

                if (highschool.isChecked()) {
                    profile.setEducation(p_highschol);
                    reference.child("profile").child("general").setValue(profile);
                } else if (college.isChecked()) {
                    profile.setEducation(p_college);
                    reference.child("profile").child("general").setValue(profile);
                } else {
                    profile.setEducation(p_graduate);
                    reference.child("profile").child("general").setValue(profile);
                }

                if (employed.isChecked()) {
                    profile.setEmployment(p_employed);
                    reference.child("profile").child("general").setValue(profile);
                } else if (unemployed.isChecked()) {
                    profile.setEmployment(p_unemployed);
                    reference.child("profile").child("general").setValue(profile);
                } else {
                    profile.setEmployment(p_student);
                    reference.child("profile").child("general").setValue(profile);
                }

                Intent finalOnboarding = new Intent(getApplicationContext(), Page2Activity.class);
                startActivity(finalOnboarding);
                finish();
                loaddLastScreen();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    //@Override
    public void checkPerms() {
        String[] perms = new String[]{
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.PROCESS_OUTGOING_CALLS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        List<String> requestingPerms = new ArrayList<>();
        for (String perm : perms) {
            if (checkSelfPermission(perm) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestingPerms.add(perm);
            }
        }
        if (requestingPerms.size() > 0) {
            requestPermissions(requestingPerms.toArray(new String[requestingPerms.size()]), 0);
        }
    }

    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean("isIntroOpnend", false);
        return isIntroActivityOpnendBefore;

    }

    private void savePrefsData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpnend", true);
        editor.commit();

    }

    // show the GETSTARTED Button and hide the next button
    private void loaddLastScreen() {
        onboarding_next.setVisibility(View.INVISIBLE);
        // btnGetStarted.setVisibility(View.VISIBLE);
        // TODO : ADD an animation the getstarted button
        // setup animation
        btnGetStarted.setAnimation(btnAnim);

    }
}