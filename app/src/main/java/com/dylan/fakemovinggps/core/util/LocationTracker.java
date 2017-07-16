package com.dylan.fakemovinggps.core.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

/**
 * @author Tyrael
 * @version 1.0
 *          <p/>
 *          <br>
 *          <b>Class Overview</b> <br>
 *          <br>
 *          Represents a class for current location detection by GPS or Network
 *          or both. Used as a separated thread. You will want to retrieve the
 *          location result, failure or progress through
 *          <code>LocationUpdateListener</code> interface.<br>
 *          <br>
 *          <b>Permissions</b> <br>
 *          <br>
 *          To use this class, you must declare the permissions below in
 *          <code>AndroidManifest.xml</code> under the
 *          <code>use-permission</code> tag:<br>
 *          <br>
 *          <code>ACCESS_COARSE_LOCATION</code> <br>
 *          <code>ACCESS_FINE_LOCATION</code><br>
 * @since August, 2013
 */

@SuppressWarnings("unused")
public class LocationTracker {

    /**
     * The context of the using class
     */
    private final Context context;

    /**
     * The current location detection thread, this reference is used for
     * starting and stopping purpose while needed
     */
    private Thread current_update_thread;

    /**
     * The runnable core of location detection, this reference is used for
     * starting and stopping purpose while needed
     */
    private LocationUpdate location_update;

    /**
     * The generated tag is being given for each and every location detection
     * runnable to indicate the last and the only runnable can return the value
     * or failure
     */
    private int processing_tag;

    /**
     * The time-out in second of the process, 0 means no time-out, after
     * time-out, the process will result a time-out failure
     */
    private int timeOut;

    /**
     * The sleeping time in second between each detection, minimum is 1 second
     */
    private int timeStep;

    /**
     * The flag that allows to use GPS as a source to detect location
     */
    private boolean GPSAllowed;

    /**
     * The flag that allows to use Network as a source to detect location
     */
    private boolean NetworkAllowed;

    /**
     * The flag that allows to retrieve location from last time detection
     */
    private boolean LastKnowLocationAllowed;

    /**
     * The flag that allows to retrieve location continuously, false means only
     * detect successfully once
     */
    private boolean TrackingModeAllowed;

    /**
     * The pre-defined method as a preferred method for location detection,
     * either GPS or NETWORK
     */
    private LocationUpdateMethod prioritizedMethod;

    /**
     * The listener to call for the status of the location detection
     */
    private LocationUpdateListener listener;

    /**
     * The min tracking distance (meter) if there are changes in location
     */
    private int minDistance;

    /**
     * The last tracked location, this is for calculating the changes in distance
     */
    private Location previousTrackedLocation = null;

    public LocationTracker(Context context, int timeOut, int timeStep, int minDistance,
                           boolean GPSAllowed, boolean NetworkAllowed,
                           boolean LastKnowLocationAllowed, boolean TrackingModeAllowed,
                           LocationUpdateMethod prioritizedMethod,
                           LocationUpdateListener listener) {
        this.context = context;
        this.timeOut = (timeOut <= 0) ? 0 : timeOut;
        this.timeStep = (timeStep > 0) ? timeStep : 1;
        this.minDistance = (minDistance > 0) ? minDistance : 1;
        this.GPSAllowed = GPSAllowed;
        this.NetworkAllowed = NetworkAllowed;
        this.LastKnowLocationAllowed = LastKnowLocationAllowed;
        this.TrackingModeAllowed = TrackingModeAllowed;
        this.prioritizedMethod = (prioritizedMethod == LocationUpdateMethod.PASSIVE) ? LocationUpdateMethod.GPS
                : prioritizedMethod;
        this.listener = listener;
    }

    /**
     * @return the second each time the result returns
     */
    public int getTimeStep() {
        return timeStep;
    }

    /**
     * @param timeStep the timeStep to set, minimum value is 1, any negative values
     *                 will be set as the minimum
     */
    public void setTimeStep(int timeStep) {
        this.timeStep = (timeStep > 0) ? timeStep : 1;
    }

    /**
     * @return the time-out, location detection will stop if the time-out
     * reached
     */
    public int getTimeOut() {
        return timeOut;
    }

    /**
     * @param timeOut the timeOut to set, minimum is 0 and means no time-out, any
     *                negative value will be set as 0
     */
    public void setTimeOut(int timeOut) {
        this.timeOut = (timeOut <= 0) ? 0 : timeOut;
    }

