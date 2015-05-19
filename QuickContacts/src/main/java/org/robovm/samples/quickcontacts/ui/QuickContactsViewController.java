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
 * Portions of this code is based on Apple Inc's QuickContacts sample (v1.3)
 * which is copyright (C) 2010-2014 Apple Inc.
 */
package org.robovm.samples.quickcontacts.ui;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.robovm.apple.addressbook.ABAddressBook;
import org.robovm.apple.addressbook.ABPerson;
import org.robovm.apple.addressbook.ABPersonEmailAddress;
import org.robovm.apple.addressbook.ABPersonProperty;
import org.robovm.apple.addressbook.ABProperty;
import org.robovm.apple.addressbook.ABPropertyLabel;
import org.robovm.apple.addressbookui.ABNewPersonViewController;
import org.robovm.apple.addressbookui.ABNewPersonViewControllerDelegate;
import org.robovm.apple.addressbookui.ABPeoplePickerNavigationController;
import org.robovm.apple.addressbookui.ABPeoplePickerNavigationControllerDelegateAdapter;
import org.robovm.apple.addressbookui.ABPersonViewController;
import org.robovm.apple.addressbookui.ABPersonViewControllerDelegate;
import org.robovm.apple.addressbookui.ABUnknownPersonViewController;
import org.robovm.apple.addressbookui.ABUnknownPersonViewControllerDelegate;
import org.robovm.apple.dispatch.DispatchQueue;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSErrorException;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSMutableArray;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIAlertView;
import org.robovm.apple.uikit.UINavigationController;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellAccessoryType;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.objc.annotation.CustomClass;

