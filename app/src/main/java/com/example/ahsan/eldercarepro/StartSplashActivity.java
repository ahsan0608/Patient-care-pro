package com.example.ahsan.eldercarepro;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class StartSplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_splash);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(StartSplashActivity.this,MainActivity.class);
                StartSplashActivity.this.startActivity(mainIntent);
                StartSplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
