package id.ac.umn.esrganapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import id.ac.umn.esrganapp.R;

public class HomeFragment extends Fragment {

    TextView text_home, sub_text_home;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        text_home = root.findViewById(R.id.text_home);
        text_home.setText("Image Enhancement by Poggers");

        sub_text_home = root.findViewById(R.id.sub_text_home);
        sub_text_home.setText("\u25CF Generate up-scaled image using ESRGAN for the best possible result\n" +
                "\u25CF Works for all popular photo formats\n" +
                "\u25CF Fix blur in photos\n" +
                "\u25CF Potentially reduce noises\n" +
                "\u25CF Up-scaled up to 4x original size");


        return root;
    }
}