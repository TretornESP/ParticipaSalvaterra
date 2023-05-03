package com.tretornesp.participa.repository;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.tretornesp.participa.BuildConfig;
import com.tretornesp.participa.R;
import com.tretornesp.participa.model.PresignResponseModel;
import com.tretornesp.participa.service.LoginService;

import java.io.IOException;
import java.io.InputStream;

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

    private static String getRealUrl(String url) {
        String fullUrl = BuildConfig.ROOT_URL + presign + "/" + url;
        LoginService loginService = LoginService.getInstance();
        String token = loginService.getToken();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(fullUrl).addHeader("Authorization", "Bearer " + token).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return PresignResponseModel.fromJson(response.body().string()).getUrl();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void putDrawable(String url, ImageView imageView) {

        if (url == null) {
            putDefaultImage(imageView);
            return;
        }

        String fullUrl = getRealUrl(url);
        if (fullUrl == null) {
            putDefaultImage(imageView);
            return;
        }
        Log.d("CUAK", "putDrawable: " + fullUrl);

        LoginService loginService = LoginService.getInstance();
        String token = loginService.getToken();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(fullUrl).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("CUAK", "onFailure: " + e.getMessage());
                putDefaultImage(imageView);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.d("CUAK", "onResponse: " + response.message());
                    putDefaultImage(imageView);
                } else {
                    final Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                    imageView.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("CUAK", "Setting bitmap...");
                            imageView.setImageBitmap(bitmap);
                        }
                    });
                }
            }
        });
    }
}
