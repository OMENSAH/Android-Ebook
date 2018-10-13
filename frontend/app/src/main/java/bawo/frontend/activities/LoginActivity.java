package bawo.frontend.activities;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.authentication.storage.CredentialsManagerException;
import com.auth0.android.authentication.storage.SecureCredentialsManager;
import com.auth0.android.authentication.storage.SharedPreferencesStorage;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;

import bawo.frontend.R;
import bawo.frontend.utils.Constants;
import bawo.frontend.utils.UIUtils;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton;
    private Auth0 auth0;
    private ProgressBar progressBar;
    private SecureCredentialsManager credentialsManager;
    public static final String ACCESS_TOKEN = "com.auth0.ACCESS_TOKEN";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeViews();
        eventListeners();
        auth0 = new Auth0(this);
        auth0.setOIDCConformant(true);

        AuthenticationAPIClient client = new AuthenticationAPIClient(auth0);
        credentialsManager = new SecureCredentialsManager(this, client, new SharedPreferencesStorage(this));

        if (getIntent().getBooleanExtra(Constants.KEY_CLEAR_CREDENTIALS, false)) {
            credentialsManager.clearCredentials();
        }

        // Obtain the existing credentials and move to the next activity
        credentialsManager.getCredentials(new BaseCallback<Credentials, CredentialsManagerException>() {
            @Override
            public void onSuccess(final Credentials credentials) {
                UIUtils.showProgressBar(progressBar);
                showNextActivity(credentials);
            }

            @Override
            public void onFailure(CredentialsManagerException error) {
                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeViews(){
        loginButton = findViewById(R.id.login_button_login);
        progressBar = findViewById(R.id.login_progressBar);
    }

    private void eventListeners(){
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               login();
            }
        });
    }

    private void login() {
        UIUtils.showProgressBar(progressBar);
        WebAuthProvider.init(auth0)
                .withScheme("demo")
                .withAudience("DigestApp-API")
                .withScope("openid profile email offline_access read:current_user update:current_user_metadata")
                .start(LoginActivity.this, new AuthCallback() {
                    @Override
                    public void onFailure(@NonNull Dialog dialog) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Log In - Error Occurred", Toast.LENGTH_SHORT).show();
                                UIUtils.hideProgressBar(progressBar);
                            }
                        });
                    }
                    @Override
                    public void onFailure(final AuthenticationException exception) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Error: "+ exception.getMessage(), Toast.LENGTH_SHORT).show();
                                UIUtils.hideProgressBar(progressBar);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(@NonNull final com.auth0.android.result.Credentials credentials) {
                        credentialsManager.saveCredentials(credentials);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Successfully Logged", Toast.LENGTH_SHORT).show();
                                showNextActivity(credentials);
                            }
                        });
                    }
                });
    }

    private void showNextActivity(Credentials credentials) {
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        intent.putExtra(ACCESS_TOKEN, credentials.getAccessToken());
        startActivity(intent);
        finish();
        UIUtils.hideProgressBar(progressBar);
    }
}