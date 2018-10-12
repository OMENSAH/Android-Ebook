package bawo.frontend.models;

public class NavigationDrawerItem {
    private String title;
    private int iconId;

    public int getImageId() {
        return iconId;
    }

    public void setImageId(int iconId) {

        this.iconId = iconId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
