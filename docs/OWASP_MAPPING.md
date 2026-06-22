# OWASP Mobile Top 10 (2024) Mapping — BananaBreach

Cross-reference between BananaBreach's planted issues (see `VULNERABILITIES.md`) and the OWASP Mobile Top 10 categories.

| OWASP Category | Related issues (see VULNERABILITIES.md) |
|---|---|
| **M1: Improper Credential Usage** | #3 hardcoded admin backdoor, #4 plaintext password cache, #18 tokens in URL |
| **M2: Inadequate Supply Chain Security** | Not deliberately covered — out of scope for this build |
| **M3: Insecure Authentication/Authorization** | #1 client-side token check, #8 client-side admin gate, #9 hidden admin menu, #10 IDOR-shaped admin endpoint, #11 client-trusted role |
| **M4: Insufficient Input/Output Validation** | #23 unsafe URL loading, #26 weak deep link validation |
| **M5: Insecure Communication** | #16 cleartext HTTP, #17 verbose request/response logging, #18 tokens in URL |
| **M6: Inadequate Privacy Controls** | #12 plaintext PII in SharedPreferences, #28 PII in logs |
| **M7: Insufficient Binary Protections** | #31 debuggable build, #33 no obfuscation, #32 secrets in resources |
| **M8: Security Misconfiguration** | #24 exported service, #25 exported receiver, #27 broad FileProvider paths, #34 over-broad permission |
| **M9: Insecure Data Storage** | #2 plaintext session storage, #12 plaintext PII, #13 hardcoded AES key, #14 XOR "encryption", #15 MD5 hash alongside plaintext |
| **M10: Insufficient Cryptography** | #5 MD5 for credentials, #6 predictable token generation, #13 hardcoded key, #14 weak XOR |

## Notes

- WebView-specific issues (#20–#23) primarily fall under **M4** (input/output validation) and **M3** (since the JS bridge leaks auth material), depending on how a given assessment frames them.
- This mapping is meant as a teaching aid, not a substitute for a full MASVS-based assessment.
