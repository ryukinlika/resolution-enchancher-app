package id.ac.umn.esrganapp.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import id.ac.umn.esrganapp.MainActivity;
import id.ac.umn.esrganapp.R;

public class LoginFragment extends Fragment {
    private Button loginButton;
    private LoginFragment context = this;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_login, container, false);
        loginButton = root.findViewById(R.id.login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }
}
