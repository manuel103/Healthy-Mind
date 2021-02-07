package com.example.healthymind.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthymind.R;
import com.example.healthymind.ui.MainActivity;
import com.example.healthymind.ui.onboarding.OnboardingActivity;
import com.example.healthymind.ui.onboarding.Page1Activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    Button login_btn;
//    ImageView image;
    TextView callSignUp;
    TextInputLayout username, password;
    FirebaseAuth firebaseAuth;
    CheckBox rememberMe;
    EditText usernameEditText;
    EditText passwordEditText;
    ProgressBar progressbar;
    RelativeLayout progressbar_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login2);

        callSignUp = findViewById(R.id.signup_screen);
//        image = findViewById(R.id.logoImage);
//        logoText = findViewById(R.id.logo_name);
//        sloganText = findViewById(R.id.slogan_name);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login_btn = findViewById(R.id.login_btn);
        rememberMe = findViewById(R.id.remember_me);
        usernameEditText = findViewById(R.id.login_username_editText);
        passwordEditText = findViewById(R.id.login_password_editText);
        progressbar = findViewById(R.id.login_progress_bar);
        progressbar_layout = findViewById(R.id.progressbar_layout);

        progressbar_layout.setVisibility(View.GONE);
        progressbar.setVisibility(View.GONE);

        // Check if username & pwd are saved in shared prefs.
        SessionManager sessionManager = new SessionManager(LoginActivity.this, SessionManager.SESSION_REMEMBERME);
        if (sessionManager.checkRememberMe()) {
            HashMap<String, String> rememberMeDetails = sessionManager.getRememberMeDetailFromSession();
            usernameEditText.setText(rememberMeDetails.get(SessionManager.KEY_SESSIONUSERNAME));
//            passwordEditText.setText(rememberMeDetails.get(SessionManager.KEY_SESSIONPASSWORD));
        }

        callSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
//                Pair[] pairs = new Pair[7];
//
//                pairs[0] = new Pair<View, String>(image, "logo_image");
//                pairs[1] = new Pair<View, String>(logoText, "logo_text");
//                pairs[2] = new Pair<View, String>(sloganText, "logo_sign");
//                pairs[3] = new Pair<View, String>(username, "uname");
//                pairs[4] = new Pair<View, String>(password, "passwd");
//                pairs[5] = new Pair<View, String>(login_btn, "sign");
//                pairs[6] = new Pair<View, String>(callSignUp, "bott_text");

//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
//                    startActivity(intent, options.toBundle());
//                }
                startActivity(intent);

            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

    }

    private Boolean validateUsername() {
        String val = username.getEditText().getText().toString();

        if (val.isEmpty()) {
            username.setError("Username is required");
            return false;
        } else {
            username.setError(null);
            username.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = password.getEditText().getText().toString();

        if (val.isEmpty()) {
            password.setError("Password is required");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    public void loginUser(View view) {
        if (!validateUsername() | !validatePassword()) {
            return;
        } else {
            isUser();
        }
        progressbar_layout.setVisibility(View.VISIBLE);
        progressbar.setVisibility(View.VISIBLE);

    }

    public static String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    private void isUser() {
        final String userEnteredUsername = username.getEditText().getText().toString().trim();
        final String userEnteredPassword = password.getEditText().getText().toString().trim();
        String newPass =  sha256(userEnteredPassword);


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("patients");

        if (rememberMe.isChecked()) {
            // Create remember me session
            SessionManager sessionManager = new SessionManager(LoginActivity.this, SessionManager.SESSION_REMEMBERME);
            sessionManager.createRememberMeSession(userEnteredUsername, newPass);

        }
       // Check if user exists in DB
        Query checkUser = reference.orderByChild("username").equalTo(userEnteredUsername);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    username.setError(null);
                    username.setErrorEnabled(false);

                    String passwordFromDB = dataSnapshot.child(userEnteredUsername).child("password").getValue(String.class);


                    if (passwordFromDB != null && passwordFromDB.equals(newPass)) {

                        username.setError(null);
                        username.setErrorEnabled(false);

                        // Get users from database
                        String nameFromDB = dataSnapshot.child(userEnteredUsername).child("name").getValue(String.class);
                        String usernameFromDB = dataSnapshot.child(userEnteredUsername).child("username").getValue(String.class);
                        String phoneNoFromDB = dataSnapshot.child(userEnteredUsername).child("phoneNo").getValue(String.class);
                        // String passwordFromDB = dataSnapshot.child(userEnteredUsername).child("password").getValue(String.class);
                        String emailFromDB = dataSnapshot.child(userEnteredUsername).child("email").getValue(String.class);

                        // Create session
                        SessionManager sessionManager = new SessionManager(LoginActivity.this, SessionManager.SESSION_USERSESSION);
                        sessionManager.createLoginSession(nameFromDB, usernameFromDB, phoneNoFromDB, passwordFromDB, emailFromDB);


                        Intent intent = new Intent(getApplicationContext(), Page1Activity.class);
                        intent.putExtra("name", nameFromDB);
                        intent.putExtra("username", usernameFromDB);
                        intent.putExtra("email", emailFromDB);
                        intent.putExtra("phoneNo", phoneNoFromDB);
                        intent.putExtra("password", passwordFromDB);

                        startActivity(intent);
                        finish();

                    } else {
                        progressbar_layout.setVisibility(View.GONE);
                        progressbar.setVisibility(View.GONE);
                        password.setError("Wrong Password");
                        password.requestFocus();
                    }
                } else {
                    progressbar_layout.setVisibility(View.GONE);
                    progressbar.setVisibility(View.GONE);
                    username.setError("No such User exists");
                    username.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressbar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}