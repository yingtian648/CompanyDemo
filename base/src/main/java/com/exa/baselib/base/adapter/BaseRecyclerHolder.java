package com.exa.baselib.base.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BaseRecyclerHolder extends RecyclerView.ViewHolder {
    public BaseRecyclerHolder(@NonNull View itemView, OnClickItemListener listener) {
        super(itemView);
        if (listener != null) {
            itemView.setOnClickListener(v -> listener.onClickItem(getLayoutPosition()));
            itemView.setOnLongClickListener(v -> {
                listener.onLongClickItem(getLayoutPosition());
                return true;
            });
        }
    }
}
