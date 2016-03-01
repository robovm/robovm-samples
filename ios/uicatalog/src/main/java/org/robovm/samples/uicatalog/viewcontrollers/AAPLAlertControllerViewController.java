/*
 * Copyright (C) 2013-2015 RoboVM AB
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
 * Portions of this code is based on Apple Inc's UICatalog sample (v11.3)
 * which is copyright (C) 2008-2015 Apple Inc.
 */
package org.robovm.samples.uicatalog.viewcontrollers;

import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSNotificationCenter;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.uikit.UIAlertAction;
import org.robovm.apple.uikit.UIAlertActionStyle;
import org.robovm.apple.uikit.UIAlertController;
import org.robovm.apple.uikit.UIAlertControllerStyle;
import org.robovm.apple.uikit.UIPopoverArrowDirection;
import org.robovm.apple.uikit.UIPopoverPresentationController;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.apple.uikit.UITextField;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.block.VoidBlock1;

@CustomClass("AAPLAlertControllerViewController")
public class AAPLAlertControllerViewController extends UITableViewController {
    // Corresponds to the section index of the table view (whether we want to
    // show an alert or action sheet style).
    private enum Section {
        Alert, ActionSheet
    }

    // Corresponds to the row in the alert style section.
    private enum AlertRow {
        Simple, OkayCancel, Other, TextEntry, TextEntrySecure
    }

    // Corresponds to the row in the action sheet style section.
    private enum ActionSheetRow {
        OkayCancel, Other
    }

    // Maintains a reference to the alert action that should be toggled when the
    // text field changes (for the secure text entry alert).
    private UIAlertAction secureTextAlertAction;
    private NSObject textChangeNotification;

    /**
     * Show an alert with an "Okay" button.
     */
    private void showSimpleAlert() {
        String title = "A Short Title Is Best";
        String message = "A message should be a short, complete sentence.";
        String cancelButtonTitle = "OK";

        UIAlertController alertController = new UIAlertController(title, message, UIAlertControllerStyle.Alert);

        // Create the action.
        UIAlertAction cancelAction = new UIAlertAction(cancelButtonTitle, UIAlertActionStyle.Cancel,
                new VoidBlock1<UIAlertAction>() {
                    @Override
                    public void invoke(UIAlertAction a) {
                        System.out.println("The simple alert's cancel action occured.");
                    }
                });

        // Add the action.
        alertController.addAction(cancelAction);

        presentViewController(alertController, true, null);
    }

    /**
     * Show an alert with an "Okay" and "Cancel" button.
     */
    private void showOkayCancelAlert() {
        String title = "A Short Title Is Best";
        String message = "A message should be a short, complete sentence.";
        String cancelButtonTitle = "Cancel";
        String otherButtonTitle = "OK";

        UIAlertController alertController = new UIAlertController(title, message, UIAlertControllerStyle.Alert);

        // Create the actions.
        UIAlertAction cancelAction = new UIAlertAction(cancelButtonTitle, UIAlertActionStyle.Cancel,
                new VoidBlock1<UIAlertAction>() {
                    @Override
                    public void invoke(UIAlertAction a) {
                        System.out.println("The \"Okay/Cancel\" alert's cancel action occured.");
                    }
                });

        UIAlertAction otherAction = new UIAlertAction(otherButtonTitle, UIAlertActionStyle.Default,
                new VoidBlock1<UIAlertAction>() {
                    @Override
                    public void invoke(UIAlertAction a) {
                        System.out.println("The \"Okay/Cancel\" alert's other action occured.");
                    }
                });

        // Add the actions.
        alertController.addAction(cancelAction);
        alertController.addAction(otherAction);

        presentViewController(alertController, true, null);
    }

    // Show an alert with two custom buttons.
    private void showOtherAlert() {
        String title = "A Short Title Is Best";
        String message = "A message should be a short, complete sentence.";
        String cancelButtonTitle = "Cancel";
        String otherButtonTitleOne = "Choice One";
        String otherButtonTitleTwo = "Choice Two";

        UIAlertController alertController = new UIAlertController(title, message, UIAlertControllerStyle.Alert);

        // Create the actions.
        UIAlertAction cancelAction = new UIAlertAction(cancelButtonTitle, UIAlertActionStyle.Cancel,
                new VoidBlock1<UIAlertAction>() {
                    @Override
                    public void invoke(UIAlertAction a) {
                        System.out.println("The \"Other\" alert's cancel action occured.");
                    }
                });

        UIAlertAction otherButtonOneAction = new UIAlertAction(otherButtonTitleOne, UIAlertActionStyle.Default,
                new VoidBlock1<UIAlertAction>() {
                    @Override
                    public void invoke(UIAlertAction a) {
                        System.out.println("The \"Other\" alert's other button one action occured.");
                    }
                });

        UIAlertAction otherButtonTwoAction = new UIAlertAction(otherButtonTitleTwo, UIAlertActionStyle.Default,
                new VoidBlock1<UIAlertAction>() {
                    @Override
                    public void invoke(UIAlertAction a) {
                        System.out.println("The \"Other\" alert's other button two action occured.");
                    }
                });

        // Add the actions.
        alertController.addAction(cancelAction);
        alertController.addAction(otherButtonOneAction);
        alertController.addAction(otherButtonTwoAction);

        presentViewController(alertController, true, null);
    }

