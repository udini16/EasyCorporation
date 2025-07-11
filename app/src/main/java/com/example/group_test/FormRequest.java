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

import com.example.group_test.model.RecyclableItems;
import com.example.group_test.model.User;
import com.example.group_test.remote.ApiUtils;
import com.example.group_test.remote.RequestService;
import com.example.group_test.sharedpref.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormRequest extends AppCompatActivity {

    private Spinner spinnerItems;
    private EditText editAddress, editNotes, editWeight;
    private Button btnSubmit;

    private List<RecyclableItems> itemList;
    private RequestService requestService;

    private static final String TAG = "FormRequest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_request);

        // Bind UI elements
        spinnerItems = findViewById(R.id.spinnerItems);
        editAddress = findViewById(R.id.editAddress);
        editNotes = findViewById(R.id.editNotes);
        editWeight = findViewById(R.id.editWeight);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Initialize API
        requestService = ApiUtils.getRequestService();

        // Load spinner data
        loadItemTypes();

        // Handle Submit
        btnSubmit.setOnClickListener(v -> handleSubmit());
    }

    private void loadItemTypes() {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        requestService.getItemTypes(token).enqueue(new Callback<List<RecyclableItems>>() {
            @Override
            public void onResponse(Call<List<RecyclableItems>> call, Response<List<RecyclableItems>> response) {
                Log.d(TAG, "ItemTypes Response Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    itemList = response.body();

                    ArrayAdapter<RecyclableItems> adapter = new ArrayAdapter<>(
                            FormRequest.this,
                            android.R.layout.simple_spinner_item,
                            itemList
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerItems.setAdapter(adapter);

                    Toast.makeText(FormRequest.this, "Items loaded successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FormRequest.this, "Failed to load items. Code: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<RecyclableItems>> call, Throwable t) {
                Log.e(TAG, "ItemTypes Load Failed: " + t.getMessage());
                Toast.makeText(FormRequest.this, "Network error loading items.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleSubmit() {
        if (itemList == null || itemList.isEmpty()) {
            Toast.makeText(this, "Item list is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        RecyclableItems selectedItem = itemList.get(spinnerItems.getSelectedItemPosition());
        String address = editAddress.getText().toString().trim();
        String notes = editNotes.getText().toString().trim();
        String weightStr = editWeight.getText().toString().trim();

        if (address.isEmpty()) {
            Toast.makeText(this, "Please enter an address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (weightStr.isEmpty()) {
            Toast.makeText(this, "Please enter weight!", Toast.LENGTH_SHORT).show();
            return;
        }

        float weight;
        try {
            weight = Float.parseFloat(weightStr);
            if (weight <= 0) {
                Toast.makeText(this, "Weight must be more than 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid weight format", Toast.LENGTH_SHORT).show();
            return;
        }

        float pricePerKg = selectedItem.getPrice_per_kg();
        float totalPrice = weight * pricePerKg;
        Log.d(TAG, "Total Price: RM" + totalPrice);

        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();
        int userId = user.getId();

        Log.d(TAG, "Submitting Request -> userID: " + userId + ", itemID: " + selectedItem.getItem_id());

        // ✅ Now sending weight and total price
        requestService.submitRequest(
                token,
                userId,
                selectedItem.getItem_id(),
                address,
                notes,
                weight,
                totalPrice
        ).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "Submit response code: " + response.code());
                if (response.isSuccessful()) {
                    Toast.makeText(FormRequest.this,
                            "Request submitted!\nTotal: RM" + String.format("%.2f", totalPrice),
                            Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(FormRequest.this, ViewSubmittedRequest.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(FormRequest.this, "Submit failed! Code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Submit error: " + t.getMessage());
                Toast.makeText(FormRequest.this, "Submit error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
