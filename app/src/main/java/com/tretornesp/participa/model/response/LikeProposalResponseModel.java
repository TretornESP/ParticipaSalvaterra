package com.tretornesp.participa.model.response;

import com.google.gson.Gson;

public class LikeProposalResponseModel {
    private String likes;

    private LikeProposalResponseModel(String likes) {
        this.likes = likes;
    }

    public String getLikes() {
        return likes;
    }

    public static LikeProposalResponseModel fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, LikeProposalResponseModel.class);
    }
}
