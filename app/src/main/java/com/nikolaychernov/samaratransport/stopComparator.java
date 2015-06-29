package com.nikolaychernov.samaratransport;

import android.location.Location;

import java.util.Comparator;

/**
 * Created by Nikolay on 28.06.2015.
 */
public class stopComparator implements Comparator<Stop> {

    private Location userLocation;

    public stopComparator(Location location){
        userLocation = location;
    }

    @Override
    public int compare(Stop stop1, Stop stop2) {
        Location location1 = new Location("");
        location1.setLatitude(stop1.latitude);
        location1.setLongitude(stop1.longitude);
        double distance1 = userLocation.distanceTo(location1);

        Location location2 = new Location("");
        location2.setLatitude(stop2.latitude);
        location2.setLongitude(stop2.longitude);
        double distance2 = userLocation.distanceTo(location2);

        return Double.compare(distance1,distance2);
    }
}
