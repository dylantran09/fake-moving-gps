package com.dylan.fakemovinggps.location;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.RequiresPermission;

import com.dylan.fakemovinggps.core.util.DLog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class FusedLocationClientHelper {
    private static final String TAG = FusedLocationClientHelper.class.getSimpleName();

    private final FusedLocationProviderClient fusedLocationClient;
    private MockLocationListener listener;

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location lastLocation = locationResult.getLastLocation();
            if (listener != null) {
                listener.onMockLocationChanged(lastLocation);
            }
        }
    };

    public FusedLocationClientHelper(Context context, MockLocationListener listener) {
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.listener = listener;
    }

    @SuppressWarnings("MissingPermission")
    public void requestLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_NO_POWER);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setSmallestDisplacement(0);
        Looper looper = Looper.myLooper();
        fusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, looper);
    }

    public void removeUpdates() {
        fusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @RequiresPermission(
            anyOf = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}
    )
    @SuppressWarnings("MissingPermission")
    public void start() {
        DLog.d(TAG, "start");
        fusedLocationClient.setMockMode(true);
    }

    @RequiresPermission(
            anyOf = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}
    )
    @SuppressWarnings("MissingPermission")
    public void setMockLocation(String locationProvider) {
        DLog.d(TAG, "setMockLocation");
        Location mockLocation = new Location(locationProvider);
        mockLocation.setLatitude(1.2797677);
        mockLocation.setLongitude(103.8459285);
        mockLocation.setAltitude(0);
        mockLocation.setTime(System.currentTimeMillis());
        mockLocation.setAccuracy(1);
        mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());

        fusedLocationClient.setMockLocation(mockLocation);
    }

    public void clearMockLocation() {
        // TODO
    }

    @RequiresPermission(
            anyOf = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}
    )
    @SuppressWarnings("MissingPermission")
    public void shutdown() {
        DLog.d(TAG, "shutdown");
        fusedLocationClient.setMockMode(false);
    }
}
