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
 * Portions of this code is based on Apple Inc's QuickContacts sample (v1.3)
 * which is copyright (C) 2008-2013 Apple Inc.
 */

package org.robovm.samples.quickcontacts.viewcontrollers;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.robovm.apple.addressbook.ABAddressBook;
import org.robovm.apple.addressbook.ABMutableMultiValue;
import org.robovm.apple.addressbook.ABPerson;
import org.robovm.apple.addressbook.ABPersonProperty;
import org.robovm.apple.addressbook.ABPropertyLabel;
import org.robovm.apple.addressbook.ABPropertyType;
import org.robovm.apple.addressbookui.ABNewPersonViewController;
import org.robovm.apple.addressbookui.ABNewPersonViewControllerDelegate;
import org.robovm.apple.addressbookui.ABPeoplePickerNavigationController;
import org.robovm.apple.addressbookui.ABPeoplePickerNavigationControllerDelegate;
import org.robovm.apple.addressbookui.ABPersonViewController;
import org.robovm.apple.addressbookui.ABPersonViewControllerDelegate;
import org.robovm.apple.addressbookui.ABUnknownPersonViewController;
import org.robovm.apple.addressbookui.ABUnknownPersonViewControllerDelegate;
import org.robovm.apple.corefoundation.CFString;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.dispatch.DispatchQueue;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.NSIndexPathExtensions;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIAlertView;
import org.robovm.apple.uikit.UINavigationController;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellAccessoryType;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.apple.uikit.UITableViewStyle;
import org.robovm.apple.uikit.UIViewContentMode;

/**
 *  Demonstrates how to use ABPeoplePickerNavigationControllerDelegate, ABPersonViewControllerDelegate,
 *  ABNewPersonViewControllerDelegate, and ABUnknownPersonViewControllerDelegate. Shows how to browse a list of 
 *  Address Book contacts, display and edit a contact record, create a new contact record, and update a partial contact record.
 */
