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
 * Portions of this code is based on Apple Inc's PhotoScroller sample (v1.3)
 * which is copyright (C) 2010-2012 Apple Inc.
 */

package org.robovm.samples.photoscroller;

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UIPageViewController;
import org.robovm.apple.uikit.UIPageViewControllerDataSourceAdapter;
import org.robovm.apple.uikit.UIPageViewControllerNavigationDirection;
import org.robovm.apple.uikit.UIPageViewControllerNavigationOrientation;
import org.robovm.apple.uikit.UIPageViewControllerOptions;
import org.robovm.apple.uikit.UIPageViewControllerTransitionStyle;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.apple.uikit.UIWindow;
import org.robovm.samples.photoscroller.viewcontrollers.PhotoViewController;

public class PhotoScroller extends UIApplicationDelegateAdapter {
    private UIWindow window;
    private UIPageViewController pageViewController;

    @Override
    public boolean didFinishLaunching (UIApplication application, UIApplicationLaunchOptions launchOptions) {
        // Set up the view controller.
        pageViewController = new UIPageViewController(UIPageViewControllerTransitionStyle.Scroll,
            UIPageViewControllerNavigationOrientation.Horizontal, new UIPageViewControllerOptions().setInterPageSpacing(12));

        // Kick things off by making the first page.
        PhotoViewController pageZero = PhotoViewController.create(0);
        if (pageZero != null) {
            pageViewController.setDataSource(new UIPageViewControllerDataSourceAdapter() {
                @Override
                public UIViewController getViewControllerBefore (UIPageViewController pageViewController,
                    UIViewController viewController) {
                    PhotoViewController vc = (PhotoViewController)viewController;
                    return PhotoViewController.create(vc.getPageIndex() - 1);
                }

                @Override
                public UIViewController getViewControllerAfter (UIPageViewController pageViewController,
                    UIViewController viewController) {
                    PhotoViewController vc = (PhotoViewController)viewController;
                    return PhotoViewController.create(vc.getPageIndex() + 1);
                }
            });
            // Assign the first page to the pageViewController
            pageViewController.setViewControllers(new NSArray<>((UIViewController)pageZero),
                UIPageViewControllerNavigationDirection.Forward, false, null);
        }

        // Create a new window at screen size.
        window = new UIWindow(UIScreen.getMainScreen().getBounds());
        // Set our viewcontroller as the root controller for the window.
        window.setRootViewController(pageViewController);
        // Make the window visible.
        window.makeKeyAndVisible();

        /*
         * Retains the window object until the application is deallocated. Prevents Java GC from collecting the window object too
         * early.
         */
        addStrongRef(window);

        return true;
    }

    public static void main (String[] args) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(args, null, PhotoScroller.class);
        pool.close();
    }
}
