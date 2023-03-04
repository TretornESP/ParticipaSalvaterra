package com.tretornesp.participa.repository;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tretornesp.participa.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringWriter;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServerRepository implements RepositoryIF {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String loginUri = "/security/login";
    private static final String logoutUri = "/security/logout";
    private static final String validateUri = "/security/validateToken";
    private static final String refreshUri = "/security/refreshToken";


    public ServerRepository() {
    }

    @Override
    public boolean login(String user, String password) {
        JSONObject object = new JSONObject();
        try {
            object.put("username", user);
            object.put("password", password);
        } catch (JSONException exception) {
            return false;
        }



        TrustManager TRUST_ALL_CERTS = new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }
        };

        RequestBody requestBody = RequestBody.create(object.toString(), JSON);
        String baseUrl = BuildConfig.ROOT_URL;
        Request request = new Request.Builder().url(baseUrl+loginUri).post(requestBody).build();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{TRUST_ALL_CERTS}, new java.security.SecureRandom());
            builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) TRUST_ALL_CERTS);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });
        } catch (Exception e) {
            Log.d("CUAK", "Error trusting all");
            return false;
        }


        OkHttpClient client = builder.build();

        try (Response response = client.newCall(request).execute()) {
             if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
             Log.d("CUAK", response.body().toString());
             return true;
        } catch (Exception e) {
            Log.d("CUAK", "Exception");
            Log.d("CUAK", e.toString());
            return false;
        }
    }

    @Override
    public void logout() {

    }

    @Override
    public boolean validate_token(String token) {
        return false;
    }

    @Override
    public String refresh_token(String token) {
        return null;
    }
}
