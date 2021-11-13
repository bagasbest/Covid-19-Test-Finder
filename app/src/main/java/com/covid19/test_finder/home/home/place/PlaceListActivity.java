package com.covid19.test_finder.home.home.place;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.covid19.test_finder.R;
import com.covid19.test_finder.databinding.ActivityPlaceAddBinding;

public class PlaceListActivity extends AppCompatActivity {

    private ActivityPlaceAddBinding binding;
    private String filter = "no";
    private PlaceAdapter adapter;

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
            } else if(filter.equals("Filter by PCR Price")) {
                initViewModel("pcr");
                filter = "pcr";
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
        adapter = new PlaceAdapter("activity");
        binding.rvLocation.setAdapter(adapter);
    }

    private void initViewModel(String filter) {
        PlaceViewModel viewModel = new ViewModelProvider(this).get(PlaceViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);
        if(filter.equals("no")) {
            viewModel.setListPlaceAll();
        } else {
            viewModel.setListPlaceByQuery(filter);
        }
        viewModel.getListPlace().observe(this, transactionModels -> {
            if (transactionModels.size() > 0) {
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