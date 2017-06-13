package edu.cmu.chimps.friendship_study;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
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
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"mikelmq99@gmail.com"});
        email.putExtra(Intent.EXTRA_SUBJECT, "Friendship Study Crash Report");
        email.putExtra(Intent.EXTRA_TEXT, error);
        email.setType("message/rfc822");
        startActivityForResult(Intent.createChooser(email, "Choose an Email client for sending error report:"),1);
        Log.e("Service running"," "+ Utils.isTrackingEnabled(this));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Intent serviceIntent = new Intent(this, TrackingService.class);
        serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        PendingIntent pendingIntent = PendingIntent.getActivity(this.getBaseContext(), 0, serviceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager) this.getBaseContext().getSystemService(this.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, pendingIntent);
        this.startService(serviceIntent);
//        android.os.Process.killProcess(android.os.Process.myPid());
//        System.exit(10);
        finish();
    }
}
