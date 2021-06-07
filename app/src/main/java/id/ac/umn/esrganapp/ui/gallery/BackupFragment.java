package id.ac.umn.esrganapp.ui.gallery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import id.ac.umn.esrganapp.MainActivity;
import id.ac.umn.esrganapp.R;
import id.ac.umn.esrganapp.ui.auth.LoginActivity;

public class BackupFragment extends Fragment implements GalleryRecyclerViewAdapter.ItemClickListener{

    private String database_url = "https://uaspemmob-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private RecyclerView recyclerView;
    private TextView onboarding_heading, onboarding_body;
    private GalleryRecyclerViewAdapter adapter;
    private List<GalleryThumbnail> data = new ArrayList<>();
    private boolean isbackup = false;
    private List<String> ImagePaths = new ArrayList<>();
    private Button loginButton, backupButton;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    // Store all Uris from firebase storage if user already backup some images
    private List<String> StorageUris = new ArrayList<>();
    private BackupFragment context = this;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReferenceFromUrl("gs://uaspemmob.appspot.com");
    private final DatabaseReference databaseImages = FirebaseDatabase.getInstance(database_url).getReference().child("Images");
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root;
        if(currentUser == null){
            root = inflater.inflate(R.layout.fragment_backup, container, false);
            loginButton = root.findViewById(R.id.login);

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            });
        }
        else {
            List<String> mainStorageUri = ((MainActivity)getActivity()).getStorageUri();
            Log.d("Main storage uri size", mainStorageUri.get(1));
            if (mainStorageUri.size() == 0) {
                root = inflater.inflate(R.layout.fragment_frame, container, false);
                Fragment backupFragment = new BackupContentFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_backup, backupFragment).commit();
            } else {
                root = inflater.inflate(R.layout.fragment_gallery_backup, container, false);
                getImagesFromExternalDir(mainStorageUri);
                recyclerView = root.findViewById(R.id.recyclerView_backup);
                int numberOfColumns = 3;
                recyclerView.setLayoutManager(new GridLayoutManager(root.getContext(), numberOfColumns));
                adapter = new GalleryRecyclerViewAdapter(root.getContext(), data);
                adapter.setClickListener(this);
                recyclerView.setAdapter(adapter);
                backupButton = root.findViewById(R.id.backupPhoto);

                backupButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }
                });
                adapter.notifyDataSetChanged();
                //Log.d("data size ", String.valueOf(data.size()));
            }
        }
        return root;
    }


    private void getImagesFromExternalDir(List<String> StorageUris) {
//        List<GalleryThumbnail> items = new ArrayList<GalleryThumbnail>();
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Getting all images from Backup Storage");
        progressDialog.show();

        for (int i = 0; i < StorageUris.size(); i++) {
            Log.d("namae", String.valueOf(i));
            try{
                final File localFile = File.createTempFile("Images", "bmp");
                String directoryStorage = "images/"+StorageUris.get(i);
                storageReference.child(directoryStorage).getFile(localFile).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull FileDownloadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0
                                * taskSnapshot.getBytesTransferred()
                                / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage(
                                "Getting images from Storage "
                                        + (int) progress + "%");
                    }
                }).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap image = BackupFragment.BitmapHelper.decodeBitmapFromFile(localFile.getAbsolutePath(),
                                100,
                                100);
                        data.add(new GalleryThumbnail(directoryStorage, image));
                        adapter.notifyDataSetChanged();
                        if(data.size() == StorageUris.size())
                        progressDialog.dismiss();
                    }
                });
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onItemClick(View view, int position) {
        String path = data.get(position).getPath();
        Uri uri = Uri.fromFile(new File(path));
        // if button menu backup selected

//        if(isbackup){
//            //Check if imageUri already exist in firebase (through ArrayList StorageUris
//            if(StorageUris.contains(uri.toString())){
//                Toast.makeText(getContext(),"That Image have already been backed up!", Toast.LENGTH_SHORT).show();
//            }
//            //add uri to imageUris and remove if uri already Exist
//            else if(ImageUris.contains(uri)){
//                ImageUris.remove(uri);
//            }else{
//                ImageUris.add(uri);
//            }
//        }
//        //full size image
//        else {
            Intent intent = new Intent(this.getContext(), ViewImageActivity.class);
            intent.putExtra("backup_image_name", path);
            startActivity(intent);
//        }
    }


    private class ImageFileFilter implements FileFilter {
        @Override
        public boolean accept(File file) {
            if (file.getAbsolutePath().endsWith(".jpg") || file.getAbsolutePath().endsWith(".png")) {
                return true;
            }
            return false;
        }
    }

    //get menu item
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        MenuItem item = menu.findItem(R.id.backupData);
        //if logged in, then the backup option will appear
        if(FirebaseAuth.getInstance().getCurrentUser() != null ){
            item.setVisible(true);
        }
    }

    //Function to handle if an item is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            //if backup selected
            case R.id.backupData:
                backupButton = getView().findViewById(R.id.backupPhoto);
                if(isbackup){
                    //remove all selected image from arraylist & set button visibility
//                    ImageUris.clear();
                    backupButton.setVisibility(View.INVISIBLE);
                }
                else{
                    //set button invisible if backup canceled
                    backupButton.setVisibility(View.VISIBLE);
                }
                //flip isbackup everytime menu item is pressed
                isbackup = !isbackup;
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private static class BitmapHelper {
        public static Bitmap decodeBitmapFromFile(String imagePath, int maxWidth, int maxHeight) {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagePath, options);
            // Calculate inSampleSize
            options.inSampleSize = calculateSampleSize(options, maxWidth, maxHeight);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(imagePath, options);
        }
        private static int calculateSampleSize(BitmapFactory.Options options,
                                               int maxHeight,
                                               int maxWidth) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > maxHeight || width > maxWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                // Calculate the largest inSampleSize value that is a power of 2 and
                // keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) > maxHeight
                        && (halfWidth / inSampleSize) > maxWidth) {
                    inSampleSize *= 2;
                }
            }
            return inSampleSize;
        }
    }

    private void uploadPicture(Uri imageUri, String user_email){
        DatabaseReference pushedItem = databaseImages.push();
        String key = pushedItem.getKey();
        StorageReference Sref = storageReference.child("images/"+key);
        String url_image = String.valueOf(Sref.getDownloadUrl());
        Images image = new Images(user_email,url_image, imageUri.toString());
        pushedItem.setValue(image);

        Sref.putFile(imageUri).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                progressDialog.dismiss();
                Toast.makeText(getContext(), "Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
//        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
//                double progress = (100.0
//                        * taskSnapshot.getBytesTransferred()
//                        / taskSnapshot.getTotalByteCount());
//                progressDialog.setMessage(
//                        "Uploaded "
//                                + (int) progress + "%");
//            }
        });
    }
}
