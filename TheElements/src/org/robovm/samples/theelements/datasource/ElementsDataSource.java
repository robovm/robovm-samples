/*
 * Copyright (C) 2014 RoboVM AB
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
import org.robovm.apple.uikit.UITableViewDataSourceAdapter;
import org.robovm.apple.uikit.UITableViewStyle;
import org.robovm.samples.theelements.model.AtomicElement;
import org.robovm.samples.theelements.views.AtomicElementTableViewCell;

public abstract class ElementsDataSource extends UITableViewDataSourceAdapter {
    public abstract String getName ();

    public abstract String getNavigationBarName ();

    public abstract UIImage getTabBarImage ();

    public abstract UITableViewStyle getTableViewStyle ();

    /** provides a standardized means of asking for the element at the specific index path, regardless of the sorting or display
     * technique for the specific datasource
     * @param indexPath
     * @return */
    public abstract AtomicElement getAtomicElement (NSIndexPath indexPath);

    AtomicElementTableViewCell getReuseableCell (UITableView tableView) {
        AtomicElementTableViewCell cell = (AtomicElementTableViewCell)tableView.dequeueReusableCell("AtomicElementTableViewCell");
        if (cell == null) {
            cell = new AtomicElementTableViewCell();
            addStrongRef(cell);
        }
        return cell;
    }
}
