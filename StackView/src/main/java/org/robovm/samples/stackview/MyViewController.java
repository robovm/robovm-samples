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
 * Portions of this code is based on Xamarin Inc's StackView sample.
 */
package org.robovm.samples.stackview;

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.uikit.*;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import org.robovm.objc.annotation.IBOutlet;

@CustomClass("MyViewController")
public class MyViewController extends UIViewController {
    private int rating;
    private UIStackView ratingsStack;

    @IBOutlet
    public void setRatingsStack(UIStackView ratingsStack) {
        this.ratingsStack = ratingsStack;
    }

    @IBAction
    private void increaseRating() {
        if (++this.rating > 5 ) {
            this.rating = 5;
            return;
        }

        UIImageView icon = new UIImageView(UIImage.getImage("icon.png"));
        icon.setContentMode(UIViewContentMode.ScaleAspectFit);
        this.ratingsStack.addArrangedSubview(icon);

        UIView.animate(0.25, () -> this.ratingsStack.layoutIfNeeded());
    }

    @IBAction
    private void decreaseRating() {
        if (--this.rating < 0) {
            this.rating = 0;
            return;
        }

        NSArray<UIView> icons = this.ratingsStack.getArrangedSubviews();
        UIView lastIcon = icons.get(icons.size()-1);
        this.ratingsStack.removeArrangedSubview(lastIcon);
        lastIcon.removeFromSuperview();

        UIView.animate(0.5, () -> this.ratingsStack.layoutIfNeeded());
    }
}
