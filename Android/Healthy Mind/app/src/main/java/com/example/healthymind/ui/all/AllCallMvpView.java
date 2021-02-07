package com.example.healthymind.ui.all;

import com.example.healthymind.entity.Recording;
import com.example.healthymind.mvp.BaseMvpView;

import java.util.List;

/**
 * Created by Harry_Hai on 2/16/2018.
 */

public interface AllCallMvpView extends BaseMvpView {
    void onUpdateView(List<Recording> allItems);
}
