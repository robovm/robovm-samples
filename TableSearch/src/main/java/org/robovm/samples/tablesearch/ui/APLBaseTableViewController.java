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

import org.robovm.apple.foundation.NSNumber;
import org.robovm.apple.foundation.NSNumberFormatter;
import org.robovm.apple.foundation.NSNumberFormatterStyle;
import org.robovm.apple.uikit.UINib;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.samples.tablesearch.APLProduct;

@CustomClass("APLBaseTableViewController")
public class APLBaseTableViewController extends UITableViewController {
    final String CELL_IDENTIFIER = "cellID";
    private final String TABLE_CELL_NIB_NAME = "TableCell";

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        // we use a nib which contains the cell's view and this class as the
        // files owner
        getTableView().registerReusableCellNib(UINib.create(TABLE_CELL_NIB_NAME, null), CELL_IDENTIFIER);
    }

    public void configureCell(UITableViewCell cell, APLProduct product) {
        cell.getTextLabel().setText(product.getTitle());

        // build the price and year string
        // use NSNumberFormatter to get the currency format out of this NSNumber
        // (product.introPrice)
        NSNumberFormatter numFormatter = new NSNumberFormatter();
        numFormatter.setNumberStyle(NSNumberFormatterStyle.Currency);
        String priceString = numFormatter.format(NSNumber.valueOf(product.getIntroPrice()));

        String detailedStr = String.format("%s | %d", priceString, product.getYearIntroduced());
        cell.getDetailTextLabel().setText(detailedStr);
    }
}
