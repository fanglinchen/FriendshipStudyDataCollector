package edu.cmu.chimps.friendship_study;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by fanglinchen on 3/24/17.
 */

public class MyApplication extends Application
{
    public void onCreate ()
    {
        super.onCreate();
//        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this)); // Set up the default exception handler for the
        // Setup handler for uncaught exceptions.
//        Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler()
//        {
//            @Override
//            public void uncaughtException (Thread thread, Throwable e)
//            {
//                handleUncaughtException (thread, e);
//            }
//        });
    }

//    public void handleUncaughtException (Thread thread, Throwable exception)
//    {
//        exception.printStackTrace(); // not all Android versions will print the stack trace automatically
//
//        Intent intent = new Intent ();
//        intent.setAction ("edu.cmu.chimps.friendship_study.SEND_LOG"); // see step 5.
//        intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
//        startActivity (intent);
//
//        System.exit(1); // kill off the crashed app
//
//    }
}