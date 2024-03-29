package com.example.healthymind.ui.navigation;


import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthymind.R;
import com.example.healthymind.entity.DrawerBean;
import com.example.healthymind.listener.OnRecyclerViewItemClick;
import com.example.healthymind.util.UserPreferences;
import com.example.healthymind.util.ValueConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NavigationDrawerFragment extends Fragment implements OnRecyclerViewItemClick<DrawerBean> {

    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    @BindView(R.id.rvMenu)
    RecyclerView mRecyclerView;
    private DrawerLayout mDrawerLayout;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;

    private List<DrawerBean> mDrawerBeans;
    private LinearLayoutManager mLinearLayoutManager;
    private NavigationDrawerAdapter mNavigationDrawerAdapter;
    private NavigationDrawerCallbacks mCallbacks;
    private ActionBarDrawerToggle mDrawerToggle;
    private Unbinder unbinder;

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentSelectedPosition = UserPreferences.getInt(STATE_SELECTED_POSITION, -1);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initData();
        return rootView;
    }

    private void initData() {
        mDrawerBeans = new ArrayList<>();
        mDrawerBeans.add(new DrawerBean(R.drawable.ic_header, getString(R.string.menu_navigation), ValueConstants.NavigationItemType.Header));
        mDrawerBeans.add(new DrawerBean(R.drawable.ic_trashre, getString(R.string.menu_recycle), ValueConstants.NavigationItemType.Item));
        mDrawerBeans.add(new DrawerBean(R.drawable.ic_setting, getString(R.string.menu_setting), ValueConstants.NavigationItemType.Item));
        mDrawerBeans.add(new DrawerBean(R.drawable.ic_about, getString(R.string.menu_about), ValueConstants.NavigationItemType.Item));
        mDrawerBeans.add(new DrawerBean(R.drawable.ic_exit, getString(R.string.menu_exit), ValueConstants.NavigationItemType.Item));

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mNavigationDrawerAdapter = new NavigationDrawerAdapter(getActivity(), mCurrentSelectedPosition);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mNavigationDrawerAdapter.loadInitialDataSet(mDrawerBeans);
        mRecyclerView.setAdapter(mNavigationDrawerAdapter);
        mNavigationDrawerAdapter.setOnRecyclerViewItemClick(this);
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void setUp(int fragmentId, Toolbar toolbar, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),
                mDrawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }
                getActivity().invalidateOptionsMenu();
            }
        };
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view, DrawerBean item, int position) {
        selectItem(item, position);
    }

    private void selectItem(DrawerBean drawerBean, int position) {
        mCurrentSelectedPosition = position;
        if (mNavigationDrawerAdapter != null) {
            mNavigationDrawerAdapter.setCurrentSelectedPosition(mCurrentSelectedPosition);
            mNavigationDrawerAdapter.notifyDataSetChanged();
        }
        UserPreferences.setInt(STATE_SELECTED_POSITION, position);
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(drawerBean.getType(), position);
        }
    }

    public interface NavigationDrawerCallbacks {
        void onNavigationDrawerItemSelected(int type, int position);

        void onCloseNavigation();
    }
}
