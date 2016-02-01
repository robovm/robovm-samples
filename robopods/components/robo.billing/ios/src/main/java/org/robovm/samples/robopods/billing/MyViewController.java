package org.robovm.samples.robopods.billing;

import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;

@CustomClass("MyViewController")
public class MyViewController extends UIViewController {

    @IBAction
    private void requestProductData() {
        AppStore.getInstance().requestProductData();
    }

    @IBAction
    private void purchaseConsumable() {
        AppStore.getInstance().purchaseProduct(AppStore.CONSUMABLE_PRODUCT1);
    }

    @IBAction
    private void purchaseNonConsumable() {
        AppStore.getInstance().purchaseProduct(AppStore.NONCONSUMABLE_PRODUCT1_IOS);
    }

    @IBAction
    private void restoreTransactions() {
        AppStore.getInstance().restoreTransactions();
    }
}
