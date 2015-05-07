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

import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIInterfaceOrientationMask;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import org.robovm.objc.annotation.IBOutlet;

@CustomClass("LandscapeViewController")
public class LandscapeViewController extends UIViewController {
    private UIImageView imageView;
    private UIImage image;

    @IBAction
    private void actionCompleted(NSObject sender) {
        dismissViewController(false, null);
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);
        imageView.setImage(image);
    }

    @Override
    public UIInterfaceOrientationMask getSupportedInterfaceOrientations() {
        return UIInterfaceOrientationMask.Landscape;
    }

    @Override
    public boolean shouldAutorotate() {
        return true;
    }

    public void setImage(UIImage image) {
        this.image = image;
    }

    @IBOutlet
    private void setImageView(UIImageView imageView) {
        this.imageView = imageView;
    }
}
