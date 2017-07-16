package com.dylan.fakemovinggps.core.util;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**

 */

/**
 * @author Tyrael
 * @version 1.0
 *          <br>
 *          <br><b>Class Overview</b><br>
 *          <br>
 *          Represents a class for preventing multiple components touching. Indicating
 *          the status of touching up, down, cancel and general of a single component by
 *          implementing <b><code>SingleTouchListener</code></b>. All components need to
 *          prevent multiple touching should apply only <b>ONE</b> <b> <code>SingleTouch</code></b> as
 *          <b><code>OnTouchListener</code></b><br>
 *          <br>
 * @since September, 2013
 */

@SuppressWarnings("unused")
public class SingleTouch implements OnTouchListener {

    private int touched_view_id = -1;
    private SingleTouchListener listener;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (touched_view_id == -1) {
                touched_view_id = v.getId();
                if (listener != null)
                    listener.onTouchDown(v, event);
                return false;
            } else {
                return true;
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (touched_view_id == v.getId()) {
                touched_view_id = -1;
                if (listener != null)
                    listener.onTouchUp(v, event);
                ActionTracker.performAction(v.getResources().getResourceEntryName(v.getId()));
                return false;
            }
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            touched_view_id = -1;
            if (listener != null)
                listener.onTouchCancel(v, event);
            return false;
        } else {
            if (listener != null)
                listener.onTouch(v, event);
            return false;
        }
    }

    /**
     * @return the listener
     */
    public SingleTouchListener getListener() {
        return listener;
    }

    /**
     * @param listener the listener to set
     */
    public void setListener(SingleTouchListener listener) {
        this.listener = listener;
    }

    /**
     * public interface<br>
     * <b>SingleTouchListener</b><br>
     * <br>
     * <b>Class Overview</b> <br>
     * <br>
     * Used for receiving notifications from the <code>SingleTouch</code> when
     * event touch up, down, cancel and general of a single component is fired.<br>
     * <br>
     * <b>Summary</b>
     */
    public interface SingleTouchListener {
        /**
         * <b>Specified by:</b> onTouchDown(...) in SingleTouchListener <br>
         * <br>
         * This is called immediately before the touch-down event is being
         * fired.
         *
         * @param v     The view is being touched-down
         * @param event The event fired
         */
        void onTouchDown(View v, MotionEvent event);

        /**
         * <b>Specified by:</b> onTouchUp(...) in SingleTouchListener <br>
         * <br>
         * This is called immediately before the touch-up event is being fired.
         *
         * @param v     The view is being touched-up
         * @param event The event fired
         */
        void onTouchUp(View v, MotionEvent event);

        /**
         * <b>Specified by:</b> onTouchCancel(...) in SingleTouchListener <br>
         * <br>
         * This is called immediately before the touch-cancel event is being
         * fired.
         *
         * @param v     The view is being touched-cancel
         * @param event The event fired
         */
        void onTouchCancel(View v, MotionEvent event);

        /**
         * <b>Specified by:</b> onTouch(...) in SingleTouchListener <br>
         * <br>
         * This is called immediately before the touch event other than up, down
         * and cancel is being fired.
         *
         * @param v     The view is being touched
         * @param event The event fired
         */
        void onTouch(View v, MotionEvent event);
    }

}
