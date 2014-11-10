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

package org.robovm.samples.inapppurchases;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSNotificationCenter;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.storekit.SKProduct;
import org.robovm.apple.storekit.SKProductsRequest;
import org.robovm.apple.storekit.SKProductsRequestDelegate;
import org.robovm.apple.storekit.SKProductsResponse;
import org.robovm.apple.storekit.SKRequest;
import org.robovm.apple.storekit.SKRequestDelegate;

public class StoreManager extends NSObject implements SKRequestDelegate, SKProductsRequestDelegate {

    public enum IAPProductRequestStatus {
        /** Indicate that there are some valid products */
        ProductsFound,
        /** indicate that are some invalid product identifiers */
        IdentifiersNotFound,
        /** Indicate that the product request failed */
        RequestFailed
    }

    // Provide notification about the product request
    public static final NSString IAPProductRequestNotification = new NSString("IAPProductRequestNotification");

    // Singleton
    private static final StoreManager instance = new StoreManager();

    // Provide the status of the product request
    private IAPProductRequestStatus status;

    // Keep track of all valid products. These products are available for sale in the App Store
    private final List<SKProduct> availableProducts = new ArrayList<>();

    // Keep track of all invalid product identifiers
    private final List<String> invalidProductIds = new ArrayList<>();

    // Indicate the cause of the product request failure
    private String errorMessage;

    private StoreManager () {
    }

    public static StoreManager getInstance () {
        return instance;
    }

    /** Query the App Store about the given product identifiers
     * @param productIds */
    public void fetchProductInformationForIds (List<String> productIds) {
        // Create a product request object and initialize it with our product identifiers
        SKProductsRequest request = new SKProductsRequest(new HashSet<String>(productIds));
        request.setDelegate(this);

        // Send the request to the App Store
        request.start();
    }

    /** @param identifier
     * @return the product's title matching a given product identifier */
    public String getTitleForId (String identifier) {
        // Iterate through availableProducts to find the product whose productIdentifier
        // property matches identifier, return its localized title when found
        for (SKProduct product : availableProducts) {
            if (product.getProductIdentifier().equals(identifier)) {
                return product.getLocalizedTitle();
            }
        }
        return null;
    }

// SKProductsRequestDelegate

    /** Used to get the App Store's response to your request and notifies your observer */
    @Override
    public void didReceiveResponse (SKProductsRequest request, SKProductsResponse response) {
        // The products array contains products whose identifiers have been recognized by the App Store.
        // As such, they can be purchased. Add them to the availableProducts array.
        if (response.getProducts().size() > 0) {
            availableProducts.addAll(response.getProducts());
            status = IAPProductRequestStatus.ProductsFound;
            NSNotificationCenter.getDefaultCenter().postNotification(IAPProductRequestNotification, this);
        }

        // The invalidProductIdentifiers array contains all product identifiers not recognized by the App Store.
        // Add them to the invalidProducts array.
        if (response.getInvalidProductIdentifiers().size() > 0) {
            invalidProductIds.addAll(response.getInvalidProductIdentifiers());
            status = IAPProductRequestStatus.IdentifiersNotFound;
            NSNotificationCenter.getDefaultCenter().postNotification(IAPProductRequestNotification, this);
        }
    }

    // SKRequestDelegate

    @Override
    public void didFinish (SKRequest request) {
        // ignore
    }

    /** Called when the product request failed. */
    @Override
    public void didFail (SKRequest request, NSError error) {
        // Prints the cause of the product request failure
        System.out.println("Product Request Status: " + error.getLocalizedDescription());
    }

    public List<SKProduct> getAvailableProducts () {
        return availableProducts;
    }

    public List<String> getInvalidProductIds () {
        return invalidProductIds;
    }

    public IAPProductRequestStatus getStatus () {
        return status;
    }

    public String getErrorMessage () {
        return errorMessage;
    }
}
