package edu.cmu.chimps.friendship_study;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dropbox.core.DbxWebAuth;
import com.github.privacystreams.accessibility.BaseAccessibilityEvent;
import com.github.privacystreams.commons.comparison.Comparators;
import com.github.privacystreams.core.Callback;
import com.github.privacystreams.core.Item;
import com.github.privacystreams.core.UQI;
import com.github.privacystreams.core.purposes.Purpose;
import com.github.privacystreams.utils.AccessibilityUtils;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.chimps.friendship_study.pam.PAMActivity;


// Survey service
// Load an Survey Url
// The content of the survey
// Friendship survey
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class QualtricsActivity extends AppCompatActivity {
    private boolean isRandomized = false;
    public UQI uqi;
    private final String endOfSurveyResrouceID = "EndOfSurvey";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this)); // Set up the default exception handler for the

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
                if(url.contains(Constants.URL.DAILY_EMA_URL)){
                    isRandomized = true;
                }
                view.loadUrl(url);

                return false;
            }
        });
        uqi.getData(BaseAccessibilityEvent.asUpdates(), Purpose.LIB_INTERNAL("Event Triggers"))
                .filter(Comparators.eq(BaseAccessibilityEvent.PACKAGE_NAME,getPackageName()))
                .forEach(new Callback<Item>(){
                    @Override
                    protected void onInput(Item input) {
                        AccessibilityNodeInfo rootView =
                                input.getValueByField(BaseAccessibilityEvent.ROOT_VIEW);
                        List<AccessibilityNodeInfo> list = preOrderTraverse(rootView);
                        if(!list.isEmpty()) {
                            for (AccessibilityNodeInfo node : list) {
                                if (node.getViewIdResourceName() != null) {
                                    if (node.getViewIdResourceName().equals(endOfSurveyResrouceID)) {
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
        if(isRandomized)
            startActivity(new Intent(this,PAMActivity.class));
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
