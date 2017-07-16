package com.dylan.fakemovinggps.core.util;

import android.os.SystemClock;

/**
 * @author Tyrael
 * @since December, 2013
 */

/**
 * <b>Class Overview</b> <br>
 * <br>
 * Represents a class for preventing multiple back-pressing events <br>
 * <b>Summary</b>
 */

public class SingleBackPress {

    private static long lastTimestamp;

    public SingleBackPress() {
    }

    public boolean onBackPressAllowed() {
        long currentTimestamp = SystemClock.uptimeMillis();
        if (lastTimestamp == 0) {
            lastTimestamp = currentTimestamp;
            return true;
        } else {
            if (currentTimestamp - lastTimestamp >= Constant.INTERVAL_BACK_PRESS) {
                lastTimestamp = currentTimestamp;
                return true;
            }
            lastTimestamp = currentTimestamp;
            return false;
        }
    }
}
