package com.bananabreach;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bananabreach.ui.auth.LoginActivity;
import com.bananabreach.ui.dashboard.DashboardActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private SharedPreferences sharedPreferences;
    private ImageView logoImage;
    private TextView appNameText;
    private TextView versionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("banana_prefs", MODE_PRIVATE);

        initializeViews();
        checkSessionAndNavigate();
    }

    private void initializeViews() {
        logoImage = findViewById(R.id.logo_image);
        appNameText = findViewById(R.id.app_name_text);
        versionText = findViewById(R.id.version_text);

        versionText.setText("Version " + BuildConfig.VERSION_NAME);
    }

    private void checkSessionAndNavigate() {
        String authToken = sharedPreferences.getString("auth_token", null);
        String userId = sharedPreferences.getString("user_id", null);

        new Handler().postDelayed(() -> {
            if (authToken != null && userId != null) {
                // NOTE: token "validation" happens entirely on-device — see
                // docs/VULNERABILITIES.md (Authentication: client-side token validation)
                if (validateTokenLocally(authToken)) {
                    navigateToDashboard();
                } else {
                    navigateToLogin();
                }
            } else {
                navigateToLogin();
            }
        }, 2000);
    }

    private boolean validateTokenLocally(String token) {
        // Intentionally weak: only checks length, never calls the server.
        return token != null && token.length() > 10;
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
}
