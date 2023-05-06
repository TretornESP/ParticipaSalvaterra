package com.tretornesp.participa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.tretornesp.participa.model.CoordinatesModel;
import com.tretornesp.participa.util.CancellableFrame;
import com.tretornesp.participa.util.ImageHandler;
import com.tretornesp.participa.util.LocationHandler;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "mainActivity";
    private static final int DEFAULT_FRAGMENT = R.id.list_item;

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

    private final BottomNavigationView.OnItemSelectedListener itemSelectedListener = item -> {

        Fragment selectedFragment = getSelectedFragment(item.getItemId());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

        return true;
    };

    public void disableBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setEnabled(false);
        bottomNavigationView.setClickable(false);
        bottomNavigationView.setVisibility(NavigationBarView.GONE);
    }

    public void enableBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setEnabled(true);
        bottomNavigationView.setClickable(true);
        bottomNavigationView.setVisibility(NavigationBarView.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(itemSelectedListener);
        bottomNavigationView.setSelectedItemId(MainActivity.DEFAULT_FRAGMENT);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();

        boolean handled = false;
        for (Fragment f: fragmentList) {
            if (f instanceof CancellableFrame) {
                handled = ((CancellableFrame) f).onBackPressed();
            }

            if (handled) {
                break;
            }
        }

        if (!handled) {
            super.onBackPressed();
        }
    }
}