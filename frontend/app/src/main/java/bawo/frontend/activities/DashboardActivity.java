package bawo.frontend.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.management.ManagementException;
import com.auth0.android.management.UsersAPIClient;
import com.auth0.android.result.UserProfile;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import bawo.frontend.R;
import bawo.frontend.adapters.ArticleAdapter;
import bawo.frontend.fragments.NavigationDrawerFragment;
import bawo.frontend.models.Article;
import bawo.frontend.utils.Constants;
import bawo.frontend.utils.UIUtils;

public class DashboardActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ArrayList<Article> articles;
    private String accessToken;
    private ArticleAdapter articleAdapter;

    private AuthenticationAPIClient authenticationAPIClient;
    private UsersAPIClient usersClient;
    private UserProfile userProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        initializeContents();
        setupDrawer();
        setupMenu();
    }

    private void initializeContents(){
        String firstWord = "Recent", lastWord =" Student News";
        toolbar = findViewById(R.id.dashboard_toolbar);
        Spannable spannable = new SpannableString(firstWord+lastWord);
        int firstWordColor = Color.WHITE;
        spannable.setSpan(new ForegroundColorSpan(firstWordColor), 0, firstWord.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        int lastWordColor = Color.BLACK;
        spannable.setSpan(new ForegroundColorSpan(lastWordColor), firstWord.length(),
                firstWord.length()+lastWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        toolbar.setTitle(spannable);
        progressBar = findViewById(R.id.dashboard_progressBar);

        recyclerView = findViewById(R.id.dashboard_recyclerView);
        articles = new ArrayList<>();
        articleAdapter = new ArticleAdapter(this, articles);
        recyclerView.setAdapter(articleAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        if(getIntent().getStringExtra(Constants.ACCESS_TOKEN) != null ){
//            accessToken = getIntent().getStringExtra(Constants.ACCESS_TOKEN);
//            getArticles(accessToken);
//            Auth0 auth0 = new Auth0(this);
//            authenticationAPIClient = new AuthenticationAPIClient(auth0);
//            usersClient = new UsersAPIClient(auth0, accessToken);
//            getProfile(accessToken);
//        }

        //Obtain the token from the Intent's extras
        String accessToken = getIntent().getStringExtra(Constants.ACCESS_TOKEN);

        Auth0 auth0 = new Auth0(this);
        auth0.setOIDCConformant(true);
        auth0.setLoggingEnabled(true);
        authenticationAPIClient = new AuthenticationAPIClient(auth0);
        usersClient = new UsersAPIClient(auth0, accessToken);
        getProfile(accessToken);
        getArticles(accessToken);
    }

    private void setupDrawer() {
        NavigationDrawerFragment fragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.dashboard_drawerNavigationFragment);
        DrawerLayout drawerLayout = findViewById(R.id.dashboard_drawerLayout);
        fragment.setUpDrawer(R.id.dashboard_drawerNavigationFragment, drawerLayout, toolbar);
    }

    private void setupMenu() {
        toolbar.inflateMenu(R.menu.menus);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = null;
                switch (item.getItemId()) {
                    case R.id.add:
                        intent = new Intent(DashboardActivity.this, PostActivity.class);
                        intent.putExtra(Constants.ACCESS_TOKEN, accessToken);
                        break;
                }
                startActivity(intent);
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        articleAdapter.notifyDataSetChanged();
    }

    private void getArticles(String accessToken){
        UIUtils.showProgressBar(progressBar);
        final Request.Builder reqBuilder = new Request.Builder()
                .get()
                .url(Constants.BASE_URL+"/all-articles");
        reqBuilder.addHeader("Authorization", "Bearer " + accessToken);
        OkHttpClient client = new OkHttpClient();
        Request request = reqBuilder.build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UIUtils.hideProgressBar(progressBar);
                        Toast.makeText(DashboardActivity.this, "Request error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(final Response response) throws IOException {
                final String responseData = response.body().string();
                if (response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UIUtils.hideProgressBar(progressBar);
                            Gson gson = new Gson();
                            Type articleType = new TypeToken<ArrayList<Article>>(){}.getType();
                            ArrayList<Article> data = gson.fromJson(responseData, articleType);
                            articles.clear();
                            articles.addAll(data);
                            articleAdapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UIUtils.hideProgressBar(progressBar);
                            Toast.makeText(DashboardActivity.this, responseData, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

    }

    private void getProfile(String accessToken) {
        authenticationAPIClient.userInfo(accessToken)
                .start(new BaseCallback<UserProfile, AuthenticationException>() {
                    @Override
                    public void onSuccess(UserProfile userinfo) {
                        usersClient.getProfile(userinfo.getId())
                                .start(new BaseCallback<UserProfile, ManagementException>() {
                                    @Override
                                    public void onSuccess(UserProfile profile) {
                                        userProfile = profile;
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                refreshInformation();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(ManagementException error) {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(DashboardActivity.this, "User Profile Request Failed", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                    }

                    @Override
                    public void onFailure(AuthenticationException error) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(DashboardActivity.this, "User Info Request Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }

    private void refreshInformation() {
        TextView userNameTextView = findViewById(R.id.profile_name);
        userNameTextView.setText(userProfile.getName());
        ImageView userPicture =  findViewById(R.id.profile_image);
        if (userProfile.getPictureURL() != null) {
            Picasso.get()
                    .load(userProfile.getPictureURL())
                    .into(userPicture);
        }
    }



}
