package edu.cmu.chimps.friendship_study;

import android.app.Application;
import android.content.Context;


public class MyApplication extends Application
{
    private static Application sApplication;

    public static Application getApplication() {
        return sApplication;
    }

    public static Context getContext() {
        return getApplication().getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
    }
}