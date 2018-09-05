package bawo.digest.activities;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.storage.CredentialsManagerException;
import com.auth0.android.authentication.storage.SecureCredentialsManager;
import com.auth0.android.authentication.storage.SharedPreferencesStorage;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.result.Credentials;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import bawo.digest.R;
import bawo.digest.adapters.ArticleAdapter;
import bawo.digest.data.MockArticleData;
import bawo.digest.fragments.NavigationDrawerFragment;
import bawo.digest.utils.Constants;

public class DashboardActivity extends AppCompatActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        initializeViews();
        setupDrawer();
        setupMenu();
        setupRecyclerView();
        getArticles();
    }

    private void initializeViews(){
        toolbar = findViewById(R.id.dashboard_toolbar);
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
                        break;

                    case R.id.review:

                        break;
                }
                startActivity(intent);
                return false;
            }
        });
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.dashboard_recyclerView);
        ArticleAdapter adapter = new ArticleAdapter(this, MockArticleData.getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    private void getArticles(){
        Auth0 auth0 = new Auth0(this);
        auth0.setOIDCConformant(true);
        AuthenticationAPIClient authenticationAPIClient = new AuthenticationAPIClient(auth0);
        SecureCredentialsManager credentialsManager = new SecureCredentialsManager(this, authenticationAPIClient, new SharedPreferencesStorage(this));
        credentialsManager.getCredentials((new BaseCallback<Credentials, CredentialsManagerException>() {
            @Override
            public void onSuccess(Credentials credentials) {
                Constants.ACCESSTOKEN = credentials.getAccessToken();

            }

            @Override
            public void onFailure(CredentialsManagerException error) {

            }
        }));
        if (Constants.ACCESSTOKEN == null) {
            Toast.makeText(DashboardActivity.this, "Token not found. Log in first.", Toast.LENGTH_SHORT).show();
            return;
        }
        final Request.Builder reqBuilder = new Request.Builder()
                .get()
                .url(Constants.BASE_URL+"/private");
        reqBuilder.addHeader("Authorization", "Bearer " + Constants.ACCESSTOKEN);
        OkHttpClient client = new OkHttpClient();
        Request request = reqBuilder.build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DashboardActivity.this, "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    final String responseData = response.body().string();
                    // Run view-related code back on the main thread
                    DashboardActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DashboardActivity.this, responseData, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

    }

}
