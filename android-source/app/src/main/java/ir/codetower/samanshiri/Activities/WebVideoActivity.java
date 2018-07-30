package ir.codetower.samanshiri.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import ir.codetower.samanshiri.R;

public class WebVideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_video);
        WebView webView= (WebView) findViewById(R.id.video_web);
        webView.getSettings().setJavaScriptEnabled(true);
//        webView.loadData(videoEmbedHtml, "text/html", "utf-8");
        webView.loadUrl("http://www.codetower.ir/api/Catalog/v1/video");
    }
}
