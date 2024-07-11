package io.wedonttrack.utils;

import okhttp3.MediaType;

/**
 * The Constants class holds constant values that are used throughout the application.
 * These include a URL for the environment, a JSON media type for HTTP requests, and a range of numbers.
 */
public interface Constants {
    public static final String ENV_URL = "env_url";
    public static final String NEW_MAPPING_BASEPATH = "/__admin/mappings/new";
    public static final String GET_MAPPINGS_BASEPATH = "__admin/mappings";
    public static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");

    public static final long MAX_NUM = 497387909;
    public static final long MIN_NUM = 497387901;
}
