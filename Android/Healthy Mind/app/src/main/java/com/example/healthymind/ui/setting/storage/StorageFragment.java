package com.example.healthymind.ui.setting.storage;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.healthymind.R;
import com.example.healthymind.ui.BaseFragment;
import com.example.healthymind.util.Constants;
import com.example.healthymind.util.StringUtils;
import com.example.healthymind.util.UserPreferences;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;


public class StorageFragment extends BaseFragment {

    private static final int REQUEST_STORAGE_LOCATION_DCA = 0;
    private static final int REQUEST_STORAGE_LOCATION_SAF = 1;

    @BindView(R.id.locationFile)
    TextView locationFile;
    @BindView(R.id.ivBack)
    protected ImageView ivBack;
    @BindView(R.id.txtScreen)
    protected TextView txtScreen;

    public static StorageFragment newInstance() {
        Bundle args = new Bundle();
        StorageFragment fragment = new StorageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initData() {
        setUpToolBarView(getString(R.string.screen_storage_location), txtScreen, ivBack);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_storage;
    }

    @Override
    protected void initView(View root) {
        updateStorageLocationText();
    }

    @Override
    protected void getArgument(Bundle bundle) {

    }

    @Override
    public void initPresenter() {

    }

    @OnClick(R.id.llTop)
    public void onClick() {
        showChooseDataFolderDialog();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        if (requestCode == REQUEST_STORAGE_LOCATION_DCA) {
            UserPreferences.setStorageUri(Uri.fromFile(new File(
                    data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR) + File.separatorChar + "recorded")));
            updateStorageLocationText();
        } else if (requestCode == REQUEST_STORAGE_LOCATION_SAF) {
            if (data == null) return;
            Uri uri = data.getData();
            Log.i(Constants.TAG, "Uri: " + uri.toString());
            getActivity().getContentResolver().takePersistableUriPermission(uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION |
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            UserPreferences.setStorageUri(Uri.fromFile(new File(uri.getPath() + File.separatorChar + "recorded")));
            updateStorageLocationText();
        }
    }

    private void showChooseDataFolderDialog() {
        Uri currentStorage = UserPreferences.getStorageUri();
        int selectedIndex = TextUtils.equals(currentStorage.toString(), UserPreferences.default_storage.toString()) ? 0 : 1;
        List<String> choices = new ArrayList<>();
        choices.add(getString(R.string.auto));
        choices.add(getString(R.string.manual));
        onShowDialog(choices, selectedIndex);
    }

    private void onShowDialog(List<String> choices, int selectedIndex) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.choose_storage_location)
                .setSingleChoiceItems(choices.toArray(new CharSequence[choices.size()]),
                        selectedIndex,
                        (dialog1, which) -> {
                            dialog1.dismiss();
                            if (which == 1) {
                                openStorageLocationDirectoryChooser();
                            } else {
                                UserPreferences.init(getActivity());
                                UserPreferences.setStorageUri(UserPreferences.default_storage);
                                updateStorageLocationText();
                            }
                        })
                .setNegativeButton(R.string.close, (dialog1, i) -> {
                    dialog1.dismiss();
                })
                .create();
        dialog.show();
    }

    private void updateStorageLocationText() {
        String path = UserPreferences.getStorageUri().getPath().replace("/tree","");
        StringUtils.setText(path, locationFile);
    }

    private void openStorageLocationDirectoryChooser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE),
                    REQUEST_STORAGE_LOCATION_SAF);
        } else {
            startActivityForResult(new Intent(getActivity(), DirectoryChooserActivity.class),
                    REQUEST_STORAGE_LOCATION_DCA);
        }
    }
}
