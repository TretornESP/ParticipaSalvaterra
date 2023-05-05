package com.tretornesp.participa;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tretornesp.participa.controller.ProfileController;
import com.tretornesp.participa.model.CredentialsModel;
import com.tretornesp.participa.model.UserModel;
import com.tretornesp.participa.service.LoginService;
import com.tretornesp.participa.util.Callback;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private final Callback loadProfileCallback = new Callback() {

        private void toast(String message) {
            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show());
        }

        @Override
        public void onSuccess(Object result) {
            UserModel user = (UserModel) result;

            TextView name = getView().findViewById(R.id.user_profile_name);
            name.post(() -> name.setText(user.getName()));
            Log.d("ProfileFragment", "User name: " + user.getName());
            Log.d("ProfileFragment", "User field name: " + name.getText());
        }

        @Override
        public void onFailure(String result) {
            toast("No se puede cargar el perfil en estos momentos");
        }

        @Override
        public void onLoginRequired() {

        }
    };

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        //If the user is not logged in, redirect to the login page.
        ProfileController profileController = new ProfileController();
        profileController.loadProfile(loadProfileCallback);
    }

}