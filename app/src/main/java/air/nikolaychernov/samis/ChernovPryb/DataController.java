package air.nikolaychernov.samis.ChernovPryb;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v4.util.Pair;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.crypto.Cipher;

import air.nikolaychernov.samis.ChernovPryb.TransportDBContract.FavorReaderDbHelper;
import air.nikolaychernov.samis.ChernovPryb.TransportDBContract.MainReaderDbHelper;
import air.nikolaychernov.samis.ChernovPryb.TransportDBContract.StopEntry;

public class DataController implements Serializable {

    private static volatile DataController instance;

    private static final String BASE64_PUBLIC_KEY = "305c300d06092a864886f70d0101010500034b00304802410081d21f93177c745b9bea9709ff49936b25ed5ec6f306191949c62242232856dda1efdd5e13e8b3df8e14f6ec5ed920d022e7a06816e8e1fd8cf0a380e2f83f470203010001";


    private StopSearchActivity activity;
    private MainReaderDbHelper mainDBhelper;
    private FavorReaderDbHelper favorBDhelper;
    private Navigation nav;
    private Context context;

    private int searchRadius = 1400;
    private boolean isAutoUpdate = true;
    //	private boolean requestAddPredict = false;
    private boolean showBuses = true;
    private String authKey = "222222";

    public boolean isShowTrams() {
        return showTrams;
    }

    public void setShowTrams(boolean showTrams) {
        this.showTrams = showTrams;
    }

    public boolean isShowTrolls() {
        return showTrolls;
    }

    public void setShowTrolls(boolean showTrolls) {
        this.showTrolls = showTrolls;
    }

    public boolean isShowComm() {
        return showComm;
    }

    public void setShowComm(boolean showComm) {
        this.showComm = showComm;
    }

    public boolean isShowBuses() {
        return showBuses;
    }

    public void setShowBuses(boolean showBuses) {
        this.showBuses = showBuses;
    }

    private boolean showTrams = true;
    private boolean showTrolls = true;
    private boolean showComm = true;
    private Typeface[] tp;

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

            instance.nav = new Navigation(act);
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
        TransportDBContract contr = new TransportDBContract();
        mainDBhelper = contr.new MainReaderDbHelper(act);
        favorBDhelper = contr.new FavorReaderDbHelper(act);
        nav = new Navigation(act);
        copyFavor();
        initSettings();

        tp = new Typeface[3];
        tp[HelveticaFont.Bold] = Typeface.createFromAsset(activity.getAssets(), "fonts/Helvetica" + "Bold" + ".otf");
        tp[HelveticaFont.Medium] = Typeface.createFromAsset(activity.getAssets(), "fonts/Helvetica" + "Medium" + ".otf");
        tp[HelveticaFont.Light] = Typeface.createFromAsset(activity.getAssets(), "fonts/Helvetica" + "Light" + ".otf");
    }

    public static class HelveticaFont {

        public static int Bold = 0, Medium = 1, Light = 2;
    }

    public void setTypeface(TextView v, int faceName) {
        v.setTypeface(tp[faceName]);
    }

    public Typeface getTypeface(int faceName) {
        return tp[faceName];
    }

    public void setSettings(int radius, boolean isAutoUpdate,
                            /*boolean requestAddPredict,*/ boolean showBuses, boolean showTrolls, boolean showTrams, boolean showComm) {
        this.searchRadius = radius;
        this.isAutoUpdate = isAutoUpdate;
        this.showBuses = showBuses;
        this.showComm = showComm;
        this.showTrams = showTrams;
        this.showTrolls = showTrolls;
//		this.requestAddPredict = requestAddPredict;
        commitSettings();
        //
    }

    private void initSettings() {
        SharedPreferences prefs = activity.getPreferences(Context.MODE_PRIVATE);
        isAutoUpdate = prefs.getBoolean("updateFlag", true);
        searchRadius = prefs.getInt("radius", 600);
        showBuses = prefs.getBoolean("showBuses", true);
        showTrolls = prefs.getBoolean("showTrolls", true);
        showTrams = prefs.getBoolean("showTrams", true);
        showComm = prefs.getBoolean("showComm", true);
        authKey = prefs.getString("authkey", "222222");
//		requestAddPredict = prefs.getBoolean("requestAdditionalPredict", false);
        commitSettings();
    }

    private void commitSettings() {
        SharedPreferences prefs = activity.getPreferences(Context.MODE_PRIVATE);
        Editor ed = prefs.edit();
        ed.putBoolean("updateFlag", isAutoUpdate);
        ed.putInt("radius", searchRadius);
        ed.putBoolean("showBuses", showBuses);
        ed.putBoolean("showTrolls", showTrolls);
        ed.putBoolean("showTrams", showTrams);
        ed.putBoolean("showComm", showComm);
//		ed.putBoolean("requestAdditionalPredict", requestAddPredict);
        ed.commit();
    }

    public void setRadius(int radius) {
        searchRadius = radius;
        commitSettings();
    }

    public void setAutoUpdate(boolean isAutoUpdate) {
        this.isAutoUpdate = isAutoUpdate;
        commitSettings();
    }

    public int getRadius() {
        return searchRadius;
    }

    public boolean isAutoUpdate() {
        return this.isAutoUpdate;
    }

