package com.nikolaychernov.samaratransport;

import android.app.Application;
import android.content.Context;

/**
 * Created by Nikolay on 27.02.2015.
 */
public class MyApplication extends Application {

    private static Context context;

    public void onCreate(){
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}