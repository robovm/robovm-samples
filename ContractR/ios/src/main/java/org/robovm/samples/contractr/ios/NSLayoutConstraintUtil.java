/*
 * Copyright (C) 2014 RoboVM AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robovm.samples.contractr.ios;

import org.robovm.apple.uikit.NSLayoutAttribute;
import org.robovm.apple.uikit.NSLayoutConstraint;
import org.robovm.apple.uikit.NSLayoutRelation;
import org.robovm.apple.uikit.UIView;

/**
 * 
 */
public class NSLayoutConstraintUtil {
    public static NSLayoutConstraint centerHorizontally(UIView view, UIView container, double multipler, double c) {
        return new NSLayoutConstraint(
                view, NSLayoutAttribute.CenterX,
                NSLayoutRelation.Equal,
                container, NSLayoutAttribute.CenterX,
                multipler, c);
    }

    public static NSLayoutConstraint centerVertically(UIView view, UIView container, double multipler, double c) {
        return new NSLayoutConstraint(
                view, NSLayoutAttribute.CenterY,
                NSLayoutRelation.Equal,
                container, NSLayoutAttribute.CenterY,
                multipler, c);
    }

    public static NSLayoutConstraint equalWidth(UIView view, UIView container, double multipler, double c) {
        return new NSLayoutConstraint(
                view, NSLayoutAttribute.Width,
                NSLayoutRelation.Equal,
                container, NSLayoutAttribute.Width,
                multipler, c);
    }

    public static NSLayoutConstraint equalHeight(UIView view, UIView container, double multipler, double c) {
        return new NSLayoutConstraint(
                view, NSLayoutAttribute.Height,
                NSLayoutRelation.Equal,
                container, NSLayoutAttribute.Height,
                multipler, c);
    }
}
