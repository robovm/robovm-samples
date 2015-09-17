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

package org.robovm.samples.theelements.datasource;

import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewStyle;
import org.robovm.samples.theelements.model.AtomicElement;
import org.robovm.samples.theelements.model.PeriodicElements;
import org.robovm.samples.theelements.ui.AtomicElementTableViewCell;

public class ElementsSortedByAtomicNumberDataSource extends ElementsDataSource {
    @Override
    public String getName() {
        return "Number";
    }

    @Override
    public String getNavigationBarName() {
        return "Sorted by Atomic Number";
    }

    @Override
    public UIImage getTabBarImage() {
        return UIImage.getImage("number_gray.png");
    }

    @Override
    public UITableViewStyle getTableViewStyle() {
        return UITableViewStyle.Plain;
    }

    @Override
    public AtomicElement getAtomicElement(NSIndexPath indexPath) {
        return PeriodicElements.getSharedPeriodicElements().getElementsSortedByNumber().get(indexPath.getRow());
    }

    @Override
    public long getNumberOfRowsInSection(UITableView tableView, long section) {
        return PeriodicElements.getSharedPeriodicElements().getElementsSortedByNumber().size();
    }

    @Override
    public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
        AtomicElementTableViewCell cell = getReuseableCell(tableView);
        cell.setElement(getAtomicElement(indexPath));

        return cell;
    }

    @Override
    public long getNumberOfSections(UITableView tableView) {
        return 1;
    }
}
