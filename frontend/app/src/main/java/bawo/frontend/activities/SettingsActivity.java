package bawo.frontend.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.management.ManagementException;
import com.auth0.android.management.UsersAPIClient;
import com.auth0.android.result.UserProfile;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import bawo.frontend.R;
import bawo.frontend.utils.Constants;
import bawo.frontend.utils.UIUtils;

public class SettingsActivity extends AppCompatActivity {
    private AuthenticationAPIClient authenticationAPIClient;
    private UsersAPIClient usersClient;
    private UserProfile userProfile;

    private ProgressBar progressBar;
    private Button btn;
    private EditText name;

    private String accessToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Obtain the token from dashboard
        accessToken = DashboardActivity.accessToken;

        Auth0 auth0 = new Auth0(this);
        auth0.setOIDCConformant(true);
        auth0.setLoggingEnabled(true);
        authenticationAPIClient = new AuthenticationAPIClient(auth0);
        usersClient = new UsersAPIClient(auth0, accessToken);
        btn = findViewById(R.id.buttonUpdate);
        name = findViewById(R.id.inputEditTextName);
        progressBar = findViewById(R.id.progressBar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInformation(name.getText().toString());
            }
        });
        getProfile(accessToken);
    }

    private void getProfile(String accessToken) {
        UIUtils.showProgressBar(progressBar);
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
                                                UIUtils.hideProgressBar(progressBar);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(ManagementException error) {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(SettingsActivity.this, "User Profile Request Failed", Toast.LENGTH_SHORT).show();
                                                UIUtils.hideProgressBar(progressBar);
                                            }
                                        });
                                    }
                                });
                    }
                    @Override
                    public void onFailure(AuthenticationException error) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(SettingsActivity.this, "User Info Request Failed", Toast.LENGTH_SHORT).show();
                                UIUtils.hideProgressBar(progressBar);
                            }
                        });
                    }
                });
    }

 private void updateInformation(final String name) {
        UIUtils.showProgressBar(progressBar);
        Map<String, Object> userMetadata = new HashMap<>();
        userMetadata.put("name", name);
        usersClient.updateMetadata(userProfile.getId(), userMetadata)
                .start(new BaseCallback<UserProfile, ManagementException>() {
                    @Override
                    public void onSuccess(final UserProfile profile) {
                        userProfile = profile;
                        runOnUiThread(new Runnable() {
                            public void run() {
                                UIUtils.hideProgressBar(progressBar);
                                Intent intent = new Intent(SettingsActivity.this, DashboardActivity.class);
                                intent.putExtra(Constants.ACCESS_TOKEN, accessToken);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onFailure(ManagementException error) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                UIUtils.hideProgressBar(progressBar);
                                Toast.makeText(SettingsActivity.this, "Profile Update Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }

    private void refreshInformation() {
        EditText name =  findViewById(R.id.inputEditTextName);
        name.setText(userProfile.getUserMetadata().get("name") != null ? userProfile.getUserMetadata().get("name") .toString(): userProfile.getName());
        ImageView userPicture =  findViewById(R.id.userImageProfile);
        if (userProfile.getPictureURL() != null) {
            Picasso.get()
                    .load(userProfile.getPictureURL())
                    .into(userPicture);
        }
    }

}
