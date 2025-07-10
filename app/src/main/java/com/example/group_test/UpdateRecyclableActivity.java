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

public class UpdateRecyclableActivity extends AppCompatActivity {

    private EditText txtItemName;
    private EditText txtPricePerKg;
    private RecyclableItems recyclableItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_recyclable);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        int item_id = intent.getIntExtra("item_id", -1);

        txtItemName = findViewById(R.id.txtItemName);
        txtPricePerKg = findViewById(R.id.txtPricePerKg);

        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        RecyclableService recyclableService = ApiUtils.getRecyclableService();

        recyclableService.getRecyclable(user.getToken(), item_id).enqueue(new Callback<RecyclableItems>() {
            @Override
            public void onResponse(Call<RecyclableItems> call, Response<RecyclableItems> response) {
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    recyclableItem = response.body();
                    txtItemName.setText(recyclableItem.getItem_name());
                    txtPricePerKg.setText(String.valueOf(recyclableItem.getPrice_per_kg()));
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
                Toast.makeText(null, "Error connecting", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void clearSessionAndRedirect(){
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();
        finish();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void updateRecyclableItem(View view) {
        String itemName = txtItemName.getText().toString().trim();
        String pricePerKgStr = txtPricePerKg.getText().toString().trim();

        if (itemName.isEmpty() || pricePerKgStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        float pricePerKg;
        try {
            pricePerKg = Float.parseFloat(pricePerKgStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("MyApp", "Old Item info: " + recyclableItem.toString());

        recyclableItem.setItem_name(itemName);
        recyclableItem.setPrice_per_kg(pricePerKg);

        Log.d("MyApp", "New Item info: " + recyclableItem.toString());

        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        RecyclableService recyclableService = ApiUtils.getRecyclableService();
        Call<RecyclableItems> call = recyclableService.updateRecyclable(
                user.getToken(),
                recyclableItem.getItem_id(),
                recyclableItem.getItem_name(),
                recyclableItem.getPrice_per_kg()
        );

        call.enqueue(new Callback<RecyclableItems>() {
            @Override
            public void onResponse(Call<RecyclableItems> call, Response<RecyclableItems> response) {
                Log.d("MyApp:", "Response: " + response.raw().toString());
                if (response.code() == 200) {
                    RecyclableItems updatedRecyclable = response.body();
                    Toast.makeText(getApplicationContext(), updatedRecyclable.getItem_name() + " updated successfully.", Toast.LENGTH_LONG).show();
                    finish();
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
                Toast.makeText(getApplicationContext(), "Error connecting", Toast.LENGTH_LONG).show();
                Log.e("MyApp:", t.toString());
            }
        });
    }
}