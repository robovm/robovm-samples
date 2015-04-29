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

import java.util.concurrent.TimeUnit;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.dispatch.DispatchQueue;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSNotification;
import org.robovm.apple.foundation.NSNotificationCenter;
import org.robovm.apple.foundation.NSOperationQueue;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIAlertView;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonItemStyle;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UISegmentedControl;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.block.VoidBlock1;
import org.robovm.samples.inapppurchases.StoreManager;
import org.robovm.samples.inapppurchases.StoreObserver;
import org.robovm.samples.inapppurchases.StoreObserver.IAPPurchaseNotificationStatus;

public class ParentViewController extends UIViewController {
    private boolean hasDownloadContent;
    private final UIView containerView;
    private final IOSProductsList productsList;
    private final IOSPurchasesList purchasesList;
    private final UILabel statusMessage;
    private final UISegmentedControl segmentedControl;

    public ParentViewController() {
        NSNotificationCenter.getDefaultCenter().addObserver(StoreObserver.IAPPurchaseNotification,
                StoreObserver.getInstance(),
                NSOperationQueue.getMainQueue(), new VoidBlock1<NSNotification>() {
                    @Override
                    public void invoke(NSNotification a) {
                        handlePurchasesNotification(a);
                    }
                });

        segmentedControl = new UISegmentedControl(NSArray.fromStrings("Products", "Purchases"));
        segmentedControl.setSelectedSegment(0);
        segmentedControl.addOnValueChangedListener(new UIControl.OnValueChangedListener() {
            @Override
            public void onValueChanged(UIControl control) {
                segmentValueChanged(control);
            }
        });
        getNavigationItem().setTitleView(segmentedControl);

        getNavigationItem().setRightBarButtonItem(
                new UIBarButtonItem("Restore", UIBarButtonItemStyle.Done, new UIBarButtonItem.OnClickListener() {
                    @Override
                    public void onClick(UIBarButtonItem barButtonItem) {
                        restore();
                    }
                }));

        UIView view = getView();
        view.setBackgroundColor(UIColor.white());

        statusMessage = new UILabel(new CGRect(0, 64, 320, 44));
        statusMessage.setTextAlignment(NSTextAlignment.Center);
        statusMessage.setFont(UIFont.getSystemFont(14));

        containerView = new UIView(UIScreen.getMainScreen().getApplicationFrame());
        view.addSubview(containerView);

        productsList = new IOSProductsList();
        purchasesList = new IOSPurchasesList();

        // Add iOSProductsList and iOSPurchasesList as child view controllers
        addChildViewController(productsList);
        productsList.didMoveToParentViewController(this);
        addChildViewController(purchasesList);
        purchasesList.didMoveToParentViewController(this);

        // iOSProductsList is the default child view controller
        cycleViewControllers(null, productsList);
    }

    @Override
    public void viewDidLayoutSubviews() {
        CGRect contentFrame = containerView.getFrame();
        CGRect messageFrame = statusMessage.getFrame();

        // Add the status message to the UI if a download is in progress.
        // Remove it when the download is done
        if (hasDownloadContent) {
            messageFrame = new CGRect(contentFrame.getOrigin().getX(), contentFrame.getOrigin().getY(), contentFrame
                    .getSize()
                    .getWidth(), 44);
            contentFrame.getSize().setHeight(contentFrame.getSize().getHeight() - messageFrame.getSize().getHeight());
            contentFrame.getOrigin().setY(contentFrame.getOrigin().getY() + messageFrame.getSize().getHeight());
        } else {
            contentFrame = getView().getFrame();
            // We need to account for the navigation bar
            contentFrame.getOrigin().setY(64);
            contentFrame.getSize().setHeight(contentFrame.getSize().getHeight() - contentFrame.getOrigin().getY());
            messageFrame.getOrigin().setY(getView().getFrame().getSize().getHeight());
        }

        containerView.setFrame(contentFrame);
        statusMessage.setFrame(messageFrame);
    }

    /**
     * Called when the status message was removed. Force the view to update its
     * layout.
     */
    private void hideStatusMessage() {
        getView().setNeedsLayout();
    }

    private void alert(String title, String message) {
        UIAlertView alertView = new UIAlertView(title, message, null, "OK");
        alertView.show();
    }

