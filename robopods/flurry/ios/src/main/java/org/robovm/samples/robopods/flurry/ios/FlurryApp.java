/*
 * Copyright (C) 2016 RoboVM AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robovm.samples.robopods.flurry.ios;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UINavigationController;
import org.robovm.pods.flurry.analytics.Flurry;
import org.robovm.pods.flurry.analytics.FlurryLogLevel;

public class FlurryApp extends UIApplicationDelegateAdapter {
    private static final String APP_KEY = "HXZMM38WG7K7HQFCKRJS";

    @Override
    public boolean didFinishLaunching(UIApplication application, UIApplicationLaunchOptions launchOptions) {
        // Remove the below lines to disable logging for release builds.
        Flurry.setDebugLogEnabled(true);
        Flurry.setLogLevel(FlurryLogLevel.All);

        // Enable crash reporting.
        Flurry.enableCrashReporting();

        // Start Flurry.
        Flurry.startSession(APP_KEY, launchOptions);

        // Automatically log page views for all controllers in the view
        // hierarchy of the root controller.
        UINavigationController navController = (UINavigationController) getWindow().getRootViewController();
        Flurry.logAllPageViews(navController);

        return true;
    }

    public static void main(String[] args) {
        try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
            UIApplication.main(args, null, FlurryApp.class);
        }
    }
}
