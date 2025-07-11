package com.example.group_test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group_test.adapter.RequestAdapter;
import com.example.group_test.model.SubmittedRequest;
import com.example.group_test.model.User;
import com.example.group_test.remote.ApiUtils;
import com.example.group_test.remote.RequestService;
import com.example.group_test.sharedpref.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestListActivity extends AppCompatActivity {

    private RecyclerView rvRequestList;
    private RequestAdapter requestAdapter;
    private RequestService requestService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_request_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvRequestList = findViewById(R.id.rvRequestList);
        rvRequestList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        registerForContextMenu(rvRequestList);

        updateRecyclerView();
    }
    @Override
    protected void onResume(){
        super.onResume();
        updateRecyclerView();
    }

    private void updateRecyclerView(){
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        requestService = ApiUtils.getRequestService();

        requestService.getAllRequests(user.getToken()).enqueue(new Callback<List<SubmittedRequest>>() {
            @Override
            public void onResponse(Call<List<SubmittedRequest>> call, Response<List<SubmittedRequest>> response) {
                Log.d("MyApp:", "Response: " + response.raw().toString());
                if (response.code() == 200){
                    List<SubmittedRequest> requests = response.body();
                    requestAdapter = new RequestAdapter(getApplicationContext(), requests);
                    rvRequestList.setAdapter(requestAdapter);

                    rvRequestList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvRequestList.getContext(),
                            DividerItemDecoration.VERTICAL);
                    rvRequestList.addItemDecoration(dividerItemDecoration);
                }
                else if (response.code() == 401){
                    // invalid token, ask user to relogin
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<List<SubmittedRequest>> call, Throwable t ) {
                Toast.makeText(getApplicationContext(), "Error connecting to the server", Toast.LENGTH_LONG).show();
                Log.e("MyApp:", t.toString());
            }
        });
    }

    private void clearSessionAndRedirect(){
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();
        finish();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.request_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        SubmittedRequest selectedRequest = requestAdapter.getSelectedItem();
        Log.d("MyApp", "selected " + selectedRequest.toString());    // debug purpose

        if (item.getItemId() == R.id.menu_update) {
            // user clicked details contextual menu
            doUpdateRequest(selectedRequest);
        }
        return super.onContextItemSelected(item);
    }
    private void doUpdateRequest(SubmittedRequest selectedRequest) {
        Toast.makeText(this, "Update status for request id: " +selectedRequest.getId(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), UpdateRequestActivity.class);
        intent.putExtra("item_id", selectedRequest.getId());
        startActivity(intent);
    }
}