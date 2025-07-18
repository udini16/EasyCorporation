package com.example.group_test;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.group_test.model.SubmittedRequest;
import com.example.group_test.model.User;
import com.example.group_test.model.RecyclableItems;
import com.example.group_test.remote.ApiUtils;
import com.example.group_test.remote.RequestService;
import com.example.group_test.sharedpref.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateRequestActivity extends AppCompatActivity {

    private RequestService requestService;
    private Button btnAccept, btnReject;
    private EditText etWeight;
    private TextView tvTotalPrice;

    private float itemPricePerKg = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_request);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        int requestId = intent.getIntExtra("request_id", -1);

        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        requestService = ApiUtils.getRequestService();

        btnAccept = findViewById(R.id.btnAccept);
        btnReject = findViewById(R.id.btnReject);
        etWeight = findViewById(R.id.etWeight); // NEW weight input
        tvTotalPrice = findViewById(R.id.tvTotalPrice); // Total price display

        btnAccept.setVisibility(View.GONE);
        btnReject.setVisibility(View.GONE);
        etWeight.setVisibility(View.GONE);
        tvTotalPrice.setVisibility(View.GONE);

        requestService.getRequest(token, requestId).enqueue(new Callback<SubmittedRequest>() {
            @Override
            public void onResponse(Call<SubmittedRequest> call, Response<SubmittedRequest> response) {
                if (response.code() == 200) {
                    SubmittedRequest request = response.body();

                    TextView tvRequestId = findViewById(R.id.tvRequestId);
                    TextView tvUsername = findViewById(R.id.tvUsername);
                    TextView tvItemName = findViewById(R.id.tvItemName);
                    TextView tvAddress = findViewById(R.id.tvAddress);
                    TextView tvStatus = findViewById(R.id.tvStatus);
                    TextView tvNotes = findViewById(R.id.tvNotes);

                    tvRequestId.setText("Request ID: " + request.getId());
                    tvUsername.setText("Username: " + (request.getUser() != null ? request.getUser().getUsername() : "Unknown"));

                    RecyclableItems item = request.getItem();
                    String itemName = (item != null) ? item.getItem_name() : "Unknown";
                    itemPricePerKg = (item != null) ? item.getPrice_per_kg() : 0f;

                    tvItemName.setText("Item Name: " + itemName);
                    tvAddress.setText("Address: " + request.getAddress());
                    tvStatus.setText("Status: " + request.getStatus());
                    tvNotes.setText("Notes: " + request.getNotes());

                    // Always show weight and total price
                    etWeight.setVisibility(View.VISIBLE);
                    tvTotalPrice.setVisibility(View.VISIBLE);

                    // Set initial values from server
                    etWeight.setText(String.valueOf(request.getWeight()));
                    tvTotalPrice.setText("Total Price: RM " + String.format("%.2f", request.getTotal_price()));

                    if ("Pending".equalsIgnoreCase(request.getStatus())) {
                        // Make editable only for Pending
                        etWeight.setEnabled(true);
                        btnAccept.setVisibility(View.VISIBLE);
                        btnReject.setVisibility(View.VISIBLE);

                        btnAccept.setOnClickListener(v -> {
                            String weightStr = etWeight.getText().toString().trim();
                            if (weightStr.isEmpty()) {
                                Toast.makeText(UpdateRequestActivity.this, "Please enter weight", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            float weight = Float.parseFloat(weightStr);
                            float totalPrice = itemPricePerKg * weight;

                            // Show updated total price before submitting
                            tvTotalPrice.setText("Total Price: RM " + String.format("%.2f", totalPrice));

                            updateStatus(token, request.getId(), "Accepted", weight, totalPrice);
                        });

                        btnReject.setOnClickListener(v -> {
                            updateStatus(token, request.getId(), "Rejected", 0f, 0f);
                        });

                    } else {
                        // Disable editing if not Pending
                        etWeight.setEnabled(false);
                        btnAccept.setVisibility(View.GONE);
                        btnReject.setVisibility(View.GONE);
                    }

                } else if (response.code() == 401) {
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                } else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }


            @Override
            public void onFailure(Call<SubmittedRequest> call, Throwable throwable) {
                Toast.makeText(UpdateRequestActivity.this, "Error connecting", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateStatus(String token, int requestId, String newStatus, float weight, float totalPrice) {
        requestService.updateRequestStatus(token, requestId, newStatus, weight, totalPrice).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UpdateRequestActivity.this, "Status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(UpdateRequestActivity.this, "Failed to update: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(UpdateRequestActivity.this, "Update failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
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
