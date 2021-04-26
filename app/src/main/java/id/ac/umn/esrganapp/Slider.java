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

    public Slider(Context context){
        this.context = context;
    }

    public int[] slide_images={
      R.drawable.app_logo, R.drawable.app_logo, R.drawable.app_logo
    };

    public String[] slide_headings={
      "Image Enhancement",
            "ESRGAN",
            "Poggers"
    };

    public String[] slide_bodys={
      "Improving the quality and information content of original data, applied to remote sensing data to improve of an image human visual analysis",
            "The Enhanced Super Resolution Generative Adversarial Networks, image Super Resolution (SR) techniques reconstruct a Higher Resolution (HR) image or sequence from the observed Lower Resolution (LR) images",
            "Lorem ipsum"
    };



    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

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
