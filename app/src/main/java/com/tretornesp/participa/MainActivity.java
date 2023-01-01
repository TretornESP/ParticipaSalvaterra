package com.tretornesp.participa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "mainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.list_item:
                                Log.d(MainActivity.TAG, "Navigating to List");
                                break;
                            case R.id.new_item:
                                Log.d(MainActivity.TAG, "Navigating to New");
                                break;
                            case R.id.map_item:
                                Log.d(MainActivity.TAG, "Navigating to Map");
                                break;
                            case R.id.profile_item:
                                Log.d(MainActivity.TAG, "Navigating to Profile");
                                break;
                        }
                        return true;
                    }
                }
        );
    }
}