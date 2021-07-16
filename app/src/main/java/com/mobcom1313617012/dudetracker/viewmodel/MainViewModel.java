package com.mobcom1313617012.dudetracker.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.mobcom1313617012.dudetracker.model.User;
import com.mobcom1313617012.dudetracker.repo.Database;
import com.mobcom1313617012.dudetracker.repo.LocationRepo;

import java.util.ArrayList;

public class MainViewModel extends ViewModel {
    public LiveData<LatLng> getLocation(Context ctx) {
        return LocationRepo.getLocation(ctx);
    }
    public LiveData<ArrayList<User>> getUsers(Context ctx) {
        return new Database(ctx).read();
    }
}
