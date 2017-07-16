package com.dylan.fakemovinggps.core.base;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class BaseApplication extends Application {
    // ImageLoader configuration
    public static final boolean MEMORY_CACHE = false;
    public static final boolean DISC_CACHE = true;
    public static final int LRU_CACHE_SIZE = 20 * 1024 * 1024; // 20MB
    public static final int MEMORY_CACHE_SIZE = 20 * 1024 * 1024; // 20MB
    public static final int DISC_CACHE_SIZE = 100 * 1024 * 1024; // 100MB
    public static final int DISC_CACHE_COUNT = 200; // 200 files cached

    private static Context mContext;
    private static AppCompatActivity mActiveActivity;
    private static RefWatcher mRefWatcher;

    public static Context getContext() {
        return mContext;
    }

    public static AppCompatActivity getActiveActivity() {
        return mActiveActivity;
    }

    public static void setActiveActivity(AppCompatActivity active) {
        mActiveActivity = active;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
//        initLeakDetection();
    }

    private void initLeakDetection() {
        mRefWatcher = LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher() {
        return mRefWatcher;
    }

//    private void initRealmDatabase() {
//        Realm.init(this);
//        RealmConfiguration config = new RealmConfiguration.Builder()
//                .name(Constant.DATABASE_NAME)
//                .schemaVersion(Constant.DATABASE_VERSION)
//                .deleteRealmIfMigrationNeeded()
//                .build();
//        Realm.setDefaultConfiguration(config);
//    }

    private void initImageLoader() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(MEMORY_CACHE)
                .cacheOnDisk(DISC_CACHE)
                .resetViewBeforeLoading(false)
//                .showImageForEmptyUri(R.drawable.ic_empty)
//                .showImageOnLoading(R.drawable.placeholder)
//                .showImageOnFail(R.drawable.ic_error)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .memoryCache(new WeakMemoryCache())
                .memoryCache(new LruMemoryCache(LRU_CACHE_SIZE))
                .diskCacheSize(DISC_CACHE_SIZE)
                .denyCacheImageMultipleSizesInMemory()
                .threadPoolSize(4)
                .threadPriority(Thread.MAX_PRIORITY)
                .defaultDisplayImageOptions(options)
                .build();
    }
}
