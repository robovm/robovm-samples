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
package org.robovm.samples.tabster.viewcontrollers;

import java.util.Arrays;
import java.util.List;

import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.uikit.UIStoryboardSegue;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;

@CustomClass("SubLevelViewController")
public class SubLevelViewController extends UITableViewController {
    private String currentSelectionTitle;
    private List<String> dataList;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        dataList = Arrays.asList("Feature 1", "Feature 2");
    }

    @Override
    public void prepareForSegue(UIStoryboardSegue segue, NSObject sender) {
        if (segue.getIdentifier().equals("modalSegue")) {
            ModalViewController modalViewController = (ModalViewController) segue.getDestinationViewController();
            modalViewController.setOwningViewController(this);
            UITableViewCell cell = (UITableViewCell) sender;
            currentSelectionTitle = cell.getTextLabel().getText();
        }
    }

    @Override
    public void didSelectRow(UITableView tableView, NSIndexPath indexPath) {
        tableView.deselectRow(indexPath, false);
    }

    @Override
    public long getNumberOfRowsInSection(UITableView tableView, long section) {
        return dataList.size();
    }

    @Override
    public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
        String cellID2 = "cellID2";

        UITableViewCell cell = tableView.dequeueReusableCell(cellID2);
        cell.getTextLabel().setText(dataList.get((int) indexPath.getRow()));

        return cell;
    }

    @IBAction
    private void unwindToSub(UIStoryboardSegue unwindSegue) {

    }

    public String getCurrentSelectionTitle() {
        return currentSelectionTitle;
    }
}
