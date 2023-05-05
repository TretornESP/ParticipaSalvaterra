package com.tretornesp.participa;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.tretornesp.participa.controller.ProposalController;
import com.tretornesp.participa.util.Callback;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class NewFragment extends Fragment {

    private final Callback createProposalCallback = new Callback() {
        @Override
        public void onSuccess(Object data) {

        }

        @Override
        public void onFailure(String message) {

        }

        @Override
        public void onLoginRequired() {

        }
    }

    private final LatLng latLng;

    public NewFragment(LatLng latLng) {
        this.latLng = latLng;
    }

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            googleMap.addMarker(new MarkerOptions().position(latLng).title("Nueva propuesta"));

            CameraPosition camera = CameraPosition.builder().target(latLng).zoom(googleMap.getMaxZoomLevel()-5.0f).build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
            googleMap.getUiSettings().setAllGesturesEnabled(false);
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.minimap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        MaterialButton materialButton = getView().findViewById(R.id.create_button);
        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText title = getView().findViewById(R.id.title_edit_text);
                TextInputEditText description = getView().findViewById(R.id.text_edit_text);
                //TODO: 3:17 am
                ProposalController proposalController = new ProposalController();
                proposalController.createProposal(createProposalCallback);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new, container, false);
    }
}