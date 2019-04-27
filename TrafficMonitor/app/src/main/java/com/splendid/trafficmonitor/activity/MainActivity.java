package com.splendid.trafficmonitor.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.splendid.trafficmonitor.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String testUrl = "https://www.baidu.com/";
        TextView clickToLoad = findViewById(R.id.click_load);
        final WebView webView = findViewById(R.id.my_webview);
        clickToLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.loadUrl(testUrl);
            }
        });
    }
}
