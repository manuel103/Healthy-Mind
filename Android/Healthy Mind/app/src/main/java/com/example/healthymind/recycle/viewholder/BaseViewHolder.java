package com.example.healthymind.recycle.viewholder;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.healthymind.listener.OnRecyclerViewItemClick;

import butterknife.ButterKnife;

public class BaseViewHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener {

    public T item;
    public Context context;
    public int position;
    private OnRecyclerViewItemClick<T> onRecyclerViewItemClick;

    public BaseViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);
    }

    public BaseViewHolder(View itemView, Context context) {
        this(itemView);
        this.context = context;
    }

    public void setOnRecyclerViewItemClick(OnRecyclerViewItemClick<T> onRecyclerViewItemClick) {
        this.onRecyclerViewItemClick = onRecyclerViewItemClick;
    }

    @Override
    public void onClick(View view) {
        View transitionView;

        if (getTransitionView() == null) {
            transitionView = view;
        } else {
            transitionView = getTransitionView();
        }
        if (onRecyclerViewItemClick != null && item != null) {
            onRecyclerViewItemClick.onItemClick(transitionView, item, position);
        }
    }

    public void fillData(T item, int position) {
        //fill data to view
        this.item = item;
        this.position = position;
        if (item == null) {
            return;
        }
    }

    protected View getTransitionView() {
        return null;
    }


}
