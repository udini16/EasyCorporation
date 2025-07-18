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

import com.example.group_test.adapter.RecyclableAdapter;
import com.example.group_test.model.RecyclableItems;
import com.example.group_test.model.User;
import com.example.group_test.sharedpref.SharedPrefManager;
import com.example.group_test.remote.ApiUtils;
import com.example.group_test.remote.RecyclableService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecyclableListActivity extends AppCompatActivity {

    private RecyclerView rvRecycleList;
    private RecyclableService recyclableService;

    private RecyclableAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recyclable_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        rvRecycleList = findViewById(R.id.rvRecycleList);

        registerForContextMenu(rvRecycleList);

        updateRecyclerView();
    }

    @Override
    protected void onResume(){
        super.onResume();
        updateRecyclerView();
    }


    private void updateRecyclerView() {
         // get user info from SharedPreferences to get token value
         SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
         User user = spm.getUser();
         String token = user.getToken();

         // get book service instance
         recyclableService = ApiUtils.getRecyclableService();

         // execute the call. send the user token when sending the query
         recyclableService.getAllRecyclables(token).enqueue(new Callback<List<RecyclableItems>>() {
             @Override
             public void onResponse(Call<List<RecyclableItems>> call, Response<List<RecyclableItems>> response) {
                 // for debug purpose
                 Log.d("MyApp:", "Response: " + response.raw().toString());

                 if (response.code() == 200) {
                     // Get list of book object from response
                     List<RecyclableItems> recyclables = response.body();

                     // initialize adapter
                     adapter = new RecyclableAdapter(getApplicationContext(), recyclables);

                     // set adapter to the RecyclerView
                     rvRecycleList.setAdapter(adapter);

                     // set layout to recycler view
                     rvRecycleList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                     // add separator between item in the list
                     DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvRecycleList.getContext(),
                             DividerItemDecoration.VERTICAL);
                     rvRecycleList.addItemDecoration(dividerItemDecoration);
                 }
                 else if (response.code() == 401) {
                     // invalid token, ask user to relogin
                     Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                     clearSessionAndRedirect();
                 }
                 else {
                     Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                     // server return other error
                     Log.e("MyApp: ", response.toString());
                 }
             }

             @Override
             public void onFailure(Call<List<RecyclableItems>> call, Throwable t) {
                 Toast.makeText(getApplicationContext(), "Error connecting to the server", Toast.LENGTH_LONG).show();
                 Log.e("MyApp:", t.toString());
             }
         });
    }

    public void clearSessionAndRedirect() {
        // clear the shared preferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();

        // terminate this MainActivity
        finish();

        // forward to Login Page
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recyclable_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        RecyclableItems selectedItem = adapter.getSelectedItem();
        Log.d("MyApp", "selected "+selectedItem.toString());    // debug purpose

        if (item.getItemId() == R.id.menu_details) {
            // user clicked details contextual menu
            doViewDetails(selectedItem);
        }
        else if (item.getItemId() == R.id.menu_update) {
            // user clicked the update contextual menu
            doUpdateItem(selectedItem);
        }

        return super.onContextItemSelected(item);
    }

    private void doViewDetails(RecyclableItems selectedItem) {
        Log.d("MyApp:", "viewing details: " + selectedItem.toString());
        // forward user to BookDetailsActivity, passing the selected book id
        Intent intent = new Intent(getApplicationContext(), RecyclableDetailsActivity.class);
        intent.putExtra("item_id", selectedItem.getItem_id());
        startActivity(intent);
    }

    private void doUpdateItem(RecyclableItems selectedBook) {
        Log.d("MyApp:", "updating item: " + selectedBook.toString());
        // forward user to UpdateBookActivity, passing the selected book id
        Intent intent = new Intent(getApplicationContext(), UpdateRecyclableActivity.class);
        intent.putExtra("item_id", selectedBook.getItem_id());
        startActivity(intent);
    }

    public void floatingAddItemClicked(View view){
        Intent intent = new Intent(RecyclableListActivity.this, NewRecyclableActivity.class);
        startActivity(intent);
    }
}

