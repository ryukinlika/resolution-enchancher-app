package id.ac.umn.esrganapp.ui.gallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import id.ac.umn.esrganapp.MainActivity;
import id.ac.umn.esrganapp.R;

public class GalleryFragment extends Fragment  implements GalleryRecyclerViewAdapter.ItemClickListener{

    private RecyclerView recyclerView;
    private GalleryRecyclerViewAdapter adapter;
    private List<GalleryThumbnail> data;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_gallery, container, false);


        data = getImagesFromExternalDir();


        recyclerView = root.findViewById(R.id.recyclerView);
        int numberOfColumns = 4;
        recyclerView.setLayoutManager(new GridLayoutManager(root.getContext(), numberOfColumns));
        adapter = new GalleryRecyclerViewAdapter(root.getContext(), data);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);



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
        Intent intent = new Intent(this.getContext(), ViewImageActivity.class);
        intent.putExtra("image_path", path);
        startActivity(intent);
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
}