    /**
     * @return the gps-allowed status, indicating whether the detection can
     * perform by GPS, true means allow, otherwise false
     */
    public boolean isGPSAllowed() {
        return GPSAllowed;
    }

    /**
     * @param gPSAllowed the gPSAllowed to set, indicating whether the detection can
     *                   perform by GPS, true means allow, otherwise false
     */
    public void setGPSAllowed(boolean gPSAllowed) {
        GPSAllowed = gPSAllowed;
    }

    /**
     * @return the networkAllowed, indicating whether the detection can perform
     * by Network, true means allow, otherwise false
     */
    public boolean isNetworkAllowed() {
        return NetworkAllowed;
    }

    /**
     * @param networkAllowed the networkAllowed to set, indicating whether the detection
     *                       can perform by Network, true means allow, otherwise false
     */
    public void setNetworkAllowed(boolean networkAllowed) {
        NetworkAllowed = networkAllowed;
    }

    /**
     * @return the lastKnowLocationAllowed, indicating whether the detection can
     * retrieve the last location has been detected as the result, true
     * means allow, otherwise false
     */
    public boolean isLastKnowLocationAllowed() {
        return LastKnowLocationAllowed;
    }

    /**
     * @param lastKnowLocationAllowed the lastKnowLocationAllowed to set, indicating whether the
     *                                detection can retrieve the last location has been detected as
     *                                the result, true means allow, otherwise false
     */
    public void setLastKnowLocationAllowed(boolean lastKnowLocationAllowed) {
        LastKnowLocationAllowed = lastKnowLocationAllowed;
    }

    /**
     * @return the trackingModeAllowed, indicating whether the detection can
     * retrieve the location continuously, true means allow, otherwise
     * false
     */
    public boolean isTrackingModeAllowed() {
        return TrackingModeAllowed;
    }

    /**
     * @param trackingModeAllowed the trackingModeAllowed to set, indicating whether the
     *                            detection can retrieve the location continuously, true means
     *                            allow, otherwise false
     */
    public void setTrackingModeAllowed(boolean trackingModeAllowed) {
        TrackingModeAllowed = trackingModeAllowed;
    }

    /**
     * @return the prioritizedMethod
     */
    public LocationUpdateMethod getPrioritizedMethod() {
        return prioritizedMethod;
    }

    /**
     * @param prioritizedMethod the prioritizedMethod to set, only GPS and Network method will
     *                          be allowed, if PASSIVE is set, it will be replaced by GPS
     *                          instead
     */
    public void setPrioritizedMethod(LocationUpdateMethod prioritizedMethod) {
        this.prioritizedMethod = (prioritizedMethod == LocationUpdateMethod.PASSIVE) ? LocationUpdateMethod.GPS
                : prioritizedMethod;
    }

    /**
     * @return the minDistance
     */
    public int getMinDistance() {
        return minDistance;
    }

    /**
     * @param minDistance the minDistance to set between last tracked location and current best
     *                    tracked location, the value is to indicate whether a location should be
     *                    returned <code>onLocationSuccess</code> method
     */
    public void setMinDistance(int minDistance) {
        this.minDistance = minDistance;
    }

    /**
     * @return the listener to handle location detection success, fail or
     * process
     */
    public LocationUpdateListener getListener() {
        return listener;
    }

    /**
     * @param listener the listener to set to handle location detection success, fail
     *                 or process
     */
    public void setListener(LocationUpdateListener listener) {
        this.listener = listener;
    }

    /**
     * Starts the location detection with the defined values. This method will
     * start a new thread to detect the current location of the device. Every
     * time it is called, it will check for any previous thread and cancel. Only
     * one thread is running at a time. If you want to change the value for
     * detecting process, you must call <code>stopUpdatingLocation</code>
     * method, change the values and call this method again
     */
    public void startUpdatingLocation() {
        processing_tag += 1;
        if (location_update != null)
            location_update.stopUpdate();
        if (current_update_thread != null)
            current_update_thread.interrupt();
        location_update = new LocationUpdate(context, timeOut, timeStep,
                GPSAllowed, NetworkAllowed, LastKnowLocationAllowed,
                TrackingModeAllowed, prioritizedMethod, listener,
                processing_tag);
        (current_update_thread = new Thread(location_update)).start();
    }

    /**
     * Stops the current location detecting thread if there is any running.
     */
    public void stopUpdatingLocation() {
        if (location_update != null)
            location_update.stopUpdate();
        if (current_update_thread != null)
            current_update_thread.interrupt();
    }

