package com.nikolaychernov.samaratransport;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.nikolaychernov.samaratransport.TransportDBContract.FavorReaderDbHelper;
import com.nikolaychernov.samaratransport.TransportDBContract.MainReaderDbHelper;
import com.nikolaychernov.samaratransport.TransportDBContract.StopEntry;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class DataController implements Serializable, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static volatile DataController instance;

    private static final String GOOGLE_PLAY = "https://play.google.com/store/apps/details?id=";
    private static final String GOOGLE_PLAY_PACKAGE_NAME = "com.android.vending";

    private StopSearchActivity activity;
    private MainReaderDbHelper mainDBhelper;
    private FavorReaderDbHelper favorBDhelper;
    private Navigation nav;
    private Context context;
    GoogleApiClient mGoogleApiClient;

    private int searchRadius = 1000;

    // private static final String KR_ID_START =
    // "/katalog_marshrutov/detail.php?KR_ID=";

    public static DataController getInstance(StopSearchActivity act) {
        if (instance == null) {
            synchronized (DataController.class) {
                instance = new DataController(act);
            }
        } else {
            instance.activity = act;
            TransportDBContract contr = new TransportDBContract();

            instance.mainDBhelper = contr.new MainReaderDbHelper(act);
            instance.favorBDhelper = contr.new FavorReaderDbHelper(act);
            instance.copyFavor();

            instance.nav = Navigation.getInstance(act);
        }
        return instance;
    }

    public static DataController getInstance(Context cont) {
        if (instance == null) {
            synchronized (DataController.class) {
                instance = new DataController(cont);
            }
        } else {
            instance.context= cont;
            TransportDBContract contr = new TransportDBContract();

            instance.mainDBhelper = contr.new MainReaderDbHelper(cont);
            instance.favorBDhelper = contr.new FavorReaderDbHelper(cont);
            instance.copyFavor();

            instance.nav = Navigation.getInstance(cont);
        }
        return instance;
    }

    public static DataController getInstance() {
        if (instance == null) {
            throw new NullPointerException("DataMan not initialized");
        }

        return instance;
    }

    public static void setInstance(DataController dm) {
        if (instance == null) {
            instance = dm;
        }
    }

    private DataController(StopSearchActivity act) {
        activity = act;
        context = act;
        TransportDBContract contr = new TransportDBContract();
        mainDBhelper = contr.new MainReaderDbHelper(act);
        favorBDhelper = contr.new FavorReaderDbHelper(act);
        nav = Navigation.getInstance(act);
        copyFavor();
        initSettings();

        mGoogleApiClient = new GoogleApiClient.Builder(MyApplication.getAppContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    private DataController(Context cont) {
        context = cont;
        TransportDBContract contr = new TransportDBContract();
        mainDBhelper = contr.new MainReaderDbHelper(cont);
        favorBDhelper = contr.new FavorReaderDbHelper(cont);
        nav = Navigation.getInstance(cont);
        copyFavor();
        initSettings();

        mGoogleApiClient = new GoogleApiClient.Builder(MyApplication.getAppContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    public void setBackgroundUpdatesActivated(boolean yes){

        Intent intent = new Intent("LOCATION_UPDATE");
        PendingIntent locationIntent = PendingIntent.getBroadcast(MyApplication.getAppContext(), 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);


        LocationRequest mLocationRequest = new LocationRequest();
        // TODO: set intervals to at least 30000 for release
        mLocationRequest.setInterval(24 * 60 * 60 * 1000);
        mLocationRequest.setFastestInterval(60 * 1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(yes){
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, locationIntent);
        } else {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationIntent);
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(0);
        }
    }

    private void initSettings() {
        SharedPreferences prefs;
        if (activity!=null){
            prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        } else {
            prefs = PreferenceManager.getDefaultSharedPreferences(context);
        }
        searchRadius = Integer.parseInt(prefs.getString("searchRadius", "1000"));
    }

    public void setRadius(String radius) {
        searchRadius = Integer.parseInt(radius);
    }

    public void setFavor(int KS_ID, boolean isFavor) {
        favorBDhelper.setFavor(KS_ID, isFavor);
    }

    public void setFavor(int[] KS_IDs, boolean isFavor) {
        favorBDhelper.setFavor(KS_IDs, isFavor);
    }

    public boolean isInFavor(int KS_ID) {
        return favorBDhelper.isInFavor(KS_ID);
    }

    public boolean isInFavor(int[] KS_IDs) {
        return favorBDhelper.isInFavor(KS_IDs);
    }

    public int getTransType(int KR_ID) { //0 = commercial, 1 = bus, 3 = tram, 4 = troll
        Route route = getRoute(KR_ID);

        if (route == null) {
            return 1;
        }
        if (route.affiliationID == 3) {
            return 0;
        }
        if (route.affiliationID > 3) {
            return 1;
        }
        return route.transportTypeID - route.affiliationID + 1;
    }

    public boolean isLocationChanged() {
        if (nav == null) {
            nav = Navigation.getInstance(activity);
        }
        return nav.isLocationChanged();
    }

    public boolean isNavWorking() {
        if (nav == null) {
            nav = Navigation.getInstance(activity);
        }
        return nav.isNavWorking();
    }

    public Location getBestLocation() {
        if (nav == null) {
            nav = Navigation.getInstance(activity);
        }
        return nav.getBestLocation();
    }

    public double getDistTo(Stop st, boolean force) {
        if (nav == null) {
            nav = Navigation.getInstance(activity);
        }
        return nav.getDistTo(st, force);
    }

    public double getDistTo(double latitude, double longitude, boolean force) {
        if (nav == null) {
            nav = Navigation.getInstance(activity);
        }
        return nav.getDistTo(latitude, longitude, force);
    }

    public double getLong(boolean force) {
        if (nav == null) {
            nav = Navigation.getInstance(activity);
        }
        return nav.getLong(force);
    }

    public double getLat(boolean force) {
        if (nav == null) {
            nav = Navigation.getInstance(activity);
        }
        return nav.getLat(force);
    }

    public void requestNewLocation(/* boolean waitForResult */) {
        // nav.requestNewLocation(waitForResult);
        nav.requestNewLocation();
    }

    public boolean navInit() {
        if (nav == null) {
            nav = Navigation.getInstance(activity);
        }
        return nav.navInit();
    }

    public void navTerminate() {
        if (nav == null) {
            nav = Navigation.getInstance(activity);
        }
        nav.navTerminate();
    }

    private static ArrayList<ArrivalInfo> parseMapBubbleArrivalInfo(String data) {
        long time = System.nanoTime();
        ArrayList<ArrivalInfo> result = new ArrayList<ArrivalInfo>();
        ArrivalInfo tmp;
        String patternBegin = "Arrival to this stop forecast</a></u></font>";
        int patternPrefixLength = 10;
        String patternEnd = "]]></text>";
        int start = data.indexOf(patternBegin) + patternBegin.length() + patternPrefixLength;
        int end = data.indexOf(patternEnd) - 1;
        if (start > end) {
            return result;
        }
        data = data.substring(start, end);

        String[] strs = data.split("\n");
        String[] words;
        for (int i = 0; i < strs.length; i++) {
            words = strs[i].split(" ");
            tmp = new ArrivalInfo();
            if (words[0].equals("Автобус")) {
                tmp.typeID = 1;
            }
            if (words[0].equals("Трамвай")) {
                tmp.typeID = 3;
            }
            if (words[0].equals("Троллейбус")) {
                tmp.typeID = 4;
            }
            tmp.routeDesc = words[2];
            if (words.length > 7) {
                tmp.routeDesc += " " + words[3];
            }
            tmp.time = Integer.parseInt(words[words.length - 2]);
            result.add(tmp);
        }

        time = System.nanoTime() - time;
        Log.i("TimeOfParseMapBubble", String.valueOf(time / 1E9));
        return result;
    }

    // main function
    public ArrayList<ArrivalInfo> getArrivalInfo(int KS_ID) throws NotFoundException, IOException {
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            Set<Integer> transTypesToShow = new HashSet<Integer>();
            if (sharedPref.getBoolean("showBuses", true)) {
                transTypesToShow.add(1);
            }
            if (sharedPref.getBoolean("showComm", true)) {
                transTypesToShow.add(0);
            }
            if (sharedPref.getBoolean("showTrams", true)) {
                transTypesToShow.add(3);
            }
            if (sharedPref.getBoolean("showTrolls", true)) {
                transTypesToShow.add(4);
            }
            //String info = getStopRouteCorrespondenceXml();
            //mainDBhelper.updateStops(new StopXmlParser().parse(getStopsXml()));
            return (
                    new ArrivalXmlParser(transTypesToShow)).parse(loadArrivalDataAPI(KS_ID));
//            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void updateStopsFromXml()  throws Exception {
        mainDBhelper.updateStops(new StopXmlParser().parse(getStopsXml()));
    }

    // API based
    public static ArrayList<ArrivalInfo> getRouteArrivalAPI(int KS_ID, int KR_ID, String who) throws NotFoundException, IOException, XmlPullParserException {
        Set<Integer> transTypesToShow = new HashSet<Integer>();
        transTypesToShow.add(0);
        transTypesToShow.add(1);
        transTypesToShow.add(3);
        transTypesToShow.add(4);
        ArrayList<ArrivalInfo> result = (new ArrivalXmlParser(transTypesToShow)).parse(loadRouteArrivalDataAPI(KS_ID, KR_ID));
        //String desc = DataController.getInstance().getRoute(KR_ID).direction;
        for (ArrivalInfo a : result) {
            a.setKR_ID(KR_ID);
            //a.routeDesc += ": " + desc;
        }
        return result;
    }

    public static String readStreamToString(InputStream in, String encoding) throws IOException {
        StringBuilder b = new StringBuilder();
        InputStreamReader r = new InputStreamReader(in, encoding);
        char[] buf = new char[1000];
        int c;
        while ((c = r.read(buf)) > 0) {
            b.append(buf, 0, c);
        }
        return b.toString();
    }

    public static String SHA1(String convertme) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return byteArray2Hex(md.digest(convertme.getBytes()));
    }

    private static String byteArray2Hex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }


    // API based
    public static String loadArrivalDataAPI(int KS_ID) throws IOException {
        return sendRequest("<request><method>getFirstArrivalToStop</method><parameters><KS_ID>" + KS_ID + "</KS_ID><COUNT>100</COUNT></parameters></request>");

    }

    // API based
    public static String loadRouteArrivalDataAPI(int KS_ID, int KR_ID) throws IOException {
        return sendRequest("<request><method>getRouteArrivalToStop</method><parameters><KS_ID>" + KS_ID + "</KS_ID><KR_ID>" + KR_ID + "</KR_ID></parameters></request>");

    }

    public static String sendRequest(String message) throws IOException {
        String resultString = new String("");
        try {
            URLConnection connection = null;
            URL url = new URL("http://tosamara.ru/api/xml");

            connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("POST");

            String body = "message=" + message + "&os=Android&clientId=40inchv&authKey=" + SHA1(message + "yPiRbhD");

            httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpConnection.setRequestProperty("Content-Length", String.valueOf(body.length()));

            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);
            httpConnection.connect();

            OutputStream os = httpConnection.getOutputStream();
            os.write(body.getBytes());

            os.flush();
            os.close();

            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = httpConnection.getInputStream();
                resultString = readStreamToString(in, "UTF-8");
                in.close();
            } else {
                throw new IOException("ServerNotRespond");
            }
            httpConnection.disconnect();
        } catch (MalformedURLException e) {
            Log.d("DataController", "MalformedURLException");
            throw new IOException("MalformedURLException " + e.getMessage());
        } catch (SocketTimeoutException e) {
            Log.d("DataController", "SocketTimeoutException");
            throw new IOException("Timeout");
        }

        return resultString;

    }

    public static String getXml(String link) throws IOException {
        String resultString = new String("");
        try {
            URLConnection connection = null;
            URL url = new URL(link);
            connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("GET");
            httpConnection.setDoInput(true);
            httpConnection.connect();

            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = httpConnection.getInputStream();
                resultString = readStreamToString(in, "UTF-8");
                in.close();
            } else {
                throw new IOException("ServerNotRespond");
            }
            httpConnection.disconnect();
        } catch (MalformedURLException e) {
            Log.d("DataController", "MalformedURLException");
            throw new IOException("MalformedURLException " + e.getMessage());
        } catch (SocketTimeoutException e) {
            Log.d("DataController", "SocketTimeoutException");
            throw new IOException("Timeout");
        }
        return resultString;
    }

    public static String getStopsXml() throws IOException {
        return getXml("http://tosamara.ru/api/classifiers/stopsFullDB.xml");
    }

    public static String getRoutesXml() throws IOException {
        return getXml("http://tosamara.ru/api/classifiers/routes.xml");
    }

    public static String getStopRouteCorrespondenceXml() throws IOException {
        return getXml("http://tosamara.ru/api/classifiers/routesAndStopsCorrespondence.xml");
    }

    public Stop[] searchByName(String name, boolean isFavorOnly) {
        if (isFavorOnly) {
            return mainDBhelper.searchByName(name, favorBDhelper.getFavorSet());
        }
        return mainDBhelper.searchByName(name);
    }

    public Stop[] getFavor() {
        int[] KS_IDs = favorBDhelper.getFavor();
        return mainDBhelper.getStops(KS_IDs);
    }

    public Stop[] searchNearMe(boolean isFavorOnly, boolean force) {
        if (getLat(force) < 0 || getLong(force) < 0) {
            return new Stop[0];
        }
        if (force) {
            requestNewLocation();
            return null;
        }
        if (isFavorOnly) {
            return mainDBhelper.searchNearMe(getLat(force), getLong(force), searchRadius, favorBDhelper.getFavorSet());
        }
        return mainDBhelper.searchNearMe(getLat(force), getLong(force), searchRadius);
    }

    public static StopGroup[] mergeStops(Stop[] src, boolean sortByDist, boolean force) {
        if (src == null || src.length <= 0) {
            return null;
        }
        Arrays.sort(src);
        ArrayList<StopGroup> result = new ArrayList<StopGroup>();
        ArrayList<Stop> tmp = new ArrayList<Stop>();
        for (int i = 0; i < src.length - 1; i++) {
            tmp.add(src[i]);
            if (!src[i].adjacentStreet.equalsIgnoreCase(src[i + 1].adjacentStreet) || !src[i].title.equalsIgnoreCase(src[i + 1].title)) {
                result.add(stopsToGroup(tmp));
                tmp = new ArrayList<>();
            }
        }

        tmp.add(src[src.length - 1]);
        result.add(stopsToGroup(tmp));

        StopGroup[] res = result.toArray(new StopGroup[0]);
        if (sortByDist) {
            Arrays.sort(res);

        }
        return res;
    }

    private static StopGroup stopsToGroup(ArrayList<Stop> src) {
        StopGroup result = new StopGroup();
        Set<Integer> IDs = new TreeSet<Integer>();
        result.latitude = 0;
        result.longitude = 0;
        Stop st;
        for (int i = 0; i < src.size(); i++) {
            st = src.get(i);
            IDs.add(st.KS_ID);
            result.busesMunicipal += st.busesMunicipal + ", ";
            result.busesCommercial += st.busesCommercial + ", ";
            result.busesPrigorod += st.busesPrigorod + ", ";
            result.busesSeason += st.busesSeason + ", ";
            result.busesSpecial += st.busesSpecial + ", ";
            result.trams += st.trams + ", ";
            result.trolleybuses += st.trolleybuses + ", ";
            result.metros += st.metros + ", ";
        }

        DataController dm = DataController.getInstance();
        result.latitude = src.get(0).latitude;
        result.longitude = src.get(0).longitude;
        result.dist = dm.getDistTo(result.latitude, result.longitude, true);
        result.KS_IDs = new int[IDs.size()];
        int k = 0;
        for (Integer i : IDs) {
            result.KS_IDs[k++] = i;
        }
        result.busesMunicipal = mergeRouteStrings(result.busesMunicipal);
        result.busesCommercial = mergeRouteStrings(result.busesCommercial);
        result.busesPrigorod = mergeRouteStrings(result.busesPrigorod);
        result.busesSeason = mergeRouteStrings(result.busesSeason);
        result.busesSpecial = mergeRouteStrings(result.busesSpecial);
        result.trams = mergeRouteStrings(result.trams);
        result.trolleybuses = mergeRouteStrings(result.trolleybuses);
        result.metros = mergeRouteStrings(result.metros);

        result.title = src.get(0).title;
        result.titleLowcase = src.get(0).titleLowcase;
        result.titleEn = src.get(0).titleEn;
        result.titleEnLowcase = src.get(0).titleEnLowcase;
        result.adjacentStreet = src.get(0).adjacentStreet;
        result.adjacentStreetEn = src.get(0).adjacentStreetEn;

        return result;
    }

    public static String mergeRouteStrings(String str1, String str2) {
        return mergeRouteStrings(str1 + ", " + str2);
    }

    public static String mergeRouteStrings(String inStr) {
        String[] str = (inStr).split(", ");
        String res = "";
        boolean flag = false;
        if (str.length > 0) {
            int i = 0;
            Arrays.sort(str, new routeComparator());
            for (; i < str.length && !flag; i++) {
                if (str[i].length() > 0) {
                    flag = true;
                }
            }
            if (!flag) {
                return "";
            }
            res = str[i - 1];
            for (; i < str.length; i++) {
                if (res.indexOf(str[i]) < 0 && str[i].length() > 0) {
                    res += ", " + str[i];
                }
            }

        }

        return res;
    }

    public Stop getStop(int KS_ID) {
        return mainDBhelper.getStop(KS_ID);
    }

    public String getStopGeoID(int KS_ID) {
        return mainDBhelper.getStopGeoID(KS_ID);
    }

    public Route getRoute(int KR_ID) {
        return mainDBhelper.getRoute(KR_ID);
    }

    public Stop[] getStops(int[] KS_IDs) {
        return mainDBhelper.getStops(KS_IDs);
    }


    private void copyFavor() {
        File stops = context.getDatabasePath("Stops.db");
        File routes = context.getDatabasePath("Routes.db");
        File stopCorrs = context.getDatabasePath("StopsGeoID.db");
        if (stops.exists()) {
            SQLiteDatabase db = new TransportDBContract().new StopsReaderDbHelper(context, "Stops.db").getReadableDatabase();
            String where = TransportDBContract.StopEntry.COLUMN_NAME_FAVOR + " = '1'";
            Cursor cur = db.query(TransportDBContract.StopEntry.TABLE_NAME, null, where, null, null, null, null);

            int KS_ID;
            if (cur.moveToFirst()) {
                do {
                    KS_ID = cur.getInt(cur.getColumnIndex(StopEntry.COLUMN_NAME_KS_ID));
                    favorBDhelper.setFavor(KS_ID, true);
                } while (cur.moveToNext());
            }

            cur.close();
            stops.delete();
            routes.delete();
            stopCorrs.delete();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        setBackgroundUpdatesActivated( sharedPref.getBoolean("backgroundFlag", false));
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    static Uri getGooglePlay(String packageName) {
        return packageName == null ? null : Uri.parse(GOOGLE_PLAY + packageName);
    }

    static boolean isPackageExists(Context context, String targetPackage) {
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals(targetPackage)) return true;
        }
        return false;
    }

    static Intent createIntentForGooglePlay(Context context) {
        String packageName = context.getPackageName();
        Intent intent = new Intent(Intent.ACTION_VIEW, getGooglePlay(packageName));
        if (isPackageExists(context, GOOGLE_PLAY_PACKAGE_NAME)) {
            intent.setPackage(GOOGLE_PLAY_PACKAGE_NAME);
        }
        return intent;
    }
}
