package com.dylan.fakemovinggps.core.util.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

public class TelephonyReceiver extends BroadcastReceiver {

    //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;  //because the passed incoming is only valid in ringing


    @Override
    public void onReceive(Context context, Intent intent) {

        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
        } else {
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if (TelephonyManager.EXTRA_STATE_IDLE.equals(stateStr)) {
                state = TelephonyManager.CALL_STATE_IDLE;
            } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(stateStr)) {
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            } else if (TelephonyManager.EXTRA_STATE_RINGING.equals(stateStr)) {
                state = TelephonyManager.CALL_STATE_RINGING;
            }


            onCallStateChanged(context, state, number);
        }
    }

    //Deals with actual events
    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    public void onCallStateChanged(Context context, int state, String number) {
        if (lastState == state) {
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;
                EventBus.getDefault().post(new TelephonyMessage(TelephonyMessage.State.INCOMING_RECEIVED, number, callStartTime, null));
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false;
                    callStartTime = new Date();
                    EventBus.getDefault().post(new TelephonyMessage(TelephonyMessage.State.OUTGOING_STARTED, savedNumber, callStartTime, null));
                } else {
                    isIncoming = true;
                    callStartTime = new Date();
                    EventBus.getDefault().post(new TelephonyMessage(TelephonyMessage.State.INCOMING_ANSWERED, savedNumber, callStartTime, null));
                }

                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    //Ring but no pickup-  a miss
                    EventBus.getDefault().post(new TelephonyMessage(TelephonyMessage.State.MISSED, savedNumber, callStartTime, null));
                } else if (isIncoming) {
                    EventBus.getDefault().post(new TelephonyMessage(TelephonyMessage.State.INCOMING_ENDED, savedNumber, callStartTime, new Date()));
                } else {
                    EventBus.getDefault().post(new TelephonyMessage(TelephonyMessage.State.OUTGOING_ENDED, savedNumber, callStartTime, new Date()));
                }
                break;
        }
        lastState = state;
    }
}