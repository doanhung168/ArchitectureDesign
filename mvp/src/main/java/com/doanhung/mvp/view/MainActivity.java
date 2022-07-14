package com.doanhung.mvp.view;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.AppOpsManagerCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.doanhung.mvp.R;
import com.doanhung.mvp.TrackingForegroundAppService;
import com.doanhung.mvp.databinding.ActivityMainBinding;
import com.doanhung.mvp.model.InfoApp;
import com.doanhung.mvp.presenter.MainContract;
import com.doanhung.mvp.presenter.MainPresenter;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MainContract.MainView {

    private ActivityMainBinding mBinding;
    private MainPresenter mMainPresenter;
    private InfoAppAdapter mInfoAppAdapter;

    // variable for checking permission once time
    // Avoid requesting permission many times
    private boolean mCheckRequestedPermission;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView();

        createOwnerPresenter();

        setUpRcvAppList();
    }

    private void initContentView() {
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
    }

    private void createOwnerPresenter() {
        mMainPresenter = new MainPresenter();
        mMainPresenter.onCreate(this);
    }

    private void setUpRcvAppList() {
        mBinding.rcvInfoApps.setLayoutManager(new LinearLayoutManager(this));
        mBinding.rcvInfoApps.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        );

        mInfoAppAdapter = new InfoAppAdapter(InfoApp.diffCallback);
        mBinding.rcvInfoApps.setAdapter(mInfoAppAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        actionToGetData();
    }


    private void actionToGetData() {
        if (checkUsageStatsPermission()) {
            mBinding.tvRequestMessage.setVisibility(INVISIBLE);
            if (mMainPresenter.enableToLoadData()) {
                mMainPresenter.getInfoAppListAndLongestUsageTimeApp(getApplication());
            }
        } else {
            mBinding.tvRequestMessage.setVisibility(VISIBLE);
            // check to not to show request permission many times. Permission dialog just only showing once time
            if (!mCheckRequestedPermission) {
                requestUsageStatsPermission();
            }
        }
    }

    @Override
    protected void onDestroy() {
        mMainPresenter = null;
        mBinding = null;
        super.onDestroy();
    }


    @Override
    public void submitInfoAppList(List<InfoApp> infoAppList) {
        if (infoAppList != null) {
            mInfoAppAdapter.submitList(infoAppList);
        }
    }

    @Override
    public void submitLongestUsageTimeApp(InfoApp infoApp) {
        if (infoApp != null) {
            mBinding.imvLongestUsageTimeAppIcon.setImageDrawable(infoApp.getAppIcon());
            mBinding.tvLongestUsageTimeAppName.setText(infoApp.getAppName());
        }
    }

    @Override
    public void triggerLoadingInfoAppList(boolean isLoading) {
        if (isLoading) {
            mBinding.frameInfoApps.setVisibility(VISIBLE);
        } else {
            mBinding.frameInfoApps.setVisibility(INVISIBLE);
            mBinding.layoutInfoApps.setVisibility(VISIBLE);
        }
    }

    @Override
    public void triggerLoadingLongestTimeApp(boolean isLoading) {
        if (isLoading) {
            mBinding.frameLongestUsageTimeApp.setVisibility(VISIBLE);
        } else {
            mBinding.frameLongestUsageTimeApp.setVisibility(INVISIBLE);
            mBinding.layoutLongestUsageTimeApp.setVisibility(VISIBLE);
        }
    }


    private void requestUsageStatsPermission() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.request_permission_quote)
                .setCancelable(false)
                .setNegativeButton(getString(R.string.non_accept), (dialog, which) -> {
                    mBinding.frameInfoApps.setVisibility(INVISIBLE);
                    mBinding.frameLongestUsageTimeApp.setVisibility(INVISIBLE);
                    dialog.dismiss();
                })
                .setPositiveButton(getString(R.string.accept), (dialog, which) -> {
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                })
                .show();
        mCheckRequestedPermission = true;
    }

    private boolean checkUsageStatsPermission() {
        int mode = AppOpsManagerCompat.noteOpNoThrow(
                getApplicationContext(),
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                getPackageName()
        );
        return mode == AppOpsManagerCompat.MODE_ALLOWED;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.startService: {
                startService(new Intent(this, TrackingForegroundAppService.class));
                break;
            }
            case R.id.stopService: {
                stopService(new Intent(this, TrackingForegroundAppService.class));
            }
            default:
                break;
        }
        return true;
    }
}