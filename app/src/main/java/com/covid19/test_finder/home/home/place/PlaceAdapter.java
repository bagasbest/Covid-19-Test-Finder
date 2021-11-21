package com.covid19.test_finder.home.home.place;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.covid19.test_finder.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    private final ArrayList<PlaceModel> listPlace = new ArrayList<>();

    private final String page;
    private final double latitude;
    private final double longitude;

    public PlaceAdapter(String page, double latitude, double longitude) {
        this.page = page;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(ArrayList<PlaceModel> items) {
        listPlace.clear();
        listPlace.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (page.equals("fragment")) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);
        }
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(listPlace.get(position), page, latitude, longitude);
    }

    @Override
    public int getItemCount() {
        return listPlace.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout cv;
        TextView location, address, price, distance;
        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            location = itemView.findViewById(R.id.location);
            address = itemView.findViewById(R.id.address);
            price = itemView.findViewById(R.id.price);
            distance = itemView.findViewById(R.id.distance);
            img = itemView.findViewById(R.id.img);
        }

        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        public void bind(PlaceModel model, String page, double latitude, double longitude) {

            /// number format digunakan untuk money currency, misal IDR. 100.000
            NumberFormat formatter = new DecimalFormat("#,###");
            LatLng myLocation = new LatLng(latitude, longitude);
            String distances = "";

            Glide.with(itemView.getContext())
                    .load(model.getImg())
                    .into(img);

            location.setText(model.getLocation());
            address.setText(model.getAddress());
            price.setText("Rp. " + formatter.format(model.getSwab()) + " ~ " + "Rp. " + formatter.format(model.getPcr()));

            if (page.equals("fragment") || page.equals("activity")) {
                distance.setText("Distance: " + String.format("%.2f", model.getDistance()) + " km");
            } else if (page.equals("swab") || page.equals("pcr")) {
                LatLng latLngLocation = new LatLng(Double.parseDouble(model.getLon()), Double.parseDouble(model.getLat()));
                distances = String.format("%.2f", SphericalUtil.computeDistanceBetween(myLocation, latLngLocation) / 1000) + " km";
                distance.setText("Distance: " + distances);
            } else if (page.equals("distance")) {
                distances = String.format("%.2f", model.getDistance()) + " km";
                distance.setText("Distance: " + distances);
            }

            String finalDistances = distances;
            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), PlaceDetailActivity.class);
                    intent.putExtra(PlaceDetailActivity.EXTRA_PLACE, model);
                    if(page.equals("fragment") || page.equals("activity")) {
                        intent.putExtra(PlaceDetailActivity.EXTRA_DISTANCE, String.format("%.2f", model.getDistance()) + " km");
                    } else {
                        intent.putExtra(PlaceDetailActivity.EXTRA_DISTANCE, finalDistances);
                    }
                    itemView.getContext().startActivity(intent);
                }
            });

        }
    }
}
