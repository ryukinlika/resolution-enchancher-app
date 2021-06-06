package id.ac.umn.esrganapp.ui.gallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import id.ac.umn.esrganapp.R;
import id.ac.umn.esrganapp.ui.auth.LoginActivity;

public class BackupFragment extends Fragment{

    private RecyclerView recyclerView;
    private TextView onboarding_heading, onboarding_body;
    private GalleryRecyclerViewAdapter adapter;
    private List<GalleryThumbnail> data;
    private List<String> ImagePaths = new ArrayList<>();
    private Button loginButton, backupButton;
    private FirebaseAuth mAuth;
    private BackupFragment context = this;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
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
            root = inflater.inflate(R.layout.fragment_backup, container, false);
            backupButton = root.findViewById(R.id.login);
            onboarding_heading = root.findViewById(R.id.onboarding_heading);
            onboarding_body = root.findViewById(R.id.onboarding_body);

            onboarding_heading.setText("No Photo have been backed up!!");
            onboarding_body.setText("Start backing up your photo!");
            backupButton.setText("Backup Now");

            backupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new GalleryFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                }
            });

//            data = getImagesFromExternalDir();
//
//
//            recyclerView = root.findViewById(R.id.recyclerView);
//            int numberOfColumns = 4;
//            recyclerView.setLayoutManager(new GridLayoutManager(root.getContext(), numberOfColumns));
//            adapter = new GalleryRecyclerViewAdapter(root.getContext(), data);
//            adapter.setClickListener(this);
//            recyclerView.setAdapter(adapter);
//            backupButton = root.findViewById(R.id.backup);
//
//            backupButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(getActivity(), LoginActivity.class);
//                    startActivity(intent);
//                }
//            });
        }
        return root;
    }

//
//    private List<GalleryThumbnail> getImagesFromExternalDir() {
//        List<GalleryThumbnail> items = new ArrayList<GalleryThumbnail>();
//
//        final String appDirectoryName = "PoggersApp";
//        File[] imageRoot = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), appDirectoryName).listFiles(new BackupFragment.ImageFileFilter());
//        if(imageRoot.length != 0){
//            for (File file : imageRoot) {
//                Bitmap image = BackupFragment.BitmapHelper.decodeBitmapFromFile(file.getAbsolutePath(),
//                        100,
//                        100);
//                items.add(new GalleryThumbnail(file.getAbsolutePath(), image));
//            }
//        }
//
//        return items;
//
//    }
//
//    @Override
//    public void onItemClick(View view, int position) {
//        String path = data.get(position).getPath();
//        if(ImagePaths.contains(path)){
//            ImagePaths.remove(path);
//        }else{
//            ImagePaths.add(path);
//        }
//
//    }
//
//    private class ImageFileFilter implements FileFilter {
//        @Override
//        public boolean accept(File file) {
//            if (file.getAbsolutePath().endsWith(".jpg") || file.getAbsolutePath().endsWith(".png")) {
//                return true;
//            }
//            return false;
//        }
//    }
//
//    private static class BitmapHelper {
//        public static Bitmap decodeBitmapFromFile(String imagePath, int maxWidth, int maxHeight) {
//            // First decode with inJustDecodeBounds=true to check dimensions
//            final BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            BitmapFactory.decodeFile(imagePath, options);
//            // Calculate inSampleSize
//            options.inSampleSize = calculateSampleSize(options, maxWidth, maxHeight);
//            // Decode bitmap with inSampleSize set
//            options.inJustDecodeBounds = false;
//            return BitmapFactory.decodeFile(imagePath, options);
//        }
//        private static int calculateSampleSize(BitmapFactory.Options options,
//                                               int maxHeight,
//                                               int maxWidth) {
//            // Raw height and width of image
//            final int height = options.outHeight;
//            final int width = options.outWidth;
//            int inSampleSize = 1;
//
//            if (height > maxHeight || width > maxWidth) {
//
//                final int halfHeight = height / 2;
//                final int halfWidth = width / 2;
//
//                // Calculate the largest inSampleSize value that is a power of 2 and
//                // keeps both
//                // height and width larger than the requested height and width.
//                while ((halfHeight / inSampleSize) > maxHeight
//                        && (halfWidth / inSampleSize) > maxWidth) {
//                    inSampleSize *= 2;
//                }
//            }
//            return inSampleSize;
//        }
//    }
}
