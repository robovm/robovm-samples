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

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSObjectProtocol;
import org.robovm.apple.uikit.NSLayoutConstraint;
import org.robovm.apple.uikit.NSLayoutFormatOptions;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UINavigationController;
import org.robovm.apple.uikit.UITabBarItem;
import org.robovm.apple.uikit.UITabBarSystemItem;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;

public class FavoritesViewController extends UIViewController {
    private final UILabel titleLabel;

    public FavoritesViewController() {
        setTabBarItem(new UITabBarItem(UITabBarSystemItem.Favorites, 0));

        UIView view = getView();
        view.setBackgroundColor(UIColor.fromRGBA(0.77, 1, 1, 1));

        titleLabel = new UILabel(new CGRect(0, 0, 100, 100));
        titleLabel.setFont(UIFont.getSystemFont(17));
        titleLabel.setText("Favorites");
        titleLabel.setTranslatesAutoresizingMaskIntoConstraints(false);
        view.addSubview(titleLabel);

        // Layout
        Map<String, NSObjectProtocol> views = new HashMap<>();
        views.put("parent", view);
        views.put("title", titleLabel);

        view.addConstraints(NSLayoutConstraint.createConstraints("H:[parent]-(<=1)-[title]",
                NSLayoutFormatOptions.AlignAllCenterY, null, views));
        view.addConstraints(NSLayoutConstraint.createConstraints("V:[parent]-(<=1)-[title]",
                NSLayoutFormatOptions.AlignAllCenterX, null, views));
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);

        // if we were navigated to through the More screen table, then we have a
        // navigation bar which
        // also means we have a title. So hide the title label in this case,
        // otherwise, we need it
        if (getParentViewController() instanceof UINavigationController) {
            titleLabel.setHidden(true);
        } else {
            titleLabel.setHidden(false);
        }
    }

}
