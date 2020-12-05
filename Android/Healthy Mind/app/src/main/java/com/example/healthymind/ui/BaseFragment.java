package com.example.healthymind.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.example.healthymind.R;
import com.example.healthymind.mvp.BasePresenter;
import com.example.healthymind.util.StringUtils;

public abstract class BaseFragment<T extends BasePresenter> extends Fragment {

    protected View rootView;

    protected T mPresenter;

    protected ViewGroup fragmentViewParent;
    View coverNetworkLoading;
    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_base, container, false);
        initCommonViews(rootView);
        fragmentViewParent.addView(inflater.inflate(getLayoutId(), container, false));
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    private void initCommonViews(View rootView) {
        fragmentViewParent = (ViewGroup) rootView.findViewById(R.id.fragmentViewParent);
        coverNetworkLoading = rootView.findViewById(R.id.coverNetworkLoading);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            getArgument(getArguments());
        }
        initView(rootView);
        initPresenter();
        initData();
    }

    protected abstract void initData();

    abstract protected int getLayoutId();

    abstract protected void initView(View root);

    abstract protected void getArgument(Bundle bundle);

    public abstract void initPresenter();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        unbinder.unbind();
        fragmentViewParent = null;
        rootView = null;
        super.onDestroy();
    }

    public void replaceFragment(int container, Fragment fragment) {
        replaceFragment(container, fragment, null);
    }

    public void replaceFragment(int container, Fragment fragment, String tag) {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        if (tag != null) {
            Fragment f = manager.findFragmentByTag(tag);
            if (f != null) {
                manager.popBackStack();
            }
        }
        FragmentTransaction transaction = manager.beginTransaction();
//        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(container, fragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    /**
     * This method replace fragment and clear all back stack
     *
     * @param container fragment container
     * @param fragment  new fragment
     */
    public void replaceAndClearFragment(int container, Fragment fragment) {
        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); // clear action
        replaceFragment(container, fragment);
    }

    public void replaceFragmentDontBack(int container, Fragment fragment) {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.popBackStack();
        FragmentTransaction transaction = manager.beginTransaction();
//        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public int getTitleString(boolean b) {
        return 0;
    }

    protected void setUpToolBarView(String title, TextView tvTitle, ImageView ivBack) {
        if (tvTitle != null) {
            StringUtils.setText(title, tvTitle);
        }
        if (ivBack != null) {
            ivBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleBack();
                }
            });
        }
    }

    protected void handleBack() {
        int backStackCnt = getFragmentManager().getBackStackEntryCount();
        if (backStackCnt > 1) {
            getFragmentManager().popBackStack();
        } else {
            getActivity().finish();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected void startWebView(WebView webView, String url) {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getContext(), getString(R.string.error) + description, Toast.LENGTH_SHORT).show();
            }
        });
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl(url);
    }
}
