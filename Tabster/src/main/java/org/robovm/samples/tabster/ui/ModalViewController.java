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
 * Portions of this code is based on Apple Inc's PhotoPicker sample (v2.0)
 * which is copyright (C) 2010-2013 Apple Inc.
 */
package org.robovm.samples.tabster.ui;

import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;

@CustomClass("ModalViewController")
public class ModalViewController extends UIViewController {
    private SubLevelViewController owningViewController;
    @IBOutlet
    private UILabel titleLabel;

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);
        titleLabel.setText(owningViewController.getCurrentSelectionTitle());
    }

    public void setOwningViewController(SubLevelViewController owningViewController) {
        this.owningViewController = owningViewController;
    }
}
