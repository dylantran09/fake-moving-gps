package com.dylan.fakemovinggps.core.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.widget.Toast;

@SuppressWarnings("unused")
public class DToast extends Toast {

    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     *                or {@link Activity} object.
     */
    public DToast(Context context) {
        super(context);
    }

    @Override
    public void show() {
        if (Constant.DEBUG)
            super.show();
    }
}
