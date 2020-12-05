package com.example.healthymind.mvp;

public interface BaseMvpView {
    void onResponseError(int apiMethod, int statusCode);
}
