package id.ac.umn.esrganapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EnhanceOptionActivity extends AppCompatActivity {

    Button gallerybtn;
    Button photobtn;
    Button nextbtn;
    Context context = this;
    ImageView gambar;

    private static final int IMAGE_CODE = 101;
    private static final int PERMISSION_CODE = 102;
    private static boolean chooseimg = false;
    private static boolean photoimg = false;
    private static boolean img = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        img=false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enhance_option);

        gallerybtn = findViewById(R.id.folderbtn);
        photobtn = findViewById(R.id.photobtn);
        nextbtn = findViewById(R.id.nextbtn);
        gambar = findViewById(R.id.imageView);

        //choose image from gallery if clicked
        gallerybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check runtime permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        //permission not granted, need request
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        //show popup fro runtime permission
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else {
                        //permission granted
                        chooseGalleryImage();
                    }
                }
                else {
                    //system os less than marshmallow
                    chooseGalleryImage();
                }
                chooseimg = true;
            }
        });

        //take picture with camera if clicked
        photobtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if(ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(new String[] { Manifest.permission.CAMERA }, 100);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, PERMISSION_CODE);
                }
                photoimg = true;
                if (photoimg)
                    Log.d("photoimgnya", "true");
            }
        });

        //go to page if clicked
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if picture selected
                if (img) {
                    //get bitmap from imageview "gambar" then convert to bytearray
                    Bitmap bitmap = ((BitmapDrawable)gambar.getDrawable()).getBitmap();
                    Log.d("BITMAP", bitmap.toString());
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    //passing bytearray
                    Intent intent = new Intent(context, EnhanceActivity.class);
                    intent.putExtra("my_image", byteArray);
                    startActivity(intent);
                }
                //if no picture selected
                else if(!img) {
                    Toast.makeText(EnhanceOptionActivity.this, "Please insert a picture first!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void chooseGalleryImage() {
        //intent to choose image
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_CODE);
    }

    //handle result of runtime permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                    chooseGalleryImage();
                }
                else {
                    //permission denied
                    Toast.makeText(this, "Permission Denied...!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //resizing bitmap or image that taken with a camera
    public Bitmap resizeBitmap(Bitmap getBitmap, int maxSize) {
        int width = getBitmap.getWidth();
        int height = getBitmap.getHeight();
        double x;

        if (width >= height && width > maxSize) {
            x = width / height;
            width = maxSize;
            height = (int) (maxSize / x);
        } else if (height >= width && height > maxSize) {
            x = height / width;
            height = maxSize;
            width = (int) (maxSize / x);
        }
        return Bitmap.createScaledBitmap(getBitmap, width, height, false);
    }

    //handle result of picked image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_CODE && chooseimg) {
            //set image to image view
            Bitmap bitmap = null;
            int maxHeight = 500;
            int maxWidth = 500;
            float scale = 1;
            Matrix matrix = new Matrix();


            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                scale = Math.min(((float)maxHeight / bitmap.getWidth()), ((float)maxWidth / bitmap.getHeight()));
                matrix.postScale(scale, scale);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //gambar.setImageURI(data.getData());
            gambar.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true));
            chooseimg = false;
        }
        else if (resultCode == RESULT_OK && requestCode == PERMISSION_CODE && photoimg) {
            //set image to image view
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageBitmap = resizeBitmap(imageBitmap, 450);
            gambar.setImageBitmap(imageBitmap);
            photoimg = false;
        }
        img = true;
    }
}

