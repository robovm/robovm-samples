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
 * Portions of this code is based on Apple Inc's AppPrefs sample (v5.1)
 * which is copyright (C) 2008-2014 Apple Inc.
 */

package org.robovm.samples.appprefs.viewcontrollers;

import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSNotificationCenter;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.foundation.NSUserDefaults;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonItemStyle;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIButtonType;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UINavigationController;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.objc.block.VoidBlock1;

public class RootViewController extends UITableViewController {
    // It's best practice to define constant strings for each preference's key.
    // These constants should be defined in a location that is visible to all
    // source files that will be accessing the preferences.
    private static final String FIRST_NAME_KEY = "firstNameKey";
    private static final String LAST_NAME_KEY = "lastNameKey";
    private static final String NAME_COLOR_KEY = "nameColorKey";

    private InfoViewController infoViewController;
    private UINavigationController infoNavController;
    private NSObject notification;

    // Values from the app's preferences
    private String firstName;
    private String lastName;
    private UIColor nameColor;

    public RootViewController () {
        setTitle("AppPrefs");

        UIButton customButton = UIButton.create(UIButtonType.InfoLight);
        customButton.setShowsTouchWhenHighlighted(true);
        customButton.addOnTouchUpInsideListener(new UIControl.OnTouchUpInsideListener() {
            @Override
            public void onTouchUpInside (UIControl control, UIEvent event) {
                if (infoViewController == null) {
                    infoViewController = new InfoViewController();
                    infoNavController = new UINavigationController(infoViewController);
                }
                presentViewController(infoNavController, true, null);
            }
        });
        UIBarButtonItem rightBarButtonItem = new UIBarButtonItem(customButton);
        getNavigationItem().setRightBarButtonItem(rightBarButtonItem);

        // Only iOS 8 and above supports the UIApplicationOpenSettingsURLString
        // used to launch the Settings app from your application.
        // Remove the Settings button from the navigation bar
        // since it won't be able to do anything.
        if (Foundation.getMajorSystemVersion() >= 8) {
            getNavigationItem().setLeftBarButtonItem(
                new UIBarButtonItem("Settings", UIBarButtonItemStyle.Plain, new UIBarButtonItem.OnClickListener() {
                    /** Launches the Settings app. The Settings app will automatically navigate to to the settings page for this
                     * app. */
                    @Override
                    public void onClick (UIBarButtonItem barButtonItem) {
                        // UIApplicationOpenSettingsURLString is only availiable in iOS 8 and above.
                        // The following code will crash if run on a prior version of iOS. See the
                        UIApplication.getSharedApplication().openURL(new NSURL(UIApplication.getOpenSettingsURLString()));
                    }
                }));
        }
    }

    @Override
    public void viewWillAppear (boolean animated) {
        super.viewWillAppear(animated);
        // Load our preferences. Preloading the relevant preferences here will
        // prevent possible diskIO latency from stalling our code in more time
        // critical areas, such as tableView:cellForRowAtIndexPath:, where the
        // values associated with these preferences are actually needed.
        onDefaultsChanged();

        // Begin listening for changes to our preferences when the Settings app does
        // so, when we are resumed from the backround, this will give us a chance to
        // update our UI
        notification = NSUserDefaults.Notifications.observeDidChange(null, new VoidBlock1<NSUserDefaults>() {
            @Override
            public void invoke (NSUserDefaults a) {
                onDefaultsChanged();
            }
        });
    }

    @Override
    public void viewWillDisappear (boolean animated) {
        super.viewWillDisappear(animated);

        // Stop listening for the NSUserDefaultsDidChangeNotification
        NSNotificationCenter.getDefaultCenter().removeObserver(notification);
    }

    /** Handler for the NSUserDefaultsDidChangeNotification. Loads the preferences from the defaults database into the holding
     * properies, then asks the tableView to reload itself. */
    private void onDefaultsChanged () {
        NSUserDefaults standardDefaults = NSUserDefaults.getStandardUserDefaults();

        firstName = standardDefaults.getString(FIRST_NAME_KEY);
        lastName = standardDefaults.getString(LAST_NAME_KEY);

        // The value for the 'Text Color' setting is stored as an integer between
        // one and three inclusive. Convert the integer into a UIColor object.
        int textColor = (int)standardDefaults.getInteger(NAME_COLOR_KEY);

        switch (textColor) {
        case 1:
            nameColor = UIColor.blue();
            break;
        case 2:
            nameColor = UIColor.red();
            break;
        case 3:
            nameColor = UIColor.green();
            break;
        default:
            throw new RuntimeException("Got an unexpected value " + textColor + " for " + NAME_COLOR_KEY);
        }

        getTableView().reloadData();
    }

    @Override
    public long getNumberOfRowsInSection (UITableView tableView, long section) {
        return 1;
    }

    @Override
    public UITableViewCell getCellForRow (UITableView tableView, NSIndexPath indexPath) {
        UITableViewCell cell = tableView.dequeueReusableCell("NameCell");
        if (cell == null) {
            cell = new UITableViewCell(UITableViewCellStyle.Default, "NameCell");
            cell.getTextLabel().setFont(UIFont.getBoldSystemFont(20));
        }

        cell.getTextLabel().setText(firstName + " " + lastName);
        cell.getTextLabel().setTextColor(nameColor);
        return cell;
    }
}
