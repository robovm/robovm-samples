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
 * Portions of this code is based on Apple Inc's TableSearch sample (v1.0)
 * which is copyright (C) 2014 Apple Inc.
 */

package org.robovm.samples.tablesearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.robovm.apple.foundation.NSNumber;
import org.robovm.apple.foundation.NSString;

public class APLProduct {
    private final String title;
    private final String hardwareType;
    private final int yearIntroduced;
    private final NSNumber introPrice;

    private static List<String> deviceTypeNames;
    private static Map<String, String> deviceTypeDisplayNames;

    public APLProduct (String type, String name, int year, double price) {
        this.hardwareType = type;
        this.title = name;
        this.yearIntroduced = year;
        this.introPrice = NSNumber.valueOf(price);
    }

    public String getTitle () {
        return title;
    }

    public String getHardwareType () {
        return hardwareType;
    }

    public int getYearIntroduced () {
        return yearIntroduced;
    }

    public NSNumber getIntroPrice () {
        return introPrice;
    }

    public static String getDeviceTypeTitle () {
        return NSString.getLocalizedString("Device");
    }

    public static String getDesktopTypeTitle () {
        return NSString.getLocalizedString("Desktop");
    }

    public static String getPortableTypeTitle () {
        return NSString.getLocalizedString("Portable");
    }

    public static List<String> getDeviceTypeNames () {
        if (deviceTypeNames == null) {
            deviceTypeNames = new ArrayList<>();
            deviceTypeNames.add(getDeviceTypeTitle());
            deviceTypeNames.add(getPortableTypeTitle());
            deviceTypeNames.add(getDesktopTypeTitle());
        }
        return deviceTypeNames;
    }

    public static String getDisplayNameForType (String type) {
        if (deviceTypeDisplayNames == null) {
            deviceTypeDisplayNames = new HashMap<>();
            for (String deviceType : getDeviceTypeNames()) {
                String displayName = NSString.getLocalizedString(deviceType);
                deviceTypeDisplayNames.put(deviceType, displayName);
            }
        }
        return deviceTypeDisplayNames.get(type);
    }

}
