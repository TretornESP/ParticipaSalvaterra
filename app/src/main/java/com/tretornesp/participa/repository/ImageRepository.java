package com.tretornesp.participa.repository;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.tretornesp.participa.BuildConfig;
import com.tretornesp.participa.R;
import com.tretornesp.participa.model.response.PresignResponseModel;
import com.tretornesp.participa.service.LoginService;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImageRepository {
    private static final String presign = "/uploads/photo";

    private static void putDefaultImage(ImageView imageView) {
        imageView.post(new Runnable() {
            @Override
            public void run() {
                imageView.setImageResource(R.drawable.ic_launcher_background);
            }
        });
    }

    public static void putDrawable(String token, String fullUrl, ImageView imageView) {

        LoginService loginService = LoginService.getInstance();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(fullUrl).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                putDefaultImage(imageView);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    putDefaultImage(imageView);
                } else {
                    final Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                    imageView.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);
                        }
                    });
                }
            }
        });
    }
}
