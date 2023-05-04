package com.tretornesp.participa.model.response;

import com.google.gson.Gson;
import com.tretornesp.participa.model.ProposalModel;

public class CreateProposalResponseModel {
    private ProposalModel proposal;

    public ProposalModel getProposal() {
        return proposal;
    }

    private CreateProposalResponseModel(ProposalModel proposal) {
        this.proposal = proposal;
    }

    public static CreateProposalResponseModel fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, CreateProposalResponseModel.class);
    }
}
