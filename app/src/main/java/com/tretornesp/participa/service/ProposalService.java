package com.tretornesp.participa.service;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.material.card.MaterialCardView;
import com.tretornesp.participa.model.ListProposalsResponseModel;
import com.tretornesp.participa.model.ProposalModel;
import com.tretornesp.participa.repository.ServerRepository;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class ProposalService implements ProposalServiceIF {
    private static final int LOAD_ITEMS = 10;
    private static final int MAX_ITEMS = 20;

    private static ProposalService instance = null;
    private Deque<ProposalModel> proposals;
    private String load_index;
    private boolean more;
    private int id = 1;

    private ProposalService() {
        this.load_index = "-1";
        this.more = true;
        this.proposals = new ArrayDeque<>();
    }

    public static ProposalService getInstance() {
        if (instance == null) {
            instance = new ProposalService();
        }
        return instance;
    }

    public void loadNext(RelativeLayout relativeLayout) {
        Log.d("CUAK", "loadNext: " + load_index);
        ServerRepository repository = new ServerRepository();
        LoginService loginService = LoginService.getInstance();
        Log.d("CUAK", "loadNext: " + loginService.getToken());
        String response = repository.getProposals(loginService.getToken(), load_index, LOAD_ITEMS);

        if (response == null) {
            return;
        }

        Log.d("CUAK", "loadNext: " + response);
        ListProposalsResponseModel proposals = ListProposalsResponseModel.fromJson(response);
        if (proposals.getEnd().equals("-1")) {
            this.more = false;
        }

        Log.d("CUAK", "loadNext proposal size: " + proposals.getProposals().size());
        for (ProposalModel proposal : proposals.getProposals()) {
            MaterialCardView card = proposal.toMaterialCardView(relativeLayout.getContext());
            card.setId(id++);
            if (relativeLayout.getChildCount() > 0) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(card.getLayoutParams());
                params.addRule(RelativeLayout.BELOW, relativeLayout.getChildAt(relativeLayout.getChildCount() - 1).getId());
                card.setLayoutParams(params);
            }
            //Run on UI thread
            relativeLayout.post(new Runnable() {
                @Override
                public void run() {
                    relativeLayout.addView(card);
                }
            });
            this.proposals.add(proposal);
            if (this.proposals.size() > MAX_ITEMS) {
                //Run on UI thread
                relativeLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        relativeLayout.removeView(relativeLayout.getChildAt(0));
                    }
                });
                this.proposals.removeFirst();
            }
        }

        this.load_index += LOAD_ITEMS;
    }

    public void loadFirst(RelativeLayout relativeLayout) {
        loadAt("-1", relativeLayout);
        return;
    }

    public void loadAt(String start, RelativeLayout relativeLayout) {
        ServerRepository repository = new ServerRepository();
        LoginService loginService = LoginService.getInstance();
        String response = repository.getProposals(loginService.getToken(), start, LOAD_ITEMS);
        if (response == null) {
            return;
        }

        Log.d("CUAK", "loadAt raw text: " + response);
        ListProposalsResponseModel proposals = ListProposalsResponseModel.fromJson(response);
        if (proposals.getEnd().equals("-1")) {
            this.more = false;
        }

        Log.d("CUAK", "loadAt proposal size: " + proposals.getProposals().size());
        for (ProposalModel proposal : proposals.getProposals()) {
            MaterialCardView card = proposal.toMaterialCardView(relativeLayout.getContext());
            card.setId(id++);
            if (relativeLayout.getChildCount() > 0) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(card.getLayoutParams());
                params.addRule(RelativeLayout.BELOW, relativeLayout.getChildAt(relativeLayout.getChildCount() - 1).getId());
                card.setLayoutParams(params);
            }

            //Run on UI thread
            relativeLayout.post(new Runnable() {
                @Override
                public void run() {
                    relativeLayout.addView(card);
                }
            });
            this.proposals.add(proposal);
            if (this.proposals.size() > MAX_ITEMS) {
                //Run on UI thread
                relativeLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        relativeLayout.removeView(relativeLayout.getChildAt(0));
                    }
                });

                this.proposals.removeFirst();
            }
        }

        this.load_index = proposals.getEnd();
    }

    public boolean hasMore() {
        return this.more;
    }
}
