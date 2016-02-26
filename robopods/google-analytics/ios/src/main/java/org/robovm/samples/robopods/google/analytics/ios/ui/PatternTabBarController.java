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
 * Portions of this code is based on Google Inc's Google Analytics sample
 * which is copyright (C) 2015 Google Inc.
 */

package org.robovm.samples.robopods.google.analytics.ios.ui;

import org.robovm.apple.foundation.NSMutableDictionary;
import org.robovm.apple.uikit.UIAlertView;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UITabBarController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import org.robovm.pods.google.analytics.GAI;
import org.robovm.pods.google.analytics.GAIDictionaryBuilder;
import org.robovm.pods.google.analytics.GAITracker;
import org.robovm.samples.robopods.google.analytics.ios.GoogleAnalyticsApp;

/**
 * PatternTabBarController exists as a subclass of UITabBarConttroller that
 * supports a 'share' action. This will trigger a custom event to Analytics and
 * display a dialog.
 */
@CustomClass("PatternTabBarController")
public class PatternTabBarController extends UITabBarController {

    @IBAction
    private void didTapShare(UIBarButtonItem sender) {
        GAITracker tracker = GAI.getSharedInstance().getTracker(GoogleAnalyticsApp.TRACKER_ID);
        NSMutableDictionary<?, ?> event = GAIDictionaryBuilder.createEvent("Action", "Share", null, null).build();
        tracker.send(event);

        String title = String.format("Share: %s", getSelectedViewController().getTitle());
        String message = "Share is not implemented in this quickstart";
        new UIAlertView(title, message, null, "OK").show();
    }
}
