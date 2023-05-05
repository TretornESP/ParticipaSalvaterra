package com.tretornesp.participa;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.tretornesp.participa.controller.ListController;
import com.tretornesp.participa.model.ProposalModel;
import com.tretornesp.participa.model.UserModel;
import com.tretornesp.participa.model.controller.ListLoadControllerModel;
import com.tretornesp.participa.service.ProposalService;
import com.tretornesp.participa.util.Callback;

import org.w3c.dom.Text;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {

    private final Callback likeCallback = new Callback() {
        private void toast(String message) {
            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show());
        }

        @Override
        public void onSuccess(Object data) {
            Boolean liked = (Boolean) data;
            if (liked) {
                toast("Liked");
            } else {
                toast("Unliked");
            }
        }

        @Override
        public void onFailure(String message) {
            toast(message);
        }

        @Override
        public void onLoginRequired() {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container_login, new LoginFragment());
            transaction.commit();
        }
    };

    private final Callback loadListCallback = new Callback() {
        private void toast(String message) {
            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show());
        }

        @Override
        public void onSuccess(Object result) {
            ListLoadControllerModel response = (ListLoadControllerModel) result;
            List<ProposalModel> proposals = response.getProposals();
            List<String> liked_proposals = response.getUser().getLiked_proposals();

            LinearLayout linearLayout = getView().findViewById(R.id.scrollable);

            for (ProposalModel proposal : proposals) {
                Log.d("ListFragment", "Proposal title: " + proposal.getTitle());

                MaterialCardView cardView = new MaterialCardView(linearLayout.getContext());
                cardView.setLayoutParams(new MaterialCardView.LayoutParams(
                        MaterialCardView.LayoutParams.WRAP_CONTENT,
                        MaterialCardView.LayoutParams.WRAP_CONTENT
                ));

                //Inflate the card view
                LayoutInflater inflater = (LayoutInflater) linearLayout.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.list_item, null);
                TextView title = layout.findViewById(R.id.title);
                title.post(() -> title.setText(proposal.getTitle()));
                TextView description = layout.findViewById(R.id.secondary_text);
                description.post(() -> description.setText(proposal.getDescription()));
                ShapeableImageView image = layout.findViewById(R.id.like);
                image.post(() -> {
                    if (liked_proposals.contains(proposal.getId())) {
                        image.setTag("liked");
                        image.setBackground(getContext().getDrawable(R.drawable.ic_baseline_favorite_border_24_red));
                    } else {
                        image.setTag("unliked");
                        image.setBackground(getContext().getDrawable(R.drawable.ic_baseline_favorite_border_24));
                    }
                });
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Cast the view into the original
                        ShapeableImageView image = (ShapeableImageView) v;
                        //Change the color
                        if (image.getTag() == null || image.getTag().equals("unliked")) {
                            image.setTag("liked");
                            image.setBackground(getContext().getDrawable(R.drawable.ic_baseline_favorite_border_24_red));
                        } else {
                            image.setTag("unliked");
                            image.setBackground(getContext().getDrawable(R.drawable.ic_baseline_favorite_border_24));

                        }
                        ListController listController = new ListController();
                        listController.like(proposal.getId(), likeCallback);
                    }
                });

                cardView.addView(layout);
                cardView.setOnClickListener(new View.OnClickListener() {
                    long lastClickTime = 0;

                    @Override
                    public void onClick(View v) {
                        ShapeableImageView image = (ShapeableImageView) v.findViewById(R.id.like);

                        if (System.currentTimeMillis() - lastClickTime < 1000) {
                            if (image.getTag() == null || image.getTag().equals("unliked")) {
                                image.setTag("liked");
                                image.setBackground(getContext().getDrawable(R.drawable.ic_baseline_favorite_border_24_red));
                                ListController listController = new ListController();
                                listController.like(proposal.getId(), likeCallback);
                            }
                        }
                        lastClickTime = System.currentTimeMillis();
                    }
                });

                //Add proposals to the list
                linearLayout.post(() -> linearLayout.addView(cardView));
            }
        }

        @Override
        public void onFailure(String result) {
            toast("No se puede cargar la lista en estos momentos");
        }

        @Override
        public void onLoginRequired() {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container_login, new LoginFragment());
            transaction.commit();
        }
    };

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

        ListController listController = new ListController();
        listController.loadList(loadListCallback);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }
}