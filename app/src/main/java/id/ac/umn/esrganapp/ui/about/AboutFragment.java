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
        text_sub_1.setText("ESRGAN is ......... \n" +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. \n" +
                "Nam convallis quam urna, eget tincidunt urna volutpat non. \n" +
                "Donec auctor sem arcu, et pellentesque ligula hendrerit vitae. \n" +
                "read more about it at https://medium.com/analytics-vidhya/esrgan-enhanced-super-resolution-gan-96a28821634");

        text_about_2 = root.findViewById(R.id.text_about_2);
        text_about_2.setText("About Poggers");
        text_sub_2 = root.findViewById(R.id.text_sub_2);

        text_sub_2.setText("from UMN I guess \n" +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. \n" +
                "Nam convallis quam urna, eget tincidunt urna volutpat non. \n" +
                "Donec auctor sem arcu, et pellentesque ligula hendrerit vitae. \n" +
                "Quisque efficitur urna ut massa ultrices condimentum. \n" +
                "Quisque efficitur urna ut massa ultrices condimentum. \n" +
                "Quisque efficitur urna ut massa ultrices condimentum. \n" +
                "Quisque efficitur urna ut massa ultrices condimentum. \n" +
                "Quisque efficitur urna ut massa ultrices condimentum. \n" +
                "Quisque efficitur urna ut massa ultrices condimentum. \n" +
                "Quisque efficitur urna ut massa ultrices condimentum. \n" +
                "Quisque efficitur urna ut massa ultrices condimentum. \n" +
                "Quisque efficitur urna ut massa ultrices condimentum. \n" +
                "Quisque efficitur urna ut massa ultrices condimentum. \n" +
                "Quisque efficitur urna ut massa ultrices condimentum. \n" +
                "Quisque efficitur urna ut massa ultrices condimentum. \n" +
                "Quisque efficitur urna ut massa ultrices condimentum. \n" +
                "Quisque efficitur urna ut massa ultrices condimentum. \n" +
                "Quisque efficitur urna ut massa ultrices condimentum. \n" +
                "Quisque efficitur urna ut massa ultrices condimentum. \n" +
                "Quisque efficitur urna ut massa ultrices condimentum. \n" +
                "Quisque efficitur urna ut massa ultrices condimentum. \n" +
                "Quisque efficitur urna ut massa ultrices condimentum. \n" +
                "Quisque efficitur urna ut massa ultrices condimentum. \n" +
                "Quisque efficitur urna ut massa ultrices condimentum. \n" +
                "Quisque efficitur urna ut massa ultrices condimentum. \n" +
                "Quisque efficitur urna ut massa ultrices condimentum. \n" +
                "Quisque efficitur urna ut massa ultrices condimentum. \n" +
                "Quisque efficitur urna ut massa ultrices condimentum. \n" +
                "Vestibulum sagittis non est eu feugiat. \n" +
                "Donec sodales dignissim diam quis sodales. \n" +
                "Cras bibendum in sem nec viverra. ");
        return root;
    }
}