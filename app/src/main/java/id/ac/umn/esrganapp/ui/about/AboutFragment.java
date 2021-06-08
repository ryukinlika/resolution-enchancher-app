package id.ac.umn.esrganapp.ui.about;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import id.ac.umn.esrganapp.MainActivity;
import id.ac.umn.esrganapp.R;
import id.ac.umn.esrganapp.ui.gallery.GalleryThumbnail;

public class AboutFragment extends Fragment {
    TextView text_about_1, text_sub_1, text_about_2, text_sub_2, text_hyperlink;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_about, container, false);

        setHasOptionsMenu(true);

        text_about_1 = root.findViewById(R.id.text_about_1);
        text_about_1.setText("Learn more about ESRGAN");
        text_sub_1 = root.findViewById(R.id.text_sub_1);
        text_sub_1.setText("\u25CF ESRGAN stand for Enhanced Super-Resolution Generative Adversarial Networks.\n\n" +
                "\u25CF ESRGAN is one of many technique to enhance an image to a super-resolution, it reconstruct a higher-resolution image from the lower-resolution images.\n");
        text_hyperlink = root.findViewById(R.id.text_link);
        text_hyperlink.setMovementMethod(LinkMovementMethod.getInstance());

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        MenuItem itemLogout = menu.findItem(R.id.Logout);
        //if logged in, then the backup option will appear
        if(FirebaseAuth.getInstance().getCurrentUser() != null ){
            itemLogout.setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //if backup selected
        if (item.getItemId() == R.id.Logout) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getContext(), "Logging Out, Returning to Main Page", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}