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
import java.util.List;

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSFileManager;
import org.robovm.apple.foundation.NSNotificationCenter;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.storekit.SKDownload;
import org.robovm.apple.storekit.SKDownloadState;
import org.robovm.apple.storekit.SKErrorCode;
import org.robovm.apple.storekit.SKPayment;
import org.robovm.apple.storekit.SKPaymentQueue;
import org.robovm.apple.storekit.SKPaymentTransaction;
import org.robovm.apple.storekit.SKPaymentTransactionObserver;
import org.robovm.apple.storekit.SKProduct;

public class StoreObserver extends NSObject implements SKPaymentTransactionObserver {
    public enum IAPPurchaseNotificationStatus {
        PurchaseFailed, PurchaseSucceeded, RestoredFailed, RestoredSucceeded, DownloadStarted, DownloadInProgress, DownloadFailed, DownloadSucceeded
    }

    public static final NSString IAPPurchaseNotification = new NSString("IAPPurchaseNotification");
    private static StoreObserver instance = new StoreObserver();

    private IAPPurchaseNotificationStatus status;

    // Keep track of all purchases
    private final List<SKPaymentTransaction> productsPurchased = new ArrayList<>();
    // Keep track of all restored purchases
    private final List<SKPaymentTransaction> productsRestored = new ArrayList<>();

    private String message;

    private float downloadProgress;
    // Keep track of the purchased/restored product's identifier
    private String purchasedID;

    private StoreObserver () {
    }

    public static StoreObserver getInstance () {
        return instance;
    }

    /** @return whether there are purchased products */
    public boolean hasPurchasedProducts () {
        // productsPurchased keeps track of all our purchases.
        // Returns true if it contains some items and false, otherwise
        return productsPurchased.size() > 0;
    }

    /** @return whether there are restored purchases */
    public boolean hasRestoredProducts () {
        // productsRestored keeps track of all our restored purchases.
        // Returns true if it contains some items and false, otherwise
        return productsRestored.size() > 0;
    }

    /** Create and add a payment request to the payment queue.
     * @param product */
    public void buy (SKProduct product) {
        SKPayment payment = SKPayment.createFromProduct(product);
        SKPaymentQueue.getDefaultQueue().addPayment(payment);
    }

    /** Implement the restoration of previously completed purchases */
    public void restore () {
        productsRestored.clear();
        SKPaymentQueue.getDefaultQueue().restoreCompletedTransactions();
    }

    /** Called when there are trasactions in the payment queue.
     * @param queue
     * @param transactions */
    @Override
    public void updatedTransactions (SKPaymentQueue queue, NSArray<SKPaymentTransaction> transactions) {
        for (SKPaymentTransaction transaction : transactions) {

            switch (transaction.getTransactionState()) {
            case Purchasing:
                break;
            // The purchase was successful
            case Purchased:
                purchasedID = transaction.getPayment().getProductIdentifier();
                productsPurchased.add(transaction);

                System.out.println("Deliver content for " + transaction.getPayment().getProductIdentifier());

                // Check whether the purchased product has content hosted with Apple.
                if (transaction.getDownloads() != null && transaction.getDownloads().size() > 0) {
                    completeTransaction(transaction, IAPPurchaseNotificationStatus.DownloadStarted);
                } else {
                    completeTransaction(transaction, IAPPurchaseNotificationStatus.PurchaseSucceeded);
                }
                break;
            // There are restored products
            case Restored:
                purchasedID = transaction.getPayment().getProductIdentifier();
                productsRestored.add(transaction);

                System.out.println("Deliver content for " + transaction.getPayment().getProductIdentifier());

                // Send a IAPDownloadStarted notification if it has
                if (transaction.getDownloads() != null && transaction.getDownloads().size() > 0) {
                    completeTransaction(transaction, IAPPurchaseNotificationStatus.DownloadStarted);
                } else {
                    completeTransaction(transaction, IAPPurchaseNotificationStatus.RestoredSucceeded);
                }
                break;
            // The transaction failed
            case Failed:
                message = String.format("Purchase of %s failed.", transaction.getPayment().getProductIdentifier());
                completeTransaction(transaction, IAPPurchaseNotificationStatus.PurchaseFailed);
                break;
            default:
                break;
            }
        }
    }

    /** Logs all transactions that have been removed from the payment queue
     * @param queue
     * @param transactions */
    @Override
    public void removedTransactions (SKPaymentQueue queue, NSArray<SKPaymentTransaction> transactions) {
        for (SKPaymentTransaction transaction : transactions) {
            System.out.println(transaction.getPayment().getProductIdentifier() + " was removed from the payment queue.");
        }
    }

    /** Called when an error occur while restoring purchases. Notify the user about the error.
     * @param queue
     * @param error */
    @Override
    public void restoreCompletedTransactionsFailed (SKPaymentQueue queue, NSError error) {
        if (error.getErrorCode() != SKErrorCode.PaymentCancelled) {
            status = IAPPurchaseNotificationStatus.RestoredFailed;
            message = error.getLocalizedDescription();
            NSNotificationCenter.getDefaultCenter().postNotification(IAPPurchaseNotification, this);
        }
    }

