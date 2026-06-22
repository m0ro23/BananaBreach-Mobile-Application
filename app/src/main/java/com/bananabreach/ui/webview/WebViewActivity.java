package com.bananabreach.ui.webview;

import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bananabreach.R;
import com.bananabreach.utils.SessionManager;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        sessionManager = SessionManager.getInstance(this);
        webView = findViewById(R.id.web_view);

        setupWebView();
        loadContent();
    }

    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();

        // Intentional: over-permissive WebView configuration —
        // see docs/VULNERABILITIES.md (WebView)
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // Intentional: bridges sensitive session data into the WebView's JS context
        webView.addJavascriptInterface(new WebAppInterface(), "Android");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("bananabreach:")) {
                    handleDeepLink(url);
                    return true;
                }
                return false;
            }
        });
    }

    private void loadContent() {
        // Intentional: URL taken directly from the launching Intent with no allow-list
        String url = getIntent().getStringExtra("url");

        if (url == null) {
            String htmlContent = getHtmlContent();
            webView.loadDataWithBaseURL(
                    "https://bananabreach.com/",
                    htmlContent,
                    "text/html",
                    "UTF-8",
                    null
            );
        } else {
            webView.loadUrl(url);
            android.util.Log.w("WebViewActivity", "Loading URL: " + url);
        }
    }

    private String getHtmlContent() {
        return "<html><body>" +
                "<h1>BananaBreach Mobile</h1>" +
                "<p>Welcome to our mobile services.</p>" +
                "<button onclick=\"Android.showToast('Loading...')\">" +
                "Click Me</button>" +
                "<script>" +
                "console.log('WebView loaded with user: " +
                sessionManager.getUserName() + "');" +
                "</script>" +
                "</body></html>";
    }

    private void handleDeepLink(String url) {
        android.util.Log.d("WebViewActivity", "Handling deep link: " + url);
        Toast.makeText(this, "Processing: " + url, Toast.LENGTH_SHORT).show();
    }

    // Intentional: exposes session/auth data to any script running in the WebView
    public class WebAppInterface {
        @JavascriptInterface
        public void showToast(String message) {
            Toast.makeText(WebViewActivity.this, message, Toast.LENGTH_SHORT).show();
            android.util.Log.d("WebViewInterface", "Toast called: " + message);
        }

        @JavascriptInterface
        public String getAuthToken() {
            return sessionManager.getAuthToken();
        }

        @JavascriptInterface
        public String getUserId() {
            return sessionManager.getUserId();
        }
    }
}
