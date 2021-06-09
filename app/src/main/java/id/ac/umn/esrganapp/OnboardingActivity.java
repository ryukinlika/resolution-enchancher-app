package id.ac.umn.esrganapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class OnboardingActivity extends AppCompatActivity{

    private ViewPager mSlide;
    private LinearLayout mDotLayer;

    private Slider slider;

    private TextView[] mDots;

    private Button next;

    private int mCurr;

    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //hide action bar
        getSupportActionBar().hide();
        setContentView(R.layout.activity_onboarding);

        //get Variables
        mSlide = (ViewPager) findViewById(R.id.slideView);
        mDotLayer = (LinearLayout) findViewById(R.id.dots);
        next = findViewById(R.id.btn_next);

        //Initialize variable for mDot (dots to indicate which page)
        addDotsIndicator(0);

        //Connects the slider activity to this activity
        slider = new Slider(this);
        mSlide.setAdapter(slider);

        mSlide.addOnPageChangeListener(viewListener);

        //Next button function. If mcurr==2 (last page), go to MainActivity. Else go to next page
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurr==2){
                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);
                }else
                    mSlide.setCurrentItem(mCurr+1);
            }
        });
    }

    //create dots for slider
    public void addDotsIndicator(int position){
        //initialize 3 dots
        mDots = new TextView[3];
        //remove dot ; so that the dots do not accumulate when user click next or back button
        mDotLayer.removeAllViews();

        for (int i = 0;i<mDots.length;i++){
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(Color.parseColor("#cccccc"));

            mDotLayer.addView(mDots[i]);
        }

        if(mDots.length>0){
            mDots[position].setTextColor(Color.parseColor("#beffad"));
        }
    }



    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        //to see where the position at
        //logic for back, next, and finish button
        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            mCurr = position;
            if(position == 2){
                next.setText("Finish");
            }else{
                next.setText("Next");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    //press back twice to exit
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please press BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
