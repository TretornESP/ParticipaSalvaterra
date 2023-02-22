package com.tretornesp.participa.repository;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringWriter;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Response;

public class ServerRepository implements RepositoryIF {


    public static final String loginUri = "/security/login";
    public static final String logoutUri = "/security/logout";
    public static final String validateUri = "/security/validateToken";
    public static final String refreshUri = "/security/refreshToken";

    Requester requester;

    public ServerRepository() {
        this.requester = Requester.getRequester();
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

        //TODO: Present async logic through layers
        return false;
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
