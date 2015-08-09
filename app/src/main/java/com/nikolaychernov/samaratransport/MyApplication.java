package com.nikolaychernov.samaratransport;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Nikolay on 27.02.2015.
 */
public class MyApplication extends Application {

    private static Context context;
    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    public void onCreate(){
        super.onCreate();
        MyApplication.context = getApplicationContext();
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker("UA-X60775707-2");
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}