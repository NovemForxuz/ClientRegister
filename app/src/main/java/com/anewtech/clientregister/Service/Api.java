package com.anewtech.clientregister.Service;

import android.telecom.CallScreeningService;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by heriz on 10/1/2018.
 */

public interface Api {
    @GET("/timenow")
    Call<ResponseBody> getPost();

    @POST("/postlogin")
    Call<ResponseBody> postVisitor(@Body RequestBody requestBody);
}
