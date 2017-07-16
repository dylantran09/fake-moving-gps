package com.dylan.fakemovinggps.core.util.notification;

import android.app.IntentService;
import android.content.Intent;
import android.widget.RemoteViews;

import com.dylan.fakemovinggps.core.base.BaseApplication;
import com.dylan.fakemovinggps.core.util.Utils;

import java.util.ArrayList;

public class NotificationService extends IntentService {

    public NotificationService() {
        super("NotificationService");
    }

    public NotificationService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null
                && intent.getExtras() != null
                && !Utils.isEmpty(intent.getStringExtra(NotificationController.NOTIFICATION_SERVICE_CLASS_TARGET))) {
            boolean isCustom = intent.getBooleanExtra(
                    NotificationController.NOTIFICATION_CUSTOM, false);
            String target = intent
                    .getStringExtra(NotificationController.NOTIFICATION_SERVICE_CLASS_TARGET);
            int id = intent.getIntExtra(NotificationController.NOTIFICATION_ID,
                    0);
            String title = intent
                    .getStringExtra(NotificationController.NOTIFICATION_TITLE);
            String message = intent
                    .getStringExtra(NotificationController.NOTIFICATION_MESSAGE);
            int icon = intent.getIntExtra(NotificationController.NOTIFICATION_ICON, 0);
            ArrayList<NotificationRemoteAction> actions = intent.getParcelableArrayListExtra(NotificationController.NOTIFICATION_CUSTOM_ACTIONS);
            RemoteViews view = intent.getParcelableExtra(NotificationController.NOTIFICATION_CUSTOM_VIEW);

            intent.removeExtra(NotificationController.NOTIFICATION_ID);
            intent.removeExtra(NotificationController.NOTIFICATION_TITLE);
            intent.removeExtra(NotificationController.NOTIFICATION_MESSAGE);
            intent.removeExtra(NotificationController.NOTIFICATION_ICON);
            intent.removeExtra(NotificationController.NOTIFICATION_SERVICE_CLASS_TARGET);
            intent.removeExtra(NotificationController.NOTIFICATION_CUSTOM);
            intent.removeExtra(NotificationController.NOTIFICATION_CUSTOM_ACTIONS);
            intent.removeExtra(NotificationController.NOTIFICATION_CUSTOM_VIEW);

            intent.setClassName(BaseApplication.getContext(), target);
            if (isCustom) {
                NotificationController.notify(id, icon, view, actions.toArray(new NotificationRemoteAction[0]));
            } else {
                NotificationController.notify(id, icon, title, message, intent);
            }

        }
    }

}
