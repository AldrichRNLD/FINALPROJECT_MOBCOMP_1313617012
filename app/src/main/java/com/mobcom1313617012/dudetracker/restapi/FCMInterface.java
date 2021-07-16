package com.mobcom1313617012.dudetracker.restapi;

import com.mobcom1313617012.dudetracker.model.FormFCM;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FCMInterface {
    @Headers({"Authorization: key=AAAAP2kZ2Gc:APA91bFqDFEMwSdCcpyXEdofiUpgo5QmtHfnLICqpK7NuvsirkvzdKl3aKhbsSz1ZezzqCEwU5iCX7UA29wluZZi_gIrdu4NS805e5sZGLZ_xbSXwVoqX0z-yGJk3Y6WflnnaTMl6iZT",
            "Content-Type:application/json"})
    @POST("fcm/send")
    Call<ResponseBody> updateLocation(
        @Body FormFCM formFCM
    );
}
