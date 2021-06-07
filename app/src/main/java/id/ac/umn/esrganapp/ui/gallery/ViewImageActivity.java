package id.ac.umn.esrganapp.ui.gallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;

import id.ac.umn.esrganapp.R;

public class ViewImageActivity extends AppCompatActivity {

    Intent intent;
    String path;
    Bitmap image;
    PhotoView imgView;
    private ProgressBar pBar;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReferenceFromUrl("gs://uaspemmob.appspot.com");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        imgView = findViewById(R.id.galleryView);
        pBar = (ProgressBar) findViewById(R.id.progressBar);

        pBar.setVisibility((View.VISIBLE));
        intent = getIntent();
        // Get the extras (if there are any)
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("image_path")) {
                path = extras.getString("image_path");
                imgView.setImageURI(Uri.fromFile(new File(path)));
                pBar.setVisibility((View.INVISIBLE));
            }
            else if (extras.containsKey("backup_image_name")){
                path = extras.getString("backup_image_name");
                storageReference.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Got the download URL for 'users/me/profile.png'
                        Picasso.get().load(uri).into(imgView, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                pBar.setVisibility((View.INVISIBLE));
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors

                    }
                });
            }
        }
        String filename = path.substring(path.lastIndexOf("/")+1);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(filename);
    }
}