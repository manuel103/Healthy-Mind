 package com.example.healthymind.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthymind.R;
import com.example.healthymind.ui.profile.ProfileActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;

 public class SignupActivity extends AppCompatActivity {

    TextInputLayout regName, regUsername, regEmail, regPhone, regPassword;
    Button regBtn;
    TextView callLogin;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    ProgressBar register_progressbar;
    RelativeLayout progressbar_layout_register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        // XML hooks
        regName = findViewById(R.id.name);
        callLogin = findViewById(R.id.backToLogin);
        regUsername = findViewById(R.id.username);
        regEmail = findViewById(R.id.email);
        regPhone = findViewById(R.id.phoneNo);
        regPassword = findViewById(R.id.password);
        regBtn = findViewById(R.id.sign_up);
        register_progressbar = findViewById(R.id.register_progress_bar);
        progressbar_layout_register = findViewById(R.id.progressbar_layout_register);
        progressbar_layout_register.setVisibility(View.GONE);
        register_progressbar.setVisibility(View.GONE);



        // save data on button click
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootNode = FirebaseDatabase.getInstance();
                reference = rootNode.getReference("patients");

                if (!validateName() | !validateUsername() | !validateEmail() | !validatePhone() | !validatePassword()) {
                    return;
                }

                String name = regName.getEditText().getText().toString();
                String username = regUsername.getEditText().getText().toString();
                String email = regEmail.getEditText().getText().toString();
                String phoneNo = regPhone.getEditText().getText().toString();
                String password = regPassword.getEditText().getText().toString();

                String newPass =  sha256(password);


                UserHelper helper = new UserHelper(name, username, email, phoneNo, newPass);
                reference.child(username).setValue(helper);

                progressbar_layout_register.setVisibility(View.VISIBLE);
                register_progressbar.setVisibility(View.VISIBLE);
                Toast.makeText(SignupActivity.this, "Account Created Successfully. Proceed to Login", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }

        });

        callLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
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

    private Boolean validateName() {
        String val = regName.getEditText().getText().toString();

        if (val.isEmpty()) {

            progressbar_layout_register.setVisibility(View.GONE);
            register_progressbar.setVisibility(View.GONE);
            regName.setError("Name is required");
            return false;
        } else {
            regName.setError(null);
            regName.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateUsername() {
        String val = regUsername.getEditText().getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";

        if (val.isEmpty()) {

            progressbar_layout_register.setVisibility(View.GONE);
            register_progressbar.setVisibility(View.GONE);
            regUsername.setError("Username is required");
            return false;
        } else {
            regUsername.setError(null);
            regUsername.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateEmail() {
        String val = regEmail.getEditText().getText().toString();
        String emailPattern = "[a-zA-z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {

            progressbar_layout_register.setVisibility(View.GONE);
            register_progressbar.setVisibility(View.GONE);
            regEmail.setError("Email is required");
            return false;
        } else if (!val.matches(emailPattern)) {
            regEmail.setError(("Invalid email address"));
            return false;
        } else {
            regEmail.setError(null);
            regEmail.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePhone() {
        String val = regPhone.getEditText().getText().toString();

        if (val.isEmpty()) {

            progressbar_layout_register.setVisibility(View.GONE);
            register_progressbar.setVisibility(View.GONE);
            regPhone.setError("Phone number is required");
            return false;
        } else {
            regPhone.setError(null);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = regPassword.getEditText().getText().toString();

        if (val.isEmpty()) {

            progressbar_layout_register.setVisibility(View.GONE);
            register_progressbar.setVisibility(View.GONE);
            regPassword.setError("Password is required");
            return false;
        } else {
            regPassword.setError(null);
            regPassword.setErrorEnabled(false);
            return true;
        }
    }
}