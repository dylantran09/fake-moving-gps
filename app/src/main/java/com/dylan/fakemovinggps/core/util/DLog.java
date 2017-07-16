package com.dylan.fakemovinggps.core.util;

import android.util.Log;

import com.dylan.fakemovinggps.BuildConfig;

@SuppressWarnings({"PointlessBooleanExpression", "unused", "BuildConfigConditions"})
public class DLog {

    private static boolean isValid(String str) {
        return !(str == null);
    }

    public static void v(String Tag, String log) {
        if (BuildConfig.DEBUG && isValid(Tag) && isValid(log))
            Log.v(Tag, log);
    }

    public static void v(String Tag, String log, Throwable e) {
        if (BuildConfig.DEBUG && isValid(Tag) && isValid(log))
            Log.v(Tag, log, e);
    }

    public static void d(String Tag, String log) {
        if (BuildConfig.DEBUG && isValid(Tag) && isValid(log))
            Log.d(Tag, log);
    }

    public static void d(String Tag, String log, Throwable e) {
        if (BuildConfig.DEBUG && isValid(Tag) && isValid(log))
            Log.d(Tag, log, e);
    }

    public static void e(String Tag, String log) {
        if (BuildConfig.DEBUG && isValid(Tag) && isValid(log))
            Log.e(Tag, log);
    }

    public static void e(String Tag, String log, Throwable e) {
        if (BuildConfig.DEBUG && isValid(Tag) && isValid(log))
            Log.e(Tag, log, e);
    }

    public static void i(String Tag, String log) {
        if (BuildConfig.DEBUG && isValid(Tag) && isValid(log))
            Log.i(Tag, log);
    }

    public static void i(String Tag, String log, Throwable e) {
        if (BuildConfig.DEBUG && isValid(Tag) && isValid(log))
            Log.i(Tag, log, e);
    }

    public static void w(String Tag, String log) {
        if (BuildConfig.DEBUG && isValid(Tag) && isValid(log))
            Log.w(Tag, log);
    }

    public static void w(String Tag, String log, Throwable e) {
        if (BuildConfig.DEBUG && isValid(Tag) && isValid(log))
            Log.w(Tag, log, e);
    }
}
