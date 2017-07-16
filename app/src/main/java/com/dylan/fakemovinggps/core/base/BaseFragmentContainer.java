package com.dylan.fakemovinggps.core.base;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class BaseFragmentContainer extends FrameLayout {

    public static final String TAG = BaseFragmentContainer.class.getName();

    public BaseFragmentContainer(Context context) {
        super(context);
        init();
    }

    public BaseFragmentContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseFragmentContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setContentDescription(TAG);
    }
}
