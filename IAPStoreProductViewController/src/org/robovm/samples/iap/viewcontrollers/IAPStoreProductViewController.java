/*
 * Copyright (C) 2014 Trillian Mobile AB
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

package org.robovm.samples.iap.viewcontrollers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.storekit.SKStoreProductParameters;
import org.robovm.apple.storekit.SKStoreProductViewController;
import org.robovm.apple.storekit.SKStoreProductViewControllerDelegate;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellSelectionStyle;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.apple.uikit.UITableViewStyle;
import org.robovm.objc.block.VoidBlock2;
import org.robovm.samples.iap.Product;

public class IAPStoreProductViewController extends UITableViewController implements SKStoreProductViewControllerDelegate {
    // Height for the Audio Books row
    private static final double UIAudioBooksRowHeight = 81.0;

    private final List<Product> myProducts = new ArrayList<>();
    private final SKStoreProductViewController storeProductViewController;

    @SuppressWarnings("unchecked")
    public IAPStoreProductViewController () {
        getNavigationItem().setTitle("Store");

        // Create a store product view controller
        storeProductViewController = new SKStoreProductViewController();
        storeProductViewController.setDelegate(this);

        // Fetch all the products
        String plistPath = NSBundle.getMainBundle().findResourcePath("Products", "plist");
        NSArray<NSDictionary<NSString, NSObject>> temp = (NSArray<NSDictionary<NSString, NSObject>>)NSArray.read(new File(
            plistPath));

        for (NSDictionary<NSString, NSObject> item : temp) {
            // Create an Product object to store its category, title, and identifier properties
            Product product = new Product(item);

            // Keep track of all the products
            myProducts.add(product);
        }

        UITableView tableView = new UITableView(UIScreen.getMainScreen().getApplicationFrame(), UITableViewStyle.Grouped);
        tableView.registerReusableCellClass(UITableViewCell.class, "productID");
        setTableView(tableView);
    }

    @Override
    public long getNumberOfSections (UITableView tableView) {
        // Return the number of sections
        return myProducts.size();
    }

    @Override
    public String getTitleForHeader (UITableView tableView, long section) {
        Product item = myProducts.get((int)section);
        // Return the title of the section header
        return item.getCategory();
    }

    @Override
    public double getHeightForRow (UITableView tableView, NSIndexPath indexPath) {
        Product item = myProducts.get((int)indexPath.getSection());
        // Change the height if "AUDIO BOOKS" is the specified row
        return item.getCategory().equals("AUDIO BOOKS") ? UIAudioBooksRowHeight : tableView.getRowHeight();
    }

    @Override
    public long getNumberOfRowsInSection (UITableView tableView, long section) {
        return 1;
    }

    @Override
    public UITableViewCell getCellForRow (UITableView tableView, NSIndexPath indexPath) {
        UITableViewCell cell = tableView.dequeueReusableCell("productID", indexPath);
        if (cell == null) {
            cell = new UITableViewCell(UITableViewCellStyle.Default, "productID");
            cell.setSelectionStyle(UITableViewCellSelectionStyle.Blue);
        }

        Product item = myProducts.get((int)indexPath.getSection());
        cell.getTextLabel().setText(item.getTitle());

        return cell;
    }

    /** Loads and launches a store product view controller with a selected product
     * @param tableView
     * @param indexPath */
    @Override
    public void didSelectRow (UITableView tableView, NSIndexPath indexPath) {
        Product item = myProducts.get((int)indexPath.getSection());

        // Create a product dictionary using the selected product's iTunes identifer
        SKStoreProductParameters parameters = new SKStoreProductParameters(item.getProductID());

        // Attempt to load the selected product from the App Store, display the store product view controller if success
        // and print an error message, otherwise.
        storeProductViewController.loadProduct(parameters, new VoidBlock2<Boolean, NSError>() {
            @Override
            public void invoke (Boolean result, NSError error) {
                if (result) {
                    presentViewController(storeProductViewController, true, null);
                } else {
                    System.err.println("Error message: " + error.getLocalizedDescription());
                }
            }
        });
    }

    /** Used to dismiss the store view controller
     * @param viewController */
    @Override
    public void didFinish (SKStoreProductViewController viewController) {
        viewController.getPresentingViewController().dismissViewController(true, null);
    }

}
