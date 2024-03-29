package com.example.healthymind.ui.about;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.healthymind.R;
import com.example.healthymind.ui.setting.SettingFragment;
import com.example.healthymind.util.StringUtils;

import butterknife.BindView;

public class AboutFragment extends SettingFragment {

    public static final String TAG = "AboutFragment";

    public static AboutFragment newInstance() {
        Bundle args = new Bundle();
        AboutFragment fragment = new AboutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.txtAbout)
    TextView txtAbout;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void initView(View root) {
        mRecyclerView.setVisibility(View.GONE);
        txtAbout.setVisibility(View.VISIBLE);
        StringUtils.setText(String.valueOf(getText(R.string.txt_about)), txtAbout);
        setUpToolBarView(getString(R.string.about_title), txtScreen, ivBack);
    }

    @Override
    protected void getArgument(Bundle bundle) {

    }

    @Override
    public void initPresenter() {

    }
}
