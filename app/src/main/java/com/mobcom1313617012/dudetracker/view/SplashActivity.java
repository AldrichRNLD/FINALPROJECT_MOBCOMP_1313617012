package com.mobcom1313617012.dudetracker.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.mobcom1313617012.dudetracker.R;
import com.mobcom1313617012.dudetracker.model.User;
import com.mobcom1313617012.dudetracker.util.Constants;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        sp = getSharedPreferences(Constants.SP_SETTINGS, Context.MODE_PRIVATE);
        sp.edit().putInt(Constants.SP_SETTINGS_STAT, 1).apply();

        User user = new User();
        user.name = sp.getString(Constants.SP_SETTINGS_NAME, null);
        user.token = null;

        askPermission();

        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    user.token = task.getResult();
                }
            });

        FirebaseMessaging.getInstance().subscribeToTopic(Constants.FCM_TOPIC);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            if(user.name != null) {
                gotoMainActivity();
            } else {
                EditText nameInput = new EditText(this);
                nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
                nameInput.setHint("Enter your name here");
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Enter Data")
                        .setPositiveButton("Save", (dialog1, which) -> {}).create();

                dialog.setCancelable(false);
                int dpi = (int) this.getResources().getDisplayMetrics().density * 10;
                dialog.setView(nameInput, dpi, dpi, dpi, dpi);
                dialog.show();

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                    if(user.token != null) {
                        user.name = nameInput.getText().toString();
                        if(user.name.length() < 3) {
                            Toast.makeText(this, "Minimum name length is 3 characters", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        sp.edit().putString(Constants.SP_SETTINGS_NAME, user.name)
                            .putString(Constants.SP_SETTINGS_TOKEN, user.token)
                            .apply();
                        dialog.dismiss();
                        gotoMainActivity();
                    } else {
                        Toast.makeText(this, "Please connect to Internet", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }, 2000);
    }

    private void askPermission() {
        ArrayList<String> permissions  = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissions.size() > 0)
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), 130);
    }

    private void gotoMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}