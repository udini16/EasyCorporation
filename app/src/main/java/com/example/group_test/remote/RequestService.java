package com.example.group_test.remote;

import com.example.group_test.model.RecyclableItems;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Field;
import retrofit2.http.POST;
import retrofit2.http.GET;

public interface RequestService {
    @GET("items")
    Call<List<RecyclableItems>> getItemTypes();

    @FormUrlEncoded
    @POST("request/submit")
    Call<Void> submitRequest(@Field("item_id") int itemId,
                             @Field("address") String address,
                             @Field("notes") String notes);
}
