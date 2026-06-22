package com.bananabreach.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static SessionManager instance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    // Intentional: session state kept in plaintext SharedPreferences —
    // see docs/VULNERABILITIES.md (Data Storage)
    private static final String PREF_NAME = "banana_session";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_IS_ADMIN = "is_admin";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    public void createSession(String userId, String userName, String token, boolean isAdmin) {
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_USER_ROLE, isAdmin ? "admin" : "user");
        editor.putBoolean(KEY_IS_ADMIN, isAdmin);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();

        android.util.Log.i("SessionManager",
                "Created session for: " + userName + " (ID: " + userId + ")");
    }

    public String getAuthToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, null);
    }

    public String getUserRole() {
        return sharedPreferences.getString(KEY_USER_ROLE, "user");
    }

    public boolean isAdmin() {
        return sharedPreferences.getBoolean(KEY_IS_ADMIN, false);
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}
