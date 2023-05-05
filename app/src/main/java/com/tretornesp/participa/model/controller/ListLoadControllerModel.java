package com.tretornesp.participa.model.controller;

import com.tretornesp.participa.model.ProposalModel;
import com.tretornesp.participa.model.UserModel;

import java.util.List;

public class ListLoadControllerModel {
    private UserModel user;
    private List<ProposalModel> proposals;

    public ListLoadControllerModel(UserModel user, List<ProposalModel> proposals) {
        this.user = user;
        this.proposals = proposals;
    }

    public UserModel getUser() {
        return user;
    }

    public List<ProposalModel> getProposals() {
        return proposals;
    }
}
