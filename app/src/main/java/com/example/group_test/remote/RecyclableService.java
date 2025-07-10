package com.example.group_test.remote;
import com.example.group_test.model.RecyclableItems;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RecyclableService {
    @GET("recyclable_items/?order=item_name&orderType=asc")
    Call<List<RecyclableItems>> getAllRecyclables(@Header("api-key") String apiKey);

    @GET("recyclable_items/{id}")
    Call<RecyclableItems> getRecyclable(@Header("api-key") String apiKey, @Path("id") int id);

    @FormUrlEncoded
    @POST("recyclable_items")
    Call<RecyclableItems> addRecyclable(@Header("api-key") String apiKey,
                                         @Field("item_name") String item_name,
                                         @Field("price_per_kg") Float price_per_kg);

    @FormUrlEncoded
    @POST("recyclable_items/{id}")
    Call<RecyclableItems> updateRecyclable(@Header("api-key") String apiKey, @Path("id") int id,
                                           @Field("item_name") String item_name, @Field("price_per_kg") Float price);
}
