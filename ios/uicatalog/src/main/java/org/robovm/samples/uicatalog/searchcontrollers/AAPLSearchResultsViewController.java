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
import org.robovm.apple.uikit.UISearchResultsUpdating;
import org.robovm.objc.annotation.CustomClass;

@CustomClass("AAPLSearchResultsViewController")
public class AAPLSearchResultsViewController extends AAPLSearchControllerBaseViewController implements
        UISearchResultsUpdating {
    public static final String STORYBOARD_IDENTIFIER = "AAPLSearchResultsViewControllerStoryboardIdentifier";

    /**
     * This is called when the controller is being dismissed to allow those who
     * are using the controller they are search as the results controller a
     * chance to reset their state. No need to update anything if we're being
     * dismissed.
     */
    @Override
    public void updateSearchResults(UISearchController searchController) {
        if (!searchController.isActive()) {
            return;
        }

        setFilterString(searchController.getSearchBar().getText());
    }
}
