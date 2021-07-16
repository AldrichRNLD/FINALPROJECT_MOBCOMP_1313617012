package com.mobcom1313617012.dudetracker.services;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mobcom1313617012.dudetracker.model.User;
import com.mobcom1313617012.dudetracker.repo.Database;
import com.mobcom1313617012.dudetracker.util.Hash;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class FCMService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        User user = new User();
        user.name = data.get("name");
        user.token = data.get("token");
        user.lat = Double.parseDouble(data.get("lat"));
        user.lng = Double.parseDouble(data.get("lng"));
        user.status = Integer.parseInt(data.get("status"));
        user.token = Hash.md5(user.token).substring(0, 8);
        Database db = new Database(this.getApplicationContext());
        if(db.isUserExists(user)) {
            db.update(user);
        }
    }
}
