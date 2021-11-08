package com.covid19.test_finder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.covid19.test_finder.databinding.ActivityMainBinding;
import com.covid19.test_finder.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /// splash screen, sekitar 4 detik
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               startActivity(new Intent(MainActivity.this, LoginActivity.class));
               finish();
            }
        }, 4000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}