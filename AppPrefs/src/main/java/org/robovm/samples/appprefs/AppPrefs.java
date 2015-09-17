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
 * Portions of this code is based on Apple Inc's AppPrefs sample (v5.1)
 * which is copyright (C) 2008 - 2014 Apple Inc.
 */
package org.robovm.samples.appprefs;

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSMutableDictionary;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.foundation.NSUserDefaults;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;

public class AppPrefs extends UIApplicationDelegateAdapter {

    @Override
    public boolean didFinishLaunching(UIApplication application, UIApplicationLaunchOptions launchOptions) {
        /*
         * The registration domain is volatile. It does not persist across
         * launches. You must register your defaults at each launch; otherwise
         * you will get (system) default values when accessing the values of
         * preferences the user (via the Settings app) or your app has not
         * modified. Registering a set of default values ensures that your app
         * always has a known good set of values to operate on.
         */
        populateRegistrationDomain();

        return true;
    }

    /**
     * Locates the file representing the root page of the settings for this app,
     * invokes loadDefaults:fromSettingsPage:inSettingsBundleAtURL: on it, and
     * registers the loaded values as the app's defaults.
     */
    private void populateRegistrationDomain() {
        NSURL settingsBundleURL = NSBundle.getMainBundle().findResourceURL("Settings", "bundle");

        /*
         * Invoke loadDefaults() on the property list file for the root settings
         * page (always named Root.plist).
         */
        NSDictionary<NSString, ?> appDefaults = loadDefaults("Root.plist", settingsBundleURL);

        /*
         * appDefaults is now populated with the preferences and their default
         * values. Add these to the registration domain.
         */
        NSUserDefaults.getStandardUserDefaults().registerDefaults(appDefaults);
        NSUserDefaults.getStandardUserDefaults().synchronize();
    }

    /**
     * Helper function that parses a Settings page file, extracts each
     * preference defined within along with its default value. If the page
     * contains a 'Child Pane Element', this method will recurs on the
     * referenced page file.
     * 
     * @param plistName
     * @param settingsBundleURL
     * @return
     */
    @SuppressWarnings("unchecked")
    private NSDictionary<NSString, ?> loadDefaults(String plistName, NSURL settingsBundleURL) {
        /*
         * Each page of settings is represented by a property-list file that
         * follows the Settings Application Schema:
         * <https://developer.apple.com/
         * library/ios/#documentation/PreferenceSettings
         * /Conceptual/SettingsApplicationSchemaReference
         * /Introduction/Introduction.html>.
         */

        // Create an NSDictionary from the plist file.
        NSDictionary<NSString, NSObject> settingsDict = (NSDictionary<NSString, NSObject>) NSDictionary
                .read(settingsBundleURL
                        .newURLByAppendingPathComponent(plistName));

        // The elements defined in a settings page are contained within an array
        // that is associated with the root-level PreferenceSpecifiers key.
        NSArray<NSDictionary<NSString, NSObject>> prefSpecifierArray = (NSArray<NSDictionary<NSString, NSObject>>) settingsDict
                .get(new NSString("PreferenceSpecifiers"));

        // If prefSpecifierArray is nil, something wen't wrong. Either the
        // specified plist does not exist or is malformed.
        if (prefSpecifierArray == null)
            return null;

        // Create a dictionary to hold the parsed results.
        NSMutableDictionary<NSString, NSObject> keyValuePairs = new NSMutableDictionary<>();

        for (NSDictionary<NSString, NSObject> prefItem : prefSpecifierArray) {
            // Each element is itself a dictionary.

            // What kind of control is used to represent the preference element
            // in the
            // Settings app.
            NSString prefItemType = (NSString) prefItem.get(new NSString("Type"));
            // How this preference element maps to the defaults database for the
            // app.
            NSString prefItemKey = (NSString) prefItem.get(new NSString("Key"));
            // The default value for the preference key.
            NSObject prefItemDefaultValue = prefItem.get(new NSString("DefaultValue"));

            // If this is a 'Child Pane Element'. That is, a reference to
            // another
            // page.
            if (prefItemType.equals(new NSString("PSChildPaneSpecifier"))) {
                // There must be a value associated with the 'File' key in this
                // preference
                // element's dictionary. Its value is the name of the plist file
                // in the
                // Settings bundle for the referenced page.

                NSString prefItemFile = (NSString) prefItem.get(new NSString("File"));

                // Recurs on the referenced page.
                NSDictionary<NSString, NSObject> childPageKeyValuePairs = (NSDictionary<NSString, NSObject>) loadDefaults(
                        prefItemFile.toString(), settingsBundleURL);

                // Add the results to our dictionary
                keyValuePairs.putAll(childPageKeyValuePairs);
            } else if (prefItemKey != null && prefItemDefaultValue != null) {
                // Some elements, such as 'Group' or 'Text Field' elements do
                // not contain
                // a key and default value. Skip those.
                keyValuePairs.put(prefItemKey, prefItemDefaultValue);
            }
        }
        return keyValuePairs;
    }

    public static void main(String[] args) {
        try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
            UIApplication.main(args, null, AppPrefs.class);
        }
    }
}
