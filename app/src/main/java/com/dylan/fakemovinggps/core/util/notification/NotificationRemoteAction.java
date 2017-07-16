package com.dylan.fakemovinggps.core.util.notification;


import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

public final class NotificationRemoteAction implements Parcelable {

    public static final Creator<NotificationRemoteAction> CREATOR = new Creator<NotificationRemoteAction>() {
        @Override
        public NotificationRemoteAction createFromParcel(Parcel in) {
            return new NotificationRemoteAction(in);
        }

        @Override
        public NotificationRemoteAction[] newArray(int size) {
            return new NotificationRemoteAction[size];
        }
    };
    @IdRes
    @NonNull
    private int id;
    @NonNull
    private Intent intent;

    public NotificationRemoteAction(@IdRes @NonNull int id, @NonNull Intent intent) {
        this.id = id;
        this.intent = intent;
    }

    protected NotificationRemoteAction(Parcel in) {
        id = in.readInt();
        intent = in.readParcelable(Intent.class.getClassLoader());
    }

    @NonNull
    @IdRes
    public int getId() {
        return id;
    }

    @NonNull
    public Intent getIntent() {
        return intent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeParcelable(intent, flags);
    }
}
