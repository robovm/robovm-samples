/*
 * Copyright (C) 2013-2015 RoboVM AB
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
package org.robovm.samples.robopods.facebook.ios.ui;

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSNumber;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.samples.robopods.facebook.ios.FacebookHandler;

@CustomClass("FriendsViewController")
public class FriendsViewController extends UITableViewController {
    private NSArray<NSDictionary<NSString, ?>> friends;

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);

        FacebookHandler.getInstance().requestFriends(new FacebookHandler.RequestListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onSuccess(NSObject result) {
                FacebookHandler.log("Friends result: %s", result);

                NSDictionary<NSString, ?> root = (NSDictionary<NSString, ?>) result;
                friends = (NSArray<NSDictionary<NSString, ?>>) root
                        .get(new NSString("data"));
                getTableView().reloadData();
            }

            @Override
            public void onError(String message) {
                FacebookHandler.getInstance().alertError("Error while getting a list of your friends!", message);
            }

            @Override
            public void onCancel() {}
        });
    }

    @Override
    public long getNumberOfRowsInSection(UITableView tableView, long section) {
        return friends != null ? friends.size() : 0;
    }

    @Override
    public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
        UITableViewCell cell = tableView.dequeueReusableCell("cell");

        NSDictionary<NSString, ?> data = friends.get(indexPath.getRow());
        String id = data.get(new NSString("id")).toString();
        boolean installed = ((NSNumber) data.get(new NSString("installed"))).intValue() == 1;
        String name = data.get(new NSString("name")).toString();

        cell.getTextLabel().setText(String.format("%s (%s)", name, id));
        cell.getDetailTextLabel().setText(installed ? "App User" : "No App User");

        return cell;
    }
}
