package com.example.healthymind.ui.setting;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthymind.entity.DrawerBean;
import com.example.healthymind.recycle.adapter.BaseAdapter;


public class SettingAdapter extends BaseAdapter<DrawerBean> {
    public SettingAdapter(FragmentActivity activity) {
        super(activity, false);
    }

    @Override
    public int getActualItemViewType(int position) {
        return 0;
    }

    @Override
    protected void onActualBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        handleItemView((SettingItemViewHolder) holder, dataSet.get(position), position);
    }

    private void handleItemView(SettingItemViewHolder holder, DrawerBean item, int position) {
        holder.fillData(item, position);
        holder.setOnRecyclerViewItemClick(onRecyclerViewItemClick);
    }

    @Override
    protected RecyclerView.ViewHolder onActualCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(SettingItemViewHolder.LAYOUT_ID, parent, false);
        return new SettingItemViewHolder(v);
    }
}
