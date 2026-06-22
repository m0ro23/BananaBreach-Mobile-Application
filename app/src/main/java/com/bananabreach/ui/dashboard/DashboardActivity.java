package com.bananabreach.ui.dashboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bananabreach.R;
import com.bananabreach.data.models.Transaction;
import com.bananabreach.ui.admin.AdminPanelActivity;
import com.bananabreach.ui.profile.ProfileActivity;
import com.bananabreach.ui.settings.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private ViewPager2 viewPager;
    private RecyclerView transactionsRecyclerView;

    private SharedPreferences sharedPreferences;
    private TextView balanceTextView;
    private TextView accountNumberTextView;

    private List<Transaction> transactions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sharedPreferences = getSharedPreferences("banana_prefs", MODE_PRIVATE);

        initializeViews();
        setupToolbar();
        setupNavigationDrawer();
        setupBottomNavigation();
        loadUserData();
        loadTransactions();
        checkAdminAccess();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.view_pager);
        transactionsRecyclerView = findViewById(R.id.transactions_recycler_view);
        balanceTextView = findViewById(R.id.balance_text);
        accountNumberTextView = findViewById(R.id.account_number_text);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setTitle("Dashboard");
    }

    private void setupNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
            } else if (id == R.id.nav_transactions) {
                startActivity(new Intent(DashboardActivity.this, TransactionActivity.class));
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(DashboardActivity.this, SettingsActivity.class));
            } else if (id == R.id.nav_admin) {
                // Intentional: gate relies solely on a local SharedPreferences flag —
                // see docs/VULNERABILITIES.md (Authorization: client-side admin check)
                boolean isAdmin = sharedPreferences.getBoolean("is_admin", false);
                if (isAdmin) {
                    startActivity(new Intent(DashboardActivity.this, AdminPanelActivity.class));
                } else {
                    Toast.makeText(this, "Access restricted", Toast.LENGTH_SHORT).show();
                }
            } else if (id == R.id.nav_logout) {
                logout();
            }

            drawerLayout.closeDrawers();
            return true;
        });

        boolean isAdmin = sharedPreferences.getBoolean("is_admin", false);
        Menu menu = navigationView.getMenu();
        MenuItem adminItem = menu.findItem(R.id.nav_admin);
        if (adminItem != null && isAdmin) {
            adminItem.setVisible(true);
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.navigation_dashboard) {
                return true;
            } else if (id == R.id.navigation_transactions) {
                startActivity(new Intent(DashboardActivity.this, TransactionActivity.class));
                return true;
            } else if (id == R.id.navigation_payment) {
                startActivity(new Intent(DashboardActivity.this, PaymentActivity.class));
                return true;
            } else if (id == R.id.navigation_profile) {
                startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
                return true;
            }

            return false;
        });
    }

    private void loadUserData() {
        String userName = sharedPreferences.getString("user_name", "User");
        String userEmail = sharedPreferences.getString("user_email", "user@example.com");
        String accountNumber = "BB" + System.currentTimeMillis() % 10000000000L;
        double balance = 2456.78;

        TextView userNameTextView = findViewById(R.id.user_name_text);
        if (userNameTextView != null) {
            userNameTextView.setText("Welcome, " + userName);
        }

        accountNumberTextView.setText("Account: " + accountNumber);
        balanceTextView.setText("$" + String.format("%.2f", balance));

        sharedPreferences.edit().putString("account_number", accountNumber).apply();
    }

    private void loadTransactions() {
        transactions.add(new Transaction("TXN001", "Coffee Shop", -4.50, "2024-01-15 09:30", "Completed"));
        transactions.add(new Transaction("TXN002", "Salary Deposit", 2500.00, "2024-01-15 10:00", "Completed"));
        transactions.add(new Transaction("TXN003", "Online Store", -45.20, "2024-01-14 14:20", "Pending"));
        transactions.add(new Transaction("TXN004", "Bank Transfer", -120.00, "2024-01-14 11:10", "Completed"));
        transactions.add(new Transaction("TXN005", "Mobile Recharge", -10.00, "2024-01-13 16:45", "Failed"));

        // Adapter wiring omitted for brevity — see TransactionActivity for the full list view.
    }

    private void checkAdminAccess() {
        boolean isAdmin = sharedPreferences.getBoolean("is_admin", false);
        if (isAdmin) {
            getSupportActionBar().setSubtitle("Admin Mode");

            android.util.Log.w("DashboardActivity",
                    "Admin access granted to user: " +
                            sharedPreferences.getString("user_name", "unknown"));
        }
    }

    private void logout() {
        // Intentional: incomplete session teardown — see docs/VULNERABILITIES.md
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("auth_token");
        editor.remove("is_logged_in");
        editor.apply();

        Intent intent = new Intent(DashboardActivity.this, com.bananabreach.ui.auth.LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        if (!isLoggedIn) {
            logout();
        }
    }
}
