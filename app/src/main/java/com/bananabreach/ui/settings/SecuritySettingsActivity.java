package com.bananabreach.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bananabreach.R;
import com.bananabreach.utils.SecurityUtils;

public class SecuritySettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private TextView currentSessionText;
    private EditText newPasswordEditText;
    private EditText confirmPasswordEditText;
    private Button changePasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_settings);

        sharedPreferences = getSharedPreferences("banana_prefs", MODE_PRIVATE);

        initializeViews();
        loadSessionInfo();
        setupClickListeners();
    }

    private void initializeViews() {
        currentSessionText = findViewById(R.id.current_session_text);
        newPasswordEditText = findViewById(R.id.new_password_input);
        confirmPasswordEditText = findViewById(R.id.confirm_password_input);
        changePasswordButton = findViewById(R.id.change_password_button);
    }

    private void loadSessionInfo() {
        // Intentional: displays the raw locally-stored token, reinforcing that
        // it's treated as plain, readable client state — see
        // docs/VULNERABILITIES.md (Data Storage: plaintext session storage).
        String token = sharedPreferences.getString("auth_token", "none");
        currentSessionText.setText("Active session token: " + token);
    }

    private void setupClickListeners() {
        changePasswordButton.setOnClickListener(v -> {
            String newPassword = newPasswordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            if (newPassword.isEmpty() || !newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Intentional: same weak MD5 hashing path used everywhere else in the app.
            String hashed = SecurityUtils.hashMD5(newPassword);
            sharedPreferences.edit().putString("user_credential", hashed).apply();

            Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show();
            newPasswordEditText.setText("");
            confirmPasswordEditText.setText("");
        });
    }
}
