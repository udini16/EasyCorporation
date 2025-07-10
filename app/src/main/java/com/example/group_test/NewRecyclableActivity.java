package com.example.group_test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.group_test.model.RecyclableItems;
import com.example.group_test.model.User;
import com.example.group_test.remote.ApiUtils;
import com.example.group_test.remote.RecyclableService;
import com.example.group_test.sharedpref.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewRecyclableActivity extends AppCompatActivity {

    private EditText etItemName;
    private EditText etPricePerKg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_recyclable);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etItemName = findViewById(R.id.etItemName);
        etPricePerKg = findViewById(R.id.etPricePerKg);
    }

    public void addNewRecycle(View view) {
        String itemName = etItemName.getText().toString().trim();
        String pricePerKgStr = etPricePerKg.getText().toString().trim();

        if (itemName.isEmpty() || pricePerKgStr.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        float pricePerKg;
        try {
            pricePerKg = Float.parseFloat(pricePerKgStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getApplicationContext(), "Invalid price format", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        RecyclableService recyclableService = ApiUtils.getRecyclableService();
        Call<RecyclableItems> call = recyclableService.addRecyclable(user.getToken(), itemName, pricePerKg);

        call.enqueue(new Callback<RecyclableItems>() {
            @Override
            public void onResponse(Call<RecyclableItems> call, Response<RecyclableItems> response) {
                Log.d("MyApp:", "Response: " + response.raw().toString());
                if (response.code() == 201) {
                    RecyclableItems addedRecyclable = response.body();
                    Toast.makeText(getApplicationContext(), addedRecyclable.getItem_name() + " added successfully.", Toast.LENGTH_LONG).show();
                    finish();  // Close the Add screen
                } else if (response.code() == 401) {
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                } else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<RecyclableItems> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error [" + t.getMessage() + "]", Toast.LENGTH_LONG).show();
                Log.e("MyApp:", t.toString());
            }
        });
    }

    public void clearSessionAndRedirect() {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();

        finish();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
