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

import java.util.Arrays;
import java.util.List;

import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellAccessoryType;
import org.robovm.apple.uikit.UITableViewCellSelectionStyle;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.apple.uikit.UITableViewDataSourceAdapter;
import org.robovm.apple.uikit.UITableViewDelegateAdapter;

public class SubLevelViewController extends UITableViewController {
    private String currentSelectionTitle;
    private List<String> dataList;
    private ModalViewController modalViewController;

    public SubLevelViewController () {
        dataList = Arrays.asList("Feature 1", "Feature 2");

        UITableView tableView = getTableView();
        tableView.setAlwaysBounceVertical(true);
        tableView.setDelegate(new UITableViewDelegateAdapter() {
            @Override
            public void didSelectRow (UITableView tableView, NSIndexPath indexPath) {
                tableView.deselectRow(indexPath, false);
            }

            @Override
            public void accessoryButtonTapped (UITableView tableView, NSIndexPath indexPath) {
                modalViewController.setOwningViewController(SubLevelViewController.this);
                UITableViewCell cell = tableView.getCellForRow(indexPath);
                currentSelectionTitle = cell.getTextLabel().getText();
                presentViewController(modalViewController, true, null);
            }
        });
        tableView.setDataSource(new UITableViewDataSourceAdapter() {
            @Override
            public long getNumberOfRowsInSection (UITableView tableView, long section) {
                return dataList.size();
            }

            @Override
            public UITableViewCell getCellForRow (UITableView tableView, NSIndexPath indexPath) {
                final String identifier = "cellID2";

                UITableViewCell cell = tableView.dequeueReusableCell(identifier);
                if (cell == null) {
                    cell = new UITableViewCell(UITableViewCellStyle.Default, identifier);
                    cell.setAccessoryType(UITableViewCellAccessoryType.DetailDisclosureButton);
                    cell.setSelectionStyle(UITableViewCellSelectionStyle.Blue);
                }
                cell.getTextLabel().setText(dataList.get((int)indexPath.getRow()));

                return cell;
            }
        });

        modalViewController = new ModalViewController();
    }

    public String getCurrentSelectionTitle () {
        return currentSelectionTitle;
    }
}
