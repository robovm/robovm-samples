/*
 * Copyright (C) 2014 RoboVM AB
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
 * Portions of this code is based on Apple Inc's StoreKitSuite sample (v1.0)
 * which is copyright (C) 2014 Apple Inc.
 */

package org.robovm.samples.inapppurchases;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.storekit.SKPaymentQueue;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UINavigationController;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIWindow;
import org.robovm.samples.inapppurchases.viewcontrollers.ParentViewController;

public class InAppPurchases extends UIApplicationDelegateAdapter {
    private UIWindow window;
    private UINavigationController rootNavigationController;
    private ParentViewController parentViewController;

    @Override
    public boolean didFinishLaunching (UIApplication application, UIApplicationLaunchOptions launchOptions) {
        // Set up the view controller.
        parentViewController = new ParentViewController();
        rootNavigationController = new UINavigationController(parentViewController);

        // Create a new window at screen size.
        window = new UIWindow(UIScreen.getMainScreen().getBounds());
        // Set our viewcontroller as the root controller for the window.
        window.setRootViewController(rootNavigationController);
        // Make the window visible.
        window.makeKeyAndVisible();

        // Attach an observer to the payment queue
        SKPaymentQueue.getDefaultQueue().addTransactionObserver(StoreObserver.getInstance());

        /*
         * Retains the window object until the application is deallocated. Prevents Java GC from collecting the window object too
         * early.
         */
        addStrongRef(window);

        return true;
    }

    @Override
    public void willTerminate (UIApplication application) {
        // Remove the observer
        SKPaymentQueue.getDefaultQueue().removeTransactionObserver(StoreObserver.getInstance());
    }

    public static void main (String[] args) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(args, null, InAppPurchases.class);
        pool.close();
    }
}
