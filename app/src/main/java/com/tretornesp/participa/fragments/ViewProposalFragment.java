package com.tretornesp.participa.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tretornesp.participa.MainActivity;
import com.tretornesp.participa.R;
import com.tretornesp.participa.controller.ProfileController;
import com.tretornesp.participa.controller.ProposalController;
import com.tretornesp.participa.model.ProposalModel;
import com.tretornesp.participa.util.Callback;
import com.tretornesp.participa.util.ImageHandler;

public class ViewProposalFragment extends Fragment {
    private String current_pid = null;

    private final Callback loadProposalCallback = new Callback() {
        private void toast(String message) {
            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show());
        }

        @Override
        public void onSuccess(Object result) {
            ProposalModel proposal = (ProposalModel) result;
            Log.d("ViewProposalFragment", proposal.toJson());
        }

        @Override
        public void onFailure(String message) {
            toast(message);
        }

        @Override
        public void onLoginRequired() {((MainActivity) getActivity()).showLogin();}
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        ProposalController proposalController = new ProposalController();
        proposalController.load(current_pid, loadProposalCallback);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null)
            current_pid = getArguments().getString("targetPid");

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_viewproposal, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        ProposalController proposalController = new ProposalController();
        proposalController.load(current_pid, loadProposalCallback);
    }
}
