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
 * Portions of this code is based on Apple Inc's SamplePhotosApp sample (v2.0)
 * which is copyright (C) 2014 Apple Inc.
 */

package org.robovm.samples.samplephotosapp.ui;

import java.util.Arrays;
import java.util.List;

import org.robovm.apple.dispatch.DispatchQueue;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSMutableArray;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSSortDescriptor;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.photos.PHAsset;
import org.robovm.apple.photos.PHAssetCollection;
import org.robovm.apple.photos.PHAssetCollectionChangeRequest;
import org.robovm.apple.photos.PHAssetCollectionSubtype;
import org.robovm.apple.photos.PHAssetCollectionType;
import org.robovm.apple.photos.PHChange;
import org.robovm.apple.photos.PHCollection;
import org.robovm.apple.photos.PHCollectionList;
import org.robovm.apple.photos.PHFetchOptions;
import org.robovm.apple.photos.PHFetchResult;
import org.robovm.apple.photos.PHFetchResultChangeDetails;
import org.robovm.apple.photos.PHPhotoLibrary;
import org.robovm.apple.photos.PHPhotoLibraryChangeObserver;
import org.robovm.apple.uikit.UIAlertAction;
import org.robovm.apple.uikit.UIAlertActionStyle;
import org.robovm.apple.uikit.UIAlertController;
import org.robovm.apple.uikit.UIAlertControllerStyle;
import org.robovm.apple.uikit.UIStoryboardSegue;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.apple.uikit.UITextField;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;

@CustomClass("AAPLRootListViewController")
public class AAPLRootListViewController extends UITableViewController implements PHPhotoLibraryChangeObserver {
    private static final String AllPhotosReuseIdentifier = "AllPhotosCell";
    private static final String CollectionCellReuseIdentifier = "CollectionCell";

    private static final String AllPhotosSegue = "showAllPhotos";
    private static final String CollectionSegue = "showCollection";

    private NSArray<PHFetchResult<? extends PHCollection>> collectionsFetchResults;
    private List<String> collectionsLocalizedTitles;

    @SuppressWarnings("unchecked")
    @Override
    public void awakeFromNib() {
        PHFetchResult<PHAssetCollection> smartAlbums = PHAssetCollection.fetchAssetCollectionsWithType(
                PHAssetCollectionType.SmartAlbum,
                PHAssetCollectionSubtype.AlbumRegular, null);
        PHFetchResult<PHCollection> topLevelUserCollections = PHCollectionList.fetchTopLevelUserCollections(null);

        collectionsFetchResults = new NSArray<>(smartAlbums, topLevelUserCollections);
        collectionsLocalizedTitles = Arrays.asList(NSString.getLocalizedString("Smart Albums"),
                NSString.getLocalizedString("Albums"));

        PHPhotoLibrary.getSharedPhotoLibrary().registerChangeObserver(this);
    }

    @Override
    protected void dispose(boolean finalizing) {
        PHPhotoLibrary.getSharedPhotoLibrary().unregisterChangeObserver(this);
        super.dispose(finalizing);
    }

    @Override
    public void prepareForSegue(UIStoryboardSegue segue, NSObject sender) {
        if (segue.getIdentifier().equals(AllPhotosSegue)) {
            AAPLAssetGridViewController assetGridViewController = (AAPLAssetGridViewController) segue
                    .getDestinationViewController();
            // Fetch all assets, sorted by date created.
            PHFetchOptions options = new PHFetchOptions();
            options.setSortDescriptors(new NSArray<NSSortDescriptor>(new NSSortDescriptor("creationDate", true)));
            assetGridViewController.setAssetsFetchResults(PHAsset.fetchAssets(options));
        } else if (segue.getIdentifier().equals(CollectionSegue)) {
            AAPLAssetGridViewController assetGridViewController = (AAPLAssetGridViewController) segue
                    .getDestinationViewController();

            NSIndexPath indexPath = getTableView().getIndexPathForCell((UITableViewCell) sender);
            PHFetchResult<? extends PHCollection> fetchResult = collectionsFetchResults.get(indexPath.getSection() - 1);
            PHCollection collection = fetchResult.get(indexPath.getRow());
            if (collection instanceof PHAssetCollection) {
                PHAssetCollection assetCollection = (PHAssetCollection) collection;
                PHFetchResult<PHAsset> assetsFetchResult = PHAsset.fetchAssetsInAssetCollection(assetCollection, null);
                assetGridViewController.setAssetsFetchResults(assetsFetchResult);
                assetGridViewController.setAssetCollection(assetCollection);
            }
        }
    }