    /**
     * @return the isTracking, indicating the status of tracking process, true
     * means 'in process', otherwise false
     */
    public boolean isTracking() {
        return location_update != null && location_update.isTracking();
    }

    /**
     * public enum <br>
     * <b>LocationUpdateMethod</b> <br>
     * Represents the method of detecting location for
     * <code>LocationTracker</code> with 3 values:<br>
     * <i>GPS:</i> Indicate the preferred method of detecting location, if this
     * is set, the result from GPS will be chosen when there are results from
     * both GPS and Network<br>
     * <i>NETWORK:</i> Indicate the preferred method of detecting location, if
     * this is set, the result from Network will be chosen when there are
     * results from both GPS and Network<br>
     * <i>PASSIVE:</i> Indicate the result method when there are no results from
     * both GPS and Network<br>
     * <b>You should only use GPS and NETWORK to set the preferred method, the
     * PASSIVE value is only used as a return value. If this method is set as
     * the preferred method, it will be replaced by GPS method instead</b>
     */
    public enum LocationUpdateMethod {
        GPS, NETWORK, PASSIVE
    }

    /**
     * public enum <br>
     * <b>LocationUpdateError</b> <br>
     * Represents the error of detecting location fail for
     * <code>LocationTracker</code> with 5 values:<br>
     * <i>NO_GPS:</i> Only GPS method is allowed but GPS status is not available<br>
     * <i>NO_NETWORK:</i> Only Network method is allowed but Network status is
     * not available<br>
     * <i>NO_GPS_NO_NETWORK:</i> Both GPS and Network status are not available<br>
     * <i>GPS_NETWORK_NOT_ALLOWED:</i> Both detection methods are disabled by
     * user<br>
     * <i>TIMEOUT:</i> Timeout is reached of detecting location but still not
     * having a result<br>
     */
    public enum LocationUpdateError {
        NO_GPS {
            @Override
            public String toString() {
                return "Only GPS method is allowed but GPS status is not available";
            }
        },
        NO_NETWORK {
            @Override
            public String toString() {
                return "Only Network method is allowed but Network status is not available";
            }
        },
        NO_GPS_NO_NETWORK {
            @Override
            public String toString() {
                return "Both GPS and Network status are not available";
            }
        },
        GPS_NETWORK_NOT_ALLOWED {
            @Override
            public String toString() {
                return "Both detection methods are disabled by user";
            }
        },
        TIMEOUT {
            @Override
            public String toString() {
                return "Timeout is reached of detecting location but still not having a result";
            }
        },
    }

    /**
     * public interface<br>
     * <b>LocationUpdateListener</b><br>
     * <br>
     * <b>Class Overview</b> <br>
     * <br>
     * Used for receiving notifications from the <code>LocationTracker</code>
     * when location value has been detected or failure in location detection.<br>
     * <br>
     * <b>Summary</b>
     */
    public interface LocationUpdateListener {

        /**
         * <b>Specified by:</b> onLocationSuccess(...) in LocationUpdateListener <br>
         * <br>
         * This is called immediately after a location is being successfully
         * retrieved. After returning from this call, you can use the location
         * and the type value. If you want to update the UI, please use
         * <code>Handler</code> or <code>runOnUI</code> method, otherwise it
         * will throw <code>CalledFromWrongThreadException</code> exception
         * because this method is called from a separated thread.
         *
         * @param location The location has been retrieved from either GPS or
         *                 NETWORK.
         * @param type     The pre-defined scanning method, can be specified by user
         *                 with the values of GPS, NETWORK or PASSIVE.
         */
        void onLocationSuccess(Location location,
                               LocationUpdateMethod type);

        /**
         * <b>Specified by:</b> onLocationFail(...) in LocationUpdateListener <br>
         * <br>
         * This is called immediately after a location is being fail to
         * retrieve. After returning from this call, the location detection will
         * stop. If you want to update the UI, please use <code>Handler</code>
         * or <code>runOnUI</code> method, otherwise it will throw
         * <code>CalledFromWrongThreadException</code> exception because this
         * method is called from a separated thread.
         *
         * @param error The string to indicate the error of location detection.
         */
        void onLocationFail(LocationUpdateError error);

