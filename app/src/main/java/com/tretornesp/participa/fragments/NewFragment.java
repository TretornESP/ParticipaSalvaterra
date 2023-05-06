package com.tretornesp.participa.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.tretornesp.participa.MainActivity;
import com.tretornesp.participa.R;
import com.tretornesp.participa.controller.ProposalController;
import com.tretornesp.participa.model.ProposalModel;
import com.tretornesp.participa.util.Callback;
import com.tretornesp.participa.util.CancellableFrame;
import com.tretornesp.participa.util.ImageHandler;
import com.tretornesp.participa.util.ImageTranslator;
import com.tretornesp.participa.util.Notification;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class NewFragment extends CancellableFrame {

    private final LatLng latLng;
    private ImageHandler imageHandler;
    private File mainImage = null;
    private File image1 = null;
    private File image2 = null;
    private File image3 = null;

    public NewFragment(LatLng latLng) {
        this.latLng = latLng;
    }

    final DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {

        switch (which){
            case DialogInterface.BUTTON_POSITIVE:
                ((MainActivity)getActivity()).showMap();
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                break;
        }
    };
    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            googleMap.addMarker(new MarkerOptions().position(latLng).title("Nueva propuesta"));

            CameraPosition camera = CameraPosition.builder().target(latLng).zoom(googleMap.getMaxZoomLevel()-5.0f).build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
            googleMap.getUiSettings().setAllGesturesEnabled(false);
        }
    };
    private final Callback createCallback = new Callback() {

        private void toast(String message) {
            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show());
        }

        @Override
        public void onSuccess (Object data) {
            Log.d("NewFragment", "Created proposal: " + ((ProposalModel)data).getId());
            toast("Propuesta creada");
            MaterialButton button = getActivity().findViewById(R.id.create_button);
            button.post(() -> {
                button.setEnabled(true);
                button.setClickable(true);
            });

            ((MainActivity)getActivity()).showMap();
        }

        @Override
        public void onFailure (String error) {
            Log.d("NewFragment", "Failed to create proposal: " + error);
            Notification.show(getActivity(), "Propuesta no creada", error);
            MaterialButton button = getActivity().findViewById(R.id.create_button);
            button.post(() -> {
                button.setEnabled(true);
                button.setClickable(true);
            });
        }

        @Override
        public void onLoginRequired () {
            MaterialButton button = getActivity().findViewById(R.id.create_button);
            button.post(() -> {
                button.setEnabled(true);
                button.setClickable(true);
            });

            Log.d("NewFragment", "Login required");
            ((MainActivity)getActivity()).showLogin();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        imageHandler.requestGalleryPermission();
    }

    @Override
    public boolean onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.exit_edit).setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener).setCancelable(false).show();

        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageHandler = new ImageHandler(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.minimap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        MaterialButton materialButton = getView().findViewById(R.id.create_button);
        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MaterialButton materialButton = (MaterialButton) view;
                materialButton.setEnabled(false);
                materialButton.setClickable(false);

                TextInputEditText title = getView().findViewById(R.id.title_edit_text);
                TextInputEditText description = getView().findViewById(R.id.text_edit_text);
                List<File> images = new ArrayList<>();
                if (image1 != null) images.add(image1);
                if (image2 != null) images.add(image2);
                if (image3 != null) images.add(image3);

                Log.d("NewFragment", "onClick: " + title.getText().toString() + " " + description.getText().toString() + " " + images.size() + " " + latLng.latitude + " " + latLng.longitude);
                ProposalController proposalController = new ProposalController();
                proposalController.create(
                        title.getText().toString(),
                        description.getText().toString(),
                        mainImage,
                        images,
                        latLng.latitude,
                        latLng.longitude,
                        createCallback
                );
            }
        });

        ActivityResultCallback<Uri> image1Callback = new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result == null) return;
                Log.d("NewFragment", "main_image onActivityResult: " + result.toString());
                String path = ImageTranslator.getImagePath(getContext(), result);
                if (path == null) {
                    Log.d("NewFragment", "main_image onActivityResult: path is null");
                    return;
                }
                Log.d("NewFragment", "main_image onActivityResult: path: " + path);
                ImageView imageView = getView().findViewById(R.id.image_1_1);
                Glide.with(getContext()).load(path).into(imageView);
                mainImage = new File(path);
            }
        };

        ActivityResultCallback<Uri> image2Callback = new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result == null) return;
                Log.d("NewFragment", "im1 onActivityResult: " + result.toString());
                String path = ImageTranslator.getImagePath(getContext(), result);
                if (path == null) {
                    Log.d("NewFragment", "im1 onActivityResult: path is null");
                    return;
                }
                Log.d("NewFragment", "im1 onActivityResult: path: " + path);
                ImageView imageView = getView().findViewById(R.id.image_1_2);
                Glide.with(getContext()).load(path).into(imageView);
                image1 = new File(path);
            }
        };


        ActivityResultCallback<Uri> image3Callback = new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result == null) return;
                Log.d("NewFragment", "im2 onActivityResult: " + result.toString());
                String path = ImageTranslator.getImagePath(getContext(), result);
                if (path == null) {
                    Log.d("NewFragment", "im2 onActivityResult: path is null");
                    return;
                }
                Log.d("NewFragment", "im2 onActivityResult: path: " + path);
                ImageView imageView = getView().findViewById(R.id.image_1_3);
                Glide.with(getContext()).load(path).into(imageView);
                image2 = new File(path);
            }
        };


        ActivityResultCallback<Uri> image4Callback = new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result == null) return;
                Log.d("NewFragment", "im3 onActivityResult: " + result.toString());
                String path = ImageTranslator.getImagePath(getContext(), result);
                if (path == null) {
                    Log.d("NewFragment", "im3 onActivityResult: path is null");
                    return;
                }
                Log.d("NewFragment", "im3 onActivityResult: path: " + path);
                ImageView imageView = getView().findViewById(R.id.image_1_4);
                Glide.with(getContext()).load(path).into(imageView);
                image3 = new File(path);
            }
        };


        ShapeableImageView image1 = getView().findViewById(R.id.image_1_1);
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageHandler.getFromGallery(image1Callback);
            }
        });

        ShapeableImageView image2 = getView().findViewById(R.id.image_1_2);
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageHandler.getFromGallery(image2Callback);
            }
        });

        ShapeableImageView image3 = getView().findViewById(R.id.image_1_3);
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageHandler.getFromGallery(image3Callback);
            }
        });

        ShapeableImageView image4 = getView().findViewById(R.id.image_1_4);
        image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageHandler.getFromGallery(image4Callback);
            }
        });
    }

    @Override
    public void onDestroy() {
        Log.d("NewFragment", "onDestroy");
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new, container, false);
    }
}