@CustomClass("QuickContactsViewController")
public class QuickContactsViewController extends UITableViewController implements ABPersonViewControllerDelegate,
        ABNewPersonViewControllerDelegate, ABUnknownPersonViewControllerDelegate {
    private static final double EDIT_UNKNOW_CONTACT_ROW_HEIGHT = 81;

    private ABAddressBook addressBook;
    private NSArray<NSDictionary<NSString, NSString>> menuArray;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        menuArray = new NSMutableArray<>(0);
        try {
            addressBook = ABAddressBook.create(null);
            checkAddressBookAccess();
        } catch (NSErrorException e) {
            UIAlertView alert = new UIAlertView("Error", "Your device doesn't support access to contacts.", null,
                    "Cancel");
            alert.show();
        }
    }

    /**
     * Check the authorization status of our application for Address Book
     */
    private void checkAddressBookAccess() {
        switch (ABAddressBook.getAuthorizationStatus()) {
        case Authorized:
            // Update our UI if the user has granted access to their Contacts
            accessGrantedForAddressBook();
            break;
        case NotDetermined:
            // Prompt the user for access to Contacts if there is no definitive
            // answer
            requestAddressBookAccess();
            break;
        case Denied:
        case Restricted:
            // Display a message if the user has denied or restricted access to
            // Contacts
            UIAlertView alert = new UIAlertView("Privacy Warning", "Permission was not granted for Contacts.", null,
                    "OK");
            alert.show();
            break;
        default:
            break;
        }
    }

    /**
     * Prompt the user for access to their Address Book data
     */
    private void requestAddressBookAccess() {
        addressBook.requestAccess(new ABAddressBook.RequestAccessCompletionHandler() {
            @Override
            public void requestAccess(boolean granted, NSError error) {
                if (granted) {
                    DispatchQueue.getMainQueue().async(new Runnable() {
                        @Override
                        public void run() {
                            accessGrantedForAddressBook();
                        }
                    });
                } else {
                    checkAddressBookAccess();
                }
            }
        });
    }

    /**
     * This method is called when the user has granted access to their address
     * book data.
     */
    private void accessGrantedForAddressBook() {
        // Load data from the plist file
        String plistPath = NSBundle.getMainBundle().findResourcePath("Menu", "plist");
        menuArray = (NSArray<NSDictionary<NSString, NSString>>) NSArray.read(new File(plistPath));
        getTableView().reloadData();
    }

    @Override
    public long getNumberOfSections(UITableView tableView) {
        return menuArray.size();
    }

    @Override
    public long getNumberOfRowsInSection(UITableView tableView, long section) {
        return 1;
    }

    @Override
    public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
        final String cellIdentifier = "CellID";
        UITableViewCell cell;
        // Make the Display Picker and Create New Contact rows look like buttons
        if (indexPath.getSection() < 2) {
            cell = new UITableViewCell(UITableViewCellStyle.Default, cellIdentifier);
            cell.getTextLabel().setTextAlignment(NSTextAlignment.Center);
        } else {
            cell = new UITableViewCell(UITableViewCellStyle.Subtitle, cellIdentifier);
            cell.setAccessoryType(UITableViewCellAccessoryType.DisclosureIndicator);
            cell.getDetailTextLabel().setNumberOfLines(0);
            // Display descriptions for the Edit Unknown Contact and Display and
            // Edit Contact rows
            cell.getDetailTextLabel().setText(
                    menuArray.get((int)indexPath.getSection()).get(new NSString("description")).toString());
            // TODO change this when we support out-wrapping of Strings within
            // arrays and dictionaries.
        }
        cell.getTextLabel().setText(
                menuArray.get((int)indexPath.getSection()).get(new NSString("title")).toString());
        return cell;
    }

    @Override
    public void didSelectRow(UITableView tableView, NSIndexPath indexPath) {
        switch ((int)indexPath.getSection()) {
        case 0:
            showPeoplePickerController();
            break;
        case 1:
            showNewPersonViewController();
            break;
        case 2:
            showPersonViewController();
            break;
        case 3:
            showUnknownPersonViewController();
            break;
        default:
            showPeoplePickerController();
            break;
        }
    }

    @Override
    public double getHeightForRow(UITableView tableView, NSIndexPath indexPath) {
        // Change the height if Edit Unknown Contact is the row selected
        return indexPath.getSection() == 3 ? EDIT_UNKNOW_CONTACT_ROW_HEIGHT : tableView.getRowHeight();
    }

    /**
     * Called when users tap "Display Picker" in the application. Displays a
     * list of contacts and allows users to select a contact from that list. The
     * application only shows the phone, email, and birthdate information of the
     * selected contact.
     */
    private void showPeoplePickerController() {
        ABPeoplePickerNavigationController picker = new ABPeoplePickerNavigationController();
        picker.setPeoplePickerDelegate(new ABPeoplePickerNavigationControllerDelegateAdapter() {
            /**
             * Displays the information of a selected person.
             * 
             * @param peoplePicker
             * @param person
             * @return
             */
            @Override
            public boolean shouldContinueAfterSelectingPerson(ABPeoplePickerNavigationController peoplePicker,
                    ABPerson person) {
                return true;
            }

            /**
             * Does not allow users to perform default actions such as dialing a
             * phone number, when they select a person property.
             * 
             * @param peoplePicker
             * @param person
             * @param property
             * @param identifier
             * @return
             */
            @Override
            public boolean shouldContinueAfterSelectingPerson(ABPeoplePickerNavigationController peoplePicker,
                    ABPerson person, ABProperty property, int identifier) {
                return false;
            }

            /**
             * Dismisses the people picker and shows the application when users
             * tap Cancel.
             * 
             * @param peoplePicker
             */
            @Override
            public void didCancel(ABPeoplePickerNavigationController peoplePicker) {
                dismissViewController(true, null);
            }
        });
        // Display only a person's phone, email, and birthdate
        List<ABPersonProperty> displayedItems = Arrays.asList(ABPersonProperty.Phone, ABPersonProperty.Email,
                ABPersonProperty.Birthday);
        picker.setDisplayedProperties(displayedItems);

        // Show the picker
        presentViewController(picker, true, null);
    }

    /**
     * Called when users tap "Display and Edit Contact" in the application.
     * Searches for a contact named "Appleseed" in in the address book. Displays
     * and allows editing of all information associated with that contact if the
     * search is successful. Shows an alert, otherwise.
     */
    private void showPersonViewController() {
        // Search for the person named "Appleseed" in the address book
        List<ABPerson> people = addressBook.getPeople("Appleseed");
        // Display "Appleseed" information if found in the address book
        if (people != null && people.size() > 0) {
            ABPerson person = people.get(0);
            ABPersonViewController picker = new ABPersonViewController();
            picker.setPersonViewDelegate(this);
            picker.setDisplayedPerson(person);
            // Allow users to edit the person’s information
            picker.setAllowsEditing(true);
            getNavigationController().pushViewController(picker, true);
        } else {
            // Show an alert if "Appleseed" is not in Contacts
            UIAlertView alert = new UIAlertView("Error", "Could not find Appleseed in the Contacts application", null,
                    "Cancel");
            alert.show();
        }
    }

    /**
     * Called when users tap "Create New Contact" in the application. Allows
     * users to create a new contact.
     */
    private void showNewPersonViewController() {
        ABNewPersonViewController picker = new ABNewPersonViewController();
        picker.setNewPersonViewDelegate(this);

        UINavigationController navigation = new UINavigationController(picker);
        presentViewController(navigation, true, null);
    }

    /**
     * Called when users tap "Edit Unknown Contact" in the application.
     */
    private void showUnknownPersonViewController() {
        ABPerson person = ABPerson.create();
        try {
            person.addEmailAddress(new ABPersonEmailAddress("John-Appleseed@mac.com", ABPropertyLabel.Other));

            ABUnknownPersonViewController picker = new ABUnknownPersonViewController();
            picker.setUnknownPersonViewDelegate(this);
            picker.setDisplayedPerson(person);
            picker.setAllowsAddingToAddressBook(true);
            picker.setAllowsActions(true);
            picker.setAlternateName("John Appleseed");
            picker.setTitle("John Appleseed");
            picker.setMessage("Company, Inc");

            getNavigationController().pushViewController(picker, true);
        } catch (NSErrorException e) {
            UIAlertView alert = new UIAlertView("Error", "Could not create unknown user", null, "Cancel");
            alert.show();
        }

    }

    /**
     * Dismisses the picker when users are done creating a contact or adding the
     * displayed person properties to an existing contact.
     */
    @Override
    public void didResolveToPerson(ABUnknownPersonViewController unknownCardViewController, ABPerson person) {
        getNavigationController().popViewController(true);
    }

    /**
     * Does not allow users to perform default actions such as emailing a
     * contact, when they select a contact property.
     */
    @Override
    public boolean shouldPerformDefaultAction(ABUnknownPersonViewController personViewController, ABPerson person,
            ABProperty property, int identifier) {
        return false;
    }

    /**
     * Dismisses the new-person view controller.
     */
    @Override
    public void didComplete(ABNewPersonViewController newPersonView, ABPerson person) {
        dismissViewController(true, null);
    }

    /**
     * Does not allow users to perform default actions such as dialing a phone
     * number, when they select a contact property.
     */
    @Override
    public boolean shouldPerformDefaultAction(ABPersonViewController personViewController, ABPerson person,
            ABProperty property, int identifier) {
        return false;
    }
}
