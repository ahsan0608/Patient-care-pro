package com.example.ahsan.eldercarepro;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Button mPatient, mNurse;
    Context context;
    LocationManager locationManager ;
    boolean GpsStatus ;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user==null){
                    Intent intent = new Intent(MainActivity.this, SplashScreenActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };



        mPatient = (Button) findViewById(R.id.login);
        mNurse = (Button) findViewById(R.id.registration);

        mPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkConnected()){

                    Intent intent = new Intent(MainActivity.this, PatientLoginActivity.class);
                    startActivity(intent);


                } else {
                    Toast.makeText(MainActivity.this,"No internet connection!",Toast.LENGTH_LONG).show();
                }

            }


        });


        mNurse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkConnected()){

                    Intent intent = new Intent(MainActivity.this, NurseLoginActivity.class);
                    startActivity(intent);


                } else {
                    Toast.makeText(MainActivity.this,"No internet connection!",Toast.LENGTH_LONG).show();
                }
            }
        });


    }



//    public void CheckGpsStatus(){
//
//        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
//
//        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//
//    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }





//    public boolean isInternetAvailable() {
//        try {
//            InetAddress address = InetAddress.getByName("www.google.com");
//            return !address.equals("");
//        } catch (UnknownHostException e) {
//            // Log error
//        }
//        return false;
//    }


}
