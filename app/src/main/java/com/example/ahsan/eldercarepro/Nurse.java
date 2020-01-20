package com.example.ahsan.eldercarepro;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Map;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;

public class Nurse extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private DatabaseReference mdatabaseUsers;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListenr;

    TextView textView5,condition,bpmTxt;

    TextView textName,textAge,textGender,lastUpdateforNurse;

    private String mTime;
    private String mBPM;
    private String mCon;
    private String mLastUpdateForNurse;

    private String mName;
    private String mAge;
    private String mGender;


    //private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nurse);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        mdatabaseUsers = FirebaseDatabase.getInstance().getReference().child("PatientsData");
        mAuth = FirebaseAuth.getInstance();
        textView5 = (TextView) findViewById(R.id.textView8);
        condition = (TextView) findViewById(R.id.textCon);
        bpmTxt = (TextView) findViewById(R.id.txtBpm);

        textName = (TextView) findViewById(R.id.pname);
        textAge = (TextView) findViewById(R.id.page);
        textGender = (TextView) findViewById(R.id.pgender);
        lastUpdateforNurse = (TextView) findViewById(R.id.lastUpdateforNurse);





//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);



        setValuesOnScree();



        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
//                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
//                .setDrawerLayout(drawer)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void setValuesOnScree() {


        String patientId = mAuth.getCurrentUser().getUid();

   //     Toast.makeText(Nurse.this,"user: "+patientId,Toast.LENGTH_SHORT).show();

        DatabaseReference patientsData = FirebaseDatabase.getInstance().getReference().child("PatientsData").child(patientId);



        patientsData.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Toast.makeText(Nurse.this,"On onDataChange",Toast.LENGTH_SHORT).show();

//                int a = (int) dataSnapshot.getChildrenCount();
//                Boolean b = dataSnapshot.exists();

//                Toast.makeText(Nurse.this,"Data count: "+a,Toast.LENGTH_SHORT).show();
//                Toast.makeText(Nurse.this,"Data exists: "+b,Toast.LENGTH_SHORT).show();


                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();


 //                   Toast.makeText(Nurse.this,"dataSnapshot exists",Toast.LENGTH_SHORT).show();

                    if (map.get("Time") !=null){

    //                    Toast.makeText(Nurse.this,"Time found",Toast.LENGTH_SHORT).show();

                        mTime = map.get("Time").toString();
                        textView5.setText(mTime);

                    }else {
                        textView5.setText("N/A");
                    }

//                    if (map.get("VALUE") !=null){
//
//                        mAMP = map.get("VALUE").toString();
//
//                        String[] splitDataa = mAMP.split("\\r\\n");
//                        String valueA = splitDataa[0];
//                        String[] bpmAmpValuee = valueA.split("-");
//                        String bpmValue = bpmAmpValuee[0];
//
//
//                        textView7.setText(bpmValue);
//
//                    }else {
//                        textView7.setText("N/A");
//                    }



                    if (map.get("VALUE") !=null){

                        mBPM = map.get("VALUE").toString();

                        String[] splitDataa = mBPM.split("\\r\\n");
                        String valueA = splitDataa[0];
                        String[] bpmAmpValuee = valueA.split("-");
                        String bpmValue = bpmAmpValuee[0];


                        bpmTxt.setText(bpmValue);

                    }else {
                        bpmTxt.setText("N/A");
                    }


                    if (map.get("LastUpdate") !=null){

                        mLastUpdateForNurse = map.get("LastUpdate").toString();
                        lastUpdateforNurse.setText(mLastUpdateForNurse);

                    }else {
                        lastUpdateforNurse.setText("N/A");
                    }



                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });










        DatabaseReference patientsBio = FirebaseDatabase.getInstance().getReference().child("users").child(patientId);

        patientsBio.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                if (map.get("name") != null) {

                    mName = map.get("name").toString();

                    try {
                        textName.setText(mName);

                    } catch (ArrayIndexOutOfBoundsException exception) {
                        textName.setText("N/A");
                        //Toast.makeText(PatientViewActivity.this,"",Toast.LENGTH_SHORT).show();
                    }


                } else {
                    textName.setText("N/A");
                }


                if (map.get("age") != null) {

                    mAge = map.get("age").toString();

                    try {
                        textAge.setText(mAge);

                    } catch (ArrayIndexOutOfBoundsException exception) {
                        textAge.setText("N/A");
                        //Toast.makeText(PatientViewActivity.this,"",Toast.LENGTH_SHORT).show();
                    }


                } else {
                    textAge.setText("N/A");
                }


                if (map.get("gender") != null) {

                    mGender = map.get("gender").toString();

                    try {
                        textGender.setText(mGender);

                    } catch (ArrayIndexOutOfBoundsException exception) {
                        textGender.setText("N/A");
                        //Toast.makeText(PatientViewActivity.this,"",Toast.LENGTH_SHORT).show();
                    }


                } else {
                    textGender.setText("N/A");
                }




                    if (map.get("bio") !=null){

                        mCon = map.get("bio").toString();


                        try {
                            String ampValue = mCon;
                            condition.setText(ampValue);

                        }catch (ArrayIndexOutOfBoundsException exception) {
                            condition.setText("N/A");
                            //Toast.makeText(PatientViewActivity.this,"",Toast.LENGTH_SHORT).show();
                        }


                    }else {
                        condition.setText("N/A");
                    }



            }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });










    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nurse, menu);
        return true;
    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.nav_homeNurse) {
            // Handle the camera action
        } else if (id == R.id.nav_patientDataNurse) {

            Intent intent = new Intent(Nurse.this,PatientDataviewActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_patientLocationNurse) {

            Intent intent = new Intent(Nurse.this,NurseMapActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_aboutPatientNurse) {


            Intent intent = new Intent(Nurse.this,PatientProfileActivity.class);
            startActivity(intent);


        } else if (id == R.id.nav_changeAcc) {

            //Toast.makeText(Nurse.this,"Changed Acc p",Toast.LENGTH_LONG).show();
            FirebaseAuth.getInstance().signOut();
            mAuth.removeAuthStateListener(firebaseAuthListenr);
            Intent intent = new Intent(Nurse.this,MainActivity.class);
            startActivity(intent);

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
