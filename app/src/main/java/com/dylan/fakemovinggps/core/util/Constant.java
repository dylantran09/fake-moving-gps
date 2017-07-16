package com.dylan.fakemovinggps.core.util;

import com.dylan.fakemovinggps.BuildConfig;
import com.dylan.fakemovinggps.R;

@SuppressWarnings({"UnusedParameters", "PointlessBooleanExpression", "unused", "ConstantConditions"})
public class Constant {

    /* COMMON VARIABLES */
    public static final int[] DEFAULT_ADD_ANIMATION = {R.anim.slide_in_right,
            R.anim.slide_out_left};
    public static final int[] DEFAULT_BACK_ANIMATION = {R.anim.slide_in_left,
            R.anim.slide_out_right};
    public static final String NOTIFICATION_DEFINED = "Notification_Defined";
    public static final String NOTIFICATION_ID = "Notification_Id";
    /* END COMMON VARIABLES */

    /* DEBUG */
    public static final boolean DEBUG = BuildConfig.DEBUG;
    /* END DEBUG */

    /* SYSTEM */
    public static final String BLANK = "";
    public static final String EOF = System.getProperty("line.separator");
    public static final int INTERVAL_CLICK = 500; // 500ms
    public static final int INTERVAL_BACK_PRESS = 300; // 300ms
    public static final int TINT_LEVEL = 0xFFaaaaaa; // 0xFFaaaaaa
    public static final float TINT_COLOR_LEVEL = 0.68f; // 0.68f
    /* END SYSTEM */

    /* NETWORK */
    public static final boolean NETWORK_ERROR_DATA_HANDLE = true;
    public static final String SERVER_URL = "";
    public static final String KEY_STORE_TYPE = "";
    public static final String KEY_STORE_PASSWORD = "";
    public static final int KEY_STORE_ID = 0;
    public static final boolean SSL_ENABLED = !(Utils.isEmpty(KEY_STORE_PASSWORD) && KEY_STORE_ID == 0);
    public static final int TIMEOUT_CONNECT = DEBUG ? 5000 : 10000;
    public static final int RETRY_CONNECT = DEBUG ? 0 : 2;

    public enum RequestType {
        HTTP {
            @Override
            public String toString() {
                return "http://";
            }
        },
        HTTPS {
            @Override
            public String toString() {
                return "https://";
            }
        }
    }

    public enum StatusCode {
        OK, ERR_SSL, ERR_UNKNOWN,
        ERR_PARSING, ERR_AUTH_FAIL,
        ERR_SERVER_FAIL, ERR_NO_CONNECTION,
        ERR_TIME_OUT, ERR_STORE_FILE,
        ERR_QUEUE_IN_REQUEST, ERR_REQUEST_POOL_FULL
    }

    public enum RequestMethod {
        GET, POST, DELETE, HEAD, OPTIONS, PATCH, PUT, TRACE;
    }

    /* END NETWORK */

    public enum Header {
        ACCEPT {
            @Override
            public String toString() {
                return "Accept";
            }
        },
        ACCEPT_CHARSET {
            @Override
            public String toString() {
                return "Accept-Charset";
            }
        },
        ACCEPT_ENCODING {
            @Override
            public String toString() {
                return "Accept-Encoding";
            }
        },
        ACCEPT_LANGUAGE {
            @Override
            public String toString() {
                return "Accept-Language";
            }
        },
        AUTHORIZATION {
            @Override
            public String toString() {
                return "Authorization";
            }
        },
        CACHE_CONTROL {
            @Override
            public String toString() {
                return "Cache-Control";
            }
        },
        CONNECTION {
            @Override
            public String toString() {
                return "Connection";
            }
        },
        CONTENT_LENGTH {
            @Override
            public String toString() {
                return "Content-Length";
            }
        },
        CONTENT_TYPE {
            @Override
            public String toString() {
                return "Content-Type";
            }
        },
        USER_AGENT {
            @Override
            public String toString() {
                return "User-Agent";
            }
        }
    }
    /* END FCM*/
}
