#  BananaBreach

<p align="center">
  <img src="BananaBreach%20App%20Interface.png" alt="BananaBreach App Interface" width="300">
</p>

**An intentionally vulnerable Android application built for mobile security education and penetration testing practice.**

BananaBreach simulates a realistic fintech/banking app. It looks and behaves like a production product login, dashboard, transactions, profile, settings but it deliberately contains common real-world Android security flaws so they can be discovered, exploited, and fixed in a safe, legal environment.

> 

---

## Why this exists

Most Android pentesting labs (DVHMA, InsecureBankv2, DIVA, etc.) are great for learning individual bugs, but don't always feel like a "real" app you'd find in the wild. BananaBreach was built to bridge that gap: a believable UI/UX wrapped around a deliberately flawed implementation, so testers can practice:

- Static analysis / reverse engineering (APK decompilation, string/secret hunting)
- Dynamic analysis (Frida, traffic interception, runtime hooking)
- Manual vulnerability discovery using OWASP MASVS / Mobile Top 10 as a checklist
- Reporting and remediation write-ups


## Tech stack

- Java, Android SDK (minSdk 23 / targetSdk 33)
- Retrofit + OkHttp for networking
- Room for local storage
- Standard Android Views (no Jetpack Compose)

## Project structure

```
BananaBreach/
├── app/
│   └── src/main/
│       ├── java/com/bananabreach/
│       │   ├── ui/            # auth, dashboard, profile, settings, admin, webview, deeplink
│       │   ├── data/          # models, repository, database
│       │   ├── network/       # Retrofit client + API interface
│       │   ├── security/      # crypto / hashing helpers (intentionally weak — see docs)
│       │   ├── utils/         # session management, constants
│       │   ├── receivers/
│       │   └── services/
│       └── res/               # layouts, drawables, values, menu, xml
└── docs/
    ├── VULNERABILITIES.md     # full vulnerability map (internal reference)
    └── OWASP_MAPPING.md       # OWASP Mobile Top 10 cross-reference
```

## Getting started

1. Clone the repo and open it in Android Studio.
2. Let Gradle sync.
3. Build → Build APK(s), or run directly on an emulator/test device.
4. See `docs/VULNERABILITIES.md` for a full list of planted issues and where to find them.

## Test accounts

| Role  | Email                     | Password   |
|-------|---------------------------|------------|
| Admin | admin@bananabreach.com    | Admin123!  |
| User  | Register via the app, or use any email/password — auth is mocked |

These credentials are hardcoded **on purpose** as part of the vulnerability set (see `docs/VULNERABILITIES.md`, item: hardcoded credentials).

