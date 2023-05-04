package com.tretornesp.participa.service;

import android.util.Log;
import android.widget.RelativeLayout;

import com.google.android.material.card.MaterialCardView;
import com.tretornesp.participa.model.request.CreateProposalRequestModel;
import com.tretornesp.participa.model.request.EditProposalRequestModel;
import com.tretornesp.participa.model.response.CreateProposalResponseModel;
import com.tretornesp.participa.model.response.LikeProposalResponseModel;
import com.tretornesp.participa.model.response.ListProposalsResponseModel;
import com.tretornesp.participa.model.ProposalModel;
import com.tretornesp.participa.repository.ServerRepository;

import java.util.ArrayDeque;
import java.util.ArrayList;
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

    public ProposalModel getProposal(String token, String proposalId) {
        if (this.cached_proposals != null) {
            for (ProposalModel proposal : this.cached_proposals) {
                if (proposal.getId().equals(proposalId)) {
                    return proposal;
                }
            }
        } else {
            this.cached_proposals = new ArrayList<>();
        }

        ServerRepository repository = new ServerRepository();
        try {
            String proposal_response = repository.getProposal(token, proposalId);
            ProposalModel proposal = ProposalModel.fromJson(proposal_response);
            this.cached_proposals.add(proposal);
            return proposal;
        } catch (Exception e) {
            Log.d("ProposalService", e.getMessage());
            return null;
        }
    }

    public CreateProposalResponseModel createProposal(String token, CreateProposalRequestModel requestModel) {
        ServerRepository repository = new ServerRepository();
        try {
            String proposal_response = repository.createProposal(token, requestModel.toJson());
            CreateProposalResponseModel proposal = CreateProposalResponseModel.fromJson(proposal_response);
            if (this.cached_proposals != null) {
                this.cached_proposals.add(proposal.getProposal());
            } else {
                this.cached_proposals = new ArrayList<>();
                this.cached_proposals.add(proposal.getProposal());
            }
            return proposal;
        } catch (Exception e) {
            Log.d("ProposalService", e.getMessage());
            return null;
        }
    }

    public void deleteProposal(String token, String proposalId) {
        ServerRepository repository = new ServerRepository();
        try {
            repository.deleteProposal(token, proposalId);
            if (this.cached_proposals != null) {
                for (ProposalModel proposal : this.cached_proposals) {
                    if (proposal.getId().equals(proposalId)) {
                        this.cached_proposals.remove(proposal);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Log.d("ProposalService", e.getMessage());
        }
    }

    public ProposalModel updateProposal(String token, String proposalId, EditProposalRequestModel requestModel) {
        ServerRepository repository = new ServerRepository();
        try {
            String result = repository.editProposal(token, proposalId, requestModel.toJson());
            CreateProposalResponseModel proposal = CreateProposalResponseModel.fromJson(result);

            if (this.cached_proposals != null) {
                this.cached_proposals.removeIf(cp -> cp.getId().equals(proposalId));
                this.cached_proposals.add(proposal.getProposal());
            } else {
                this.cached_proposals = new ArrayList<>();
                this.cached_proposals.add(proposal.getProposal());
            }
            return proposal.getProposal();
        } catch (Exception e) {
            Log.d("ProposalService", e.getMessage());
            return null;
        }
    }

    public int likeProposal(String token, String proposalId) {
        ServerRepository repository = new ServerRepository();
        try {
            String result = repository.likeProposal(token, proposalId);
            LikeProposalResponseModel likes = LikeProposalResponseModel.fromJson(result);
            float fl = Float.parseFloat(likes.getLikes());

            if (this.cached_proposals != null) {
                for (ProposalModel proposal : this.cached_proposals) {
                    if (proposal.getId().equals(proposalId)) {
                        proposal.setLikes((int) fl);
                        break;
                    }
                }
            }

            return (int) fl;
        } catch (Exception e) {
            Log.d("ProposalService", e.getMessage());
            return -1;
        }
    }

    public int dislikeProposal(String token, String proposalId) {
        ServerRepository repository = new ServerRepository();
        try {
            String result = repository.unlikeProposal(token, proposalId);
            LikeProposalResponseModel likes = LikeProposalResponseModel.fromJson(result);
            float fl = Float.parseFloat(likes.getLikes());

            if (this.cached_proposals != null) {
                for (ProposalModel proposal : this.cached_proposals) {
                    if (proposal.getId().equals(proposalId)) {
                        proposal.setLikes((int) fl);
                        break;
                    }
                }
            }

            return (int) fl;
        } catch (Exception e) {
            Log.d("ProposalService", e.getMessage());
            return -1;
        }
    }
}
