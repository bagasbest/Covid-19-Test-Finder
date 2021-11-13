package com.covid19.test_finder.home.home.place;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class PlaceViewModel extends ViewModel {


    private final MutableLiveData<ArrayList<PlaceModel>> listPlace = new MutableLiveData<>();
    final ArrayList<PlaceModel> placeModelArrayList = new ArrayList<>();

    private static final String TAG = PlaceViewModel.class.getSimpleName();

    public void setListPlace(ArrayList<PlaceModel> place) {
        placeModelArrayList.clear();
        place.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("location")
                    .limit(15)
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                PlaceModel model = new PlaceModel();

                                model.setAddress("" + document.get("address"));
                                model.setImg("" + document.get("img"));
                                model.setLat("" + document.get("lat"));
                                model.setLon("" + document.get("lon"));
                                model.setLocation("" + document.get("location"));
                                model.setPcr(document.getLong("pcr"));
                                model.setPhone("" + document.get("phone"));
                                model.setSwab(document.getLong("swab"));
                                model.setUid("" + document.get("uid"));

                                placeModelArrayList.add(model);
                                place.add(model);
                            }
                            listPlace.postValue(placeModelArrayList);
                        } else {
                            Log.e(TAG, task.toString());
                        }
                    });
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public void setListPlaceAll() {
        placeModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("location")
                    .limit(15)
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                PlaceModel model = new PlaceModel();

                                model.setAddress("" + document.get("address"));
                                model.setImg("" + document.get("img"));
                                model.setLat("" + document.get("lat"));
                                model.setLon("" + document.get("lon"));
                                model.setLocation("" + document.get("location"));
                                model.setPcr(document.getLong("pcr"));
                                model.setPhone("" + document.get("phone"));
                                model.setSwab(document.getLong("swab"));
                                model.setUid("" + document.get("uid"));

                                placeModelArrayList.add(model);
                            }
                            listPlace.postValue(placeModelArrayList);
                        } else {
                            Log.e(TAG, task.toString());
                        }
                    });
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public void setListPlaceByQuery(String filter) {
        placeModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("location")
                    .limit(15)
                    .orderBy(filter, Query.Direction.ASCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                PlaceModel model = new PlaceModel();

                                model.setAddress("" + document.get("address"));
                                model.setImg("" + document.get("img"));
                                model.setLat("" + document.get("lat"));
                                model.setLon("" + document.get("lon"));
                                model.setLocation("" + document.get("location"));
                                model.setPcr(document.getLong("pcr"));
                                model.setPhone("" + document.get("phone"));
                                model.setSwab(document.getLong("swab"));
                                model.setUid("" + document.get("uid"));

                                placeModelArrayList.add(model);
                            }
                            listPlace.postValue(placeModelArrayList);
                        } else {
                            Log.e(TAG, task.toString());
                        }
                    });
        } catch (Exception error) {
            error.printStackTrace();
        }
    }


    public LiveData<ArrayList<PlaceModel>> getListPlace() {
        return listPlace;
    }


}
