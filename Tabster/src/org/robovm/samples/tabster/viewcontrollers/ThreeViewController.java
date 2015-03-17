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
import org.robovm.apple.foundation.NSRange;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.foundation.NSUserDefaults;
import org.robovm.apple.uikit.NSLayoutConstraint;
import org.robovm.apple.uikit.NSLayoutFormatOptions;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonSystemItem;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIKeyboardType;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UITextBorderStyle;
import org.robovm.apple.uikit.UITextField;
import org.robovm.apple.uikit.UITextFieldDelegateAdapter;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;

public class ThreeViewController extends UIViewController {
    private static final String BADGE_VALUE_PREF_KEY = "BadgeValue"; // badge value key for storing to NSUserDefaults

    private UIBarButtonItem doneButton;
    private final UITextField badgeField;

    public ThreeViewController () {
        UIView view = getView();
        view.setBackgroundColor(UIColor.fromWhiteAlpha(0.66, 1));

        badgeField = new UITextField();
        badgeField.setBorderStyle(UITextBorderStyle.RoundedRect);
        badgeField.setFont(UIFont.getSystemFont(14));
        badgeField.setKeyboardType(UIKeyboardType.NumberPad);
        badgeField.setTranslatesAutoresizingMaskIntoConstraints(false);
        badgeField.setDelegate(new UITextFieldDelegateAdapter() {
            @Override
            public void didBeginEditing (UITextField textField) {
                // user is starting to edit, add the done button to the navigation bar
                getNavigationItem().setRightBarButtonItem(doneButton);
            }

            @Override
            public void didEndEditing (UITextField textField) {
                // user is done editing, remove the done button from the navigation bar
                getNavigationItem().setRightBarButtonItem(null);
            }

            @Override
            public boolean shouldChangeCharacters (UITextField textField, NSRange range, String string) {
                boolean result = true;

                // restrict the maximum number of characters to 5
                if (textField.getText().length() == 5 && string.length() > 0) {
                    result = false;
                }

                return result;
            }
        });
        view.addSubview(badgeField);

        UILabel valueLabel = new UILabel();
        valueLabel.setText("Value");
        valueLabel.setTranslatesAutoresizingMaskIntoConstraints(false);
        valueLabel.setFont(UIFont.getSystemFont(17));
        valueLabel.setTextColor(UIColor.darkText());
        view.addSubview(valueLabel);

        // Layout
        Map<String, NSObjectProtocol> views = new HashMap<>();
        views.put("value", valueLabel);
        views.put("badge", badgeField);
        views.put("top", getTopLayoutGuide());

        view.addConstraints(NSLayoutConstraint.create("H:|-20-[value(48)]-[badge]-20-|", NSLayoutFormatOptions.None, null, views));
        view.addConstraints(NSLayoutConstraint.create("V:[top]-58-[value]", NSLayoutFormatOptions.None, null, views));
        view.addConstraints(NSLayoutConstraint.create("V:[top]-54-[badge]", NSLayoutFormatOptions.None, null, views));

        doneButton = new UIBarButtonItem(UIBarButtonSystemItem.Done, new UIBarButtonItem.OnClickListener() {
            @Override
            public void onClick (UIBarButtonItem barButtonItem) {
                // dismiss the keyboard by resigning our badge edit field as first responder
                badgeField.resignFirstResponder();

                // set the badge value to our tab item (but only if a valid string)
                if (badgeField.getText().length() > 0) {
                    // a value was entered,
                    // because we are inside a navigation controller,
                    // we must access its tabBarItem to set the badgeValue.
                    getNavigationController().getTabBarItem().setBadgeValue(badgeField.getText());
                } else {
                    // no value was entered
                    getNavigationController().getTabBarItem().setBadgeValue(null);
                }

                NSUserDefaults.getStandardUserDefaults().put(BADGE_VALUE_PREF_KEY, new NSString(badgeField.getText()));
            }
        });
    }

    @Override
    public void viewDidLoad () {
        super.viewDidLoad();

        getNavigationItem().setTitle("Three");
    }

    @Override
    public void viewWillAppear (boolean animated) {
        // set the badge value in our test field and tabbar item
        String badgeValue = NSUserDefaults.getStandardUserDefaults().getString(BADGE_VALUE_PREF_KEY);
        if (badgeValue != null && badgeValue.length() != 0) {
            badgeField.setText(badgeValue);
            getNavigationController().getTabBarItem().setBadgeValue(badgeField.getText());
        }
    }
}
