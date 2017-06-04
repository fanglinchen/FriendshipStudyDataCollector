package edu.cmu.chimps.friendship_study;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import edu.cmu.chimps.friendship_study.pam.PAMActivity;
import edu.cmu.chimps.love_study.R;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class QualtricsActivity extends AppCompatActivity {
    private boolean isRandomized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qualtrics);
        String surveyUrl = getIntent().getStringExtra(Constants.URL.KEY_SURVEY_URL);

        WebView webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setAccessibilityDelegate(new View.AccessibilityDelegate());
        webView.loadUrl(surveyUrl);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.contains(Constants.URL.DAILY_EMA_URL)){
                    isRandomized = true;
                }
                view.loadUrl(url);

                return false;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(isRandomized)
            startActivity(new Intent(this,PAMActivity.class));
    }
}
