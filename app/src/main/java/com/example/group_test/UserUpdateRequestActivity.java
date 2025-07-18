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

    private EditText editAddress, editNotes;
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
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnUpdate = findViewById(R.id.btnUpdate);

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

        // Setup spinner with statuses allowed to choose from
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Pending", "Cancelled"}
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // Set spinner selection based on current status
        if ("Cancelled".equalsIgnoreCase(request.getStatus())) {
            spinnerStatus.setSelection(1);
        } else {
            spinnerStatus.setSelection(0);
        }

        // Check if user can edit: only if status Pending or Cancelled
        boolean canEdit = "Pending".equalsIgnoreCase(request.getStatus()) || "Cancelled".equalsIgnoreCase(request.getStatus());

        // Enable or disable editing accordingly
        editAddress.setEnabled(canEdit);
        editNotes.setEnabled(canEdit);
        spinnerStatus.setEnabled(canEdit);
        btnUpdate.setEnabled(canEdit);

        if (!canEdit) {
            Toast.makeText(this, "You cannot edit this request when status is " + request.getStatus(), Toast.LENGTH_LONG).show();
        }

        btnUpdate.setOnClickListener(v -> {
            if (canEdit) {
                handleUpdate();
            } else {
                Toast.makeText(this, "Editing is disabled for this request status.", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        float totalPrice = request.getTotal_price(); // Keep old price
        float weight = request.getWeight(); // Keep old weight

        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        String apiKey = spm.getUser().getToken();

        if (apiKey == null || apiKey.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
            clearSessionAndRedirect();
            return;
        }

        requestService.updateFullRequest(apiKey, request.getId(), newAddress, newNotes, weight, newStatus, totalPrice)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.code() == 200) {
                            Toast.makeText(getApplicationContext(), "Request updated successfully", Toast.LENGTH_LONG).show();
                            finish();
                        } else if (response.code() == 401) {
                            Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
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
