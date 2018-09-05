package bawo.digest.utils;

import android.view.View;
import android.widget.ProgressBar;

public class UIUtils {
    public static void showProgressBar(ProgressBar progressBar){
        progressBar.setVisibility(View.VISIBLE);
    }

    public static void hideProgressBar(ProgressBar progressBar){
        if(progressBar.getVisibility() == View.VISIBLE){
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
