package com.example.healthymind.ui.onboarding;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthymind.R;
import com.example.healthymind.auth.SessionManager;
import com.example.healthymind.ui.MainActivity;
import com.example.healthymind.util.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Page2Activity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    List<String> name1 = new ArrayList<String>();
    List<String> phno1 = new ArrayList<String>();
    MyAdapter ma;
    Button select;
    RadioButton notify_yes, notify_no;
    DatabaseReference reference;
    FirebaseDatabase database;
    ProfileConstructor profile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page2);

        // Init views
        getAllContacts(this.getContentResolver());
        ListView lv = (ListView) findViewById(R.id.lv);
        notify_yes = findViewById(R.id.radio_yes);
        notify_no = findViewById(R.id.radio_no);
        profile = new ProfileConstructor();


        ma = new MyAdapter();
        lv.setAdapter(ma);
        lv.setOnItemClickListener(this);
        lv.setItemsCanFocus(false);
        lv.setTextFilterEnabled(true);
        // adding
        select = (Button) findViewById(R.id.button1);


        SessionManager sessionManager = new SessionManager(Page2Activity.this, SessionManager.SESSION_REMEMBERME);
        if (sessionManager.checkRememberMe()) {
            HashMap<String, String> rememberMeDetails = sessionManager.getRememberMeDetailFromSession();
            String username = rememberMeDetails.get(SessionManager.KEY_SESSIONUSERNAME);

            reference = database.getInstance().getReference(Constants.FIREBASE_CHILD_DEPRESSION_LEVELS).child(username);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        // ToDO Do some stuff here

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        select.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
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

                //Toast.makeText(Page2Activity.this, checkedcontacts, 1000).show();

                // Log.d("Contacts are ", String.valueOf(checkedcontacts));

                String p_notify_yes = notify_yes.getText().toString();
                String p_notify_no = notify_no.getText().toString();
                String p_contacts = String.valueOf(checkedcontacts);

                profile.setContacts(p_contacts);
                reference.child("profile").child("contacts").setValue(profile);


                if (notify_yes.isChecked()) {
                    profile.setNotify(p_notify_yes);
                    reference.child("profile").child("contacts").setValue(profile);
                } else {
                    profile.setNotify(p_notify_no);
                    reference.child("profile").child("contacts").setValue(profile);
                }


                //open main activity
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
                // also we need to save a boolean value to storage so next time when the user run the app
                // we could know that he is already checked the intro screen activity
                savePrefsData();
                finish();
            }

        });


    }

    private void savePrefsData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpnend", true);
        editor.commit();

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        ma.toggle(arg2);
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

    class MyAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {
        private SparseBooleanArray mCheckStates;
        LayoutInflater mInflater;
        TextView tv1, tv;
        CheckBox cb;

        MyAdapter() {
            mCheckStates = new SparseBooleanArray(name1.size());
            mInflater = (LayoutInflater) Page2Activity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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