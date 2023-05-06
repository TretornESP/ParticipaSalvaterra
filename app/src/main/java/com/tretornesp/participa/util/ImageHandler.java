package com.tretornesp.participa.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.tretornesp.participa.PermissionUtils;

import java.io.File;

public class ImageHandler {
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 1;
    private static final String RATIONALE_INFO = "La aplicación no puede acceder a la galeria sin tu permiso.";
    private static final String RATIONALE_ABORT = "No se puede acceder a la galeria sin permisos.";
    private static final String GALLERY_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE;

    private Boolean permissions;
    private final Fragment fragment;
    private String requestPermission;
    private ActivityResultCallback<Uri> callback;
    private final ActivityResultLauncher<String> getContentLauncher;
    private final ActivityResultLauncher<String> requestPermissionLauncher;


    public ImageHandler(Fragment fragment) {
        this.fragment = fragment;
        this.permissions = false;

        int gallery = fragment.getContext().checkCallingOrSelfPermission(GALLERY_PERMISSION);
        this.permissions = (gallery == PackageManager.PERMISSION_GRANTED);
        requestPermission = Manifest.permission.READ_EXTERNAL_STORAGE;

        this.requestPermissionLauncher = fragment.registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                permissions = result;
            }
        });

        this.getContentLauncher = fragment.registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                callback.onActivityResult(result);
            }
        });
    }

    private boolean isGalleryRejected() {
        return fragment.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void requestPermission(String permission) {
        if (!isGalleryRejected()) {
            this.requestPermission = permission;
            requestPermissionLauncher.launch(permission);
        }
    }

    public void requestGalleryPermission() {
        if (!isGalleryEnabled()) {
            requestPermission(GALLERY_PERMISSION);
        }
    }

    public void getFromGallery(ActivityResultCallback<Uri> callback) {
        this.callback = callback;

        if (isGalleryEnabled()) {
            getContentLauncher.launch("image/*");
        } else {
            if (!isGalleryRejected()) {
                requestPermission(GALLERY_PERMISSION);
            } else {
                PermissionUtils.RationaleDialog.newInstance(GALLERY_PERMISSION_REQUEST_CODE, false, RATIONALE_INFO, RATIONALE_ABORT)
                        .show(fragment.getChildFragmentManager(), "dialog");
            }
        }
    }

    public boolean isGalleryEnabled() {
        return Boolean.TRUE.equals(permissions);
    }

    public String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = fragment.getContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) {
            Log.d("NewFragment", "cursor is null");
            return null;
        }
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }
}
