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
 * Portions of this code is based on Apple Inc's TableSearch sample (v1.0)
 * which is copyright (C) 2014 Apple Inc.
 */

package org.robovm.samples.tablesearch.viewcontrollers;

import java.util.ArrayList;
import java.util.List;

import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellAccessoryType;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.samples.tablesearch.APLProduct;

public class APLResultsTableViewController extends APLBaseTableViewController {
    private List<APLProduct> filteredProducts = new ArrayList<>();

    public APLResultsTableViewController () {
    }

    @Override
    public long getNumberOfRowsInSection (UITableView tableView, long section) {
        return filteredProducts.size();
    }

    @Override
    public UITableViewCell getCellForRow (UITableView tableView, NSIndexPath indexPath) {
        APLProduct product = filteredProducts.get((int)indexPath.getRow());

        UITableViewCell cell = tableView.dequeueReusableCell("CellID");
        if (cell == null) {
            cell = new UITableViewCell(UITableViewCellStyle.Value1, "CellID");
            cell.setAccessoryType(UITableViewCellAccessoryType.DisclosureIndicator);
        }
        configureCell(cell, product);
        return cell;
    }

    public List<APLProduct> getFilteredProducts () {
        return filteredProducts;
    }

    public void setFilteredProducts (List<APLProduct> filteredProducts) {
        this.filteredProducts = filteredProducts;
    }
}
