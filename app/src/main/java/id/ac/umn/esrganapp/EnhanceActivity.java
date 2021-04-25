package id.ac.umn.esrganapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.modeldownloader.CustomModel;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class EnhanceActivity extends AppCompatActivity {

    Interpreter interpreter;
    private Boolean available = false;
    Button btn;
    ImageView imgView;
    Bitmap bm, bitmap;

    int MODEL_WIDTH, MODEL_HEIGHT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enhance);
        setTitle("Image Enhancer");
        MODEL_HEIGHT = 50; //set model size
        MODEL_WIDTH = 50; //set model size

        btn = findViewById(R.id.tryButton);
        imgView = findViewById(R.id.imageViewTest);
        imgView.invalidate();
        BitmapDrawable drawable = (BitmapDrawable) imgView.getDrawable();
        bm = drawable.getBitmap();

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if(connManager != null){
            networkInfo = connManager.getActiveNetworkInfo();
        }
        if(networkInfo != null && networkInfo.isConnected()){
            CustomModelDownloadConditions conditions = new CustomModelDownloadConditions.Builder()
                    .requireWifi()  // Also possible: .requireCharging() and .requireDeviceIdle()
                    .build();
            FirebaseModelDownloader.getInstance()
                    .getModel("esrgan-lite",DownloadType.LOCAL_MODEL, conditions)
                    .addOnSuccessListener(new OnSuccessListener<CustomModel>() {
                        @Override
                        public void onSuccess(CustomModel model) {
                            available = true;
                            // Download complete. Depending on your app, you could enable the ML
                            // feature, or switch from the local model to the remote model, etc.

                            // The CustomModel object contains the local path of the model file,
                            // which you can use to instantiate a TensorFlow Lite interpreter.
                            File modelFile = model.getFile();
                            if (modelFile != null) {
                                interpreter = new Interpreter(modelFile);
                            }
                        }
                    });
        }
        else{
            //create popup, notifies no network and terminate
            this.finish();
        }



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn.setEnabled(false);
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                new AsyncEnhanceImage().execute(bm);
            }
        });

    }






    private Bitmap getOutputImage(ByteBuffer output){
        output.rewind();

        int outputWidth = 200;
        int outputHeight = 200;
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
            ByteBuffer input = ByteBuffer.allocateDirect(50*50*3*4).order(ByteOrder.nativeOrder());
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
}

