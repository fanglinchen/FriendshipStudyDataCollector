package edu.cmu.chimps.friendship_study;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by lenovo on 2017/6/12.
 */

public class ExceptionHandlerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String error = intent.getStringExtra("ERROR");
        Intent send = new Intent(Intent.ACTION_SENDTO);
        String uriText = "mailto:" + Uri.encode("mikelmq99@gmail.com") +
                "?subject=" + Uri.encode("Friendship Study Crash Report") +
                "&body=" + Uri.encode(error);
        Uri uri = Uri.parse(uriText);
        send.setData(uri);

        startActivityForResult(Intent.createChooser(send, "Friendship Activity has crashed. Please choose an Email client for sending error report:"),1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        finish();
    }
}
