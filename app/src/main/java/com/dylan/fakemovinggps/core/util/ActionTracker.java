package com.dylan.fakemovinggps.core.util;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.dylan.fakemovinggps.core.base.BaseApplication;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@SuppressWarnings({"PointlessBooleanExpression", "ResultOfMethodCallIgnored"})
public final class ActionTracker {

    public static final int STORAGE_PERMISSION_REQUEST_CODE = 9011;
    private static File action;
    private static FileWriter fw;
    private static DateFormat actionTime = new SimpleDateFormat("HH:mm:ss");

    public static void openActionLog() {
        if (!Constant.DEBUG)
            return;
        try {
            int permission = ContextCompat.checkSelfPermission(BaseApplication.getContext(), Manifest.permission_group.STORAGE);
            if (permission == PackageManager.PERMISSION_GRANTED) {
                DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                action = new File(Environment.getExternalStorageDirectory()
                        .getPath()
                        + "/"
                        + BaseApplication.getContext().getPackageName()
                        .replace(".", "_"), "action_" + formatter.format(new Date(System.currentTimeMillis())) + ".txt");
                if (!action.exists())
                    action.createNewFile();
                fw = new FileWriter(action);
            } else {
                ActivityCompat.requestPermissions(BaseApplication.getActiveActivity(), new String[]{
                        Manifest.permission_group.STORAGE
                }, STORAGE_PERMISSION_REQUEST_CODE);
            }
        } catch (Exception e) {
        }
    }

    public static void enterScreen(String name, Screen screen) {
        if (!Constant.DEBUG)
            return;
        try {
            if (!Utils.isEmpty(name) && fw != null) {
                String append = "";
                Date now = new Date();
                switch (screen) {
                    case ACTIVITY:
                        append = actionTime.format(now) + "> " + name + " > Visible\n";
                        break;
                    case FRAGMENT:
                        append = actionTime.format(now) + "   > " + name + " > Visible\n";

                }
                fw.append(append);
                fw.flush();
            }
        } catch (Exception e) {
        }
    }

    public static void exitScreen(String name) {
        if (!Constant.DEBUG)
            return;
        try {
            if (!Utils.isEmpty(name) && fw != null) {
                fw.append(actionTime.format(new Date()) + "< ").append(name).append("\n");
                fw.flush();
            }
        } catch (Exception e) {
        }
    }

    public static void performAction(String action) {
        if (!Constant.DEBUG)
            return;
        try {
            if (!Utils.isEmpty(action) && fw != null) {
                fw.append(actionTime.format(new Date()) + "      > touch view: ").append(action).append("\n");
                fw.flush();
            }
        } catch (Exception e) {
        }
    }

    public static void closeWithCrashActionLog() {
        if (!Constant.DEBUG)
            return;
        try {
            if (fw != null) {
                fw.append(actionTime.format(new Date()) + ">>CRASHED<<");
                fw.flush();
                fw.close();
            }
            fw = null;
            action = null;
        } catch (Exception e) {
        }
    }

    public static void closeActionLog() {
        if (!Constant.DEBUG)
            return;
        try {
            if (fw != null) {
                fw.append(actionTime.format(new Date()) + ">>EXIT<<");
                fw.flush();
                fw.close();
            }
            fw = null;
            action = null;
        } catch (Exception e) {
        }
    }

    public enum Screen {
        ACTIVITY, FRAGMENT
    }
}
