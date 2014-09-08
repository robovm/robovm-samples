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
 * Portions of this code is based on Apple Inc's MoviePlayer sample (v1.5)
 * which is copyright (C) 2008-2014 Apple Inc.
 */

package org.robovm.samples.movieplayer;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UITabBarController;
import org.robovm.apple.uikit.UIWindow;
import org.robovm.samples.movieplayer.viewcontrollers.MyLocalMovieViewController;
import org.robovm.samples.movieplayer.viewcontrollers.MyMovieViewController;
import org.robovm.samples.movieplayer.viewcontrollers.MyStreamingMovieViewController;

public class MoviePlayer extends UIApplicationDelegateAdapter {
    private UIWindow window;
    private UITabBarController tabBarController;

    @Override
    public boolean didFinishLaunching (UIApplication application, UIApplicationLaunchOptions launchOptions) {
        window = new UIWindow(UIScreen.getMainScreen().getBounds());
        window.setBackgroundColor(UIColor.white());
        /* Setup a tab bar controller that handles our custom view controllers */
        tabBarController = new UITabBarController();
        tabBarController.addChildViewController(new MyStreamingMovieViewController());
        tabBarController.addChildViewController(new MyLocalMovieViewController());

        /* Set the tab bar controller as the root of our window. */
        window.setRootViewController(tabBarController);
        window.makeKeyAndVisible();

        /*
         * Retains the window object until the application is deallocated. Prevents Java GC from collecting the window object too
         * early.
         */
        application.addStrongRef(window);

        return true;
    }

    @Override
    public void willEnterForeground (UIApplication application) {
        MyMovieViewController selectedViewController = (MyMovieViewController)tabBarController.getSelectedViewController();
        selectedViewController.willEnterForeground();
    }

    public static void main (String[] args) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(args, null, MoviePlayer.class);
        pool.close();
    }
}
