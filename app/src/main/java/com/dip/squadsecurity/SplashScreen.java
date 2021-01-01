package com.dip.squadsecurity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


public class SplashScreen extends AppCompatActivity {

    ProgressBar initial;
    Handler wait;
    Runnable delay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ActionBar myActionBar = getSupportActionBar();
        //For hiding android actionbar

        initial = (ProgressBar) findViewById(R.id.progressBar);
        wait = new Handler();
        delay = new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("MYPREFERENCE", Context.MODE_PRIVATE);
                final String log = sharedPreferences.getString("login", "");
                final String lg=sharedPreferences.getString("profile","");
                if(log.equals("Login") && lg.equals("Done")) {
                    Intent launchNext = new Intent(SplashScreen.this, MainActivity.class);
                    launchNext.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    launchNext.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    launchNext.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(launchNext);
                    finish();
                }
                else if(!lg.equals("Done")&& log.equals("Login")){
                    Intent launchNext = new Intent(SplashScreen.this, UserProfile.class);
                    launchNext.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    launchNext.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    launchNext.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(launchNext);
                    finish();
                }
                else {
                Intent launchNext = new Intent(SplashScreen.this, LoginActivity.class);
                launchNext.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                launchNext.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                launchNext.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(launchNext);
                finish();
                }

            }
        };
        wait.postDelayed(delay, 1000);

    }
}
