package com.tretornesp.participa.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.tretornesp.participa.model.CoordinatesModel;

import java.util.HashMap;
import java.util.Map;

public class LocationHandler extends Fragment {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String RATIONALE_INFO = "La aplicación no puede obtener tu ubicación sin permisos.";
    private static final String RATIONALE_ABORT = "La aplicación no puede funcionar sin acceso a la ubicación";
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    private final Map<String, Boolean> permissions;
    private final Fragment fragment;
    private String requestPermission;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    public LocationHandler(Fragment fragment) {
        this.fragment = fragment;
        this.permissions = new HashMap<>();
        this.permissions.put(COARSE_LOCATION, false);
        this.permissions.put(FINE_LOCATION, false);

        int coarseLocation = fragment.getContext().checkCallingOrSelfPermission(COARSE_LOCATION);
        int fineLocation = fragment.getContext().checkCallingOrSelfPermission(FINE_LOCATION);

        this.permissions.put(COARSE_LOCATION, coarseLocation == PackageManager.PERMISSION_GRANTED);
        this.permissions.put(FINE_LOCATION, fineLocation == PackageManager.PERMISSION_GRANTED);

        this.requestPermissionLauncher = fragment.registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    permissions.put(requestPermission, true);
                } else {
                    permissions.put(requestPermission, false);
                }
            }
        });
    }

    private boolean isLocationRejected() {
        return (fragment.shouldShowRequestPermissionRationale(FINE_LOCATION) ||
                fragment.shouldShowRequestPermissionRationale(COARSE_LOCATION));
    }

    private void requestPermission(String permission) {
        if (!isLocationRejected()) {
            this.requestPermission = permission;
            requestPermissionLauncher.launch(permission);
        }
    }

    public void requestLocationPermission() {
        if (Boolean.FALSE.equals(permissions.get(FINE_LOCATION)))
            requestPermission(FINE_LOCATION);
        if (Boolean.FALSE.equals(permissions.get(COARSE_LOCATION)))
            requestPermission(COARSE_LOCATION);
    }

    public CoordinatesModel getCurrentLocation() {
        if (isLocationEnabled()) {
            LocationManager locationManager = (LocationManager) fragment.getActivity().getSystemService(Context.LOCATION_SERVICE);
            @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            return CoordinatesModel.fromLatLng(location.getLatitude(), location.getLongitude());
        } else {
            if (!isLocationRejected()) {
                requestPermission(FINE_LOCATION);
                requestPermission(COARSE_LOCATION);
            } else {
                PermissionUtils.RationaleDialog.newInstance(LOCATION_PERMISSION_REQUEST_CODE, false, RATIONALE_INFO, RATIONALE_ABORT).show(fragment.getChildFragmentManager(), "dialog");
            }
        }
        return null;
    }

    public boolean isLocationEnabled() {
        return Boolean.TRUE.equals(permissions.get(COARSE_LOCATION)) || Boolean.TRUE.equals(permissions.get(FINE_LOCATION));
    }
}
