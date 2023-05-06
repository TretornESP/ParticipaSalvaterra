package com.tretornesp.participa.model.controller;

public class SubstitutionProposalImageControllerModel {
    private String url;
    private String signed;

    public SubstitutionProposalImageControllerModel(String url, String signed) {
        this.url = url;
        this.signed = signed;
    }

    public String getUrl() {
        return url;
    }

    public String getSigned() {
        return signed;
    }
}
