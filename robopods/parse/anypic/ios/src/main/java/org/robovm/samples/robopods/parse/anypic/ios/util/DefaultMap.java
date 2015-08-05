package org.robovm.samples.robopods.parse.anypic.ios.util;

import java.util.HashMap;

public class DefaultMap extends HashMap<String, Object> {
    private static final long serialVersionUID = -3169042514056781309L;

    public Object get(String key, Object defaultValue) {
        if (containsKey(key)) {
            return get(key);
        }
        return defaultValue;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        if (containsKey(key)) {
            return (boolean) get(key);
        }
        return defaultValue;
    }

    public int getInt(String key, int defaultValue) {
        if (containsKey(key)) {
            return (int) get(key);
        }
        return defaultValue;
    }

    public long getLong(String key, long defaultValue) {
        if (containsKey(key)) {
            return (long) get(key);
        }
        return defaultValue;
    }

    public float getFloat(String key, float defaultValue) {
        if (containsKey(key)) {
            return (float) get(key);
        }
        return defaultValue;
    }

    public double getDouble(String key, double defaultValue) {
        if (containsKey(key)) {
            return (double) get(key);
        }
        return defaultValue;
    }
}
