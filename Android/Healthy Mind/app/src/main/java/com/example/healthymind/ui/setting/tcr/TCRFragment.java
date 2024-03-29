package com.example.healthymind.ui.setting.tcr;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.healthymind.R;
import com.example.healthymind.ui.BaseFragment;
import com.example.healthymind.util.FragmentUtil;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class TCRFragment extends BaseFragment {
    public static final String TAG = "TCRFragment";
    @BindView(R.id.txtTitle)
    protected TextView mTvTitle;
    @BindView(R.id.ivBack)
    protected ImageView ivBack;
    @BindView(R.id.txtScreen)
    protected TextView txtScreen;

    public static TCRFragment newInstance() {
        Bundle args = new Bundle();
        TCRFragment fragment = new TCRFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initData() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tcr;
    }

    @Override
    protected void initView(View root) {
        setUpToolBarView(getString(R.string.setting_tcr), txtScreen, ivBack);
        mTvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtil.showFragment(getActivity(), PolicyFragment.newInstance(), true);
            }
        });
    }

    @Override
    protected void getArgument(Bundle bundle) {

    }

    @Override
    public void initPresenter() {

    }

}
