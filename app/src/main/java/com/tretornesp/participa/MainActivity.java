package com.tretornesp.participa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.tretornesp.participa.fragments.ListFragment;
import com.tretornesp.participa.fragments.LoginFragment;
import com.tretornesp.participa.fragments.MapsFragment;
import com.tretornesp.participa.fragments.NewFragment;
import com.tretornesp.participa.fragments.ProfileFragment;
import com.tretornesp.participa.fragments.RegisterFragment;
import com.tretornesp.participa.fragments.ViewProposalFragment;
import com.tretornesp.participa.util.CancellableFrame;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "mainActivity";
    private static final int DEFAULT_FRAGMENT = R.id.list_item;
    private Fragment lastFragment;

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
        navigate(selectedFragment);
        return true;
    };

    public void disableBottomNavigation() {
        Log.d(MainActivity.TAG, "Disabling bottom navigation");
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.post(() -> {
            bottomNavigationView.setEnabled(false);
            bottomNavigationView.setClickable(false);
            bottomNavigationView.setVisibility(NavigationBarView.GONE);
        });
    }

    public void enableBottomNavigation() {
        Log.d(MainActivity.TAG, "Enabling bottom navigation");
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.post(() -> {
            bottomNavigationView.setEnabled(true);
            bottomNavigationView.setClickable(true);
            bottomNavigationView.setVisibility(NavigationBarView.VISIBLE);
        });
    }

    private void navigate(Fragment fragment) {
        lastFragment = fragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    private void navigateNoret(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    public void ret() {
        if (lastFragment == null) {
            Log.d(MainActivity.TAG, "Last fragment is null, defaulting to ListFragment");
            lastFragment = new ListFragment();
        }
        if (lastFragment instanceof ListFragment) {
            Log.d(MainActivity.TAG, "Returning to ListFragment");
        }
        if (lastFragment instanceof MapsFragment) {
            Log.d(MainActivity.TAG, "Returning to MapsFragment");
        }
        if (lastFragment instanceof ProfileFragment) {
            Log.d(MainActivity.TAG, "Returning to ProfileFragment");
        }
        if (lastFragment instanceof NewFragment) {
            Log.d(MainActivity.TAG, "Returning to NewFragment");
        }
        if (lastFragment instanceof LoginFragment) {
            Log.d(MainActivity.TAG, "Returning to LoginFragment");
        }
        if (lastFragment instanceof RegisterFragment) {
            Log.d(MainActivity.TAG, "Returning to RegisterFragment");
        }

        enableBottomNavigation();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, lastFragment).commit();
    }

    public void showNewProposal(LatLng latLng) {
        Log.d(MainActivity.TAG, "Showing new proposal");
        disableBottomNavigation();
        Fragment fragment = new NewFragment(latLng);
        navigate(fragment);
    }

    public void showMap() {
        Log.d(MainActivity.TAG, "Showing map");
        enableBottomNavigation();
        Fragment fragment = new MapsFragment();
        navigate(fragment);
    }

    public void showLogin() {
        Log.d(MainActivity.TAG, "Showing login");
        disableBottomNavigation();
        Fragment fragment = new LoginFragment();
        navigateNoret(fragment);
    }

    public void showRegister() {
        Log.d(MainActivity.TAG, "Showing register");
        disableBottomNavigation();
        Fragment fragment = new RegisterFragment();
        navigateNoret(fragment);
    }

    public void showItem(String item) {
        Log.d(MainActivity.TAG, "Showing item: " + item);
        enableBottomNavigation();
        Bundle bundle = new Bundle();
        bundle.putString("targetPid", item);
        Fragment fragment = new ViewProposalFragment();
        fragment.setArguments(bundle);
        navigate(fragment);
    }

    public void showList() {
        Log.d(MainActivity.TAG, "Showing list");
        enableBottomNavigation();
        Fragment fragment = new ListFragment();
        navigate(fragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(itemSelectedListener);
        bottomNavigationView.setSelectedItemId(MainActivity.DEFAULT_FRAGMENT);

        showList();
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