package com.covid19.test_finder.home.situation;

import android.Manifest;
import android.app.Activity;
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

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.covid19.test_finder.R;
import com.covid19.test_finder.databinding.FragmentSituationBinding;
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

import org.jetbrains.annotations.NotNull;

public class SituationFragment extends Fragment implements OnMapReadyCallback {

    private FragmentSituationBinding binding;
    private Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int permissionCode = 101;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSituationBinding.inflate(inflater, container, false);

        fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(getActivity());


        // UNTUK MENGETAHUI LOKASI PENGGUNA SAAT INI
        fetchLocation();

        // MELAKUKAN REQUEST AKTIFKAN GPS OTOMATIS KEPADA PERANGKAT PENGGUNA
        Maps.showLocationPrompt(this);

        return binding.getRoot();
    }

    private void fetchLocation() {
        // MENGECEK APAKAH PENGGUNA SUDAH MENYALAKAN PERMISSION LOKASI ATAU BELUM, JIKA BELUM MAKA TAMPILKAN PERMISSION LOKASI, UNTUK MENDETEKSI LOKASI SAAT INI
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
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
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Hai, Ini merupakan lokasi kamu").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
        googleMap.addMarker(markerOptions);
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        LatLng dkiJakarta = new LatLng(-6.200000, 106.816666);
        googleMap.addMarker(new MarkerOptions().position(dkiJakarta).title("DKI Jakarta").snippet("Tanggal: 30 September 2021\nKasus: 857,765\nSembuh: 842,510\nKematian: 13,539"));

        LatLng aceh = new LatLng(4.695135, 96.749397);
        googleMap.addMarker(new MarkerOptions().position(aceh).title("Aceh").snippet("Tanggal: 30 September 2021\nKasus: 37,733\nSembuh: 34.027\nKematian: 1,927"));

        LatLng bali = new LatLng(-8.409518, 115.188919);
        googleMap.addMarker(new MarkerOptions().position(bali).title("Bali").snippet("Tanggal: 30 September 2021\nKasus: 112,582\nSembuh: 107,627\nKematian: 3,943"));

        LatLng banten = new LatLng(-6.120000, 106.150276);
        googleMap.addMarker(new MarkerOptions().position(banten).title("Banten").snippet("Tanggal: 30 September 2021\nKasus: 131,475\nSembuh: 128,002\nKematian: 2,670"));

        LatLng bangkaBelitung = new LatLng(-2.133333, 106.116669);
        googleMap.addMarker(new MarkerOptions().position(bangkaBelitung).title("Bangka Belitung").snippet("Tanggal: 30 September 2021\nKasus: 51,209\nSembuh: 48,924\nKematian: 1,397"));

        LatLng bengkulu = new LatLng(-3.788892, 102.266579);
        googleMap.addMarker(new MarkerOptions().position(bengkulu).title("Bengkulu").snippet("Tanggal: 30 September 2021\nKasus: 23,021\nSembuh: 22,463\nKematian: 466"));

        LatLng diYogyakarta = new LatLng(-7.797068, 110.370529);
        googleMap.addMarker(new MarkerOptions().position(diYogyakarta).title("DI Yogyakarta").snippet("Tanggal: 30 September 2021\nKasus: 154,829\nSembuh: 148,125\nKematian: 5,190"));

        LatLng jambi = new LatLng(-1.609972, 103.607254);
        googleMap.addMarker(new MarkerOptions().position(jambi).title("Jambi").snippet("Tanggal: 30 September 2021\nKasus: 29,547\nSembuh: 28,366\nKematian: 767"));


        LatLng jabar = new LatLng(-6.905977, 107.613144);
        googleMap.addMarker(new MarkerOptions().position(jabar).title("Jawa Barat").snippet("Tanggal: 30 September 2021\nKasus: 702,722\nSembuh: 685,219\nKematian: 14,624"));


        LatLng jateng = new LatLng(-6.995016, 110.418427);
        googleMap.addMarker(new MarkerOptions().position(jateng).title("Jawa Tengah").snippet("Tanggal: 30 September 2021\nKasus: 482,009\nSembuh: 448,597\nKematian: 29,894"));


        LatLng jatim = new LatLng(-7.866688, 111.466614);
        googleMap.addMarker(new MarkerOptions().position(jatim).title("Jawa Timur").snippet("Tanggal: 30 September 2021\nKasus: 395,475\nSembuh: 364,251\nKematian: 29,413"));


        LatLng kalbar = new LatLng(0.000000, 109.333336);
        googleMap.addMarker(new MarkerOptions().position(kalbar).title("Kalimantan Barat").snippet("Tanggal: 30 September 2021\nKasus: 39,961\nSembuh: 38,711\nKematian: 1,045"));


        LatLng kaltim = new LatLng(-0.502106, 117.153709);
        googleMap.addMarker(new MarkerOptions().position(kaltim).title("Kalimantan Timur").snippet("Tanggal: 30 September 2021\nKasus: 156,811\nSembuh: 150,282\nKematian: 5,381"));


        LatLng kalteng = new LatLng(-1.681488, 113.382355);
        googleMap.addMarker(new MarkerOptions().position(kalteng).title("Kalimantan Tengah").snippet("Tanggal: 30 September 2021\nKasus: 45,162\nSembuh: 42,338\nKematian: 1,367"));


        LatLng kalsel = new LatLng(-3.316694, 114.590111);
        googleMap.addMarker(new MarkerOptions().position(kalsel).title("Kalimantan Selatan").snippet("Tanggal: 30 September 2021\nKasus: 69,425\nSembuh: 66,531\nKematian: 2,359"));


        LatLng kaltara = new LatLng(2.72594, 116.911);
        googleMap.addMarker(new MarkerOptions().position(kaltara).title("Kalimantan Utara").snippet("Tanggal: 30 September 2021\nKasus: 35,007\nSembuh: 31,202\nKematian: 773"));

        LatLng kepriau = new LatLng(-0.15478, 104.58037);
        googleMap.addMarker(new MarkerOptions().position(kepriau).title("Kepulauan Riau").snippet("Tanggal: 30 September 2021\nKasus: 53,633\nSembuh: 51,435\nKematian: 1,737"));

        LatLng ntb = new LatLng(-8.12179, 117.63696);
        googleMap.addMarker(new MarkerOptions().position(ntb).title("Nusa Tenggara Barat").snippet("Tanggal: 30 September 2021\nKasus: 27,423\nSembuh: 26,380\nKematian: 799"));

        LatLng sumsel = new LatLng(-3.12668, 104.09306);
        googleMap.addMarker(new MarkerOptions().position(sumsel).title("Sumatera Selatan").snippet("Tanggal: 30 September 2021\nKasus: 59,615\nSembuh: 56,184\nKematian: 3,046"));


        LatLng sumbar = new LatLng(-0.739940, 100.800005);
        googleMap.addMarker(new MarkerOptions().position(sumbar).title("Sumatera Barat").snippet("Tanggal: 30 September 2021\nKasus: 89,276\nSembuh: 86,244\nKematian: 2,121"));


        LatLng sumut = new LatLng(2.19235, 99.38122);
        googleMap.addMarker(new MarkerOptions().position(sumut).title("Sumatera Utara").snippet("Tanggal: 30 September 2021\nKasus: 34,179\nSembuh: 32,317\nKematian: 1,028"));


        LatLng sulteng = new LatLng(-3.54912, 121.72796);
        googleMap.addMarker(new MarkerOptions().position(sulteng).title("Sulawesi Tenggara").snippet("Tanggal: 30 September 2021\nKasus: 20,030\nSembuh: 19,216\nKematian: 523"));

        LatLng sulsel = new LatLng(-3.64467, 119.94719);
        googleMap.addMarker(new MarkerOptions().position(sulsel).title("Sulawesi Selatan").snippet("Tanggal: 30 September 2021\nKasus: 108,584\nSembuh: 104,498\nKematian: 2,206"));

        LatLng sultengah = new LatLng(-1.69378, 120.80886);
        googleMap.addMarker(new MarkerOptions().position(sultengah).title("Sulawesi Tengah").snippet("Tanggal: 30 September 2021\nKasus: 46,341\nSembuh: 43,858\nKematian: 1,561"));

        LatLng lampung = new LatLng(-4.8555, 105.0273);
        googleMap.addMarker(new MarkerOptions().position(lampung).title("Lampung").snippet("Tanggal: 30 September 2021\nKasus: 49,064\nSembuh: 43,964\nKematian: 3,764"));

        LatLng riau = new LatLng(0.50041, 101.54758);
        googleMap.addMarker(new MarkerOptions().position(riau).title("Riau").snippet("Tanggal: 30 September 2021\nKasus: 127,735\nSembuh: 122,629\nKematian: 4,064"));


        LatLng malukuUtara = new LatLng(0.63012, 127.97202);
        googleMap.addMarker(new MarkerOptions().position(malukuUtara).title("Maluku Utara").snippet("Tanggal: 30 September 2021\nKasus: 11,956\nSembuh: 11,567\nKematian: 302"));

        LatLng maluku = new LatLng(-3.11884, 129.42078);
        googleMap.addMarker(new MarkerOptions().position(maluku).title("Maluku").snippet("Tanggal: 30 September 2021\nKasus: 14,524\nSembuh: 14,113\nKematian: 258"));

        LatLng papbar = new LatLng(-1.38424, 132.90253);
        googleMap.addMarker(new MarkerOptions().position(papbar).title("Papua Barat").snippet("Tanggal: 30 September 2021\nKasus: 22,899\nSembuh: 22,461\nKematian: 352"));

        LatLng papua = new LatLng(-3.98857, 138.34853);
        googleMap.addMarker(new MarkerOptions().position(papua).title("Papua").snippet("Tanggal: 30 September 2021\nKasus: 33,746\nSembuh: 31,441\nKematian: 496"));


        LatLng sulbar = new LatLng(-2.49745, 119.3919);
        googleMap.addMarker(new MarkerOptions().position(sulbar).title("Sulawesi Barat").snippet("Tanggal: 30 September 2021\nKasus: 12,159\nSembuh: 11,567\nKematian: 336"));

        LatLng ntt = new LatLng(-8.56568, 120.69786);
        googleMap.addMarker(new MarkerOptions().position(ntt).title("Nusa Tenggara Timur").snippet("Tanggal: 30 September 2021\nKasus: 62,716\nSembuh: 60,747\nKematian: 1,296"));


        LatLng gorontalo = new LatLng(0.71862, 122.45559);
        googleMap.addMarker(new MarkerOptions().position(gorontalo).title("Gorontalo").snippet("Tanggal: 30 September 2021\nKasus: 11,735\nSembuh: 11,152\nKematian: 458"));


        // untuk nampilin dateTime
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(@NonNull Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(@NonNull Marker marker) {
                LinearLayout info = new LinearLayout(getContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.LEFT);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });


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