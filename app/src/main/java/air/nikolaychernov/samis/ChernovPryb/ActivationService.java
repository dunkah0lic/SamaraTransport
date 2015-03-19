package air.nikolaychernov.samis.ChernovPryb;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ActivationService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "air.nikolaychernov.samis.ChernovPryb.action.FOO";
    private static final String ACTION_BAZ = "air.nikolaychernov.samis.ChernovPryb.action.BAZ";

    private static final String BASE64_PUBLIC_KEY = "305c300d06092a864886f70d0101010500034b00304802410081d21f93177c745b9bea9709ff49936b25ed5ec6f306191949c62242232856dda1efdd5e13e8b3df8e14f6ec5ed920d022e7a06816e8e1fd8cf0a380e2f83f470203010001";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "air.nikolaychernov.samis.ChernovPryb.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "air.nikolaychernov.samis.ChernovPryb.extra.PARAM2";

    Handler mHandler;
    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, ActivationService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, ActivationService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public ActivationService() {
        super("ActivationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        try {

            String xform = "RSA/ECB/PKCS1Padding";
            HttpClient client = new DefaultHttpClient();
            String getURL = "http://proverbial-deck-865.appspot.com/activation?name=" + param1 ;
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
                String decKeyStr = new String(decKey, "UTF-8");
                final String decLicense = new String(decBytes, "UTF-8");

                Context ctx = getApplicationContext();
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
                //SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("authkey", decKeyStr);
                editor.commit();

                Log.d("GET RESPONSE:", response);
                //if (ok) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ActivationService.this,  decLicense + "!", Toast.LENGTH_LONG).show();
                        }
                    });
                //}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
