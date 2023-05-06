package com.tretornesp.participa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textview.MaterialTextView;
import com.tretornesp.participa.util.Callback;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Fragment selectedFragment = new LoginFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_login, selectedFragment).commit();

    }
}