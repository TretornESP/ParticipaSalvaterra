package com.tretornesp.participa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

public class MapsFragment extends Fragment {

    private static final String TAG = "MapsFragment";

    private final boolean permissionsGranted = false;
    private Marker currentMarker = null;
    private boolean infoDisplayed = false;
    private boolean uiLocked = false;

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        final DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new NewFragment(currentMarker.getPosition())).commit();
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


            LatLng home = new LatLng(42.087637, -8.501553);
            googleMap.addMarker(new MarkerOptions().position(home).title("Salvaterra"));

            CameraPosition camera = CameraPosition.builder().target(home).zoom(googleMap.getMaxZoomLevel()-5.0f).build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
            googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(LayoutInflater.from(getActivity())));

            LatLngBounds SALVATERRA = new LatLngBounds(
                    new LatLng(42.052705, -8.557369),
                    new LatLng(42.188511, -8.390883)
            );

            googleMap.setLatLngBoundsForCameraTarget(SALVATERRA);

            if (MainActivity.permissionsGranted) {
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
                Toast.makeText(getContext(), "Marker: " + markername, Toast.LENGTH_SHORT).show();
                infoDisplayed = true;
                return false;
            });

            googleMap.setOnInfoWindowClickListener(marker -> {
                String markername = marker.getTitle();
                Toast.makeText(getContext(), "Clicked info: " + markername, Toast.LENGTH_SHORT).show();
            });

            //Kind of nasty!
            googleMap.setOnInfoWindowCloseListener(marker -> {
                final Handler handler = new Handler();
                handler.postDelayed(() -> infoDisplayed = false, 50);
            });
        }
    };


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