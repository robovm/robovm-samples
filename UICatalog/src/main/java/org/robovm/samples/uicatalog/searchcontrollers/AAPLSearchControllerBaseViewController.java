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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.objc.annotation.CustomClass;

@CustomClass("AAPLSearchControllerBaseViewController")
public class AAPLSearchControllerBaseViewController extends UITableViewController {
    private static final String CELL_IDENTIFIER = "searchResultsCell";

    private List<String> allResults;
    private List<String> visibleResults;

    /*
     * A null / empty filter string means show all results. Otherwise, show only
     * results containing the filter.
     */
    private String filterString;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        allResults = Arrays.asList("Here's", "to", "the", "crazy", "ones.", "The", "misfits.", "The", "rebels.", "The",
                "troublemakers.", "The", "round", "pegs", "in", "the", "square", "holes.", "The", "ones", "who", "see",
                "things", "differently.", "They're", "not", "fond", "of", "rules.", "And", "they", "have", "no",
                "respect", "for", "the", "status", "quo.", "You", "can", "quote", "them,", "disagree", "with", "them,",
                "glorify", "or", "vilify", "them.", "About", "the", "only", "thing", "you", "can't", "do", "is",
                "ignore", "them.", "Because", "they", "change", "things.", "They", "push", "the", "human", "race",
                "forward.", "And", "while", "some", "may", "see", "them", "as", "the", "crazy", "ones,", "we", "see",
                "genius.", "Because", "the", "people", "who", "are", "crazy", "enough", "to", "think", "they", "can",
                "change", "the", "world,", "are", "the", "ones", "who", "do.");
        visibleResults = new ArrayList<String>(allResults);
    }

    public void setFilterString(String filterString) {
        this.filterString = filterString;

        visibleResults.clear();

        if (filterString == null || filterString.length() <= 0) {
            visibleResults.addAll(allResults);
        } else {
            for (String result : allResults) {
                if (result.contains(filterString)) {
                    visibleResults.add(result);
                }
            }
        }

        getTableView().reloadData();
    }

    public String getFilterString() {
        return filterString;
    }

    @Override
    public long getNumberOfRowsInSection(UITableView tableView, long section) {
        return visibleResults.size();
    }

    @Override
    public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
        return tableView.dequeueReusableCell(CELL_IDENTIFIER, indexPath);
    }

    @Override
    public void willDisplayCell(UITableView tableView, UITableViewCell cell, NSIndexPath indexPath) {
        cell.getTextLabel().setText(visibleResults.get((int) indexPath.getRow()));
    }
}
