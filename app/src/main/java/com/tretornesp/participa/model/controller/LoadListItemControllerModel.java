package com.tretornesp.participa.model.controller;

import android.view.View;

import com.tretornesp.participa.model.ProposalModel;

public class LoadListItemControllerModel {
    private View view;
    private ProposalModel proposal;

    public LoadListItemControllerModel(View view, ProposalModel proposal) {
        this.view = view;
        this.proposal = proposal;
    }

    public View getView() {
        return view;
    }

    public ProposalModel getProposal() {
        return proposal;
    }
}
