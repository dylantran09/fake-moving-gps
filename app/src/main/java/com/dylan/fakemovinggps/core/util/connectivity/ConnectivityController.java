package com.dylan.fakemovinggps.core.util.connectivity;

import android.content.ComponentName;
import android.content.pm.PackageManager;

import com.dylan.fakemovinggps.core.base.BaseApplication;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.WeakHashMap;

public final class ConnectivityController {

    private static final WeakHashMap<Object, ConnectivityListener> listeners = new WeakHashMap<>();
    private static ConnectivityController controller;

    public static ConnectivityController getInstance() {
        if (controller == null)
            controller = new ConnectivityController();
        return controller;
    }

    public static void enable() {
        if (BaseApplication.getContext() != null) {
            ComponentName receiver = new ComponentName(BaseApplication.getContext(), ConnectivityReceiver.class);
            PackageManager pm = BaseApplication.getContext().getPackageManager();
            pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            EventBus.getDefault().register(controller);
        }
    }

    public static void disable() {
        if (BaseApplication.getContext() != null) {
            PackageManager pm = BaseApplication.getContext().getPackageManager();
            pm.setComponentEnabledSetting(new ComponentName(BaseApplication.getContext(), ConnectivityReceiver.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            EventBus.getDefault().unregister(controller);
            clearListeners();
        }
    }

    public static void registerListener(ConnectivityListener listener) {
        if (listeners != null && !listeners.containsKey(listener) && listener != null) {
            listeners.put(listener, listener);
        }
    }

    public static void removeListener(ConnectivityListener listener) {
        if (listeners != null && listeners.containsValue(listener) && listener != null)
            listeners.remove(listener);
    }

    public static void clearListeners() {
        if (listeners != null)
            listeners.clear();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onMessageEvent(ConnectivityMessage message) {
        notifyListeners(message);
    }

    private void notifyListeners(ConnectivityMessage message) {
        for (ConnectivityListener listener : listeners.values()) {
            switch (message.getState()) {
                case CONNECTED:
                    listener.onConnected();
                    break;
                case DISCONNECTED:
                    listener.onDisconnected();
                    break;
            }
        }
    }

    public interface ConnectivityListener {

        void onConnected();

        void onDisconnected();
    }
}
