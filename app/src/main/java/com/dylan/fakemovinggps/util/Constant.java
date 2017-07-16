package com.dylan.fakemovinggps.util;

public class Constant {

    public static final String ACTION_PREFIX = "com.dylan.fakemovinggps";

    public interface Action {
        String ACTION_START_FAKING_LOCATION = ACTION_PREFIX + ".ACTION_START_FAKING_LOCATION";
        String ACTION_STOP_FAKING_LOCATION = ACTION_PREFIX + ".ACTION_STOP_FAKING_LOCATION";

    }

    public interface Callback {
        String REQUEST_ALLOW_MOCK_LOCATIONS_APPS = ACTION_PREFIX + ".REQUEST_ALLOW_MOCK_LOCATIONS_APPS";

        String START_FAKING_LOCATION_SUCCESSFULLY = ACTION_PREFIX + ".START_FAKING_LOCATION_SUCCESSFULLY";
        String LOCATION_UPDATE = ACTION_PREFIX + ".LOCATION_UPDATE";
    }

    public static final int ID_SERVICE_NOTIFICATION = 13;
}
