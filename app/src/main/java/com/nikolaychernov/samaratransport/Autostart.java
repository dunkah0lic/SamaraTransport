package com.nikolaychernov.samaratransport;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

public class Autostart extends BroadcastReceiver {
    public Autostart() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.


        if (getBootup(context)) {
            Intent intent1 = new Intent(context, BackgroundService.class);
            context.startService(intent1);
            Log.i("Autostart", "started");
        }
    }

    public static boolean getBootup(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("backgroundFlag", false);
    }
}
