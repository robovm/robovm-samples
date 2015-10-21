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

package org.robovm.samples.tabster.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellAccessoryType;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.apple.uikit.UITableViewDataSourceAdapter;
import org.robovm.apple.uikit.UITableViewDelegateAdapter;

public class OneViewController extends UITableViewController {
    private final List<String> dataList;
    private SubLevelViewController subLevelViewController;

    public OneViewController () {
        dataList = new ArrayList<>(Arrays.asList("Mac Pro", "Mac mini", "iMac", "MacBook", "MacBook Pro", "MacBook Air"));

        subLevelViewController = new SubLevelViewController();

        UITableView tableView = getTableView();
        tableView.setAlwaysBounceVertical(true);
        tableView.setBackgroundColor(UIColor.white());
        tableView.setDataSource(new UITableViewDataSourceAdapter() {
            @Override
            public long getNumberOfRowsInSection (UITableView tableView, long section) {
                return dataList.size();
            }

            @Override
            public UITableViewCell getCellForRow (UITableView tableView, NSIndexPath indexPath) {
                final String cellID = "cellID";

                UITableViewCell cell = tableView.dequeueReusableCell(cellID);
                if (cell == null) {
                    cell = new UITableViewCell(UITableViewCellStyle.Default, cellID);
                    cell.setAccessoryType(UITableViewCellAccessoryType.DisclosureIndicator);
                }
                cell.getTextLabel().setText(dataList.get((int)indexPath.getRow()));

                return cell;
            }
        });
        tableView.setDelegate(new UITableViewDelegateAdapter() {
            @Override
            public void didSelectRow (UITableView tableView, NSIndexPath indexPath) {
                UITableViewCell cell = tableView.getCellForRow(indexPath);
                subLevelViewController.setTitle(cell.getTextLabel().getText());

                getNavigationController().pushViewController(subLevelViewController, true);
            }
        });

    }

    @Override
    public void viewDidLoad () {
        super.viewDidLoad();

        getNavigationItem().setTitle("One");
    }

    @Override
    public void viewWillAppear (boolean animated) {
        super.viewWillAppear(animated);

        // this UIViewController is about to re-appear, make sure we remove the current selection in our table view
        NSIndexPath tableSelection = getTableView().getIndexPathForSelectedRow();
        getTableView().deselectRow(tableSelection, false);
    }
}
