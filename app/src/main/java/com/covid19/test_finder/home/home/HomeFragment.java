package com.covid19.test_finder.home.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.covid19.test_finder.R;
import com.covid19.test_finder.databinding.FragmentHomeBinding;
import com.covid19.test_finder.home.home.place.PlaceAdapter;
import com.covid19.test_finder.home.home.place.PlaceListActivity;
import com.covid19.test_finder.home.home.place.PlaceModel;
import com.covid19.test_finder.home.home.place.PlaceViewModel;
import com.covid19.test_finder.utils.Maps;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.maps.android.SphericalUtil;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private FragmentHomeBinding binding;
    private Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int permissionCode = 101;
    private PlaceAdapter adapter;
    private ProgressDialog mProgressDialog;
    private final ArrayList<PlaceModel> place = new ArrayList<>();
    private final ArrayList<Double> distance = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        mProgressDialog = new ProgressDialog(getActivity());

        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

   //     initRecyclerView();
        initViewModel("initState");

        fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(getActivity());


        // UNTUK MENGETAHUI LOKASI PENGGUNA SAAT INI
        fetchLocation();

        // MELAKUKAN REQUEST AKTIFKAN GPS OTOMATIS KEPADA PERANGKAT PENGGUNA
        Maps.showLocationPrompt(this);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.seeALL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PlaceListActivity.class);
                intent.putExtra(PlaceListActivity.EXTRA_DISTANCE, distance);
                intent.putExtra(PlaceListActivity.EXTRA_LAT, currentLocation.getLatitude());
                intent.putExtra(PlaceListActivity.EXTRA_LON, currentLocation.getLongitude());
                startActivity(intent);
            }
        });

        binding.view3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
                bottomSheetDialog.setContentView(R.layout.bottom_sheet);

                RecyclerView rvLocation = bottomSheetDialog.findViewById(R.id.rvLocation);
                ProgressBar progressBar = bottomSheetDialog.findViewById(R.id.progressBar);

                rvLocation.setLayoutManager(new LinearLayoutManager(getActivity()));
                adapter = new PlaceAdapter("fragment", 0.0,0.0);
                rvLocation.setAdapter(adapter);

                PlaceViewModel viewModel = new ViewModelProvider(getActivity()).get(PlaceViewModel.class);

                progressBar.setVisibility(View.VISIBLE);
                viewModel.setListPlace(place, distance);
                viewModel.getListPlace().observe(getViewLifecycleOwner(), place -> {
                    if (place.size() > 0) {
                        Collections.sort(place, new Comparator<PlaceModel>() {
                            @Override
                            public int compare(PlaceModel model, PlaceModel t1) {
                                return Double.compare(model.getDistance(), t1.getDistance());
                            }
                        });
                        adapter.setData(place);
                    }
                    progressBar.setVisibility(View.GONE);
                });

                bottomSheetDialog.show();

            }
        });

    }

    private void initRecyclerView() {
        binding.rvLocation.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new PlaceAdapter("fragment", 0.0, 0.0);
        binding.rvLocation.setAdapter(adapter);
    }


    private void initViewModel(String state) {
        PlaceViewModel viewModel = new ViewModelProvider(this).get(PlaceViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);
        if(state.equals("lateState")) {
            viewModel.setListPlace(place, distance);
            viewModel.getListPlace().observe(getViewLifecycleOwner(), place -> {
                if (place.size() > 0) {
                    Collections.sort(place, new Comparator<PlaceModel>() {
                        @Override
                        public int compare(PlaceModel model, PlaceModel t1) {
                            return Double.compare(model.getDistance(), t1.getDistance());
                        }
                    });
                    adapter.setData(place);
                }
                binding.progressBar.setVisibility(View.GONE);
            });
        } else {
            viewModel.setListPlace(place, null);
        }

    }

    private void fetchLocation() {
        // MENGECEK APAKAH PENGGUNA SUDAH MENYALAKAN PERMISSION LOKASI ATAU BELUM, JIKA BELUM MAKA TAMPILKAN PERMISSION LOKASI, UNTUK MENDETEKSI LOKASI SAAT INI
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    permissionCode
            );
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;

                SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_maps);
                assert supportMapFragment != null;
                supportMapFragment.getMapAsync(this);
            }
        });
    }


    @SuppressLint({"PotentialBehaviorOverride", "DefaultLocale"})
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        /// number format digunakan untuk money currency, misal IDR. 100.000
        NumberFormat formatter = new DecimalFormat("#,###");

        final Handler handler = new Handler();
        handler.postDelayed(() -> {

            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            googleMap.addMarker(markerOptions);
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
            ) {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            }

            for (int i = 0; i < 15; i++) {
                LatLng latLngLocation = new LatLng(Double.parseDouble(place.get(i).getLon()), Double.parseDouble(place.get(i).getLat()));
                String distances = String.format("%.2f", SphericalUtil.computeDistanceBetween(latLng, latLngLocation) / 1000) + " km";
                googleMap.addMarker(new MarkerOptions().position(latLngLocation).title(place.get(i).getLocation()).snippet("Swab Test: Rp. " + formatter.format(place.get(i).getSwab()) + "\nPCR Test: Rp." + formatter.format(place.get(0).getPcr()) + "\nDistance: " + distances + "|" + place.get(i).getImg()));
                distance.add(SphericalUtil.computeDistanceBetween(latLng, latLngLocation) / 1000);
            }

            initRecyclerView();
            initViewModel("lateState");

            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(@NonNull Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(@NonNull Marker marker) {
                    @SuppressLint("InflateParams") View row = getLayoutInflater().inflate(R.layout.custom_snippet, null);
                    ImageView img = row.findViewById(R.id.img);
                    TextView location = row.findViewById(R.id.location);
                    TextView snippet = row.findViewById(R.id.snippet);

                    location.setText(marker.getTitle());
                    if (!marker.getTitle().equals("Your Location")) {
                        img.setVisibility(View.VISIBLE);
                        snippet.setVisibility(View.VISIBLE);
                        Glide.with(requireActivity())
                                .load(Objects.requireNonNull(marker.getSnippet()).substring(marker.getSnippet().indexOf("|") + 1))
                                .into(img);
                        snippet.setText(marker.getSnippet().substring(0, marker.getSnippet().indexOf("|")));
                    } else {
                        img.setVisibility(View.GONE);
                        snippet.setVisibility(View.GONE);
                    }
                    return row;
                }
            });
            mProgressDialog.dismiss();
        }, 1500);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == permissionCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LocationRequest.PRIORITY_HIGH_ACCURACY) {
            if (resultCode == Activity.RESULT_OK) {
                Log.e("Status", "On");
            } else {
                Log.e("Status", "Off");
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}