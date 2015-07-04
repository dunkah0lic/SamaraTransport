package com.nikolaychernov.samaratransport;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class Autostart extends BroadcastReceiver implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    public Autostart() {
    }

    GoogleApiClient mGoogleApiClient;
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        this.context = context;

        if (getBootup(context)) {
            //Intent intent1 = new Intent(context, BackgroundService.class);
            //context.startService(intent1);
            Log.i("Autostart", "started");

            mGoogleApiClient = new GoogleApiClient.Builder(MyApplication.getAppContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    public static boolean getBootup(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("backgroundFlag", false);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Intent intent = new Intent("LOCATION_UPDATE");
        PendingIntent locationIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);


        LocationRequest mLocationRequest = new LocationRequest();
        // TODO: set intervals to at least 30000 for release
        mLocationRequest.setInterval(60*60*1000);
        mLocationRequest.setFastestInterval(60*1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(getBootup(context)){
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, locationIntent);
            Log.i("Autostart", "requestLocationUpdates started");
        } else {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationIntent);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
