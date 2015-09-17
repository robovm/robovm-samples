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
 * Portions of this code is based on Apple Inc's CurrentAddress sample (v1.4)
 * which is copyright (C) 2009 - 2013 Apple Inc.
 */

package org.robovm.samples.currentaddress.ui;

import org.robovm.apple.corelocation.CLPlacemark;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSIndexSet;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.apple.uikit.UITableViewRowAnimation;
import org.robovm.objc.annotation.CustomClass;

@CustomClass("PlacemarkViewController")
public class PlacemarkViewController extends UITableViewController {
    private CLPlacemark placemark;

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);

        // Get the thoroughfare table cell and set the detail text to show the
        // thoroughfare.
        UITableViewCell cell = getTableView().getCellForRow(NSIndexPath.row(0, 0));
        cell.getDetailTextLabel().setText(placemark.getThoroughfare());

        // Get the sub-thoroughfare table cell and set the detail text to show
        // the sub-thoroughfare.
        cell = getTableView().getCellForRow(NSIndexPath.row(1, 0));
        cell.getDetailTextLabel().setText(placemark.getSubThoroughfare());

        // Get the locality table cell and set the detail text to show the
        // locality.
        cell = getTableView().getCellForRow(NSIndexPath.row(2, 0));
        cell.getDetailTextLabel().setText(placemark.getLocality());

        // Get the sub-locality table cell and set the detail text to show the
        // sub-locality.
        cell = getTableView().getCellForRow(NSIndexPath.row(3, 0));
        cell.getDetailTextLabel().setText(placemark.getSubLocality());

        // Get the administrative area table cell and set the detail text to
        // show the administrative area.
        cell = getTableView().getCellForRow(NSIndexPath.row(4, 0));
        cell.getDetailTextLabel().setText(placemark.getAdministrativeArea());

        // Get the sub-administrative area table cell and set the detail text to
        // show the sub-administrative area.
        cell = getTableView().getCellForRow(NSIndexPath.row(5, 0));
        cell.getDetailTextLabel().setText(placemark.getSubAdministrativeArea());

        // Get the postal code table cell and set the detail text to show the
        // postal code.
        cell = getTableView().getCellForRow(NSIndexPath.row(6, 0));
        cell.getDetailTextLabel().setText(placemark.getPostalCode());

        // Get the country table cell and set the detail text to show the
        // country.
        cell = getTableView().getCellForRow(NSIndexPath.row(7, 0));
        cell.getDetailTextLabel().setText(placemark.getCountry());

        // Get the ISO country code table cell and set the detail text to show
        // the ISO country code.
        cell = getTableView().getCellForRow(NSIndexPath.row(8, 0));
        cell.getDetailTextLabel().setText(placemark.getISOcountryCode());

        // Tell the table to reload section zero of the table.
        getTableView().reloadSections(new NSIndexSet(0), UITableViewRowAnimation.None);
    }

    public CLPlacemark getPlacemark() {
        return placemark;
    }

    public void setPlacemark(CLPlacemark placemark) {
        this.placemark = placemark;
    }
}
