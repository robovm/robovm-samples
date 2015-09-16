/*
 * Copyright (C) 2014 RoboVM AB
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
 * Portions of this code is based on Apple Inc's Tabster sample (v1.6)
 * which is copyright (C) 2011-2014 Apple Inc.
 */

package org.robovm.samples.tabster.viewcontrollers;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIInterfaceOrientationMask;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;

public class LandscapeViewController extends UIViewController {
    private UIImage image;
    private final UIImageView imageView;

    public LandscapeViewController() {
        UIView view = getView();
        view.setBackgroundColor(UIColor.white());

        imageView = new UIImageView(new CGRect(0, 0, 568, 320));
        view.addSubview(imageView);

        UIButton button = new UIButton(new CGRect(20, 259, 49, 41));
        button.setImage(UIImage.getImage("left"), UIControlState.Normal);
        button.setImage(UIImage.getImage("left_pressed"),
                UIControlState.with(UIControlState.Selected, UIControlState.Highlighted));
        button.addOnTouchUpInsideListener(new UIControl.OnTouchUpInsideListener() {
            @Override
            public void onTouchUpInside(UIControl control, UIEvent event) {
                dismissViewController(false, null);
            }
        });
        view.addSubview(button);
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
}
