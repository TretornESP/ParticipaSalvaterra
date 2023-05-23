package com.tretornesp.participa.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tretornesp.participa.util.CustomInfoWindowAdapter;
import com.tretornesp.participa.MainActivity;
import com.tretornesp.participa.R;
import com.tretornesp.participa.controller.ListController;
import com.tretornesp.participa.model.ProposalModel;
import com.tretornesp.participa.model.UserModel;
import com.tretornesp.participa.model.controller.ListLoadControllerModel;
import com.tretornesp.participa.model.controller.SubstitutionProposalImageControllerModel;
import com.tretornesp.participa.util.Callback;
import com.tretornesp.participa.util.LocationHandler;

import java.util.List;

public class MapsFragment extends Fragment {

    private static final String TAG = "MapsFragment";

    private GoogleMap currentMap = null;
    private Marker currentMarker = null;
    private boolean infoDisplayed = false;
    private boolean uiLocked = false;
    private LocationHandler locationHandler;
    private List<ProposalModel> proposals;

    private final Callback loadSignedImageCallback = new Callback() {
        @Override
        public void onSuccess(Object data) {
            SubstitutionProposalImageControllerModel response = (SubstitutionProposalImageControllerModel) data;
            if (proposals == null) return;
            for (ProposalModel proposal : proposals) {
                if (proposal.getMain_photo().equals(response.getUrl())) {
                    proposal.setMain_photo(response.getSigned());
                    Glide.with(getContext()).load(response.getSigned()).preload();
                    break;
                }
                for (String photo : proposal.getPhotos()) {
                    if (photo.equals(response.getUrl())) {
                        proposal.setPhoto(photo, response.getSigned());
                        Glide.with(getContext()).load(response.getSigned()).preload();
                        break;
                    }
                }
            }
        }

        @Override
        public void onFailure(String message) {

        }

        @Override
        public void onLoginRequired() {

        }
    };

    private final Callback loadListCallback = new Callback() {
        private void toast(String message) {
            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show());
        }

        @Override
        public void onSuccess(Object result) {
            ListController listController = new ListController();

            ListLoadControllerModel response = (ListLoadControllerModel) result;
            proposals = response.getProposals();
            UserModel currentUser = response.getUser();
            List<String> liked_proposals = response.getUser().getLiked_proposals();

            LinearLayout linearLayout = getView().findViewById(R.id.scrollable);

            if (proposals == null) return;
            Handler handler = new Handler(getActivity().getApplication().getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    for (ProposalModel proposal : proposals) {
                        Log.d("ListFragment", "Proposal title: " + proposal.getTitle());
                        if (!proposal.getMain_photo().contains("http"))
                            listController.substituteImage(proposal.getMain_photo(), loadSignedImageCallback);
                        LatLng latLng = new LatLng(proposal.getCoordinates().getLat(), proposal.getCoordinates().getLng());
                        BitmapDescriptor bitmapDescriptorFactory;

                        if (currentUser.getUid().equals(proposal.getAuthor())) {
                            bitmapDescriptorFactory = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                        } else {
                            if (liked_proposals.contains(proposal.getId())) {
                                bitmapDescriptorFactory = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                            } else {
                                bitmapDescriptorFactory = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
                            }
                        }

                        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(proposal.getId()).icon(bitmapDescriptorFactory);
                        currentMap.addMarker(markerOptions);
                    }
                    currentMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(LayoutInflater.from(getActivity()), proposals));

                }
            });
        }

        @Override
        public void onFailure(String result) {
            toast("No se puede cargar la lista en estos momentos");
        }

        @Override
        public void onLoginRequired() {
            ((MainActivity)getActivity()).showLogin();
        }
    };


        private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        final DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {

            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Disable bottom_navigation
                    ((MainActivity) getActivity()).showNewProposal(currentMarker.getPosition());
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    if (currentMarker != null)
                        currentMarker.remove();
                    break;
            }
            unlock();
        };

        private void lock() {
            uiLocked = true;
        }

        private void unlock() {
            uiLocked = false;
        }

        private void newProposal(GoogleMap googleMap, LatLng latLng) {
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("cuak_empty_marker"); //Nasty hack!!
            currentMarker = googleMap.addMarker(markerOptions);
            Log.d(TAG, "Latitude: " + latLng.latitude + " longitude: " + latLng.longitude);
            CameraPosition camera = CameraPosition.builder().target(latLng).zoom(googleMap.getMaxZoomLevel()-5.0f).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));

            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(R.string.new_proposal_confirmation).setPositiveButton(R.string.yes, dialogClickListener)
                        .setNegativeButton(R.string.no, dialogClickListener).setCancelable(false).show();
            }, 1000);
        }

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
        @SuppressLint("MissingPermission")
        public void onMapReady(@NonNull GoogleMap googleMap) {
            currentMap = googleMap;
            ListController listController = new ListController();

            LatLng home = new LatLng(42.087637, -8.501553);
            googleMap.addMarker(new MarkerOptions().position(home).title("Salvaterra"));

            CameraPosition camera = CameraPosition.builder().target(home).zoom(googleMap.getMaxZoomLevel()-7.0f).build();

            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camera));

            LatLngBounds SALVATERRA = new LatLngBounds(
                    new LatLng(42.052705, -8.557369),
                    new LatLng(42.188511, -8.390883)
            );

            googleMap.setLatLngBoundsForCameraTarget(SALVATERRA);

                        //We are calling this only to force requesting permissions

            if (locationHandler.isLocationEnabled()) {
                googleMap.setMyLocationEnabled(true);
                googleMap.setOnMyLocationButtonClickListener(() -> {
                    // Return false so that we don't consume the event and the default behavior still occurs
                    // (the camera animates to the user's current position).
                    return false;
                });
                googleMap.setOnMyLocationClickListener(location -> {
                    if (infoDisplayed || uiLocked) {
                        return;
                    }

                    lock();
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    if (!SALVATERRA.contains(latLng)) {
                        Toast.makeText(getContext(), R.string.out_of_bounds, Toast.LENGTH_LONG).show();
                        unlock();
                        return;
                    }

                    newProposal(googleMap, latLng);

                });
            }

            googleMap.setOnMapClickListener(latLng -> {

                if (infoDisplayed || uiLocked) {
                    return;
                }

                lock();

                if (!SALVATERRA.contains(latLng)) {
                    Toast.makeText(getContext(), R.string.out_of_bounds, Toast.LENGTH_LONG).show();
                    unlock();
                    return;
                }

                newProposal(googleMap, latLng);
            });

            googleMap.setOnMarkerClickListener(marker -> {
                String markername = marker.getTitle();
                //Toast.makeText(getContext(), "Marker: " + markername, Toast.LENGTH_SHORT).show();
                infoDisplayed = true;
                return false;
            });

            googleMap.setOnInfoWindowClickListener(marker -> {
                String markername = marker.getTitle();
                //Toast.makeText(getContext(), "Clicked info: " + markername, Toast.LENGTH_SHORT).show();
            });

            //Kind of nasty!
            googleMap.setOnInfoWindowCloseListener(marker -> {
                final Handler handler = new Handler();
                handler.postDelayed(() -> infoDisplayed = false, 50);
            });

            listController.loadList(loadListCallback);
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationHandler = new LocationHandler(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        locationHandler.requestLocationPermission();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

}