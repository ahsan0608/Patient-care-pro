package com.example.ahsan.eldercarepro;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import dmax.dialog.SpotsDialog;
import android.app.AlertDialog;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


import android.telephony.SmsManager;

public class Patient extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {


    Handler bluetoothIn;
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClint;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private Button mMyData;
    private LatLng pickupLocation;
    private Marker mPatientMarker;

    private String helpingHandContactNo;

    TextView bluetoothCondition;
    TextView bluetooothStatus;

    Button checkBTConnection;
    private int mMaxChars = 50000;//Default//change this to string..........
    private UUID mDeviceUUID;
    private BluetoothDevice mDevice;

    private static final String TAG = "BlueTest5-Controlling";
    private BluetoothSocket mBTSocket;
    private ReadInput mReadThread = null;

    private boolean mIsUserInitiatedDisconnect = false;
    private boolean mIsBluetoothConnected = false;

    private StringBuilder recDataString = new StringBuilder();

    final int handlerState = 0;
    private FirebaseUser current_user;
    private DatabaseReference mHelpingHandDatabase;

    private DatabaseReference mdatabaseUsers;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListenr;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;

    String phoneNo;
    String message;
    String txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);



        //BTinit();


        bluetoothCondition = findViewById(R.id.bluetooothCondition);
        bluetooothStatus = findViewById(R.id.bluetooothStatus);
        checkBTConnection = findViewById(R.id.checkBTConnection);

        mdatabaseUsers = FirebaseDatabase.getInstance().getReference().child("PatientsData");
        mAuth = FirebaseAuth.getInstance();



        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

            mDevice = extras.getParcelable(SearchBluetoothActivity.DEVICE_EXTRA);
            mDeviceUUID = UUID.fromString(extras.getString(SearchBluetoothActivity.DEVICE_UUID));
            mMaxChars = extras.getInt(SearchBluetoothActivity.BUFFER_SIZE);


        bluetoothCondition.setText("Connected to: "+mDevice);




        current_user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(current_user.getUid());
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mHelpingHandDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(current_user.getUid());

                mHelpingHandDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        try {
                            helpingHandContactNo  = dataSnapshot.child("contact_no").getValue().toString();
                        } catch (Exception e){

                        }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        boolean found = false;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            bluetoothCondition.setText("Device doesnt Support Bluetooth");
            Toast.makeText(getApplicationContext(), "Device doesnt Support Bluetooth", Toast.LENGTH_SHORT).show();
        }
        if (!bluetoothAdapter.isEnabled()) {
            bluetooothStatus.setText("Connectivity Status: OFF");
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);

            Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
            //bluetoothCondition.setText("Check for your paired device. ");
            for (BluetoothDevice iterator : bondedDevices) {
                //bluetoothCondition.setText("Check for your paired device.");
                Toast.makeText(getApplicationContext(), "Device has paired to:" + iterator.getAddress(), Toast.LENGTH_LONG).show();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        } else {
            bluetooothStatus.setText("Connectivity Status: ON");
        }


        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        if (bondedDevices.isEmpty()) {
            //bluetoothCondition.setText("Please connect the device first.");
            Toast.makeText(getApplicationContext(), "Please connect the device first.", Toast.LENGTH_SHORT).show();
        } else {
            //bluetoothCondition.setText("Check for your paired device. ");
            for (BluetoothDevice iterator : bondedDevices) {
                Toast.makeText(getApplicationContext(), "This device is paired to:" + iterator.getAddress(), Toast.LENGTH_LONG).show();
            }
        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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


        checkBTConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(Patient.this, SearchBluetoothActivity.class);
                startActivity(newIntent);
            }
        });


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.patient, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_homePatient) {
            // Handle the camera action
        } else if (id == R.id.nav_patientDataPatient) {

            Intent intent = new Intent(Patient.this,PatientDataviewActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_patientProfilePatient) {
            Intent intent = new Intent(Patient.this,PatientProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_patientDataPatient) {

        } else if (id == R.id.nav_updatePatientCondition) {
            updateConditionDialog();

        }else if (id == R.id.nav_editDataPatient){
            showChangeNumberDialog();
        }
        else if (id == R.id.nav_share) {

            FirebaseAuth.getInstance().signOut();
            mAuth.removeAuthStateListener(firebaseAuthListenr);
            Intent intent = new Intent(Patient.this,MainActivity.class);
            startActivity(intent);

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }







    private void updateConditionDialog() {

        android.app.AlertDialog.Builder alertDialog = new AlertDialog.Builder(Patient.this);
        alertDialog.setTitle("Present Condition");
        alertDialog.setMessage("Write about your present health condition in short.");

        LayoutInflater inflater = this.getLayoutInflater();
        View layout_pwd = inflater.inflate(R.layout.change_bio_layout,null);

        final MaterialEditText edtBio = layout_pwd.findViewById(R.id.edtBio);

        alertDialog.setView(layout_pwd);

        alertDialog.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                final android.app.AlertDialog waitingDialog = new SpotsDialog(Patient.this);
                waitingDialog.show();


                if (edtBio.getText().toString().trim().length() == 0){

                    Toast.makeText(Patient.this,"Field can't be empty!",Toast.LENGTH_SHORT).show();

                }else {



                    Map<String,Object> bioUpdate = new HashMap<>();
                    bioUpdate.put("bio",edtBio.getText().toString());

                    String userId = mAuth.getCurrentUser().getUid();
                    DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("users");



                    user.child(userId)
                            .updateChildren(bioUpdate)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    waitingDialog.dismiss();
                                    Toast.makeText(Patient.this,"Condition has been updated.",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Patient.this, e.getMessage() ,Toast.LENGTH_SHORT).show();
                        }
                    });


                }



            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });

        alertDialog.show();


    }







    private void showChangeNumberDialog() {

        android.app.AlertDialog.Builder alertDialog = new AlertDialog.Builder(Patient.this);
        alertDialog.setTitle("Update Contact");
        alertDialog.setMessage("This field can't be empty");

        LayoutInflater inflater = this.getLayoutInflater();
        View layout_pwd = inflater.inflate(R.layout.change_password_layout,null);


        final MaterialEditText edtContactNumber = layout_pwd.findViewById(R.id.edtContactNumber);

        alertDialog.setView(layout_pwd);

        alertDialog.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                final android.app.AlertDialog waitingDialog = new SpotsDialog(Patient.this);
                waitingDialog.show();


                    if (edtContactNumber.getText().toString().trim().length() == 0){

                        Toast.makeText(Patient.this,"Field can't be empty!",Toast.LENGTH_SHORT).show();

                    }else {


                        String num = edtContactNumber.getText().toString().trim();


                        Map<String,Object> contactUpdate = new HashMap<>();
                        contactUpdate.put("contact_no","tel:+88"+num);


                        String userId = mAuth.getCurrentUser().getUid();
                        DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("users");

                        user.child(userId)
                                .updateChildren(contactUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        waitingDialog.dismiss();
                                        Toast.makeText(Patient.this,"Contact number has been updated",Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Patient.this, e.getMessage() ,Toast.LENGTH_SHORT).show();
                            }
                        });


                    }



            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });

        alertDialog.show();


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(500);                  //old 1000
        mLocationRequest.setFastestInterval(500);           //old 1000
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClint, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isEnabled()) {
            bluetooothStatus.setText("Connectivity Status: ON");
        } else {
            bluetooothStatus.setText("Connectivity Status: OFF");
        }


        if (getApplicationContext() != null) {
            mLastLocation = location;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(11));


            if (mPatientMarker != null){
                mPatientMarker.remove();
            }

            mPatientMarker =  mMap.addMarker(new MarkerOptions().position(latLng).title("Your Current Position"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15), 600, null);



            try {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("patientsLocation");

                GeoFire geoFire = new GeoFire(ref);
                geoFire.setLocation(userId, new GeoLocation(location.getLatitude(),location.getLongitude()));
            }catch (Exception e){
                //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }




        } else {

            Toast.makeText(Patient.this, "Not found", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        buildGoogleApiClint();
        mMap.setMyLocationEnabled(true);
    }


    protected synchronized void buildGoogleApiClint() {
        mGoogleApiClint = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClint.connect();
    }


    private class ReadInput implements Runnable {

        final Handler handler = new Handler();

        private boolean bStop = false;
        private Thread t;

        public ReadInput() {
            t = new Thread(this, "Input Thread");
            t.start();
        }

        public boolean isRunning() {
            return t.isAlive();
        }

        @Override
        public void run() {

            InputStream inputStream;
            int bytes;

            byte[] buffer = new byte[1024];
            while(!bStop)
            {
                try
                {
                    inputStream = mBTSocket.getInputStream();
                    int byteCount = inputStream.available();
                    if(byteCount > 0)
                    {

                        byte[] rawBytes = new byte[byteCount];
                        inputStream.read(rawBytes);
                        final String getString=new String(rawBytes,"UTF-8");

                        final String trimString = getString.trim();
                        try {
                            String[] parts = trimString.split("-");
                            String codeValue = parts[1];

                            String val222 = new String("222");
                            String val111 = new String("111");
                            String val999 = new String("999");




                            if (codeValue.equals(val222)){
                                txt = "Patient has fallen.";
                            }
                            if (codeValue.equals(val111)){
                                txt = "Patient had a stroke.";
                            }

                            if (codeValue.equals(val999)){
                                txt = "Emergency help.";
                            }

                            handler.post(new Runnable() {
                                public void run()
                                {
                                    String userId = mAuth.getCurrentUser().getUid();
                                    mdatabaseUsers.child(userId).child("VALUE").setValue(trimString);
                                    String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                                    mdatabaseUsers.child(userId).child("Time").setValue(mydate);
                                    mdatabaseUsers.child(userId).child("LastUpdate").setValue(txt);


                                }
                            });





                            if (codeValue.equals(val222)){
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String num = helpingHandContactNo;
                                        String msg = "Patient has fallen!";

                                        sendSMS(num,msg);
                                    }
                                });
                            }


                            if (codeValue.equals(val111)){
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String num = helpingHandContactNo;
                                        String msg = "Patient had a stroke!";

                                        sendSMS(num,msg);
                                    }
                                });
                            }



                            if (codeValue.equals(val999)){
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String num = helpingHandContactNo;
                                        String msg = "I need emergency help!";

                                        sendSMS(num,msg);
                                    }
                                });
                            }


                        } catch (Exception e){
                            //Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG).show();
                        }





                    }

                }
                catch (IOException ex)
                {
                    bStop = true;
                }
            }

        }
        public void stop() {
            bStop = true;
        }
    }


    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }




    private class DisConnectBT extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {//cant inderstand these dotss

            if (mReadThread != null) {
                mReadThread.stop();
                while (mReadThread.isRunning())
                    ; // Wait until it stops
                mReadThread = null;

            }

            try {
                mBTSocket.close();
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mIsBluetoothConnected = false;
            if (mIsUserInitiatedDisconnect) {
                finish();
            }
        }

    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mBTSocket != null && mIsBluetoothConnected) {
            new DisConnectBT().execute();
        }
        Log.d(TAG, "Paused");
        super.onPause();
    }


    @Override
    protected void onResume() {
        if (mBTSocket == null || !mIsBluetoothConnected) {
            new ConnectBT().execute();
        }
        Log.d(TAG, "Resumed");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Stopped");
        super.onStop();


        try {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("patientsLocation");
            GeoFire geoFire = new GeoFire(ref);
            geoFire.removeLocation(userId );

        }catch (Exception e){

        }


    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean mConnectSuccessful = true;

        @Override
        protected void onPreExecute() {

            //progressDialog = ProgressDialog.show(Controlling.this, "Hold on", "Connecting");// http://stackoverflow.com/a/11130220/1287554

        }

        @Override
        protected Void doInBackground(Void... devices) {

            try {
                if (mBTSocket == null || !mIsBluetoothConnected) {
                    mBTSocket = mDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBTSocket.connect();
                }
            } catch (IOException e) {
// Unable to connect to device`
                // e.printStackTrace();
                mConnectSuccessful = false;



            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!mConnectSuccessful) {
                Toast.makeText(getApplicationContext(), "Could not connect to device.Please turn on your Hardware", Toast.LENGTH_LONG).show();
                finish();
            } else {
                msg("Connected to device");
                mIsBluetoothConnected = true;
                mReadThread = new ReadInput(); // Kick off input reader
            }

            //progressDialog.dismiss();
        }

    }





}
