package com.example.healthymind.ui.setting;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import com.example.healthymind.R;
import com.example.healthymind.entity.DrawerBean;
import com.example.healthymind.recycle.viewholder.BaseViewHolder;
import com.example.healthymind.util.ImageUtil;
import com.example.healthymind.util.StringUtils;


public class SettingItemViewHolder extends BaseViewHolder<DrawerBean> {
    public static final int LAYOUT_ID = R.layout.item_setting;
    @BindView(R.id.txtTitle)
    TextView tvTitle;
    @BindView(R.id.ivSetting)
    ImageView imgIcon;

    public SettingItemViewHolder(View itemView) {
        super(itemView);
    }

    public void fillData(DrawerBean item, int position) {
        super.fillData(item, position);
        ImageUtil.setImageView(itemView.getContext(), item.getResId(), imgIcon);
        StringUtils.setText(item.getTitle(), tvTitle);
    }
}
