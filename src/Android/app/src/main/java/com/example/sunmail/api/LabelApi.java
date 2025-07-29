// api/LabelApi.java
package com.example.sunmail.api;

import com.example.sunmail.model.LabelRequest;
import com.example.sunmail.model.Label;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface LabelApi {
    @POST("labels")
    Call<Void> createLabel(@Body LabelRequest labelRequest);

    @GET("labels")
    Call<List<Label>> getLabels();
    @PATCH("labels/{id}")
    Call<Void> updateLabel(@Path("id") String id, @Body LabelRequest labelRequest);
    @DELETE("labels/{id}")
    Call<Void> deleteLabel(@Path("id") String id);

}
