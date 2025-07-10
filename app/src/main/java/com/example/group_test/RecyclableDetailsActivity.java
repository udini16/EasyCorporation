package com.example.group_test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
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

public class RecyclableDetailsActivity extends AppCompatActivity {

    private RecyclableService recyclableService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recyclable_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        int itemId = intent.getIntExtra("item_id", -1);

        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        recyclableService = ApiUtils.getRecyclableService();

        recyclableService.getRecyclable(token, itemId).enqueue(new Callback<RecyclableItems>() {
            @Override
            public void onResponse(Call<RecyclableItems> call, Response<RecyclableItems> response) {
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    RecyclableItems recyclable = response.body();

                    TextView tvItemName = findViewById(R.id.tvItemName);
                    TextView tvPricePerKg = findViewById(R.id.tvPricePerKg);

                    tvItemName.setText(recyclable.getItem_name());
                    tvPricePerKg.setText("RM " + String.format("%.2f", recyclable.getPrice_per_kg()));
                } else if (response.code() == 401) {
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<RecyclableItems> call, Throwable throwable) {
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
}