package id.ac.umn.esrganapp.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import id.ac.umn.esrganapp.R;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private String database_url = "https://uaspemmob-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private FirebaseAuth mAuth;
    private final FirebaseDatabase db = FirebaseDatabase.getInstance(database_url);
    private final DatabaseReference UserDb = db.getReference().child("Users");
    private TextView banner, registerUser;
    private EditText Name, Email, Pass, C_Pass;
    private ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        registerUser = (Button) findViewById(R.id.register);
        registerUser.setOnClickListener(this);

        Name = (EditText) findViewById(R.id.name);
        Email = (EditText) findViewById(R.id.email);
        Pass = (EditText) findViewById(R.id.password);
        C_Pass = (EditText) findViewById(R.id.confirmpassword);

        pBar = (ProgressBar) findViewById(R.id.progressBar);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.register){
            registerUser();
        }
    }

    private void registerUser(){
        String email = Email.getText().toString().trim();
        String name = Name.getText().toString().trim();
        String pass = Pass.getText().toString().trim();
        String CPass = C_Pass.getText().toString().trim();

        if(isOnline()!=true){
            Toast.makeText(RegisterActivity.this, "No Internet Connection, Please try again",Toast.LENGTH_LONG).show();
        }

        if(name.isEmpty()){
            Name.setError("Name is Required!");
            Name.requestFocus();
            return;
        }
        if(email.isEmpty()){
            Email.setError("Email is Required!");
            Email.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Email.setError("Email is not valid!");
            Email.requestFocus();
            return;
        }
        if(pass.isEmpty()){
            Pass.setError("Password is Required!");
            Pass.requestFocus();
            return;
        }
        if(pass.length() <6){
            Pass.setError("Min length is 6 for Password!");
            Pass.requestFocus();
            return;
        }
        if(CPass.isEmpty()){
            C_Pass.setError("Password Confirmation is Required!");
            C_Pass.requestFocus();
            return;
        }
        if(!pass.equals(CPass)) {
            C_Pass.setError("Password Confirmation has to be same as Password!");
            C_Pass.requestFocus();
            return;
        }

        pBar.setVisibility((View.VISIBLE));
        Log.d("a","now no here");
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(name, email);
                            String s = mAuth.getCurrentUser().getUid();
                            Log.d("debug user id",s);

                            UserDb.child(s)
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, "User has been Registered Successfully!!",Toast.LENGTH_LONG).show();
                                        pBar.setVisibility(View.VISIBLE);
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    }
                                    else {
                                        Toast.makeText(RegisterActivity.this, "Failed to Register, Please Try Again",Toast.LENGTH_LONG).show();
                                        pBar.setVisibility(View.GONE);


                                    }
                                }
                            });

                        } else {
                            Toast.makeText(RegisterActivity.this, "Email already Exist! Please try again",Toast.LENGTH_LONG).show();
                            pBar.setVisibility(View.GONE);
                        }
                    }
                });

    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}