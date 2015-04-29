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
 * Portions of this code is based on Apple Inc's UICatalog sample (v11.3)
 * which is copyright (C) 2008-2015 Apple Inc.
 */
package org.robovm.samples.uicatalog.searchcontrollers;

import org.robovm.apple.uikit.UISearchController;
import org.robovm.objc.annotation.CustomClass;

@CustomClass("AAPLSearchShowResultsInSourceViewController")
public class AAPLSearchShowResultsInSourceViewController extends AAPLSearchResultsViewController {
    private UISearchController searchController;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        // Create the search controller, but we'll make sure that this
        // AAPLSearchShowResultsInSourceViewController
        // performs the results updating.
        searchController = new UISearchController(null);
        searchController.setSearchResultsUpdater(this);
        searchController.setDimsBackgroundDuringPresentation(false);

        // Make sure the that the search bar is visible within the navigation
        // bar.
        searchController.getSearchBar().sizeToFit();

        // Include the search controller's search bar within the table's header
        // view.
        getTableView().setTableHeaderView(searchController.getSearchBar());

        setDefinesPresentationContext(true);
    }
}
