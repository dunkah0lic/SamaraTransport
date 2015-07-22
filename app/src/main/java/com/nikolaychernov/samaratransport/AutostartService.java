package com.nikolaychernov.samaratransport;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class AutostartService extends Service  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    GoogleApiClient mGoogleApiClient = null;

    public AutostartService() {
    }

    @Override
    public void onStart(Intent intent1, int startid){
        mGoogleApiClient = new GoogleApiClient.Builder(MyApplication.getAppContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();



    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onConnected(Bundle bundle) {
        Intent intent = new Intent("LOCATION_UPDATE");
        PendingIntent locationIntent = PendingIntent.getBroadcast(MyApplication.getAppContext(), 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);


        LocationRequest mLocationRequest = new LocationRequest();
        // TODO: set intervals to at least 30000 for release
        mLocationRequest.setInterval(24 * 60 * 60 * 1000);
        mLocationRequest.setFastestInterval(60 * 1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, locationIntent);
        Log.i("Autostart", "requestLocationUpdates started");
        this.stopSelf();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
