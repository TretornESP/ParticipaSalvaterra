package com.tretornesp.participa.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.tretornesp.participa.model.CredentialsModel;
import com.tretornesp.participa.model.request.LoginRequestModel;
import com.tretornesp.participa.model.response.LoginResponseModel;
import com.tretornesp.participa.model.UserModel;
import com.tretornesp.participa.model.response.RefreshTokenResponse;
import com.tretornesp.participa.repository.ServerRepository;
import com.tretornesp.participa.service.exception.TokenMissingException;

public class LoginService {

    private static LoginService instance = null;

    private CredentialsModel cached_credentials;

    private LoginService() {
        this.cached_credentials = null;
    }
    public static LoginService getInstance() {
        if (instance == null) {
            instance = new LoginService();
        }
        return instance;
    }

    public static CredentialsModel getCredentials() throws TokenMissingException {
        if (instance == null) {
            throw new TokenMissingException("No credentials found");
        }

        if (instance.cached_credentials == null) {
            throw new TokenMissingException("No credentials found");
        }

        return instance.cached_credentials;
    }

    public boolean has(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("credentials", Context.MODE_PRIVATE);

        String token = sharedPref.getString("token", null);
        String refresh = sharedPref.getString("refresh", null);
        String user = sharedPref.getString("user", null);

        return token != null && refresh != null && user != null;
    }
    public void persist(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("credentials", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("token", this.cached_credentials.getToken());
        editor.putString("refresh", this.cached_credentials.getRefresh());
        editor.putString("user", this.cached_credentials.getUser().toJson());

        editor.apply();
    }
    public void use(Context context) {
        this.cached_credentials = load(context);
    }
    public CredentialsModel load(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("credentials", Context.MODE_PRIVATE);

        String token = sharedPref.getString("token", null);
        String refresh = sharedPref.getString("refresh", null);
        String user = sharedPref.getString("user", null);

        if (token == null || refresh == null || user == null) {
            return null;
        }

        this.cached_credentials = new CredentialsModel(token, refresh, UserModel.fromJson(user));

        return this.cached_credentials;
    }
    public void invalidate(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("credentials", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.remove("token");
        editor.remove("refresh");
        editor.remove("user");

        editor.apply();
    }

    public CredentialsModel login(String user, String password) {
        ServerRepository repository = new ServerRepository();

        try {
            String response = repository.login(LoginRequestModel.create(user, password).toJson());
            LoginResponseModel responseModel = LoginResponseModel.fromJson(response);

            this.cached_credentials = new CredentialsModel(responseModel.getToken(), responseModel.getRefresh(), responseModel.getUser());

            return cached_credentials;
        } catch (Exception e) {
            Log.d("LoginService", e.toString());
            return null;
        }
    }

    public boolean isLoggedIn(String token) {
        if (this.cached_credentials == null) {
            return false;
        }
        if (this.cached_credentials.getToken() == null) {
            return false;
        }

        return this.cached_credentials.getToken().equals(token);
    }

    public void logout(String token) {
        ServerRepository repository = new ServerRepository();

        try {
            repository.logout(token);
            this.cached_credentials = null;
        } catch (Exception e) {
            Log.d("LoginService", e.toString());
        }
    }
    public boolean isVerified(String token) {
        ServerRepository repository = new ServerRepository();
        try {
            return repository.isVerified(token);
        } catch (Exception e) {
            Log.d("LoginService", e.toString());
            return false;
        }
    }
    public boolean validateToken(String token) {
        ServerRepository repository = new ServerRepository();
        try {
            return repository.validateToken(token);
        } catch (Exception e) {
            Log.d("LoginService", e.toString());
            return false;
        }
    }
    public CredentialsModel refreshToken(String token) {
        ServerRepository repository = new ServerRepository();

        try {
            String response = repository.refreshToken(token);
            RefreshTokenResponse responseModel = RefreshTokenResponse.fromJson(response);
            String new_token = responseModel.getToken();
            if (cached_credentials == null) {
                throw new TokenMissingException("No credentials found");
            }
            this.cached_credentials.setToken(new_token);
            return this.cached_credentials;
        } catch (Exception e) {
            Log.d("LoginService", e.toString());
            return null;
        }
    }
}
