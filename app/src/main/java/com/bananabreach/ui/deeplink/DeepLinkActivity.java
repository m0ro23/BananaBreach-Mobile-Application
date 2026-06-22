package com.bananabreach.ui.deeplink;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Handles bananabreach://deeplink/user/{id} links.
 *
 * Intentional: the user id segment is trusted directly from the incoming URI
 * and used to look up/display data with no server-side ownership check —
 * see docs/VULNERABILITIES.md (Android Components: weak deep link validation).
 */
public class DeepLinkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();
        if (data != null) {
            String userId = data.getLastPathSegment();

            android.util.Log.d("DeepLinkActivity", "Deep link opened for user: " + userId);
            Toast.makeText(this, "Opening profile: " + userId, Toast.LENGTH_SHORT).show();

            // In the full scenario this would navigate straight to that user's
            // profile/transactions without verifying the current session owns it.
        }

        finish();
    }
}