public class QuickContactsViewController extends UITableViewController implements
        ABPeoplePickerNavigationControllerDelegate,
        ABPersonViewControllerDelegate, ABNewPersonViewControllerDelegate,
        ABUnknownPersonViewControllerDelegate {

    private final static float UI_EDIT_UNKNOWN_CONTACT_ROW_HEIGHT = 81.0f;

    private ABAddressBook addressBook;
    private NSArray<NSDictionary<NSString, NSString>> menuArray;

    enum TableRowSelected {

        UIDisplayPickerRow,
        UICreateNewContactRow,
        UIDisplayContactRow,
        UIEditUnknownContactRow;

        static TableRowSelected[] values;

        static {
            values = TableRowSelected.values();
        }

        public static TableRowSelected toTableRowSelected(int row) {
            return values[row];
        }
    }

    private void initUI() {
        CGRect frame = new CGRect(0, 0, 320, 568);
        UITableView tableView = new UITableView(frame, UITableViewStyle.Grouped);
        setTableView(tableView);
        getTableView().setOpaque(false);
        getTableView().setClearsContextBeforeDrawing(false);
        getTableView().setContentMode(UIViewContentMode.ScaleToFill);
        getTableView().setAlwaysBounceVertical(true);

        UITableViewCell cell = new UITableViewCell(new CGRect());
        cell.setFrame(new CGRect(0, 119, 320, 44));
        cell.getContentView().setFrame(new CGRect(0.0, 0.0, 320, 43));
        getTableView().addSubview(cell);
    }

    /**
     * Implement viewDidLoad to do additional setup after loading the view,
     * typically from a nib.
     */
    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        initUI();

        // Create an address book object
        NSDictionary<NSString, NSObject> options = new NSDictionary<NSString, NSObject>();
        this.addressBook = ABAddressBook.create(options);
        this.menuArray = new NSArray<>();
        this.checkAddressBookAccess();
    }

    /**
     * Checks current access status of current device's address book
     */
    private void checkAddressBookAccess() {

        switch (ABAddressBook.getAuthorizationStatus()) {
        case Authorized:
            this.accessGrantedForAddressBook();
            break;
        case NotDetermined:
            requestAddressBookAccess();
            break;
        case Denied:
        case Restricted:
            UIAlertView alert = new UIAlertView("Privacy Warning", "Permission was not granted for Contacts.", null, "OK", new String[0]);
            alert.show();
            break;
        default:
            break;
        }
    }

    /**
     * This method is called when the user has granted access to their address
     * book data.
     */
    @SuppressWarnings("unchecked")
    private void accessGrantedForAddressBook() {
        // Load data from the plist file

        String plist = NSBundle.getMainBundle().findResourcePath("Menu", "plist");
        this.menuArray = (NSArray<NSDictionary<NSString, NSString>>) NSArray.read(new File(plist));
        getTableView().reloadData();

    }

    /**
     * Requests access to current device's address book
     */
    private void requestAddressBookAccess() {

        ABAddressBook.RequestAccessCompletionHandler handler = new ABAddressBook.RequestAccessCompletionHandler() {
            public void requestAccess(boolean granted, NSError error) {
                if (granted) {
                    DispatchQueue.getMainQueue().async(new Runnable() {
                        public void run() {
                            accessGrantedForAddressBook();
                        }

                    });
                }
            }
        };
        this.addressBook.requestAccess(handler);
    }

    @Override
    public long getNumberOfSections(UITableView tableView) {
        return this.menuArray.size();
    }

    @Override
    public long getNumberOfRowsInSection(UITableView tableView, long section) {
        return 1;
    }

    @Override
    public UITableViewCell getRowCell(UITableView tableView,
            NSIndexPath indexPath) {
        final String cellIdentifier = "CellID";
        UITableViewCell aCell;
        // Make the Display Picker and Create New Contact rows look like buttons
        int section = (int) NSIndexPathExtensions.getSection(indexPath);
        if (NSIndexPathExtensions.getSection(indexPath) < 2) {
            aCell = new UITableViewCell(UITableViewCellStyle.Default, cellIdentifier);
            aCell.getTextLabel().setTextAlignment(NSTextAlignment.Center);
        } else {
            aCell = new UITableViewCell(UITableViewCellStyle.Subtitle, cellIdentifier);
            aCell.setAccessoryType(UITableViewCellAccessoryType.DetailDisclosureButton);
            aCell.getDetailTextLabel().setNumberOfLines(0);
            // Display descriptions for the Edit Unknown Contact and Display and
            // Edit Contact rows
            aCell.getDetailTextLabel().setText(this.menuArray.get(section).get(new NSString("description")).toString()); // valueForKey:@"description"];
        }
        aCell.getTextLabel().setText(this.menuArray.get(section).get(new NSString("title")).toString());
        return aCell;
    }

    @Override
    public void didSelectRow(UITableView tableView, NSIndexPath indexPath) {

        int section = (int) NSIndexPathExtensions.getSection(indexPath);
        switch (TableRowSelected.toTableRowSelected(section)) {
        case UIDisplayPickerRow:
            this.showPeoplePickerController();
            break;
        case UICreateNewContactRow:
            this.showNewPersonViewController();
            break;
        case UIDisplayContactRow:
            this.showPersonViewController();
            break;
        case UIEditUnknownContactRow:
            this.showUnknownPersonViewController();
            break;
        default:
            this.showPeoplePickerController();
            break;
        }
    }

    /**
     * Change the height if Edit Unknown Contact is the row selected
     */
    @Override
    public double getRowHeight(UITableView tableView, NSIndexPath indexPath) {
        return (NSIndexPathExtensions.getSection(indexPath) == TableRowSelected.UIEditUnknownContactRow.ordinal()) ? UI_EDIT_UNKNOWN_CONTACT_ROW_HEIGHT
                : getTableView().getRowHeight();
    }

    /**
     * Called when users tap "Create New Contact" in the application. Allows
     * users to create a new contact.
     */
    private void showNewPersonViewController() {
        ABNewPersonViewController picker = new ABNewPersonViewController();
        picker.setNewPersonViewDelegate(this);

        UINavigationController navigation = new UINavigationController(picker);
        this.presentViewController(navigation, true, null);
    }

    /**
     * Called when users tap "Display Picker" in the application. Displays a
     * list of contacts and allows users to select a contact from that list. The
     * application only shows the phone, email, and birthdate information of the
     * selected contact.
     */
    private void showPeoplePickerController() {
        ABPeoplePickerNavigationController picker = new ABPeoplePickerNavigationController();
        picker.setPeoplePickerDelegate(this);

        // Display only a person's phone, email, and birthdate
        List<ABPersonProperty> props = new LinkedList<ABPersonProperty>();
        props.add(ABPersonProperty.Phone);
        props.add(ABPersonProperty.Email);
        props.add(ABPersonProperty.Birthday);
        picker.setDisplayedProperties(props);

        // Show the picker
        this.presentViewController(picker, true, null);
    }

    /**
     * Called when users tap "Display and Edit Contact" in the application.
     * Searches for a contact named "Appleseed" in in the address book. Displays
     * and allows editing of all information associated with that contact if the
     * search is successful. Shows an alert, otherwise.
     */
    private void showPersonViewController() {
        // Search for the person named "Appleseed" in the address book
        NSArray<?> people = (NSArray<?>) this.addressBook.getPeople("Appleseed");
        // Display "Appleseed" information if found in the address book
        if ((people != null) && !people.isEmpty()) {
            ABPerson person = people.get(0).as(ABPerson.class);
            ABPersonViewController picker = new ABPersonViewController();
            picker.setPersonViewDelegate(this);
            picker.setDisplayedPerson(person);
            // Allow users to edit the person???s information
            picker.setAllowsEditing(true);
            this.getNavigationController().pushViewController(picker, true);
        }
        else
        {
            // Show an alert if "Appleseed" is not in Contacts
            UIAlertView alert = new UIAlertView("Error", "Could not find Appleseed in the Contacts application", null, "Cancel", new String[0]);
            alert.show();
        }
    }

    /**
     * Called when users tap "Edit Unknown Contact" in the application.
     */
    private void showUnknownPersonViewController() {
        ABPerson aContact = ABPerson.create();
        ABMutableMultiValue email = ABMutableMultiValue.create(ABPropertyType.MultiString);
        CFString nameValue = new CFString("John-Appleseed@mac.com");
        email.addValueAndLabel(nameValue, ABPropertyLabel.Other.value().toString());
        aContact.setValue(ABPersonProperty.Email, email);

        ABUnknownPersonViewController picker = new ABUnknownPersonViewController();
        picker.setUnknownPersonViewDelegate(this);
        picker.setDisplayedPerson(aContact);
        picker.setAllowsAddingToAddressBook(true);
        picker.setAllowsActions(true);
        picker.setAlternateName("John Appleseed");
        picker.setTitle("John Appleseed");
        picker.setMessage("Company, Inc");

        this.getNavigationController().pushViewController(picker, true);
    }

    @Override
    public void didResolveToPerson(
            ABUnknownPersonViewController unknownCardViewController,
            ABPerson person) {
        this.getNavigationController().popViewController(true);
    }

    @Override
    public boolean shouldPerformDefaultAction(
            ABUnknownPersonViewController personViewController,
            ABPerson person, int property, int identifier) {
        return false;
    }

    @Override
    public void didComplete(ABNewPersonViewController newPersonView,
            ABPerson person) {
        this.dismissViewController(true, null);
    }

    @Override
    public boolean shouldPerformDefaultAction(
            ABPersonViewController personViewController, ABPerson person,
            int property, int identifier) {
        return false;
    }

    @Override
    public void didCancel(ABPeoplePickerNavigationController peoplePicker) {
        this.dismissViewController(true, null);
    }

    @Override
    public boolean shouldContinueAfterSelectingPerson(
            ABPeoplePickerNavigationController peoplePicker, ABPerson person) {
        return true;
    }

    @Override
    public boolean shouldContinueAfterSelectingPerson(
            ABPeoplePickerNavigationController peoplePicker, ABPerson person,
            int property, int identifier) {
        return false;
    }

}