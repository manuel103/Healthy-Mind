package com.example.healthymind.ui.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthymind.R;
import com.example.healthymind.entity.DrawerBean;
import com.example.healthymind.listener.OnRecyclerViewItemClick;
import com.example.healthymind.ui.BaseFragment;
import com.example.healthymind.ui.profile.ProfileActivity;
import com.example.healthymind.ui.setting.general.GeneralFragment;
import com.example.healthymind.ui.setting.recorder.RecordingFragment;
import com.example.healthymind.util.FragmentUtil;
import com.example.healthymind.util.ValueConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SettingFragment extends BaseFragment<SettingPresenter> implements SettingMvpView, OnRecyclerViewItemClick {
    public static final String TAG = "SettingFragment";
    @BindView(R.id.rvSetting)
    protected RecyclerView mRecyclerView;
    @BindView(R.id.ivBack)
    protected ImageView ivBack;
    @BindView(R.id.txtScreen)
    protected TextView txtScreen;
    protected SettingAdapter mSettingAdapter;
    protected List<DrawerBean> mDrawerBeans;
    protected LinearLayoutManager mLinearLayoutManager;

    public static SettingFragment newInstance() {
        Bundle args = new Bundle();
        SettingFragment fragment = new SettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void initView(View root) {
        setUpToolBarView(getString(R.string.screen_setting), txtScreen, ivBack);
    }

    @Override
    protected void initData() {

        mDrawerBeans = new ArrayList<>();
        mDrawerBeans.add(new DrawerBean(R.drawable.ic_setting, getString(R.string.setting_general), ValueConstants.NavigationItemType.Item));
        mDrawerBeans.add(new DrawerBean(R.drawable.ic_recorder_brown, getString(R.string.setting_recording), ValueConstants.NavigationItemType.Item));
//        mDrawerBeans.add(new DrawerBean(R.drawable.ic_cloud_upload, getString(R.string.setting_cloud), ValueConstants.NavigationItemType.Item));
//        mDrawerBeans.add(new DrawerBean(R.drawable.ic_sync, getString(R.string.recording_transfer), ValueConstants.NavigationItemType.Item));
//        mDrawerBeans.add(new DrawerBean(R.drawable.ic_cloud_update, getString(R.string.setting_update), ValueConstants.NavigationItemType.Item));
//        mDrawerBeans.add(new DrawerBean(R.drawable.ic_support, getString(R.string.setting_tcr) + "", ValueConstants.NavigationItemType.Item));
//        mDrawerBeans.add(new DrawerBean(R.drawable.ic_storage_location, getString(R.string.storage_location_title), ValueConstants.NavigationItemType.Item));
        mDrawerBeans.add(new DrawerBean(R.drawable.ic_person, getString(R.string.setting_profile), ValueConstants.NavigationItemType.Item));


        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mSettingAdapter = new SettingAdapter(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mSettingAdapter.loadInitialDataSet(mDrawerBeans);
        mRecyclerView.setAdapter(mSettingAdapter);
        mSettingAdapter.setOnRecyclerViewItemClick(this);
    }

    @Override
    protected void getArgument(Bundle bundle) {

    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void onResponseError(int apiMethod, int statusCode) {

    }

    @Override
    public void onItemClick(View view, Object item, int position) {
        switch (position) {
//            case 2:
//                FragmentUtil.showFragment(getActivity(), StorageFragment.newInstance(), true);
//                break;
//            case 2:
//                FragmentUtil.showFragment(getActivity(), TCRFragment.newInstance(), true);
//                break;
//            case 3:
//                FragmentUtil.showFragment(getActivity(), UpdateFragment.newInstance(), true);
//                break;
            case 2:
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
                break;
//            case 3:
//                break;
//            case 2:
//                FragmentUtil.showFragment(getActivity(), CloudFragment.newInstance(), true);
//                break;
            case 1:
                FragmentUtil.showFragment(getActivity(), RecordingFragment.newInstance(), true);
                break;
            case 0:
                FragmentUtil.showFragment(getActivity(), GeneralFragment.newInstance(), true);
                break;
        }
    }
}
