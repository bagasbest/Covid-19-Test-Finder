package com.covid19.test_finder.home.about;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.covid19.test_finder.R;
import com.covid19.test_finder.databinding.FragmentAboutBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AboutFragment extends Fragment {

    private FragmentAboutBinding binding;
    private FirebaseUser user;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAboutBinding.inflate(inflater, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        populateProfileUser();
        return binding.getRoot();
    }

    private void populateProfileUser() {
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Glide.with(getActivity())
                                .load(R.drawable.icon)
                                .into(binding.dp);

                        binding.username.setText("" + documentSnapshot.get("username"));
                        binding.name.setText("" + documentSnapshot.get("name"));
                        binding.phone.setText("" + documentSnapshot.get("phone"));
                        binding.email.setText("" + documentSnapshot.get("email"));
                        binding.gender.setText("" + documentSnapshot.get("gender"));
                        binding.age.setText("" + documentSnapshot.get("age"));
                        binding.nik.setText("" + documentSnapshot.get("nik"));

                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}