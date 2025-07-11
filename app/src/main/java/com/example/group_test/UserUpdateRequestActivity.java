package com.example.group_test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.group_test.model.SubmittedRequest;
import com.example.group_test.remote.ApiUtils;
import com.example.group_test.remote.RequestService;
import com.example.group_test.sharedpref.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserUpdateRequestActivity extends AppCompatActivity {

    private EditText editAddress, editNotes, editWeight;
    private Spinner spinnerStatus;
    private Button btnUpdate;

    private SubmittedRequest request;
    private RequestService requestService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update_request);

        editAddress = findViewById(R.id.editAddress);
        editNotes = findViewById(R.id.editNotes);
        editWeight = findViewById(R.id.editWeight);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnUpdate = findViewById(R.id.btnUpdate);

        requestService = ApiUtils.getRequestService();

        request = (SubmittedRequest) getIntent().getSerializableExtra("request");

        if (request == null) {
            Toast.makeText(this, "No request data found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        editAddress.setText(request.getAddress());
        editNotes.setText(request.getNotes());
        editWeight.setText(String.valueOf(request.getWeight()));

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Pending", "Cancelled"}
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        if ("Cancelled".equalsIgnoreCase(request.getStatus())) {
            spinnerStatus.setSelection(1);
        } else {
            spinnerStatus.setSelection(0);
        }

        btnUpdate.setOnClickListener(v -> handleUpdate());
    }

    private void handleUpdate() {
        String newAddress = editAddress.getText().toString().trim();
        String newNotes = editNotes.getText().toString().trim();
        String weightStr = editWeight.getText().toString().trim();
        String newStatus = spinnerStatus.getSelectedItem().toString();

        if (newAddress.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        float newWeight;
        try {
            newWeight = Float.parseFloat(weightStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid weight format", Toast.LENGTH_SHORT).show();
            return;
        }
        float pricePerKg = 0f;
        if (request.getItem() != null) {
            pricePerKg = request.getItem().getPrice_per_kg();
        }
        float totalPrice = pricePerKg * newWeight;

        Log.d("PRICE_DEBUG", "Price per kg: " + pricePerKg);
        Log.d("PRICE_DEBUG", "Weight entered: " + newWeight);
        Log.d("PRICE_DEBUG", "Total price calculated: " + totalPrice);

        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        String apiKey = spm.getUser().getToken();

        if (apiKey == null || apiKey.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
            clearSessionAndRedirect();
            return;
        }

        Log.d("TOKEN_DEBUG", "Using API Key: " + apiKey);
        Log.d("UPDATE_DEBUG", "Sending update with:");
        Log.d("UPDATE_DEBUG", "ID: " + request.getId());
        Log.d("UPDATE_DEBUG", "Address: " + newAddress);
        Log.d("UPDATE_DEBUG", "Notes: " + newNotes);
        Log.d("UPDATE_DEBUG", "Weight: " + newWeight);
        Log.d("UPDATE_DEBUG", "Status: " + newStatus);
        requestService.updateFullRequest(apiKey, request.getId(), newAddress, newNotes, newWeight, newStatus, totalPrice)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.d("UserUpdate", "Response: " + response.raw());
                        if (response.code() == 200) {
                            Toast.makeText(getApplicationContext(), "Request updated successfully", Toast.LENGTH_LONG).show();
                            finish();
                        } else if (response.code() == 401) {
                            Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                            clearSessionAndRedirect();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to update: " + response.code(), Toast.LENGTH_LONG).show();
                            Log.e("UserUpdate", "Update failed: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("UserUpdate", "onFailure: " + t.toString());
                    }
                });
    }
    private void clearSessionAndRedirect() {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();
        finish();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}
