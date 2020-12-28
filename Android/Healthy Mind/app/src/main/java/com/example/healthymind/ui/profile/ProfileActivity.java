package com.example.healthymind.ui.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.util.Log;
import com.example.healthymind.R;
import com.example.healthymind.auth.SessionManager;
import com.example.healthymind.helper.FileHelper;
import com.example.healthymind.service.ConvertToWav;
import com.example.healthymind.ui.MainActivity;
import com.example.healthymind.ui.all.OverviewFragment;
import com.example.healthymind.ui.onboarding.Page2Activity;
import com.example.healthymind.ui.onboarding.ProfileConstructor;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    List<String> name1 = new ArrayList<String>();
    List<String> phno1 = new ArrayList<String>();
    MyAdapter ma;
    Button select, converter;

    LinearLayout personalinfo, experience;
    TextView personalinfobtn, experiencebtn, profile_name, occupation;
    Button update, contacts_update;
    TextInputLayout update_email, update_phone_number, update_password;
    ProfileConstructor profile;

    String _USERNAME;
    Context context;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SessionManager sessionManager = new SessionManager(ProfileActivity.this, SessionManager.SESSION_REMEMBERME);
        if (sessionManager.checkRememberMe()) {
            HashMap<String, String> rememberMeDetails = sessionManager.getRememberMeDetailFromSession();
            _USERNAME = rememberMeDetails.get(SessionManager.KEY_SESSIONUSERNAME);
        }

        // Fetch contacts
        profile = new ProfileConstructor();
        getAllContacts(this.getContentResolver());
        ListView lv = (ListView) findViewById(R.id.lv_trustedContacts);
        ma = new ProfileActivity.MyAdapter();
        lv.setAdapter(ma);
        lv.setOnItemClickListener(this);
        lv.setItemsCanFocus(false);
        lv.setTextFilterEnabled(true);
        // adding

        OverviewFragment analyze = new OverviewFragment();
        select = (Button) findViewById(R.id.contacts_update);
//        converter = (Button) findViewById(R.id.convert);

        reference = FirebaseDatabase.getInstance().getReference("patients").child(_USERNAME);

        profile_name = findViewById(R.id.profile_name);
        occupation = findViewById(R.id.occupation);

        update = findViewById(R.id.data_update);
        update_phone_number = findViewById(R.id.update_phone);
        update_email = findViewById(R.id.update_email);
        update_password = findViewById(R.id.update_password);
        // contacts_update = findViewById(R.id.contacts_update);

        personalinfo = findViewById(R.id.personalinfo);
//        update_contacts = findViewById(R.id.update_contacts);
        experience = findViewById(R.id.experience);
//        review = findViewById(R.id.review);
        personalinfobtn = findViewById(R.id.personalinfobtn);
        experiencebtn = findViewById(R.id.experiencebtn);
//        reviewbtn = findViewById(R.id.reviewbtn);

        /*making personal info visible*/
        personalinfo.setVisibility(View.VISIBLE);
        experience.setVisibility(View.GONE);

//        review.setVisibility(View.GONE);

//Show All data
        showAllUserData();

        personalinfobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                personalinfo.setVisibility(View.VISIBLE);
                experience.setVisibility(View.GONE);
//                review.setVisibility(View.GONE);
                personalinfobtn.setTextColor(getResources().getColor(R.color.designMainColor));
                experiencebtn.setTextColor(getResources().getColor(R.color.miniDescription));
//                reviewbtn.setTextColor(getResources().getColor(R.color.miniDescription));

            }
        });

        experiencebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                personalinfo.setVisibility(View.GONE);
                experience.setVisibility(View.VISIBLE);
//                review.setVisibility(View.GONE);
                personalinfobtn.setTextColor(getResources().getColor(R.color.miniDescription));
                experiencebtn.setTextColor(getResources().getColor(R.color.designMainColor));
//                reviewbtn.setTextColor(getResources().getColor(R.color.miniDescription));

            }
        });

//        reviewbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                personalinfo.setVisibility(View.GONE);
//                experience.setVisibility(View.GONE);
//                review.setVisibility(View.VISIBLE);
//                personalinfobtn.setTextColor(getResources().getColor(R.color.miniDescription));
//                experiencebtn.setTextColor(getResources().getColor(R.color.miniDescription));
//                reviewbtn.setTextColor(getResources().getColor(R.color.designMainColor));
//
//            }
//        });

        // Set test data
        profile_name.setText(_USERNAME);

        // Save new contacts button
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "Contact Details Updated", Toast.LENGTH_LONG).show();

                StringBuilder checkedcontacts = new StringBuilder();
                // ArrayList<String> checkedcontacts = new ArrayList<String>();

                for (int i = 0; i < name1.size(); i++) {
                    if (ma.mCheckStates.get(i) == true) {
                        checkedcontacts.append(name1.get(i).toString() + ": ");
                        checkedcontacts.append(phno1.get(i).toString() + ", ");
                        // checkedcontacts.add("\n");

                    } else {

                    }
                }

                String p_contacts = String.valueOf(checkedcontacts);

                profile.setContacts(p_contacts);
                reference.child("profile").child("contacts").setValue(profile);
            }

        });