        /**
         * <b>Specified by:</b> onLocationCountDown(...) in
         * LocationUpdateListener <br>
         * <br>
         * This is called while a location is being detecting. The method
         * indicate the time has left in the progress. If you want to update the
         * UI, please use <code>Handler</code> or <code>runOnUI</code> method,
         * otherwise it will throw <code>CalledFromWrongThreadException</code>
         * exception because this method is called from a separated thread.
         *
         * @param time_out  The total seconds of the detection process. The value of 0
         *                  means no time-out.
         * @param countdown The seconds has left in detecting location.S
         */
        void onLocationCountDown(int time_out, int countdown);

        /**
         * <b>Specified by:</b> onLocationPermissionRequest(...) in
         * LocationUpdateListener <br>
         * <br>
         * This is called when location service permission is not ENABLED (or DISABLED by device owner)
         * to let developer explicitly requests and handle the permission. Please
         * use <code>Handler</code> or <code>runOnUI</code> method,
         * otherwise it will throw <code>CalledFromWrongThreadException</code>
         * exception because this method is called from a separated thread. Note: stop the location
         * update and restart when user accepted the location permission
         */
        void onLocationPermissionRequest();
    }

    /**
     * @author Tyrael
     * @version 1.0
     *          <p/>
     *          <br>
     *          <b>Class Overview</b> <br>
     *          <br>
     *          Represents a class for core runnable of the process. Used as a
     *          separated thread
     * @since August, 2013
     */
    private class LocationUpdate implements Runnable {

        /**
         * The minimum distance of listening for location
         */
        private final int MINIMUM_DISTANCE = 1;
        /**
         * The minimum time interval of listening for location
         */
        private final int MINIMUM_TIME = 1;
        /**
         * The context of the using class
         */
        private final Context context;
        /**
         * The listener to call for the status of the location detection
         */
        private final LocationUpdateListener listener;
        /**
         * The tag is being given for this runnable to indicate this runnable
         * can return the value or failure
         */
        private final int tag;
        /**
         * The time-out in second of the process, 0 means no time-out, after
         * time-out, the process will result a time-out failure
         */
        private int TIMEOUT_SEC = 15;
        /**
         * The sleeping time in second between each detection, minimum is 1
         * second
         */
        private int time_step = 1;
        /**
         * The flag that indicate GPS detection is completed or canceled
         */
        private boolean flagGetGPSDone = false;
        /**
         * The flag that indicate Network detection is completed or canceled
         */
        private boolean flagNetworkDone = false;
        /**
         * The flag that indicate GPS accessibility is enabled
         */
        private boolean flagGPSEnable = true;
        /**
         * The flag that indicate Network accessibility is enabled
         */
        private boolean flagNetworkEnable = true;
        /**
         * The flag that allows to use GPS as a source to detect location
         */
        private boolean flagGPSAllowed = true;
        /**
         * The flag that allows to use Network as a source to detect location
         */
        private boolean flagNetworkAllowed = true;
        /**
         * The flag that allows to retrieve location from last time detection
         */
        private boolean flagLastKnowLocationAllowed = false;
        /**
         * The flag that allows to retrieve location continuously, false means
         * only detect successfully once
         */
        private boolean flagTrackingModeAllowed = false;
        /**
         * The location manager to check for GPS and Network accessibility
         */
        private LocationManager locationManager;
        /**
         * The result location by GPS detection
         */
        private Location currentLocationGPS = null;
        /**
         * The result location by Network detection
         */
        private Location currentLocationNetwork = null;
        /**
         * The Network listener to detect location changes base on Network
         */
        private final LocationListener NetworkListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                OnNetworkChange(location);
            }

            public void onProviderDisabled(String provider) {
                flagNetworkEnable = false;
            }

