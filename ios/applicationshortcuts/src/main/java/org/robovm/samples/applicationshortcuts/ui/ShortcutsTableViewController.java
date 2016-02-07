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

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.foundation.NSCoder;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSMutableArray;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationShortcutItem;
import org.robovm.apple.uikit.UIStoryboardSegue;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellAccessoryType;
import org.robovm.apple.uikit.UITableViewCellSelectionStyle;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.apple.uikit.UITableViewRowAnimation;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;

@CustomClass("ShortcutsTableViewController")
public class ShortcutsTableViewController extends UITableViewController {
    private NSArray<UIApplicationShortcutItem> staticShortcuts;
    private final NSArray<UIApplicationShortcutItem> dynamicShortcuts = UIApplication.getSharedApplication()
            .getShortcutItems();

    public ShortcutsTableViewController(NSCoder coder) {
        super(coder);
        NSDictionary<NSString, ?> info = NSBundle.getMainBundle().getInfoDictionary(); // TODO
                                                                                       // we
                                                                                       // could
                                                                                       // wrap
                                                                                       // it
        // Obtain the UIApplicationShortcutItems array from the Info.plist. If
        // unavailable, there are no static shortcuts.
        @SuppressWarnings("unchecked") NSArray<NSDictionary<NSString, ?>> shortcuts = (NSArray<NSDictionary<NSString, ?>>) info
                .get("UIApplicationShortcutItems");
        if (shortcuts == null) {
            staticShortcuts = new NSArray<>();
        } else {
            staticShortcuts = new NSMutableArray<>();
            for (NSDictionary<NSString, ?> shortcut : shortcuts) {
                String shortcutType = shortcut.getString("UIApplicationShortcutItemType");
                String shortcutTitle = shortcut.getString("UIApplicationShortcutItemTitle");

                String localizedTitle = NSBundle.getMainBundle().getLocalizedInfoDictionary()
                        .getString(shortcutTitle, null);
                if (localizedTitle != null) {
                    shortcutTitle = localizedTitle;
                }

                String shortcutSubtitle = shortcut.getString("UIApplicationShortcutItemSubtitle", null);
                if (shortcutSubtitle != null) {
                    shortcutSubtitle = NSBundle.getMainBundle().getLocalizedInfoDictionary()
                            .getString(shortcutSubtitle, null);
                }

                staticShortcuts.add(new UIApplicationShortcutItem(shortcutType, shortcutTitle, shortcutSubtitle, null,
                        null));
            }
        }
    }

    @Override
    public long getNumberOfSections(UITableView tableView) {
        return 2;
    }

    @Override
    public String getTitleForHeader(UITableView tableView, long section) {
        switch ((int) section) {
        case 0:
            return "Static";
        default:
            return "Dynamic";
        }
    }

    @Override
    public long getNumberOfRowsInSection(UITableView tableView, long section) {
        return section == 0 ? staticShortcuts.size() : dynamicShortcuts.size();
    }

    @Override
    public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
        UITableViewCell cell = tableView.dequeueReusableCell("CellID", indexPath);

        UIApplicationShortcutItem shortcut;

        if (indexPath.getSection() == 0) {
            // Static shortcuts (cannot be edited).
            shortcut = staticShortcuts.get(indexPath.getRow());
            cell.setAccessoryType(UITableViewCellAccessoryType.None);
            cell.setSelectionStyle(UITableViewCellSelectionStyle.None);
        } else {
            // Dynamic shortcuts.
            shortcut = dynamicShortcuts.get(indexPath.getRow());
        }

        cell.getTextLabel().setText(shortcut.getLocalizedTitle());
        cell.getDetailTextLabel().setText(shortcut.getLocalizedSubtitle());

        return cell;
    }

    @Override
    public void prepareForSegue(UIStoryboardSegue segue, NSObject sender) {
        // Supply the shortcutItem matching the selected row from the data
        // source.
        if (segue.getIdentifier().equals("ShowShortcutDetail")) {
            NSIndexPath indexPath = getTableView().getIndexPathForSelectedRow();
            ShortcutDetailViewController controller = (ShortcutDetailViewController) segue
                    .getDestinationViewController();
            if (controller != null) {
                controller.setShortcutItem(dynamicShortcuts.get(indexPath.getRow()));
            }
        }
    }

    @Override
    public boolean shouldPerformSegue(String identifier, NSObject sender) {
        // Block navigating to detail view controller for static shortcuts
        // (which are not editable).
        NSIndexPath selectedIndexPath = getTableView().getIndexPathForSelectedRow();
        if (selectedIndexPath == null) {
            return false;
        }
        return selectedIndexPath.getSection() > 0;
    }

    /**
     * Unwind segue action called when the user taps 'Done' after navigating to
     * the detail controller.
     */
    @IBAction
    private void done(UIStoryboardSegue sender) {
        // Obtain the edited shortcut from our source view controller.
        ShortcutDetailViewController sourceViewController = (ShortcutDetailViewController) sender
                .getSourceViewController();
        NSIndexPath selected = getTableView().getIndexPathForSelectedRow();
        UIApplicationShortcutItem updatedShortcutItem = sourceViewController.getShortcutItem();
        if (updatedShortcutItem == null) {
            return;
        }

        // Update our data source.
        dynamicShortcuts.set(selected.getRow(), updatedShortcutItem);

        // Update the application's shortcutItems.
        UIApplication.getSharedApplication().setShortcutItems(dynamicShortcuts);

        getTableView().reloadRow(selected, UITableViewRowAnimation.Automatic);
    }

    /**
     * Unwind segue action called when the user taps 'Cancel' after navigating
     * to the detail controller.
     */
    @IBAction
    private void cancel(UIStoryboardSegue sender) {}
}
