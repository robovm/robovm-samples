/*
 * Copyright (C) 2016 RoboVM AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robovm.samples.robopods.billing;

import org.robovm.pods.billing.*;
import org.robovm.pods.dialog.AlertDialog;

import java.util.List;

public class AppStore {
    private static final String GOOGLE_PLAY_LICENSE_KEY = "YOUR_LICENSE_KEY";
    public static final String CONSUMABLE_PRODUCT1 = "org.robovm.robopods.billing.consumable1";
    public static final String NONCONSUMABLE_PRODUCT1_ANDROID = "org.robovm.robopods.android.product1";
    public static final String NONCONSUMABLE_PRODUCT1_IOS = "org.robovm.robopods.ios.product1";

    private static final AppStore instance = new AppStore();

    private final Store store;
    private boolean isSetup;

    private AppStore() {
        // Define the available products.
        ProductCatalog catalog = new ProductCatalog.Builder()
                // Start a new product with a universal identifier for all stores.
                .newProduct(CONSUMABLE_PRODUCT1)
                // Set product type (consumable, non-consumable, subscription).
                .setType(ProductType.CONSUMABLE)
                // Set default price.
                .setPrice(0.99, "USD")

                // Start a new product.
                .newProduct()
                // Add store-specific identifiers.
                .addIdentifier(StoreType.ANDROID_GOOGLE_PLAY, NONCONSUMABLE_PRODUCT1_ANDROID)
                .addIdentifier(StoreType.IOS_APP_STORE, NONCONSUMABLE_PRODUCT1_IOS)
                // Set product type (consumable, non-consumable, subscription).
                .setType(ProductType.NON_CONSUMABLE)
                // Set default price.
                .setPrice(1.99, "USD")
                .setTitle("A non-consumable product")

                .build();

        store = new Store.Builder()
                // Add the keys for the various stores. Not necessary for the iOS AppStore.
                .addStoreKey(StoreType.ANDROID_GOOGLE_PLAY, GOOGLE_PLAY_LICENSE_KEY)
                // Whether transactions should always be finished. Will only consume consumable products on Android.
                .setAutoFinishTransactions(true)
                // Set the product catalog.
                .setProductCatalog(catalog)
                // Add an observer to listen for billing callbacks.
                .addBillingObserver(new Observer())
                // Add a transaction verificator.
                .setTransactionVerificator(new TransactionVerificator() {
                    @Override
                    public void verify(Transaction transaction, VerificationCallback callback) {
                        // You could add server side verification here.

                        // Always call the callback when the verification is done.
                        callback.onResult(transaction, true, null);
                    }
                })
                .build();
    }

    public static AppStore getInstance() {
        return instance;
    }

    /**
     * Setup the store.
     */
    public void setup() {
        store.setup(new StoreSetupListener() {
            @Override
            public void onSuccess() {
                isSetup = true;
            }

            @Override
            public void onError(BillingError error) {
                // Setup failed. Billing not available.
                isSetup = false;
            }
        });
    }

    /**
     * @return true if the store is setup.
     */
    public boolean isSetup() {
        return isSetup;
    }

    /**
     * Request product data (prices, currency, availability).
     */
    public void requestProductData() {
        checkStoreIsSetup();

        store.requestProductData();
    }

    /**
     * Purchase the product with the specified id. Always request product data and check if the product is available first!!!
     *
     * @param productId
     */
    public void purchaseProduct(String productId) {
        purchaseProduct(getProduct(productId));
    }

    /**
     * Purchase the product. Always request product data and check if the product is available first!!!
     *
     * @param product
     */
    public void purchaseProduct(Product product) {
        checkStoreIsSetup();

        if (isProductAvailable(product)) {
            store.purchaseProduct(product);
        } else {
            new AlertDialog.Builder("Error!", "Product is not available!", "OK").show();
        }
    }

    /**
     * Restore already made transactions. Useful to restore non-consumable purchases.
     */
    public void restoreTransactions() {
        checkStoreIsSetup();

        store.restoreTransactions();
    }

    /**
     * Check if the product is available.
     *
     * @param productId
     * @return
     */
    public boolean isProductAvailable(String productId) {
        return isProductAvailable(getProduct(productId));
    }

    public boolean isProductAvailable(Product product) {
        return product.isAvailable();
    }

    public Product getProduct(String productId) {
        return store.getProductCatalog().getProduct(productId);
    }

    private boolean checkStoreIsSetup() {
        if (!isSetup) {
            new AlertDialog.Builder("Error!", "Store is not setup!", "OK").show();
        }
        return isSetup;
    }

    private class Observer implements BillingObserver {
        @Override
        public void onPurchaseSuccess(Transaction transaction) {
            // FIXME Reward the product to the user.

            // FIXME This is the perfect time to verify the transaction.

            // FIXME If you didn't set autoFinishTransactions to true, you will have to finish the transaction now, or store it and verify later.

            new AlertDialog.Builder("Success!", "You have just purchased: " + transaction.getProduct().getIdentifier(),
                    "OK").show();
        }

        @Override
        public void onPurchaseCancel() {}

        @Override
        public void onPurchaseError(Transaction transaction, BillingError error) {
            // FIXME Inform the user and retry the purchase.

            new AlertDialog.Builder("Error!", "An error happened during your purchase! Please contact support!", "OK")
                    .show();
        }

        @Override
        public void onProductsRequestSuccess(List<Product> products) {
            new AlertDialog.Builder("Success!", "The products have been requested! You can now make purchases!", "OK")
                    .show();
        }

        @Override
        public void onProductsRequestCancel() {}

        @Override
        public void onProductsRequestError(BillingError error) {
            // FIXME check the error reason and retry if possible.
            new AlertDialog.Builder("Error!", "Products request failed!", "OK").show();
        }

        @Override
        public void onRestoreSuccess(List<Transaction> restoredTransactions) {
            // FIXME Check the list of restored transactions and reward the products to the user.

            new AlertDialog.Builder("Success!", "You have restored " + restoredTransactions.size() + " transactions!",
                    "OK").show();
        }

        @Override
        public void onRestoreCancel() {}

        @Override
        public void onRestoreError(BillingError error) {
            new AlertDialog.Builder("Error!", "The restore failed! Please retry!", "OK").show();
        }
    }
}
