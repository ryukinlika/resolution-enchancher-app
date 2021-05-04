package id.ac.umn.esrganapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    Animation topanim, botanim;
    ImageView logo;
    TextView text1,text2;


    Context context = this;
    //Initialize how long splash screen will be loaded
    int duration = 3500; //ms
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Link class to xml activity splash
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean firstTimeRun = getSharedPreferences("firstTimeCheck", MODE_PRIVATE)
                        .getBoolean("firstTimeRun", true);

                if(firstTimeRun){
                    Log.d("wegwe", String.valueOf(firstTimeRun));
                    getSharedPreferences("firstTimeCheck", MODE_PRIVATE).edit()
                            .putBoolean("firstTimeRun",false).apply();
                    Log.d("wegwe", "Kucing"+ firstTimeRun);

                    Intent intent = new Intent(context, OnboardingActivity.class);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);
                }

            }
        }, duration);

        //animations
        topanim = AnimationUtils.loadAnimation(this, R.anim.top_anim);

        logo = findViewById(R.id.splash_app_logo);

        //Set animation to image
        logo.setAnimation(topanim);
    }

}