package com.example.healthymind.ui.all;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

import com.example.healthymind.R;

public class Resource1WebView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources_web_view);

        WebView mywebview = (WebView) findViewById(R.id.webView);
        mywebview.loadUrl("http://www.befrienderskenya.org/depression.aspx");
    }
}