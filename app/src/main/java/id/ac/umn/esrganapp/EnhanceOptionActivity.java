package id.ac.umn.esrganapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class EnhanceOptionActivity extends AppCompatActivity {

    Button gallerybtn;
    Button photobtn;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enhance_option);

        gallerybtn = findViewById(R.id.folderbtn);
        photobtn = findViewById(R.id.photobtn);

        gallerybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EnhanceActivity.class);
                startActivity(intent);
            }
        });

        photobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EnhanceActivity.class);
                startActivity(intent);
            }
        });
    }
}
