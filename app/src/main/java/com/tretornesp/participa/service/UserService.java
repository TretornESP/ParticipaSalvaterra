package com.tretornesp.participa.service;

import android.util.Log;

import com.tretornesp.participa.model.UserModel;
import com.tretornesp.participa.model.request.EditUserRequestModel;
import com.tretornesp.participa.model.request.RegisterUserRequestModel;
import com.tretornesp.participa.model.response.EditUserResponseModel;
import com.tretornesp.participa.model.response.GetCurrentUserResponseModel;
import com.tretornesp.participa.model.response.RegisterUserResponseModel;
import com.tretornesp.participa.repository.ServerRepository;

import java.util.ArrayList;
import java.util.List;

public class UserService {
    private static UserService instance = null;

    private UserModel cached_current_user;
    private List<UserModel> cached_users;

    private UserService() {
        this.cached_current_user = null;
        this.cached_users = null;
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public UserModel getUser(String token, String userid) {
        if (this.cached_users == null) {
            cached_users = new ArrayList<>();
        } else {
            for (UserModel user : this.cached_users) {
                if (user.getUid().equals(userid)) {
                    return user;
                }
            }
        }

        ServerRepository repository = new ServerRepository();
        try {
            String user_response = repository.getUser(token, userid);
            UserModel user = UserModel.fromJson(user_response);
            this.cached_users.add(user);
            return user;
        } catch (Exception e) {
            Log.d("UserService", e.toString());
            return null;
        }
    }
    public UserModel getCurrentUser(String token) {
        if (this.cached_current_user == null) {
            ServerRepository repository = new ServerRepository();
            try {
                String user_response = repository.getCurrentUser(token);
                GetCurrentUserResponseModel user = GetCurrentUserResponseModel.fromJson(user_response);
                this.cached_current_user = user.getUser();
                return user.getUser();
            } catch (Exception e) {
                Log.d("UserService", e.toString());
                return null;
            }
        } else {
            return this.cached_current_user;
        }
    }
    public void editUser(String token, EditUserRequestModel requestModel) {
        ServerRepository repository = new ServerRepository();
        try {
            String user_response = repository.editUser(token, requestModel.toJson());
            EditUserResponseModel user = EditUserResponseModel.fromJson(user_response);
            this.cached_current_user = user.getUser();
        } catch (Exception e) {
            Log.d("UserService", e.toString());
        }
    }
    public void deleteUser(String token) {
        ServerRepository repository = new ServerRepository();
        try {
            repository.deleteUser(token);
            this.cached_current_user = null;
        } catch (Exception e) {
            Log.d("UserService", e.toString());
        }
    }
    public UserModel registerUser(RegisterUserRequestModel requestModel) {
        ServerRepository repository = new ServerRepository();
        try {
            String user_response = repository.registerUser(requestModel.toJson());
            RegisterUserResponseModel user = RegisterUserResponseModel.fromJson(user_response);
            this.cached_current_user = user.getUser();
            return user.getUser();
        } catch (Exception e) {
            Log.d("UserService", e.toString());
            return null;
        }
    }
    public void invalidateCache() {
        this.cached_current_user = null;
        this.cached_users = null;
    }
}
