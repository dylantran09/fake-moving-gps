package com.dylan.fakemovinggps;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.dylan.fakemovinggps.core.base.BaseActivity;
import com.dylan.fakemovinggps.core.dialog.ConfirmListener;
import com.dylan.fakemovinggps.core.permission.PermissionUtil;
import com.dylan.fakemovinggps.core.util.DLog;
import com.dylan.fakemovinggps.location.LocationService;
import com.dylan.fakemovinggps.util.Constant;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;

import butterknife.BindView;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, SearchView.OnQueryTextListener, View.OnClickListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.bt_mock_controller)
    FloatingActionButton mBtServiceController;

    ActionBarDrawerToggle mDrawerToggle;
    ActionBar mActionBar;
    SearchView mSearchView;

    SupportMapFragment mMapFragment;
    GoogleMap mMap;

    LocationService mLocationService;
    ServiceConnection mLocationServiceConnection;
    boolean mBound = false;

    BroadcastReceiver mLocationUpdateReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    @Override
    public void onBaseCreate() {
        mLocationServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                LocationService.LocationBinder binder = (LocationService.LocationBinder) service;
                mLocationService = binder.getService();
                mBound = true;
                if (mBtServiceController != null) {
                    if (mLocationService.isMockLocationRunning) {
                        mBtServiceController.setImageResource(R.drawable.ic_pause);
                    } else {
                        mBtServiceController.setImageResource(R.drawable.ic_play);
                    }
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mBound = false;
            }
        };
        mLocationUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null || intent.getAction() == null) {
                    return;
                }
                switch (intent.getAction()) {
                    case Constant.Callback.REQUEST_ALLOW_MOCK_LOCATIONS_APPS:
                        showDecisionDialog(getApplicationContext(),
                                -1,
                                -1,
                                "",
                                "enter device settings first",
                                "OK",
                                "Cancel",
                                null,
                                new ConfirmListener() {
                            @Override
                            public void onConfirmed(int id, Object onWhat) {
                                startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
                            }
                        });
                        break;
                    case Constant.Callback.START_FAKING_LOCATION_SUCCESSFULLY:
                        if (mBtServiceController != null) {
                            mBtServiceController.setImageResource(R.drawable.ic_pause);
                        }
                        break;
                    case Constant.Callback.LOCATION_UPDATE:
                        double lat = intent.getDoubleExtra("lat", 0);
                        double lon = intent.getDoubleExtra("lon", 0);
                        updateMyLocation(new LatLng(lat, lon));
                        break;
                }
            }
        };
        mMapFragment = SupportMapFragment.newInstance();
    }

    @Override
    public void onDeepLinking(Intent data) {

    }

    @Override
    public void onNotification(Intent data) {

    }

    @Override
    public void onBindView() {
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mNavView.setNavigationItemSelectedListener(this);

        mBtServiceController.setOnClickListener(this);
        if (mLocationService != null) {
            if (mLocationService.isMockLocationRunning) {
                mBtServiceController.setImageResource(R.drawable.ic_pause);
            } else {
                mBtServiceController.setImageResource(R.drawable.ic_play);
            }
        }
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
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_container, mMapFragment);
        ft.commitNow();

        mMapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, LocationService.class);
        bindService(intent, mLocationServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.Callback.REQUEST_ALLOW_MOCK_LOCATIONS_APPS);
        filter.addAction(Constant.Callback.START_FAKING_LOCATION_SUCCESSFULLY);
        filter.addAction(Constant.Callback.LOCATION_UPDATE);
        registerReceiver(mLocationUpdateReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mLocationUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        // Unbind from the service
        if (mBound) {
            unbindService(mLocationServiceConnection);
            mBound = false;
        }
        if (mLocationService != null) {
            mLocationService.stopSelf();
        }
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_repositories:
                // TODO
                break;
            case R.id.nav_properties:
                // TODO
                break;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        try {
            // Associate searchable configuration with the SearchView
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            mSearchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            mSearchView.setOnQueryTextListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setAllGesturesEnabled(true);

        enableMyLocation();

        updateMyLocation(new LatLng(1.2797677, 103.8459285));
    }

    private void enableMyLocation() {
        if (PermissionUtil.checkGPSPermission(this)) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }

    private void updateMyLocation(LatLng latLng) {
        if (mMap != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            mMap.animateCamera(cameraUpdate);
            enableMyLocation();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // do your search
        DLog.d("Search", "onQueryTextSubmit(" + query + ")");
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // do your search on change or save the last string or...
        DLog.d("Search", "onQueryTextChange(" + newText + ")");
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_mock_controller:
                if (mLocationService != null) {
                    if (!mLocationService.isMockLocationRunning) {
                        Intent intent = new Intent(this, LocationService.class);
                        intent.setAction(Constant.Action.ACTION_START_FAKING_LOCATION);
                        startService(intent);
                    } else {
                        Intent intent = new Intent(this, LocationService.class);
                        intent.setAction(Constant.Action.ACTION_STOP_FAKING_LOCATION);
                        startService(intent);
                        mBtServiceController.setImageResource(R.drawable.ic_play);
                    }
                }
                break;
        }
    }

    @Override
    public void onSingleClick(View v) {

    }
}
