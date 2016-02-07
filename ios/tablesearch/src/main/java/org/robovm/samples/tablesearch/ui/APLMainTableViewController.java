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
 * Portions of this code is based on Apple Inc's TableSearch sample (v1.2)
 * which is copyright (C) 2015 Apple Inc.
 */

package org.robovm.samples.tablesearch.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.robovm.apple.foundation.NSCoder;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.UISearchBar;
import org.robovm.apple.uikit.UISearchBarDelegateAdapter;
import org.robovm.apple.uikit.UISearchController;
import org.robovm.apple.uikit.UISearchControllerDelegateAdapter;
import org.robovm.apple.uikit.UISearchResultsUpdating;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.samples.tablesearch.APLProduct;

@CustomClass("APLMainTableViewController")
public class APLMainTableViewController extends APLBaseTableViewController implements UISearchResultsUpdating {
    private List<APLProduct> products;
    private UISearchController searchController;
    // our secondary search results table view
    private APLResultsTableViewController resultsTableController;

    private boolean searchControllerWasActive;
    private boolean searchControllerSearchFieldWasFirstResponder;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        if (products == null) {
            products = new ArrayList<>();
        }

        resultsTableController = new APLResultsTableViewController();
        searchController = new UISearchController(resultsTableController);
        searchController.setSearchResultsUpdater(this);
        searchController.getSearchBar().sizeToFit();
        getTableView().setTableHeaderView(searchController.getSearchBar());

        // we want to be the delegate for our filtered table so
        // didSelectRowAtIndexPath is called for both tables
        resultsTableController.getTableView().setDelegate(this);// so we can
                                                                // monitor text
                                                                // changes +
                                                                // others
        searchController.setDelegate(new UISearchControllerDelegateAdapter() {
            /**
             * Called after the search controller's search bar has agreed to
             * begin editing or when 'active' is set to true. If you choose not
             * to present the controller yourself or do not implement this
             * method, a default presentation is performed on your behalf.
             * 
             * Implement this method if the default presentation is not adequate
             * for your purposes.
             */
            @Override
            public void present(UISearchController searchController) {}
        });
        searchController.setDimsBackgroundDuringPresentation(false);
        // so we can monitor text changes + others
        searchController.getSearchBar().setDelegate(new UISearchBarDelegateAdapter() {
            @Override
            public void searchButtonClicked(UISearchBar searchBar) {
                searchBar.resignFirstResponder();
            }
        });

