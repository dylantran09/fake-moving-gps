package com.dylan.fakemovinggps.location;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.dylan.fakemovinggps.R;
import com.dylan.fakemovinggps.core.permission.PermissionUtil;
import com.dylan.fakemovinggps.core.util.DLog;
import com.dylan.fakemovinggps.util.Constant;

public class LocationService extends Service implements MockLocationListener {
    private static final String TAG = LocationService.class.getSimpleName();

    private LocationBinder binder = new LocationBinder();

    private MockLocationManager mMockLocationManager;

    public boolean isMockLocationRunning = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        DLog.d(TAG, "onCreate");
        super.onCreate();

        mMockLocationManager = new MockLocationManager(getApplicationContext(), LocationManager.GPS_PROVIDER, this);

        registerForLocationUpdates();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case Constant.Action.ACTION_START_FAKING_LOCATION:
                    processStartFakingLocation(intent);
                    break;
                case Constant.Action.ACTION_STOP_FAKING_LOCATION:
                    processStopFakingLocation(intent);
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    private void processStartFakingLocation(Intent intent) {
        if (startMockLocation()) {
            showNotification();
            sendBroadcast(Constant.Callback.START_FAKING_LOCATION_SUCCESSFULLY);
        }
    }

    private void processStopFakingLocation(Intent intent) {
        stopMockLocation();
    }

    @Override
    public void onDestroy() {
        DLog.d(TAG, "onDestroy");
        stopMockLocation();
        unregisterForLocationUpdates();
        super.onDestroy();
    }

    private void registerForLocationUpdates() {
        if (mMockLocationManager != null
                && PermissionUtil.checkGPSPermission(getApplicationContext())) {
            mMockLocationManager.requestLocationUpdates();
        }
    }

    private void unregisterForLocationUpdates() {
        if (mMockLocationManager != null) {
            mMockLocationManager.removeUpdates();
        }
    }

    public boolean startMockLocation() {
        try {
            if (mMockLocationManager != null) {
                mMockLocationManager.start();
                mMockLocationManager.setMockLocation();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            sendBroadcast(Constant.Callback.REQUEST_ALLOW_MOCK_LOCATIONS_APPS);
            return false;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
        isMockLocationRunning = true;
        return true;
    }

    private void showNotification() {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setLargeIcon(icon)
                .setContentTitle("Fake Location")
                .setContentText("Fake location is enabled");

        startForeground(Constant.ID_SERVICE_NOTIFICATION, builder.build());
    }

    public boolean stopMockLocation() {
        if (isMockLocationRunning) {
            try {
                if (mMockLocationManager != null) {
                    mMockLocationManager.shutdown();
                }
            } catch (SecurityException e) {
                e.printStackTrace();
                sendBroadcast(Constant.Callback.REQUEST_ALLOW_MOCK_LOCATIONS_APPS);
                return false;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return false;
            }
            isMockLocationRunning = false;
            stopForeground(true);
            return true;
        }
        return false;
    }

    @Override
    public void onMockLocationChanged(Location location) {
        DLog.d(TAG, "onLocationChanged");
        if (location != null) {
            DLog.d(TAG, "onLocationChanged " + location.getLatitude() + ", " + location.getLongitude());
            //sendBroadcastUpdatedLocation(location);
        }
    }

    private void sendBroadcastUpdatedLocation(Location location) {
        Intent intent = new Intent();
        intent.setAction(Constant.Callback.LOCATION_UPDATE);
        intent.putExtra("lat", location.getLatitude());
        intent.putExtra("lon", location.getLongitude());
        sendBroadcast(intent);
    }

    private void sendBroadcast(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        sendBroadcast(intent);
    }

    public class LocationBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }
}
