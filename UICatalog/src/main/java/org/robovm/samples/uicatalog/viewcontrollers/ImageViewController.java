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
 * Portions of this code is based on Apple Inc's UICatalog sample (v2.11)
 * which is copyright (C) 2008-2013 Apple Inc.
 */

package org.robovm.samples.uicatalog.viewcontrollers;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UISlider;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.Method;

/** The view controller for hosting the UIImageView containing multiple images. */
public class ImageViewController extends UIViewController {
    private static final float MIN_DURATION = 0.0f;
    private static final float MAX_DURATION = 10.0f;

    private UIImageView imageView;
    private UISlider slider;

    @Override
    public void viewDidLoad () {
        super.viewDidLoad();

        imageView = new UIImageView(new CGRect(42, 70, 236, 174));
        slider = new UISlider(new CGRect(18, 398, 284, 23));

        NSArray<UIImage> images = new NSArray<UIImage>(UIImage.create("scene1.jpg"), UIImage.create("scene2.jpg"),
            UIImage.create("scene3.jpg"), UIImage.create("scene4.jpg"), UIImage.create("scene5.jpg"));

        imageView.setAnimationImages(images);
        UIImageView.setAnimationDurationInSeconds(5.0);
        imageView.setBackgroundColor(UIColor.black());
        imageView.setAnimationDuration(5.0f); // sync with slider below
        imageView.stopAnimating();

        slider.setMaximumValue(MIN_DURATION);
        slider.setMaximumValue(MAX_DURATION);
        slider.setValue(5.0f);

        slider.addOnValueChangedListener(new UIControl.OnValueChangedListener() {
            @Override
            public void onValueChanged (UIControl control) {
                sliderAction();
            }
        });

        getView().addSubview(imageView);
        getView().addSubview(slider);

    }

    @Method
    private void sliderAction () {
        UISlider durationSlider = slider;
        imageView.setAnimationDuration(durationSlider.getValue());
        if (!imageView.isAnimating()) {
            imageView.startAnimating();
        }
    }

    @Override
    public void viewWillDisappear (boolean animated) {
        super.viewWillDisappear(animated);
        imageView.stopAnimating();
    }

    @Override
    public void viewWillAppear (boolean animated) {
        super.viewWillAppear(animated);
        imageView.startAnimating();
        getNavigationController().getNavigationBar().setTintColor(UIColor.black());
    }

}
