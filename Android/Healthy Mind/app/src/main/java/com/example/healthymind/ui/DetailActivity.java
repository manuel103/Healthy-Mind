package com.example.healthymind.ui;

import com.example.healthymind.R;
import com.example.healthymind.ui.about.AboutFragment;
import com.example.healthymind.ui.donate.DonateOptionFragment;
import com.example.healthymind.ui.recycleBin.RecycleBinFragment;
import com.example.healthymind.ui.setting.SettingFragment;
import com.example.healthymind.util.Constants;
import com.example.healthymind.util.FragmentUtil;


public class DetailActivity extends BaseActivity {
    public static String KEY_SETTING = "Settings";
    public static String KEY_ABOUT = "About";
    public static String KEY_DONATE = "Donate";
    public static String KEY_DELETE = "Delete";
    public static String KEY_NUMBER = "Number";
    public static String KEY_PROFILE = "Profile";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_detail;
    }

    @Override
    protected void initView() {
        String screen = getIntent().getStringExtra(Constants.KEY_SCREEN);
        if (screen.equals(KEY_SETTING)) {
            FragmentUtil.showFragment(this, SettingFragment.newInstance(), true);
        } else if (screen.equals(KEY_ABOUT)) {
            FragmentUtil.showFragment(this, AboutFragment.newInstance(), false, null, AboutFragment.TAG, false, null);
        } else if (screen.equals(KEY_DONATE)) {
            FragmentUtil.showFragment(this, DonateOptionFragment.newInstance(), false, null, DonateOptionFragment.TAG, false, null);
        } else if (screen.equals(KEY_DELETE)) {
            FragmentUtil.showFragment(this, RecycleBinFragment.newInstance(), false, null, AboutFragment.TAG, false, null);
        } else {
            finish();
            return;
        }
    }

    @Override
    protected void initData() {

    }
}
