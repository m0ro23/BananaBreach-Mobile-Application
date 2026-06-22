package com.bananabreach.ui.admin;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bananabreach.R;
import com.bananabreach.data.models.User;
import com.bananabreach.network.ApiClient;
import com.bananabreach.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPanelActivity extends AppCompatActivity {

    private TextView adminTitleTextView;
    private TextView statsTextView;
    private ListView usersListView;
    private Button refreshButton;
    private Button exportDataButton;
    private Button systemLogsButton;

    private ApiService apiService;
    private SharedPreferences sharedPreferences;
    private List<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        apiService = ApiClient.getInstance().getApiService();
        sharedPreferences = getSharedPreferences("banana_prefs", MODE_PRIVATE);

        initializeViews();
        setupClickListeners();
        loadAdminData();
    }

    private void initializeViews() {
        adminTitleTextView = findViewById(R.id.admin_title);
        statsTextView = findViewById(R.id.stats_text);
        usersListView = findViewById(R.id.users_list);
        refreshButton = findViewById(R.id.refresh_button);
        exportDataButton = findViewById(R.id.export_data_button);
        systemLogsButton = findViewById(R.id.system_logs_button);

        String adminName = sharedPreferences.getString("user_name", "Admin");
        adminTitleTextView.setText("Admin Panel - " + adminName);
    }

    private void setupClickListeners() {
        refreshButton.setOnClickListener(v -> loadAdminData());

        exportDataButton.setOnClickListener(v -> {
            Toast.makeText(this, "Exporting user data...", Toast.LENGTH_SHORT).show();

            android.util.Log.w("AdminPanel",
                    "Exporting user data by admin: " +
                            sharedPreferences.getString("user_name", "unknown"));

            showExportDialog();
        });

        systemLogsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Loading system logs...", Toast.LENGTH_SHORT).show();
            loadSystemLogs();
        });
    }

    private void loadAdminData() {
        String adminToken = sharedPreferences.getString("auth_token", "");

        // Intentional: server trusts a client-supplied token param for an admin-only
        // list endpoint, with no per-resource ownership check — classic IDOR shape.
        Call<List<User>> call = apiService.getUsers(adminToken);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList = response.body();
                    updateUsersList();
                    updateStats();
                } else {
                    Toast.makeText(AdminPanelActivity.this,
                            "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(AdminPanelActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUsersList() {
        // Adapter wiring omitted for brevity. In the full vulnerability scenario this
        // view renders other users' PII pulled from the admin endpoint above.
    }

    private void updateStats() {
        String stats = "Total Users: " + userList.size() +
                "\nActive Sessions: " + userList.size() * 2 +
                "\nSystem Status: Online";
        statsTextView.setText(stats);
    }

    private void showExportDialog() {
        // Export flow omitted for brevity — see docs/VULNERABILITIES.md for the
        // intended behavior (writes to external storage with no permission check).
    }

    private void loadSystemLogs() {
        String adminToken = sharedPreferences.getString("auth_token", "");

        Call<String> call = apiService.getSystemLogs(adminToken);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showLogDialog(response.body());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(AdminPanelActivity.this,
                        "Failed to load logs", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLogDialog(String logs) {
        // Dialog rendering omitted for brevity — logs may contain sensitive
        // operational details, see docs/VULNERABILITIES.md (Logging).
    }
}
