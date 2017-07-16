package com.dylan.fakemovinggps.core.util.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.dylan.fakemovinggps.core.base.BaseApplication;

import org.greenrobot.eventbus.EventBus;

@SuppressWarnings("unused")
public class ConnectivityReceiver extends BroadcastReceiver {
    private static final ConnectivityManager manager = (ConnectivityManager) BaseApplication
            .getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    private static boolean connected = true;
    private static boolean disconnected = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

            final NetworkInfo activeNetInfo = manager.getActiveNetworkInfo();
            if (activeNetInfo != null) {
                if (connected) {
                    connected = false;
                    disconnected = true;
                    if (activeNetInfo.isConnected()) {
                        EventBus.getDefault().post(new ConnectivityMessage(ConnectivityMessage.State.CONNECTED));
                    }
                }
            } else {
                if (disconnected) {
                    connected = true;
                    disconnected = false;
                    EventBus.getDefault().post(new ConnectivityMessage(ConnectivityMessage.State.DISCONNECTED));
                }
            }
        }
    }
}
