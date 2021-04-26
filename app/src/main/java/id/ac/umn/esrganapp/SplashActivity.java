package id.ac.umn.esrganapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    Animation topanim, botanim;
    ImageView logo;
    TextView text1,text2;

    Context context = this;
    int duration = 3500; //ms
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //<< this
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, OnboardingActivity.class);
                startActivity(intent);
            }
        }, duration);

        //animations
        topanim = AnimationUtils.loadAnimation(this, R.anim.top_anim);
        botanim = AnimationUtils.loadAnimation(this, R.anim.bot_anim);

        logo = findViewById(R.id.splash_app_logo);
        text1 = findViewById(R.id.splash_app_text);
        text2 = findViewById(R.id.splash_app_text_desc);

        logo.setAnimation(topanim);
        text1.setAnimation(botanim);
        text2.setAnimation(botanim);

    }
}