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
 * Portions of this code is based on Apple Inc's Regions sample (v1.1)
 * which is copyright (C) 2011 Apple Inc.
 */

package org.robovm.samples.regions;

import org.robovm.apple.corelocation.CLLocationManager;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UINavigationController;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIWindow;
import org.robovm.samples.regions.viewcontrollers.RegionsViewController;

public class Regions extends UIApplicationDelegateAdapter {
    private UIWindow window;
    private UINavigationController navigationController;
    private RegionsViewController rootViewController;

    @Override
    public boolean didFinishLaunching (UIApplication application, UIApplicationLaunchOptions launchOptions) {
        // Set up the view controller.
        rootViewController = new RegionsViewController();

        navigationController = new UINavigationController(rootViewController);

        // Create a new window at screen size.
        window = new UIWindow(UIScreen.getMainScreen().getBounds());
        // Set our viewcontroller as the root controller for the window.
        window.setRootViewController(navigationController);
        // Make the window visible.
        window.makeKeyAndVisible();

        /*
         * Retains the window object until the application is deallocated. Prevents Java GC from collecting the window object too
         * early.
         */
        addStrongRef(window);

        return true;
    }

    @Override
    public void didEnterBackground (UIApplication application) {
        // Reset the icon badge number to zero.
        UIApplication.getSharedApplication().setApplicationIconBadgeNumber(0);

        if (CLLocationManager.isSignificantLocationChangeMonitoringAvailable()) {
            // Stop normal location updates and start significant location change updates for battery efficiency.
            rootViewController.getLocationManager().stopUpdatingLocation();
            rootViewController.getLocationManager().startMonitoringSignificantLocationChanges();
        } else {
            System.out.println("Significant location change monitoring is not available.");
        }
    }

    @Override
    public void didBecomeActive (UIApplication application) {
        if (rootViewController == null) return;
        if (CLLocationManager.isSignificantLocationChangeMonitoringAvailable()) {
            // Stop significant location updates and start normal location updates again since the app is in the forefront.
            rootViewController.getLocationManager().stopMonitoringSignificantLocationChanges();
            rootViewController.getLocationManager().startUpdatingLocation();
        } else {
            System.out.println("Significant location change monitoring is not available.");
        }

        if (!rootViewController.getUpdatesTableView().isHidden()) {
            // Reload the updates table view to reflect update events that were recorded in the background.
            rootViewController.getUpdatesTableView().reloadData();

            // Reset the icon badge number to zero.
            UIApplication.getSharedApplication().setApplicationIconBadgeNumber(0);
        }
    }

    public static void main (String[] args) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(args, null, Regions.class);
        pool.close();
    }
}
