package com.example.ahsan.eldercarepro;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class PatientLoginActivity extends AppCompatActivity {

    private ProgressBar mprogressBar;

    private EditText mEmail, mPassword;
    private Button mLogin, mReg;
    private DatabaseReference mdatabaseUsers;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListenr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_login);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthListenr = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user!=null) {
                    Intent intent = new Intent(PatientLoginActivity.this, SearchBluetoothActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };


        mprogressBar = (ProgressBar) findViewById(R.id.progressBarr);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mLogin = (Button) findViewById(R.id.login);
        mReg = (Button) findViewById(R.id.registration);


        mReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(PatientLoginActivity.this,PatientRegistrationActivity.class);
                startActivity(intent);
//                final String email = mEmail.getText().toString();
//                final String password = mPassword.getText().toString();
//                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(PatientLoginActivity.this,new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (!task.isSuccessful()){
//                            FirebaseAuthException e = (FirebaseAuthException )task.getException();
//                            Toast.makeText(PatientLoginActivity.this,"sign up error "+ e.getMessage(),Toast.LENGTH_SHORT).show();
//                        } else {
//                            String userId = mAuth.getCurrentUser().getUid();
//                            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
//                            current_user_db.setValue(true);
//                            Intent intent = new Intent(PatientLoginActivity.this, MapsActivity.class);
//                            startActivity(intent);
//                            finish();
//                            return;
//                        }
//
//                    }
//                });
            }
        });


        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mprogressBar.setVisibility(View.VISIBLE);

                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(PatientLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()){
                            mprogressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(PatientLoginActivity.this,"sign in error",Toast.LENGTH_SHORT).show();
                        }else {
                            mprogressBar.setVisibility(View.INVISIBLE);
                            Intent intent = new Intent(PatientLoginActivity.this, SearchBluetoothActivity.class);
                            startActivity(intent);
                            finish();
                            return;
                        }

                    }
                });

            }
        });

    }



    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListenr);
    }


    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListenr);
    }
}
