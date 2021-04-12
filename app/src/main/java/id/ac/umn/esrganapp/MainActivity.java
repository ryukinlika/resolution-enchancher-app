package id.ac.umn.esrganapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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

public class MainActivity extends AppCompatActivity {

    Interpreter interpreter;
    private Boolean available = false;
    Button btn;
    ImageView imgView;
    Bitmap bm, bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.tryButton);
        imgView = findViewById(R.id.imageViewTest);
        imgView.invalidate();
        BitmapDrawable drawable = (BitmapDrawable) imgView.getDrawable();
        bm = drawable.getBitmap();

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

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = Bitmap.createScaledBitmap(bm, 50,50, true);
                ByteBuffer input = ByteBuffer.allocateDirect(50*50*3*4).order(ByteOrder.nativeOrder());
                for (int y = 0; y < 50; y++) {
                    for (int x = 0; x < 50; x++) {
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
                int bufferSize = 120000 * java.lang.Float.SIZE / java.lang.Byte.SIZE;
                ByteBuffer modelOutput = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder());
                Log.d("test", "before interpreter run");
                interpreter.run(input, modelOutput);
                Log.d("test", "after interpreter run");

                modelOutput.rewind();
                Bitmap bitmap_output = Bitmap.createBitmap(200,200, Bitmap.Config.ARGB_8888);
                bitmap_output.copyPixelsFromBuffer(modelOutput);
                imgView.setImageBitmap(bitmap_output);
                Log.d("test", "set bitmap to output");


            }
        });

    }
}