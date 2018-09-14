package bawo.digest.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import bawo.digest.R;
import bawo.digest.utils.Constants;
import bawo.digest.utils.UIUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostActivity extends AppCompatActivity {
    private String accessToken;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        setupWidgets();

        if(getIntent().getStringExtra(LoginActivity.ACCESS_TOKEN) != null)
            accessToken = getIntent().getStringExtra(LoginActivity.ACCESS_TOKEN);

        JSONObject data = new JSONObject();
        try {
            data.put("title", "title");
            data.put("author", "author");
            data.put("featuredImage", "Image");
            data.put("body", "body");
            postArticle(data, accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupWidgets(){
        progressBar = findViewById(R.id.post_progressBar);
    }

    private void postArticle(JSONObject data, String accessToken) throws IOException {
        UIUtils.showProgressBar(progressBar);

        MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, data.toString());
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
                            startActivity(new Intent(PostActivity.this, DashboardActivity.class));
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UIUtils.hideProgressBar(progressBar);
                            Toast.makeText(PostActivity.this, "Request error: " + responseData, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    }