package com.tretornesp.participa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "mainActivity";
    private static final int DEFAULT_FRAGMENT = R.id.list_item;
    public static boolean permissionsGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private Fragment getSelectedFragment(int id) {

        if (id == R.id.list_item) {
            return new ListFragment();
        } else if (id == R.id.map_item) {
            return new MapsFragment();
        } else if (id == R.id.profile_item) {
            return new ProfileFragment();
        } else {
            Log.d(MainActivity.TAG, "Invalid id, defaulting to List");
            return new ListFragment();
        }
    }

    private void restoreNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(PreferenceManager.getDefaultSharedPreferences(this).getInt("selectedPosition", MainActivity.DEFAULT_FRAGMENT));
    }

    private final BottomNavigationView.OnItemSelectedListener itemSelectedListener = item -> {

        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt("selectedPosition", item.getItemId()).commit();
        Fragment selectedFragment = getSelectedFragment(item.getItemId());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

        return true;
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION) || PermissionUtils
                .isPermissionGranted(permissions, grantResults,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            permissionsGranted = true;
        } else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            permissionsGranted = false;
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("rejectedLocation", true).apply();
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (!permissionsGranted) {
            boolean alreadyRejected = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("rejectedLocation", false);
            if (!alreadyRejected)
                PermissionUtils.requestLocationPermissions(this, LOCATION_PERMISSION_REQUEST_CODE, true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("rejectedLocation", false).apply(); //NASTY HACK
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(itemSelectedListener);

        String coarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION;
        String fineLocation = Manifest.permission.ACCESS_FINE_LOCATION;

        int coarseRes = this.checkCallingOrSelfPermission(coarseLocation);
        int fineRes = this.checkCallingOrSelfPermission(fineLocation);

        if (coarseRes == PackageManager.PERMISSION_GRANTED && fineRes == PackageManager.PERMISSION_GRANTED)
            MainActivity.permissionsGranted = true;
        else
            MainActivity.permissionsGranted = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        restoreNavigation();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt("selectedPosition", MainActivity.DEFAULT_FRAGMENT).apply();
        restoreNavigation();
    }
}