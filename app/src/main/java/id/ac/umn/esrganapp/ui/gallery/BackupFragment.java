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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;


import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
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
    private boolean isdeleted = false;
    private List<String> ImageDelete = new ArrayList<String>();
    private Button loginButton, deleteButton;
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
            setHasOptionsMenu(true);
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
                deleteButton = root.findViewById(R.id.deletePhoto);
                deleteButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        for (String StoragePath : ImageDelete){
                            StorageReference Sref = storageReference.child(StoragePath);
                            Sref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // File deleted successfully
                                    String realKeyName = StoragePath.substring(7);

                                    databaseImages.child(realKeyName).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {

                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            for(GalleryThumbnail a : data){
                                                if (a.getPath() == StoragePath) {
                                                    data.remove(a);break;
                                                }
                                            }
                                            adapter.notifyDataSetChanged();
                                            Toast.makeText(getContext(), "Delete all Image Successful!!", Toast.LENGTH_SHORT).show();
                                            Log.d("Data size", String.valueOf(data.size()));
                                            if(data.size() == 0){
//                                                root.setVisibility(View.GONE);
//                                                View subframe = inflater.inflate(R.layout.fragment_frame, container, false);
//                                                subframe.setTag(root.getTag());
//                                                container.addView(subframe);
                                                Fragment backupFragment = new BackupContentFragment();
                                                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_gallery_backup, backupFragment).commit();
                                            }
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!
                                    Toast.makeText(getContext(), "Image failed to be deleted, Please try again", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        //remove all selected image from arraylist & set button visibility
                        ImageDelete.clear();
                        for(GalleryThumbnail a : data)a.setCheckedFalse();
                        adapter.notifyDataSetChanged();
                        deleteButton.setVisibility(View.INVISIBLE);

//                Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.recyclerView);
//                FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
//                fragmentTransaction.detach(currentFragment);
//                fragmentTransaction.attach(currentFragment);
//                fragmentTransaction.commit();
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
        String keyName = data.get(position).getPath();
        //if button menu deleted selected
        if(isdeleted){
            if(ImageDelete.contains(keyName)){
                data.get(position).setCheckedFalse();
                adapter.notifyItemChanged(position);
                ImageDelete.remove(keyName);
            }else{
                data.get(position).setCheckedTrue();
                adapter.notifyItemChanged(position);
                ImageDelete.add(keyName);
            }
        }
        //full size image
        else {
            Intent intent = new Intent(this.getContext(), ViewImageActivity.class);
            intent.putExtra("backup_image_name", keyName);
            startActivity(intent);
        }
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
        //if backup visible set to false
        menu.findItem(R.id.backupData).setVisible(false);
        MenuItem itemDelete = menu.findItem(R.id.deleteImage);
        MenuItem itemLogout = menu.findItem(R.id.Logout);
        //if logged in, then the backup option will appear
        if(FirebaseAuth.getInstance().getCurrentUser() != null ){
            if(((MainActivity)getActivity()).getStorageUriSize()>0)
                itemDelete.setVisible(true);
            itemLogout.setVisible(true);
        }
    }

    //Function to handle if an item is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            //if backup selected
            case R.id.deleteImage:
                deleteButton = getView().findViewById(R.id.deletePhoto);
                if(isdeleted){
                    //remove all selected image from arraylist & set button visibility
                    ImageDelete.clear();
                    for(GalleryThumbnail a : data)a.setCheckedFalse();
                    adapter.notifyDataSetChanged();
                    deleteButton.setVisibility(View.INVISIBLE);
                }
                else{
                    deleteButton.setVisibility(View.VISIBLE);
                }
                isdeleted = !isdeleted;
                break;
            case R.id.Logout:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getContext(), "Logging Out, Returning to Main Page", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), MainActivity.class));
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
