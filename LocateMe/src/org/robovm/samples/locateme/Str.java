/*
 * Copyright (C) 2014 Trillian Mobile AB
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
 * Portions of this code is based on Apple Inc's LocateMe sample (v2.2)
 * which is copyright (C) 2008-2010 Apple Inc.
 */

package org.robovm.samples.locateme;

import org.robovm.apple.corelocation.CLLocation;
import org.robovm.apple.foundation.NSString;

public class Str {
    public static String getLocalizedString (String key) {
        return NSString.getLocalizedString(key);
    }

    public static String getLocalizedCoordinateString (CLLocation location) {
        if (location.getHorizontalAccuracy() < 0) {
            return getLocalizedString("DataUnavailable");
        }
        String latString = (location.getCoordinate().latitude() < 0) ? getLocalizedString("South") : getLocalizedString("North");
        String lonString = (location.getCoordinate().longitude() < 0) ? getLocalizedString("West") : getLocalizedString("East");
        return String.format("%.4f° %s, %.4f° %s", Math.abs(location.getCoordinate().latitude()), latString,
            Math.abs(location.getCoordinate().longitude()), lonString);
    }

    public static String getLocalizedAltitudeString (CLLocation location) {
        if (location.getVerticalAccuracy() < 0) {
            return getLocalizedString("DataUnavailable");
        }
        String seaLevelString = (location.getAltitude() < 0) ? getLocalizedString("BelowSeaLevel")
            : getLocalizedString("AboveSeaLevel");
        return String.format("%.2f meters %s", Math.abs(location.getAltitude()), seaLevelString);
    }

    public static String getLocalizedHorizontalAccuracyString (CLLocation location) {
        if (location.getHorizontalAccuracy() < 0) {
            return getLocalizedString("DataUnavailable");
        }
        return String.format("%.2f meters", location.getHorizontalAccuracy());
    }

    public static String getLocalizedVerticalAccuracyString (CLLocation location) {
        if (location.getVerticalAccuracy() < 0) {
            return getLocalizedString("DataUnavailable");
        }
        return String.format("%.2f meters", location.getVerticalAccuracy());
    }

    public static String getLocalizedCourseString (CLLocation location) {
        if (location.getCourse() < 0) {
            return getLocalizedString("DataUnavailable");
        }
        return String.format("%.4f° Clockwise from North", location.getCourse());
    }

    public static String getLocalizedSpeedString (CLLocation location) {
        if (location.getSpeed() < 0) {
            return getLocalizedString("DataUnavailable");
        }
        return String.format("%.2f meters per seconds", location.getSpeed());
    }
}
