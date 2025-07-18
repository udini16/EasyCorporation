package com.example.group_test;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group_test.adapter.SubmittedRequestAdapter;
import com.example.group_test.model.SubmittedRequest;
import com.example.group_test.model.User;
import com.example.group_test.remote.ApiUtils;
import com.example.group_test.remote.RequestService;
import com.example.group_test.sharedpref.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewSubmittedRequest extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SubmittedRequestAdapter adapter;
    private RequestService requestService;
    private static final String TAG = "ViewSubmittedRequest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_submitted_request);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        requestService = ApiUtils.getRequestService();

        loadSubmittedRequests();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSubmittedRequests(); // Refresh data when returning to this activity to reflect updates
    }

    private void loadSubmittedRequests() {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        final String token = user.getToken();
        final int userId = user.getId();

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Invalid user token", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Loading requests for user ID: " + userId);

        requestService.getSubmittedRequests(token, userId).enqueue(new Callback<List<SubmittedRequest>>() {
            @Override
            public void onResponse(Call<List<SubmittedRequest>> call, Response<List<SubmittedRequest>> response) {
                Log.d(TAG, "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    List<SubmittedRequest> requests = response.body();
                    Log.d(TAG, "Number of requests received: " + requests.size());

                    if (adapter == null) {
                        adapter = new SubmittedRequestAdapter(ViewSubmittedRequest.this, requests);
                        recyclerView.setAdapter(adapter);
                    } else {
                        // Update adapter data and refresh list
                        adapter = new SubmittedRequestAdapter(ViewSubmittedRequest.this, requests);
                        recyclerView.setAdapter(adapter);
                        // Or if your adapter supports update method, use that instead for better performance
                        // adapter.updateData(requests);
                    }
                } else {
                    Log.e(TAG, "Failed to load requests: " + response.message());
                    Toast.makeText(ViewSubmittedRequest.this,
                            "Failed to load requests. Server returned: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SubmittedRequest>> call, Throwable t) {
                Log.e(TAG, "Request failed: ", t);
                Toast.makeText(ViewSubmittedRequest.this,
                        "Failed to load requests: " + t.getLocalizedMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}