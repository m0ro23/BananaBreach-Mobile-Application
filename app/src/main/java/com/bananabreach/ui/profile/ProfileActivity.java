package com.bananabreach.ui.profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bananabreach.R;
import com.bananabreach.utils.SecurityUtils;

public class ProfileActivity extends AppCompatActivity {

    private TextView userNameTextView;
    private TextView userEmailTextView;
    private TextView userIdTextView;
    private ImageView profileImageView;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private Button saveButton;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPreferences = getSharedPreferences("banana_prefs", MODE_PRIVATE);

        initializeViews();
        loadUserData();
        setupClickListeners();
    }

    private void initializeViews() {
        userNameTextView = findViewById(R.id.profile_user_name);
        userEmailTextView = findViewById(R.id.profile_user_email);
        userIdTextView = findViewById(R.id.profile_user_id);
        profileImageView = findViewById(R.id.profile_image);
        nameEditText = findViewById(R.id.profile_name_edit);
        emailEditText = findViewById(R.id.profile_email_edit);
        phoneEditText = findViewById(R.id.profile_phone_edit);
        saveButton = findViewById(R.id.save_profile_button);
    }

    private void loadUserData() {
        String userId = sharedPreferences.getString("user_id", "N/A");
        String userName = sharedPreferences.getString("user_name", "User");
        String userEmail = sharedPreferences.getString("user_email", "user@example.com");
        String userPhone = sharedPreferences.getString("user_phone", "");

        userIdTextView.setText("ID: " + userId);
        userNameTextView.setText(userName);
        userEmailTextView.setText(userEmail);

        nameEditText.setText(userName);
        emailEditText.setText(userEmail);
        phoneEditText.setText(userPhone);

        android.util.Log.i("ProfileActivity",
                "Loading profile for user: " + userId + " (" + userName + ")");
    }

    private void setupClickListeners() {
        saveButton.setOnClickListener(v -> saveProfile());

        profileImageView.setOnClickListener(v ->
                Toast.makeText(this, "Feature coming soon", Toast.LENGTH_SHORT).show());
    }

    private void saveProfile() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_name", name);
        editor.putString("user_email", email);
        editor.putString("user_phone", phone);
        editor.apply();

        // Intentional: weak hash stored alongside plaintext fields
        String nameHash = SecurityUtils.hashMD5(name);
        editor.putString("user_name_hash", nameHash);
        editor.apply();

        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

        userNameTextView.setText(name);
        userEmailTextView.setText(email);
    }
}
