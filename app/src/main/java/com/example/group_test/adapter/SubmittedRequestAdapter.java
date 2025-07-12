package com.example.group_test.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group_test.R;
import com.example.group_test.UserUpdateRequestActivity;
import com.example.group_test.model.SubmittedRequest;
import com.example.group_test.remote.ApiUtils;
import com.example.group_test.remote.RequestService;
import com.example.group_test.sharedpref.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubmittedRequestAdapter extends RecyclerView.Adapter<SubmittedRequestAdapter.ViewHolder> {

    private final Context context;
    private final List<SubmittedRequest> requestList;
    private final RequestService requestService;

    public SubmittedRequestAdapter(Context context, List<SubmittedRequest> requestList) {
        this.context = context;
        this.requestList = requestList;
        this.requestService = ApiUtils.getRequestService();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_submitted_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubmittedRequest request = requestList.get(position);

        String itemName = (request.getItem() != null && request.getItem().getItem_name() != null)
                ? request.getItem().getItem_name()
                : "Unknown";

        holder.itemName.setText("Item: " + itemName);
        holder.address.setText("Address: " + request.getAddress());
        holder.notes.setText("Notes: " + request.getNotes());
        holder.weight.setText(String.format("Weight: %.2f kg", request.getWeight()));
        holder.totalPrice.setText(String.format("Total Price: RM %.2f", request.getTotal_price()));
        holder.status.setText("Status: " + request.getStatus());

        // Highlight and allow clicking to update status if it's pending
        if ("Pending".equalsIgnoreCase(request.getStatus())) {
            holder.status.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
        } else {
            holder.status.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray));
        }
        holder.status.setOnClickListener(null);


        holder.itemView.setOnLongClickListener(v -> {
            Log.d("LONG_CLICK", "Clicked on: " + request.toString());
            Intent intent = new Intent(context, UserUpdateRequestActivity.class);
            intent.putExtra("request", request); // Make sure SubmittedRequest implements Serializable
            context.startActivity(intent);
            return true;
        });

    }

    private void updateStatusToCancelled(SubmittedRequest request, int position) {
        SharedPrefManager spm = new SharedPrefManager(context);
        String apiKey = spm.getUser().getToken();

        if (apiKey == null || apiKey.isEmpty()) {
            Toast.makeText(context, "Invalid session. Please login again", Toast.LENGTH_SHORT).show();
            return;
        }

        // Extract the other required fields from the request
        String address = request.getAddress();
        String notes = request.getNotes();
        float weight = request.getWeight();
        String status = "Cancelled";

        Log.d("CANCEL_UPDATE", "ID: " + request.getId());
        Log.d("CANCEL_UPDATE", "Address: " + address);
        Log.d("CANCEL_UPDATE", "Notes: " + notes);
        Log.d("CANCEL_UPDATE", "Weight: " + weight);
        Log.d("CANCEL_UPDATE", "Status: " + status);

        float totalPrice = 0;
        requestService.updateFullRequest(apiKey, request.getId(), address, notes, weight, status, totalPrice)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            request.setStatus("Cancelled");
                            notifyItemChanged(position);
                            Toast.makeText(context, "Status updated to Cancelled", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 401) {
                            Toast.makeText(context, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Update failed: " + response.code(), Toast.LENGTH_SHORT).show();
                            Log.e("CANCEL_UPDATE", "Failed: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("CANCEL_UPDATE", "Error: " + t.getMessage());
                    }
                });
    }


    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, address, notes, weight, totalPrice, status;

        public ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.textViewItemName);
            address = itemView.findViewById(R.id.textViewAddress);
            notes = itemView.findViewById(R.id.textViewNotes);
            weight = itemView.findViewById(R.id.textViewWeight);
            totalPrice = itemView.findViewById(R.id.textViewTotalPrice);
            status = itemView.findViewById(R.id.textViewStatus);
        }
    }
}
