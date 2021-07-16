package com.mobcom1313617012.dudetracker.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobcom1313617012.dudetracker.R;
import com.mobcom1313617012.dudetracker.model.FormFCM;
import com.mobcom1313617012.dudetracker.model.User;
import com.mobcom1313617012.dudetracker.repo.Database;
import com.mobcom1313617012.dudetracker.repo.FCMRepo;
import com.mobcom1313617012.dudetracker.util.Constants;
import com.mobcom1313617012.dudetracker.util.Hash;
import com.mobcom1313617012.dudetracker.view.adapter.FriendAdapter;
import com.mobcom1313617012.dudetracker.viewmodel.MainViewModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private User me;
    private MapViewFocused mapView;
    private RecyclerView friendList;
    private ArrayList<Marker> markerList;
    private SharedPreferences sp;
    public GoogleMap gmap = null;

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        sp = getSharedPreferences(Constants.SP_SETTINGS, Context.MODE_PRIVATE);

        me = new User();
        me.name = sp.getString(Constants.SP_SETTINGS_NAME, null);
        me.token = sp.getString(Constants.SP_SETTINGS_TOKEN, null);
        me.lat = Double.parseDouble(sp.getString(Constants.SP_SETTINGS_LAT, "0.0"));
        me.lng = Double.parseDouble(sp.getString(Constants.SP_SETTINGS_LNG, "0.0"));
        me.status = sp.getInt(Constants.SP_SETTINGS_STAT, 0);

        markerList = new ArrayList<>();
        ArrayList<User> usersMain = new ArrayList<>();
        FriendAdapter adapter = new FriendAdapter(this, usersMain);

        Button friendAdd = findViewById(R.id.main_add);
        TextView nameTv = findViewById(R.id.main_name);
        TextView tokenTv = findViewById(R.id.main_token);
        TextView friendInfo = findViewById(R.id.main_friend_info);
        mapView = findViewById(R.id.main_map);
        friendList = findViewById(R.id.main_friend_list);
        friendList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        friendList.setAdapter(adapter);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        nameTv.setText("Name: "+me.name);
        tokenTv.setText("Token: "+Hash.md5(me.token).substring(0, 8));

        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.getLocation(this);

        LiveData<ArrayList<User>> usersLiveData = viewModel.getUsers(this);
        usersLiveData.observe(this, users -> {
            if(users.size() > 0) {
                friendInfo.setVisibility(View.GONE);
                friendList.setVisibility(View.VISIBLE);
                if(gmap != null) {
                    markerList.clear();
                    gmap.clear();
                    LatLngBounds.Builder bBuilder = new LatLngBounds.Builder();
                    for(User user: users) {
                        if(user.lat != 0 && user.lng != 0) {
                            LatLng curLoc = new LatLng(user.lat, user.lng);
                            Marker marker = gmap.addMarker(
                                    new MarkerOptions().icon(
                                        BitmapDescriptorFactory
                                            .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                                    )
                                    .position(curLoc)
                                    .title(user.name)
                            );
                            markerList.add(marker);
                            bBuilder.include(marker.getPosition());
                        }
                    }
                    if(markerList.size() > 0) {
                        Handler tmp = new Handler(Looper.myLooper());
                        tmp.postDelayed(new Runnable() {
                            int cnt = 0;
                            @Override
                            public void run() {
                                try {
                                    LatLngBounds boundary = bBuilder.build();
                                    gmap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundary, 100));
                                } catch (Exception e) {
                                    if(cnt < 5)
                                        tmp.postDelayed(this, 1000);
                                    else
                                        Toast.makeText(MainActivity.this, "Failed drawing points in map", Toast.LENGTH_SHORT).show();
                                }
                                cnt++;
                            }
                        }, 1000);
                    }
                }
                usersMain.clear();
                usersMain.addAll(users);
                adapter.notifyDataSetChanged();
            } else {
                friendList.setVisibility(View.GONE);
                friendInfo.setVisibility(View.VISIBLE);
                zoomToMe();
            }
        });

        friendAdd.setOnClickListener(v -> {
            EditText nameInput = new EditText(this);
            EditText tokenInput = new EditText(this);
            nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
            nameInput.setHint("Enter name");
            tokenInput.setInputType(InputType.TYPE_CLASS_TEXT);
            tokenInput.setHint("Enter unique token ID");
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Enter Friend Data")
                    .setNegativeButton("Cancel", (dialog1, which) -> {})
                    .setPositiveButton("Add", (dialog1, which) -> {
                        User user = new User();
                        user.name = nameInput.getText().toString();
                        user.token = tokenInput.getText().toString();
                        if(user.name.length() < 3) {
                            Toast.makeText(this, "Name must contain more than 3 characters", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(user.token.length() != 8) {
                            Toast.makeText(this, "Token must be 8 characters long", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        new Database(MainActivity.this).create(user);
                    }).create();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(20, 20, 20, 0);
            LinearLayout dialogLayout = new LinearLayout(this);
            dialogLayout.setOrientation(LinearLayout.VERTICAL);
            dialogLayout.addView(nameInput, params);
            dialogLayout.addView(tokenInput, params);
            dialog.setView(dialogLayout);
            dialog.show();
        });
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        if(markerList.size() == 0)
            zoomToMe();
    }

    private void zoomToMe() {
        if(gmap != null && me.lat != 0 && me.lng != 0) {
            LatLng curLoc = new LatLng(me.lat, me.lng);
            gmap.clear();
            gmap.addMarker(
                    new MarkerOptions().icon(
                        BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                    )
                    .position(curLoc)
                    .title("Me")
            );
            gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLoc, 14.0F));
        }
    }

    @Override
    public void onBackPressed() {
        me.status = 0;
        sp.edit().putInt(Constants.SP_SETTINGS_STAT, 0).apply();
        FormFCM form = new FormFCM();
        form.user = me;
        FCMRepo.sendForm(form);
        new Handler(Looper.myLooper()).postDelayed(this::finishAffinity, 1000);
    }
}