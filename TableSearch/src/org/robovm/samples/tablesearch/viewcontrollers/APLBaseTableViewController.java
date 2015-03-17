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

import org.robovm.apple.foundation.NSNumberFormatter;
import org.robovm.apple.foundation.NSNumberFormatterStyle;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.samples.tablesearch.APLProduct;

public class APLBaseTableViewController extends UITableViewController {

    public void configureCell (UITableViewCell cell, APLProduct product) {
        cell.getTextLabel().setText(product.getTitle());

        // build the price and year string
        // use NSNumberFormatter to get the currency format out of this NSNumber (product.introPrice)
        NSNumberFormatter numFormatter = new NSNumberFormatter();
        numFormatter.setNumberStyle(NSNumberFormatterStyle.Currency);
        String priceString = numFormatter.format(product.getIntroPrice());

        String detailedStr = String.format("%s | %d", priceString, product.getYearIntroduced());
        cell.getDetailTextLabel().setText(detailedStr);
    }
}
