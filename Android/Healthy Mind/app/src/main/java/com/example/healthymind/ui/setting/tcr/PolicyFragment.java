package com.example.healthymind.ui.setting.tcr;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import com.example.healthymind.R;
import com.example.healthymind.ui.BaseFragment;


public class PolicyFragment extends BaseFragment {
    public static final String TAG = "PolicyFragment";
    @BindView(R.id.ivBack)
    protected ImageView ivBack;
    @BindView(R.id.txtScreen)
    protected TextView txtScreen;
    @BindView(R.id.wvPolicy)
    protected WebView mWebView;

    public static PolicyFragment newInstance() {
        Bundle args = new Bundle();
        PolicyFragment fragment = new PolicyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initData() {
        startWebView(mWebView, "https://carstly.com/privacy-policy");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_policy;
    }

    @Override
    protected void initView(View root) {
        setUpToolBarView("https://carstly.com/privacy-policy", txtScreen, ivBack);
    }

    @Override
    protected void getArgument(Bundle bundle) {

    }

    @Override
    public void initPresenter() {

    }

}
