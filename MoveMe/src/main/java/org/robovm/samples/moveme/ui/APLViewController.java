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
 * Portions of this code is based on Apple Inc's MoveMe sample (v3.0)
 * which is copyright (C) 2008-2013 Apple Inc.
 */
package org.robovm.samples.moveme.ui;

import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.foundation.NSErrorException;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.foundation.NSStringEncoding;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;

@CustomClass("APLViewController")
public class APLViewController extends UIViewController {
    @IBOutlet
    private APLMoveMeView moveMeView;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        try {
            // Load the display strings.
            String resourcePath = NSBundle.getMainBundle().findResourcePath("DisplayStrings", "txt");
            String string = NSString.readFile(resourcePath, NSStringEncoding.UTF16BigEndian);

            if (string != null) {
                String[] displayStrings = string.split("\\r?\\n");
                moveMeView.setDisplayStrings(displayStrings);
                moveMeView.setupNextDisplayString();
            }
        } catch (NSErrorException e) {
            throw new Error(e);
        }
    }
}
