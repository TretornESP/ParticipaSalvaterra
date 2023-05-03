package com.tretornesp.participa.repository;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tretornesp.participa.BuildConfig;
import com.tretornesp.participa.util.SSLUtil;

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
    private static final String proposalUri = "/proposal";


    public ServerRepository() {
    }

    @Override
    public String login(String user, String password) {
        JSONObject object = new JSONObject();
        try {
            object.put("username", user);
            object.put("password", password);
        } catch (JSONException exception) {
            return null;
        }

        RequestBody requestBody = RequestBody.create(object.toString(), JSON);
        String baseUrl = BuildConfig.ROOT_URL;
        Request request = new Request.Builder().url(baseUrl+loginUri).post(requestBody).build();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient client = builder.build();

        try (Response response = client.newCall(request).execute()) {
             if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
             return response.body().string();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void logout() {

    }

    @Override
    public boolean validateToken(String token) {
        return false;
    }

    @Override
    public String refreshToken(String token) {
        return null;
    }

    @Override
    public String getProposals(String token, String start, int size) {
        if (token == null) return null;

        String baseUrl;
        if (start.equals("-1")) {
            baseUrl = BuildConfig.ROOT_URL+proposalUri+"/?items="+size;
        } else {
            baseUrl = BuildConfig.ROOT_URL+proposalUri+"/?start="+start+"&items="+size;
        }
        Log.d("CUAK", "getProposals: " + baseUrl);
        //baseUrl = "https://participasalvaterra.es/proposal/?items=1&start=645292df925c9b587f2920bc";
        Request request = new Request.Builder()
                .url(baseUrl)
                .addHeader("Authorization", "Bearer "+token)
                .build();

        Log.d("CUAK", "getProposals: " + request.toString());

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient client = builder.build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return response.body().string();
        } catch (Exception e) {
            Log.d("CUAK", "getProposals error: " + e.getMessage());
            Log.d("CUAK", "getProposals error: " + e.getStackTrace());
            Log.d("CUAK", "getProposals error: " + e.getCause());

            return null;
        }
    }

}