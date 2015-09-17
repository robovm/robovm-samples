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
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIStoryboardSegue;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.objc.annotation.CustomClass;

@CustomClass("TwoViewController")
public class TwoViewController extends UITableViewController {
    private List<String> dataList;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        dataList = Arrays.asList("Cherry Lake", "Lake Don Pedro");
    }

    @Override
    public long getNumberOfRowsInSection(UITableView tableView, long section) {
        return dataList.size();
    }

    @Override
    public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
        String cellID = "cellIDTwo";

        UITableViewCell cell = tableView.dequeueReusableCell(cellID);
        cell.getTextLabel().setText(dataList.get(indexPath.getRow()));

        return cell;
    }

    @Override
    public void prepareForSegue(UIStoryboardSegue segue, NSObject sender) {
        if (segue.getIdentifier().equals("LandscapeViewSegue")) {
            LandscapeViewController landscapeViewController = (LandscapeViewController) segue
                    .getDestinationViewController();
            UITableViewCell cell = (UITableViewCell) sender;
            UIImage image = null;
            if (cell.getTextLabel().getText().equals("Cherry Lake")) {
                image = UIImage.getImage("cherrylake");
            } else if (cell.getTextLabel().getText().equals("Lake Don Pedro")) {
                image = UIImage.getImage("lakedonpedro");
            }
            landscapeViewController.setImage(image);
        }
    }
}
