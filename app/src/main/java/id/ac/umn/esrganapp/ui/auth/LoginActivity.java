package id.ac.umn.esrganapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
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

import id.ac.umn.esrganapp.MainActivity;
import id.ac.umn.esrganapp.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView register;
    private FirebaseAuth mAuth;
    private EditText In_email, In_pass;
    private Button signIn;
    private ProgressBar pBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        register= (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);

        signIn = (Button) findViewById(R.id.signIn);
        signIn.setOnClickListener(this);

        In_email = (EditText) findViewById(R.id.email);
        In_pass = (EditText) findViewById(R.id.password);

        pBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;

            case R.id.signIn:
                userLogin();
                break;
        }
    }

    private void userLogin() {
        String email = In_email.getText().toString().trim();
        String pass = In_pass.getText().toString().trim();

        if(email.isEmpty()){
            In_email.setError("Email is required!");
            In_email.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            In_email.setError("Email is not Valid!");
            In_email.requestFocus();
            return;
        }
        if(pass.isEmpty()){
            In_pass.setError("Email is required!");
            In_pass.requestFocus();
            return;
        }

        pBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //redirect to camera enchance
                            Toast.makeText(LoginActivity.this, "Login Successful! Returning to Main Page",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else{
                            Toast.makeText(LoginActivity.this, "Username or Password is incorrect, Please try again",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
