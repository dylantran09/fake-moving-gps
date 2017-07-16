package com.dylan.fakemovinggps.core.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.dylan.fakemovinggps.core.base.BaseApplication;
import com.dylan.fakemovinggps.core.util.Utils;

/**
 * @author Tyrael
 * @version 1.0 <br>
 *          <br>
 *          <b>Class Overview</b> <br>
 *          <br>
 *          Represents a class for storing data to the shared preference <br>
 * @since January 2014
 */

@SuppressWarnings({"BooleanMethodIsAlwaysInverted", "SameParameterValue", "UnusedReturnValue", "UnusedParameters"})
public class DataSaver {

    /**
     * The name of this storage in the system
     */
    private static final String KEY_SHARED_PREFERENCES = Utils.getSharedPreferenceKey();

    /**
     * Represent the instance of this class, only one instance can be used at a
     * time and apply for the entire application
     */
    private static DataSaver instance;

    /**
     * The reference to SharedPreferences which actually read and write the data
     * to the storage
     */
    private final SharedPreferences prefs;

    private DataSaver() {
        prefs = BaseApplication.getContext().getSharedPreferences(
                KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    /**
     * This method will return the instance of this class, only one instance can
     * be used at a time
     *
     * @return The instance of this class
     */
    public static synchronized DataSaver getInstance() {
        if (instance == null) {
            instance = new DataSaver();
        }
        return instance;
    }

    /**
     * This method is to set the QUEUE field to the storage.
     *
     * @param queue The queue int value
     * @return true if the process is success, false otherwise
     */
    private synchronized boolean setQueue(int queue) {
        return prefs.edit().putInt(Key.QUEUE.toString(), queue).commit();
    }

    /**
     * This method is to get the QUEUE value from the storage
     *
     * @return The QUEUE integer value, 0 if the field not presented
     */
    private synchronized int getQueue() {
        return prefs.getInt(Key.QUEUE.toString(), 0);
    }

    /**
     * This method is to set the VERSION field to the storage.
     *
     * @param version The version string value
     * @return true if the process is success, false otherwise
     */
    private synchronized boolean setVersion(String version) {
        return prefs.edit().putString(Key.VERSION.toString(), version).commit();
    }

    /**
     * This method is to get the VERSION value from the storage
     *
     * @return The VERSION string value, null if the field not presented
     */
    private synchronized String getVersion() {
        return prefs.getString(Key.VERSION.toString(), null);
    }

    /**
     * This method is to set the FCM field to the storage.
     *
     * @param gcm The gcm string value
     * @return true if the process is success, false otherwise
     */
    private synchronized boolean setGcm(String gcm) {
        return prefs.edit().putString(Key.FCM.toString(), gcm).commit();
    }

    /**
     * This method is to get the FCM value from the storage
     *
     * @return The FCM string value, null if the field not presented
     */
    private synchronized String getGcm() {
        return prefs.getString(Key.FCM.toString(), null);
    }

    /**
     * This method is to set the TOKEN field to the storage.
     *
     * @param token The token string value
     * @return true if the process is success, false otherwise
     */
    private synchronized boolean setToken(String token) {
        return prefs.edit().putString(Key.TOKEN.toString(), token).commit();
    }

    /**
     * This method is to get the TOKEN value from the storage
     *
     * @return The TOKEN string value, null if the field not presented
     */
    private synchronized String getToken() {
        return prefs.getString(Key.TOKEN.toString(), null);
    }

    /**
     * This method is to set the UPDATED status to the storage.
     *
     * @param isUpdated The updated boolean value
     * @return true if the process is success, false otherwise
     */
    private synchronized boolean setUpdated(boolean isUpdated) {
        return prefs.edit().putBoolean(Key.UPDATED.toString(), isUpdated)
                .commit();
    }

    /**
     * This method is to get the UPDATED value from the storage
     *
     * @return The UPDATED boolean value, false if the field not presented
     */
    private synchronized boolean isUpdated() {
        return prefs.getBoolean(Key.UPDATED.toString(), false);
    }

    /**
     * This method is to set the LOG status to the storage.
     *
     * @param isLogged The log boolean value
     * @return true if the process is success, false otherwise
     */
    private synchronized boolean setLogged(boolean isLogged) {
        return prefs.edit().putBoolean(Key.LOGGED.toString(), isLogged)
                .commit();
    }

    /**
     * This method is to get the LOG value from the storage
     *
     * @return The LOG boolean value, false if the field not presented
     */
    private synchronized boolean isLogged() {
        return prefs.getBoolean(Key.LOGGED.toString(), false);
    }

    /**
     * This method is to set the String value to the storage base on the KEY
     *
     * @param key   The key for the value, defined in <code>enum Key</code>
     * @param value The value for this key as a string
     * @return true if the process is success, false otherwise
     * @throws Exception if the key is not found in the storage
     */
    public synchronized boolean setString(Key key, String value)
            throws Exception {
        boolean result;
        switch (key) {
            case TOKEN:
                result = setToken(value);
                break;
            case FCM:
                result = setGcm(value);
                break;
            case VERSION:
                result = setVersion(value);
                break;
            default:
                throw new Exception("DataSaver:setString: No key found!");
        }

        return result;
    }

    /**
     * This method is to get a STRING value from the storage base on the KEY
     *
     * @param key The key for the value, defined in <code>enum Key</code>
     * @return The value of this key
     * @throws Exception if the key is not found in the storage
     */
    public synchronized String getString(Key key) throws Exception {
        String value;
        switch (key) {
            case TOKEN:
                value = getToken();
                break;
            case FCM:
                value = getGcm();
                break;
            case VERSION:
                value = getVersion();
                break;
            default:
                throw new Exception("DataSaver:getString: No key found!");
        }
        return value;
    }

    /**
     * This method is to get a INTEGER value from the storage base on the KEY
     *
     * @param key The key for the value, defined in <code>enum Key</code>
     * @return The value of this key
     * @throws Exception if the key is not found in the storage
     */
    public synchronized int getInt(Key key) throws Exception {
        int value;
        switch (Key.QUEUE) {
            case QUEUE:
                value = getQueue();
                break;
            default:
                throw new Exception("getInt: No key found!");
        }
        return value;
    }

    /**
     * This method is to set the INTEGER value to the storage base on the KEY
     *
     * @param key   The key for the value, defined in <code>enum Key</code>
     * @param value The value for this key as a int
     * @return true if the process is success, false otherwise
     * @throws Exception if the key is not found in the storage
     */
    public synchronized boolean setInt(Key key, int value)
            throws Exception {
        boolean result;
        switch (Key.QUEUE) {
            case QUEUE:
                result = setQueue(value);
                break;
            default:
                throw new Exception("DataSaver:setInt: No key found!");
        }
        return result;
    }

    /**
     * This method is to set the BOOLEAN value to the storage base on the KEY
     *
     * @param key   The key for the value, defined in <code>enum Key</code>
     * @param value The value for this key as
     *              <code>true<code> or <code>false<code>
     * @return true if the process is success, false otherwise
     * @throws Exception if the key is not found in the storage
     */
    public synchronized boolean setEnabled(Key key, boolean value)
            throws Exception {
        boolean result;
        switch (key) {
            case LOGGED:
                result = setLogged(value);
                break;
            case UPDATED:
                result = setUpdated(value);
                break;
            default:
                throw new Exception("DataSaver:setEnabled: No key found!");
        }

        return result;
    }

    /**
     * This method is to get a BOOLEAN value from the storage base on the KEY
     *
     * @param key The key for the value, defined in <code>enum Key</code>
     * @return The value of this key
     * @throws Exception if the key is not found in the storage
     */

    public synchronized boolean isEnabled(Key key) throws Exception {
        boolean value;
        switch (key) {
            case LOGGED:
                value = isLogged();
                break;
            case UPDATED:
                value = isUpdated();
                break;
            default:
                throw new Exception("DataSaver:isEnabled: No key found!");
        }
        return value;
    }

    /**
     * public enum <br>
     * <b>Key</b> <br>
     * Represents the key of stored data in share preference. Each key will
     * present a field and has the value of the override <code>toString()</code>
     */
    public enum Key {
        QUEUE {
            @Override
            public String toString() {
                return "queue";
            }
        },
        TOKEN {
            @Override
            public String toString() {
                return "token";
            }
        },
        LOGGED {
            @Override
            public String toString() {
                return "logged";
            }
        },
        FCM {
            @Override
            public String toString() {
                return "fcm";
            }
        },
        VERSION {
            @Override
            public String toString() {
                return "version";
            }
        },
        UPDATED {
            @Override
            public String toString() {
                return "updated";
            }
        }
    }

}
