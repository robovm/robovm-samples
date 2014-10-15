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
 * Portions of this code is based on Apple Inc's MoveMe sample (v3.0)
 * which is copyright (C) 2008-2013 Apple Inc.
 */

package org.robovm.samples.moveme.viewcontrollers;

import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.foundation.NSStringEncoding;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.samples.moveme.views.APLMoveMeView;

public class APLViewController extends UIViewController {
    private APLMoveMeView moveMeView;

    @Override
    public void viewDidLoad () {
        super.viewDidLoad();

        moveMeView = new APLMoveMeView();

        // Load the display strings.
        String resourcePath = NSBundle.getMainBundle().findResourcePath("DisplayStrings", "txt");
        String string = NSString.readFile(resourcePath, NSStringEncoding.UTF16BigEndian);

        if (string != null) {
            String[] displayStrings = string.split("\\r?\\n");
            moveMeView.setDisplayStrings(displayStrings);
            moveMeView.setupNextDisplayString();
        }

        setView(moveMeView);
    }
}
