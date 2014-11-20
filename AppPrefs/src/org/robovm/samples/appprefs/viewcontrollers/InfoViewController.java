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
 * Portions of this code is based on Apple Inc's AppPrefs sample (v5.1)
 * which is copyright (C) 2008-2014 Apple Inc.
 */

package org.robovm.samples.appprefs.viewcontrollers;

import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonSystemItem;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIModalTransitionStyle;
import org.robovm.apple.uikit.UITextView;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;

public class InfoViewController extends UIViewController {

    public InfoViewController () {
        setTitle("AppPrefs");
        setModalTransitionStyle(UIModalTransitionStyle.FlipHorizontal);

        UIView view = getView();
        view.setBackgroundColor(UIColor.white());

        UITextView textView = new UITextView(view.getBounds());
        textView
            .setText("This sample demonstrates how to display your app's preferences or settings in the \"Settings\" system application.\n\n"
                + "A settings bundle, included in the application’s bundle, contains the information needed by the Settings application to display your preferences and make it possible for the user to modify them. It then saves any configured values in the defaults database so that your application can retrieve them at runtime.\n\n"
                + "Open the \"Settings\" application to change these preference values.");
        textView.setFont(UIFont.getSystemFont(16));
        view.addSubview(textView);

        getNavigationItem().setLeftBarButtonItem(
            new UIBarButtonItem(UIBarButtonSystemItem.Done, new UIBarButtonItem.OnClickListener() {
                @Override
                public void onClick (UIBarButtonItem barButtonItem) {
                    dismissViewController(true, null);
                }
            }));
    }
}
