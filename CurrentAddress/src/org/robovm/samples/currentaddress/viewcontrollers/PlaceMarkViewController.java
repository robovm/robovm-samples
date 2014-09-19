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
 * Portions of this code is based on Apple Inc's CurrentAddress sample (v1.4)
 * which is copyright (C) 2009-2013 Apple Inc.
 */

package org.robovm.samples.currentaddress.viewcontrollers;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.corelocation.CLPlacemark;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellSelectionStyle;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.apple.uikit.UITableViewDataSourceAdapter;
import org.robovm.apple.uikit.UITableViewStyle;

public class PlaceMarkViewController extends UITableViewController {
    private CLPlacemark placemark = new CLPlacemark();

    public PlaceMarkViewController () {
        super();

        getNavigationItem().setTitle("Address");

        UITableView tableView = new UITableView(new CGRect(0, 0, 320, 480), UITableViewStyle.Plain);
        tableView.setBackgroundColor(UIColor.white());
        tableView.setAlwaysBounceVertical(true);
        tableView.setRowHeight(44);
        tableView.setSectionHeaderHeight(22);
        tableView.setSectionFooterHeight(22);
        tableView.setDataSource(new UITableViewDataSourceAdapter() {
            @Override
            public long getNumberOfSections (UITableView tableView) {
                return 1;
            }

            @Override
            public long getNumberOfRowsInSection (UITableView tableView, long section) {
                if (section == 0) return 9;
                return 0;
            }

            @Override
            public UITableViewCell getRowCell (UITableView tableView, NSIndexPath indexPath) {
                if (indexPath.getSection() == 0) {
                    switch ((int)indexPath.getRow()) {
                    case 0:
                        return createCell("Thoroughfare", placemark.getThoroughfare());
                    case 1:
                        return createCell("Sub-thoroughfare", placemark.getSubThoroughfare());
                    case 2:
                        return createCell("Locality", placemark.getLocality());
                    case 3:
                        return createCell("Sub-locality", placemark.getSubLocality());
                    case 4:
                        return createCell("Administrative Area", placemark.getAdministrativeArea());
                    case 5:
                        return createCell("Sub-administrative Area", placemark.getSubAdministrativeArea());
                    case 6:
                        return createCell("Postal Code", placemark.getPostalCode());
                    case 7:
                        return createCell("Country", placemark.getCountry());
                    default:
                        return createCell("Country Code", placemark.getISOcountryCode());
                    }
                }
                return null;
            }
        });
        setTableView(tableView);
    }

    private UITableViewCell createCell (String name, String detail) {
        UITableViewCell cell = getTableView().dequeueReusableCell(name);
        if (cell == null) {
            cell = new UITableViewCell(UITableViewCellStyle.Subtitle, name);
            cell.setIndentationWidth(10);
            cell.setSelectionStyle(UITableViewCellSelectionStyle.None);

            UILabel textLabel = cell.getTextLabel();
            textLabel.setText(name);
            textLabel.setFont(UIFont.getBoldSystemFont(18));
            textLabel.setTextColor(UIColor.darkText());

            UILabel detailTextLabel = cell.getDetailTextLabel();
            detailTextLabel.setFont(UIFont.getSystemFont(14));
            detailTextLabel.setTextColor(UIColor.fromRGBA(0.5, 0.5, 0.5, 1));
        }
        if (detail != null) cell.getDetailTextLabel().setText(detail);

        return cell;
    }

    @Override
    public void viewWillAppear (boolean animated) {
        super.viewWillAppear(animated);

// // Get the thoroughfare table cell and set the detail text to show the thoroughfare.
// UITableViewCell cell = getTableView().getCellForRow(NSIndexPath.createWithRow(0, 0));
// cell.getDetailTextLabel().setText(placemark.getThoroughfare());
//
// // Get the sub-thoroughfare table cell and set the detail text to show the sub-thoroughfare.
// cell = getTableView().getCellForRow(NSIndexPath.createWithRow(1, 0));
// cell.getDetailTextLabel().setText(placemark.getSubThoroughfare());
//
// // Get the locality table cell and set the detail text to show the locality.
// cell = getTableView().getCellForRow(NSIndexPath.createWithRow(2, 0));
// cell.getDetailTextLabel().setText(placemark.getLocality());
//
// // Get the sub-locality table cell and set the detail text to show the sub-locality.
// cell = getTableView().getCellForRow(NSIndexPath.createWithRow(3, 0));
// cell.getDetailTextLabel().setText(placemark.getSubLocality());
//
// // Get the administrative area table cell and set the detail text to show the administrative area.
// cell = getTableView().getCellForRow(NSIndexPath.createWithRow(4, 0));
// cell.getDetailTextLabel().setText(placemark.getAdministrativeArea());
//
// // Get the sub-administrative area table cell and set the detail text to show the sub-administrative area.
// cell = getTableView().getCellForRow(NSIndexPath.createWithRow(5, 0));
// cell.getDetailTextLabel().setText(placemark.getSubAdministrativeArea());
//
// // Get the postal code table cell and set the detail text to show the postal code.
// cell = getTableView().getCellForRow(NSIndexPath.createWithRow(6, 0));
// cell.getDetailTextLabel().setText(placemark.getPostalCode());
//
// // Get the country table cell and set the detail text to show the country.
// cell = getTableView().getCellForRow(NSIndexPath.createWithRow(7, 0));
// cell.getDetailTextLabel().setText(placemark.getCountry());
//
// // Get the ISO country code table cell and set the detail text to show the ISO country code.
// cell = getTableView().getCellForRow(NSIndexPath.createWithRow(8, 0));
// cell.getDetailTextLabel().setText(placemark.getISOcountryCode());
//
// // Tell the table to reload section zero of the table.
// getTableView().reloadSections(NSIndexSet.indexSetWithIndex$(0), UITableViewRowAnimation.None);
    }

    public void setPlacemark (CLPlacemark placemark) {
        this.placemark = placemark;
    }
}
