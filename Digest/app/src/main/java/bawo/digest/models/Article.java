package bawo.digest.models;

import java.io.Serializable;

public class Article implements Serializable{
    private String title;
    private String details;
    private int imageId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    @Override
    public String toString() {
        return "Article{" +
                "title='" + title + '\'' +
                ", details='" + details + '\'' +
                ", imageId=" + imageId +
                '}';
    }
}
