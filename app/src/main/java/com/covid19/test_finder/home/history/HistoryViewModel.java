package com.covid19.test_finder.home.history;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class HistoryViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<HistoryModel>> listHistory = new MutableLiveData<>();
    final ArrayList<HistoryModel> historyModelArrayList = new ArrayList<>();

    private static final String TAG = HistoryViewModel.class.getSimpleName();

    public void setListHistoryByAll() {
        historyModelArrayList.clear();
        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("history")
                    .orderBy("status", Query.Direction.ASCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                HistoryModel model = new HistoryModel();

                                model.setAddress("" + document.get("address"));
                                model.setImg("" + document.get("img"));
                                model.setCheckMethod("" + document.get("checkMethod"));
                                model.setDateTime("" + document.get("dateTime"));
                                model.setLocation("" + document.get("location"));
                                model.setPrice(document.getLong("price"));
                                model.setPhone("" + document.get("phone"));
                                model.setPaymentMethod("" + document.get("paymentMethod"));
                                model.setPaymentProof("" + document.get("paymentProof"));
                                model.setResult("" + document.get("result"));
                                model.setStatus("" + document.get("status"));
                                model.setUserId("" + document.get("userId"));
                                model.setHistoryId("" + document.get("historyId"));

                                historyModelArrayList.add(model);
                            }
                            listHistory.postValue(historyModelArrayList);
                        } else {
                            Log.e(TAG, task.toString());
                        }
                    });
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public void setListHistoryByUserId(String userId) {
        historyModelArrayList.clear();
        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("history")
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                HistoryModel model = new HistoryModel();

                                model.setAddress("" + document.get("address"));
                                model.setImg("" + document.get("img"));
                                model.setCheckMethod("" + document.get("checkMethod"));
                                model.setDateTime("" + document.get("dateTime"));
                                model.setLocation("" + document.get("location"));
                                model.setPrice(document.getLong("price"));
                                model.setPhone("" + document.get("phone"));
                                model.setPaymentMethod("" + document.get("paymentMethod"));
                                model.setPaymentProof("" + document.get("paymentProof"));
                                model.setResult("" + document.get("result"));
                                model.setStatus("" + document.get("status"));
                                model.setUserId("" + document.get("userId"));
                                model.setHistoryId("" + document.get("historyId"));

                                historyModelArrayList.add(model);
                            }
                            listHistory.postValue(historyModelArrayList);
                        } else {
                            Log.e(TAG, task.toString());
                        }
                    });
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public LiveData<ArrayList<HistoryModel>> getHistoryList() {
        return listHistory;
    }


}