    /** Called when all restorable transactions have been processed by the payment queue.
     * @param queu */
    @Override
    public void restoreCompletedTransactionsFinished (SKPaymentQueue queue) {
        System.out.println("All restorable transactions have been processed by the payment queue.");
    }

    /** Called when the payment queue has downloaded content.
     * @param queue
     * @param downloads */
    @Override
    public void updatedDownloads (SKPaymentQueue queue, NSArray<SKDownload> downloads) {
        for (SKDownload download : downloads) {
            switch (download.getDownloadState()) {
            // The content is being downloaded. Let's provide a download progress to the user
            case Active:
                status = IAPPurchaseNotificationStatus.DownloadInProgress;
                purchasedID = download.getTransaction().getPayment().getProductIdentifier();
                downloadProgress = downloadProgress * 100;
                NSNotificationCenter.getDefaultCenter().postNotification(IAPPurchaseNotification, this);
                break;
            case Cancelled:
                // StoreKit saves your downloaded content in the Caches directory. Let's remove it
                // before finishing the transaction.
                NSFileManager.getDefaultManager().removeItemAtURL(download.getContentURL(), null);
                finishDownloadTransaction(download.getTransaction());
                break;
            case Failed:
                // If a download fails, remove it from the Caches, then finish the transaction.
                // It is recommended to retry downloading the content in this case.
                NSFileManager.getDefaultManager().removeItemAtURL(download.getContentURL(), null);
                finishDownloadTransaction(download.getTransaction());
                break;
            case Paused:
                System.out.println("Download was paused");
                break;
            case Finished:
                // Download is complete. StoreKit saves the downloaded content in the Caches directory.
                System.out.println("Location of downloaded file " + download.getContentURL());
                finishDownloadTransaction(download.getTransaction());
                break;
            case Waiting:
                System.out.println("Download Waiting");
                SKPaymentQueue.getDefaultQueue().startDownloads(new NSArray<>(download));
                break;
            default:
                break;
            }
        }
    }

    /** Notify the user about the purchase process. Start the download process if status is IAPDownloadStarted. Finish all
     * transactions, otherwise.
     * @param transaction
     * @param status */
    private void completeTransaction (SKPaymentTransaction transaction, IAPPurchaseNotificationStatus status) {
        this.status = status;
        // Do not send any notifications when the user cancels the purchase
        if (transaction.getError().getErrorCode() != SKErrorCode.PaymentCancelled) {
            // Notify the user
            NSNotificationCenter.getDefaultCenter().postNotification(IAPPurchaseNotification, this);
        }

        if (status == IAPPurchaseNotificationStatus.DownloadStarted) {
            // The purchased product is a hosted one, let's download its content
            SKPaymentQueue.getDefaultQueue().startDownloads(transaction.getDownloads());
        } else {
            // Remove the transaction from the queue for purchased and restored statuses
            SKPaymentQueue.getDefaultQueue().finishTransaction(transaction);
        }
    }

    private void finishDownloadTransaction (SKPaymentTransaction transaction) {
        // allAssetsDownloaded indicates whether all content associated with the transaction were downloaded.
        boolean allAssetsDownloaded = true;

        // A download is complete if its state is SKDownloadStateCancelled, SKDownloadStateFailed, or SKDownloadStateFinished
        // and pending, otherwise. We finish a transaction if and only if all its associated downloads are complete.
        // For the SKDownloadStateFailed case, it is recommended to try downloading the content again before finishing the
        // transaction.
        for (SKDownload download : transaction.getDownloads()) {
            if (download.getDownloadState() != SKDownloadState.Cancelled && download.getDownloadState() != SKDownloadState.Failed
                && download.getDownloadState() != SKDownloadState.Finished) {
                // Let's break. We found an ongoing download. Therefore, there are still pending downloads.
                allAssetsDownloaded = false;
                break;
            }
        }

        // Finish the transaction and post a IAPDownloadSucceeded notification if all downloads are complete
        if (allAssetsDownloaded) {
            status = IAPPurchaseNotificationStatus.DownloadSucceeded;

            SKPaymentQueue.getDefaultQueue().finishTransaction(transaction);
            NSNotificationCenter.getDefaultCenter().postNotification(IAPPurchaseNotification, this);
        }
    }

    public IAPPurchaseNotificationStatus getStatus () {
        return status;
    }

    public String getMessage () {
        return message;
    }

    public String getPurchasedID () {
        return purchasedID;
    }

    public float getDownloadProgress () {
        return downloadProgress;
    }

    public List<SKPaymentTransaction> getProductsPurchased () {
        return productsPurchased;
    }

    public List<SKPaymentTransaction> getProductsRestored () {
        return productsRestored;
    }
}