            public void onProviderEnabled(String provider) {
                flagNetworkEnable = true;
            }

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
            }
        };
        /**
         * The GPS listener to detect the location changes base on GPS
         */
        private final LocationListener GPSListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                OnGPSChange(location);
            }

            public void onProviderDisabled(String provider) {
                flagGPSEnable = false;
            }

            public void onProviderEnabled(String provider) {
                flagGPSEnable = true;
            }

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
            }
        };
        /**
         * The result location after filtered by pre-defined conditions
         */
        private Location bestLocation = null;
        /**
         * The pre - defined method as a preferred method for location detection
         * , either GPS or NETWORK
         */
        private LocationUpdateMethod prioritizedMethod = LocationUpdateMethod.GPS;
        /**
         * The result method of location detection
         */
        private LocationUpdateMethod resultMethod = LocationUpdateMethod.GPS;
        /**
         * The counter of the process
         */
        private int counts = 0;
        /**
         * The flag that indicate whether this runnable should stop
         */
        private boolean isStopping = false;
        /**
         * The flag that indicate the status of location tracking
         */
        private boolean isTracking = false;

        public LocationUpdate(Context context, int timeOut, int timeStep,
                              boolean GPSAllowed, boolean NetworkAllowed,
                              boolean LastKnowLocationAllowed, boolean TrackingModeAllowed,
                              LocationUpdateMethod prioritizedMethod,
                              LocationUpdateListener listener, int tag) {
            this.context = context;
            this.listener = listener;
            this.tag = tag;
            this.TIMEOUT_SEC = timeOut;
            this.time_step = timeStep;
            this.flagGPSAllowed = GPSAllowed;
            this.flagNetworkAllowed = NetworkAllowed;
            this.flagLastKnowLocationAllowed = LastKnowLocationAllowed;
            this.flagTrackingModeAllowed = TrackingModeAllowed;
            this.prioritizedMethod = prioritizedMethod;
            initGPS();
        }

        /**
         * @return the isTracking, indicating the status of tracking process,
         * true means 'in process', otherwise false
         */
        public boolean isTracking() {
            return isTracking;
        }

        /**
         * Stops this runnable of location detection
         */
        public void stopUpdate() {
            isStopping = true;
        }

        /**
         * Initializes values of this runnable before detecting location, this
         * method check the accessibility of GPS and Network status, combining
         * with other conditions for the final pre-defined values. After that it
         * starts the detection
         */
        private void initGPS() {
            locationManager = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);

            flagGPSEnable = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            flagNetworkEnable = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            flagGetGPSDone = (!flagGPSEnable);
            flagNetworkDone = (!flagNetworkEnable);

            if (!flagGPSAllowed)
                flagGetGPSDone = true;

            if (!flagNetworkAllowed)
                flagNetworkDone = true;

            startAllUpdate();
            isTracking = false;
            isStopping = false;
            bestLocation = null;
            counts = 0;
        }

        /**
         * Starts listening from the GPS or Network for location
         */
        private void startAllUpdate() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    if (listener != null)
                        listener.onLocationPermissionRequest();
                return;
            }
            if (flagGPSAllowed)
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, MINIMUM_TIME,
                        MINIMUM_DISTANCE, GPSListener);
            if (flagNetworkAllowed)
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, MINIMUM_TIME,
                        MINIMUM_DISTANCE, NetworkListener);
        }

        /**
         * Stops listening from the GPS and Network for location
         */
        private void stopAllUpdate() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    if (listener != null)
                        listener.onLocationPermissionRequest();
                return;
            }
            locationManager.removeUpdates(GPSListener);
            locationManager.removeUpdates(NetworkListener);
        }

        /**
         * Update the current location change to GPS location result. Continue
         * to update location depending on the tracking mode
         *
         * @param location The changed location to update
         */
        private void OnGPSChange(Location location) {
            currentLocationGPS = location;
            flagGetGPSDone = true;
            flagNetworkDone = true;
            if (!flagTrackingModeAllowed)
                stopAllUpdate();
        }

        /**
         * Update the current location change to Network location result.
         * Continue to update location depending on the tracking mode
         *
         * @param location The changed location to update
         */
        private void OnNetworkChange(Location location) {
            currentLocationNetwork = location;
            flagNetworkDone = true;
            flagGetGPSDone = true;
            if (!flagTrackingModeAllowed)
                stopAllUpdate();
        }

        /**
         * Apply the pre-defined condition, compare between GPS and Network
         * locations for a result location
         *
         * @return The best current location depending the pre-defined
         * conditions
         */
        private Location getCurrentLocation() {
            Location retLocation = null;
            if ((flagGetGPSDone && flagNetworkDone)) {
                if (flagLastKnowLocationAllowed) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            currentLocationGPS = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            currentLocationNetwork = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        } else {
                            if (listener != null)
                                listener.onLocationPermissionRequest();
                            return null;
                        }
                    } else {
                        currentLocationGPS = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        currentLocationNetwork = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
                if (currentLocationGPS == null && currentLocationNetwork == null) {
                    retLocation = locationManager
                            .getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    if (retLocation == null) {
                        retLocation = new Location(
                                LocationManager.PASSIVE_PROVIDER);
                    }
                    resultMethod = LocationUpdateMethod.PASSIVE;
                } else {
                    if (prioritizedMethod == LocationUpdateMethod.GPS) {
                        if (currentLocationGPS != null) {

                            retLocation = currentLocationGPS;
                            resultMethod = LocationUpdateMethod.GPS;
                        } else {
                            retLocation = currentLocationNetwork;
                            resultMethod = LocationUpdateMethod.NETWORK;
                        }
                    } else if (prioritizedMethod == LocationUpdateMethod.NETWORK) {
                        if (currentLocationNetwork != null) {
                            retLocation = currentLocationNetwork;
                            resultMethod = LocationUpdateMethod.NETWORK;
                        } else {
                            retLocation = currentLocationGPS;
                            resultMethod = LocationUpdateMethod.GPS;
                        }
                    } else {
                        retLocation = locationManager
                                .getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                        if (retLocation == null) {
                            retLocation = new Location(
                                    LocationManager.PASSIVE_PROVIDER);
                        }
                        resultMethod = LocationUpdateMethod.PASSIVE;
                    }
                }
            }
            return retLocation;
        }

        @Override
        public void run() {
            while (!isStopping) {
                /*
                 * Check whether this runnable is stop, if it not, continue to
				 * detect location
				 */
                isTracking = true;
                if (!flagGPSEnable && !flagNetworkEnable) {
                    flagGetGPSDone = true;
                    flagNetworkDone = true;
                    if (tag == processing_tag)
                        listener.onLocationFail(LocationUpdateError.NO_GPS_NO_NETWORK);
                    stopAllUpdate();
                    isTracking = false;
                    return;
                }

                if (!flagGPSAllowed && !flagNetworkAllowed) {
                    flagGetGPSDone = true;
                    flagNetworkDone = true;
                    if (tag == processing_tag)
                        listener.onLocationFail(LocationUpdateError.GPS_NETWORK_NOT_ALLOWED);
                    stopAllUpdate();
                    isTracking = false;
                    return;
                }

                if (flagGPSAllowed && !flagNetworkAllowed && !flagGPSEnable) {
                    flagGetGPSDone = true;
                    flagNetworkDone = true;
                    if (tag == processing_tag)
                        listener.onLocationFail(LocationUpdateError.NO_GPS);
                    stopAllUpdate();
                    isTracking = false;
                    return;
                }

                if (!flagGPSAllowed && flagNetworkAllowed && !flagNetworkEnable) {
                    flagGetGPSDone = true;
                    flagNetworkDone = true;
                    if (tag == processing_tag)
                        listener.onLocationFail(LocationUpdateError.NO_NETWORK);
                    stopAllUpdate();
                    isTracking = false;
                    return;
                }

                listener.onLocationCountDown(TIMEOUT_SEC,
                        (TIMEOUT_SEC != 0) ? TIMEOUT_SEC - counts : 0);
                /*
                 * Call for every step of location detection for UI update
				 */
                counts++;
                if (counts > TIMEOUT_SEC && TIMEOUT_SEC != 0) {
                    /*
                     * If the time-out is reached then stop and return a failure
					 * with time-out error
					 */
                    flagGetGPSDone = true;
                    flagNetworkDone = true;
                    if (tag == processing_tag)
                        listener.onLocationFail(LocationUpdateError.TIMEOUT);
                    stopAllUpdate();
                    isTracking = false;
                    return;
                }

                bestLocation = getCurrentLocation();
                /* Calculate for a best location */

                if (bestLocation != null) {
                    /*
                     * Return a result location if any along with the method
					 */
                    if (tag == processing_tag) {
                        if (previousTrackedLocation != null) {
                            if (Utils.calculateDistance(previousTrackedLocation.getLongitude(),
                                    previousTrackedLocation.getLatitude(),
                                    bestLocation.getLongitude(),
                                    bestLocation.getLatitude()) >= minDistance) {
                                listener.onLocationSuccess(bestLocation, resultMethod);
                            }
                        } else {
                            listener.onLocationSuccess(bestLocation, resultMethod);
                        }
                        previousTrackedLocation = bestLocation;
                    }
                    if (!flagTrackingModeAllowed) {
                        /*
                         * Continue to listen for a new location or not
						 */
                        stopAllUpdate();
                        isTracking = false;
                        return;
                    }
                }
                try {
                    Thread.sleep(1000 * time_step);
                    /*
                     * Sleeps for the pre-defined interval second, if this
					 * thread is interrupted then finish this thread
					 */
                } catch (InterruptedException e) {
                    break;
                }
            }
            stopAllUpdate();
            isTracking = false;
        }
    }
}
