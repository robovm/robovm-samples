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
 * Portions of this code is based on Apple Inc's LocateMe sample (v4.0)
 * which is copyright (C) 2008-2014 Apple Inc.
 */

package org.robovm.samples.locateme.ui;

import org.robovm.apple.corelocation.CLLocation;
import org.robovm.apple.foundation.NSDateFormatter;
import org.robovm.apple.foundation.NSDateFormatterStyle;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellSelectionStyle;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.apple.uikit.UITableViewStyle;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.samples.locateme.util.Str;

@CustomClass("LocationDetailViewController")
public class LocationDetailViewController extends UITableViewController {
    private CLLocation location;
    private NSDateFormatter dateFormatter;

    public LocationDetailViewController(UITableViewStyle style) {
        super(style);
    }

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        dateFormatter = new NSDateFormatter();
        dateFormatter.setDateStyle(NSDateFormatterStyle.Medium);
        dateFormatter.setTimeStyle(NSDateFormatterStyle.Long);
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);

        setTitle(NSString.getLocalizedString("LocationInfo"));
        getTableView().reloadData();
    }

    @Override
    public long getNumberOfSections(UITableView tableView) {
        return 3;
    }

    @Override
    public long getNumberOfRowsInSection(UITableView tableView, long section) {
        return (section == 0) ? 3 : 2;
    }

    @Override
    public String getTitleForHeader(UITableView tableView, long section) {
        switch ((int) section) {
        case 0:
            return Str.getLocalizedString("Attributes");
        case 1:
            return Str.getLocalizedString("Accuracy");
        default:
            return Str.getLocalizedString("Course and Speed");
        }
    }

    @Override
    public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
        final String locationAttributeCellID = "LocationAttributeCellID";
        UITableViewCell cell = tableView.dequeueReusableCell(locationAttributeCellID);
        if (cell == null) {
            cell = new UITableViewCell(UITableViewCellStyle.Value2, locationAttributeCellID);
            cell.setSelectionStyle(UITableViewCellSelectionStyle.None);
        }
        int section = indexPath.getSection();
        int row = indexPath.getRow();
        if (section == 0) {
            switch (row) {
            case 0:
                cell.getTextLabel().setText(Str.getLocalizedString("timestamp"));
                cell.getDetailTextLabel().setText(dateFormatter.format(location.getTimestamp()));
                break;
            case 1:
                cell.getTextLabel().setText(Str.getLocalizedString("coordinate"));
                cell.getDetailTextLabel().setText(Str.getLocalizedCoordinateString(location));
                break;
            default:
                cell.getTextLabel().setText(Str.getLocalizedString("altitude"));
                cell.getDetailTextLabel().setText(Str.getLocalizedAltitudeString(location));
                break;
            }
        } else if (section == 1) {
            switch (row) {
            case 0:
                cell.getTextLabel().setText(Str.getLocalizedString("horizontal"));
                cell.getDetailTextLabel().setText(Str.getLocalizedHorizontalAccuracyString(location));
                break;
            default:
                cell.getTextLabel().setText(Str.getLocalizedString("vertical"));
                cell.getDetailTextLabel().setText(Str.getLocalizedVerticalAccuracyString(location));
                break;
            }
        } else {
            switch (row) {
            case 0:
                cell.getTextLabel().setText(Str.getLocalizedString("course"));
                cell.getDetailTextLabel().setText(Str.getLocalizedCourseString(location));
                break;
            default:
                cell.getTextLabel().setText(Str.getLocalizedString("speed"));
                cell.getDetailTextLabel().setText(Str.getLocalizedSpeedString(location));
                break;
            }
        }
        return cell;
    }

    public void setLocation(CLLocation location) {
        this.location = location;
    }
}
