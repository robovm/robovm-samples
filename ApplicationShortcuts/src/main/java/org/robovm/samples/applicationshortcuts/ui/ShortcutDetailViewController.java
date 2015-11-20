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
 * Portions of this code is based on Apple Inc's ApplicationShortcuts sample (v1.0)
 * which is copyright (C) 2015 Apple Inc.
 */
package org.robovm.samples.applicationshortcuts.ui;

import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSMutableDictionary;
import org.robovm.apple.foundation.NSNotificationCenter;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.UIApplicationShortcutIcon;
import org.robovm.apple.uikit.UIApplicationShortcutIconType;
import org.robovm.apple.uikit.UIApplicationShortcutItem;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIPickerView;
import org.robovm.apple.uikit.UIPickerViewModel;
import org.robovm.apple.uikit.UIStoryboardSegue;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.apple.uikit.UITextField;
import org.robovm.apple.uikit.UITextFieldDelegateAdapter;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;
import org.robovm.samples.applicationshortcuts.ApplicationShortcuts;

@CustomClass("ShortcutDetailViewController")
public class ShortcutDetailViewController extends UITableViewController {
    private static final String[] PICKER_ITEMS = new String[] { "Compose", "Play", "Pause", "Add", "Location",
        "Search", "Share" };

    @IBOutlet
    private UITextField titleTextField;
    @IBOutlet
    private UITextField subtitleTextField;
    @IBOutlet
    private UIPickerView pickerView;
    @IBOutlet
    private UIBarButtonItem doneButton;

    /**
     * Used to share information between this controller and its parent.
     */
    private UIApplicationShortcutItem shortcutItem;

    private NSObject textFieldObserverToken;

    @Override
    protected void dispose(boolean finalizing) {
        if (textFieldObserverToken != null) {
            NSNotificationCenter.getDefaultCenter().removeObserver(textFieldObserverToken);
        }
        super.dispose(finalizing);
    }

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        titleTextField.setDelegate(new UITextFieldDelegateAdapter() {
            @Override
            public boolean shouldReturn(UITextField textField) {
                textField.resignFirstResponder();
                return true;
            }
        });
        pickerView.setModel(new UIPickerViewModel() {
            @Override
            public long getNumberOfComponents(UIPickerView pickerView) {
                return 1;
            }

            @Override
            public long getNumberOfRows(UIPickerView pickerView, long component) {
                return PICKER_ITEMS.length;
            }

            @Override
            public String getRowTitle(UIPickerView pickerView, long row, long component) {
                return PICKER_ITEMS[(int) row];
            }
        });

        if (shortcutItem == null) {
            throw new RuntimeException("shortcutItem was not set");
        }
        // Initialize the UI to reflect the values of the shortcutItem.

        setTitle(shortcutItem.getLocalizedTitle());

        titleTextField.setText(shortcutItem.getLocalizedTitle());
        subtitleTextField.setText(shortcutItem.getLocalizedSubtitle());

        // Extract the name value representing the icon from the userInfo
        // dictionary, if provided.
        int iconValue = shortcutItem.getUserInfo().getInt(ApplicationShortcuts.APPLICATION_SHORTCUT_USER_INFO_ICON_KEY);
        // Select the matching row in the picker for the icon type.
        UIApplicationShortcutIconType iconType = getIconTypeForSelectedRow(iconValue);

        pickerView.selectRow(iconType.ordinal(), 0, false);

        textFieldObserverToken = UITextField.Notifications.observeTextDidChange(null, (textField) -> {
            // You cannot dismiss the view controller without a valid shortcut
            // title.
                int titleTextLength = titleTextField.getText() != null ? titleTextField.getText().length() : 0;
                doneButton.setEnabled(titleTextLength > 0);
            });
    }

    private UIApplicationShortcutIconType getIconTypeForSelectedRow(int row) {
        return UIApplicationShortcutIconType.values()[row];
    }

    @Override
    public void prepareForSegue(UIStoryboardSegue segue, NSObject sender) {
        if (shortcutItem == null) {
            throw new RuntimeException("shortcutItem was not set");
        }

        if (segue.getIdentifier().equals("ShortcutDetailUpdated")) {
            // In the updated case, create a shortcut item to represent the
            // final state of the view controller.
            UIApplicationShortcutIconType iconType = getIconTypeForSelectedRow((int) pickerView.getSelectedRow(0));

            UIApplicationShortcutIcon icon = new UIApplicationShortcutIcon(iconType);

            NSDictionary<NSString, ?> info = new NSMutableDictionary<>();
            info.put(ApplicationShortcuts.APPLICATION_SHORTCUT_USER_INFO_ICON_KEY, pickerView.getSelectedRow(0));
            shortcutItem = new UIApplicationShortcutItem(shortcutItem.getType(), titleTextField.getText(),
                    subtitleTextField.getText(), icon, info);
        }
    }

    public void setShortcutItem(UIApplicationShortcutItem shortcutItem) {
        this.shortcutItem = shortcutItem;
    }

    public UIApplicationShortcutItem getShortcutItem() {
        return shortcutItem;
    }
}
