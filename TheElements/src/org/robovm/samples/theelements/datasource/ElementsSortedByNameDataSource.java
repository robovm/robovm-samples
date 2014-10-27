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

import java.util.List;

import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewStyle;
import org.robovm.samples.theelements.model.AtomicElement;
import org.robovm.samples.theelements.model.PeriodicElements;
import org.robovm.samples.theelements.views.AtomicElementTableViewCell;

public class ElementsSortedByNameDataSource extends ElementsDataSource {
    @Override
    public String getName () {
        return "Name";
    }

    @Override
    public String getNavigationBarName () {
        return "Sorted by Name";
    }

    @Override
    public UIImage getTabBarImage () {
        return UIImage.create("name_gray.png");
    }

    @Override
    public UITableViewStyle getTableViewStyle () {
        return UITableViewStyle.Plain;
    }

    @Override
    public AtomicElement getAtomicElement (NSIndexPath indexPath) {
        return PeriodicElements
            .sharedPeriodicElements()
            .getElementsWithInitialLetter(
                PeriodicElements.sharedPeriodicElements().getElementNameIndexes().get((int)indexPath.getSection()))
            .get((int)indexPath.getRow());
    }

    @Override
    public UITableViewCell getCellForRow (UITableView tableView, NSIndexPath indexPath) {
        AtomicElementTableViewCell cell = getReuseableCell(tableView);
        cell.setElement(getAtomicElement(indexPath));
        return cell;
    }

    @Override
    public long getNumberOfSections (UITableView tableView) {
        // this table has multiple sections. One for each unique character that an element begins with
        // [A,B,C,D,E,F,G,H,I,K,L,M,N,O,P,R,S,T,U,V,X,Y,Z]
        // return the size of that array
        return PeriodicElements.sharedPeriodicElements().getElementNameIndexes().size();
    }

    @Override
    public List<String> getSectionIndexTitles (UITableView tableView) {
        // returns the array of section titles. There is one entry for each unique character that an element begins with
        // [A,B,C,D,E,F,G,H,I,K,L,M,N,O,P,R,S,T,U,V,X,Y,Z]
        return PeriodicElements.sharedPeriodicElements().getElementNameIndexes();
    }

    @Override
    public long getSectionForSectionIndexTitle (UITableView tableView, String title, long index) {
        return index;
    }

    @Override
    public long getNumberOfRowsInSection (UITableView tableView, long section) {
        // the section represents the initial letter of the element
        // return that letter
        String initialLetter = PeriodicElements.sharedPeriodicElements().getElementNameIndexes().get((int)section);

        // get the array of elements that begin with that letter
        List<AtomicElement> elementsWithInitialLetter = PeriodicElements.sharedPeriodicElements().getElementsWithInitialLetter(
            initialLetter);
        // return the count
        return elementsWithInitialLetter.size();
    }

    @Override
    public String getTitleForHeader (UITableView tableView, long section) {
        // this table has multiple sections. One for each unique character that an element begins with
        // [A,B,C,D,E,F,G,H,I,K,L,M,N,O,P,R,S,T,U,V,X,Y,Z]
        // return the letter that represents the requested section
        // this is actually a delegate method, but we forward the request to the datasource in the view controller
        return PeriodicElements.sharedPeriodicElements().getElementNameIndexes().get((int)section);
    }
}
