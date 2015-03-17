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
 * Portions of this code is based on Apple Inc's LazyTableImages sample (v1.5)
 * which is copyright (C) 2010-2014 Apple Inc.
 */

package org.robovm.samples.lazytableimages.viewcontrollers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIScrollView;
import org.robovm.apple.uikit.UIScrollViewDelegateAdapter;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.objc.block.VoidBlock1;
import org.robovm.samples.lazytableimages.operation.AppRecord;
import org.robovm.samples.lazytableimages.operation.IconDownloader;

public class RootViewController extends UITableViewController {
    private static final int CUSTOM_ROW_COUNT = 7;
    private static final String CELL_IDENTIFIER = "LazyTableCell";
    private static final String PLACEHOLDER_CELL_IDENTIFIER = "PlaceholderCell";

    public class MyTableViewCell extends UITableViewCell {
        @Override
        protected long init (UITableViewCellStyle style, String reuseIdentifier) {
            // ignore the style argument and force the creation with style UITableViewCellStyleSubtitle
            return super.init(UITableViewCellStyle.Subtitle, reuseIdentifier);
        }
    }

    private List<AppRecord> entries = new ArrayList<>();
    private final Map<Integer, IconDownloader> imageDownloadsInProgress = new HashMap<>();

    public RootViewController () {
        setTitle("Top Paid Apps");

        getTableView().setDelegate(new UIScrollViewDelegateAdapter() {
            /** Load images for all onscreen rows when scrolling is finished. */
            @Override
            public void didEndDragging (UIScrollView scrollView, boolean decelerate) {
                if (!decelerate) {
                    loadImagesForOnscreenRows();
                }
            }

            /** When scrolling stops, proceed to load the app icons that are on screen. */
            @Override
            public void didEndDecelerating (UIScrollView scrollView) {
                loadImagesForOnscreenRows();
            }
        });

        getTableView().registerReusableCellClass(MyTableViewCell.class, CELL_IDENTIFIER);
        getTableView().registerReusableCellClass(MyTableViewCell.class, PLACEHOLDER_CELL_IDENTIFIER);
    }

    private void terminateAllDownloads () {
        // terminate all pending download connections
        for (IconDownloader download : imageDownloadsInProgress.values()) {
            download.cancelDownload();
        }
        imageDownloadsInProgress.clear();
    }

    /** If this view controller is going away, we need to cancel all outstanding downloads. */
    @Override
    protected void dispose (boolean finalizing) {
        // terminate all pending download connections
        terminateAllDownloads();
        super.dispose(finalizing);
    }

    @Override
    public void didReceiveMemoryWarning () {
        super.didReceiveMemoryWarning();

        // terminate all pending download connections
        terminateAllDownloads();
    }

    /** Customize the number of rows in the table view. */
    @Override
    public long getNumberOfRowsInSection (UITableView tableView, long section) {
        int count = entries.size();

        // if there's no data yet, return enough rows to fill the screen
        if (count == 0) {
            return CUSTOM_ROW_COUNT;
        }
        return count;
    }

    @Override
    public UITableViewCell getCellForRow (UITableView tableView, NSIndexPath indexPath) {
        MyTableViewCell cell = null;

        int nodeCount = entries.size();

        if (nodeCount == 0 && indexPath.getRow() == 0) {
            // add a placeholder cell while waiting on table data
            cell = (MyTableViewCell)getTableView().dequeueReusableCell(PLACEHOLDER_CELL_IDENTIFIER, indexPath);
            cell.getDetailTextLabel().setText("Loading…");
        } else {
            cell = (MyTableViewCell)getTableView().dequeueReusableCell(CELL_IDENTIFIER, indexPath);

            // Leave cells empty if there's no data yet
            if (nodeCount > 0) {
                // Set up the cell representing the app
                AppRecord appRecord = entries.get((int)indexPath.getRow());

                cell.getTextLabel().setText(appRecord.appName);
                cell.getDetailTextLabel().setText(appRecord.artist);

                // Only load cached images; defer new downloads until scrolling ends
                if (appRecord.appIcon == null) {
                    if (!getTableView().isDragging() && !getTableView().isDecelerating()) {
                        startIconDownload(appRecord, indexPath);
                    }
                    // if a download is deferred or in progress, return a placeholder image
                    cell.getImageView().setImage(UIImage.create("Placeholder.png"));
                } else {
                    cell.getImageView().setImage(appRecord.appIcon);
                }
            }
        }
        return cell;
    }

    private void startIconDownload (AppRecord appRecord, final NSIndexPath indexPath) {
        IconDownloader iconDownloader = imageDownloadsInProgress.get((int)indexPath.getRow());
        if (iconDownloader == null) {
            iconDownloader = new IconDownloader(appRecord, new VoidBlock1<AppRecord>() {
                @Override
                public void invoke (AppRecord a) {
                    UITableViewCell cell = getTableView().getCellForRow(indexPath);

                    // Display the newly loaded image
                    cell.getImageView().setImage(a.appIcon);
                    // Remove the IconDownloader from the in progress list.
                    // This will result in it being deallocated.
                    imageDownloadsInProgress.remove((int)indexPath.getRow());
                }
            });
            imageDownloadsInProgress.put((int)indexPath.getRow(), iconDownloader);
            iconDownloader.startDownload();
        }
    }

    /** This method is used in case the user scrolled into a set of cells that don't have their app icons yet. */
    private void loadImagesForOnscreenRows () {
        if (entries.size() > 0) {
            NSArray<NSIndexPath> visiblePaths = getTableView().getIndexPathsForVisibleRows();
            for (NSIndexPath indexPath : visiblePaths) {
                AppRecord appRecord = entries.get((int)indexPath.getRow());

                // Avoid the app icon download if the app already has an icon
                if (appRecord.appIcon == null) {
                    startIconDownload(appRecord, indexPath);
                }
            }
        }
    }

    public void setEntries (List<AppRecord> entries) {
        this.entries = entries;
    }
}
