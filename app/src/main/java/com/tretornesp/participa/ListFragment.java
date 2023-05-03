package com.tretornesp.participa;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.tretornesp.participa.model.ProposalModel;
import com.tretornesp.participa.service.ProposalService;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //Whenever the user sees the fragment

    @Override
    public void onResume() {
        super.onResume();
        ProposalService service = ProposalService.getInstance();
        RelativeLayout relativeLayout = getView().findViewById(R.id.scrollable);

        new Thread(new Runnable() {
            @Override
            public void run() {
                service.loadFirst(relativeLayout);
            }

        }).start();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }
}