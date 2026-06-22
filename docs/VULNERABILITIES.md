# Vulnerability Map — BananaBreach

This document is the internal reference for every vulnerability deliberately planted in BananaBreach. Use it to verify findings during a test, or as an answer key when grading a training exercise.

All severities are indicative (CVSS-style intuition, not a formal score) and assume the app is reachable by an attacker with a copy of the APK and/or a rooted test device.

## 1. Authentication

| # | Vulnerability | Location | Severity |
|---|---|---|---|
| 1 | Client-side token validation (length check only, no server round-trip) | `MainActivity.validateTokenLocally()` | High |
| 2 | Session token stored and trusted in plaintext | `SessionManager`, `LoginActivity` | High |
| 3 | Hardcoded admin backdoor credentials | `LoginActivity.attemptLogin()`, `strings.xml` | Critical |
| 4 | Plaintext password cached locally "temporarily" | `LoginActivity.performLogin()` | High |
| 5 | Weak password hash (MD5) used for local credential storage | `SecurityUtils.hashMD5()` | Medium |
| 6 | Predictable session token generation (timestamp + small random range) | `SecurityUtils.generateToken()` | Medium |
| 7 | Incomplete session teardown on logout (role/profile data survives) | `DashboardActivity.logout()` | High |

## 2. Authorization

| # | Vulnerability | Location | Severity |
|---|---|---|---|
| 8 | Admin-only screen gated purely by a local SharedPreferences flag | `DashboardActivity.checkAdminAccess()` | Critical |
| 9 | Admin menu item hidden via UI visibility, not access control | `DashboardActivity.setupNavigationDrawer()` | High |
| 10 | Admin user-list endpoint accepts a client-supplied token with no per-user ownership check (IDOR shape) | `AdminPanelActivity.loadAdminData()` | High |
| 11 | User role trusted from local storage rather than re-verified server-side | `SharedPreferences: user_role` | High |

## 3. Data Storage

| # | Vulnerability | Location | Severity |
|---|---|---|---|
| 12 | Sensitive profile/session data stored in plaintext SharedPreferences | Most activities | High |
| 13 | Hardcoded AES key shipped in the APK | `SecurityUtils.HARDCODED_KEY` | Critical |
| 14 | Trivial single-byte XOR used as "encryption" | `SecurityUtils.xorEncrypt()` | Low |
| 15 | MD5 hash stored alongside the plaintext value it was derived from | `LoginActivity`, `ProfileActivity` | Medium |

## 4. Network Security

| # | Vulnerability | Location | Severity |
|---|---|---|---|
| 16 | Cleartext HTTP allowed at the manifest and base-URL level | `AndroidManifest.xml`, `ApiClient` | High |
| 17 | Full request/response bodies logged at runtime (`Level.BODY`) | `ApiClient` | Medium |
| 18 | Auth/session tokens passed as URL query parameters | `ApiService` (`@Query` on login/profile/admin calls) | High |
| 19 | Hardcoded API key shipped in the client and sent on every request | `ApiClient.API_KEY` | Critical |

## 5. WebView

| # | Vulnerability | Location | Severity |
|---|---|---|---|
| 20 | JavaScript enabled with no content restrictions | `WebViewActivity.setupWebView()` | High |
| 21 | File and content access enabled in WebView settings | `WebViewActivity.setupWebView()` | High |
| 22 | JavaScript bridge exposes the live auth token and user ID to page script | `WebViewActivity.WebAppInterface` | Critical |
| 23 | URL to load taken directly from the launching Intent, no allow-list | `WebViewActivity.loadContent()` | High |

## 6. Android Components

| # | Vulnerability | Location | Severity |
|---|---|---|---|
| 24 | Exported background service with no caller verification | `BackgroundSyncService` (manifest) | Medium |
| 25 | Exported broadcast receiver bound to a device-admin intent | `DeviceAdminReceiver` (manifest) | Medium |
| 26 | Weak deep link validation — scheme/host trusted without further checks | `DeepLinkActivity`, `WebViewActivity` (manifest intent filters) | Medium |
| 27 | FileProvider configured with broad path access | `file_paths.xml` | Medium |

## 7. Logging

| # | Vulnerability | Location | Severity |
|---|---|---|---|
| 28 | Saved credentials and PII written to logcat | `LoginActivity.loadSavedCredentials()` | Medium |
| 29 | Verbose error bodies and stack traces surfaced to the user and logs | `LoginActivity.performLogin()` | Low |
| 30 | Debug-level HTTP logging enabled in release-adjacent build | `ApiClient` | Low |

## 8. Reverse Engineering Exposure

| # | Vulnerability | Location | Severity |
|---|---|---|---|
| 31 | `debuggable="true"` left enabled | `AndroidManifest.xml` | Low |
| 32 | Sensitive constants (admin email/password, debug key, salt) shipped in `strings.xml` | `res/values/strings.xml` | Medium |
| 33 | No code obfuscation/minification in either build type | `app/build.gradle` | Low |
| 34 | Over-broad `READ_LOGS` permission requested | `AndroidManifest.xml` | Medium |

---

## Suggested remediation patterns (for write-ups)

- Replace client-side token "validation" with a real server round-trip on every privileged action.
- Move all admin/role checks server-side; never trust a local boolean.
- Never put tokens or passwords in URLs — use request bodies/headers over TLS only.
- Replace MD5/XOR with vetted libraries (`Argon2`/`bcrypt` for passwords, AES-GCM with a securely stored key for data at rest).
- Strip or scope WebView JS bridges; never expose raw tokens to JS.
- Set `exported="false"` on any component that doesn't need to be reachable from outside the app, and validate deep link input strictly.
- Strip debug logging and `debuggable` from release builds; enable ProGuard/R8.