    /**
     * Update the UI according to the notification result
     * 
     * @param notification
     */
    private void handlePurchasesNotification(NSNotification notification) {
        StoreObserver purchasesNotification = (StoreObserver) notification.getObject();
        IAPPurchaseNotificationStatus status = purchasesNotification.getStatus();

        switch (status) {
        case PurchaseSucceeded:
            String title = StoreManager.getInstance().getTitleForId(purchasesNotification.getPurchasedID());

            // Display the product's title associated with the payment's product
            // identifier if it exists or the product
            // identifier, otherwise.
            String displayedTitle = title != null ? title : purchasesNotification.getPurchasedID();
            alert("Purchase Status", displayedTitle + " was successfully purchased.");
            break;
        case PurchaseFailed:
            alert("Purchase Status", purchasesNotification.getMessage());
            break;
        // Switch to the iOSPurchasesList view controller when receiving a
        // successful restore notification
        case RestoredSucceeded:
            // Get the view controller currently displayed
            UIViewController selectedController = getViewControllerForSelectedIndex((int) segmentedControl
                    .getSelectedSegment());
            segmentedControl.setSelectedSegment(1);
            cycleViewControllers(selectedController, purchasesList);
            break;
        case RestoredFailed:
            alert("Purchase Status", purchasesNotification.getMessage());
            break;
        // Notify the user that downloading is about to start when receiving a
        // download started notification
        case DownloadStarted:
            hasDownloadContent = true;
            getView().addSubview(statusMessage);
            break;
        // Display a status message showing the download progress
        case DownloadInProgress:
            hasDownloadContent = true;
            title = StoreManager.getInstance().getTitleForId(purchasesNotification.getPurchasedID());
            displayedTitle = title.length() > 0 ? title : purchasesNotification.getPurchasedID();
            statusMessage.setText(String.format("Downloading %s %.2f%%", displayedTitle,
                    purchasesNotification.getDownloadProgress()));
            break;
        // Downloading is done, remove the status message
        case DownloadSucceeded:
            hasDownloadContent = false;
            statusMessage.setText("Download complete: 100%");

            // Remove the message after 2 seconds
            DispatchQueue.getMainQueue().after(2, TimeUnit.SECONDS, new Runnable() {
                @Override
                public void run() {
                    hideStatusMessage();
                }
            });
            break;
        default:
            break;
        }
    }

    /**
     * Transition from the old view controller to the new one.
     * 
     * @param oldViewController
     * @param newViewController
     */
    private void cycleViewControllers(UIViewController oldViewController, UIViewController newViewController) {
        if (newViewController == null)
            throw new NullPointerException("newViewController");
        if (oldViewController != null) {
            oldViewController.getView().removeFromSuperview();
        }

        CGRect frame = newViewController.getView().getFrame();
        frame.getSize().setWidth(containerView.getFrame().getWidth());
        frame.getSize().setHeight(containerView.getFrame().getHeight());
        newViewController.getView().setFrame(frame);
        containerView.addSubview(newViewController.getView());
    }

    /**
     * @param index
     * @return the view controller associated with the segmented control's
     *         selected index.
     */
    private UIViewController getViewControllerForSelectedIndex(int index) {
        UIViewController viewController = null;
        switch (index) {
        case 0:
            viewController = productsList;
            break;
        case 1:
            viewController = purchasesList;
            break;
        default:
            break;
        }
        return viewController;
    }

    /**
     * Called when a user taps on the segmented control
     * 
     * @param sender
     */
    private void segmentValueChanged(UIControl sender) {
        UISegmentedControl segControl = (UISegmentedControl) sender;

        // Return productsList if the user tapped Products in the segmented
        // control and purchasesList, otherwise
        UIViewController newViewController = segControl.getSelectedSegment() == 0 ? productsList : purchasesList;

        // Return purchasesList if the user tapped Purchases in the segmented
        // control and productsList, otherwise
        UIViewController oldViewController = segControl.getSelectedSegment() == 1 ? purchasesList : productsList;

        cycleViewControllers(oldViewController, newViewController);
    }

    private void restore() {
        // Call StoreObserver to restore all restorable purchases
        StoreObserver.getInstance().restore();
    }

    @Override
    protected void dispose(boolean finalizing) {
        NSNotificationCenter.getDefaultCenter().removeObserver(this, StoreObserver.IAPPurchaseNotification,
                StoreObserver.getInstance());
        super.dispose(finalizing);
    }
}
