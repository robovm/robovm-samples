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
 * Portions of this code is based on Apple Inc's StoreKitSuite sample (v1.0)
 * which is copyright (C) 2014 Apple Inc.
 */

package org.robovm.samples.inapppurchases.viewcontrollers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.storekit.SKPaymentTransaction;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.samples.inapppurchases.MyModel;

public class PaymentTransactionDetails extends UITableViewController {
    private final List<MyModel> details = new ArrayList<>();

    public PaymentTransactionDetails () {
    }

    @Override
    public long getNumberOfSections (UITableView tableView) {
        // Return the number of sections.
        return details.size();
    }

    @Override
    public long getNumberOfRowsInSection (UITableView tableView, long section) {
        MyModel model = details.get((int)section);
        // Return the number of rows in the section.
        return model.getElements().size();
    }

    @Override
    public String getTitleForHeader (UITableView tableView, long section) {
        MyModel model = details.get((int)section);
        // Return the header title for this section
        return model.getName();
    }

    public void setDetails (List<MyModel> details) {
        this.details.clear();
        this.details.addAll(details);
    }

    @SuppressWarnings("unchecked")
    @Override
    public UITableViewCell getCellForRow (UITableView tableView, NSIndexPath indexPath) {
        MyModel model = details.get((int)indexPath.getSection());
        List<SKPaymentTransaction> transactions = (List<SKPaymentTransaction>)model.getElements();
        Map<String, String> map = (Map<String, String>)transactions.get((int)indexPath.getRow());

        if (model.getName().equals("DOWNLOAD")) {
            UITableViewCell cell = tableView.dequeueReusableCell("customCellID", indexPath);
            if (cell == null) {
                cell = new UITableViewCell(UITableViewCellStyle.Default, "customCellID");
            }

            switch ((int)indexPath.getRow()) {
            case 0:
                cell.getTextLabel().setText("Identifier");
                cell.getDetailTextLabel().setText(map.get("Identifier"));
                break;
            case 1:
                cell.getTextLabel().setText("Content Version");
                cell.getDetailTextLabel().setText(map.get("Version"));
                break;
            case 2:
                cell.getTextLabel().setText("Content Length");
                cell.getDetailTextLabel().setText(map.get("Length"));
                break;
            default:
                break;
            }
            return cell;
        } else if (model.getName().equals("ORIGINAL TRANSACTION")) {
            UITableViewCell cell = tableView.dequeueReusableCell("customCellID", indexPath);
            if (cell == null) {
                cell = new UITableViewCell(UITableViewCellStyle.Value1, "customCellID");
            }

            switch ((int)indexPath.getRow()) {
            case 0:
                cell.getTextLabel().setText("Transaction ID");
                cell.getDetailTextLabel().setText(map.get("Transaction ID"));
                break;
            case 1:
                cell.getTextLabel().setText("Transaction Date");
                cell.getDetailTextLabel().setText(map.get("Transaction Date"));
                break;
            default:
                break;
            }

            return cell;
        } else {
            UITableViewCell cell = tableView.dequeueReusableCell("basicCellID", indexPath);
            cell.getTextLabel().setText(transactions.get(0).toString());
            return cell;
        }
    }
}
