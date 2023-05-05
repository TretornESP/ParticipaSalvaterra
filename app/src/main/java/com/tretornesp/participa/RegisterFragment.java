package com.tretornesp.participa;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.tretornesp.participa.controller.RegisterController;
import com.tretornesp.participa.model.UserModel;
import com.tretornesp.participa.util.Callback;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    private final Callback registerCallback = new Callback() {

        private void toast(String message) {
            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show());
        }

        @Override
        public void onSuccess (Object data) {
            Log.d("RegisterFragment", "Registered user: " + ((UserModel)data).getUid());
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container_login, new LoginFragment());
            transaction.commit();
        }

        @Override
        public void onFailure (String error) {
            toast(error);
        }

        @Override
        public void onLoginRequired () {

        }
    };

    final DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
        switch (which){
            case DialogInterface.BUTTON_POSITIVE: {
                String email = ((TextView) getView().findViewById(R.id.email)).getText().toString();
                String password = ((TextView) getView().findViewById(R.id.password)).getText().toString();
                String passwordConfirmation = ((TextView) getView().findViewById(R.id.password_confirmation)).getText().toString();
                String name = ((TextView) getView().findViewById(R.id.name)).getText().toString();
                String dni = ((TextView) getView().findViewById(R.id.dni)).getText().toString();
                RadioGroup radioGroup = getView().findViewById(R.id.radio_group);
                int selectedId = radioGroup.getCheckedRadioButtonId();
                boolean isPublic = selectedId == R.id.public_selected;

                new RegisterController().register(
                        name,
                        email,
                        password,
                        dni,
                        passwordConfirmation,
                        isPublic,
                        registerCallback
                );
                break;
            }
            case DialogInterface.BUTTON_NEGATIVE:
                //Nothing
                break;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        MaterialTextView materialTextView = getView().findViewById(R.id.login_button);

        materialTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container_login, new LoginFragment());
                transaction.commit();
            }
        });

        MaterialButton materialButton = getView().findViewById(R.id.register_action);
        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.eula_title);
                builder.setMessage(R.string.register_confirmation);
                builder.setPositiveButton(R.string.accept, dialogClickListener);
                builder.setNegativeButton(R.string.decline, dialogClickListener);
                builder.setCancelable(false);

                AlertDialog dialog = builder.create();
                dialog.show();

                TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                textView.setMovementMethod(LinkMovementMethod.getInstance());

            }
        });
    }
}