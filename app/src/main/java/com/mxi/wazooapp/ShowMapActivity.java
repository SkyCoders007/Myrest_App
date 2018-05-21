package com.mxi.wazooapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ShowMapActivity extends Activity {

    WebView showMap;
    String mapUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);
        showMap = (WebView) findViewById(R.id.webView_map);

        mapUrl = getIntent().getExtras().getString("MAP_URL");
        Log.e("Dhaval ", "Map URL in show map screen = " + mapUrl);

        showMap.setWebViewClient(new WebViewClient());
        showMap.getSettings().setJavaScriptEnabled(true);
//        showMap.loadUrl("http://maps.google.com/maps?" + "saddr=43.0054446,-87.9678884" + "&daddr=42.9257104,-88.0508355");
        showMap.loadUrl(mapUrl);
    }
}
