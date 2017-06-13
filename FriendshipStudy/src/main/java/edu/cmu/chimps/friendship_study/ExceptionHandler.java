package edu.cmu.chimps.friendship_study;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This is the exception handler which would handles the unexpected
 */

public class ExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {
    private final Context myContext;
    private final String LINE_SEPARATOR = "\n";
    private static boolean happened = false;
    public ExceptionHandler(Context context) {
        myContext = context;
    }
    @Override
    public void uncaughtException(Thread thread, Throwable exception) {
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        StringBuilder errorReport = new StringBuilder();
        errorReport.append("************ CAUSE OF ERROR ************\n\n");
        errorReport.append(stackTrace.toString());

        errorReport.append("\n************ DEVICE INFORMATION ***********\n");
        errorReport.append("Brand: ");
        errorReport.append(Build.BRAND);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Device: ");
        errorReport.append(Build.DEVICE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Model: ");
        errorReport.append(Build.MODEL);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Id: ");
        errorReport.append(Build.ID);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Product: ");
        errorReport.append(Build.PRODUCT);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("\n************ FIRMWARE ************\n");
        errorReport.append("SDK: ");
        errorReport.append(Build.VERSION.SDK);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Release: ");
        errorReport.append(Build.VERSION.RELEASE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Incremental: ");
        errorReport.append(Build.VERSION.INCREMENTAL);
        errorReport.append(LINE_SEPARATOR);
        Log.e("Test","In exception handling ");

        String errors = errorReport.toString();
        Intent sendError = new Intent(myContext, ExceptionHandlerActivity.class);
        sendError.putExtra("ERROR", errors);
        myContext.startActivity(sendError);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }
}


//            Intent email = new Intent(Intent.ACTION_SEND);
//            email.putExtra(Intent.EXTRA_EMAIL, new String[]{"mikelmq99@gmail.com"});
//            email.putExtra(Intent.EXTRA_SUBJECT, "Friendship Study Crash Report");
//            email.putExtra(Intent.EXTRA_TEXT, errors);
//            email.setType("message/rfc822");
//            myContext.startActivity(Intent.createChooser(email, "Choose an Email client for sending error report:"));

//        myContext.startActivityForResult(Intent.createChooser(email, "Choose an Email client for sending error report:"),1);

//        Intent serviceIntent = new Intent(myContext,TrackingService.class);
//        serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
//        PendingIntent pendingIntent = PendingIntent.getActivity(myContext.getBaseContext(), 0, serviceIntent, PendingIntent.FLAG_ONE_SHOT);
//        AlarmManager mgr = (AlarmManager) myContext.getBaseContext().getSystemService(myContext.ALARM_SERVICE);
//        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, pendingIntent);
//        myContext.startService(serviceIntent);
