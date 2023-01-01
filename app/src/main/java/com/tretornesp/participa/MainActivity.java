package com.tretornesp.participa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "mainActivity";

    private final BottomNavigationView.OnItemSelectedListener itemSelectedListener = item -> {
        Fragment selectedFragment = null;

        if (item.getItemId() == R.id.list_item) {
            Log.d(MainActivity.TAG, "Navigating to List");
            selectedFragment = new ListFragment();
        } else if (item.getItemId() == R.id.new_item) {
            Log.d(MainActivity.TAG, "Navigating to New");
        } else if (item.getItemId() == R.id.map_item) {
            Log.d(MainActivity.TAG, "Navigating to Map");
            selectedFragment = new MapsFragment();
        } else if (item.getItemId() == R.id.profile_item) {
            Log.d(MainActivity.TAG, "Navigating to Profile");
        }

        if (selectedFragment != null) getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        return true;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(itemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ListFragment()).commit();
    }
}