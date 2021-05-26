package id.ac.umn.esrganapp.ui.gallery;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

import id.ac.umn.esrganapp.R;

public class ViewImageActivity extends AppCompatActivity {

    Intent intent;
    String path;
    Bitmap image;
    PhotoView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        imgView = findViewById(R.id.galleryView);


        intent = getIntent();
        path = intent.getStringExtra("image_path");
        imgView.setImageURI(Uri.fromFile(new File(path)));

        String filename = path.substring(path.lastIndexOf("/")+1);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(filename);
    }
}