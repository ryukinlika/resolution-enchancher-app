package id.ac.umn.esrganapp.ui.gallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import id.ac.umn.esrganapp.R;

public class GalleryFragment extends Fragment implements GalleryRecyclerViewAdapter.ItemClickListener{

    private RecyclerView recyclerView;
    private String database_url = "https://uaspemmob-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private GalleryRecyclerViewAdapter adapter;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    //Used to store image uris that will be used to upload image to firebase
    private List<Uri> ImageUris = new ArrayList<>();
    private List<Uri> ImageDelete = new ArrayList<>();
    // Store all Uris from firebase storage if user already backup some images
    private List<String> StorageUris = new ArrayList<>();
    private List<GalleryThumbnail> data;
    private boolean isbackup = false;
    private boolean isdeleted = false;
    private Button backupButton;
    private Button deleteButton;
    private FirebaseStorage  storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReferenceFromUrl("gs://uaspemmob.appspot.com");
    private final DatabaseReference databaseImages = FirebaseDatabase.getInstance(database_url).getReference().child("Images");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //if user already logged in
        if(currentUser!=null){
            //Function to get all image uri that the user have saved. Used to compare later if image already backed up or not
            databaseImages.orderByChild("email").equalTo(currentUser.getEmail()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Iterates through all value gotten from query
                    Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                    //Function to store all img_uri (used later to compare the selected image for backup
                    //to know if image has already exist in backup or not
                    while (dataSnapshots.hasNext()) {
                        DataSnapshot dataSnapshotChild = dataSnapshots.next();
                        StorageUris.add(dataSnapshotChild.child("img_uri").getValue().toString());
                    }
                    Log.d("sizeAll", String.valueOf(StorageUris.size()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        data = getImagesFromExternalDir();
        recyclerView = root.findViewById(R.id.recyclerView);
        int numberOfColumns = 3;
        recyclerView.setLayoutManager(new GridLayoutManager(root.getContext(), numberOfColumns));
        adapter = new GalleryRecyclerViewAdapter(root.getContext(), data);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        backupButton = root.findViewById(R.id.backupPhoto);
        backupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_email = currentUser.getEmail();
                for (int i = 0; i < ImageUris.size(); i++) {
                    uploadPicture(ImageUris.get(i),user_email);
                }
            }
        });

        deleteButton = root.findViewById(R.id.deletePhoto);
        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                for (Uri s : ImageDelete){
                    File fdelete = new File(s.getPath());
                    if (fdelete.exists()) {
                        if (fdelete.delete()) {
                            for(GalleryThumbnail a : data){
                                if (a.getPath() == fdelete.getPath()) {
                                    data.remove(a);break;
                                }
                            }
                            adapter.notifyDataSetChanged();
                            System.out.println("file Deleted :" + s.getPath());
                        } else {
                            System.out.println("file not Deleted :" + s.getPath());
                        }
                    }
                    Log.d("My array list content: ", s.toString());
                    }
//                Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.recyclerView);
//                FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
//                fragmentTransaction.detach(currentFragment);
//                fragmentTransaction.attach(currentFragment);
//                fragmentTransaction.commit();
            }
        });

        return root;
    }

    private List<GalleryThumbnail> getImagesFromExternalDir() {
        List<GalleryThumbnail> items = new ArrayList<GalleryThumbnail>();

        final String appDirectoryName = "PoggersApp";
        File[] imageRoot = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), appDirectoryName).listFiles(new ImageFileFilter());
        if(imageRoot.length != 0){
            for (File file : imageRoot) {
                Bitmap image = BitmapHelper.decodeBitmapFromFile(file.getAbsolutePath(),
                        100,
                        100);
                items.add(new GalleryThumbnail(file.getAbsolutePath(), image));
            }
        }

        return items;

    }

    @Override
    public void onItemClick(View view, int position) {
        String path = data.get(position).getPath();
        Uri uri = Uri.fromFile(new File(path));
        // if button menu backup selected

        if(isbackup){
            //Check if imageUri already exist in firebase (through ArrayList StorageUris
            if(StorageUris.contains(uri.toString())){
                Toast.makeText(getContext(),"That Image have already been backed up!", Toast.LENGTH_SHORT).show();
            }
            //add uri to imageUris and remove if uri already Exist
            else if(ImageUris.contains(uri)){
                data.get(position).setCheckedFalse();
                adapter.notifyItemChanged(position);
                ImageUris.remove(uri);
            }else{
                data.get(position).setCheckedTrue();
                adapter.notifyItemChanged(position);
                ImageUris.add(uri);
            }
        }
        else if(isdeleted){
            if(ImageDelete.contains(uri)){
                data.get(position).setCheckedFalse();
                adapter.notifyItemChanged(position);
                ImageDelete.remove(uri);
            }else{
                data.get(position).setCheckedTrue();
                adapter.notifyItemChanged(position);
                ImageDelete.add(uri);
            }
        }
        //full size image
        else {
            Intent intent = new Intent(this.getContext(), ViewImageActivity.class);
            intent.putExtra("image_path", path);
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
        MenuItem item = menu.findItem(R.id.backupData);
        //if logged in, then the backup option will appear
        if(FirebaseAuth.getInstance().getCurrentUser() != null ){
            item.setVisible(true);
        }
        //show delete image option
        MenuItem itemDelete = menu.findItem(R.id.deleteImage);
        itemDelete.setVisible(true);
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
                    ImageUris.clear();
                    for(GalleryThumbnail a : data)a.setCheckedFalse();
                    adapter.notifyDataSetChanged();
                    backupButton.setVisibility(View.INVISIBLE);
                }
                else{
                    //set button invisible if backup canceled
                    if(isdeleted){
                        isdeleted = false;
                        ImageDelete.clear();
                        for(GalleryThumbnail a : data)a.setCheckedFalse();
                        adapter.notifyDataSetChanged();
                        deleteButton.setVisibility(View.INVISIBLE);
                    }
                    backupButton.setVisibility(View.VISIBLE);
                }
                //flip isbackup everytime menu item is pressed
                isbackup = !isbackup;
                break;
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
                    //set button visible
                    if(isbackup){
                        isbackup = false;
                        ImageUris.clear();
                        for(GalleryThumbnail a : data)a.setCheckedFalse();
                        adapter.notifyDataSetChanged();
                        backupButton.setVisibility(View.INVISIBLE);
                    }
                    deleteButton.setVisibility(View.VISIBLE);
                }
                isdeleted = !isdeleted;
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