package id.ac.umn.esrganapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.modeldownloader.CustomModel;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.Tensor;
import org.tensorflow.lite.gpu.CompatibilityList;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.model.Model;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

import id.ac.umn.esrganapp.ml.Esrgan;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class EnhanceActivity extends AppCompatActivity {

    Interpreter interpreter;
    private Boolean available = false;
    Button btn, btnsave;
    ImageView imgView;
    Bitmap bm, bitmap;

    private static final int PERMISSION_CODE = 103; //write_external_storage

    int MODEL_WIDTH, MODEL_HEIGHT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enhance);
        setTitle("Image Enhancer");
//        MODEL_HEIGHT = 50; //set model default size
//        MODEL_WIDTH = 50; //set model default size
//

        btn = findViewById(R.id.tryButton);
        btnsave = findViewById(R.id.saveButton);
//        btnsave.setEnabled(false);
//        btn.setEnabled(false);
        imgView = findViewById(R.id.imageViewTest);

        //take passing bytearray and decode
        Bundle extras = getIntent().getExtras();
        byte[] byteArray = extras.getByteArray("my_image");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imgView.setImageBitmap(bmp);
//        MODEL_WIDTH = bmp.getWidth();
//        MODEL_HEIGHT = bmp.getHeight();
//        Log.d("model width", String.valueOf(MODEL_WIDTH));
//        Log.d("model height", String.valueOf(MODEL_HEIGHT));

        imgView.invalidate();
//        BitmapDrawable drawable = (BitmapDrawable) imgView.getDrawable();
//        bm = drawable.getBitmap();

