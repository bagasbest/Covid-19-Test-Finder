package com.covid19.test_finder.home.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private FragmentHomeBinding binding;
    private Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int permissionCode = 101;
    private PlaceAdapter adapter;
    private ProgressDialog mProgressDialog;
    private ArrayList<PlaceModel> place = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        mProgressDialog = new ProgressDialog(getActivity());

        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        initRecyclerView();
        initViewModel();

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
                startActivity(new Intent(getActivity(), PlaceListActivity.class));
            }
        });

    }

    private void initRecyclerView() {
        binding.rvLocation.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new PlaceAdapter("fragment");
        binding.rvLocation.setAdapter(adapter);
    }


    private void initViewModel() {
        PlaceViewModel viewModel = new ViewModelProvider(this).get(PlaceViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.setListPlace(place);
        viewModel.getListPlace().observe(getViewLifecycleOwner(), place -> {
            if (place.size() > 0) {
                adapter.setData(place);
            }
            binding.progressBar.setVisibility(View.GONE);
        });
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
                googleMap.addMarker(new MarkerOptions().position(latLngLocation).title(place.get(i).getLocation()).snippet("Swab Test: Rp. " + formatter.format(place.get(i).getSwab()) + "\nPCR Test: Rp." + formatter.format(place.get(0).getPcr()) + "|" + place.get(i).getImg()));

            }

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
                        Glide.with(requireActivity())
                                .load(Objects.requireNonNull(marker.getSnippet()).substring(marker.getSnippet().indexOf("|") + 1))
                                .into(img);
                        snippet.setText(marker.getSnippet().substring(0, marker.getSnippet().indexOf("|")));
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