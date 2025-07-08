package com.example.group_test;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.group_test.model.RecyclableItems;
import com.example.group_test.remote.ApiUtils;
import com.example.group_test.remote.RequestService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormRequest extends AppCompatActivity {

    Spinner spinnerItems;
    EditText editAddress, editNotes;
    Button btnSubmit;

    List<RecyclableItems> itemList;
    RequestService requestService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_request);

        // Bind UI
        spinnerItems = findViewById(R.id.spinnerItems);
        editAddress = findViewById(R.id.editAddress);
        editNotes = findViewById(R.id.editNotes);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Initialize API
        requestService = ApiUtils.getRequestService();

        // Load items from API
        loadItemTypes();

        // Submit button logic
        btnSubmit.setOnClickListener(v -> {
            if (itemList == null || itemList.isEmpty()) {
                Toast.makeText(this, "No items available!", Toast.LENGTH_SHORT).show();
                return;
            }

            RecyclableItems selectedItem = itemList.get(spinnerItems.getSelectedItemPosition());
            String address = editAddress.getText().toString().trim();
            String notes = editNotes.getText().toString().trim();

            if (address.isEmpty()) {
                Toast.makeText(this, "Address is required!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Submit request
            requestService.submitRequest(selectedItem.getItem_id(), address, notes)
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(FormRequest.this, "Request submitted successfully!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(FormRequest.this, "Submission failed!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(FormRequest.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    private void loadItemTypes() {
        requestService.getItemTypes().enqueue(new Callback<List<RecyclableItems>>() {
            @Override
            public void onResponse(Call<List<RecyclableItems>> call, Response<List<RecyclableItems>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    itemList = response.body();

                    ArrayAdapter<RecyclableItems> adapter = new ArrayAdapter<>(FormRequest.this,
                            android.R.layout.simple_spinner_item,
                            itemList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerItems.setAdapter(adapter);
                } else {
                    Toast.makeText(FormRequest.this, "Failed to load items.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<RecyclableItems>> call, Throwable t) {
                Toast.makeText(FormRequest.this, "Error loading items: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
