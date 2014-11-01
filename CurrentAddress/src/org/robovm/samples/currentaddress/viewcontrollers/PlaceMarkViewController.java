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

import org.robovm.apple.corelocation.CLPlacemark;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellSelectionStyle;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UITableViewController;

public class PlaceMarkViewController extends UITableViewController {
    private CLPlacemark placemark = new CLPlacemark();

    public PlaceMarkViewController () {
        getNavigationItem().setTitle("Address");

        UITableView tableView = getTableView();
        tableView.setBackgroundColor(UIColor.white());
        tableView.setAlwaysBounceVertical(true);
        tableView.setDataSource(this);
    }

    public void setPlacemark (CLPlacemark placemark) {
        this.placemark = placemark;
    }

    @Override
    public long getNumberOfSections (UITableView tableView) {
        return 1;
    }

    @Override
    public long getNumberOfRowsInSection (UITableView tableView, long section) {
        return 9;
    }

    @Override
    public UITableViewCell getCellForRow (UITableView tableView, NSIndexPath indexPath) {
        final String cellID = "cellID";

        UITableViewCell cell = tableView.dequeueReusableCell(cellID);
        if (cell == null) {
            cell = new UITableViewCell(UITableViewCellStyle.Subtitle, cellID);
            cell.setSelectionStyle(UITableViewCellSelectionStyle.None);
        }
        int row = (int)indexPath.getRow();
        switch (row) {
        case 0:
            cell.getTextLabel().setText("Thoroughfare");
            cell.getDetailTextLabel().setText(placemark.getThoroughfare());
            break;
        case 1:
            cell.getTextLabel().setText("Sub-thoroughfare");
            cell.getDetailTextLabel().setText(placemark.getSubThoroughfare());
            break;
        case 2:
            cell.getTextLabel().setText("Locality");
            cell.getDetailTextLabel().setText(placemark.getLocality());
            break;
        case 3:
            cell.getTextLabel().setText("Sub-locality");
            cell.getDetailTextLabel().setText(placemark.getSubLocality());
            break;
        case 4:
            cell.getTextLabel().setText("Administrative Area");
            cell.getDetailTextLabel().setText(placemark.getAdministrativeArea());
            break;
        case 5:
            cell.getTextLabel().setText("Sub-administrative Area");
            cell.getDetailTextLabel().setText(placemark.getSubAdministrativeArea());
            break;
        case 6:
            cell.getTextLabel().setText("Postal Code");
            cell.getDetailTextLabel().setText(placemark.getPostalCode());
            break;
        case 7:
            cell.getTextLabel().setText("Country");
            cell.getDetailTextLabel().setText(placemark.getCountry());
            break;
        default:
            cell.getTextLabel().setText("Country Code");
            cell.getDetailTextLabel().setText(placemark.getISOcountryCode());
            break;
        }
        return cell;
    }
}