    @Override
    public long getNumberOfSections(UITableView tableView) {
        return 1 + collectionsFetchResults.size();
    }

    @Override
    public long getNumberOfRowsInSection(UITableView tableView, long section) {
        int numberOfRows = 0;
        if (section == 0) {
            numberOfRows = 1; // "All Photos" section
        } else {
            PHFetchResult<? extends PHCollection> fetchResult = collectionsFetchResults.get((int) section - 1);
            numberOfRows = (int) fetchResult.size();
        }
        return numberOfRows;
    }

    @Override
    public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
        UITableViewCell cell = null;
        String localizedTitle = null;

        if (indexPath.getSection() == 0) {
            cell = tableView.dequeueReusableCell(AllPhotosReuseIdentifier, indexPath);
            localizedTitle = NSString.getLocalizedString("All Photos");
        } else {
            cell = tableView.dequeueReusableCell(CollectionCellReuseIdentifier, indexPath);
            PHFetchResult<? extends PHCollection> fetchResult = collectionsFetchResults.get(indexPath.getSection() - 1);
            PHCollection collection = fetchResult.get(indexPath.getRow());
            localizedTitle = collection.getLocalizedTitle();
        }
        cell.getTextLabel().setText(localizedTitle);

        return cell;
    }

    @Override
    public String getTitleForHeader(UITableView tableView, long section) {
        String title = null;
        if (section > 0) {
            title = collectionsLocalizedTitles.get((int) section - 1);
        }
        return title;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void didChange(final PHChange changeInstance) {
        // Call might come on any background queue. Re-dispatch to the main
        // queue to handle it.

        DispatchQueue
                .getMainQueue()
                .async(() -> {
                    NSMutableArray<PHFetchResult<? extends PHCollection>> updatedCollectionsFetchResults = null;

                    for (PHFetchResult<? extends PHCollection> collectionsFetchResult : collectionsFetchResults) {
                        PHFetchResultChangeDetails<? extends PHCollection> changeDetails = changeInstance
                                .getChangeDetailsForFetchResult(collectionsFetchResult);

                        if (changeDetails != null) {
                            if (updatedCollectionsFetchResults == null) {
                                updatedCollectionsFetchResults = (NSMutableArray<PHFetchResult<? extends PHCollection>>) collectionsFetchResults
                                        .mutableCopy();
                            }
                            updatedCollectionsFetchResults.set(collectionsFetchResults.indexOf(collectionsFetchResult),
                                    changeDetails.getFetchResultAfterChanges());
                        }
                    }

                    if (updatedCollectionsFetchResults != null) {
                        collectionsFetchResults = updatedCollectionsFetchResults;
                        getTableView().reloadData();
                    }
                });
    }

    @IBAction
    private void handleAddButtonItem(NSObject sender) {
        // Prompt user from new album title.
        final UIAlertController alertController = new UIAlertController(NSString.getLocalizedString("New Album"),
                null, UIAlertControllerStyle.Alert);
        alertController.addAction(new UIAlertAction(NSString.getLocalizedString("Cancel"),
                UIAlertActionStyle.Cancel, null));
        alertController
                .addTextField((textField) -> textField.setPlaceholder(NSString.getLocalizedString("Album Name")));
        alertController.addAction(new UIAlertAction(NSString.getLocalizedString("Create"),
                UIAlertActionStyle.Default,
                (action) -> {
                    UITextField textField = alertController.getTextFields().first();
                    final String title = textField.getText();

                    // Create new album.
                PHPhotoLibrary.getSharedPhotoLibrary().performChanges(
                        () -> PHAssetCollectionChangeRequest.createAssetCollectionCreationRequest(title),
                        (success, error) -> {
                            if (!success) {
                                System.err.println("Error creating album: " + error);
                            }
                        });
            }));

        presentViewController(alertController, true, null);
    }
}
