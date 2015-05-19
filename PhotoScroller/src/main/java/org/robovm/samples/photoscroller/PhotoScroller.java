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
import org.robovm.apple.uikit.UIViewController;
import org.robovm.samples.photoscroller.ui.PhotoViewController;

public class PhotoScroller extends UIApplicationDelegateAdapter {

    @Override
    public boolean didFinishLaunching(UIApplication application, UIApplicationLaunchOptions launchOptions) {
        // kick things off by making the first page
        PhotoViewController pageZero = PhotoViewController.create(0);
        if (pageZero != null) {
            // assign the first page to the pageViewController (our
            // rootViewController)
            UIPageViewController pageViewController = (UIPageViewController) getWindow().getRootViewController();
            pageViewController.setDataSource(new UIPageViewControllerDataSourceAdapter() {
                @Override
                public UIViewController getViewControllerBefore(UIPageViewController pageViewController,
                        UIViewController viewController) {
                    int index = ((PhotoViewController) viewController).getPageIndex();
                    return PhotoViewController.create(index - 1);
                }

                @Override
                public UIViewController getViewControllerAfter(UIPageViewController pageViewController,
                        UIViewController viewController) {
                    int index = ((PhotoViewController) viewController).getPageIndex();
                    return PhotoViewController.create(index + 1);
                }
            });

            pageViewController.setViewControllers(new NSArray<UIViewController>(pageZero),
                    UIPageViewControllerNavigationDirection.Forward, false, null);
        }

        return true;
    }

    public static void main(String[] args) {
        try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
            UIApplication.main(args, null, PhotoScroller.class);
        }
    }

}
