package com.nikolaychernov.activation.backend;

/**
 * Created by Nikolay on 12.03.2015.
 */

import com.googlecode.objectify.ObjectifyService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Cipher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ActivationServlet extends HttpServlet {
    private static final String BASE64_PRIVATE_KEY = "30820153020100300d06092a864886f70d01010105000482013d3082013902010002410081d21f93177c745b9bea9709ff49936b25ed5ec6f306191949c62242232856dda1efdd5e13e8b3df8e14f6ec5ed920d022e7a06816e8e1fd8cf0a380e2f83f47020301000102401ae4f6079a00fd761109fb7a65b9cf618e3cebba999434d4e954b3ba31e0648640423d5be9ab600522754ccba520d84da57019668e7be451dd4c15d478c47ea1022100b65fb634f1a1c997a769e8227f6e34a754054b3cacd51a1ac4b5d26faf3ff637022100b63b10d06b2aa8205efeca3663183bbd2609e4f7ad61f5854439531f117f77710220666391ee63828ba5a30e288fc5af5fcc59b5a729e776b4f33661464601c40d3d0220342f9611199f8da6378e1fba93864d154ddf6782c654574b62ce47cf8de3430102200bd235905d2b6d79444bca7c11059af0778cc8930e1193fc5155ce8b4b68fad7";

    static {
        ObjectifyService.register(User.class);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String response = "This is the get response!\n";
        String xform = "RSA/ECB/PKCS1Padding";


        String name = req.getParameter("name");
        String response1 = response + name;

        Random rand = new Random();
        int n = rand.nextInt();
        String license = "LICENSED";
        byte[] signature = null;
        byte[] encBytes = null;
        byte[] encKey = null;
        boolean licensed = true;
        licensed = getLicense(name);
        if (licensed) {
            license = "LICENSED";
        } else {
            license = "NOT_LICENSED";
        }

        PrivateKey prk1 = null;
        try {
            prk1 = loadPrivateKey(BASE64_PRIVATE_KEY);
            encBytes = encrypt(license.getBytes("UTF-8"), prk1, xform);
				/*Signature signer = Signature.getInstance("SHA1withRSA");
	            signer.initSign(prk1); // PKCS#8 is preferred
	            signer.update(license.getBytes());
	            signature = signer.sign();*/
        } catch (GeneralSecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("license", license);
            //json.put("signature", signature);
            json.put("signature", encBytes);
            if (license == "LICENSED"){
                encKey = encrypt("yPiRbhD".getBytes("UTF-8"), prk1, xform);
                json.put("authkey", encKey);
            } else {
                encKey = encrypt("1111111".getBytes("UTF-8"), prk1, xform);
                json.put("authkey", encKey);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        resp.setContentType("application/json");
        // Get the printwriter object from response to write the required json object to the output stream
        PrintWriter out = resp.getWriter();
        // Assuming your json object is **jsonObject**, perform the following, it will return your json object
        out.print(json);
        out.flush();
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String name = req.getParameter("name");
        resp.setContentType("text/plain");
        if (name == null) {
            resp.getWriter().println("Please enter a name");
        }
        resp.getWriter().println("Hello " + name);
    }

    public static PrivateKey loadPrivateKey(String hex) throws GeneralSecurityException {
        if (hex == null || hex.trim().length() == 0)     return null;
        byte[] clear=new BigInteger(hex,16).toByteArray();
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        PrivateKey priv = fact.generatePrivate(keySpec);
        Arrays.fill(clear, (byte) 0);
        return priv;
    }

    private static byte[] encrypt(byte[] inpBytes, PrivateKey key,
                                  String xform) throws Exception {
        Cipher cipher = Cipher.getInstance(xform);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(inpBytes);
    }

    private static boolean getLicense(String name){
        boolean result = false;
        System.out.println(name);

        User temp = ObjectifyService.ofy().load().type(User.class).filter("email", name).first().now();
        if (name.equals("40inchverticalrus@gmail.com")||temp!=null&&temp.getAccess()==0) {
            result = true;
        }


        return result;
    }
}
