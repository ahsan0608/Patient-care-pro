package com.example.ahsan.eldercarepro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class PatientProfileActivity extends AppCompatActivity {

    private DatabaseReference mdatabaseUsers;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListenr;

    TextView nameT,genderT,ageT, mailT;


    private String mname;
    private String mgender;
    private String mage;
    private String mmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);

        mdatabaseUsers = FirebaseDatabase.getInstance().getReference().child("PatientsData");
        mAuth = FirebaseAuth.getInstance();
        nameT = (TextView) findViewById(R.id.txtVname);
        genderT = (TextView) findViewById(R.id.txtVgender);
        ageT = (TextView) findViewById(R.id.txtVage);
        mailT = (TextView) findViewById(R.id.txtVmail);

        setValuesOnScree();
    }



    private void setValuesOnScree() {

        String patientId = mAuth.getCurrentUser().getUid();

        //Toast.makeText(PatientProfileActivity.this,"user: "+patientId,Toast.LENGTH_SHORT).show();

        //DatabaseReference patientsData = FirebaseDatabase.getInstance().getReference().child("PatientsData").child(patientId);
        DatabaseReference patientsData2 = FirebaseDatabase.getInstance().getReference().child("users").child(patientId);



        patientsData2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if (map.get("name") !=null){

                        mname = map.get("name").toString();


                        try {
                            String ampValue = mname;
                            nameT.setText(ampValue);

                        }catch (ArrayIndexOutOfBoundsException exception) {
                            nameT.setText("N/A");
                            //Toast.makeText(PatientViewActivity.this,"",Toast.LENGTH_SHORT).show();
                        }


                    }else {
                        nameT.setText("N/A");
                    }






                    if (map.get("age") !=null){

                        mage = map.get("age").toString();


                        try {
                            String ampValue = mage;
                            ageT.setText(ampValue);

                        }catch (ArrayIndexOutOfBoundsException exception) {
                            ageT.setText("N/A");
                            //Toast.makeText(PatientViewActivity.this,"",Toast.LENGTH_SHORT).show();
                        }


                    }else {
                        ageT.setText("N/A");
                    }





                    if (map.get("gender") !=null){

                        mgender = map.get("gender").toString();


                        try {
                            genderT.setText(mgender);

                        }catch (ArrayIndexOutOfBoundsException exception) {
                            genderT.setText("N/A");
                            //Toast.makeText(PatientViewActivity.this,"",Toast.LENGTH_SHORT).show();
                        }


                    }else {
                        genderT.setText("N/A");
                    }



                    if (map.get("email") !=null){

                        mmail = map.get("email").toString();


                        try {
                            mailT.setText(mmail);

                        }catch (ArrayIndexOutOfBoundsException exception) {
                            mailT.setText("N/A");
                            //Toast.makeText(PatientViewActivity.this,"",Toast.LENGTH_SHORT).show();
                        }


                    }else {
                        mailT.setText("N/A");
                    }




                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

}
