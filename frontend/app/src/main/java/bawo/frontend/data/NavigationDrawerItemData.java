package bawo.frontend.data;

import java.util.ArrayList;
import java.util.List;

import bawo.frontend.R;
import bawo.frontend.models.NavigationDrawerItem;

public class NavigationDrawerItemData {
    public static List<NavigationDrawerItem> getData(){
        List<NavigationDrawerItem> dataList = new ArrayList<>();

        int[] imageIds = getImages();
        String[] titles = getTitles();

        for(int i = 0; i< titles.length; i++){
            NavigationDrawerItem navItem = new NavigationDrawerItem();
            navItem.setTitle(titles[i]);
            navItem.setImageId(imageIds[i]);
            dataList.add(navItem);
        }
        return dataList;
    }

    private static int[] getImages(){
        return new int[]{
                R.drawable.settings, R.drawable.logout
        };
    }

    private  static String[] getTitles(){
        return new String[]{
                "Account Settings", "Logout"

        };
    }

}
