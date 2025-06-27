package com.example.group_test.remote;

public class ApiUtils {
    public static final String BASE_URL = "https://codelah.my/2024794407/api/";

    public static UserService getUserService() {
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }
}
