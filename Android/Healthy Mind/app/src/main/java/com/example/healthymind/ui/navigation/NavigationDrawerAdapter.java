package com.example.healthymind.ui.navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthymind.entity.DrawerBean;
import com.example.healthymind.recycle.adapter.BaseAdapter;
import com.example.healthymind.util.ValueConstants;

public class NavigationDrawerAdapter extends BaseAdapter<DrawerBean> {

    private int mCurrentSelectedPosition;

    public NavigationDrawerAdapter(FragmentActivity activity, int currentSelectedPosition) {
        super(activity, false);
        this.mCurrentSelectedPosition = currentSelectedPosition;
    }

    @Override
    public int getActualItemViewType(int position) {
        if (position == 0) {
            return ValueConstants.NavigationItemType.Header;
        } else {
            return ValueConstants.NavigationItemType.Item;
        }
    }

    @Override
    protected void onActualBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ValueConstants.NavigationItemType.Header:
                handleHeaderView((DrawerHeaderViewHolder) holder, dataSet.get(position), position);
                break;
            case ValueConstants.NavigationItemType.Item:
                handleItemView((DrawerItemViewHolder) holder, dataSet.get(position), position);
                break;
            default:
        }
    }

    private void handleItemView(DrawerItemViewHolder holder, DrawerBean item, int position) {
        holder.fillData(item, mCurrentSelectedPosition, position);
        holder.setOnRecyclerViewItemClick(onRecyclerViewItemClick);
    }

    private void handleHeaderView(DrawerHeaderViewHolder holder, DrawerBean item, int position) {
        holder.fillData(item, position);
    }

    @Override
    protected RecyclerView.ViewHolder onActualCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == ValueConstants.NavigationItemType.Header) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(DrawerHeaderViewHolder.LAYOUT_ID, parent, false);
            return new DrawerHeaderViewHolder(v);
        } else if (viewType == ValueConstants.NavigationItemType.Item) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(DrawerItemViewHolder.LAYOUT_ID, parent, false);
            return new DrawerItemViewHolder(v);
        } else {
            return null;
        }
    }

    public void setCurrentSelectedPosition(int currentSelectedPosition) {
        this.mCurrentSelectedPosition = currentSelectedPosition;
    }
}
