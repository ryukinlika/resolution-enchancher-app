package id.ac.umn.esrganapp.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import id.ac.umn.esrganapp.R;

public class BackupContentFragment extends Fragment {

    View root;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_backup, container, false);
        Button backupButton = root.findViewById(R.id.login);
        TextView onboarding_heading = root.findViewById(R.id.onboarding_heading);
        TextView onboarding_body = root.findViewById(R.id.onboarding_body);

        onboarding_heading.setText("No Photo have been backed up!!");
        onboarding_body.setText("Start backing up your photo!");
        backupButton.setText("Backup Now");

        ImageView img= root.findViewById(R.id.onboarding_logo_app);
        img.setImageResource(R.drawable.nobackup);

        backupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //((ViewGroup)container).removeView(root);

                Fragment fragment = new GalleryFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_backup, fragment);
//                    fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                //((ViewGroup)container).removeView(root);
            }
        });
        return root;
    }

}
