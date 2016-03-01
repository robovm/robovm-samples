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
package org.robovm.samples.tabster.ui;

import org.robovm.apple.foundation.NSRange;
import org.robovm.apple.foundation.NSUserDefaults;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonSystemItem;
import org.robovm.apple.uikit.UITextField;
import org.robovm.apple.uikit.UITextFieldDelegate;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;

@CustomClass("ThreeViewController")
public class ThreeViewController extends UIViewController implements UITextFieldDelegate {
    // badge value key for storing to NSUserDefaults
    private static final String BADGE_VALUE_PREF_KEY = "BadgeValue";

    @IBOutlet
    private UITextField badgeField;
    private UIBarButtonItem doneButton;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        // set the badge value in our test field and tabbar item
        String badgeValue = NSUserDefaults.getStandardUserDefaults().getString(BADGE_VALUE_PREF_KEY);

        doneButton = new UIBarButtonItem(UIBarButtonSystemItem.Done, new UIBarButtonItem.OnClickListener() {
            @Override
            public void onClick(UIBarButtonItem barButtonItem) {
                // dismiss the keyboard by resigning our badge edit field as
                // first responder
                badgeField.resignFirstResponder();

                // set the badge value to our tab item (but only if a valid
                // string)
                if (badgeField.getText() != null && badgeField.getText().length() > 0) {
                    // a value was entered,
                    // because we are inside a navigation controller,
                    // we must access its tabBarItem to set the badgeValue
                    getNavigationController().getTabBarItem().setBadgeValue(badgeField.getText());
                } else {
                    // no value was entered
                    getNavigationController().getTabBarItem().setBadgeValue(null);
                }

                NSUserDefaults.getStandardUserDefaults().put(BADGE_VALUE_PREF_KEY, badgeField.getText());
            }
        });

        if (badgeValue != null && badgeValue.length() > 0) {
            badgeField.setText(badgeValue);
            getNavigationController().getTabBarItem().setBadgeValue(badgeField.getText());
        }
    }

    @Override
    public void didBeginEditing(UITextField textField) {
        // user is starting to edit, add the done button to the navigation bar
        getNavigationItem().setRightBarButtonItem(doneButton);
    }

    @Override
    public void didEndEditing(UITextField textField) {
        // user is done editing, remove the done button from the navigation bar
        getNavigationItem().setRightBarButtonItem(null);
    }

    @Override
    public boolean shouldChangeCharacters(UITextField textField, NSRange range, String string) {
        boolean result = true;

        // restrict the maximum number of characters to 5
        if (textField.getText().length() == 5 && string.length() > 0)
            result = false;

        return result;
    }

    @Override
    public boolean shouldBeginEditing(UITextField textField) {
        return true;
    }

    @Override
    public boolean shouldEndEditing(UITextField textField) {
        return true;
    }

    @Override
    public boolean shouldClear(UITextField textField) {
        return true;
    }

    @Override
    public boolean shouldReturn(UITextField textField) {
        return true;
    }
}
