package com.example.healthymind.ui.important;

import android.annotation.SuppressLint;

import com.example.healthymind.entity.MessageEvent;
import com.example.healthymind.entity.Recording;
import com.example.healthymind.ui.all.AllCallFragment;

import java.util.ArrayList;
import java.util.List;


public class ImportantFragment extends AllCallFragment {
    protected List<Recording> listImportant = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    public void onUpdateView(List<Recording> allItems) {
        listImportant.clear();
        for (Recording recorder : allItems) {
            if (recorder.isImportant()) {
                listImportant.add(recorder);
            }
        }
        formatData(listImportant);
        mCallAdapter.dataSet.clear();
        mCallAdapter.loadInitialDataSet(mData);
        mRecyclerView.getRecycledViewPool().clear();
        mCallAdapter.notifyDataSetChanged();
    }

    @Override
    public void onHandleSearch(MessageEvent event) {
        if (event.getCurrentItem() == IMPORTANT) {
            filter(event.getMessage());
        }
    }
}
