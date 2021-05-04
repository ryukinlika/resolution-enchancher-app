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

    private Button back, next;

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
        back = findViewById(R.id.btn_back);
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

        //Go to
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlide.setCurrentItem(mCurr-1);
            }
        });

        //Remove back button onload
        back.setEnabled(false);
        back.setVisibility(View.INVISIBLE);
        back.setText("");
    }

    public void addDotsIndicator(int position){
        mDots = new TextView[3];
        mDotLayer.removeAllViews();

        for (int i = 0;i<mDots.length;i++){
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(Color.parseColor("#cccccc"));

            mDotLayer.addView(mDots[i]);
        }

        if(mDots.length>0){
            mDots[position].setTextColor(Color.parseColor("#FFFFFF"));
        }
    }



    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            mCurr = position;
            if(position == 0){
                next.setEnabled(true);
                next.setText("Next");
                back.setEnabled(false);
                back.setVisibility(View.INVISIBLE);
                back.setText("");
            }else if(position == 2){
                next.setEnabled(true);
                next.setText("Finish");
                back.setEnabled(true);
                back.setVisibility(View.VISIBLE);
                back.setText("Back");
            }else{
                next.setEnabled(true);
                next.setText("Next");
                back.setEnabled(true);
                back.setVisibility(View.VISIBLE);
                back.setText("Back");
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
