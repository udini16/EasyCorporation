package com.example.group_test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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

    private EditText editAddress, editNotes;
    private Spinner spinnerStatus;
    private Button btnUpdate;
    private TextView textTotalPrice;

    private SubmittedRequest request;
    private RequestService requestService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update_request);

        editAddress = findViewById(R.id.editAddress);
        editNotes = findViewById(R.id.editNotes);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnUpdate = findViewById(R.id.btnUpdate);
        textTotalPrice = findViewById(R.id.textViewTotalPrice); // Link to the TextView in layout

        requestService = ApiUtils.getRequestService();
        request = (SubmittedRequest) getIntent().getSerializableExtra("request");

        if (request == null) {
            Toast.makeText(this, "No request data found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set existing data
        editAddress.setText(request.getAddress());
        editNotes.setText(request.getNotes());

        // Setup spinner with available statuses
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Pending", "Cancelled"}
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // Set spinner selection
        if ("Cancelled".equalsIgnoreCase(request.getStatus())) {
            spinnerStatus.setSelection(1);
        } else {
            spinnerStatus.setSelection(0);
        }

        // Check if the request is completed
        boolean isCompleted = "Completed".equalsIgnoreCase(request.getStatus());

        if (isCompleted) {
            // Show total price and disable editing
            textTotalPrice.setVisibility(TextView.VISIBLE);
            textTotalPrice.setText("Total Price: RM " + String.format("%.2f", request.getTotal_price()));

            editAddress.setEnabled(false);
            editNotes.setEnabled(false);
            spinnerStatus.setEnabled(false);
            btnUpdate.setEnabled(false);
            btnUpdate.setAlpha(0.5f); // Visual indicator of disabled state

            Toast.makeText(this, "This request has been completed. Editing is disabled.", Toast.LENGTH_LONG).show();
        } else {
            // Allow editing
            textTotalPrice.setVisibility(TextView.GONE);
            editAddress.setEnabled(true);
            editNotes.setEnabled(true);
            spinnerStatus.setEnabled(true);
            btnUpdate.setEnabled(true);
        }

        btnUpdate.setOnClickListener(v -> {
            if (!isCompleted) {
                handleUpdate();
            } else {
                Toast.makeText(this, "Editing is not allowed for completed requests.", Toast.LENGTH_SHORT).show();
            }
        });

        Log.d("ACTIVITY_LAUNCH", "UserUpdateRequestActivity launched");
        Log.d("ACTIVITY_LAUNCH", "Received request: " + request.toString());
    }

    private void handleUpdate() {
        String newAddress = editAddress.getText().toString().trim();
        String newNotes = editNotes.getText().toString().trim();
        String newStatus = spinnerStatus.getSelectedItem().toString();

        if (newAddress.isEmpty()) {
            Toast.makeText(this, "Please fill in the address", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        String apiKey = spm.getUser().getToken();

        if (apiKey == null || apiKey.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
            clearSessionAndRedirect();
            return;
        }

        // Only address, notes, and status are editable. Keep old weight and price.
        requestService.updateFullRequest(
                apiKey,
                request.getId(),
                newAddress,
                newNotes,
                request.getWeight(),     // Keep original weight
                newStatus,
                request.getTotal_price() // Keep original total price
        ).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) {
                    Toast.makeText(getApplicationContext(), "Request updated successfully", Toast.LENGTH_LONG).show();
                    finish();
                } else if (response.code() == 401) {
                    Toast.makeText(getApplicationContext(), "Session expired. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to update: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
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
