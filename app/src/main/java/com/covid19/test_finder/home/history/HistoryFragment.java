package com.covid19.test_finder.home.history;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.covid19.test_finder.R;
import com.covid19.test_finder.databinding.FragmentHistoryBinding;
import com.covid19.test_finder.home.home.place.PlaceAdapter;
import com.covid19.test_finder.home.home.place.PlaceViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private HistoryAdapter adapter;

    @Override
    public void onResume() {
        super.onResume();
        getRoleUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void getRoleUser() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String role = "" + documentSnapshot.get("role");
                        initRecyclerView(role);
                        initViewModel(role, uid);
                    }
                });
    }

    private void initRecyclerView(String role) {
        if (role.equals("user")) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            binding.rvHistory.setLayoutManager(layoutManager);
        } else {
            binding.rvHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

        adapter = new HistoryAdapter();
        binding.rvHistory.setAdapter(adapter);
    }

    private void initViewModel(String role, String uid) {
        HistoryViewModel viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);
        if (role.equals("admin")) {
            viewModel.setListHistoryByAll();
        } else {
            viewModel.setListHistoryByUserId(uid);
        }
        viewModel.getHistoryList().observe(this, history -> {
            if (history.size() > 0) {
                binding.noData.setVisibility(View.GONE);
                adapter.setData(history);
            } else {
                binding.noData.setVisibility(View.VISIBLE);
            }
            binding.progressBar.setVisibility(View.GONE);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}