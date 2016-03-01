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
 * Portions of this code is based on Apple Inc's UICatalog sample (v11.3)
 * which is copyright (C) 2008-2015 Apple Inc.
 */
package org.robovm.samples.uicatalog.viewcontrollers;

import org.robovm.apple.uikit.UIActivityIndicatorView;
import org.robovm.apple.uikit.UIActivityIndicatorViewStyle;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;
import org.robovm.samples.uicatalog.Colors;

@CustomClass("AAPLActivityIndicatorViewController")
public class AAPLActivityIndicatorViewController extends UITableViewController {
    @IBOutlet
    private UIActivityIndicatorView grayStyleActivityIndicatorView;
    @IBOutlet
    private UIActivityIndicatorView tintedActivityIndicatorView;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        configureGrayActivityIndicatorView();
        configureTintedActivityIndicatorView();

        // When activity is done, use UIActivityIndicatorView.stopAnimating().
    }

    private void configureGrayActivityIndicatorView() {
        grayStyleActivityIndicatorView.setActivityIndicatorViewStyle(UIActivityIndicatorViewStyle.Gray);
        grayStyleActivityIndicatorView.startAnimating();
        grayStyleActivityIndicatorView.setHidesWhenStopped(true);
    }

    private void configureTintedActivityIndicatorView() {
        tintedActivityIndicatorView.setActivityIndicatorViewStyle(UIActivityIndicatorViewStyle.Gray);
        tintedActivityIndicatorView.setColor(Colors.PURPLE);
        tintedActivityIndicatorView.startAnimating();
    }
}
