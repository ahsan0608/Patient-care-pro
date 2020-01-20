package com.example.ahsan.eldercarepro;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import gr.net.maroulis.library.EasySplashScreen;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EasySplashScreen config = new EasySplashScreen(SplashScreenActivity.this)
                .withFullScreen()
                .withTargetActivity(MainActivity.class)
                .withSplashTimeOut(5000)
                .withBackgroundColor(Color.WHITE)
                .withFooterText("Developed by Ahsan")
                .withLogo(R.drawable.ecare2)
                .withAfterLogoText("Elder Care");

        //Typeface pacificoFontfooter = Typeface.createFromAsset(getAssets(), "automobile-Bold.otf");
        //Typeface pacificoFont = Typeface.createFromAsset(getAssets(), "FTY STRATEGYCIDE NCV.ttf");
        //config.getAfterLogoTextView().setTypeface(pacificoFont);
        //config.getFooterTextView().setTypeface(pacificoFontfooter);


        config.getFooterTextView().setTextColor(Color.BLACK);
        config.getAfterLogoTextView().setTextColor(Color.BLACK);


        View easySplashScreen = config.create();

        setContentView(easySplashScreen);
    }
}
