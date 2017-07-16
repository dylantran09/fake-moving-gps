package com.dylan.fakemovinggps.core.util;

import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * @author Tyrael
 * @since December, 2013
 */

/**
 * <b>Class Overview</b> <br>
 * <br>
 * Represents a class for preventing multiple clicking events of a single
 * component by implementing <b><code>SingleClickListener</code></b>. All
 * components need to prevent multiple clicking should apply <b>
 * <code>SingleClick</code></b> to <b><code>OnClickListener</code></b><br>
 * <br>
 * <b>Summary</b>
 */

@SuppressWarnings("unused")
public class SingleClick implements OnClickListener {

    private SingleClickListener listener;
    private LastClick lastClick;

    @Override
    public void onClick(View v) {
        long currentTimestamp = SystemClock.uptimeMillis();
        if (lastClick != null) {
            if (listener != null
                    && !(currentTimestamp - lastClick.getTimeStamp() <= Constant.INTERVAL_CLICK))
                listener.onSingleClick(v);
        } else {
            if (listener != null)
                listener.onSingleClick(v);
        }
        lastClick = new LastClick(currentTimestamp);
    }

    public SingleClickListener getListener() {
        return listener;
    }

    public void setListener(SingleClickListener listener) {
        this.listener = listener;
    }

    /**
     * public interface<br>
     * <b>SingleClickListener</b><br>
     * <br>
     * <b>Class Overview</b> <br>
     * <br>
     * Used for receiving notifications from the <code>SingleClick</code> when
     * event click of a single component is fired.<br>
     * <br>
     * <b>Summary</b>
     */
    public interface SingleClickListener {
        /**
         * <b>Specified by:</b> onSingleClick(...) in SingleClickListener <br>
         * <br>
         * This is called immediately after the click event is being fired
         * within the pre-defined minimum interval time.
         *
         * @param v The view is being clicked
         */
        void onSingleClick(View v);
    }

    private class LastClick {
        private final long timeStamp;

        public LastClick(long timeStamp) {
            super();
            this.timeStamp = timeStamp;
        }

        /**
         * @return the timeStamp
         */
        public long getTimeStamp() {
            return timeStamp;
        }

    }

}
