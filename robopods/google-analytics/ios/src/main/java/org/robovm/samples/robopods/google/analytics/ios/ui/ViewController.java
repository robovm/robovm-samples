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

import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.pods.google.analytics.GAI;
import org.robovm.pods.google.analytics.GAIDictionaryBuilder;
import org.robovm.pods.google.analytics.GAIFields;
import org.robovm.pods.google.analytics.GAITracker;
import org.robovm.samples.robopods.google.analytics.ios.GoogleAnalyticsApp;

@CustomClass("ViewController")
public class ViewController extends UIViewController {
    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);

        String name = String.format("Pattern~%s", getTitle());

        GAITracker tracker = GAI.getSharedInstance().getTracker(GoogleAnalyticsApp.TRACKER_ID);
        tracker.put(GAIFields.ScreenName(), name);
        tracker.send(GAIDictionaryBuilder.createScreenView().build());
    }
}
