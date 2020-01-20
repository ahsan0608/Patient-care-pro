package com.example.ahsan.eldercarepro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class PatientDataviewActivity extends AppCompatActivity {


    private DatabaseReference mdatabaseUsers;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListenr;

    TextView textView5,textView6,textView7, lastUpdate;


    private String mTime;
    private String mLastUpdate;
    private String mBPM;
    private String mAMP;
    private String mBIO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dataview);

        mdatabaseUsers = FirebaseDatabase.getInstance().getReference().child("PatientsData");
        mAuth = FirebaseAuth.getInstance();
        textView5 = (TextView) findViewById(R.id.textView5);
        textView6 = (TextView) findViewById(R.id.textView6);
        textView7 = (TextView) findViewById(R.id.textView7);
        lastUpdate = (TextView) findViewById(R.id.lastUpdate);

        setValuesOnScree();
    }

    private void setValuesOnScree() {





        String patientId = mAuth.getCurrentUser().getUid();


        DatabaseReference patientsData = FirebaseDatabase.getInstance().getReference().child("PatientsData").child(patientId);
        DatabaseReference patientsData2 = FirebaseDatabase.getInstance().getReference().child("users").child(patientId);

        

        patientsData.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();


                    if (map.get("Time") !=null){

                        mTime = map.get("Time").toString();
                        textView5.setText(mTime);

                    }else {
                        textView5.setText("N/A");
                    }

                    if (map.get("VALUE") !=null){

                        mBPM = map.get("VALUE").toString();

                        String[] splitDataa = mBPM.split("\\r\\n");
                        String valueA = splitDataa[0];
                        String[] bpmAmpValuee = valueA.split("-");
                        String bpmValue = bpmAmpValuee[0];


                        textView7.setText(bpmValue);

                    }else {
                        textView7.setText("N/A");
                    }


                    if (map.get("LastUpdate") !=null){

                        mLastUpdate = map.get("LastUpdate").toString();
                        lastUpdate.setText(mLastUpdate);

                    }else {
                        lastUpdate.setText("N/A");
                    }


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        patientsData2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if (map.get("bio") !=null){

                        mBIO = map.get("bio").toString();


                        try {
                            String ampValue = mBIO;
                            textView6.setText(ampValue);

                        }catch (ArrayIndexOutOfBoundsException exception) {
                            textView6.setText("N/A");
                            //Toast.makeText(PatientViewActivity.this,"",Toast.LENGTH_SHORT).show();
                        }


                    }else {
                        textView6.setText("N/A");
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }


}
