package com.exa.companydemo.base.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerHolder> {
    protected int itemLayoutId;
    private Context context;
    protected List<T> dataList;
    private OnClickItemListener listener;

    public BaseRecyclerAdapter(Context context, int itemLayoutId, List<T> dataList) {
        this.context = context;
        this.itemLayoutId = itemLayoutId;
        this.dataList = dataList;
    }

    /**
     * @param context
     * @param itemLayoutId 为0时呈现为多视图
     * @param dataList
     * @param listener
     */
    public BaseRecyclerAdapter(Context context, int itemLayoutId, List<T> dataList, OnClickItemListener listener) {
        this.context = context;
        this.itemLayoutId = itemLayoutId;
        this.dataList = dataList;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (dataList.get(position) instanceof BaseRecyclerData) {
            return ((BaseRecyclerData) dataList.get(position)).ITEM_TYPE;
        }
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public BaseRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return createHolder(parent, itemLayoutId, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerHolder holder, int position) {
        onViewHolder(holder.itemView, dataList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    /**
     * 显示数据
     *
     * @param view
     * @param data
     * @param position
     */
    protected abstract void onViewHolder(@NonNull View view, @NonNull T data, int position);

    /**
     * 创建holder
     *
     * @param parent
     * @param itemLayoutId
     * @param viewType
     * @return
     */
    protected BaseRecyclerHolder createHolder(ViewGroup parent, int itemLayoutId, int viewType) {
        if (itemLayoutId == 0) {
            return new BaseRecyclerHolder(createHolderView(viewType, parent), listener);
        }
        View view = LayoutInflater.from(context).inflate(itemLayoutId, parent, false);
        return new BaseRecyclerHolder(view, listener);
    }

    /**
     * 如果 没有列表项的布局id传入 则时多种item
     * 需实现 createViewHolder 方法
     *
     * @param viewType
     * @return
     */
    protected View createHolderView(int viewType, ViewGroup parent) {
        return null;
    }
}
