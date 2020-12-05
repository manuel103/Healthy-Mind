package com.example.healthymind.ui.coming;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.List;

import com.example.healthymind.entity.MessageEvent;
import com.example.healthymind.entity.Recording;
import com.example.healthymind.ui.all.AllCallFragment;

public class InComingFragment extends AllCallFragment {
    protected List<Recording> listInComing = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    public void onUpdateView(List<Recording> allItems) {
        listInComing.clear();
        for (Recording recorder : allItems) {
            if (!recorder.isOutGoing()) {
                listInComing.add(recorder);
            }
        }
        formatData(listInComing);
        mCallAdapter.dataSet.clear();
        mCallAdapter.loadInitialDataSet(mData);
        mRecyclerView.getRecycledViewPool().clear();
        mCallAdapter.notifyDataSetChanged();
    }

    @Override
    public void onHandleSearch(MessageEvent event) {
        if (event.getCurrentItem() == INCOMING) {
            filter(event.getMessage());
        }
    }
}
