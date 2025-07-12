package com.example.group_test.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.group_test.R;
import com.example.group_test.model.SubmittedRequest;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    private final List<SubmittedRequest> requestList;
    private final Context context;
    private int currentPos = -1;

    public RequestAdapter(Context context, List<SubmittedRequest> requestList) {
        this.context = context;
        this.requestList = requestList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.request_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SubmittedRequest request = requestList.get(position);

        holder.tvUsername.setText("User: " + (request.getUser() != null ? request.getUser().getUsername() : "Unknown"));
        holder.tvItemName.setText("Item: " + (request.getItem() != null ? request.getItem().getItem_name() : "Unknown"));
        holder.tvAddress.setText("Address: " + request.getAddress());
        holder.tvStatus.setText("Status: " + request.getStatus());
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public SubmittedRequest getSelectedItem() {
        if (currentPos >= 0 && currentPos < requestList.size()) {
            return requestList.get(currentPos);
        }
        return null;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        TextView tvUsername, tvItemName, tvAddress, tvStatus;

        ViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvStatus = itemView.findViewById(R.id.tvStatus);

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            currentPos = getAdapterPosition();
            return false;
        }
    }
}
