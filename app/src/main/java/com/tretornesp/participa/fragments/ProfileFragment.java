package com.tretornesp.participa.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.tretornesp.participa.MainActivity;
import com.tretornesp.participa.R;
import com.tretornesp.participa.controller.ProfileController;
import com.tretornesp.participa.controller.ProposalController;
import com.tretornesp.participa.model.CredentialsModel;
import com.tretornesp.participa.model.UserModel;
import com.tretornesp.participa.service.LoginService;
import com.tretornesp.participa.util.Callback;
import com.tretornesp.participa.util.ImageHandler;
import com.tretornesp.participa.util.ImageTranslator;
import com.tretornesp.participa.util.Notification;
import com.tretornesp.participa.util.UrlLauncher;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private ImageHandler imageHandler;

    private final Callback loadProfileCallback = new Callback() {

        private void toast(String message) {
            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show());
        }

        @Override
        public void onSuccess(Object result) {
            UserModel user = (UserModel) result;

            TextView name = getView().findViewById(R.id.user_profile_name);
            name.post(() -> name.setText(user.getName()));
            ShapeableImageView image = getView().findViewById(R.id.user_profile_photo);
            image.post(() -> Glide.with(getContext()).load(user.getPhoto()).into(image));
            TextView email = getView().findViewById(R.id.user_profile_visibility);
            email.post(() -> email.setText(user.isIspublic() ? "perfil publico" : "perfil privado"));

        }

        @Override
        public void onFailure(String result) {
            toast("No se puede cargar el perfil en estos momentos");
        }

        @Override
        public void onLoginRequired() {
            ((MainActivity) getActivity()).showLogin();
        }
    };

    private final Callback logoutCallback = new Callback() {

        private void toast(String message) {
            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show());
        }

        @Override
        public void onSuccess(Object result) {
            ((MainActivity)getActivity()).showLogin();
        }

        @Override
        public void onFailure(String result) {
            toast("No se puede cerrar sesion");
        }

        @Override
        public void onLoginRequired() {
            ((MainActivity) getActivity()).showLogin();
        }
    };

    private final Callback changeImage = new Callback() {
        private void toast(String message) {
            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show());
        }

        @Override
        public void onSuccess(Object data) {
            String url = (String) data;
            ShapeableImageView image = getView().findViewById(R.id.user_profile_photo);
            image.post(() -> Glide.with(getContext()).load(url).into(image));
        }

        @Override
        public void onFailure(String message) {
            toast("No se puede cambiar la imagen");
        }

        @Override
        public void onLoginRequired() {
            ((MainActivity) getActivity()).showLogin();
        }
    };

    private final Callback changeNameCallback = new Callback() {
        private void toast(String message) {
            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show());
        }

        @Override
        public void onSuccess(Object data) {
            String name = (String) data;
            TextView nameView = getView().findViewById(R.id.user_profile_name);
            nameView.post(() -> nameView.setText(name));
        }

        @Override
        public void onFailure(String message) {
            toast("No se puede cambiar el nombre");
        }

        @Override
        public void onLoginRequired() {
            ((MainActivity) getActivity()).showLogin();
        }
    };

    private final Callback changeName = new Callback() {
        private void toast(String message) {
            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show());
        }

        @Override
        public void onSuccess(Object data) {
            String name = (String) data;
            ProfileController profileController = new ProfileController();
            profileController.updateName(name, changeNameCallback);
        }

        @Override
        public void onFailure(String message) {
            toast("No se puede cambiar el nombre");
        }

        @Override
        public void onLoginRequired() {
            ((MainActivity) getActivity()).showLogin();
        }
    };

    private final Callback changePasswordCallback = new Callback() {
        private void toast(String message) {
            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show());
        }

        @Override
        public void onSuccess(Object data) {
            toast("Contraseña cambiada correctamente");
        }

        @Override
        public void onFailure(String message) {
            toast("No se puede cambiar la contraseña");
        }

        @Override
        public void onLoginRequired() {
            ((MainActivity) getActivity()).showLogin();
        }
    };

    private final Callback changePassword = new Callback() {
        private void toast(String message) {
            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show());
        }

        @Override
        public void onSuccess(Object data) {
            String pass = (String) data;
            ProfileController profileController = new ProfileController();
            profileController.updatePassword(pass, changePasswordCallback);
        }

        @Override
        public void onFailure(String message) {
            toast("No se puede cambiar la contraseña");
        }

        @Override
        public void onLoginRequired() {
            ((MainActivity) getActivity()).showLogin();
        }
    };

    private final Callback logoutConfirmCallback = new Callback() {
        @Override
        public void onSuccess(Object data) {
            ProfileController profileController = new ProfileController();
            profileController.logout(logoutCallback);
        }

        @Override
        public void onFailure(String message) {

        }

        @Override
        public void onLoginRequired() {
            ((MainActivity) getActivity()).showLogin();
        }
    };

    ActivityResultCallback<Uri> imageCallback = new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            if (result == null) return;
            String path = ImageTranslator.getImagePath(getContext(), result);
            if (path == null) {
                return;
            }
            ProfileController profileController = new ProfileController();
            profileController.updateImage(new File(path), changeImage);
        }
    };

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        imageHandler = new ImageHandler(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        ProfileController profileController = new ProfileController();
        profileController.loadProfile(loadProfileCallback);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Set the click listener for the logout button.
        getView().findViewById(R.id.logout_button).setOnClickListener(v -> {
            Notification.actOnYes(getActivity(), "Cerrar sesión", "¿Esta seguro de que desea cerrar la sesión?", logoutConfirmCallback);
        });
        getView().findViewById(R.id.help_button).setOnClickListener(v -> {
            UrlLauncher.open(UrlLauncher.HELP_URL, getActivity());
        });
        getView().findViewById(R.id.report_proposal_button).setOnClickListener(v -> {
            UrlLauncher.open(UrlLauncher.REPORT_URL, getActivity());
        });
        getView().findViewById(R.id.user_profile_photo).setOnClickListener(v -> {
            imageHandler.getFromGallery(imageCallback);
        });
        getView().findViewById(R.id.user_profile_name).setOnClickListener(v -> {
            Notification.input(getActivity(), "Cambiar nombre", "Introduce tu nuevo nombre", changeName);
        });
        getView().findViewById(R.id.change_password_button).setOnClickListener(v -> {
            Notification.requestPassword(getActivity(), "Cambiar contraseña", "Introduce una nueva contraseña", changePassword);
        });
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