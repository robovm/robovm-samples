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
 * Portions of this code is based on Apple Inc's Tabster sample (v1.6)
 * which is copyright (C) 2011-2014 Apple Inc.
 */

package org.robovm.samples.tabster;

import java.util.ArrayList;
import java.util.List;

import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.foundation.NSCoder;
import org.robovm.apple.foundation.NSMutableArray;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.foundation.NSUserDefaults;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UINavigationController;
import org.robovm.apple.uikit.UINavigationControllerDelegateAdapter;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UITabBarController;
import org.robovm.apple.uikit.UITabBarItem;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.apple.uikit.UIWindow;
import org.robovm.samples.tabster.ui.FavoritesViewController;
import org.robovm.samples.tabster.ui.FeaturedViewController;
import org.robovm.samples.tabster.ui.FourViewController;
import org.robovm.samples.tabster.ui.OneViewController;
import org.robovm.samples.tabster.ui.ThreeViewController;
import org.robovm.samples.tabster.ui.TwoViewController;

public class Tabster extends UIApplicationDelegateAdapter {
    private static final boolean CUSTOMIZE_TAB_BAR = false; // Turn on/off
                                                            // custom tab bar
                                                            // appearance

    // NSUserDefaults keys:
    private static final String TAB_BAR_ORDER_PREF_KEY = "TabBarOrder"; // The
                                                                        // ordering
                                                                        // of
                                                                        // the
                                                                        // tabs

    private UIWindow window;
    private UITabBarController tabBarController;

    private UINavigationController oneNavController;
    private OneViewController oneViewController;
    private UINavigationController twoNavController;
    private TwoViewController twoViewController;
    private UINavigationController threeNavController;
    private ThreeViewController threeViewController;
    private FourViewController fourViewController;
    private FavoritesViewController favoritesViewController;
    private FeaturedViewController featuredViewController;

    @SuppressWarnings("unchecked")
    @Override
    public boolean didFinishLaunching(UIApplication application, UIApplicationLaunchOptions launchOptions) {
        // Set up the view controller.
        tabBarController = new UITabBarController();

        // customize the More page's navigation bar color
        tabBarController.getMoreNavigationController().getNavigationBar().setTintColor(UIColor.gray());

        // Adding view controllers.
        NSArray<UIViewController> viewControllers = new NSMutableArray<UIViewController>();

        oneViewController = new OneViewController();
        oneNavController = new UINavigationController(oneViewController);
        oneNavController.setTabBarItem(new UITabBarItem("One", UIImage.getImage("tab1"), 0));
        viewControllers.add(oneNavController);

        twoViewController = new TwoViewController();
        twoNavController = new UINavigationController(twoViewController);
        twoNavController.setTabBarItem(new UITabBarItem("Two", UIImage.getImage("tab2"), 0));
        viewControllers.add(twoNavController);

        threeViewController = new ThreeViewController();
        threeNavController = new UINavigationController(threeViewController);
        threeNavController.setTabBarItem(new UITabBarItem("Three", UIImage.getImage("tab3"), 0));
        viewControllers.add(threeNavController);

        fourViewController = new FourViewController();
        viewControllers.add(fourViewController);

        favoritesViewController = new FavoritesViewController();
        viewControllers.add(favoritesViewController);

        featuredViewController = new FeaturedViewController();
        viewControllers.add(featuredViewController);

        tabBarController.setViewControllers(viewControllers);

        if (CUSTOMIZE_TAB_BAR) {
            // set the bar tint color for iOS 7 and later
            if (Foundation.getMajorSystemVersion() >= 7) {
                tabBarController.getTabBar().setBarTintColor(UIColor.darkGray());
            } else {
                // set the bar tint color for iOS 6 and earlier
                tabBarController.getTabBar().setTintColor(UIColor.darkGray());
            }

            tabBarController.getTabBar().setSelectedImageTintColor(UIColor.yellow());

            // note:
            // 1) you can also apply additional custom appearance to UITabBar
            // using:
            // "backgroundImage" and "selectionIndicatorImage"
            // 2) you can also customize the appearance of individual
            // UITabBarItems as well.
        }

        // restore the tab-order from prefs
        NSArray<NSString> arr = (NSArray<NSString>) NSUserDefaults.getStandardUserDefaults().getArray(
                TAB_BAR_ORDER_PREF_KEY);

        if (arr != null && arr.size() > 0) {
            List<String> classNames = arr.asStringList();
            NSArray<UIViewController> controllers = new NSMutableArray<>();
            for (String className : classNames) {
                for (UIViewController controller : tabBarController.getViewControllers()) {
                    String controllerClassName = null;

                    if (controller instanceof UINavigationController) {
                        controllerClassName = ((UINavigationController) controller).getTopViewController().getClass()
                                .getName();
                    } else {
                        controllerClassName = controller.getClass().getName();
                    }

                    if (className.equals(controllerClassName)) {
                        controllers.add(controller);
                        break;
                    }
                }
            }

            if (controllers.size() == tabBarController.getViewControllers().size()) {
                tabBarController.setViewControllers(controllers);
            }
        }

        // listen for changes in view controller from the More screen
        tabBarController.getMoreNavigationController().setDelegate(new UINavigationControllerDelegateAdapter() {
            @Override
            public void willShowViewController(UINavigationController navigationController,
                    UIViewController viewController,
                    boolean animated) {
                if (viewController == tabBarController.getMoreNavigationController().getViewControllers().first()) {
                    // returned to the More page
                }
            }
        });

        // choose to make one of our view controllers
        // ("FeaturedViewController"),
        // not movable/reorderable in More's edit screen
        NSArray<UIViewController> customizeableViewControllers;
        if (tabBarController.getViewControllers() == null) {
            customizeableViewControllers = new NSMutableArray<>();
        } else {
            customizeableViewControllers = new NSMutableArray<>(tabBarController.getViewControllers());
        }
        for (UIViewController viewController : customizeableViewControllers) {
            if (viewController instanceof FeaturedViewController) {
                customizeableViewControllers.remove(viewController);
                break;
            }
        }
        tabBarController.setCustomizableViewControllers(customizeableViewControllers);

        // Create a new window at screen size.
        window = new UIWindow(UIScreen.getMainScreen().getBounds());
        // Set our viewcontroller as the root controller for the window.
        window.setRootViewController(tabBarController);
        // Make the window visible.
        window.makeKeyAndVisible();

        return true;
    }

    @Override
    public void didEnterBackground(UIApplication application) {
        // this will store tab ordering.
        saveTabOrder();
    }

    /** Store the tab-order to preferences */
    private void saveTabOrder() {
        List<String> classNames = new ArrayList<String>();
        for (UIViewController controller : tabBarController.getViewControllers()) {
            if (controller instanceof UINavigationController) {
                UINavigationController navController = (UINavigationController) controller;
                classNames.add(navController.getTopViewController().getClass().getName());
            } else {
                classNames.add(controller.getClass().getName());
            }
        }

        NSUserDefaults.getStandardUserDefaults().put(TAB_BAR_ORDER_PREF_KEY, NSArray.fromStrings(classNames));
    }

    @Override
    public boolean shouldRestoreApplicationState(UIApplication application, NSCoder coder) {
        return true;
    }

    @Override
    public boolean shouldSaveApplicationState(UIApplication application, NSCoder coder) {
        return true;
    }

    public static void main(String[] args) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(args, null, Tabster.class);
        pool.close();
    }
}