    /**
     * Show a text entry alert with two custom buttons.
     */
    private void showTextEntryAlert() {
        String title = "A Short Title Is Best";
        String message = "A message should be a short, complete sentence.";
        String cancelButtonTitle = "Cancel";
        String otherButtonTitle = "OK";

        UIAlertController alertController = new UIAlertController(title, message, UIAlertControllerStyle.Alert);

        // Add the text field for text entry.
        alertController.addTextField(new VoidBlock1<UITextField>() {
            @Override
            public void invoke(UITextField a) {
                // If you need to customize the text field, you can do so here.
            }
        });

        // Create the actions.
        UIAlertAction cancelAction = new UIAlertAction(cancelButtonTitle, UIAlertActionStyle.Cancel,
                new VoidBlock1<UIAlertAction>() {
                    @Override
                    public void invoke(UIAlertAction a) {
                        System.out.println("The \"Text Entry\" alert's cancel action occured.");
                    }
                });

        UIAlertAction otherAction = new UIAlertAction(otherButtonTitle, UIAlertActionStyle.Default,
                new VoidBlock1<UIAlertAction>() {
                    @Override
                    public void invoke(UIAlertAction textField) {
                        System.out.println("The \"Text Entry\" alert's other action occured.");
                    }
                });

        // Add the actions.
        alertController.addAction(cancelAction);
        alertController.addAction(otherAction);

        presentViewController(alertController, true, null);
    }

    // Show a secure text entry alert with two custom buttons.
    private void showSecureTextEntryAlert() {
        String title = "A Short Title Is Best";
        String message = "A message should be a short, complete sentence.";
        String cancelButtonTitle = "Cancel";
        String otherButtonTitle = "OK";

        UIAlertController alertController = new UIAlertController(title, message, UIAlertControllerStyle.Alert);

        // Add the text field for the secure text entry.
        alertController.addTextField(new VoidBlock1<UITextField>() {
            @Override
            public void invoke(UITextField textField) {
                // Listen for changes to the text field's text so that we can
                // toggle the current
                // action's enabled property based on whether the user has
                // entered a sufficiently
                // secure entry.
                textField.setSecureTextEntry(true);

                textChangeNotification = UITextField.Notifications.observeTextDidChange(textField,
                        new VoidBlock1<UITextField>() {
                            @Override
                            public void invoke(UITextField textField) {
                                // Enforce a minimum length of >= 5 characters
                                // for secure text alerts.
                                secureTextAlertAction.setEnabled(textField.getText().length() >= 5);
                            }
                        });
            }
        });

        // Create the actions.
        UIAlertAction cancelAction = new UIAlertAction(cancelButtonTitle, UIAlertActionStyle.Cancel,
                new VoidBlock1<UIAlertAction>() {
                    @Override
                    public void invoke(UIAlertAction a) {
                        System.out.println("The \"Secure Text Entry\" alert's cancel action occured.");

                        // Stop listening for text changed notifications.
                        NSNotificationCenter.getDefaultCenter().removeObserver(textChangeNotification);
                    }
                });

        UIAlertAction otherAction = new UIAlertAction(otherButtonTitle, UIAlertActionStyle.Default,
                new VoidBlock1<UIAlertAction>() {
                    @Override
                    public void invoke(UIAlertAction a) {
                        System.out.println("The \"Secure Text Entry\" alert's other action occured.");

                        // Stop listening for text changed notifications.
                        NSNotificationCenter.getDefaultCenter().removeObserver(textChangeNotification);
                    }
                });

        // The text field initially has no text in the text field, so we'll
        // disable it.
        otherAction.setEnabled(false);

        // Hold onto the secure text alert action to toggle the enabled/disabled
        // state when the text changed.
        secureTextAlertAction = otherAction;

        // Add the actions.
        alertController.addAction(cancelAction);
        alertController.addAction(otherAction);

        presentViewController(alertController, true, null);
    }

