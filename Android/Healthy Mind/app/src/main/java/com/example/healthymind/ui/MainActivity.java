package com.example.healthymind.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.healthymind.auth.LoginActivity;
import com.example.healthymind.auth.SessionManager;
import com.example.healthymind.ui.all.OverviewFragment;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.EventBus;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import com.example.healthymind.R;
import com.example.healthymind.entity.MessageEvent;
import com.example.healthymind.ui.adapter.ViewPagerAdapter;
import com.example.healthymind.ui.coming.InComingFragment;
import com.example.healthymind.ui.going.OutGoingFragment;
import com.example.healthymind.ui.important.ImportantFragment;
import com.example.healthymind.ui.navigation.NavigationDrawerFragment;
import com.example.healthymind.ui.setting.general.PinActivity;
import com.example.healthymind.ui.theme.MaterialTheme;
import com.example.healthymind.ui.theme.SetThemeDialogFragment;
import com.example.healthymind.util.Constants;
import com.example.healthymind.util.DialogUtils;
import com.example.healthymind.util.MySharedPreferences;

public class MainActivity extends BaseActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final String KEY_ARG_CURRENT_THEME = "KEY_ARG_CURRENT_THEME";

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.tabs)
    protected TabLayout tableLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.adView)
     AdView mAdView;
     Drawable drawable;

     Interpreter interpreter;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    public MainActivity() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {

//        try {
//            interpreter = new Interpreter(loadModelFile(), null);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        if (MySharedPreferences.getInstance(this).getBoolean(MySharedPreferences.KEY_PIN, false)) {
            startActivity(new Intent(MainActivity.this, PinActivity.class));
        }
        setUpToolbar();
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, toolbar, mDrawerLayout);
        setUpViewPager(viewPager);
        tableLayout.setupWithViewPager(viewPager);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpViewPager(viewPager);
        tableLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void initData() {
    }

    private void setUpViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new OverviewFragment(), getString(R.string.tab_overview));
        adapter.addFrag(new InComingFragment(), getString(R.string.tab_incoming));
        adapter.addFrag(new OutGoingFragment(), getString(R.string.tab_outgoing));
        adapter.addFrag(new ImportantFragment(), getString(R.string.tab_important));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
        tableLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorWhite));
        tableLayout.setTabTextColors(getResources().getColor(R.color.colorGray79), getResources().getColor(R.color.colorWhite));
    }

    public void setUpToolbar() {
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void checkPerms() {
        String[] perms = new String[]{
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.PROCESS_OUTGOING_CALLS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        List<String> requestingPerms = new ArrayList<>();
        for (String perm : perms) {
            if (checkSelfPermission(perm) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestingPerms.add(perm);
            }
        }
        if (requestingPerms.size() > 0) {
            requestPermissions(requestingPerms.toArray(new String[requestingPerms.size()]), 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (drawable == null) {
                        drawable = toolbar.getNavigationIcon();
                    }
//                    toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
//                    toolbar.setNavigationIcon(androidx.core.R.drawable.abc_ic_ab_back_material);
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            searchView.clearFocus();
                        }
                    });
                } else {
                    if (drawable != null) {
                        toolbar.setNavigationIcon(drawable);
                        setUpToolbar();
                    }
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                int currentItem = viewPager.getCurrentItem();
                EventBus.getDefault().post(new MessageEvent(s, currentItem));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

//        SessionManager sessionManager = new SessionManager(MainActivity.this, SessionManager.SESSION_REMEMBERME);
//        if (sessionManager.checkRememberMe()) {
//            HashMap<String, String> rememberMeDetails = sessionManager.getRememberMeDetailFromSession();
//            String username = rememberMeDetails.get(SessionManager.KEY_SESSIONUSERNAME);
//
//            Log.d(username, "Is the current logged in user....");
//        }

        return true;

    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            int backStackCnt = getSupportFragmentManager().getBackStackEntryCount();
            if (backStackCnt > 1) {
                getSupportFragmentManager().popBackStack();
            } else {
                showConfirmDialog();
            }
        }
    }

    private void showConfirmDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getString(R.string.txt_comfirm));
        alertDialog.setPositiveButton(getString(R.string.dialog_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        alertDialog.setNegativeButton(getString(R.string.dialog_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

        @Override
    public void onNavigationDrawerItemSelected(int type, int position) {
        switch (position) {
            case 0:
                break;
            case 1:
                Intent intentDelete = new Intent(getBaseContext(), DetailActivity.class);
                intentDelete.putExtra(Constants.KEY_SCREEN, DetailActivity.KEY_DELETE);
                startActivity(intentDelete);
                break;
            case 2:
                Intent intentSetting = new Intent(getBaseContext(), DetailActivity.class);
                intentSetting.putExtra(Constants.KEY_SCREEN, DetailActivity.KEY_SETTING);
                startActivity(intentSetting);
                break;
//            case 3:
//                Intent intentSendMail = new Intent(Intent.ACTION_SENDTO);
//                intentSendMail.setType("text/plain");
//                intentSendMail.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
//                intentSendMail.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_body));
//                intentSendMail.setData(Uri.parse("mailto:harryhaivn@gmail.com"));
//                intentSendMail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intentSendMail);
//                break;
//            case 4:
//                Intent intentHelp = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=vn.harry.callrecorder"));
//                startActivity(intentHelp);
//                break;
//            case 5:
//                goApp(true, null);
//                break;
            case 3:
                Intent intentAbout = new Intent(getBaseContext(), DetailActivity.class);
                intentAbout.putExtra(Constants.KEY_SCREEN, DetailActivity.KEY_ABOUT);
                startActivity(intentAbout);
                break;
//            case 7:
//                Intent intentDonate = new Intent(getBaseContext(), DetailActivity.class);
//                intentDonate.putExtra(Constants.KEY_SCREEN, DetailActivity.KEY_DONATE);
//                startActivity(intentDonate);
//                break;
//            case 8:
//                goApp(true, "com.example.healthymind");
//                break;
            case 4:
                DialogFragment dialogFragment = SetThemeDialogFragment.newInstance(new MaterialTheme(theme));
                DialogUtils.showDialogFragment(getSupportFragmentManager(), dialogFragment);
                break;
            case 5:
                finish();
                break;
//            case 11:
//                break;
            default:
                break;
        }
        onCloseNavigation();
    }

//    @Override
//    public void onNavigationDrawerItemSelected(int type, int position) {
//        switch (position) {
//            case 0:
//                break;
//            case 1:
//                Intent intentDelete = new Intent(getBaseContext(), DetailActivity.class);
//                intentDelete.putExtra(Constants.KEY_SCREEN, DetailActivity.KEY_DELETE);
//                startActivity(intentDelete);
//                break;
//            case 2:
//                Intent intentSetting = new Intent(getBaseContext(), DetailActivity.class);
//                intentSetting.putExtra(Constants.KEY_SCREEN, DetailActivity.KEY_SETTING);
//                startActivity(intentSetting);
//                break;
//            case 3:
//                Intent intentSendMail = new Intent(Intent.ACTION_SENDTO);
//                intentSendMail.setType("text/plain");
//                intentSendMail.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
//                intentSendMail.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_body));
//                intentSendMail.setData(Uri.parse("mailto:harryhaivn@gmail.com"));
//                intentSendMail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intentSendMail);
//                break;
//            case 4:
//                Intent intentHelp = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=vn.harry.callrecorder"));
//                startActivity(intentHelp);
//                break;
//            case 5:
//                goApp(true, null);
//                break;
//            case 6:
//                Intent intentAbout = new Intent(getBaseContext(), DetailActivity.class);
//                intentAbout.putExtra(Constants.KEY_SCREEN, DetailActivity.KEY_ABOUT);
//                startActivity(intentAbout);
//                break;
//            case 7:
//                Intent intentDonate = new Intent(getBaseContext(), DetailActivity.class);
//                intentDonate.putExtra(Constants.KEY_SCREEN, DetailActivity.KEY_DONATE);
//                startActivity(intentDonate);
//                break;
//            case 8:
//                goApp(true, "com.example.healthymind");
//                break;
//            case 9:
//                DialogFragment dialogFragment = SetThemeDialogFragment.newInstance(new MaterialTheme(theme));
//                DialogUtils.showDialogFragment(getSupportFragmentManager(), dialogFragment);
//                break;
//            case 10:
//                finish();
//                break;
//            case 11:
//                break;
//            default:
//                break;
//        }
//        onCloseNavigation();
//    }

    public void openDrawer() {
        mDrawerLayout.requestLayout();
        mDrawerLayout.openDrawer(Gravity.START);
    }

    @Override
    public void onCloseNavigation() {
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }


//    public void goApp(boolean googlePlay, String packageName) {//true if Google Play, false if Amazone Store
//        try {
//            if (packageName == null) {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse((googlePlay ? "market://details?id=" : "amzn://apps/android?p=") + getPackageName())));
//            } else {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse((googlePlay ? "market://details?id=" : "amzn://apps/android?p=") + packageName)));
//            }
//
//        } catch (ActivityNotFoundException e1) {
//            try {
//                if (packageName == null) {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse((googlePlay ? "http://play.google.com/store/apps/details?id=" : "http://www.amazon.com/gp/mas/dl/android?p=") + getPackageName())));
//                } else {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse((googlePlay ? "http://play.google.com/store/apps/details?id=" : "http://www.amazon.com/gp/mas/dl/android?p=") + packageName)));
//                }
//            } catch (ActivityNotFoundException e2) {
//                Toast.makeText(this, getString(R.string.can_openlink), Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

//    private MappedByteBuffer loadModelFile() throws IOException{
//        AssetFileDescriptor assetFileDescriptor = this.getAssets().openFd("cnn_model.tflite");
//        FileInputStream fileInputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
//        FileChannel fileChannel = fileInputStream.getChannel();
//
//        long startOffset = assetFileDescriptor.getStartOffset();
//        long length = assetFileDescriptor.getLength();
//
//        return  fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, length);
//    }

}