package com.tretornesp.participa.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tretornesp.participa.CustomInfoWindowAdapter;
import com.tretornesp.participa.R;
import com.tretornesp.participa.model.ProposalModel;

public class Notification{

    public static class NotificationDialog extends DialogFragment {

        private Dialog customDialog = null;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return customDialog;
        }

        private NotificationDialog(FragmentActivity activity, String title, String text) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(title);
            builder.setMessage(text)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            customDialog.dismiss();
                        }
                    })
                    .setCancelable(true)
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            customDialog.dismiss();
                        }
                    });
            customDialog = builder.create();
        }

        @Override
        public void show(@NonNull FragmentManager manager, @Nullable String tag) {
            if (customDialog == null) {
                throw new NullPointerException("Dialog cannot be null");
            } else {
                super.show(manager, tag);
            }
        }
    }

    public static void show(FragmentActivity activity, String title, String text) {
        Handler handler = new Handler(activity.getApplication().getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                new NotificationDialog(activity, title, text).show(activity.getSupportFragmentManager(), "Notification");
            }
        });
    }
}