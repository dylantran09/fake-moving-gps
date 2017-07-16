package com.dylan.fakemovinggps.core.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.dylan.fakemovinggps.R;
import com.dylan.fakemovinggps.core.base.BaseActivity;

public class BaseAlertDialog extends DialogFragment {
    public static final String TAG = BaseAlertDialog.class.getSimpleName();

    private BaseActivity mActivity;

    private int id;
    private String title;
    private String message;
    private String positiveButton;
    private String negativeButton;
    private Object onWhat;
    private ConfirmListener listener;

    public static BaseAlertDialog getInstance(int id, String title, String message,
                                            String positiveButton, String negativeButton,
                                            Object onWhat, ConfirmListener listener) {
        BaseAlertDialog dialog = new BaseAlertDialog();
        dialog.setId(id);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setPositiveButton(positiveButton);
        dialog.setNegativeButton(negativeButton);
        dialog.setOnWhat(onWhat);
        dialog.setListener(listener);
        return dialog;
    }

    public static BaseAlertDialog getInstance(int id, String title, String message,
                                            String positiveButton,
                                            Object onWhat, ConfirmListener listener) {
        BaseAlertDialog dialog = new BaseAlertDialog();
        dialog.setId(id);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setPositiveButton(positiveButton);
        dialog.setOnWhat(onWhat);
        dialog.setListener(listener);
        return dialog;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPositiveButton(String positiveButton) {
        this.positiveButton = positiveButton;
    }

    public void setNegativeButton(String negativeButton) {
        this.negativeButton = negativeButton;
    }

    public void setOnWhat(Object onWhat) {
        this.onWhat = onWhat;
    }

    public void setListener(ConfirmListener onClickHandleAlert) {
        this.listener = onClickHandleAlert;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        mActivity = (BaseActivity) activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setRetainInstance(true);
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.BaseAlertDialog);

        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }

        if (!TextUtils.isEmpty(message)) {
            builder.setMessage(message);
        }

        if (!TextUtils.isEmpty(positiveButton)) {
            builder.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        if (!TextUtils.isEmpty(negativeButton)) {
            builder.setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            if (listener != null) {
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (listener != null) {
            listener.onConfirmed(id, onWhat);
        }
    }
}
