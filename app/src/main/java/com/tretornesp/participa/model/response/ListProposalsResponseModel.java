package com.tretornesp.participa.model.response;

import com.google.gson.Gson;
import com.tretornesp.participa.model.ProposalModel;

import java.util.List;

public class ListProposalsResponseModel {
    private String end;
    private List<ProposalModel> proposals;

    private ListProposalsResponseModel(String end, List<ProposalModel> proposals) {
        this.end = end;
        this.proposals = proposals;
    }

    public String getEnd() {
        return end;
    }

    public List<ProposalModel> getProposals() {
        return proposals;
    }

    public static ListProposalsResponseModel fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, ListProposalsResponseModel.class);
    }
}
