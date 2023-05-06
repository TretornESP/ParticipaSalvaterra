package com.tretornesp.participa;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
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
        LoginService service = LoginService.getInstance();
        if (service.has(getContext())) {
            getActivity().runOnUiThread(() -> {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            });
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        MaterialTextView materialTextView = getView().findViewById(R.id.register_button);

        materialTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container_login, new RegisterFragment());
                transaction.commit();
            }
        });

        MaterialButton loginButton = getView().findViewById(R.id.cirLoginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginService service = LoginService.getInstance();

                //Get email and password from the form
                TextInputEditText email = getView().findViewById(R.id.editTextEmail);
                TextInputEditText password = getView().findViewById(R.id.editTextPassword);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (service.login(email.getText().toString(), password.getText().toString()) != null) {
                            service.persist(getContext());
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Login successful", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                startActivity(intent);
                            });
                        } else {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Error login", Toast.LENGTH_LONG).show();
                            });
                        }
                    }
                }).start();
            }
        });
    }
}