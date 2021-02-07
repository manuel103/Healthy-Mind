package com.example.healthymind.ui.navigation;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.healthymind.R;
import com.example.healthymind.entity.DrawerBean;
import com.example.healthymind.recycle.viewholder.BaseViewHolder;
import com.example.healthymind.util.ImageUtil;
import com.example.healthymind.util.StringUtils;

import butterknife.BindView;

public class DrawerItemViewHolder extends BaseViewHolder<DrawerBean> {

    public static final int LAYOUT_ID = R.layout.item_navigation_drawer;
    @BindView(R.id.txtTitle)
    TextView tvTitle;
    @BindView(R.id.ivNavi)
    ImageView imgIcon;
    @BindView(R.id.root)
    RelativeLayout mRoot;

    public DrawerItemViewHolder(View itemView) {
        super(itemView);
    }

    public void fillData(DrawerBean item, int positionSelected, int position) {
        super.fillData(item, position);
        ImageUtil.setImageView(itemView.getContext(), item.getResId(), imgIcon);
        StringUtils.setText(item.getTitle(), tvTitle);
        if (position == positionSelected) {
            mRoot.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.colorGray79));
        } else {
            mRoot.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.colorWhite));
        }
    }
}
