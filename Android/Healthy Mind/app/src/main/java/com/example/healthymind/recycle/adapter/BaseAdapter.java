package com.example.healthymind.recycle.adapter;


import androidx.fragment.app.FragmentActivity;

import com.example.healthymind.listener.OnRecyclerViewItemClick;


public abstract class BaseAdapter<D> extends NormalAdapter<D> {

    public OnRecyclerViewItemClick<D> onRecyclerViewItemClick;
    protected boolean hasMore = false;
    protected FragmentActivity activity;

    public BaseAdapter(FragmentActivity activity, boolean hasCustomLoadMoreHolder) {
        super(activity, hasCustomLoadMoreHolder);
        this.activity = activity;
    }

    @Override
    public boolean hasMoreData() {
        return hasMore;
    }

    public void setOnRecyclerViewItemClick(OnRecyclerViewItemClick<D> onRecyclerViewItemClick) {
        this.onRecyclerViewItemClick = onRecyclerViewItemClick;
    }

    public void setHasMoreData(boolean hasMore) {
        this.hasMore = hasMore;
    }

}
