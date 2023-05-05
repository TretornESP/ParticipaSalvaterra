package com.tretornesp.participa.controller;

import android.os.Handler;
import android.os.Looper;

import com.tretornesp.participa.model.CredentialsModel;
import com.tretornesp.participa.model.ProposalModel;
import com.tretornesp.participa.model.UserModel;
import com.tretornesp.participa.model.controller.ListLoadControllerModel;
import com.tretornesp.participa.service.LoginService;
import com.tretornesp.participa.service.ProposalService;
import com.tretornesp.participa.service.UserService;
import com.tretornesp.participa.service.exception.TokenMissingException;
import com.tretornesp.participa.util.Callback;

import java.util.List;

public class ListController {
    public ListController() {

    }

    private void _loadList(Callback callback) {
        ProposalService proposalService = ProposalService.getInstance();

        try {
            CredentialsModel credentials = LoginService.getCredentials();
            UserModel user = UserService.getInstance().getCurrentUser(credentials.getToken());
            List<ProposalModel> proposals = proposalService.getProposals(credentials.getToken(), null, null);
            callback.onSuccess(new ListLoadControllerModel(user, proposals));
        } catch (TokenMissingException tme) {
            callback.onLoginRequired();
        }
    }

    private void _like(String id, Callback callback) {
        UserService userService = UserService.getInstance();
        ProposalService proposalService = ProposalService.getInstance();
        try {
            CredentialsModel credentials = LoginService.getCredentials();

            UserModel user = userService.getCurrentUser(credentials.getToken());
            if (user.getLiked_proposals().contains(id)) {
                proposalService.dislikeProposal(credentials.getToken(), id);
                callback.onSuccess(false);
            } else {
                proposalService.likeProposal(credentials.getToken(), id);
                callback.onSuccess(true);
            }
            userService.invalidateCache();

        } catch (TokenMissingException tme) {
            callback.onLoginRequired();
        }
    }

    public void loadList(Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                _loadList(callback);
            }
        }).start();
    }

    public void like(String id, Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                _like(id, callback);
            }
        }).start();
    }
}
