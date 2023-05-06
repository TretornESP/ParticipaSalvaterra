package com.tretornesp.participa;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tretornesp.participa.controller.ListController;
import com.tretornesp.participa.model.ProposalModel;
import com.tretornesp.participa.model.controller.ImageViewLoadControllerModel;
import com.tretornesp.participa.model.controller.ImageViewLoadMarkerControllerModel;
import com.tretornesp.participa.model.controller.ListLoadControllerModel;
import com.tretornesp.participa.model.controller.LoadListItemControllerModel;
import com.tretornesp.participa.util.Callback;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private static final String TAG = "CustomInfoWindowAdapter";
    private final LayoutInflater inflater;
    private final List<ProposalModel> proposals;

    public CustomInfoWindowAdapter(LayoutInflater inflater, List<ProposalModel> proposals) {
        this.inflater = inflater;
        this.proposals = proposals;
    }

    @Override
    public View getInfoContents(final Marker m) {
        //Carga layout personalizado.
        View v = inflater.inflate(R.layout.marker_layout, null);

        for (ProposalModel p: proposals) {
            if (p.getId().equals(m.getTitle())) {
                ((TextView)v.findViewById(R.id.marker_title)).setText(p.getTitle());
                ((TextView)v.findViewById(R.id.marker_secondary_text)).setText(p.getDescription());

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ImageView imageView = v.findViewById(R.id.marker_image);
                            FutureTarget<Bitmap> future = Glide.with(v).asBitmap().load(p.getMain_photo()).onlyRetrieveFromCache(true).submit();
                            Bitmap bitmap = future.get();
                            imageView.setImageBitmap(bitmap);
                        } catch (ExecutionException | InterruptedException ignored) {}
                    }
                });
                t.start();
                try {
                    t.join();
                } catch (InterruptedException ignored) {}
                break;
            }
        }

        return v;
    }

    @Override
    public View getInfoWindow(@NonNull Marker m) {
        return null;
    }

}
