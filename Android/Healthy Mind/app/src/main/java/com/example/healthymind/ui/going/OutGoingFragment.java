package com.example.healthymind.ui.going;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.List;

import com.example.healthymind.entity.MessageEvent;
import com.example.healthymind.entity.Recording;
import com.example.healthymind.ui.all.AllCallFragment;

public class OutGoingFragment extends AllCallFragment {
    protected List<Recording> listOutGoing = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    public void onUpdateView(List<Recording> allItems) {
        listOutGoing.clear();
        for (Recording recorder : allItems) {
            if (recorder.isOutGoing()) {
                listOutGoing.add(recorder);
            }
        }
        formatData(listOutGoing);
        mCallAdapter.dataSet.clear();
        mCallAdapter.loadInitialDataSet(mData);
        mRecyclerView.getRecycledViewPool().clear();
        mCallAdapter.notifyDataSetChanged();
    }

    @Override
    public void onHandleSearch(MessageEvent event) {
        if (event.getCurrentItem() == OUTGOING) {
            filter(event.getMessage());
        }
    }
}
