package id.ac.umn.esrganapp;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import org.w3c.dom.Text;

public class Slider extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    //initialize Slider
    public Slider(Context context){
        this.context = context;
    }

    //set image for slider
    public int[] slide_images={
      R.drawable.camera, R.drawable.insect_comparison, R.drawable.app_logo
    };

    //set heading for slider
    public String[] slide_headings={
      "Low Cost, High Quality",
            "Improvise your photo",
            "Save your photo"
    };

    //set body for slider
    public String[] slide_bodys={
            "No need to buy high quality camera now!",
            "ESRGAN improves your photo quality by four times!",
            "Save and Backup your enchanced photo easily!"
    };



    @Override
    public int getCount() {
        return slide_headings.length;
    }

    //return object from this
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    //initialize and set image, heading, and body to slide_onboarding layout
    @Override
    public Object instantiateItem(ViewGroup container, int position){
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_onboarding, container, false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.onboarding_logo_app);
        TextView slideHeadingView = (TextView) view.findViewById(R.id.onboarding_heading);
        TextView slideBodyView = (TextView) view.findViewById(R.id.onboarding_body);

        slideImageView.setImageResource(slide_images[position]);
        slideHeadingView.setText(slide_headings[position]);
        slideBodyView.setText(slide_bodys[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        container.removeView((RelativeLayout) object);
    }



}