//        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = null;
//        if(connManager != null){
//            networkInfo = connManager.getActiveNetworkInfo();
//        }
//        if(networkInfo != null && networkInfo.isConnected()){
//            CustomModelDownloadConditions conditions = new CustomModelDownloadConditions.Builder()
//                    .requireWifi()  // Also possible: .requireCharging() and .requireDeviceIdle()
//                    .build();
//            FirebaseModelDownloader.getInstance()
//                    .getModel("esrgan-tf2",DownloadType.LOCAL_MODEL, conditions)
//                    .addOnSuccessListener(new OnSuccessListener<CustomModel>() {
//                        @Override
//                        public void onSuccess(CustomModel model) {
//                            available = true;
//                            // Download complete. Depending on your app, you could enable the ML
//                            // feature, or switch from the local model to the remote model, etc.
//
//                            // The CustomModel object contains the local path of the model file,
//                            // which you can use to instantiate a TensorFlow Lite interpreter.
//                            File modelFile = model.getFile();
//                            if (modelFile != null) {
//                                interpreter = new Interpreter(modelFile);
//                                interpreter.resizeInput(0, new int[]{1, MODEL_WIDTH,MODEL_HEIGHT, 3});
//
////                                Tensor a = interpreter.getInputTensor(0);
////                                Log.d("tensor shwape: ", String.valueOf(a.shape()));
////                                Log.d("tensor dataty: ", String.valueOf(a.dataType()));
////                                Log.d("tensor dimension: ", String.valueOf(a.numDimensions()));
////                                Log.d("tensor numbyte: ", String.valueOf(a.numBytes()));
//
//                                btn.setEnabled(true);
//                            }
//                            else{
//                                Toast.makeText(EnhanceActivity.this, "Error loading component!",Toast.LENGTH_LONG).show();
//                                EnhanceActivity.this.finish();
//                            }
//                        }
//                    });
//        }
//        else{
//            //create popup, notifies no network and terminate
//            Toast.makeText(EnhanceActivity.this, "No internet connection available!",Toast.LENGTH_LONG).show();
//            this.finish();
//        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn.setEnabled(false);
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
//                new AsyncEnhanceImage().execute(bmp);
                new AsyncEnhanceImageLocal().execute(bmp);
            }
        });

        btnsave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //check runtime permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        //permission not granted, need request
                        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        //show popup for runtime permission
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                else {
                    //system os less than marshmallow
                    btnsave.setEnabled(false);
                    btn.setEnabled(false);
                    findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

                    BitmapDrawable drawable = (BitmapDrawable) imgView.getDrawable();
                    Bitmap output  = drawable.getBitmap();
                    new bitmapToFile(output, EnhanceActivity.this).execute();
                }

            }
        });
    }


    private Bitmap getOutputImage(ByteBuffer output){
        output.rewind();

        int outputWidth = MODEL_WIDTH*4;
        int outputHeight = MODEL_HEIGHT*4;
        Bitmap bitmap = Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888);
        int [] pixels = new int[outputWidth * outputHeight];
        for (int i = 0; i < outputWidth * outputHeight; i++) {
            int a = 0xFF;

            float r = output.getFloat() ;
            float g = output.getFloat() ;
            float b = output.getFloat() ;

            pixels[i] = a << 24 | ((int) r << 16) | ((int) g << 8) | (int) b;
        }
        bitmap.setPixels(pixels, 0, outputWidth, 0, 0, outputWidth, outputHeight);
        return bitmap;
    }

    class AsyncEnhanceImage extends AsyncTask<Bitmap, Void, Bitmap>{
        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {

            bitmap = Bitmap.createScaledBitmap(bitmaps[0], MODEL_WIDTH,MODEL_HEIGHT, true);
            ByteBuffer input = ByteBuffer.allocateDirect(MODEL_WIDTH*MODEL_HEIGHT*3*4).order(ByteOrder.nativeOrder());
            for (int y = 0; y < MODEL_HEIGHT; y++) {
                for (int x = 0; x < MODEL_WIDTH; x++) {
                    int px = bitmap.getPixel(x, y);
                    // Get channel values from the pixel value.
                    float rf = Color.red(px);
                    float gf = Color.green(px);
                    float bf = Color.blue(px);

                    input.putFloat(rf);
                    input.putFloat(gf);
                    input.putFloat(bf);
                }
            }
            int bufferSize = 4*MODEL_WIDTH * 4*MODEL_HEIGHT * 3 * java.lang.Float.SIZE / java.lang.Byte.SIZE;
            ByteBuffer modelOutput = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder());
            Log.d("test", "before interpreter run");
            interpreter.run(input, modelOutput);
            Log.d("test", "after interpreter run");

            modelOutput.rewind();
            Bitmap bitmap_output;
            bitmap_output = getOutputImage(modelOutput);

            return bitmap_output;
        }
        protected void onPostExecute(Bitmap bitmap_output){
            btn.setEnabled(true);
            findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
            imgView.setImageBitmap(bitmap_output);
            Log.d("test", "set bitmap to output");
        }
    }

    class AsyncEnhanceImageLocal extends AsyncTask<Bitmap, Void, Bitmap>{
        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {
            Bitmap bitmap = bitmaps[0];
            Bitmap enhancedImageBitmap = bitmap;
            try {
                Esrgan model = Esrgan.newInstance(EnhanceActivity.this);
                // Creates inputs for reference.
                TensorImage originalImage = TensorImage.fromBitmap(bitmap);
                // Runs model inference and gets result.
                Esrgan.Outputs outputs = model.process(originalImage);
                TensorImage enhancedImage = outputs.getEnhancedImageAsTensorImage();

                enhancedImageBitmap = enhancedImage.getBitmap();
                // Releases model resources if no longer used.
                model.close();


            } catch (IOException io){
                // Error reading the model
            }
            return enhancedImageBitmap;
        }
        protected void onPostExecute(Bitmap bitmap_output){
            btn.setEnabled(true);
            btnsave.setEnabled(true);
            findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
            imgView.setImageBitmap(bitmap_output);
            Log.d("test", "set bitmap to output");
        }
    }

    public class bitmapToFile extends AsyncTask<Void, Integer, String> {

        Context context;
        Bitmap bitmap;

        public bitmapToFile(Bitmap bitmap, Context context) {
            this.bitmap = bitmap;
            this.context= context;
        }

        @Override
        protected String doInBackground(Void... params) {
            SharedPreferences pref = getSharedPreferences("file_save", MODE_PRIVATE);
            int counter = pref.getInt("file_save_counter", 0);
            String name = "IMG_" + counter;
            counter++;

            final String appDirectoryName = "PoggersApp";
            final File imageRoot = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), appDirectoryName);
            if(!imageRoot.exists())imageRoot.mkdirs();

            try {
                OutputStream fos;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentResolver resolver = getContentResolver();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name + ".jpg");
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + appDirectoryName);
                    Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                } else {
//                    String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                    File image = new File(imageRoot, name + ".jpg");
                    fos = new FileOutputStream(image);
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                Objects.requireNonNull(fos).close();

                final SharedPreferences.Editor edit = pref.edit();
                edit.putInt("file_save_counter", counter);
                edit.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // back to main thread after finishing doInBackground
            // update your UI or take action after
            // exp; make progressbar gone
            btnsave.setEnabled(true);
            btn.setEnabled(true);
            findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
            Toast.makeText(EnhanceActivity.this, "Image successfully saved",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(EnhanceActivity.this, MainActivity.class);
            intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    //handle result of runtime permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                    btnsave.setEnabled(false);
                    btn.setEnabled(false);
                    findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

                    BitmapDrawable drawable = (BitmapDrawable) imgView.getDrawable();
                    Bitmap output  = drawable.getBitmap();
                    new bitmapToFile(output, EnhanceActivity.this).execute();
                }
                else {
                    //permission denied
                    Toast.makeText(this, "Permission Denied...!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}



