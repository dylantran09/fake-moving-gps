package com.dylan.fakemovinggps.location;

import android.Manifest;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.RequiresPermission;

import com.dylan.fakemovinggps.core.util.DLog;

public class MockLocationManager implements LocationListener {
    public static final String TAG = MockLocationManager.class.getSimpleName();

    private final LocationManager locationManager;
    private String locationProvider;
    private MockLocationListener listener;

    public MockLocationManager(Context context, String locationProvider, MockLocationListener listener) {
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.locationProvider = locationProvider;
        this.listener = listener;
    }

    @RequiresPermission(
            anyOf = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}
    )
    @SuppressWarnings("MissingPermission")
    public void requestLocationUpdates() {
        locationManager.requestLocationUpdates(locationProvider, 0, 0, this);
    }

    public void removeUpdates() {
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (listener != null) {
            listener.onMockLocationChanged(location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // do nothing
    }

    @Override
    public void onProviderEnabled(String provider) {
        // do nothing
    }

    @Override
    public void onProviderDisabled(String provider) {
        // do nothing
    }

    public void start() {
        DLog.d(TAG, "start");
        locationManager.addTestProvider(
                locationProvider,
                false,
                false,
                false,
                false,
                true,
                true,
                true,
                Criteria.NO_REQUIREMENT,
                Criteria.ACCURACY_HIGH);
        locationManager.setTestProviderEnabled(locationProvider, true);
    }

    public void setMockLocation() {
        DLog.d(TAG, "setMockLocation");
        Location mockLocation = new Location(locationProvider);
        mockLocation.setLatitude(1.2797677);
        mockLocation.setLongitude(103.8459285);
        mockLocation.setAltitude(0);
        mockLocation.setTime(System.currentTimeMillis());
        mockLocation.setAccuracy(1);
        mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());

        locationManager.setTestProviderLocation(locationProvider, mockLocation);
    }

    public void clearMockLocation() {
        locationManager.clearTestProviderLocation(locationProvider);
    }

    public void shutdown() {
        DLog.d(TAG, "shutdown");
        locationManager.removeTestProvider(locationProvider);
    }
}
