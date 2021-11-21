package com.covid19.test_finder.home.home.place;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.covid19.test_finder.R;
import com.covid19.test_finder.databinding.ActivityPlaceAddBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.LongFunction;

public class PlaceListActivity extends AppCompatActivity {

    public static final String EXTRA_DISTANCE = "distance";
    public static final String EXTRA_LAT = "latitude";
    public static final String EXTRA_LON = "longitude";
    private ActivityPlaceAddBinding binding;
    private String filter = "no";
    private PlaceAdapter adapter;
    private String option = "activity";

    @Override
    protected void onResume() {
        super.onResume();
        initRecyclerView();
        initViewModel(filter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaceAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.filter, android.R.layout.simple_list_item_1);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        binding.filter.setAdapter(adapter);
        binding.filter.setOnItemClickListener((adapterView, view, i, l) -> {
            initRecyclerView();
            filter = binding.filter.getText().toString();
            if(filter.equals("Filter by SWAB Price")) {
                initViewModel("swab");
                filter = "swab";
                option = "swab";
            } else if(filter.equals("Filter by PCR Price")) {
                initViewModel("pcr");
                filter = "pcr";
                option = "pcr";
            } else if(filter.equals("Filter by Distance")) {
                initViewModel("distance");
                filter = "distance";
                option = "distance";
            }
        });


        /// kembali ke halaman sebelumnya
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        

    }

    private void initRecyclerView() {
        binding.rvLocation.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlaceAdapter(option, getIntent().getDoubleExtra(EXTRA_LAT, 0.0), getIntent().getDoubleExtra(EXTRA_LON, 0.0) );
        binding.rvLocation.setAdapter(adapter);
    }

    private void initViewModel(String filter) {
        PlaceViewModel viewModel = new ViewModelProvider(this).get(PlaceViewModel.class);


        binding.progressBar.setVisibility(View.VISIBLE);
        if(filter.equals("no")) {
            ArrayList<Double> distance = (ArrayList<Double>) getIntent().getSerializableExtra(EXTRA_DISTANCE);
            viewModel.setListPlaceAll(distance);
        } else if (!filter.equals("distance")){
            viewModel.setListPlaceByQuery(filter,  getIntent().getDoubleExtra(EXTRA_LAT, 0.0), getIntent().getDoubleExtra(EXTRA_LON, 0.0));
        } else {
            viewModel.setListPlaceByDistance(getIntent().getDoubleExtra(EXTRA_LAT, 0.0), getIntent().getDoubleExtra(EXTRA_LON, 0.0));
        }
        viewModel.getListPlace().observe(this, transactionModels -> {
            if (transactionModels.size() > 0) {
                if(option.equals("distance")) {
                    Collections.sort(transactionModels, new Comparator<PlaceModel>() {
                        @Override
                        public int compare(PlaceModel model, PlaceModel t1) {
                            return Double.compare(model.getDistance(), t1.getDistance());
                        }
                    });
                }
                adapter.setData(transactionModels);
            }
            binding.progressBar.setVisibility(View.GONE);
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}