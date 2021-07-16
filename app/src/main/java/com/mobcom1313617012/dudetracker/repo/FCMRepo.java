package com.mobcom1313617012.dudetracker.repo;

import android.util.Log;

import com.mobcom1313617012.dudetracker.model.FormFCM;
import com.mobcom1313617012.dudetracker.restapi.FCMClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FCMRepo {
    public static void sendForm(FormFCM form) {
        Call<ResponseBody> r = FCMClient.getInterface().updateLocation(form);

        r.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body() != null) {
                    if (response.isSuccessful())
                        Log.v("succ", response.body().toString());
                    else
                        Log.e("fail", response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("fail", t.getMessage());
            }
        });
    }
}
