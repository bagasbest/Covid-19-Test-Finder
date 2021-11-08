package com.covid19.test_finder.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.covid19.test_finder.R;
import com.covid19.test_finder.databinding.ActivityHomeBinding;
import com.covid19.test_finder.home.about.AboutFragment;
import com.covid19.test_finder.home.history.HistoryFragment;
import com.covid19.test_finder.home.home.HomeFragment;
import com.covid19.test_finder.home.situation.SituationFragment;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new HomeFragment());
        
        /// situation page
        binding.situation.setOnClickListener(view -> {
            binding.situation.setTextColor(ContextCompat.getColor(this, R.color.white));
            binding.home.setTextColor(ContextCompat.getColor(this, R.color.black));
            binding.history.setTextColor(ContextCompat.getColor(this, R.color.black));
            binding.about.setTextColor(ContextCompat.getColor(this, R.color.black));

            binding.situation.setBackgroundResource(R.drawable.bg_rounded);
            binding.home.setBackground(null);
            binding.history.setBackground(null);
            binding.about.setBackground(null);

            replaceFragment(new SituationFragment());


        });


        /// home page
        binding.home.setOnClickListener(view -> {
            binding.situation.setTextColor(ContextCompat.getColor(this, R.color.black));
            binding.home.setTextColor(ContextCompat.getColor(this, R.color.white));
            binding.history.setTextColor(ContextCompat.getColor(this, R.color.black));
            binding.about.setTextColor(ContextCompat.getColor(this, R.color.black));

            binding.situation.setBackground(null);
            binding.home.setBackgroundResource(R.drawable.bg_rounded);
            binding.history.setBackground(null);
            binding.about.setBackground(null);

            replaceFragment(new HomeFragment());


        });


        /// history page
        binding.history.setOnClickListener(view -> {
            binding.situation.setTextColor(ContextCompat.getColor(this, R.color.black));
            binding.home.setTextColor(ContextCompat.getColor(this, R.color.black));
            binding.history.setTextColor(ContextCompat.getColor(this, R.color.white));
            binding.about.setTextColor(ContextCompat.getColor(this, R.color.black));

            binding.situation.setBackground(null);
            binding.home.setBackground(null);
            binding.history.setBackgroundResource(R.drawable.bg_rounded);
            binding.about.setBackground(null);

            replaceFragment(new HistoryFragment());


        });


        /// about page
        binding.about.setOnClickListener(view -> {
            binding.situation.setTextColor(ContextCompat.getColor(this, R.color.black));
            binding.home.setTextColor(ContextCompat.getColor(this, R.color.black));
            binding.history.setTextColor(ContextCompat.getColor(this, R.color.black));
            binding.about.setTextColor(ContextCompat.getColor(this, R.color.white));

            binding.situation.setBackground(null);
            binding.home.setBackground(null);
            binding.history.setBackground(null);
            binding.about.setBackgroundResource(R.drawable.bg_rounded);

            replaceFragment(new AboutFragment());


        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}