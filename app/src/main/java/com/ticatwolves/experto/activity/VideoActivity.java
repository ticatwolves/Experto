package com.ticatwolves.experto.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ticatwolves.experto.R;

public class VideoActivity extends AppCompatActivity {

    WebView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        videoView = (WebView) findViewById(R.id.video_chat);
        videoView.setWebViewClient(new WebViewClient());
        videoView.getSettings().setJavaScriptEnabled(true);
        videoView.loadUrl("https://appr.tc/r/104561321");

    }
}
