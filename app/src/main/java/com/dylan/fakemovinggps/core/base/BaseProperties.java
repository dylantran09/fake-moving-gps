package com.dylan.fakemovinggps.core.base;

import android.app.ProgressDialog;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.dylan.fakemovinggps.core.dialog.BaseAlertDialog;
import com.dylan.fakemovinggps.core.util.SingleBackPress;
import com.dylan.fakemovinggps.core.util.SingleTouch;

public abstract class BaseProperties {

    /**
     * Loading dialog reference, this loading dialog will be applied for the
     * entire application
     */
    public static ProgressDialog loadingDialog = null;

    /**
     * Alert dialog reference, this decision dialog will be applied for the
     * entire application
     */
    public static BaseAlertDialog alertDialog = null;

    /**
     * Single touch reference, this single touch will be applied for components
     * to ensure only one component touched at the same time
     */
    private static SingleTouch singleTouch = null;

    /**
     * Single back-press reference, the single back-press ensure only the
     * back-press event only execute once after a short period
     */
    private static SingleBackPress singleBackPress = null;

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isExceptionalView(View view) {
        return ((view instanceof AdapterView) || (view instanceof EditText)
                || (view instanceof SwipeRefreshLayout)
                || (view instanceof DrawerLayout) || (view instanceof ViewPager));
    }

    public static SingleTouch getSingleTouch() {
        if (singleTouch == null)
            singleTouch = new SingleTouch();
        return singleTouch;
    }

    public static SingleBackPress getSingleBackPress() {
        if (singleBackPress == null)
            singleBackPress = new SingleBackPress();
        return singleBackPress;
    }
}
