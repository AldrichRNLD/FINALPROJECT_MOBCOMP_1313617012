package com.mobcom1313617012.dudetracker.model;

import com.google.gson.annotations.SerializedName;
import com.mobcom1313617012.dudetracker.util.Constants;

public class FormFCM {
    @SerializedName("to")
    public String dest = "/topics/" + Constants.FCM_TOPIC;;
    @SerializedName("data")
    public User user;
}
