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
 * Portions of this code is based on Apple Inc's TheElements sample (v1.12)
 * which is copyright (C) 2008-2013 Apple Inc.
 */

package org.robovm.samples.theelements;

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.foundation.NSMutableArray;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UINavigationController;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UITabBarController;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.apple.uikit.UIWindow;
import org.robovm.samples.theelements.datasource.ElementsSortedByAtomicNumberDataSource;
import org.robovm.samples.theelements.datasource.ElementsSortedByNameDataSource;
import org.robovm.samples.theelements.datasource.ElementsSortedByStateDataSource;
import org.robovm.samples.theelements.datasource.ElementsSortedBySymbolDataSource;
import org.robovm.samples.theelements.viewcontrollers.ElementsTableViewController;

public class TheElements extends UIApplicationDelegateAdapter {
    private UIWindow window;
    private UITabBarController tabBarController;
    private ElementsTableViewController nameController;
    private ElementsTableViewController numberController;
    private ElementsTableViewController symbolController;
    private ElementsTableViewController stateController;

    @Override
    public void didFinishLaunching (UIApplication application) {
        // Set up the view controller.
        tabBarController = new UITabBarController();

        // For each tableview 'screen' we need to create a datasource instance
        // (the class that is passed in) we then need to create an instance of
        // ElementsTableViewController with that datasource instance finally we need to return
        // a UINaviationController for each screen, with the ElementsTableViewController as the
        // root view controller.

        NSArray<UIViewController> viewControllers = new NSMutableArray<>();

        // by name
        UINavigationController navController = new UINavigationController();
        nameController = new ElementsTableViewController();
        navController.addChildViewController(nameController);
        nameController.setDataSource(new ElementsSortedByNameDataSource());
        viewControllers.add(navController);

        // by atomic number
        navController = new UINavigationController();
        numberController = new ElementsTableViewController();
        navController.addChildViewController(numberController);
        numberController.setDataSource(new ElementsSortedByAtomicNumberDataSource());
        viewControllers.add(navController);

        // by symbol
        navController = new UINavigationController();
        symbolController = new ElementsTableViewController();
        navController.addChildViewController(symbolController);
        symbolController.setDataSource(new ElementsSortedBySymbolDataSource());
        viewControllers.add(navController);

        // by state
        navController = new UINavigationController();
        stateController = new ElementsTableViewController();
        navController.addChildViewController(stateController);
        stateController.setDataSource(new ElementsSortedByStateDataSource());
        viewControllers.add(navController);

        tabBarController.setViewControllers(viewControllers);

        /*
         * Retain all of our custom view controllers. These contain the data sources, which should not be released until the
         * application gets deallocated.
         */
        addStrongRef(nameController);
        addStrongRef(numberController);
        addStrongRef(symbolController);
        addStrongRef(stateController);

        // Create a new window at screen size.
        window = new UIWindow(UIScreen.getMainScreen().getBounds());
        // Set our viewcontroller as the root controller for the window.
        window.setRootViewController(tabBarController);
        // Make the window visible.
        window.makeKeyAndVisible();

        /*
         * Retains the window object until the application is deallocated. Prevents Java GC from collecting the window object too
         * early.
         */
        addStrongRef(window);
    }

    public static void main (String[] args) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(args, null, TheElements.class);
        pool.close();
    }
}
