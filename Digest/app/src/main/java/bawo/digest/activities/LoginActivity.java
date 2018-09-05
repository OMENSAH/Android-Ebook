package bawo.digest.activities;

import android.app.Dialog;
import android.content.Intent;
import android.net.Credentials;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.authentication.storage.SecureCredentialsManager;
import com.auth0.android.authentication.storage.SharedPreferencesStorage;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.WebAuthProvider;

import bawo.digest.R;
import bawo.digest.utils.Constants;
import bawo.digest.utils.UIUtils;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton;
    private Auth0 auth0;
    private ProgressBar progressBar;
    private SecureCredentialsManager credentialsManager;

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

        // Check if the activity was launched after a logout
        if (getIntent().getBooleanExtra(Constants.KEY_CLEAR_CREDENTIALS, false)) {
            credentialsManager.clearCredentials();
        }
        //checking for session
        if (credentialsManager.hasValidCredentials()) {
            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
            finish();
        }
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
                .withAudience(String.format("https://%s/userinfo", getString(R.string.com_auth0_domain)))
                .withScope("openid offline_access")
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
                    public void onSuccess(@NonNull com.auth0.android.result.Credentials credentials) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Successfully Logged", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                                finish();
                                UIUtils.hideProgressBar(progressBar);
                            }
                        });
                        credentialsManager.saveCredentials(credentials);
                    }
                });
    }



}
