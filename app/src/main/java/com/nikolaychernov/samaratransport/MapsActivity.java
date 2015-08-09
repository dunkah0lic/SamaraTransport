package com.nikolaychernov.samaratransport;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends ActionBarActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    int KR_ID;
    GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
    Tracker tracker = analytics.newTracker("UA-60775707-2");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tracker.setScreenName("MapsActivity");
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("show")
                .setLabel("Map")
                .build());
        setContentView(R.layout.activity_maps);

        KR_ID = getIntent().getIntExtra("KR_ID",1);
        setUpMapIfNeeded();

        TransportDBContract contr = new TransportDBContract();
        TransportDBContract.RouteReaderDbHelper helper = contr.new RouteReaderDbHelper(this);
        ArrayList<Stop> stops = helper.getStopsForRoute(KR_ID);
        for (int i=0; i<stops.size(); i++){
            mMap.addMarker(new MarkerOptions().position(new LatLng(stops.get(i).latitude, stops.get(i).longitude)).title(stops.get(i).title));
            Log.d("ArrivalListAdapter", "btnOnClick stops " + stops.get(i).latitude + " " + stops.get(i).longitude);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(53.219404,50.198077), 11));

        TransportDBContract.MainReaderDbHelper mainReaderDbHelper = contr.new MainReaderDbHelper(this);
        Route route = mainReaderDbHelper.getRoute(KR_ID);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        //ab.setIcon(null);
        if (route.direction!=null){
            ab.setTitle(route.number + ": " + route.direction);
        }

        //ab.setSubtitle(st.direction);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setUpMapIfNeeded();

    }


        /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);

    }
}
