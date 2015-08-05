/*
 * Copyright (C) 2013-2015 RoboVM AB
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 * 
 * Portions of this code is based on Parse's AnyPic sample
 * which is copyright (C) 2013 Parse.
 */
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
