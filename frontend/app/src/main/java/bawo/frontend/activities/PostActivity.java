package bawo.frontend.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import bawo.frontend.R;
import bawo.frontend.models.Article;
import bawo.frontend.utils.Constants;
import bawo.frontend.utils.UIUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostActivity extends AppCompatActivity {
    //access token from Auth0
    private String accessToken;
    //user
    private String user;
    //declaring views
    private ImageButton articleImage;
    private EditText articleTitle;
    private EditText articleBody;
    private Button button;
    private ProgressBar progressBar;

    private String imagePath;
    private static final int GALLERY_CODE = 1;
    //Uri to store the image uri
    private Uri filePath;

    //storage permission code
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        setupWidgets();
        if (getIntent().getStringExtra(Constants.ACCESS_TOKEN) != null || getIntent().getStringExtra(Constants.User) != null ){
            accessToken = getIntent().getStringExtra(Constants.ACCESS_TOKEN);
            user = getIntent().getStringExtra(Constants.User);
        }
        setEventListeners();
    }

    private void setupWidgets() {
        progressBar = findViewById(R.id.post_progressBar);
        articleImage = findViewById(R.id.post_imageButton);
        articleTitle = findViewById(R.id.post_Title);
        articleBody = findViewById(R.id.post_description);
        button = findViewById(R.id.post_button_post);
    }

    private void setEventListeners() {
        articleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canSelectImage();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imagePath != null && !articleBody.getText().toString().equals("") && !articleTitle.getText().toString().equals("")) {
                    Article article = new Article();
                    article.setTitle(  articleTitle.getText().toString());
                    article.setBody(articleBody.getText().toString());
                    article.setAuthor(user);
                    article.setFeaturedImage(imagePath);
                    article.setPosted_on(String.valueOf(System.currentTimeMillis()));
                    MultipartBody body = uploadRequestBody(article);
                    try {
                        postArticle(body, accessToken);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(PostActivity.this, "Complete the form", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void canSelectImage(){
        if(checkPermissionREAD_EXTERNAL_STORAGE(this)){
            chooseImage();
        }else{
            Toast.makeText(this, "No Permissions", Toast.LENGTH_SHORT).show();
        }
    }
    private void chooseImage(){
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_CODE);
    }

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context, Manifest.permission.READ_EXTERNAL_STORAGE);
                } else {
                    ActivityCompat.requestPermissions((Activity) context,  new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseImage();
                } else {
                    Toast.makeText(PostActivity.this, "Excess to Read External Storage Denied",     Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void showDialog(final String msg, final Context context, final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{permission},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(this, "Unable to Upload Data", Toast.LENGTH_SHORT).show();
                return;
            } else {
                filePath = data.getData();
                articleImage.setImageURI(filePath);
                imagePath = getPath(filePath);
            }
        }
    }


    //method to get the file path from uri
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();
        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }

    public  MultipartBody uploadRequestBody(Article article) {
        File file = new File(article.getFeaturedImage());
        MediaType MEDIA_TYPE = MediaType.parse("image/jpeg");
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("featuredImage", file.getName(), RequestBody.create(MEDIA_TYPE, file))
                .addFormDataPart("body", article.getBody())
                .addFormDataPart("title", article.getTitle())
                .addFormDataPart("author", article.getAuthor())
                .addFormDataPart("posted_on", article.getPosted_on())
                .build();
    }

    private void postArticle(RequestBody body, final String accessToken) throws IOException {
        Log.i("me", "postArticle: "+ body.toString());
        UIUtils.showProgressBar(progressBar);
        Request.Builder reqBuilder = new Request.Builder()
                .post(body)
                .url(Constants.BASE_URL + "/add-article");
        reqBuilder.addHeader("Authorization", "Bearer " + accessToken);
        OkHttpClient client = new OkHttpClient();
        Request request = reqBuilder.build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UIUtils.hideProgressBar(progressBar);
                        Toast.makeText(PostActivity.this, "Request error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                if (response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UIUtils.hideProgressBar(progressBar);
                            Toast.makeText(PostActivity.this, responseData, Toast.LENGTH_LONG).show();
                            Intent intent =  new Intent(PostActivity.this, DashboardActivity.class);
                            intent.putExtra(Constants.ACCESS_TOKEN, accessToken);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UIUtils.hideProgressBar(progressBar);
                            Toast.makeText(PostActivity.this, "Data Failure: " + responseData, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}