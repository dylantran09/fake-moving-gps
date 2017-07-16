package com.dylan.fakemovinggps.core.util;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.EditText;

/**
 * @author Tyrael
 * @since August, 2013
 */

/**
 * <b>Class Overview</b> <br>
 * <br>
 * Represents a class for soft keyboard detection. Indicating the status of
 * showing or hiding.<br>
 * <br>
 * <b>Summary</b>
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class SoftKeyBoardTracker {

    /**
     * The root view of the UI where the soft keyboard registered into
     */
    private final View root;

    /**
     * The soft keyboard listener which allows to response every time the soft
     * keyboard changes its status
     */
    private final OnKeyBoardListener listener;

    /**
     * The global layout listener to detect the change of the global layout
     */
    private OnGlobalLayoutListener layoutListener;

    /**
     * The flag that indicates the soft keyboard has shown or not
     */
    private boolean isKeyboardShown = false;

    private ViewTreeObserver.OnGlobalFocusChangeListener focusChangeListener;

    private EditText focusedView;

    public SoftKeyBoardTracker(View root, OnKeyBoardListener listener) {
        this.root = root;
        this.listener = listener;
        init();
    }

    /**
     * @return the isKeyboardShown
     */
    public boolean isKeyboardShown() {
        return isKeyboardShown;
    }

    /**
     * Removes the keyboard listener from this root view
     */

    public void remove() {
        if (root != null && layoutListener != null) {
            root.getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);
            root.getViewTreeObserver().removeOnGlobalFocusChangeListener(focusChangeListener);
        }
    }

    /**
     * Register the keyboard listener into this root view and call the method
     * every time the soft keyboard changes its status
     */
    private void init() {
        root.setFocusable(true);
        root.setFocusableInTouchMode(true);
        layoutListener = new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                // r will be populated with the coordinates of your view that
                // area still visible.
                root.getWindowVisibleDisplayFrame(r);

                int heightDiff = root.getRootView().getHeight()
                        - (r.bottom - r.top);
                if (heightDiff > 100) { // if more than 100 pixels, its probably
                    // a keyboard...
                    if (!isKeyboardShown) {
                        isKeyboardShown = true;
                        if (focusedView != null && listener != null)
                            listener.onKeyBoardShown(focusedView);
                    }
                } else {
                    if (isKeyboardShown) {
                        if (focusedView != null && listener != null)
                            listener.onKeyBoardHidden(focusedView);
                        isKeyboardShown = false;
                    }
                }
            }
        };

        focusChangeListener = new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                focusedView = null;
                if (newFocus instanceof EditText)
                    focusedView = (EditText) newFocus;
                else {
                    if (oldFocus != null)
                        oldFocus.clearFocus();
                    root.requestFocus();
                }
            }
        };
        root.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        root.getViewTreeObserver().addOnGlobalFocusChangeListener(focusChangeListener);
    }

    /**
     * public interface<br>
     * <b>OnKeyBoardListener</b><br>
     * <br>
     * <b>Class Overview</b> <br>
     * <br>
     * Used for receiving notifications from the
     * <code>SoftKeyBoardTracker</code> when soft keyboard has shown or hidden<br>
     * <br>
     * <b>Summary</b>
     */
    public interface OnKeyBoardListener {
        /**
         * <b>Specified by:</b> onKeyBoardShown() in OnKeyBoardListener <br>
         * <br>
         * This is called immediately after the soft keyboard is being shown.
         */
        void onKeyBoardShown(EditText focused);

        /**
         * <b>Specified by:</b> onKeyBoardHidden() in OnKeyBoardListener <br>
         * <br>
         * This is called immediately after the soft keyboard is being hidden.
         */
        void onKeyBoardHidden(EditText unFocused);
    }

}
