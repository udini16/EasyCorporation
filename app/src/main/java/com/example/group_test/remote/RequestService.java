package com.example.group_test.remote;

import com.example.group_test.model.RecyclableItems;
import com.example.group_test.model.SubmittedRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RequestService {

    // ✅ Get list of recyclable items
    @GET("recyclable_items")
    Call<List<RecyclableItems>> getItemTypes(@Header("api-key") String apiKey);

    // ✅ Get submitted requests by user ID
    @GET("requests")
    Call<List<SubmittedRequest>> getSubmittedRequests(
            @Header("api-key") String apiKey,
            @Query("user_id") int userId
    );

    // ✅ Submit a new request
    @FormUrlEncoded
    @POST("requests")
    Call<Void> submitRequest(
            @Header("api-key") String apiKey,
            @Field("user_id") int userId,
            @Field("item_id") int itemId,
            @Field("address") String address,
            @Field("notes") String notes,
            @Field("weight") float weight,
            @Field("total_price") float totalPrice
    );

    // ✅ Get all requests (for admin)
    @GET("requests")
    Call<List<SubmittedRequest>> getAllRequests(@Header("api-key") String apiKey);

    // ✅ Update full request (user: address, notes, weight, status)
    @FormUrlEncoded
    @POST("requests/{id}")
    Call<Void> updateFullRequest(
            @Header("api-key") String apiKey, // instead of Authorization
            @Path("id") int requestId,
            @Field("address") String address,
            @Field("notes") String notes,
            @Field("weight") float weight,
            @Field("status") String status,
            @Field("total_price") float totalPrice
    );

    @FormUrlEncoded
    @POST("requests/{id}")
    Call<Void> updateRequestStatus(
            @Header("api-key") String apiKey, // instead of Authorization
            @Path("id") int requestId,
            @Field("status") String status
    );


    @GET("requests/{id}")
    Call<SubmittedRequest> getRequest(@Header("api-key") String apiKey, @Path("id") int id);
}
