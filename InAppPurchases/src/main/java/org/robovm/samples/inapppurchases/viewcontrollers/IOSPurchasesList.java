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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSByteCountFormatter;
import org.robovm.apple.foundation.NSByteCountFormatterCountStyle;
import org.robovm.apple.foundation.NSDateFormatter;
import org.robovm.apple.foundation.NSDateFormatterStyle;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.storekit.SKDownload;
import org.robovm.apple.storekit.SKPaymentTransaction;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellAccessoryType;
import org.robovm.apple.uikit.UITableViewCellSelectionStyle;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.samples.inapppurchases.MyModel;
import org.robovm.samples.inapppurchases.StoreManager;
import org.robovm.samples.inapppurchases.StoreObserver;

public class IOSPurchasesList extends UITableViewController {
    private final List<MyModel> allPurchases = new ArrayList<>();
    private final List<String> sectionNames = new ArrayList<>();

    private final PaymentTransactionDetails transactionDetails;

    public IOSPurchasesList() {
        transactionDetails = new PaymentTransactionDetails();

        allPurchases.add(new MyModel("PURCHASED"));
        allPurchases.add(new MyModel("RESTORED"));
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);

        // Update allPurchases if there are purchased products
        if (StoreObserver.getInstance().hasPurchasedProducts()) {
            MyModel model = allPurchases.get(0);
            model.setElements(StoreObserver.getInstance().getProductsPurchased());
            sectionNames.add("PURCHASED");
        }

        // Update allPurchases if there are restored products
        if (StoreObserver.getInstance().hasRestoredProducts()) {
            MyModel model = allPurchases.get(1);
            model.setElements(StoreObserver.getInstance().getProductsRestored());
            sectionNames.add("RESTORED");
        }
        // Refresh the UI with the above data
        getTableView().reloadData();
    }

    @Override
    public long getNumberOfSections(UITableView tableView) {
        int numberOfSections = 0;
        if (!allPurchases.get(0).isEmpty()) {
            numberOfSections++;
        }
        if (!allPurchases.get(1).isEmpty()) {
            numberOfSections++;
        }

        // Return the number of sections.
        return numberOfSections;
    }

    @Override
    public String getTitleForHeader(UITableView tableView, long section) {
        // Fetch the section name at the given index
        String sectionName = sectionNames.get((int) section);
        // Fetch the model whose name matches sectionName
        MyModel model = sectionName.equals("PURCHASED") ? allPurchases.get(0) : allPurchases.get(1);

        // Return the header title for this section
        return !model.isEmpty() ? model.getName() : null;
    }

    @Override
    public long getNumberOfRowsInSection(UITableView tableView, long section) {
        String sectionName = sectionNames.get((int) section);
        MyModel model = sectionName.equals("PURCHASED") ? allPurchases.get(0) : allPurchases.get(1);

        // Return the number of rows in the section.
        return model.getElements().size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
        String sectionName = sectionNames.get((int) indexPath.getSection());
        MyModel model = sectionName.equals("PURCHASED") ? allPurchases.get(0) : allPurchases.get(1);
        List<SKPaymentTransaction> purchases = (List<SKPaymentTransaction>) model.getElements();

        UITableViewCell cell = tableView.dequeueReusableCell("purchasedID", indexPath);
        if (cell == null) {
            cell = new UITableViewCell(UITableViewCellStyle.Default, "purchasedID");
            cell.setSelectionStyle(UITableViewCellSelectionStyle.Blue);
            cell.setAccessoryType(UITableViewCellAccessoryType.DisclosureIndicator);
        }

        SKPaymentTransaction paymentTransaction = purchases.get((int) indexPath.getRow());
        String title = StoreManager.getInstance().getTitleForId(paymentTransaction.getPayment().getProductIdentifier());

        // Display the product's title associated with the payment's product
        // identifier if it exists or the product identifier,
        // otherwise
        cell.getTextLabel().setText(title != null ? title : paymentTransaction.getPayment().getProductIdentifier());

        return cell;
    }

    private NSDateFormatter newDateFormatter() {
        NSDateFormatter myDateFormatter = new NSDateFormatter();
        myDateFormatter.setDateStyle(NSDateFormatterStyle.Short);
        myDateFormatter.setTimeStyle(NSDateFormatterStyle.Short);
        return myDateFormatter;
    }

    @Override
    public void didSelectRow(UITableView tableView, NSIndexPath indexPath) {
        showDetails();
    }

    @SuppressWarnings("unchecked")
    private void showDetails() {
        String sectionName = sectionNames.get((int) getTableView().getIndexPathForSelectedRow().getSection());
        MyModel model = sectionName.equals("PURCHASED") ? allPurchases.get(0) : allPurchases.get(1);

        List<SKPaymentTransaction> purchases = (List<SKPaymentTransaction>) model.getElements();

        SKPaymentTransaction paymentTransaction = purchases.get((int) getTableView().getIndexPathForSelectedRow()
                .getRow());
        List<MyModel> purchaseDetails = new ArrayList<>();

        // Add the product identifier, transaction id, and transaction date to
        // purchaseDetails
        purchaseDetails.add(new MyModel("PRODUCT IDENTIFIER", paymentTransaction.getPayment().getProductIdentifier()));
        purchaseDetails.add(new MyModel("TRANSACTION ID", paymentTransaction.getTransactionIdentifier()));
        purchaseDetails.add(new MyModel("TRANSACTION DATE", newDateFormatter().format(
                paymentTransaction.getTransactionDate())));

        NSArray<SKDownload> allDownloads = paymentTransaction.getDownloads();
        // If this product is hosted, add its first download to purchaseDetails
        if (allDownloads.size() > 0) {
            // We are only showing the first download
            SKDownload firstDownload = allDownloads.first();

            Map<String, String> identifier = newKeyValuePair("Identifier", firstDownload.getContentIdentifier());
            Map<String, String> version = newKeyValuePair("Version", firstDownload.getContentVersion());
            Map<String, String> contentLength = newKeyValuePair("Length",
                    NSByteCountFormatter.format(firstDownload.getContentLength(), NSByteCountFormatterCountStyle.File));

            // Add the identifier, version, and length of a download to
            // purchaseDetails
            purchaseDetails.add(new MyModel("DOWNLOAD", identifier, version, contentLength));
        }

        // If the product is a restored one, add its original transaction's
        // transaction id and transaction date to purchaseDetails
        if (paymentTransaction.getOriginalTransaction() != null) {
            Map<String, String> transactionID = newKeyValuePair("Transaction ID", paymentTransaction
                    .getOriginalTransaction()
                    .getTransactionIdentifier());
            Map<String, String> transactionDate = newKeyValuePair("Transaction Date",
                    newDateFormatter().format(paymentTransaction.getOriginalTransaction().getTransactionDate()));

            purchaseDetails.add(new MyModel("ORIGINAL TRANSACTION", transactionID, transactionDate));
        }

        transactionDetails.setDetails(purchaseDetails);
        transactionDetails.setTitle(StoreManager.getInstance().getTitleForId(
                paymentTransaction.getPayment().getProductIdentifier()));
    }

    private Map<String, String> newKeyValuePair(String key, String value) {
        Map<String, String> pair = new HashMap<>();
        pair.put(key, value);
        return pair;
    }
}
