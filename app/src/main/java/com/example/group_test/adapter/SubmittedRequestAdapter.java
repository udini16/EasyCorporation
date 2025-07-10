package com.example.group_test.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group_test.R;
import com.example.group_test.model.SubmittedRequest;

import java.util.List;

public class SubmittedRequestAdapter extends RecyclerView.Adapter<SubmittedRequestAdapter.ViewHolder> {

    private final Context context;
    private final List<SubmittedRequest> requestList;

    public SubmittedRequestAdapter(Context context, List<SubmittedRequest> requestList) {
        this.context = context;
        this.requestList = requestList;
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

        // Get item name safely
        String itemName = "Unknown";
        if (request.getItem() != null && request.getItem().getItem_name() != null) {
            itemName = request.getItem().getItem_name();
        }

        holder.itemName.setText("Item: " + itemName);
        holder.address.setText("Address: " + request.getAddress());
        holder.notes.setText("Notes: " + request.getNotes());
        holder.status.setText("Status: " + request.getStatus());
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, address, notes, status;

        public ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.textViewItemName);
            address = itemView.findViewById(R.id.textViewAddress);
            notes = itemView.findViewById(R.id.textViewNotes);
            status = itemView.findViewById(R.id.textViewStatus);
        }
    }
}
