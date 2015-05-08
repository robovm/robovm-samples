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
 * Portions of this code is based on Apple Inc's TheElements sample (v1.12)
 * which is copyright (C) 2008-2013 Apple Inc.
 */
package org.robovm.samples.theelements;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.foundation.NSMutableArray;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UINavigationController;
import org.robovm.apple.uikit.UIStoryboard;
import org.robovm.apple.uikit.UITabBarController;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.samples.theelements.datasource.ElementsDataSource;
import org.robovm.samples.theelements.datasource.ElementsSortedByAtomicNumberDataSource;
import org.robovm.samples.theelements.datasource.ElementsSortedByNameDataSource;
import org.robovm.samples.theelements.datasource.ElementsSortedByStateDataSource;
import org.robovm.samples.theelements.datasource.ElementsSortedBySymbolDataSource;
import org.robovm.samples.theelements.ui.ElementsTableViewController;

public class TheElements extends UIApplicationDelegateAdapter {

    @Override
    public boolean didFinishLaunching(UIApplication application, UIApplicationLaunchOptions launchOptions) {
        // for each tableview 'screen' we need to create a datasource instance
        // (the class that is passed in) we then need to create an instance of
        // ElementsTableViewController with that datasource instance finally we
        // need to return a UINaviationController for each screen, with the
        // ElementsTableViewController as the root view controller.

        UITabBarController tabBarController = (UITabBarController) getWindow().getRootViewController();

        ElementsDataSource dataSource;

        UIStoryboard storyboard = UIStoryboard.create("Main", null);

        NSMutableArray<UIViewController> viewControllers = new NSMutableArray<>(4);

        // create our tabbar view controllers, since we already have one defined
        // in our storyboard we will create 3 more instances of it, and assign
        // each it's own kind data source

        // by name
        UINavigationController navController = (UINavigationController) storyboard
                .instantiateViewController("navForTableView");
        ElementsTableViewController viewController = (ElementsTableViewController) navController.getTopViewController();
        dataSource = new ElementsSortedByNameDataSource();
        viewController.setDataSource(dataSource);
        viewControllers.add(navController);

        // by atomic number
        navController = (UINavigationController) storyboard.instantiateViewController("navForTableView");
        viewController = (ElementsTableViewController) navController.getTopViewController();
        dataSource = new ElementsSortedByAtomicNumberDataSource();
        viewController.setDataSource(dataSource);
        viewControllers.add(navController);

        // by symbol
        navController = (UINavigationController) storyboard.instantiateViewController("navForTableView");
        viewController = (ElementsTableViewController) navController.getTopViewController();
        dataSource = new ElementsSortedBySymbolDataSource();
        viewController.setDataSource(dataSource);
        viewControllers.add(navController);

        // by state
        navController = (UINavigationController) storyboard.instantiateViewController("navForTableView");
        viewController = (ElementsTableViewController) navController.getTopViewController();
        dataSource = new ElementsSortedByStateDataSource();
        viewController.setDataSource(dataSource);
        viewControllers.add(navController);

        tabBarController.setViewControllers(viewControllers);

        return true;
    }

    public static void main(String[] args) {
        try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
            UIApplication.main(args, null, TheElements.class);
        }
    }
}
