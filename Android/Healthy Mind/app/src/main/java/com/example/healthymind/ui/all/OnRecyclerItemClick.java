package com.example.healthymind.ui.all;

import android.view.View;

import com.example.healthymind.entity.Recording;

/**
 * Created by Harry_Hai on 2/21/2018.
 */

interface OnRecyclerItemClick<T> {
    void onItemClick(View view, Recording item, int position);
}
