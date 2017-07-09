package edu.cmu.chimps.friendship_study;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.github.privacystreams.accessibility.AccEvent;
import com.github.privacystreams.commons.comparison.Comparators;
import com.github.privacystreams.core.Callback;
import com.github.privacystreams.core.Item;
import com.github.privacystreams.core.UQI;
import com.github.privacystreams.core.purposes.Purpose;

import java.util.ArrayList;
import java.util.List;


// Survey service
// Load an Survey Url
// The content of the survey
// Friendship survey
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class QualtricsActivity extends AppCompatActivity {
    private boolean isRandomized = false;
    public UQI uqi;
    private final String endOfSurveyResourceID = "EndOfSurvey";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this)); // Set up the default exception handler for unexpected exception
        setContentView(R.layout.qualtrics);
        String surveyUrl = getIntent().getStringExtra(Constants.URL.KEY_SURVEY_URL);
        uqi=new UQI(this);
        WebView webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setAccessibilityDelegate(new View.AccessibilityDelegate());
        webView.loadUrl(surveyUrl);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("url",url);
                if(url.split("/")[url.split("/").length-1].contains(Constants.URL.DAILY_EMA_URL.split("=")[1])){
                    isRandomized = true;
                }
                view.loadUrl(url);

                return false;
            }
        });
        uqi.getData(AccEvent.asUpdates(), Purpose.LIB_INTERNAL("Event Triggers"))
                .filter(Comparators.eq(AccEvent.PACKAGE_NAME,getPackageName()))
                .forEach(new Callback<Item>(){
                    @Override
                    protected void onInput(Item input) {
                        AccessibilityNodeInfo rootView =
                                input.getValueByField(AccEvent.ROOT_NODE);
                        List<AccessibilityNodeInfo> list = preOrderTraverse(rootView);
                        if(list!=null&&!list.isEmpty()) {
                            for (AccessibilityNodeInfo node : list) {
                                if (node.getViewIdResourceName() != null) {
                                    if (node.getViewIdResourceName().equals(endOfSurveyResourceID)) {
                                        Toast.makeText(QualtricsActivity.this, "Thank you for your submission!",
                                                Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(isRandomized){
            Log.e("isRandomized","isRandomized");
            startActivity(new Intent(QualtricsActivity.this,MainActivity.class));
        }
    }
    public static List<AccessibilityNodeInfo> preOrderTraverse(AccessibilityNodeInfo root){
        if(root == null)
            return null;
        List<AccessibilityNodeInfo> list = new ArrayList<>();
        list.add(root);
        int childCount = root.getChildCount();
        for(int i = 0; i < childCount; i ++){
            AccessibilityNodeInfo node = root.getChild(i);
            if(node != null)
                list.addAll(preOrderTraverse(node));
        }
        return list;
    }
}
