package org.robovm.samples.robopods.dialog;

import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;

@CustomClass("MyViewController")
public class MyViewController extends UIViewController {

    @IBAction
    private void alertButtonTapped() {
        DialogHandler.getInstance().showAlertDialogSample();
    }

    @IBAction
    private void inputButtonTapped() {
        DialogHandler.getInstance().showInputDialogSample();
    }

    @IBAction
    private void progressButtonTapped() {
        DialogHandler.getInstance().showProgressDialogSample();
    }
}
