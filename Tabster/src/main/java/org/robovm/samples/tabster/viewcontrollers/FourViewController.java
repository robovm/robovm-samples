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
package org.robovm.samples.tabster.viewcontrollers;

import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UINavigationController;
import org.robovm.apple.uikit.UITabBarItem;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;

@CustomClass("FourViewController")
public class FourViewController extends UIViewController {
    private UILabel titleLabel;

    /**
     * this is called when the UITabBarController loads it's views at launch
     * time
     */
    @Override
    public void awakeFromNib() {
        // make our tabbar icon a custom one;
        // we could do it in Interface Builder, but this is just to illustrate a
        // point about using awakeFromNib vs. viewDidLoad.

        UITabBarItem customTab = new UITabBarItem("Four", UIImage.create("tab4.png"), 0);
        setTabBarItem(customTab);
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);

        // if we were navigated to through the More screen table, then we have a
        // navigation bar which also means we have a title. So hide the title
        // label in this case, otherwise, we need it
        if (getParentViewController() instanceof UINavigationController) {
            titleLabel.setHidden(true);
        } else {
            titleLabel.setHidden(false);
        }
    }

    @IBOutlet
    private void setTitleLabel(UILabel titleLabel) {
        this.titleLabel = titleLabel;
    }
}
