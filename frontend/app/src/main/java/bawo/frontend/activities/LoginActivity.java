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
    private ProgressBar progressBar;


    private Auth0 auth0;
    private SecureCredentialsManager credentialsManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth0 = new Auth0(this);
        auth0.setOIDCConformant(true);
        credentialsManager = new SecureCredentialsManager(this, new AuthenticationAPIClient(auth0), new SharedPreferencesStorage(this));

// Check if the activity was launched after a logout
        if (getIntent().getBooleanExtra(Constants.KEY_CLEAR_CREDENTIALS, false)) {
            credentialsManager.clearCredentials();
        }

        // Check if a log in button must be shown
        if (!credentialsManager.hasValidCredentials()) {
            setContentView(R.layout.activity_login);
            loginButton = findViewById(R.id.login_button_login);
            progressBar = findViewById(R.id.login_progressBar);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    login();
                }
            });
            return;
        }

        // Obtain the existing credentials and move to the next activity
        credentialsManager.getCredentials(new BaseCallback<Credentials, CredentialsManagerException>() {
            @Override
            public void onSuccess(final Credentials credentials) {
                showNextActivity(credentials);
            }

            @Override
            public void onFailure(CredentialsManagerException error) {
                //Authentication cancelled by the user. Exit the app
                finish();
            }
        });
    }

    /**
     * Override required when setting up Local Authentication in the Credential Manager
     * Refer to SecureCredentialsManager#requireAuthentication method for more information.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (credentialsManager.checkAuthenticationResult(requestCode, resultCode)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void login() {
        UIUtils.showProgressBar(progressBar);
        WebAuthProvider.init(auth0)
                .withScheme("demo")
                .withAudience(String.format("https://%s/api/v2/", getString(R.string.com_auth0_domain)))
                .withScope("openid profile email offline_access read:current_user update:current_user_metadata")
                .start(this, webCallback);
    }

    private final AuthCallback webCallback = new AuthCallback() {
        @Override
        public void onFailure(@NonNull final Dialog dialog) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.show();
                }
            });
        }

        @Override
        public void onFailure(final AuthenticationException exception) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, "Log In - Error Occurred : " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    UIUtils.hideProgressBar(progressBar);
                }
            });
        }

        @Override
        public void onSuccess(@NonNull Credentials credentials) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, "Log In - Success", Toast.LENGTH_SHORT).show();
                    UIUtils.hideProgressBar(progressBar);
                }
            });
            credentialsManager.saveCredentials(credentials);
            showNextActivity(credentials);
        }
    };
    private void showNextActivity(Credentials credentials) {
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        intent.putExtra(Constants.ACCESS_TOKEN, credentials.getAccessToken());
        intent.putExtra(Constants.KEY_ID_TOKEN, credentials.getIdToken());
        startActivity(intent);
        finish();
    }

}
