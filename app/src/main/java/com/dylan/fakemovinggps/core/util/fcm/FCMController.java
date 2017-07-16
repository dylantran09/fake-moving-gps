package com.dylan.fakemovinggps.core.util.fcm;

import android.app.Activity;
import android.os.AsyncTask;

import com.dylan.fakemovinggps.core.data.DataSaver;
import com.dylan.fakemovinggps.core.data.DataSaver.Key;
import com.dylan.fakemovinggps.core.util.DLog;
import com.dylan.fakemovinggps.core.util.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;


@SuppressWarnings("unused")
public class FCMController {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = FCMController.class.getSimpleName();
    private static FCMController instance;
    private final Activity mContext;
    private String registeredId;

    private FCMController(Activity context) {
        this.mContext = context;
    }

    public static FCMController getInstance(Activity context) {
        if (instance == null)
            instance = new FCMController(context);
        return instance;
    }

    public void subscribe() {
        if (checkPlayServices()) {
            registeredId = getRegistrationId();
            if (Utils.isEmpty(registeredId)) {
                registerInBackground();
            } else {
                validateToken();
            }
        } else {
            DLog.d(TAG, "No valid Google Play Services APK found.");
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If it
     * doesn't, display a dialog that allows users to download the APK from the
     * Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GoogleApiAvailability.getInstance().isUserResolvableError(resultCode)) {
                GoogleApiAvailability.getInstance().getErrorDialog(mContext, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                DLog.d(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on FCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId() {
        try {
            final String registrationId = DataSaver.getInstance().getString(Key.FCM);
            if (Utils.isEmpty(registrationId)) {
                DLog.d(TAG, "Registration not found.");
                return "";
            }
            // Check if app was updated; if so, it must clear the registration
            // ID
            // since the existing regID is not guaranteed to work with the new
            // app version.
            String registeredVersion = DataSaver.getInstance().getString(
                    DataSaver.Key.VERSION);
            String currentVersion = Utils.getAppVersion();
            if (!registeredVersion.equals(currentVersion)) {
                DataSaver.getInstance().setEnabled(Key.UPDATED, false);
                DLog.d(TAG, "App version changed.");
                return "";
            } else {
                DLog.d(TAG, "App is already registered with id = "
                        + registrationId);
                return registrationId;
            }
        } catch (Exception e) {
            return "";
        }
    }

    private void validateToken() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    String newId = FirebaseInstanceId.getInstance().getToken();
                    if (registeredId.equals(newId)) {
                        if (!DataSaver.getInstance().isEnabled(Key.UPDATED))
                            sendRegistrationIdToBackend(registeredId);
                    } else {
                        try {
                            DataSaver.getInstance().setEnabled(Key.UPDATED, false);
                            sendRegistrationIdToBackend(newId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private void registerInBackground() {
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
                String msg = "";
                DLog.d(TAG, "Start registering...");
                try {
                    registeredId = FirebaseInstanceId.getInstance().getToken();

                    msg = "Device registered, registration id = " + registeredId;

                    if (!DataSaver.getInstance().isEnabled(Key.UPDATED))
                        sendRegistrationIdToBackend(registeredId);

                } catch (IOException e) {
                    msg = "Error :" + e.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                } catch (Exception e) {
                    e.printStackTrace();
                }
                DLog.d(TAG, msg);
                return null;
            }
        }.execute();
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use
     * FCM/HTTP or CCS to send messages to your app. Not needed for this demo
     * since the device sends upstream messages to a server that echoes back the
     * message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend(String regId) {
        DLog.d(TAG, "Send the registered id to server...");
        storeRegistrationId(regId);
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param regId registration ID
     */
    private void storeRegistrationId(String regId) {
        try {
            String appVersion = Utils.getAppVersion();
            DLog.d(TAG, "Saving regId on app version " + appVersion);
            DataSaver.getInstance().setString(Key.FCM, regId);
            DataSaver.getInstance().setString(Key.VERSION, appVersion);
            DataSaver.getInstance().setEnabled(Key.UPDATED, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