//        converter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                ConvertToWav ctw = new ConvertToWav();
////                File waveFile = getFile2("wav");
////                File mRecording = getFile2("3gpp");
//
//                FileHelper fh = new FileHelper();
//
//
////                Log.d("3GPP files are: " + mRecording);
//
////                try {
////                    Log.d(ctw.rawToWave(mRecording, waveFile));
////
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
//            }
//
//        });

//        analyze.analyzePredictions();

//        Convert(context);
    }

    public void getAllContacts(ContentResolver cr) {
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            name1.add(name);
            phno1.add(phoneNumber);
        }

        phones.close();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        ma.toggle(arg2);
    }

    // Display patient data on their profile
    private void showAllUserData() {

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String phone_number = dataSnapshot.child("phoneNo").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String password = dataSnapshot.child("password").getValue(String.class);
                    String occupation_details = dataSnapshot.child("profile").child("general").child("employment").getValue(String.class);
                    // String trusted_contacts = dataSnapshot.child("profile").child("contacts").child("contacts").getValue(String.class);

                    update_phone_number.getEditText().setText(phone_number);
                    // update_contacts.getEditText().setText(trusted_contacts);
                    update_email.getEditText().setText(email);
                    update_password.getEditText().setText(password);
                    occupation.setText(occupation_details);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }


    public void update(View view) {
        updatePhone();
        updateEmail();
        updatePassword();
    }

//    public void updateContacts(View view) {
//        updateTrustedContacts();
//    }

//    public void updateTrustedContacts() {
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    String trusted_contacts = dataSnapshot.child("profile").child("contacts").child("contacts").getValue(String.class);
//
//                    if (trusted_contacts != update_contacts.getEditText().getText().toString()) {
//                        reference.child("profile").child("contacts").child("contacts").setValue(update_contacts.getEditText().getText().toString());
//
//                        Toast.makeText(ProfileActivity.this, "Contact Details Updated", Toast.LENGTH_LONG).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Toast.makeText(ProfileActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//
//        });
//    }

    public void updatePhone() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String phone_number = dataSnapshot.child("phoneNo").getValue(String.class);

                    if (phone_number != update_phone_number.getEditText().getText().toString()) {
                        reference.child("phoneNo").setValue(update_phone_number.getEditText().getText().toString());

                        Toast.makeText(ProfileActivity.this, "Details Updated", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void updateEmail() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String email = dataSnapshot.child("email").getValue(String.class);

                    if (email != update_phone_number.getEditText().getText().toString()) {
                        reference.child("email").setValue(update_email.getEditText().getText().toString());
                        Toast.makeText(ProfileActivity.this, "Details Updated", Toast.LENGTH_LONG).show();

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Toast.makeText(ProfileActivity.this, "Details Updated", Toast.LENGTH_LONG).show();

            }

        });
    }

    public void updatePassword() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String password = dataSnapshot.child("password").getValue(String.class);

                    if (password != update_phone_number.getEditText().getText().toString()) {
                        reference.child("password").setValue(update_password.getEditText().getText().toString());
                        Toast.makeText(ProfileActivity.this, "Details Updated", Toast.LENGTH_LONG).show();

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void goBack(View view) {
        finish();
    }



    class MyAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {
        private SparseBooleanArray mCheckStates;
        LayoutInflater mInflater;
        TextView tv1, tv;
        CheckBox cb;

        MyAdapter() {
            mCheckStates = new SparseBooleanArray(name1.size());
            mInflater = (LayoutInflater) ProfileActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return name1.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub

            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View vi = convertView;
            if (convertView == null)
                vi = mInflater.inflate(R.layout.row, null);
            tv = (TextView) vi.findViewById(R.id.textView1);
            tv1 = (TextView) vi.findViewById(R.id.textView2);
            cb = (CheckBox) vi.findViewById(R.id.checkBox1);
            tv.setText("Name :" + name1.get(position));
            tv1.setText("Phone No :" + phno1.get(position));
            cb.setTag(position);
            cb.setChecked(mCheckStates.get(position, false));
            cb.setOnCheckedChangeListener(this);

            return vi;
        }

        public boolean isChecked(int position) {
            return mCheckStates.get(position, false);
        }

        public void setChecked(int position, boolean isChecked) {
            mCheckStates.put(position, isChecked);
        }

        public void toggle(int position) {
            setChecked(position, !isChecked(position));
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub

            mCheckStates.put((Integer) buttonView.getTag(), isChecked);
        }
    }

}
