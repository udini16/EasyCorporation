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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView tvRequestId, tvStatus, tvAddress;

        public ViewHolder(View itemView) {
            super(itemView);
            tvRequestId = itemView.findViewById(R.id.tvRequestId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvAddress = itemView.findViewById(R.id.tvAddress);

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            currentPos = getAdapterPosition();
            return false;
        }
    }

    private List<SubmittedRequest> requestList;
    private Context mContext;
    private int currentPos;

    public RequestAdapter(Context context, List<SubmittedRequest> listData) {
        requestList = listData;
        mContext = context;
    }

    private Context getmContext() { return mContext; }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.request_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SubmittedRequest request = requestList.get(position);
        holder.tvRequestId.setText("Request ID: " + request.getId());
        holder.tvStatus.setText("Status: " + request.getStatus());
        holder.tvAddress.setText("Address: " + request.getAddress());
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public SubmittedRequest getSelectedItem() {
        if (currentPos >= 0 && requestList != null && currentPos < requestList.size()) {
            return requestList.get(currentPos);
        }
        return null;
    }
}
