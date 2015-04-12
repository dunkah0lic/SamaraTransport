package com.nikolaychernov.activation.backend;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nikolay on 11.04.2015.
 */
public class Utils {

    public static String getAccessToken(String refreshToken) {

        String url = "https://accounts.google.com/o/oauth2/token";
        String charset = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()
        String grantType = "refresh_token";
        String clientId = "555843424933-cee3ufklqa6sr6rs39sjccp5tihjtn8t.apps.googleusercontent.com";
        String clientSecret = "mOzecEHd47XDPBUneinwLwIw";
        String query = "";

        query = String.format("grant_type=" + grantType + "&client_id=" + clientId + "&client_secret=" + clientSecret + "&refresh_token=" + refreshToken);

        try {
            URLConnection connection = new URL(url).openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

            try (OutputStream output = connection.getOutputStream()) {
                output.write(query.getBytes(charset));
            }

            InputStream response = connection.getInputStream();
            String responseString = readStreamToString(response, charset);
            System.out.println(responseString);

            JSONObject json = new JSONObject(responseString);
            String accessToken = json.getString("access_token");

            return accessToken;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static int checkLicense(String packageName, String productId, String token, String accessToken){
        int result = 1;
        try {

            String getURL = "https://www.googleapis.com/androidpublisher/v2/applications/" + packageName + "/purchases/products/" + productId + "/tokens/" + token + "?access_token=" + accessToken;
            URLConnection connection = new URL(getURL).openConnection();
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            InputStream response = connection.getInputStream();

            String responseString = readStreamToString(response, "UTF-8");
            JSONObject json = new JSONObject(responseString);

            String kind = json.getString("kind");
            String developerPayload = json.getString("developerPayload");
            int consumptionState = json.getInt("consumptionState");
            int purchaseState = json.getInt("purchaseState");
            long purchaseTimeMillis = json.getLong("purchaseTimeMillis");
            if (purchaseState==0){
                result = 0;
            }
            System.out.println(kind);
            System.out.println(developerPayload);
            System.out.println(consumptionState);
            System.out.println(purchaseState);
            System.out.println(purchaseTimeMillis);


        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return result;
    }

    public static String getRefreshToken(String code)
    {

        org.apache.http.client.HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("https://accounts.google.com/o/oauth2/token");
        try
        {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
            nameValuePairs.add(new BasicNameValuePair("grant_type",    "authorization_code"));
            nameValuePairs.add(new BasicNameValuePair("client_id",     "555843424933-cee3ufklqa6sr6rs39sjccp5tihjtn8t.apps.googleusercontent.com"));
            nameValuePairs.add(new BasicNameValuePair("client_secret", "mOzecEHd47XDPBUneinwLwIw"));
            nameValuePairs.add(new BasicNameValuePair("code", code));
            nameValuePairs.add(new BasicNameValuePair("redirect_uri", "http://proverbial-deck-865.appspot.com/hello"));
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
            String refreshToken = json.getString("refresh_token");
            return refreshToken;
        }
        catch (Exception e) { e.printStackTrace(); }

        return null;
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
}
