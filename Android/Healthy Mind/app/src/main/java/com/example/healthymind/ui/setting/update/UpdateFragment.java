package com.example.healthymind.ui.setting.update;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.healthymind.R;
import com.example.healthymind.ui.BaseFragment;
import com.example.healthymind.util.MySharedPreferences;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateFragment extends BaseFragment {
    @BindView(R.id.ivBack)
    protected ImageView ivBack;
    @BindView(R.id.txtScreen)
    protected TextView txtScreen;
    @BindView(R.id.switchGA)
    protected Switch mSwitchGa;

    public UpdateFragment() {
    }

    public static UpdateFragment newInstance() {
        UpdateFragment fragment = new UpdateFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_update;
    }

    @Override
    protected void initView(View root) {
        setUpToolBarView(getString(R.string.screen_update), txtScreen, ivBack);
        Activity activity = getActivity();
        mSwitchGa.setChecked(MySharedPreferences.getInstance(getActivity()).getBoolean(MySharedPreferences.KEY_GA, false));
        mSwitchGa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Tracker t = ((App) activity.getApplication()).getDefaultTracker();
//                t.enableAutoActivityTracking(isChecked);
//                MySharedPreferences.getInstance(getActivity()).putBoolean(MySharedPreferences.KEY_GA, isChecked);
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
