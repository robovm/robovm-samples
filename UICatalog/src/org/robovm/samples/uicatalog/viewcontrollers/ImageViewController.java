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

        NSArray<UIImage> images = new NSArray<UIImage>(UIImage.createFromBundle("scene1.jpg"),
            UIImage.createFromBundle("scene2.jpg"), UIImage.createFromBundle("scene3.jpg"),
            UIImage.createFromBundle("scene4.jpg"), UIImage.createFromBundle("scene5.jpg"));

        this.imageView.setAnimationImages(images);
        UIImageView.setDurationForAnimation(5.0);
        this.imageView.setBackgroundColor(UIColor.black());
        this.imageView.setAnimationDuration(5.0f); // sync with slider below
        this.imageView.stopAnimating();

        this.slider.setMaximumValue(MIN_DURATION);
        this.slider.setMaximumValue(MAX_DURATION);
        slider.setValue(5.0f);

        this.slider.addOnValueChangedListener(new UIControl.OnValueChangedListener() {
            @Override
            public void onValueChanged (UIControl control) {
                sliderAction();
            }
        });

        this.getView().addSubview(imageView);
        this.getView().addSubview(slider);

    }

    @Method
    private void sliderAction () {
        UISlider durationSlider = slider;
        this.imageView.setAnimationDuration(durationSlider.getValue());
        if (!this.imageView.isAnimating()) {
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
        this.getNavigationController().getNavigationBar().setTintColor(UIColor.black());
    }

}
