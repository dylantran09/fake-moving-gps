package com.dylan.fakemovinggps.core.util.notification;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.dylan.fakemovinggps.core.base.BaseApplication;
import com.dylan.fakemovinggps.core.util.Constant;

import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class NotificationController {

    public static final String NOTIFICATION_SERVICE_TAG = "Notification_Service_";
    public static final String NOTIFICATION_TAG = "Notification_";
    public static final String NOTIFICATION_SERVICE_CLASS_TARGET = "Notification_Class_Target";
    public static final String NOTIFICATION_TITLE = "Notification_Title";
    public static final String NOTIFICATION_MESSAGE = "Notification_Message";
    public static final String NOTIFICATION_ICON = "Notification_Icon";
    public static final String NOTIFICATION_ID = "Notification_Id";
    public static final String NOTIFICATION_CUSTOM = "Notification_Custom";
    public static final String NOTIFICATION_CUSTOM_ACTIONS = "Notification_Custom_Actions";
    public static final String NOTIFICATION_CUSTOM_VIEW = "Notification_Custom_View";


    public static void delay(int id, @DrawableRes int icon, String title,
                             String message, long when, Class<?> target, Intent src, RemoteViews view, NotificationRemoteAction... actions) {

        AlarmManager am = (AlarmManager) BaseApplication.getContext()
                .getSystemService(Activity.ALARM_SERVICE);
        if (am != null) {
            Intent intent = new Intent(BaseApplication.getContext(),
                    NotificationService.class);
            intent.putExtra(Constant.NOTIFICATION_DEFINED, true);
            intent.putExtra(NOTIFICATION_SERVICE_CLASS_TARGET, target.getName());
            intent.putExtra(NOTIFICATION_ID, id);
            intent.putExtra(NOTIFICATION_CUSTOM_VIEW, view);
            intent.putParcelableArrayListExtra(NOTIFICATION_CUSTOM_ACTIONS,
                    new ArrayList<>(Arrays.asList(actions)));
            intent.putExtra(NOTIFICATION_CUSTOM, false);
            intent.setAction(NOTIFICATION_SERVICE_TAG + id);
            intent.putExtras(src);

            PendingIntent sender = PendingIntent.getService(
                    BaseApplication.getContext(), 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            am.set(AlarmManager.RTC_WAKEUP, when, sender);
        }
    }

    public static void delay(int id, @DrawableRes int icon, String title,
                             String message, long when, Intent src, Class<?> target) {
        AlarmManager am = (AlarmManager) BaseApplication.getContext()
                .getSystemService(Activity.ALARM_SERVICE);
        if (am != null) {
            Intent intent = new Intent(BaseApplication.getContext(),
                    NotificationService.class);
            intent.putExtra(Constant.NOTIFICATION_DEFINED, true);
            intent.putExtra(NOTIFICATION_SERVICE_CLASS_TARGET, target.getName());
            intent.putExtra(NOTIFICATION_TITLE, title);
            intent.putExtra(NOTIFICATION_MESSAGE, message);
            intent.putExtra(NOTIFICATION_ICON, icon);
            intent.putExtra(NOTIFICATION_ID, id);
            intent.putExtra(NOTIFICATION_CUSTOM, true);
            intent.setAction(NOTIFICATION_SERVICE_TAG + id);
            intent.putExtras(src);
            PendingIntent sender = PendingIntent.getService(
                    BaseApplication.getContext(), 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            am.set(AlarmManager.RTC_WAKEUP, when, sender);
        }
    }

    public static void notify(int id, @DrawableRes int icon, String title, String message,
                              Intent intent) {
        NotificationManager manager = (NotificationManager) BaseApplication
                .getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setAction(NOTIFICATION_TAG + id);
            intent.putExtra(Constant.NOTIFICATION_DEFINED, true);
            PendingIntent contentIntent = PendingIntent.getActivity(
                    BaseApplication.getContext(), 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            Uri alarmSound = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    BaseApplication.getContext())
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setStyle(
                            new NotificationCompat.BigTextStyle()
                                    .bigText(message))
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(alarmSound)
                    .setDefaults(
                            Notification.DEFAULT_LIGHTS
                                    | Notification.DEFAULT_VIBRATE)
                    .setContentIntent(contentIntent);

            manager.notify(id, mBuilder.build());
        }
    }

    public static void notify(int id, @DrawableRes int icon, RemoteViews view,
                              NotificationRemoteAction... actions) {
        NotificationManager manager = (NotificationManager) BaseApplication
                .getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            if (actions != null) {
                for (NotificationRemoteAction action : actions) {
                    if (action != null) {
                        action.getIntent().putExtra(NOTIFICATION_ID, id);
                        action.getIntent().putExtra(Constant.NOTIFICATION_DEFINED, true);
                        action.getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(
                                BaseApplication.getContext(), 0, action.getIntent(),
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        if (view != null) {
                            view.setOnClickPendingIntent(action.getId(), pendingIntent);
                        }
                    }
                }
            }
            Uri alarmSound = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    BaseApplication.getContext())
                    .setSmallIcon(icon)
                    .setSound(alarmSound)
                    .setDefaults(
                            Notification.DEFAULT_LIGHTS
                                    | Notification.DEFAULT_VIBRATE)
                    .setContent(view);
            manager.notify(id, mBuilder.build());
        }
    }
}
