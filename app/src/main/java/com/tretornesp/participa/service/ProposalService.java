package com.tretornesp.participa.service;

import android.util.Log;
import android.widget.RelativeLayout;

import com.google.android.material.card.MaterialCardView;
import com.tretornesp.participa.model.response.ListProposalsResponseModel;
import com.tretornesp.participa.model.ProposalModel;
import com.tretornesp.participa.repository.ServerRepository;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class ProposalService {
    private static ProposalService instance = null;

    private List<ProposalModel> cached_proposals;

    private ProposalService() {
        this.cached_proposals = null;
    }

    public static ProposalService getInstance() {
        if (instance == null) {
            instance = new ProposalService();
        }
        return instance;
    }

    //Hey, cache doesn't work if you use pagination
    public List<ProposalModel> getProposals(String token, String start, Integer items) {
        if (this.cached_proposals == null) {
            ServerRepository repository = new ServerRepository();
            try {
                String proposals_response = repository.getProposals(token, start, items);
                ListProposalsResponseModel proposals = ListProposalsResponseModel.fromJson(proposals_response);
                if ((start == null || start.equals("-1") && items == null)) {
                    this.cached_proposals = proposals.getProposals();
                }
                return proposals.getProposals();
            } catch (Exception e) {
                Log.d("ProposalService", e.getMessage());
                return null;
            }
        } else {
            return this.cached_proposals;
        }
    }
}
