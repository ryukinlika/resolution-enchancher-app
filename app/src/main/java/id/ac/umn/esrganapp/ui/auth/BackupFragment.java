package id.ac.umn.esrganapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import id.ac.umn.esrganapp.R;

public class BackupFragment extends Fragment {
    private Button loginButton;
    private FirebaseAuth mAuth;
    private BackupFragment context = this;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        View root;
        if(currentUser == null){
            root = inflater.inflate(R.layout.fragment_backup, container, false);
            loginButton = root.findViewById(R.id.login);

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            });
        }
        else {
            root = inflater.inflate(R.layout.activity_backup, container, false);
            Toast.makeText(getActivity(), "Yey berhasil ganti fragment",Toast.LENGTH_LONG).show();
        }
        return root;


    }
}
