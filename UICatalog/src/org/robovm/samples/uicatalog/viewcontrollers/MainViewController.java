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
 * Portions of this code is based on Apple Inc's UICatalog sample (v2.11)
 * which is copyright (C) 2008-2013 Apple Inc.
 */

package org.robovm.samples.uicatalog.viewcontrollers;

import java.util.LinkedList;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellAccessoryType;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.apple.uikit.UIWindow;
import org.robovm.rt.bro.annotation.MachineSizedSInt;

/** The application's main view controller (front page) */
public class MainViewController extends UITableViewController {
    private final static String MY_CELL_IDENTIFIER = "MyTableViewCell";
    private final LinkedList<MenuListItem> menuList = new LinkedList<MenuListItem>();

    private class MyTableViewCell extends UITableViewCell {
        @Override
        protected long init (UITableViewCellStyle style, String reuseIdentifier) {
            return super.init(UITableViewCellStyle.Subtitle, reuseIdentifier);
        }
    }

    /** Item storing meta data for menu items */
    private class MenuListItem {
        private final String title;
        private final String explanation;
        private final UIViewController viewController;

        public MenuListItem (String title, String explanation, UIViewController viewController) {
            super();
            this.title = title;
            this.explanation = explanation;
            this.viewController = viewController;
        }

        public String getTitle () {
            return title;
        }

        public String getExplanation () {
            return explanation;
        }

        public UIViewController getViewController () {
            return viewController;
        }

    }

    /** construct the array of page descriptions we will use (each description is a dictionary) */
    @Override
    public void viewDidLoad () {
        super.viewDidLoad();

        UIWindow window = new UIWindow(UIScreen.getMainScreen().getBounds());
        window.setBackgroundColor(UIColor.lightGray());
        window.makeKeyAndVisible();

        // <rect key="frame" x="0.0" y="64" width="320" height="416"/>
        CGRect tableViewBounds = new CGRect(0.0, 64.0, 320, 416);
        setTableView(new UITableView(tableViewBounds));

        // for showing various UIButtons:
        ButtonsViewController buttonsViewController = new ButtonsViewController();
        // for showing various UIControls:
        ControlsViewController controlsViewController = new ControlsViewController();
        // for showing various UITextFields:
        TextFieldController textfieldController = new TextFieldController();
        // for UISearchBar:
        SearchBarViewController searchBarViewController = new SearchBarViewController();
        // for showing UITextView:
        TextViewController textViewController = new TextViewController();
        // for showing various UIPickers:
        ImageViewController imageViewController = new ImageViewController();
        // for showing UIImageView:
        WebViewController webViewController = new WebViewController();
        // for showing UIWebView:
        TransitionViewController transitionController = new TransitionViewController();
        // for showing various UISegmentedControls:
        SegmentViewController segments = new SegmentViewController();
        // for showing various UIBarButtonItem items inside a UIToolbar:
        ToolbarViewController toolbarViewController = new ToolbarViewController();
        // for showing various UIActionSheets and UIAlertViews:
        AlertsViewController alertsViewController = new AlertsViewController();
        // for showing how to a use flip animation transition between two
        // UIViews:
        PickerViewController pickerViewController = new PickerViewController();

        // Add items to list where they will be retrieved when to display
        menuList.add(new MenuListItem("ButtonsTitle", "Various uses of UIButton", buttonsViewController));
        menuList.add(new MenuListItem("ControlsTitle", "Various uses of UIControl", controlsViewController));
        menuList.add(new MenuListItem("TextFieldTitle", "Uses of UITextField", textfieldController));
        menuList.add(new MenuListItem("SearchBar", "Use of UISearchBar", searchBarViewController));
        menuList.add(new MenuListItem("TextView", "Use of UITextField", textViewController));
        menuList.add(new MenuListItem("Pickers", "Uses of UIDatePicker, UIPickerView", pickerViewController));
        menuList.add(new MenuListItem("Images", "Use of UIWebView", imageViewController));
        menuList.add(new MenuListItem("WebView", "Various uses of UISegmentedControl", webViewController));
        menuList.add(new MenuListItem("Segments", "Various uses of UISegmentedControl", segments));
        menuList.add(new MenuListItem("Toolbars", "Uses of UIToolbar", toolbarViewController));
        menuList.add(new MenuListItem("Alerts", "Various uses of UIAlertView, UIActionSheet", alertsViewController));
        menuList.add(new MenuListItem("Transition", "Shows UIViewAnimationTransitions", transitionController));

        UIBarButtonItem temporaryBarButtonItem = new UIBarButtonItem();
        temporaryBarButtonItem.setTitle("Back");
        getNavigationItem().setBackBarButtonItem(temporaryBarButtonItem);

        getTableView().registerReusableCellClass(MyTableViewCell.class, MY_CELL_IDENTIFIER);
    }

    @Override
    public void viewWillAppear (boolean animated) {
        super.viewWillAppear(animated);

        // this UIViewController is about to re-appear, make sure we remove the
        // current selection in our table view
        NSIndexPath tableSelection = getTableView().getIndexPathForSelectedRow();
        getTableView().deselectRow(tableSelection, false);

        // some over view controller could have changed our nav bar tint color,
        // so reset it here
        getNavigationController().getNavigationBar().setTintColor(UIColor.darkGray());
    }

    /** the table's selection has changed, switch to that item's UIViewController */
    @Override
    public void didSelectRow (UITableView tableView, NSIndexPath indexPath) {
        MenuListItem item = menuList.get((int)indexPath.getRow());

        UIViewController targetViewController = item.getViewController();
        getNavigationController().pushViewController(targetViewController, true);
    }

    @Override
    public @MachineSizedSInt long getNumberOfRowsInSection (UITableView tableView, @MachineSizedSInt long section) {
        return menuList.size();
    }

    @Override
    public UITableViewCell getCellForRow (UITableView tableView, NSIndexPath indexPath) {
        UITableViewCell cell = getTableView().dequeueReusableCell(MY_CELL_IDENTIFIER, indexPath);

        cell.setAccessoryType(UITableViewCellAccessoryType.DisclosureIndicator);
        cell.getTextLabel().setText(menuList.get((int)indexPath.getRow()).getTitle());
        cell.getDetailTextLabel().setText(menuList.get((int)indexPath.getRow()).getExplanation());
        return cell;
    }

}