        // Search is now just presenting a view controller. As such, normal view
        // controller
        // presentation semantics apply. Namely that presentation will walk up
        // the view controller
        // hierarchy until it finds the root view controller or one that defines
        // a presentation context.
        setDefinesPresentationContext(true); // know where you want
                                             // UISearchController to be
                                             // displayed
    }

    @Override
    public void viewDidAppear(boolean animated) {
        super.viewDidAppear(animated);

        // restore the searchController's active state
        if (searchControllerWasActive) {
            searchController.setActive(searchControllerWasActive);
            searchControllerWasActive = false;

            if (searchControllerSearchFieldWasFirstResponder) {
                searchController.getSearchBar().becomeFirstResponder();
                searchControllerSearchFieldWasFirstResponder = false;
            }
        }
    }

    @Override
    public long getNumberOfRowsInSection(UITableView tableView, long section) {
        return products.size();
    }

    @Override
    public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
        APLProduct product = products.get((int) indexPath.getRow());

        UITableViewCell cell = getTableView().dequeueReusableCell(CELL_IDENTIFIER);
        configureCell(cell, product);
        return cell;
    }

    /**
     * here we are the table view delegate for both our main table and filtered
     * table, so we can push from the current navigation controller
     * (resultsTableController's parent view controller is not this
     * UINavigationController)
     */
    @Override
    public void didSelectRow(UITableView tableView, NSIndexPath indexPath) {
        APLProduct selectedProduct = tableView == getTableView() ? products.get((int) indexPath.getRow())
                : resultsTableController
                        .getFilteredProducts().get((int) indexPath.getRow());

        APLDetailViewController detailViewController = (APLDetailViewController) getStoryboard()
                .instantiateViewController("APLDetailViewController");
        // hand off the current product to the detail view controller
        detailViewController.setProduct(selectedProduct);

        getNavigationController().pushViewController(detailViewController, true);

        tableView.deselectRow(indexPath, false);
    }

    @Override
    public void updateSearchResults(UISearchController searchController) {
        // update the filtered array based on the search text
        String searchText = searchController.getSearchBar().getText();
        List<APLProduct> searchResults = new ArrayList<>();

        // strip out all the leading and trailing spaces
        String strippedStr = searchText.trim();

        // break up the search terms (separated by spaces)
        String[] searchItems = strippedStr.split(" ");

        // Find products with the specified search terms.
        search: for (APLProduct product : products) {
            // Every search term must fit.
            for (String searchTerm : searchItems) {
                String searchString = searchTerm.toUpperCase(Locale.ENGLISH);
                // TITLE: searchString must be contained in the product title.
                if (product.getTitle().toUpperCase(Locale.ENGLISH).indexOf(searchString) != -1) {
                    continue;
                }
                try {
                    double searchNumber = Double.parseDouble(searchString);

                    // YEAR: searchNumber must equal the product introduction
                    // year.
                    if (product.getYearIntroduced() == searchNumber) {
                        continue;
                    }
                    // PRICE: searchNumber must equal the product price.
                    if (Math.abs(product.getIntroPrice() - searchNumber) <= 0.0000001) {
                        continue;
                    }
                } catch (NumberFormatException e) {
                    // Ignore.
                }
                // Search term doesn't fit, so discard this product.
                continue search;
            }
            // All search terms fit.
            searchResults.add(product);
        }

        // hand over the filtered results to our search results table
        APLResultsTableViewController tableController = (APLResultsTableViewController) searchController
                .getSearchResultsController();
        tableController.setFilteredProducts(searchResults);
        tableController.getTableView().reloadData();
    }

    public void setProducts(List<APLProduct> products) {
        this.products = products;
    }

    // we restore several items for state restoration:
    // 1) Search controller's active state,
    // 2) search text,
    // 3) first responder

    private static final String VIEW_CONTROLLER_TITLE_KEY = "ViewControllerTitleKey";
    private static final String SEARCH_CONTROLLER_IS_ACTIVE_KEY = "SearchControllerIsActiveKey";
    private static final String SEARCH_BAR_TEXT_KEY = "SearchBarTextKey";
    private static final String SEARCH_BAR_IS_FIRST_RESPONDER_KEY = "SearchBarIsFirstResponderKey";

    @Override
    public void encodeRestorableState(NSCoder coder) {
        super.encodeRestorableState(coder);

        // encode the view state so it can be restored later

        // encode the title
        coder.encodeString(VIEW_CONTROLLER_TITLE_KEY, getTitle());

        // encode the search controller's active state
        boolean searchDisplayControllerIsActive = searchController.isActive();
        coder.encodeBoolean(SEARCH_CONTROLLER_IS_ACTIVE_KEY, searchDisplayControllerIsActive);

        // encode the first responser status
        if (searchDisplayControllerIsActive) {
            coder.encodeBoolean(SEARCH_BAR_IS_FIRST_RESPONDER_KEY, searchController.getSearchBar().isFirstResponder());
        }

        // encode the search bar text
        coder.encodeString(SEARCH_BAR_TEXT_KEY, searchController.getSearchBar().getText());
    }

    @Override
    public void decodeRestorableState(NSCoder coder) {
        super.decodeRestorableState(coder);

        // restore the title
        setTitle(coder.decodeString(VIEW_CONTROLLER_TITLE_KEY));

        // restore the active state:
        // we can't make the searchController active here since it's not part of
        // the view
        // hierarchy yet, instead we do it in viewWillAppear
        searchControllerWasActive = coder.decodeBoolean(SEARCH_CONTROLLER_IS_ACTIVE_KEY);

        // restore the first responder status:
        // we can't make the searchController first responder here since it's
        // not part of the view
        // hierarchy yet, instead we do it in viewWillAppear
        searchControllerSearchFieldWasFirstResponder = coder.decodeBoolean(SEARCH_BAR_IS_FIRST_RESPONDER_KEY);

        // restore the text in the search field
        searchController.getSearchBar().setText(coder.decodeString(SEARCH_BAR_TEXT_KEY));
    }
}
