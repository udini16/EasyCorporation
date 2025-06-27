package com.example.group_test.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclableAdapter extends RecyclerView.Adapter<RecyclableAdapter.ViewHolder> {
    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvItemName;
        public TextView tvItemPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
        }
    }
}
