package com.covid19.test_finder.home.home.place;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.covid19.test_finder.R;
import com.covid19.test_finder.databinding.ActivityPlaceBinding;

public class PlaceActivity extends AppCompatActivity {

    private ActivityPlaceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}