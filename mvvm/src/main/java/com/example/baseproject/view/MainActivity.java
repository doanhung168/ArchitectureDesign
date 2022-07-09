package com.example.baseproject.view;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.baseproject.R;
import com.example.baseproject.TrackingForegroundAppService;
import com.example.baseproject.databinding.ActivityMainBinding;
import com.example.baseproject.model.InfoApp;
import com.example.baseproject.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private MainViewModel mainViewModel;
    private ActivityMainBinding mBinding;
    private AlertDialog mLoadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mainViewModel = new ViewModelProvider(
                this,
                new MainViewModel.MainViewModelFactory(getApplication())).get(MainViewModel.class);
        mBinding.setViewModel(mainViewModel);
        mBinding.setLifecycleOwner(this);

        setUpRcvAppList();
        setUpBtnEnableUsageStatsPermission();
        setUpNotifyWhenLoadingData();
    }

    private void setUpRcvAppList() {
        InfoAppAdapter infoAppAdapter = new InfoAppAdapter(InfoApp.diffCallback);

        mBinding.rcvInfoApps.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mBinding.rcvInfoApps.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        );
        mBinding.rcvInfoApps.setAdapter(infoAppAdapter);

        mainViewModel.getInfoAppList().observe(this, infoAppAdapter::submitList);
    }

    private void setUpBtnEnableUsageStatsPermission() {
        mBinding.btnEnablePermission.setOnClickListener(v ->
                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        );
    }

    private void setUpNotifyWhenLoadingData() {

        mLoadingDialog = new AlertDialog.Builder(this)
                .setView(R.layout.progress_dialog)
                .create();
        mLoadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        mainViewModel.isLoadingInfoAppList().observe(this, isLoading -> {
            if (isLoading) {
                mLoadingDialog.show();
            } else {
                mLoadingDialog.dismiss();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        actionToGetData();
    }

    private void actionToGetData() {
        if (checkUsageStatsPermission()) {
            showHideWithPermission();
            // check to not to load data again
            if (mainViewModel.getInfoAppList().getValue() == null) {
                mainViewModel.requestInfoAppList();
            }
        } else {
            showHideNoPermission();
        }
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

    private void showHideNoPermission() {
        mBinding.tvRequestPermissionMessage.setVisibility(VISIBLE);
        mBinding.btnEnablePermission.setVisibility(VISIBLE);
    }

    private void showHideWithPermission() {
        mBinding.tvRequestPermissionMessage.setVisibility(INVISIBLE);
        mBinding.btnEnablePermission.setVisibility(INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        mBinding = null;
        super.onDestroy();
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