    /**
     * Show a dialog with an "Okay" and "Cancel" button.
     * 
     * @param selectedPath
     */
    private void showOkayCancelActionSheet(NSIndexPath selectedPath) {
        String cancelButtonTitle = "Cancel";
        String destructiveButtonTitle = "OK";

        UIAlertController alertController = new UIAlertController(null, null, UIAlertControllerStyle.ActionSheet);

        // Create the actions.
        UIAlertAction cancelAction = new UIAlertAction(cancelButtonTitle, UIAlertActionStyle.Cancel,
                new VoidBlock1<UIAlertAction>() {
                    @Override
                    public void invoke(UIAlertAction a) {
                        System.out.println("The \"Okay/Cancel\" alert action sheet's cancel action occured.");
                    }
                });

        UIAlertAction destructiveAction = new UIAlertAction(destructiveButtonTitle, UIAlertActionStyle.Destructive,
                new VoidBlock1<UIAlertAction>() {
                    @Override
                    public void invoke(UIAlertAction a) {
                        System.out.println("The \"Okay/Cancel\" alert action sheet's destructive action occured.");
                    }
                });

        // Add the actions.
        alertController.addAction(cancelAction);
        alertController.addAction(destructiveAction);

        // Configure the alert controller's popover presentation controller if
        // it has one.
        UIPopoverPresentationController popoverPresentationController = alertController
                .getPopoverPresentationController();
        if (popoverPresentationController != null) {
            UITableViewCell selectedCell = getTableView().getCellForRow(selectedPath);
            popoverPresentationController.setSourceRect(selectedCell.getFrame());
            popoverPresentationController.setSourceView(getView());
            popoverPresentationController.setPermittedArrowDirections(UIPopoverArrowDirection.Up);
        }

        presentViewController(alertController, true, null);
    }

    /**
     * Show a dialog with two custom buttons.
     * 
     * @param selectedPath
     */
    private void showOtherActionSheet(NSIndexPath selectedPath) {
        String destructiveButtonTitle = "Destructive Choice";
        String otherButtonTitle = "Safe Choice";

        UIAlertController alertController = new UIAlertController(null, null, UIAlertControllerStyle.ActionSheet);

        // Create the actions.
        UIAlertAction destructiveAction = new UIAlertAction(destructiveButtonTitle, UIAlertActionStyle.Destructive,
                new VoidBlock1<UIAlertAction>() {
                    @Override
                    public void invoke(UIAlertAction a) {
                        System.out.println("The \"Other\" alert action sheet's destructive action occured.");
                    }
                });

        UIAlertAction otherAction = new UIAlertAction(otherButtonTitle, UIAlertActionStyle.Default,
                new VoidBlock1<UIAlertAction>() {
                    @Override
                    public void invoke(UIAlertAction a) {
                        System.out.println("The \"Other\" alert action sheet's other action occured.");
                    }
                });

        // Add the actions.
        alertController.addAction(destructiveAction);
        alertController.addAction(otherAction);

        // Configure the alert controller's popover presentation controller if
        // it has one.
        UIPopoverPresentationController popoverPresentationController = alertController
                .getPopoverPresentationController();
        if (popoverPresentationController != null) {
            UITableViewCell selectedCell = getTableView().getCellForRow(selectedPath);
            popoverPresentationController.setSourceRect(selectedCell.getFrame());
            popoverPresentationController.setSourceView(getView());
            popoverPresentationController.setPermittedArrowDirections(UIPopoverArrowDirection.Up);
        }

        presentViewController(alertController, true, null);
    }

    // Determine the action to perform based on the selected cell.
    @Override
    public void didSelectRow(UITableView tableView, NSIndexPath indexPath) {
        Section section = Section.values()[indexPath.getSection()];

        if (section == Section.Alert) {
            AlertRow row = AlertRow.values()[indexPath.getRow()];

            switch (row) {
            case Simple:
                showSimpleAlert();
                break;
            case OkayCancel:
                showOkayCancelAlert();
                break;
            case Other:
                showOtherAlert();
                break;
            case TextEntry:
                showTextEntryAlert();
                break;
            case TextEntrySecure:
                showSecureTextEntryAlert();
                break;
            default:
                break;
            }
        } else if (section == Section.ActionSheet) {
            ActionSheetRow row = ActionSheetRow.values()[indexPath.getRow()];

            switch (row) {
            case OkayCancel:
                showOkayCancelActionSheet(indexPath);
                break;
            case Other:
                showOtherActionSheet(indexPath);
                break;
            default:
                break;
            }
        }

        tableView.deselectRow(indexPath, true);
    }
}
