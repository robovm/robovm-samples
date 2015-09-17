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

import java.util.HashMap;
import java.util.Map;

import org.robovm.apple.foundation.NSObjectProtocol;
import org.robovm.apple.uikit.NSLayoutConstraint;
import org.robovm.apple.uikit.NSLayoutFormatOptions;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIButtonType;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;

public class ModalViewController extends UIViewController {
    private SubLevelViewController owningViewController;
    private final UILabel titleLabel;

    public ModalViewController() {
        UIView view = getView();
        view.setBackgroundColor(UIColor.white());

        titleLabel = new UILabel();
        titleLabel.setFont(UIFont.getSystemFont(17));
        titleLabel.setTranslatesAutoresizingMaskIntoConstraints(false);
        view.addSubview(titleLabel);

        UIButton button = new UIButton(UIButtonType.RoundedRect);
        button.setTitle("Done", UIControlState.Normal);
        button.setTitleShadowColor(UIColor.fromWhiteAlpha(0.5, 1), UIControlState.Normal);
        button.setTranslatesAutoresizingMaskIntoConstraints(false);
        button.addOnTouchUpInsideListener(new UIControl.OnTouchUpInsideListener() {
            @Override
            public void onTouchUpInside(UIControl control, UIEvent event) {
                dismissViewController(true, null);
            }
        });
        view.addSubview(button);

        // Layout
        Map<String, NSObjectProtocol> views = new HashMap<>();
        views.put("parent", view);
        views.put("title", titleLabel);
        views.put("done", button);

        view.addConstraints(NSLayoutConstraint.createConstraints("H:[parent]-(<=1)-[title]",
                NSLayoutFormatOptions.AlignAllCenterY, null, views));
        view.addConstraints(NSLayoutConstraint.createConstraints("V:[parent]-(<=1)-[title]-33-[done]",
                NSLayoutFormatOptions.AlignAllCenterX, null, views));
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);

        titleLabel.setText(owningViewController.getCurrentSelectionTitle());
    }

    public void setOwningViewController(SubLevelViewController owningViewController) {
        this.owningViewController = owningViewController;
    }
}
