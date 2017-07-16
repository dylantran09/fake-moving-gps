package com.dylan.fakemovinggps.core.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dylan.fakemovinggps.core.dialog.ConfirmListener;
import com.dylan.fakemovinggps.core.util.ActionTracker;
import com.dylan.fakemovinggps.core.util.SingleClick;
import com.dylan.fakemovinggps.core.util.SingleClick.SingleClickListener;
import com.dylan.fakemovinggps.core.util.SingleTouch;
import com.dylan.fakemovinggps.core.util.Utils;

import butterknife.ButterKnife;
import butterknife.Unbinder;


@SuppressWarnings("unused")
public abstract class BaseFragment extends Fragment implements
        BaseInterface, SingleClickListener {

    /**
     * The flag to indicate all stack of fragments should resume when the host
     * activity is resuming. If true then all stacks will call resume, false
     * only the top fragment will call resume. Change true or false depends on
     * the behavior
     */
    private static final boolean isAllAttachedToActivityLifeCycle = false;

    /**
     * The single click to handle click action for this screen
     */
    private SingleClick singleClick = null;

    /**
     * Local active activity, in case the getActivity return null;
     */
    private BaseActivity activeActivity;

    /**
     * The unbinder of ButterKnife to unbind views when the fragment view is destroyed
     */
    private Unbinder unbinder;

    /**
     * The fragment layout res Id which will be inflated on onCreateView call
     */
    @LayoutRes
    protected abstract int getFragmentLayoutRes();

    /*
     * ANDROID LIFECYCLE
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onBaseCreate();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getFragmentLayoutRes(), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view != null)
            view.setClickable(true);
        onBindView();
        onInitializeViewData();
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if (activity instanceof BaseActivity) {
            activeActivity = (BaseActivity) activity;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAllAttachedToActivityLifeCycle) {
            // EventBus.getDefault().register(this);
            ActionTracker.enterScreen(getTag(), ActionTracker.Screen.FRAGMENT);
            onBaseResume();
        } else {
            resumeCurrentFragment();
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        getWorkingActivity().startActivityForResult(intent, requestCode);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        getWorkingActivity().startActivityForResult(intent, requestCode, options);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isAllAttachedToActivityLifeCycle) {
            onBasePause();
        } else {
            pauseCurrentFragment();
        }
    }

    @Override
    public void onDetach() {
        onBaseFree();
        Utils.nullViewDrawablesRecursive(getView());
        Utils.unbindDrawables(getView());
        super.onDetach();
        activeActivity = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null)
            unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        BaseApplication.getRefWatcher().watch(this);
    }

    /*
     * BASE INTERFACE
     */

    protected BaseActivity getWorkingActivity() {
        if (getActivity() != null
                && getActivity() instanceof BaseActivity)
            return (BaseActivity) getActivity();
        else if (getActiveActivity() != null
                && getActiveActivity() instanceof BaseActivity)
            return (BaseActivity) getActiveActivity();
        else
            return activeActivity;
    }

    @Override
    public void onBindView() {
        unbinder = ButterKnife.bind(this, getView());
        /* Views are bind by ButterKnife, override this for more actions on binding views */
    }

    @Override
    public String getResourceString(int id, Object... args) {
        return getWorkingActivity().getResourceString(id, args);
    }

    @Override
    public final void registerSingleAction(View... views) {
        for (View view : views) {
            if (view != null) {
                if (!isExceptionalView(view)) {
                    view.setOnClickListener(getSingleClick());
                    view.setOnTouchListener(getSingleTouch());
                }
            }
        }
    }

    @Override
    public final void unregisterSingleAction(View... views) {
        for (View view : views)
            if (view != null) {
                view.setOnClickListener(null);
                view.setOnTouchListener(null);
            }
    }

    @Override
    public final void registerSingleAction(@IdRes int... ids) {
        for (int id : ids) {
            View view = findViewById(id);
            if (view != null && !isExceptionalView(view)) {
                view.setOnClickListener(getSingleClick());
                view.setOnTouchListener(getSingleTouch());
            }
        }
    }

    @Override
    public void unregisterSingleAction(@IdRes int... ids) {
        for (int id : ids) {
            View view = findViewById(id);
            if (view != null) {
                view.setOnClickListener(null);
                view.setOnTouchListener(null);
            }
        }
    }

    @Override
    public final Activity getActiveActivity() {
        return BaseApplication.getActiveActivity();
    }

    @Override
    public final Context getBaseContext() {
        return BaseApplication.getContext();
    }

    @Override
    public final void showDecisionDialog(Context context, int id, @DrawableRes int icon,
                                      String title, String message, String confirm, String cancel,
                                      Object onWhat, ConfirmListener listener) {
        getWorkingActivity().showDecisionDialog(activeActivity, id, icon,
                title, message, confirm, cancel, onWhat, listener);
    }

    @Override
    public final void showAlertDialog(Context context, int id, @DrawableRes int icon,
                                      String title, String message, String confirm,
                                      Object onWhat, ConfirmListener listener) {
        getWorkingActivity().showAlertDialog(activeActivity, id, icon,
                    title, message, confirm, onWhat, listener);
    }

    @Override
    public final void showLoadingDialog(Context context, String loading) {
        getWorkingActivity().showLoadingDialog(activeActivity, loading);
    }

    @Override
    public final void closeLoadingDialog() {
        getWorkingActivity().closeLoadingDialog();
    }

    @Override
    public boolean isExceptionalView(View view) {
        return BaseProperties.isExceptionalView(view);
    }

    @Override
    public final SingleClick getSingleClick() {
        if (singleClick == null) {
            singleClick = new SingleClick();
            singleClick.setListener(this);
        }
        return singleClick;
    }

    @Override
    public final SingleTouch getSingleTouch() {
        return getWorkingActivity().getSingleTouch();
    }

    @AnimRes
    @Override
    public int getEnterInAnimation() {
        return -1;
    }

    @AnimRes
    @Override
    public int getBackInAnimation() {
        return -1;
    }

    @AnimRes
    @Override
    public int getEnterOutAnimation() {
        return -1;
    }

    @AnimRes
    @Override
    public int getBackOutAnimation() {
        return -1;
    }

    /*
     * BASE FRAGMENT
     */

    @IdRes
    public final int getMainContainerId() {
        return getWorkingActivity().getMainContainerId();
    }

    public String getUniqueTag() {
        return getClass().getSimpleName();
    }

    protected void onBasePause() {
        // EventBus.getDefault().unregister(this);
        closeLoadingDialog();
    }

    protected final View findViewById(@IdRes int id) {
        if (getView() != null) {
            return getView().findViewById(id);
        }
        return null;
    }

    protected final void finish() {
        if (getView() != null && getView().getParent() != null) {
            int containerId = ((ViewGroup) getView().getParent()).getId();
            if (containerId != View.NO_ID && containerId >= 0) {
                getWorkingActivity().backStack(containerId, null);
            }
        }
    }

    protected final void addMultipleFragments(@IdRes int containerId, BaseFragment... fragments) {
        getWorkingActivity().addMultipleFragments(containerId, fragments);
    }

    protected final void addFragment(@IdRes int containerId, BaseFragment fragment) {
        getWorkingActivity().addFragment(containerId, fragment);
    }

    protected final void replaceFragment(@IdRes int containerId,
                                         BaseFragment fragment, boolean clearStack) {
        getWorkingActivity().replaceFragment(containerId, fragment,
                    clearStack);
    }

    private void pauseCurrentFragment() {
        if (getView() != null && getView().getParent() != null) {
            int containerId = ((ViewGroup) getView().getParent()).getId();
            BaseFragment top = getWorkingActivity()
                    .getTopFragment(containerId);
            if (top != null && !Utils.isEmpty(top.getTag())
                    && getTag().equals(top.getTag())) {
                top.onBasePause();
            }
        }
    }

    private void resumeCurrentFragment() {
        if (getView() != null && getView().getParent() != null) {
            int containerId = ((ViewGroup) getView().getParent()).getId();
            BaseFragment top = getWorkingActivity()
                    .getTopFragment(containerId);
            if (top != null && !Utils.isEmpty(top.getTag())
                    && getTag().equals(top.getTag())) {
                // EventBus.getDefault().register(this);
                ActionTracker.enterScreen(getTag(), ActionTracker.Screen.FRAGMENT);
                onBaseResume();
            }
        }
    }
}
