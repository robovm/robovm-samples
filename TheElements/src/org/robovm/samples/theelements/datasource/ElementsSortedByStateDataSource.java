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
import org.robovm.samples.theelements.views.AtomicElementTableViewCell;

public class ElementsSortedByStateDataSource extends ElementsDataSource {
    @Override
    public String getName () {
        return "State";
    }

    @Override
    public String getNavigationBarName () {
        return "Grouped by State";
    }

    @Override
    public UIImage getTabBarImage () {
        return UIImage.create("state_gray.png");
    }

    @Override
    public UITableViewStyle getTableViewStyle () {
        // atomic state is displayed in a grouped style tableview
        return UITableViewStyle.Grouped;
    }

    @Override
    public AtomicElement getAtomicElement (NSIndexPath indexPath) {
        // this table has multiple sections. One for each physical state
        // [solid, liquid, gas, artificial]
        // the section represents the index in the state array
        // the row the index into the array of data for a particular state

        // get the state
        String elementState = PeriodicElements.sharedPeriodicElements().getElementPhysicalStates()[(int)indexPath.getSection()];

        // return the element in the state array
        return PeriodicElements.sharedPeriodicElements().getElementsWithPhysicalState(elementState).get((int)indexPath.getRow());
    }

    @Override
    public UITableViewCell getCellForRow (UITableView tableView, NSIndexPath indexPath) {
        AtomicElementTableViewCell cell = getReuseableCell(tableView);
        // set the element for this cell as specified by the datasource. The atomicElementForIndexPath: is declared
        // as part of the ElementsDataSource Protocol and will return the appropriate element for the index row
        //
        cell.setElement(getAtomicElement(indexPath));

        return cell;
    }

    @Override
    public long getNumberOfSections (UITableView tableView) {
        // this table has multiple sections. One for each physical state
        // [solid, liquid, gas, artificial]
        // return the number of items in the states array

        return PeriodicElements.sharedPeriodicElements().getElementPhysicalStates().length;
    }

    @Override
    public long getNumberOfRowsInSection (UITableView tableView, long section) {
        // this table has multiple sections. One for each physical state
        // [solid, liquid, gas, artificial]

        // get the state key for the requested section
        String state = PeriodicElements.sharedPeriodicElements().getElementPhysicalStates()[(int)section];

        // return the number of items that are in the array for that state
        return PeriodicElements.sharedPeriodicElements().getElementsWithPhysicalState(state).size();
    }

    @Override
    public String getTitleForHeader (UITableView tableView, long section) {
        // this table has multiple sections. One for each physical state

        // [solid, liquid, gas, artificial]
        // return the state that represents the requested section
        // this is actually a delegate method, but we forward the request to the datasource in the view controller
        return PeriodicElements.sharedPeriodicElements().getElementPhysicalStates()[(int)section];
    }
}
