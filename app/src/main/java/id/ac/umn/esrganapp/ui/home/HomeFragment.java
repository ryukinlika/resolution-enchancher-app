package id.ac.umn.esrganapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;

import id.ac.umn.esrganapp.MainActivity;
import id.ac.umn.esrganapp.R;

public class HomeFragment extends Fragment {

    TextView text_home, sub_text_home;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        setHasOptionsMenu(true);

        text_home = root.findViewById(R.id.text_home);
        text_home.setText("Image Enhancement by Poggers");

        sub_text_home = root.findViewById(R.id.sub_text_home);
        sub_text_home.setText("Features");
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