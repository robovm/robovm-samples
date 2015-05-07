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

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIViewContentMode;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;

@CustomClass("AAPLImageViewController")
public class AAPLImageViewController extends UIViewController {

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        // The root view of the view controller set in Interface Builder is a
        // UIImageView.
        UIImageView imageView = (UIImageView) getView();

        imageView.setAnimationImages(new NSArray<UIImage>(UIImage.create("image_animal_1"),
                UIImage.create("image_animal_2"),
                UIImage.create("image_animal_3"),
                UIImage.create("image_animal_4"),
                UIImage.create("image_animal_5")));

        // We want the image to be scaled to the correct aspect ratio within
        // imageView's bounds.
        imageView.setContentMode(UIViewContentMode.ScaleAspectFit);

        // If the image does not have the same aspect ratio as imageView's
        // bounds, then imageView's backgroundColor will be applied to the
        // "empty" space.
        imageView.setBackgroundColor(UIColor.white());

        imageView.setAnimationDuration(5);
        imageView.startAnimating();

        imageView.setAccessibilityElement(true);
        imageView.setAccessibilityLabel("Animated");
    }
}
