package com.tretornesp.participa.controller;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.model.Marker;
import com.tretornesp.participa.model.CredentialsModel;
import com.tretornesp.participa.model.ProposalModel;
import com.tretornesp.participa.model.UserModel;
import com.tretornesp.participa.model.controller.ImageViewLoadControllerModel;
import com.tretornesp.participa.model.controller.ImageViewLoadMarkerControllerModel;
import com.tretornesp.participa.model.controller.ListLoadControllerModel;
import com.tretornesp.participa.model.controller.LoadListItemControllerModel;
import com.tretornesp.participa.model.controller.SubstitutionProposalImageControllerModel;
import com.tretornesp.participa.service.LoginService;
import com.tretornesp.participa.service.ProposalService;
import com.tretornesp.participa.service.UploadsService;
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

    private void _refresh(Callback callback) {
        ProposalService proposalService = ProposalService.getInstance();

        try {
            CredentialsModel credentials = LoginService.getCredentials();
            UserModel user = UserService.getInstance().getCurrentUser(credentials.getToken());
            proposalService.invalidateCache();
            List<ProposalModel> proposals = proposalService.getProposals(credentials.getToken(), null, null);
            callback.onSuccess(new ListLoadControllerModel(user, proposals));
        } catch (TokenMissingException tme) {
            callback.onLoginRequired();
        }
    }

    private void _getSignedImage(String url, ImageView image, Callback callback) {
        UploadsService uploadsService = UploadsService.getInstance();

        try {
            CredentialsModel credentials = LoginService.getCredentials();
            String signed = uploadsService.presignImage(credentials.getToken(), url);
            callback.onSuccess(new ImageViewLoadControllerModel(image, signed));
        } catch (TokenMissingException tme) {
            callback.onLoginRequired();
        } catch (Exception e) {
            callback.onFailure(e.getMessage());
        }
    }

    private void _getSignedImageMarker(String url, ImageView image, Marker marker, Callback callback) {
        UploadsService uploadsService = UploadsService.getInstance();

        try {
            CredentialsModel credentials = LoginService.getCredentials();
            String signed = uploadsService.presignImage(credentials.getToken(), url);
            callback.onSuccess(new ImageViewLoadMarkerControllerModel(image, signed, marker));
        } catch (TokenMissingException tme) {
            callback.onLoginRequired();
        } catch (Exception e) {
            callback.onFailure(e.getMessage());
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

    private void _signUrlSubstitute(String url, Callback callback) {
        UploadsService uploadsService = UploadsService.getInstance();

        try {
            CredentialsModel credentials = LoginService.getCredentials();
            String signed = uploadsService.presignImage(credentials.getToken(), url);
            callback.onSuccess(new SubstitutionProposalImageControllerModel(url, signed));
        } catch (TokenMissingException tme) {
            callback.onLoginRequired();
        } catch (Exception e) {
            callback.onFailure(e.getMessage());
        }
    }

    private void _loadListItem(View v, String id, Callback callback) {
        ProposalService proposalService = ProposalService.getInstance();
        try {
            CredentialsModel credentials = LoginService.getCredentials();
            ProposalModel proposal = proposalService.getProposal(credentials.getToken(), id);
            callback.onSuccess(new LoadListItemControllerModel(v, proposal));
        } catch (TokenMissingException tme) {
            callback.onLoginRequired();
        } catch (Exception e) {
            callback.onFailure(e.getMessage());
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

    public void loadListItem(View v, String id, Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                _loadListItem(v, id, callback);
            }
        }).start();
    }

    public void refresh(Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                _refresh(callback);
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

    public void loadImage(String url, ImageView image, Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (url == null) {
                    callback.onFailure("No image");
                    return;
                }
                if (url.contains("http")) {
                    callback.onSuccess(new ImageViewLoadControllerModel(image, url));
                } else {
                    _getSignedImage(url, image, callback);
                }
            }
        }).start();
    }

    public void loadImageIntoMarker(String url, ImageView image, Marker m, Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (url == null) {
                    callback.onFailure("No image");
                    return;
                }
                if (url.contains("http")) {
                    callback.onSuccess(new ImageViewLoadMarkerControllerModel(image, url, m));
                    return;
                }
                _getSignedImageMarker(url, image, m, callback);
            }
        }).start();
    }

    public void substituteImage(String image, Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (image == null) {
                    callback.onFailure("No image");
                    return;
                }
                if (image.contains("http")) {
                    callback.onSuccess(new SubstitutionProposalImageControllerModel(image, image));
                    return;
                }
                _signUrlSubstitute(image, callback);
            }
        }).start();
    }
}

