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

package org.robovm.samples.inapppurchases.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSNotification;
import org.robovm.apple.foundation.NSNotificationCenter;
import org.robovm.apple.foundation.NSOperationQueue;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.storekit.SKPaymentQueue;
import org.robovm.apple.storekit.SKProduct;
import org.robovm.apple.uikit.UIAlertView;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellSelectionStyle;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.objc.block.VoidBlock1;
import org.robovm.samples.inapppurchases.MyModel;
import org.robovm.samples.inapppurchases.StoreManager;
import org.robovm.samples.inapppurchases.StoreManager.IAPProductRequestStatus;
import org.robovm.samples.inapppurchases.StoreObserver;

public class IOSProductsList extends UITableViewController {
    private final List<MyModel> products = new ArrayList<>();
    private final List<String> sectionNames = new ArrayList<>();

    public IOSProductsList() {
        // Register for StoreManager's notifications
        NSNotificationCenter.getDefaultCenter().addObserver(StoreManager.IAPProductRequestNotification,
                StoreManager.getInstance(), NSOperationQueue.getMainQueue(), new VoidBlock1<NSNotification>() {
                    @Override
                    public void invoke(NSNotification a) {
                        handleProductRequestNotification(a);
                    }
                });

        // The tableview is organized into 2 sections: AVAILABLE PRODUCTS and
        // INVALID PRODUCT IDS
        products.add(new MyModel("AVAILABLE PRODUCTS"));
        products.add(new MyModel("INVALID PRODUCT IDS"));

        fetchProductInformation();

        getTableView().registerReusableCellClass(UITableViewCell.class, "availableProductID");
        getTableView().registerReusableCellClass(UITableViewCell.class, "invalidIdentifierID");
    }

    /** Retrieve product information from the App Store */
    @SuppressWarnings("unchecked")
    private void fetchProductInformation() {
        // Query the App Store for product information if the user is is allowed
        // to make purchases.
        // Display an alert, otherwise.
        if (SKPaymentQueue.canMakePayments()) {
            // Load the product identifiers fron ProductIds.plist
            String filePath = NSBundle.getMainBundle().findResourcePath("ProductIds", "plist");
            NSArray<NSString> productIds = (NSArray<NSString>) NSArray.read(new File(filePath));

            StoreManager.getInstance().fetchProductInformationForIds(productIds.asStringList());
        } else {
            // Warn the user that they are not allowed to make purchases.
            alert("Warning", "Purchases are disabled on this device.");
        }
    }

    /**
     * Update the UI according to the notification result
     * 
     * @param notification
     */
    private void handleProductRequestNotification(NSNotification notification) {
        MyModel model = null;
        StoreManager productRequestNotification = (StoreManager) notification.getObject();
        IAPProductRequestStatus result = productRequestNotification.getStatus();

        sectionNames.clear();

        switch (result) {
        // The App Store has recognized some identifiers and returned their
        // matching products.
        case ProductsFound:
            model = products.get(0);
            model.setElements(productRequestNotification.getAvailableProducts());
            // Keep track of the position of the AVAILABLE PRODUCTS section
            sectionNames.add("AVAILABLE PRODUCTS");
            break;
        // Some product identifiers were not recognized by the App Store
        case IdentifiersNotFound:
            model = products.get(1);
            model.setElements(productRequestNotification.getInvalidProductIds());
            // Keep track of the position of the INVALID PRODUCT IDS section
            sectionNames.add("INVALID PRODUCT IDS");
            break;
        default:
            break;
        }
        // Reload the tableview to update it
        if (model != null) {
            getTableView().reloadData();
        }
    }

    /**
     * Display an alert with a given title and message
     * 
     * @param title
     * @param message
     */
    private void alert(String title, String message) {
        UIAlertView alertView = new UIAlertView(title, message, null, "OK");
        alertView.show();
    }

    @Override
    public long getNumberOfSections(UITableView tableView) {
        int numberOfSections = 0;

        if (!products.get(0).isEmpty()) {
            numberOfSections++;
        }
        if (!products.get(1).isEmpty()) {
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
        MyModel model = sectionName.equals("AVAILABLE PRODUCTS") ? products.get(0) : products.get(1);

        // Return the header title for this section
        return !model.isEmpty() ? model.getName() : null;
    }

    @Override
    public long getNumberOfRowsInSection(UITableView tableView, long section) {
        String sectionName = sectionNames.get((int) section);
        MyModel model = sectionName.equals("AVAILABLE PRODUCTS") ? products.get(0) : products.get(1);

        // Return the number of rows in the section.
        return model.getElements().size();
    }

    @Override
    public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
        String sectionName = sectionNames.get((int) indexPath.getSection());

        MyModel model = sectionName.equals("AVAILABLE PRODUCTS") ? products.get(0) : products.get(1);

        List<?> productRequestResponse = model.getElements();

        if (model.getName().equals("AVAILABLE PRODUCTS")) {
            UITableViewCell cell = tableView.dequeueReusableCell("availableProductID", indexPath);
            if (cell == null) {
                cell = new UITableViewCell(UITableViewCellStyle.Value1, "availableProductID");
                cell.setSelectionStyle(UITableViewCellSelectionStyle.Blue);
            }

            SKProduct product = (SKProduct) productRequestResponse.get((int) indexPath.getRow());
            // Show the localized title of the product
            cell.getTextLabel().setText(product.getLocalizedTitle());
            // Show the product's price in the locale and currency returned by
            // the App Store
            cell.getDetailTextLabel().setText(
                    String.format("%s %.02f", product.getPriceLocale().getCurrencySymbol(), product.getPrice()
                            .floatValue()));

            return cell;
        } else {
            UITableViewCell cell = tableView.dequeueReusableCell("invalidIdentifierID", indexPath);
            if (cell == null) {
                cell = new UITableViewCell(UITableViewCellStyle.Default, "invalidIdentifierID");
            }
            cell.getTextLabel().setText((String) productRequestResponse.get((int) indexPath.getRow()));
            cell.setSelectionStyle(UITableViewCellSelectionStyle.None);
            cell.getTextLabel().setTextColor(UIColor.gray());
            return cell;
        }
    }

    /**
     * Start a purchase when the user taps a row.
     * 
     * @param tableView
     * @param indexPath
     */
    @Override
    public void didSelectRow(UITableView tableView, NSIndexPath indexPath) {
        // Only items in the first section of the table can be bought
        if (tableView.getNumberOfSections() == 2 && indexPath.getSection() == 0) {
            MyModel model = products.get(0);
            List<?> productRequestResponse = model.getElements();

            SKProduct product = (SKProduct) productRequestResponse.get((int) indexPath.getRow());
            // Attempt to purchase the tapped product
            StoreObserver.getInstance().buy(product);
        }
    }

    @Override
    protected void dispose(boolean finalizing) {
        // Unregister for StoreManager's notifications
        NSNotificationCenter.getDefaultCenter().removeObserver(this, StoreManager.IAPProductRequestNotification,
                StoreManager.getInstance());
        super.dispose(finalizing);
    }

}
