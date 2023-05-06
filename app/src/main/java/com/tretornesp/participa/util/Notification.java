package com.tretornesp.participa.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.tretornesp.participa.R;

public class Notification{

    public static class NotificationDialog extends DialogFragment {

        private Dialog customDialog = null;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return customDialog;
        }

        private NotificationDialog(FragmentActivity activity, String title, String text, boolean showno, boolean input, boolean textCheck, Callback onInput) {
            final EditText editTextName = new EditText(activity);
            editTextName.setHint("Contraseña");
            editTextName.setFocusable(true);
            editTextName.setClickable(true);
            editTextName.setFocusableInTouchMode(true);
            editTextName.setSelectAllOnFocus(true);
            editTextName.setSingleLine(true);
            editTextName.setImeOptions(EditorInfo.IME_ACTION_NEXT);

            final EditText editTextPassword = new EditText(activity);
            editTextPassword.setHint("Confirma contraseña");
            editTextPassword.setFocusable(true);
            editTextPassword.setClickable(true);
            editTextPassword.setFocusableInTouchMode(true);
            editTextPassword.setSelectAllOnFocus(true);
            editTextPassword.setSingleLine(true);
            editTextPassword.setImeOptions(EditorInfo.IME_ACTION_DONE);


            LinearLayout linearLayout = new LinearLayout(activity);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.addView(editTextName);
            linearLayout.addView(editTextPassword);

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);

            if (input) {
                builder.setView(linearLayout);
            }

            builder.setTitle(title);
            builder.setMessage(text)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (input) {
                                if (textCheck) {
                                    if (editTextName.getText().toString().equals(editTextPassword.getText().toString())) {
                                        onInput.onSuccess(editTextName.getText().toString());
                                    } else {
                                        onInput.onFailure("Passwords do not match");
                                    }
                                } else {
                                    onInput.onSuccess(editTextName.getText().toString());
                                }
                            } else {
                                if (onInput != null) {
                                    onInput.onSuccess(null);
                                }
                            }
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
            if (showno) {
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        customDialog.dismiss();
                    }
                });
            }
            customDialog = builder.create();

            if (input) {
                editTextName.setVisibility(View.VISIBLE);
                if (textCheck) {
                    editTextName.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    editTextName.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    editTextPassword.setVisibility(View.VISIBLE);
                    editTextPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    editTextName.setInputType(InputType.TYPE_CLASS_TEXT);
                    editTextName.setHint("Nombre");
                    editTextName.setTransformationMethod(null);
                    editTextPassword.setVisibility(View.GONE);
                }
            }
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
                new NotificationDialog(activity, title, text, false, false, false, null).show(activity.getSupportFragmentManager(), "Notification");
            }
        });
    }

    public static void input(FragmentActivity activity, String title, String text, Callback callback) {
        Handler handler = new Handler(activity.getApplication().getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                new NotificationDialog(activity, title, text, false, true, false, callback).show(activity.getSupportFragmentManager(), "Notification");
            }
        });
    }

    public static void actOnYes(FragmentActivity activity, String title, String text, Callback callback) {
        Handler handler = new Handler(activity.getApplication().getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                new NotificationDialog(activity, title, text, true, false, false, callback).show(activity.getSupportFragmentManager(), "Notification");
            }
        });
    }

    public static void requestPassword(FragmentActivity activity, String title, String text, Callback callback) {
        Handler handler = new Handler(activity.getApplication().getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                new NotificationDialog(activity, title, text, true, true, true, callback).show(activity.getSupportFragmentManager(), "Notification");
            }
        });
    }
}