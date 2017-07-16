package com.dylan.fakemovinggps;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;

import com.dylan.fakemovinggps.core.base.BaseActivity;
import com.dylan.fakemovinggps.core.permission.PermissionCallback;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    public void onBaseCreate() {
        if (!permissionHelper.checkPermission()) {
            permissionHelper.requestAllPermission(false, new PermissionCallback() {
                @Override
                public void onPermissionsGranted() {
                    goToHomePage();
                }
            });
        } else {
            goToHomePage();
        }
    }

    @Override
    public void onDeepLinking(Intent data) {

    }

    @Override
    public void onNotification(Intent data) {

    }

    @Override
    public void onBindView() {

    }

    @Override
    public void onInitializeViewData() {

    }

    @Override
    public void onBaseResume() {

    }

    @Override
    public void onBaseFree() {

    }

    @Override
    protected void onInitializeFragments() {

    }

    private void goToHomePage() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }, 300);
    }

    @Override
    public void onSingleClick(View v) {

    }
}
