package com.nikolaychernov.samaratransport;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Nikolay on 27.02.2015.
 */
public class MyApplication extends Application {

    //gitlab test

    public static final String THEME_PREFERENCES= "theme_preferences";
    public static final String THEME = "theme";

    private static Context context;
    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    public static int getCurrentTheme() {
        return currentTheme;
    }

    public static void setCurrentTheme(int theme) {
        currentTheme = theme;
    }

    private static int currentTheme;


    public void onCreate(){
        super.onCreate();
        MyApplication.context = getApplicationContext();
        currentTheme = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE).getInt(THEME, R.style.AppTheme_DeepPurple);
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
    synchronized public Tracker getDefaultTracker() {
        if  (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            tracker = analytics.newTracker(R.xml.global_tracker);
            // Enable Advertising Features.
            tracker.enableAdvertisingIdCollection(true);
            tracker.enableExceptionReporting(true);
        }
        return tracker;
    }
}