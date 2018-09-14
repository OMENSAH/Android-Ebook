package bawo.digest.models;

import java.io.Serializable;

public class Article implements Serializable{
    private String title;
    private String author;
    private String body;
    private String featuredImage;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFeaturedImage() {
        return featuredImage;
    }

    public void setFeaturedImage(String featuredImage) {
        this.featuredImage = featuredImage;
    }

    @Override
    public String toString() {
        return "Article{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", body='" + body + '\'' +
                ", featuredImage='" + featuredImage + '\'' +
                '}';
    }
}
