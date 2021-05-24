package id.ac.umn.esrganapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class RegisterAcitivity extends AppCompatActivity {
    private EditText username, password, confPass;
    private Button register;
    private byte[] hash, salt;
    Context context = this;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register = findViewById(R.id.registerButton);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confPass = findViewById(R.id.confirmPassword);
        try {
            //Generate Salt for pass
            SecureRandom random = new SecureRandom();
            salt = new byte[16];
            random.nextBytes(salt);

            //SHA-521 init
            KeySpec spec = new PBEKeySpec(password.getText().toString().toCharArray(), salt, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            hash = factory.generateSecret(spec).getEncoded();
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.err.println("We're sorry, please try again");
        }


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("salt",salt.toString());
                Log.d("password", password.getText().toString());
                Log.d("asd",hash.toString());
                if(username.getText().toString().equals("uasmobile") &&
                        password.getText().toString().equals("uasmobilegenap")) {
                    Intent register = new Intent (context, MainActivity.class);
                    register.putExtra("nama","check");
                    startActivity(register);
                }
            }
        });
    }
}