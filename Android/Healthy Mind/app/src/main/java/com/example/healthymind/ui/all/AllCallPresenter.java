package com.example.healthymind.ui.all;

import android.text.TextUtils;

import androidx.fragment.app.FragmentActivity;

import com.example.healthymind.entity.Recording;
import com.example.healthymind.helper.FileHelper;
import com.example.healthymind.mvp.BasePresenter;
import com.example.healthymind.util.MySharedPreferences;

import java.util.ArrayList;
import java.util.List;


public class AllCallPresenter extends BasePresenter<AllCallMvpView> {
    private List<Recording> listDir = new ArrayList<>();
    private List<Recording> listDirNew = new ArrayList<>();

    public void updateDataToDB(FragmentActivity activity) {
        listDir = FileHelper.listRecordings(activity);
        Recording.insertList(listDir);
        String day = MySharedPreferences.getInstance(activity).getString(MySharedPreferences.KEY_AUTO_DELETE, "");
        if (!TextUtils.isEmpty(day)) {
            Recording.deleteRecordList(Recording.getAllItems(), Integer.parseInt(day));
        }

        getMvpView().onUpdateView(onDeleteEmpty(Recording.getAllItems()));
    }

    public List<Recording> onDeleteEmpty(List<Recording> allItems) {
        listDirNew.clear();
        for (int i = 0; i < allItems.size(); i++) {
            if (allItems.get(i).getUserName() != null && allItems.get(i).getUri() != null && allItems.get(i).getPhoneNumber() != null && allItems.get(i).getFileName() != null) {
                listDirNew.add(allItems.get(i));
            }
        }
        return listDirNew;
    }
}
