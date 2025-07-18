package com.example.group_test.remote;

import retrofit2.Retrofit;

public class ApiUtils {

    private static final String BASE_URL = "https://codelah.my/db_reggin/api/";

    // Reuse Retrofit instance (singleton)
    private static Retrofit retrofit = null;

    private static Retrofit getRetrofitClient() {
        if (retrofit == null) {
            retrofit = RetrofitClient.getClient(BASE_URL);
        }
        return retrofit;
    }

    // Provide singleton service instances
    public static UserService getUserService() {
        return getRetrofitClient().create(UserService.class);
    }

    public static RequestService getRequestService() {
        return getRetrofitClient().create(RequestService.class);
    }

    public static RecyclableService getRecyclableService() {
        return getRetrofitClient().create(RecyclableService.class);
    }
}
