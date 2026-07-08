package com.example.final_project.network;

import com.example.final_project.network.models.AnalyticsResponse;
import com.example.final_project.network.models.HelloResponse;
import com.example.final_project.network.models.PredictResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;
public interface ApiService {
    @GET("hello")
    Call<HelloResponse> hello();

    @FormUrlEncoded
    @POST("predict")
    Call<PredictResponse> predict(@Field("x") int x);

    @GET("plot")
    Call<ResponseBody> getPlot(
            @Query("val") int val,
            @Query("type") String type
    );
    @Multipart
    @POST("uploadcsv")
    Call<AnalyticsResponse> uploadCsv(
            @Part MultipartBody.Part file,
            @Part("type") RequestBody type
    );}
