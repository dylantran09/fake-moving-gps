package com.dylan.fakemovinggps.core.util.telephony;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.dylan.fakemovinggps.core.base.BaseApplication;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;
import java.util.WeakHashMap;

public final class TelephonyController {

    public static final int PHONE_PERMISSION_REQUEST_CODE = 9005;
    private static final WeakHashMap<Object, OnTelephonyMessageReceivedListener> listeners = new WeakHashMap<>();
    private static TelephonyController controller;

    public static TelephonyController getInstance() {
        if (controller == null)
            controller = new TelephonyController();
        return controller;
    }

    public static void enable() {
        int permission = ContextCompat.checkSelfPermission(BaseApplication.getContext(), Manifest.permission_group.PHONE);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            if (controller != null)
                EventBus.getDefault().register(controller);
        } else {
            ActivityCompat.requestPermissions(BaseApplication.getActiveActivity(), new String[]{Manifest.permission_group.PHONE}, PHONE_PERMISSION_REQUEST_CODE);
        }
    }

    public static void disable() {
        if (controller != null) {
            EventBus.getDefault().unregister(controller);
            clearListeners();
        }
    }

    public static void registerListener(OnTelephonyMessageReceivedListener listener) {
        if (listeners != null && !listeners.containsKey(listener) && listener != null) {
            listeners.put(listener, listener);
        }
    }

    public static void removeListener(OnTelephonyMessageReceivedListener listener) {
        if (listeners != null && listener != null && listeners.containsKey(listener)) {
            listeners.remove(listener);
        }
    }

    public static void clearListeners() {
        if (listeners != null) {
            listeners.clear();
        }
    }

    private void notifyListeners(TelephonyMessage message) {
        for (OnTelephonyMessageReceivedListener listener : listeners.values()) {
            switch (message.getState()) {
                case MISSED:
                    listener.onMissedCall(message.getNumber(), message.getStart());
                    break;
                case INCOMING_RECEIVED:
                    listener.onIncomingCallReceived(message.getNumber(), message.getStart());
                    break;
                case INCOMING_ANSWERED:
                    listener.onIncomingCallAnswered(message.getNumber(), message.getStart());
                    break;
                case INCOMING_ENDED:
                    listener.onIncomingCallEnded(message.getNumber(), message.getStart(), message.getEnd());
                    break;
                case OUTGOING_STARTED:
                    listener.onOutgoingCallStarted(message.getNumber(), message.getStart());
                    break;
                case OUTGOING_ENDED:
                    listener.onOutgoingCallEnded(message.getNumber(), message.getStart(), message.getEnd());
                    break;
                default:
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onMessageEvent(TelephonyMessage message) {
        notifyListeners(message);
    }

    public interface OnTelephonyMessageReceivedListener {
        void onIncomingCallReceived(String number, Date start);

        void onIncomingCallAnswered(String number, Date start);

        void onIncomingCallEnded(String number, Date start, Date end);

        void onOutgoingCallStarted(String number, Date start);

        void onOutgoingCallEnded(String number, Date start, Date end);

        void onMissedCall(String number, Date start);
    }


}
