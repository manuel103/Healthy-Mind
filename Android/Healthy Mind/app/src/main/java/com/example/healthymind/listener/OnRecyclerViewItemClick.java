package com.example.healthymind.listener;

import android.view.View;

public interface OnRecyclerViewItemClick<T> {
    void onItemClick(View view, T item, int position);
}
