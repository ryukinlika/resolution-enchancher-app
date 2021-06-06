package id.ac.umn.esrganapp.ui.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import id.ac.umn.esrganapp.R;

public class AboutFragment extends Fragment {
    TextView text_about_1, text_sub_1, text_about_2, text_sub_2;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_about, container, false);

        text_about_1 = root.findViewById(R.id.text_about_1);
        text_about_1.setText("Learn more about ESRGAN");
        text_sub_1 = root.findViewById(R.id.text_sub_1);
        text_sub_1.setText("\u25CF ESRGAN stand for Enhanced Super-Resolution Generative Adversarial Networks.\n\n" +
                "\u25CF ESRGAN is one of many technique to enhance an image to a super-resolution, it reconstruct a higher-resolution image from the lower-resolution images.\n\n" +
                "\u25CF Read more about it at https://medium.com/analytics-vidhya/esrgan-enhanced-super-resolution-gan-96a28821634");

        text_about_2 = root.findViewById(R.id.text_about_2);
        text_about_2.setText("About Poggers");
        text_sub_2 = root.findViewById(R.id.text_sub_2);

        text_sub_2.setText("Poggers is a group of college students from Universitas Multimedia Nusantara. \n" +
                "We have five members that have their own jobdesc. \n" +
                "Currently, the members of Poggers is: \n\n" +
                "\u25CF Aaron Beniah Nugroho \n" +
                "\u25CF Gilbert Haensel \n" +
                "\u25CF Hugo Taniady \n" +
                "\u25CF Jovanko Kenshian \n" +
                "\u25CF Ryukin Aranta Lika \n\n" +
                "Our goal is to make a mobile application to implement the ESRGAN model for enhancing the quality of an image.\n" +
                "We hope this application can be helpful for all the users.\n");
        return root;
    }
}