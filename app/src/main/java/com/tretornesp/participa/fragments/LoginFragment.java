package com.tretornesp.participa.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.tretornesp.participa.MainActivity;
import com.tretornesp.participa.R;
import com.tretornesp.participa.model.CredentialsModel;
import com.tretornesp.participa.service.LoginService;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void checkToken(CredentialsModel credentialsModel) {
        Log.d("mainActivity", "Checking token...");

        LoginService service = LoginService.getInstance();
        if (service.validateToken(credentialsModel.getToken())) {
            service.persist(getContext());
            service.use(getContext());
            ((MainActivity)getActivity()).ret();
        } else {
            Log.d("mainActivity", "Token didnt work...");
        }
    }

    private void checkStored() {
        LoginService service = LoginService.getInstance();
        if (service.has(getContext())) {
            CredentialsModel credentialsModel = service.load(getContext());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    checkToken(credentialsModel);
                }
            }).start();
        }
    }

    private void checkLogin(String user, String password) {
        LoginService service = LoginService.getInstance();
        Log.d("mainActivity", "Checking login...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                CredentialsModel credentialsModel = service.login(user, password);
                if (credentialsModel != null) {
                    Log.d("mainActivity", "Login successfull");
                    checkToken(credentialsModel);
                } else {
                    Log.d("mainActivity", "Login failed");
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Credenciales invalidas", Toast.LENGTH_LONG).show();
                    });
                }
            }
        }).start();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        MaterialTextView materialTextView = getView().findViewById(R.id.register_button);

        materialTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).showRegister();
            }
        });

        MaterialButton loginButton = getView().findViewById(R.id.cirLoginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Get email and password from the form
                TextInputEditText email = getView().findViewById(R.id.editTextEmail);
                TextInputEditText password = getView().findViewById(R.id.editTextPassword);

                checkLogin(email.getText().toString(), password.getText().toString());
            }
        });

        checkStored();
    }
}