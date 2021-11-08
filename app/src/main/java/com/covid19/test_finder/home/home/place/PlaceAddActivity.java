package com.covid19.test_finder.home.home.place;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.covid19.test_finder.R;
import com.covid19.test_finder.databinding.ActivityPlaceAddBinding;

public class PlaceAddActivity extends AppCompatActivity {

    private ActivityPlaceAddBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaceAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}