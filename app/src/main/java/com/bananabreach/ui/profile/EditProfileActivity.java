package com.bananabreach.ui.profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bananabreach.R;

public class EditProfileActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        sharedPreferences = getSharedPreferences("banana_prefs", MODE_PRIVATE);

        nameEditText = findViewById(R.id.edit_name_input);
        emailEditText = findViewById(R.id.edit_email_input);
        phoneEditText = findViewById(R.id.edit_phone_input);
        saveButton = findViewById(R.id.edit_save_button);

        loadCurrentValues();

        saveButton.setOnClickListener(v -> saveChanges());
    }

    private void loadCurrentValues() {
        nameEditText.setText(sharedPreferences.getString("user_name", ""));
        emailEditText.setText(sharedPreferences.getString("user_email", ""));
        phoneEditText.setText(sharedPreferences.getString("user_phone", ""));
    }

    private void saveChanges() {
        sharedPreferences.edit()
                .putString("user_name", nameEditText.getText().toString().trim())
                .putString("user_email", emailEditText.getText().toString().trim())
                .putString("user_phone", phoneEditText.getText().toString().trim())
                .apply();

        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
        finish();
    }
}
