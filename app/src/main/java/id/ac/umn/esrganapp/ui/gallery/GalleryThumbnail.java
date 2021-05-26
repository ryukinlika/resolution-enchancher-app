package id.ac.umn.esrganapp.ui.gallery;

import android.graphics.Bitmap;

public class GalleryThumbnail {
    private String path;
    private Bitmap image;


    public GalleryThumbnail(String path, Bitmap image) {
        this.path = path;
        this.image = image;
    }


    public String getPath() {
        return path;
    }



    public Bitmap getImage() {
        return image;
    }
}
