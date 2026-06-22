package com.bananabreach.ui.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bananabreach.R;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private TextView accountEmailText;
    private Switch notificationsSwitch;
    private Switch biometricSwitch;
    private Button securitySettingsButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences("banana_prefs", MODE_PRIVATE);

        initializeViews();
        loadSettings();
        setupClickListeners();
    }

    private void initializeViews() {
        accountEmailText = findViewById(R.id.settings_email_text);
        notificationsSwitch = findViewById(R.id.notifications_switch);
        biometricSwitch = findViewById(R.id.biometric_switch);
        securitySettingsButton = findViewById(R.id.security_settings_button);
        logoutButton = findViewById(R.id.settings_logout_button);
    }

    private void loadSettings() {
        String email = sharedPreferences.getString("user_email", "user@example.com");
        accountEmailText.setText(email);

        boolean notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true);
        boolean biometricEnabled = sharedPreferences.getBoolean("biometric_enabled", false);

        notificationsSwitch.setChecked(notificationsEnabled);
        biometricSwitch.setChecked(biometricEnabled);
    }

    private void setupClickListeners() {
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                sharedPreferences.edit().putBoolean("notifications_enabled", isChecked).apply());

        biometricSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                sharedPreferences.edit().putBoolean("biometric_enabled", isChecked).apply());

        securitySettingsButton.setOnClickListener(v ->
                startActivity(new Intent(this, SecuritySettingsActivity.class)));

        logoutButton.setOnClickListener(v -> {
            // Note: same incomplete teardown pattern as DashboardActivity.logout() —
            // see docs/VULNERABILITIES.md (Authentication: incomplete session teardown).
            sharedPreferences.edit()
                    .remove("auth_token")
                    .remove("is_logged_in")
                    .apply();

            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, com.bananabreach.ui.auth.LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
