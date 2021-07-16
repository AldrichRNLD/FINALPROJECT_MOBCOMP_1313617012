package com.mobcom1313617012.dudetracker.repo;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.mobcom1313617012.dudetracker.model.FormFCM;
import com.mobcom1313617012.dudetracker.model.User;
import com.mobcom1313617012.dudetracker.util.Constants;

public class LocationRepo {
    private static final MutableLiveData<LatLng> LOCATION = new MutableLiveData<>();
    private static LocationRequest lr = null;
    private static LocationCallback lc = null;
    private static SharedPreferences sp = null;

    public static MutableLiveData<LatLng> getLocation(Context ctx) {
        if (lr == null) {
            lr = new LocationRequest();
            lr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            lr.setInterval(10000);
            lr.setFastestInterval(10000);
            lr.setSmallestDisplacement(5F);
        }
        if(sp == null)
            sp = ctx.getSharedPreferences(Constants.SP_SETTINGS, Context.MODE_PRIVATE);

        FusedLocationProviderClient fpc = LocationServices.getFusedLocationProviderClient(ctx);

        if(lc == null) {
            lc = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        for (Location loc : locationResult.getLocations()) {
                            if (loc != null) {
                                LatLng latlng = new LatLng(loc.getLatitude(), loc.getLongitude());
                                User user = new User();
                                user.name = sp.getString(Constants.SP_SETTINGS_NAME, null);
                                user.token = sp.getString(Constants.SP_SETTINGS_TOKEN, null);
                                user.lat = loc.getLatitude();
                                user.lng = loc.getLongitude();
                                user.status = sp.getInt(Constants.SP_SETTINGS_STAT, 0);
                                sp.edit().putString(Constants.SP_SETTINGS_LAT, String.valueOf(user.lat))
                                        .putString(Constants.SP_SETTINGS_LNG, String.valueOf(user.lng))
                                        .apply();
                                FormFCM form = new FormFCM();
                                form.user = user;
                                FCMRepo.sendForm(form);
                                LOCATION.postValue(latlng);
                            }
                        }
                    }
                }
            };
        }

        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                fpc.requestLocationUpdates(lr, lc, null);

        return LOCATION;
    }
}
