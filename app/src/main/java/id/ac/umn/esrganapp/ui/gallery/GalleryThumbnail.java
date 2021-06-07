package id.ac.umn.esrganapp.ui.gallery;

import android.graphics.Bitmap;

public class GalleryThumbnail {
    private String path;
    private Bitmap image;
    private boolean checked;

    public GalleryThumbnail(String path, Bitmap image) {
        this.path = path;
        this.image = image;
        checked = false;
    }


    public String getPath() {
        return path;
    }

    public boolean getChecked(){ return checked;}
    public void setCheckedTrue() {checked = true;}
    public void setCheckedFalse() {checked = false;}
    public Bitmap getImage() {
        return image;
    }
}
