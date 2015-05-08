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
 * Portions of this code is based on Apple Inc's TableSearch sample (v1.2)
 * which is copyright (C) 2015 Apple Inc.
 */

package org.robovm.samples.tablesearch.ui;

import java.util.List;

import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.samples.tablesearch.APLProduct;

@CustomClass("APLResultsTableViewController")
public class APLResultsTableViewController extends APLBaseTableViewController {
    private List<APLProduct> filteredProducts;

    @Override
    public long getNumberOfRowsInSection(UITableView tableView, long section) {
        return filteredProducts.size();
    }

    @Override
    public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
        APLProduct product = filteredProducts.get((int) indexPath.getRow());

        UITableViewCell cell = getTableView().dequeueReusableCell(CELL_IDENTIFIER);
        configureCell(cell, product);
        return cell;
    }

    public List<APLProduct> getFilteredProducts() {
        return filteredProducts;
    }

    public void setFilteredProducts(List<APLProduct> filteredProducts) {
        this.filteredProducts = filteredProducts;
    }
}
