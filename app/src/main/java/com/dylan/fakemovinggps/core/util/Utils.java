package com.dylan.fakemovinggps.core.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Debug;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.dylan.fakemovinggps.core.base.BaseApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings({"WeakerAccess", "EmptyCatchBlock", "unused", "deprecation"})
public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    public static void closeSoftKeyboard(Context context, View root) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(root.getWindowToken(), 0);
    }

    public static void showSoftKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public static boolean isNetworkConnectionAvailable(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo != null) {
            if (netInfo.getType() == ConnectivityManager.TYPE_WIFI
                    || netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return netInfo.isConnected();
            }
        }
        return false;
    }

    public static String readStringFromAssets(Context context, String path) {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream in = null;
        BufferedReader buffer = null;
        try {
            in = context.getAssets().open(path);
            buffer = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String str;

            while ((str = buffer.readLine()) != null) {
                stringBuilder.append(str);
            }

            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (buffer != null)
                    buffer.close();
                if (in != null)
                    in.close();
            } catch (Exception e) {
                e.getMessage();
            }
        }
        return stringBuilder.toString();
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    public static int[] calculateScaleSize(int width, int height, int targetSize) {
        int[] result = new int[2];
        float scale;
        if (width >= height) {
            scale = width / targetSize;
        } else {
            scale = height / targetSize;
        }
        if (scale < 1) {
            scale = 1;
        }
        result[0] = (int) ((float) width / scale);
        result[1] = (int) ((float) height / scale);
        return result;
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        if (html == null) {
            return null;
        }
        Spanned result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    private static Drawable darkenDrawable(Context context, Bitmap original) {
        Paint p = new Paint();
        Bitmap mutated = original.copy(Bitmap.Config.ARGB_8888, true);
        Canvas c = new Canvas(mutated);
        ColorFilter filter = new LightingColorFilter(Constant.TINT_LEVEL,
                0x00000000);
        p.setColorFilter(filter);
        c.drawBitmap(mutated, 0, 0, p);
        return new BitmapDrawable(context.getResources(), mutated);
    }

    private static Drawable darkenColorDrawable(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= Constant.TINT_COLOR_LEVEL;
        color = Color.HSVToColor(hsv);
        return new ColorDrawable(color);
    }

    private static Drawable darkenNinePatchDrawable(Context context,
                                                    NinePatchDrawable drawable) {
        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return darkenDrawable(context, bitmap);
    }

    public static Drawable makeTintableStateDrawable(Context context, int id) {
        Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), id, context.getTheme());
        return makeTintableStateDrawable(context, drawable);
    }

    public static Drawable makeTintableStateDrawable(Context context,
                                                     Drawable drawable) {
        if (drawable != null) {
            StateListDrawable states = new StateListDrawable();
            Drawable darken = null;
            drawable.clearColorFilter();
            if (drawable instanceof StateListDrawable) {
                if (drawable.getCurrent() instanceof NinePatchDrawable) {
                    darken = darkenNinePatchDrawable(context,
                            (NinePatchDrawable) drawable.getCurrent());
                } else if (drawable.getCurrent() instanceof BitmapDrawable) {
                    darken = darkenDrawable(context,
                            ((BitmapDrawable) drawable.getCurrent())
                                    .getBitmap());
                }
            } else if (drawable instanceof BitmapDrawable) {
                darken = darkenDrawable(context,
                        ((BitmapDrawable) drawable).getBitmap());
            } else if (drawable instanceof ColorDrawable) {
                darken = darkenColorDrawable(((ColorDrawable) drawable)
                        .getColor());
            }
            if (darken != null) {
                states.addState(new int[]{android.R.attr.state_pressed},
                        darken);
                states.addState(new int[]{android.R.attr.state_focused},
                        darken);
            }
            states.addState(new int[]{}, drawable);
            return states;
        }
        return null;
    }

    public static boolean isNetworkConnectionAvailable() {
        ConnectivityManager conMgr = (ConnectivityManager) BaseApplication
                .getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo != null) {
            if (netInfo.getType() == ConnectivityManager.TYPE_WIFI
                    || netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return netInfo.isConnected();
            }
        }
        return false;
    }

    public static boolean isInternetAvailable() {
        if (isNetworkConnectionAvailable()) {
            try {
                return Runtime.getRuntime()
                        .exec("/system/bin/ping -c 1 8.8.8.8").waitFor() == 0;
            } catch (IOException | InterruptedException e) {
            }
        }
        return false;
    }

    public static Date getDateFromString(String str) {
        Calendar c = Calendar.getInstance();
        int year = Integer.parseInt(str.split("-")[0]);
        int month = Integer.parseInt(str.split("-")[1]);
        int day = Integer.parseInt(str.split("-")[2]);
        c.set(year, month - 1, day);
        return c.getTime();
    }

    public static long daysBetween(Date startDate, Date endDate) {
        Calendar sDate = getDatePart(startDate);
        Calendar eDate = getDatePart(endDate);
        long daysBetween = 0;
        while (sDate.before(eDate)) {
            sDate.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;
    }

    private static Calendar getDatePart(Date date) {
        Calendar cal = Calendar.getInstance(); // get calendar instance
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0); // set hour to midnight
        cal.set(Calendar.MINUTE, 0); // set minute in hour
        cal.set(Calendar.SECOND, 0); // set second in minute
        cal.set(Calendar.MILLISECOND, 0); // set millisecond in second

        return cal; // return the date part
    }

    public static double calculateDistance(double fromLong, double fromLat,
                                           double toLong, double toLat) {
        double d2r = Math.PI / 180;
        double dLong = (toLong - fromLong) * d2r;
        double dLat = (toLat - fromLat) * d2r;
        double a = Math.pow(Math.sin(dLat / 2.0), 2) + Math.cos(fromLat * d2r)
                * Math.cos(toLat * d2r) * Math.pow(Math.sin(dLong / 2.0), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = 6367000 * c;
        return Math.round(d);
    }

    public static double[] getMiddleLocation(double lat1, double lon1, double lat2, double lon2) {

        double dLon = Math.toRadians(lon2 - lon1);

        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        return new double[]{lat3, lon3};
    }

    public static boolean isEmpty(String str) {
        return (str == null) || str.equals(Constant.BLANK);
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device
     * density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need
     *                to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on
     * device density
     */
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    /**
     * This method converts device specific pixels to density independent
     * pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / (metrics.densityDpi / 160f);
    }

    public static String getUID() {
        return "35"
                + // we make this look like a valid IMEI
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10
                + Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10
                + Build.DISPLAY.length() % 10 + Build.HOST.length() % 10
                + Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10
                + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10
                + Build.TAGS.length() % 10 + Build.TYPE.length() % 10
                + Build.USER.length() % 10; // 13 digits
    }

    public static String getSharedPreferenceKey() {
        PackageInfo pInfo;
        try {
            pInfo = BaseApplication
                    .getContext()
                    .getPackageManager()
                    .getPackageInfo(
                            BaseApplication.getContext().getPackageName(), 0);
            return getUID() + "." + pInfo.packageName;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return getUID();
    }

    public static String getAppVersion() {
        PackageInfo pInfo;
        try {
            pInfo = BaseApplication
                    .getContext()
                    .getPackageManager()
                    .getPackageInfo(
                            BaseApplication.getContext().getPackageName(), 0);
            return pInfo.versionName;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void nullViewDrawablesRecursive(View view) {
        if (view != null) {
            try {
                ViewGroup viewGroup = (ViewGroup) view;

                int childCount = viewGroup.getChildCount();
                for (int index = 0; index < childCount; index++) {
                    View child = viewGroup.getChildAt(index);
                    nullViewDrawablesRecursive(child);
                }
            } catch (Exception e) {
            }

            nullViewDrawable(view);
        }
        System.gc();
    }

    private static void nullViewDrawable(View view) {
        try {
            // Drawable background = view.getBackground();
            // if (background != null && background instanceof BitmapDrawable) {
            // BitmapDrawable bitmapDrawable = (BitmapDrawable) background;
            // Bitmap bitmap = bitmapDrawable.getBitmap();
            // if (bitmap != null && !bitmap.isRecycled()) {
            // bitmap.recycle();
            // bitmap = null;
            // }
            // background = null;
            // }
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN)
                view.setBackgroundDrawable(null);
            else
                view.setBackground(null);
            view.setBackgroundResource(0);
        } catch (Exception e) {
        }

        try {
            ImageView imageView = (ImageView) view;
            //
            // Drawable drawable = imageView.getDrawable();
            //
            // if (drawable != null && drawable instanceof BitmapDrawable) {
            // BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            // Bitmap bitmap = bitmapDrawable.getBitmap();
            // if (bitmap != null && !bitmap.isRecycled()) {
            // bitmap.recycle();
            // bitmap = null;
            // }
            // drawable = null;
            // }
            imageView.setImageDrawable(null);
            imageView.setImageBitmap(null);
            imageView.setImageResource(0);

        } catch (Exception e) {
        }

        try {
            ImageButton imageButton = (ImageButton) view;
            //
            // Drawable drawable = imageView.getDrawable();
            //
            // if (drawable != null && drawable instanceof BitmapDrawable) {
            // BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            // Bitmap bitmap = bitmapDrawable.getBitmap();
            // if (bitmap != null && !bitmap.isRecycled()) {
            // bitmap.recycle();
            // bitmap = null;
            // }
            // drawable = null;
            // }
            imageButton.setImageDrawable(null);
            imageButton.setImageBitmap(null);
            imageButton.setImageResource(0);

        } catch (Exception e) {
        }
        System.gc();
    }

    public static void unbindDrawables(View view) {
        if (view != null) {
            if (view.getBackground() != null) {
                view.getBackground().setCallback(null);
            }
            if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    unbindDrawables(((ViewGroup) view).getChildAt(i));
                }
                ((ViewGroup) view).removeAllViews();
            }
        }
        System.gc();
    }

    public static void logHeap(String tag) {
        Double allocated = (double) Debug.getNativeHeapAllocatedSize()
                / (double) (1048576);
        Double available = (double) Debug.getNativeHeapSize() / 1048576.0;
        Double free = (double) Debug.getNativeHeapFreeSize() / 1048576.0;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

        DLog.d(TAG, "debug." + tag);
        DLog.d(TAG, "debug.heap native: allocated " + df.format(allocated)
                + "MB of " + df.format(available) + "MB (" + df.format(free)
                + "MB free)");
        DLog.d(TAG,
                "debug.memory: allocated: "
                        + df.format((double) (Runtime.getRuntime()
                        .totalMemory() / 1048576))
                        + "MB of "
                        + df.format((double) (Runtime.getRuntime().maxMemory() / 1048576))
                        + "MB ("
                        + df.format((double) (Runtime.getRuntime()
                        .freeMemory() / 1048576)) + "MB free)");

        DLog.d(TAG,
                "debug.memory: actual allocated: "
                        + df.format((Runtime.getRuntime().totalMemory() / 1048576)
                        - ((double) (Runtime.getRuntime()
                        .freeMemory() / 1048576)))
                        + "MB of "
                        + df.format((double) (Runtime.getRuntime().maxMemory() / 1048576))
                        + "MB");
    }
}
