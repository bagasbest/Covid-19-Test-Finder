package com.covid19.test_finder.home.home.place;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.covid19.test_finder.R;
import com.covid19.test_finder.databinding.ActivityPlaceDetailBinding;
import com.covid19.test_finder.home.home.order.OrderActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class PlaceDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PLACE = "place";
    public static final String EXTRA_DISTANCE = "distance";
    private ActivityPlaceDetailBinding binding;
    private PlaceModel model;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaceDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        model = getIntent().getParcelableExtra(EXTRA_PLACE);
        String rating = "Rating: " + sharedPref.getString(model.getUid(), "5.0");
        /// number format digunakan untuk money currency, misal IDR. 100.000
        NumberFormat formatter = new DecimalFormat("#,###");

        Glide.with(this)
                .load(model.getImg())
                .into(binding.img);

        binding.location.setText(model.getLocation());
        binding.address.setText(model.getAddress());
        binding.rating.setText(rating);
        binding.distance.setText("Distance: " + getIntent().getStringExtra(EXTRA_DISTANCE));
        if(model.getSwab() > 0) {
            binding.swab.setVisibility(View.VISIBLE);
        }
        if(model.getPcr() > 0) {
            binding.pcr.setVisibility(View.VISIBLE);
        }
        binding.price.setText("SWAB, Rp." + formatter.format(model.getSwab()) + " ~ PCR, Rp." + formatter.format(model.getPcr()));

        binding.orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlaceDetailActivity.this, OrderActivity.class);
                intent.putExtra(OrderActivity.EXTRA_PLACE, model);
                startActivity(intent);
            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}