//	public boolean isRequestAddPredict() {
//		return requestAddPredict;
//	}
//
//	public void setRequestAddPredict(boolean value) {
//		requestAddPredict = value;
//		commitSettings();
//	}

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
            nav = new Navigation(activity);
        }
        return nav.isLocationChanged();
    }

    public boolean isNavWorking() {
        if (nav == null) {
            nav = new Navigation(activity);
        }
        return nav.isNavWorking();
    }

    public Location getBestLocation() {
        if (nav == null) {
            nav = new Navigation(activity);
        }
        return nav.getBestLocation();
    }

    public double getDistTo(Stop st, boolean force) {
        if (nav == null) {
            nav = new Navigation(activity);
        }
        return nav.getDistTo(st, force);
    }

    public double getDistTo(double latitude, double longitude, boolean force) {
        if (nav == null) {
            nav = new Navigation(activity);
        }
        return nav.getDistTo(latitude, longitude, force);
    }

    public double getLong(boolean force) {
        if (nav == null) {
            nav = new Navigation(activity);
        }
        return nav.getLong(force);
    }

    public double getLat(boolean force) {
        if (nav == null) {
            nav = new Navigation(activity);
        }
        return nav.getLat(force);
    }

    public void requestNewLocation(/* boolean waitForResult */) {
        // nav.requestNewLocation(waitForResult);
        nav.requestNewLocation();
    }

    public boolean navInit() {
        if (nav == null) {
            nav = new Navigation(activity);
        }
        return nav.navInit();
    }

    public void navTerminate() {
        if (nav == null) {
            nav = new Navigation(activity);
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
//            if (DataController.getInstance().requestAddPredict) {
//                return mergeArrival(
//                        getArrivalInfoAPI(KS_ID),
//                        parseMapBubbleArrivalInfo(loadArrivalDataFromMapBubble(KS_ID)));
//            } else {
            Set<Integer> transTypesToShow = new HashSet<Integer>();
            if (showBuses) {
                transTypesToShow.add(1);
            }
            if (showComm) {
                transTypesToShow.add(0);
            }
            if (showTrams) {
                transTypesToShow.add(3);
            }
            if (showTrolls) {
                transTypesToShow.add(4);
            }
            return (new ArrivalXmlParser(transTypesToShow)).parse(loadArrivalDataAPI(KS_ID));
//            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new ArrayList<ArrivalInfo>();
        }
        // if (DataController.getInstance().requestAddPredict) {
        // return mergeArrival(
        // parseArrivalInfo(loadArrivalData(phpBridgeAddress, KS_ID)),
        // parseMapBubbleArrivalInfo(loadArrivalDataFromMapBubble(KS_ID)));
        // } else {
        // return parseArrivalInfo(loadArrivalData(phpBridgeAddress, KS_ID));
        // }
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
            String authKey = "";

            Context context = MyApplication.getAppContext();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            Pair<String,String> pair = getAuthKey(message);
            authKey = pair.first;
            String license = pair.second;
            String tempKey = authKey;

            Log.d("authKey = ", authKey);
            String hash = "";
            try{
                hash = SHA1(tempKey);
            } catch (Exception e){

            }
            if (!license.equals("LICENSED")){
                Log.d("DataController", "No license");
                instance.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(instance.activity, "Отсутствует лицензия", Toast.LENGTH_SHORT).show();
                    }
                });
            }


            String body = "message=" + message + "&os=Android&clientId=40inchv&authKey=" + authKey;//SHA1(message + authKey);//"yPiRbhD");

            httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpConnection.setRequestProperty("Content-Length", String.valueOf(body.length()));

            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);
            //httpConnection.setConnectTimeout(5000);
            // httpConnection.setReadTimeout(4000);

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
            throw new IOException("MalformedURLException " + e.getMessage());
        } catch (SocketTimeoutException e) {
            throw new IOException("Timeout");
        }

        return resultString;

    }


   private static String getPassword(){
       String decKeyStr="555555";
       try {

           String xform = "RSA/ECB/PKCS1Padding";
           HttpClient client = new DefaultHttpClient();
           String getURL = "http://proverbial-deck-865.appspot.com/activation?name=" + StopSearchActivity.accountName;//"40inchverticalrus@gmail.com" ;
           HttpGet get = new HttpGet(getURL);
           HttpResponse responseGet = client.execute(get);
           HttpEntity resEntityGet = responseGet.getEntity();
           //InputStream inputStream = resEntityGet.getContent();

           if (resEntityGet != null) {
               // do something with the response
               final String response = EntityUtils.toString(resEntityGet);
               JSONObject jsonObject = new JSONObject(response);
               final String name = jsonObject.getString("name");
               final String license = jsonObject.getString("license");
               String signature = jsonObject.getString("signature");
               String authKey = jsonObject.getString("authkey");



               String[] byteValues = signature.substring(1, signature.length() - 1).split(",");
               byte[] bytes = new byte[byteValues.length];

               for (int i=0, len=bytes.length; i<len; i++) {
                   bytes[i] = Byte.parseByte(byteValues[i].trim());
               }

               String[] byteValuesKey = authKey.substring(1, authKey.length() - 1).split(",");
               byte[] bytesKey = new byte[byteValuesKey.length];

               for (int i=0, len=bytesKey.length; i<len; i++) {
                   bytesKey[i] = Byte.parseByte(byteValuesKey[i].trim());
               }

               PublicKey pk1 = generatePublicKey(BASE64_PUBLIC_KEY);
                /*Signature sign = Signature.getInstance("SHA1withRSA");
                sign.initVerify(pk1);
                sign.update(license.getBytes());
                final boolean ok = sign.verify(bytes);*/
               byte[] decBytes = decrypt(bytes, pk1, xform);
               byte[] decKey = decrypt(bytesKey, pk1, xform);
               decKeyStr = new String(decKey, "UTF-8");
               final String decLicense = new String(decBytes, "UTF-8");

               //Handler mHandler = new Handler();

               Log.d("GET RESPONSE:", response + decLicense + "!");
               //if (ok) {


               //}
           }
       } catch (Exception e) {
           e.printStackTrace();
       }

       return decKeyStr;
   }

   private static Pair<String, String> getAuthKey(String message) throws IOException {
       String xform = "RSA/ECB/PKCS1Padding";

       String authKey = "";
       String license = "";
       org.apache.http.client.HttpClient client = new DefaultHttpClient();
       HttpPost post = new HttpPost("http://proverbial-deck-865.appspot.com/activation");
       try
       {
           List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
           nameValuePairs.add(new BasicNameValuePair("xml", message));
           nameValuePairs.add(new BasicNameValuePair("name", StopSearchActivity.accountName));


           post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

           org.apache.http.HttpResponse response = client.execute(post);
           BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
           StringBuffer buffer = new StringBuffer();
           for (String line = reader.readLine(); line != null; line = reader.readLine())
           {
               buffer.append(line);
           }
           System.out.println(buffer.toString());
           JSONObject json = new JSONObject(buffer.toString());
           authKey = json.getString("authkey");
           license = json.getString("license");

           String[] byteValuesKey = authKey.substring(1, authKey.length() - 1).split(",");
           byte[] bytesKey = new byte[byteValuesKey.length];

           for (int i=0, len=bytesKey.length; i<len; i++) {
               bytesKey[i] = Byte.parseByte(byteValuesKey[i].trim());
           }

           PublicKey pk1 = generatePublicKey(BASE64_PUBLIC_KEY);

           byte[] decKey = decrypt(bytesKey, pk1, xform);
           String decKeyStr = new String(decKey, "UTF-8");
           authKey = decKeyStr;

       }
       catch (Exception e) { e.printStackTrace(); }
       Pair<String,String> result = new Pair<String,String>(authKey, license);
       return result;
   }

    public static PublicKey generatePublicKey(String hex){
        try {
            if (hex == null || hex.trim().length() == 0)     return null;
            byte[] data=new BigInteger(hex,16).toByteArray();
            X509EncodedKeySpec keyspec=new X509EncodedKeySpec(data);
            KeyFactory keyfactory=KeyFactory.getInstance("RSA");
            return keyfactory.generatePublic(keyspec);
        }
        catch (  Exception e) {
            return null;
        }
    }

    private static byte[] decrypt(byte[] inpBytes, PublicKey key,
                                  String xform) throws Exception{
        Cipher cipher = Cipher.getInstance(xform);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(inpBytes);
    }



    public Stop[] searchByName(String name, boolean isFavorOnly) {
        if (isFavorOnly) {
            return mainDBhelper.searchByName(name, favorBDhelper.getFavorSet());
        }
        return mainDBhelper.searchByName(name);
    }

    public Stop[] getFavor() {
        int[] KS_IDs = favorBDhelper.getFavor();
        // Stop[] result = new Stop[KS_IDs.length];
        // for (int i = 0; i < KS_IDs.length; i++) {
        // result[i] = mainDBhelper.getStop(KS_IDs[i]);
        // }
        return mainDBhelper.getStops(KS_IDs);
    }

    public Stop[] searchNearMe(boolean isFavorOnly, boolean force) {
        if (getLat(force) < 0 || getLong(force) < 0) {
            return new Stop[0];
        }
        if (force) {
            // navTerminate();
            // navInit();
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
                tmp = new ArrayList<Stop>();
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
        // //Log.appendLog("DataController stopsToGroup");
        // toGroups -= System.nanoTime();
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
        File stops = activity.getDatabasePath("Stops.db");
        File routes = activity.getDatabasePath("Routes.db");
        File stopCorrs = activity.getDatabasePath("StopsGeoID.db");
        if (stops.exists()) {
            SQLiteDatabase db = new TransportDBContract().new StopsReaderDbHelper(activity, "Stops.db").getReadableDatabase();
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

}
