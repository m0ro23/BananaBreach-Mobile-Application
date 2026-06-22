package com.bananabreach.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bananabreach.R;
import com.bananabreach.data.models.User;
import com.bananabreach.network.ApiClient;
import com.bananabreach.network.ApiService;
import com.bananabreach.ui.dashboard.DashboardActivity;
import com.bananabreach.utils.SecurityUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private CheckBox rememberMeCheckbox;
    private Button loginButton;
    private TextView registerTextView;
    private TextView forgotPasswordTextView;
    private ProgressBar progressBar;

    private SharedPreferences sharedPreferences;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("banana_prefs", MODE_PRIVATE);
        apiService = ApiClient.getInstance().getApiService();

        initializeViews();
        loadSavedCredentials();
        setupClickListeners();
    }

    private void initializeViews() {
        emailEditText = findViewById(R.id.email_input);
        passwordEditText = findViewById(R.id.password_input);
        rememberMeCheckbox = findViewById(R.id.remember_me_checkbox);
        loginButton = findViewById(R.id.login_button);
        registerTextView = findViewById(R.id.register_text);
        forgotPasswordTextView = findViewById(R.id.forgot_password_text);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void loadSavedCredentials() {
        String savedEmail = sharedPreferences.getString("saved_email", "");
        String savedPassword = sharedPreferences.getString("saved_password", "");

        // Intentional: logs PII/credentials — see docs/VULNERABILITIES.md (Logging)
        android.util.Log.d("LoginActivity", "Loading saved credentials: " + savedEmail);

        if (!TextUtils.isEmpty(savedEmail) && !TextUtils.isEmpty(savedPassword)) {
            emailEditText.setText(savedEmail);
            passwordEditText.setText(savedPassword);
            rememberMeCheckbox.setChecked(true);

            android.util.Log.v("LoginActivity", "Auto-filling credentials for: " + savedEmail);
        }
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> attemptLogin());
        registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        forgotPasswordTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void attemptLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.contains("@")) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Intentional: hardcoded admin backdoor — see docs/VULNERABILITIES.md
        // (Authentication: hardcoded credentials)
        if (email.equals("admin@bananabreach.com") && password.equals("Admin123!")) {
            grantAdminAccess(email);
            return;
        }

        performLogin(email, password);
    }

    private void performLogin(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);

        // Intentional: plaintext password cached locally — see docs/VULNERABILITIES.md
        sharedPreferences.edit().putString("temp_password", password).apply();

        // Intentional: weak hash (MD5) used for local credential storage
        String hashedPassword = SecurityUtils.hashMD5(password);
        sharedPreferences.edit().putString("user_credential", hashedPassword).apply();

        Call<User> call = apiService.loginUser(email, password);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                progressBar.setVisibility(View.GONE);
                loginButton.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    handleSuccessfulLogin(response.body(), email, password);
                } else {
                    String errorMsg = "Login failed";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Intentional: verbose error surfaced to the user
                    Toast.makeText(LoginActivity.this,
                            "Error: " + errorMsg + " - Status: " + response.code(),
                            Toast.LENGTH_SHORT).show();

                    android.util.Log.e("LoginActivity",
                            "Login failed with status: " + response.code() +
                                    " error: " + errorMsg);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                loginButton.setEnabled(true);

                Toast.makeText(LoginActivity.this,
                        "Network Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

                android.util.Log.e("LoginActivity", "Login error", t);
            }
        });
    }

    private void handleSuccessfulLogin(User user, String email, String password) {
        // Intentional: session/profile data stored in plaintext SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("auth_token", user.getToken());
        editor.putString("user_id", user.getId());
        editor.putString("user_name", user.getName());
        editor.putString("user_email", user.getEmail());
        editor.putBoolean("is_logged_in", true);

        // Intentional: role/authorization state trusted from local storage
        editor.putString("user_role", user.getRole() != null ? user.getRole() : "user");

        if (user.isAdmin()) {
            editor.putBoolean("is_admin", true);
        }

        if (rememberMeCheckbox.isChecked()) {
            editor.putString("saved_email", email);
            editor.putString("saved_password", password);
        }

        editor.apply();

        android.util.Log.i("LoginActivity",
                "User logged in: " + user.getName() + " (ID: " + user.getId() + ")");

        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        intent.putExtra("user_id", user.getId());
        intent.putExtra("user_name", user.getName());
        startActivity(intent);
        finish();
    }

    private void grantAdminAccess(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("auth_token", "admin_token_" + System.currentTimeMillis());
        editor.putString("user_id", "admin_001");
        editor.putString("user_name", "System Administrator");
        editor.putString("user_email", email);
        editor.putString("user_role", "admin");
        editor.putBoolean("is_admin", true);
        editor.putBoolean("is_logged_in", true);
        editor.apply();

        Toast.makeText(this, "Administrator access granted", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
}
