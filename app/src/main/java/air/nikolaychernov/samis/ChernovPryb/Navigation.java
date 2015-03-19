package air.nikolaychernov.samis.ChernovPryb;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

public class Navigation {

    private boolean GPSStatus = false;
    private boolean netStatus = false;
    private Location lastBestLoc = null;
    private Location forceLoc = null;
    private LocationListener GPSListener;
    private LocationListener networkListener;
    private LocationListener forceNetworkListener;
    private LocationManager locationManager;
    private boolean locationChanged = true;
    private boolean firstUpdate = true;
    private StopSearchActivity activity;

    private static final int TIME_TRESHOLD = 1000 * 20;
    private static final int MOVE_TRESHOLD = 10;

    public Navigation(StopSearchActivity activity) {
        this.activity = activity;

        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        Location currentNetworkLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Location currentGPSLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        lastBestLoc = isBetterLocation(currentGPSLoc, currentNetworkLoc) ? currentGPSLoc : currentNetworkLoc;
        forceLoc = lastBestLoc;

        newNavListeners();
    }

    private void newNavListeners() {
        GPSListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                locationChanged = !equalsByCoord(location, lastBestLoc) || firstUpdate;
                firstUpdate = false;
                lastBestLoc = location;
                forceLoc = location;
                if (isLocationChanged()) {
                    activity.notifyMoved();
                }
            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub
                locationManager.removeUpdates(this);
                GPSStatus = false;
            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, MOVE_TRESHOLD, this);
                GPSStatus = true;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // TODO Auto-generated method stub
                GPSStatus = (status == LocationProvider.AVAILABLE) ? true : false;
            }

        };

        networkListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                forceLoc = location;
                if (!GPSStatus || isBetterLocation(location, lastBestLoc) || firstUpdate) {
                    locationChanged = !equalsByCoord(location, lastBestLoc) || firstUpdate;
                    firstUpdate = false;
                    lastBestLoc = location;
                    if (isLocationChanged()) {
                        activity.notifyMoved();
                    }
                }
            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub
                locationManager.removeUpdates(this);
                netStatus = false;
            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, MOVE_TRESHOLD, this);
                netStatus = true;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // TODO Auto-generated method stub
                netStatus = (status == LocationProvider.AVAILABLE);
            }

        };

        forceNetworkListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                forceLoc = location;
                if (isBetterLocation(forceLoc, lastBestLoc)) {
                    lastBestLoc = forceLoc;
                    if (!GPSStatus || firstUpdate) {
                        locationChanged = !equalsByCoord(location, lastBestLoc) || firstUpdate;
                        firstUpdate = false;
                        if (isLocationChanged()) {
                            activity.notifyMoved();
                        }
                    }
                }
            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub
                locationManager.removeUpdates(this);
                netStatus = false;
            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
                netStatus = true;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // TODO Auto-generated method stub
                netStatus = (status == LocationProvider.AVAILABLE);
            }
        };
    }

    public static boolean equalsByCoord(Location loc1, Location loc2) {
        try {
            return getDistBetween(loc1.getLatitude(), loc1.getLongitude(), loc2.getLatitude(), loc2.getLongitude()) < 10;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLocationChanged() {
        boolean tmp = locationChanged;
        locationChanged = false;
        return tmp;
    }

    public boolean isNavWorking() {
        return GPSStatus || netStatus;
    }

    public Location getBestLocation() {
        return lastBestLoc;
    }

    public double getDistTo(double latitude, double longitude, boolean force) {
        if (getBestLocation() == null) {
            return -1;
        }
        float[] res = new float[1];
        Location.distanceBetween(getLat(force), getLong(force), latitude, longitude, res);
        return res[0];
    }

    public static double getDistBetween(double latitude1, double longitude1, double latitude2, double longitude2) {
        float[] res = new float[1];
        Location.distanceBetween(latitude1, longitude1, latitude2, longitude2, res);
        return res[0];
    }

    public double getDistTo(Stop st, boolean force) {
        if (st != null) {
            return getDistTo(st.latitude, st.longitude, force);
        } else {
            return -1;
        }
    }

    public double getDirectionTo(double latitude, double longitude) {
        if (getBestLocation() == null) {
            return -1;
        }
        Location loc = new Location(getBestLocation());
        loc.setLatitude(latitude);
        loc.setLongitude(longitude);
        return getBestLocation().bearingTo(loc);
    }

    public void requestNewLocation(/* final boolean waitForResult */) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                final LocationManager locMan = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
                LocationProvider provider = locMan.getProvider(LocationManager.NETWORK_PROVIDER);
                LocationListener singeUpdateListener = new LocationListener() {
                    public void onLocationChanged(Location location) {
                        Log.d("requestNewLocation", "Single Location Update Received: " + location.getLatitude() + "," + location.getLongitude());
                        // synchronized (shared) {
                        // shared.notifyAll();
                        // }
                        locMan.removeUpdates(this);
                        lastBestLoc = location;
                        forceLoc = location;
                        activity.notifyMoved();
                    }

                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    public void onProviderEnabled(String provider) {
                    }

                    public void onProviderDisabled(String provider) {
                    }
                };
                if (provider != null) {
                    locMan.requestLocationUpdates(provider.getName(), 0, 0, singeUpdateListener, activity.getMainLooper());
                }
            }
        }).start();
