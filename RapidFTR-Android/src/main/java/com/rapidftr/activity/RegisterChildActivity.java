package com.rapidftr.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.rapidftr.R;
import com.rapidftr.javascript.DatabaseInterface;

public class RegisterChildActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_child);

        WebView webview = (WebView) findViewById(R.id.web_view);

        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        webview.addJavascriptInterface(new DatabaseInterface(this),"DBInterface");

        webview.setWebViewClient(new WebViewClient());

        webview.loadUrl("file:///android_asset/child_registration_form.html");
    }
}

