 package com.example.healthymind.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthymind.R;
import com.example.healthymind.ui.profile.ProfileActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    TextInputLayout regName, regUsername, regEmail, regPhone, regPassword;
    Button regBtn;
    TextView callLogin;
    FirebaseDatabase rootNode;
    DatabaseReference reference;

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

                UserHelper helper = new UserHelper(name, username, email, phoneNo, password);
                reference.child(username).setValue(helper);
                Toast.makeText(SignupActivity.this, "Account Created Successfully", Toast.LENGTH_LONG).show();

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

    private Boolean validateName() {
        String val = regName.getEditText().getText().toString();

        if (val.isEmpty()) {
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
            regPassword.setError("Password is required");
            return false;
        } else {
            regPassword.setError(null);
            regPassword.setErrorEnabled(false);
            return true;
        }
    }
}