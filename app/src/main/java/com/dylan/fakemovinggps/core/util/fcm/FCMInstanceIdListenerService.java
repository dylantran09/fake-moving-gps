package com.dylan.fakemovinggps.core.util.fcm;

import android.content.Intent;

import com.dylan.fakemovinggps.core.util.DLog;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * @author Tyrael
 * @version 1.0 <br>
 * @since October 2015
 */
public class FCMInstanceIdListenerService extends FirebaseInstanceIdService {

    private static final String TAG = FCMInstanceIdListenerService.class.getSimpleName();

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        DLog.d(TAG, "FCM id has been refreshed");
        Intent intent = new Intent(this, FCMRegistrationService.class);
        startService(intent);
    }
    // [END refresh_token]
}