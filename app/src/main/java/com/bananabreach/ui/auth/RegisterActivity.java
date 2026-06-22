package com.bananabreach.ui.auth;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bananabreach.R;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Layout intentionally minimal — registration mocks straight to login
        // in this training build. Hook up activity_register.xml here for a
        // fuller flow if you extend the project.
        Toast.makeText(this, "Registration flow placeholder", Toast.LENGTH_SHORT).show();
        finish();
    }
}
