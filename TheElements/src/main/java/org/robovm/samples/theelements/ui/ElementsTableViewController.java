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
 * Portions of this code is based on Apple Inc's TheElements sample (v1.12)
 * which is copyright (C) 2008-2013 Apple Inc.
 */

package org.robovm.samples.theelements.ui;

import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIStoryboardSegue;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.samples.theelements.datasource.ElementsDataSource;
import org.robovm.samples.theelements.model.AtomicElement;

@CustomClass("ElementsTableViewController")
public class ElementsTableViewController extends UITableViewController {
    private ElementsDataSource dataSource;

    public void setDataSource(ElementsDataSource dataSource) {
        // retain the data source
        this.dataSource = dataSource;

        // set the title, and tab bar images from the dataSource
        // object. These are part of the ElementsDataSource class.
        setTitle(dataSource.getName());
        getTabBarItem().setImage(dataSource.getTabBarImage());

        // set the long name shown in the navigation bar
        getNavigationItem().setTitle(dataSource.getNavigationBarName());
    }

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        UITableView tableView = getTableView();

        tableView.setSectionIndexMinimumDisplayRowCount(10);
        tableView.setDataSource(dataSource);

        // create a custom navigation bar button and set it to always say "back"
        UIBarButtonItem temporaryBarButtonItem = new UIBarButtonItem();
        temporaryBarButtonItem.setTitle("Back");
        getNavigationItem().setBackBarButtonItem(temporaryBarButtonItem);
    }

    @Override
    public void viewWillAppear(boolean animated) {
        // force the tableview to load
        getTableView().reloadData();
    }

    @Override
    public void prepareForSegue(UIStoryboardSegue segue, NSObject sender) {
        if (segue.getIdentifier().equals("showDetail")) {
            NSIndexPath selectedIndexPath = getTableView().getIndexPathForSelectedRow();

            // find the right view controller
            AtomicElement element = dataSource.getAtomicElement(selectedIndexPath);
            AtomicElementViewController viewController = (AtomicElementViewController) segue
                    .getDestinationViewController();

            // hide the bottom tabbar when we push this view controller
            viewController.setHidesBottomBarWhenPushed(true);

            // pass the element to this detail view controller
            viewController.setElement(element);
        }
    }
}
