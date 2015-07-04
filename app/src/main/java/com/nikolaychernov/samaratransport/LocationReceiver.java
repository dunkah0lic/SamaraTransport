package com.nikolaychernov.samaratransport;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

/**
 * Created by Nikolay on 04.07.2015.
 */
public class LocationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if (LocationResult.hasResult(intent)) {
            LocationResult result = LocationResult.extractResult(intent);
            Location location = result.getLastLocation();

            Intent intent1 = new Intent(context, ShortBackgroundService.class);
            intent1.putExtra("longitude", location.getLongitude());
            intent1.putExtra("latitude", location.getLatitude());
            context.startService(intent1);
            Log.i("LocationReceiver", "started");
        }

    }

    public static boolean getBootup(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("backgroundFlag", false);
    }
}
