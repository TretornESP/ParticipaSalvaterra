package com.tretornesp.participa.service;

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

    public CredentialsModel getCredentials() throws TokenMissingException {
        if (this.cached_credentials == null) {
            throw new TokenMissingException("No credentials found");
        } else {
            return this.cached_credentials;
        }
    }

    public boolean isLoggedIn() {
        if (this.cached_credentials == null) {
            return false;
        }
        return this.cached_credentials.getToken() != null;
    }

    public CredentialsModel login(String user, String password) {
        ServerRepository repository = new ServerRepository();

        try {
            String response = repository.login(LoginRequestModel.create(user, password).toJson());
            LoginResponseModel responseModel = LoginResponseModel.fromJson(response);

            this.cached_credentials = new CredentialsModel(responseModel.getToken(), responseModel.getRefresh(), responseModel.getUser());

            return cached_credentials;
        } catch (Exception e) {
            Log.d("LoginService", e.getMessage());
            return null;
        }
    }
    public void logout() {
        ServerRepository repository = new ServerRepository();

        try {
            repository.logout(this.cached_credentials.getToken());
            this.cached_credentials = null;
        } catch (Exception e) {
            Log.d("LoginService", e.getMessage());
        }
    }
    public boolean isVerified() {
        ServerRepository repository = new ServerRepository();
        try {
            return repository.isVerified(this.cached_credentials.getToken());
        } catch (Exception e) {
            Log.d("LoginService", e.getMessage());
            return false;
        }
    }
    public boolean validateToken() {
        ServerRepository repository = new ServerRepository();
        try {
            return repository.validateToken(this.cached_credentials.getToken());
        } catch (Exception e) {
            Log.d("LoginService", e.getMessage());
            return false;
        }
    }
    public void refreshToken() {
        ServerRepository repository = new ServerRepository();

        try {
            String response = repository.refreshToken(this.cached_credentials.getRefresh());
            RefreshTokenResponse responseModel = RefreshTokenResponse.fromJson(response);
            String token = responseModel.getToken();
            this.cached_credentials.setToken(token);
        } catch (Exception e) {
            Log.d("LoginService", e.getMessage());
        }
    }
}