//		if (waitForResult) {
//			synchronized (shared) {
//				try {
//					shared.wait();
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
    }

    public double getLong(boolean force) {
        if (force && forceLoc != null) {
            return forceLoc.getLongitude();
        }
        // if (force)
        // requestNewLocation();
        Location loc = getBestLocation();
        return loc != null ? loc.getLongitude() : -1;
    }

    public double getLat(boolean force) {
        if (force && forceLoc != null) {
            return forceLoc.getLatitude();
        }
        // if (force)
        // requestNewLocation();
        Location loc = getBestLocation();
        return loc != null ? loc.getLatitude() : -1;
    }

    public void navTerminate() {
        if (GPSListener != null) {
            locationManager.removeUpdates(GPSListener);
        }
        if (networkListener != null) {
            locationManager.removeUpdates(networkListener);
        }
        if (forceNetworkListener != null) {
            locationManager.removeUpdates(forceNetworkListener);
        }
        netStatus = false;
        GPSStatus = false;
    }

    public boolean navInit() {
        navTerminate();

        // BEGIN added 12.12.13 while trying to improve navigation
        // locationManager = (LocationManager) activity
        // .getSystemService(Context.LOCATION_SERVICE);
        // newNavListeners();
        // END
        GPSStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && (locationManager.getProvider(LocationManager.GPS_PROVIDER) != null);
        netStatus = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && (locationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null);

        Location currentNetworkLoc = null;
        try {
            if (netStatus) {
                currentNetworkLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        } catch (IllegalArgumentException e) {
            currentNetworkLoc = null;
        }
        Location currentGPSLoc = null;
        try {
            if (GPSStatus) {
                currentGPSLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        } catch (IllegalArgumentException e) {
            currentGPSLoc = null;
        }
        lastBestLoc = isBetterLocation(currentGPSLoc, currentNetworkLoc) ? currentGPSLoc : currentNetworkLoc;
        forceLoc = lastBestLoc;

        try {
            if (locationManager.getProvider(LocationManager.GPS_PROVIDER) != null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, MOVE_TRESHOLD * 5, GPSListener);
            }
        } catch (Exception e) {
            // activity.say("Ошибка инициализации GPS " +
            // e.getLocalizedMessage());
            Log.e("GPS", "GPS Error", e);
        }

        try {
            if (locationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, MOVE_TRESHOLD, networkListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, forceNetworkListener);
            }
        } catch (Exception e) {
            // activity.say("Ошибка навигации по сетям " +
            // e.getLocalizedMessage());
            Log.e("Network", "Network nav Error", e);
        }

        return GPSStatus || netStatus;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }
        if (location == null) {
            return false;
        }
        // if (getDistBetween(location.getLatitude(), location.getLongitude(),
        // currentBestLocation.getLatitude(),
        // currentBestLocation.getLongitude()) < moveTreshold)
        // return false;

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TIME_TRESHOLD;
        boolean isSignificantlyOlder = timeDelta < -TIME_TRESHOLD;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use
        // the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be
            // worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and
        // accuracy
        // if (isMoreAccurate) {
        // return true;
        // } else if (isNewer && !isLessAccurate) {
        // return true;
        // } else if (isNewer && !isSignificantlyLessAccurate
        // && isFromSameProvider) {
        // return true;
        // }

        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

}
