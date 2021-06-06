package id.ac.umn.esrganapp.ui.gallery;

public class Images {

    public String imgUrl, email, img_uri;

    public Images(String email, String imgUrl, String img_uri){
        this.email = email;
        this.imgUrl = imgUrl;
        this.img_uri = img_uri;
    }

    public String getImg_uri() {
        return img_uri;
    }

    public void setImg_uri(String imgUrl) {
        this.img_uri = img_uri;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
