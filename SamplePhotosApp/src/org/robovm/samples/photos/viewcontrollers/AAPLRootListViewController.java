/*
 * Copyright (C) 2015 Trillian Mobile AB
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
 * Portions of this code is based on Apple Inc's Example app using Photos framework (v2.0)
 * which is copyright (C) 2014 Apple Inc.
 */

package org.robovm.samples.photos.viewcontrollers;

import java.util.Arrays;
import java.util.List;

import org.robovm.apple.dispatch.DispatchQueue;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSMutableArray;
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
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonSystemItem;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellAccessoryType;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.apple.uikit.UITableViewStyle;
import org.robovm.apple.uikit.UITextField;
import org.robovm.objc.block.VoidBlock1;
import org.robovm.objc.block.VoidBlock2;

public class AAPLRootListViewController extends UITableViewController implements PHPhotoLibraryChangeObserver {
    private static final String AllPhotosReuseIdentifier = "AllPhotosCell";
    private static final String CollectionCellReuseIdentifier = "CollectionCell";

    private final AAPLAssetGridViewController assetGridViewController;
    private NSArray<PHFetchResult> collectionsFetchResults;
    private final List<String> collectionsLocalizedTitles;

    public AAPLRootListViewController () {
        setTitle("Photos");

        assetGridViewController = new AAPLAssetGridViewController();

        UITableView tableView = new UITableView(getView().getBounds(), UITableViewStyle.Grouped);
        tableView.registerReusableCellClass(UITableViewCell.class, AllPhotosReuseIdentifier);
        tableView.registerReusableCellClass(UITableViewCell.class, CollectionCellReuseIdentifier);
        setTableView(tableView);

        getNavigationItem().setRightBarButtonItem(
            new UIBarButtonItem(UIBarButtonSystemItem.Add, new UIBarButtonItem.OnClickListener() {
                @Override
                public void onClick (UIBarButtonItem barButtonItem) {
                    addButtonPressed();
                }
            }));

        PHFetchResult smartAlbums = PHAssetCollection.fetchAssetCollectionsWithType(PHAssetCollectionType.SmartAlbum,
            PHAssetCollectionSubtype.AlbumRegular, null);
        PHFetchResult topLevelUserCollections = PHCollectionList.fetchTopLevelUserCollections(null);

        collectionsFetchResults = new NSArray<>(smartAlbums, topLevelUserCollections);
        collectionsLocalizedTitles = Arrays.asList(NSString.getLocalizedString("Smart Albums"),
            NSString.getLocalizedString("Albums"));

        PHPhotoLibrary.getSharedPhotoLibrary().registerChangeObserver(this);
    }

    @Override
    protected void dispose (boolean finalizing) {
        PHPhotoLibrary.getSharedPhotoLibrary().unregisterChangeObserver(this);
        super.dispose(finalizing);
    }

    private void showAllPhotos () {
        // Fetch all assets, sorted by date created.
        PHFetchOptions options = new PHFetchOptions();
        options.setSortDescriptors(new NSArray<>(new NSSortDescriptor("creationDate", true)));
        assetGridViewController.setAssetsFetchResults(PHAsset.fetchAssets(options));
        assetGridViewController.setAssetCollection(null);
        assetGridViewController.getCollectionView().reloadData();

        getNavigationController().pushViewController(assetGridViewController, true);
    }

    private void showCollection (NSIndexPath indexPath) {
        PHFetchResult fetchResult = collectionsFetchResults.get((int)indexPath.getSection() - 1);
        PHCollection collection = (PHCollection)fetchResult.get((int)indexPath.getRow());

        if (collection instanceof PHAssetCollection) {
            PHAssetCollection assetCollection = (PHAssetCollection)collection;
            PHFetchResult assetsFetchResults = PHAsset.fetchAssetsInAssetCollection(assetCollection, null);
            assetGridViewController.setAssetsFetchResults(assetsFetchResults);
            assetGridViewController.setAssetCollection(assetCollection);
            assetGridViewController.getCollectionView().reloadData();

            getNavigationController().pushViewController(assetGridViewController, true);
        }

    }

    @Override
    public long getNumberOfSections (UITableView tableView) {
        return 1 + collectionsFetchResults.size();
    }

    @Override
    public long getNumberOfRowsInSection (UITableView tableView, long section) {
        int numberOfRows = 0;
        if (section == 0) {
            numberOfRows = 1; // "All Photos" section
        } else {
            PHFetchResult fetchResult = collectionsFetchResults.get((int)section - 1);
            numberOfRows = (int)fetchResult.size();
        }
        return numberOfRows;
    }

    @Override
    public UITableViewCell getCellForRow (UITableView tableView, NSIndexPath indexPath) {
        UITableViewCell cell = null;
        String localizedTitle = null;

        if (indexPath.getSection() == 0) {
            cell = tableView.dequeueReusableCell(AllPhotosReuseIdentifier, indexPath);
            localizedTitle = NSString.getLocalizedString("All Photos");
        } else {
            cell = tableView.dequeueReusableCell(CollectionCellReuseIdentifier, indexPath);
            PHFetchResult fetchResult = collectionsFetchResults.get((int)indexPath.getSection() - 1);
            PHCollection collection = (PHCollection)fetchResult.get((int)indexPath.getRow());
            localizedTitle = collection.getLocalizedTitle();
        }
        cell.setAccessoryType(UITableViewCellAccessoryType.DisclosureIndicator);
        cell.getTextLabel().setText(localizedTitle);

        return cell;
    }

    @Override
    public void didSelectRow (UITableView tableView, NSIndexPath indexPath) {
        long section = indexPath.getSection();
        if (section == 0) {
            showAllPhotos();
        } else {
            showCollection(indexPath);
        }
    }

    @Override
    public String getTitleForHeader (UITableView tableView, long section) {
        String title = null;
        if (section > 0) {
            title = collectionsLocalizedTitles.get((int)section - 1);
        }
        return title;
    }

    @Override
    public void didChange (final PHChange changeInstance) {
        // Call might come on any background queue. Re-dispatch to the main queue to handle it.

        DispatchQueue.getMainQueue().async(new Runnable() {
            @SuppressWarnings("unchecked")
            @Override
            public void run () {
                NSMutableArray<PHFetchResult> updatedCollectionsFetchResults = null;

                for (PHFetchResult collectionsFetchResult : collectionsFetchResults) {
                    PHFetchResultChangeDetails changeDetails = changeInstance
                        .getChangeDetailsForFetchResult(collectionsFetchResult);

                    if (changeDetails != null) {
                        if (updatedCollectionsFetchResults == null) {
                            updatedCollectionsFetchResults = (NSMutableArray<PHFetchResult>)collectionsFetchResults.mutableCopy();
                        }
                        updatedCollectionsFetchResults.set(collectionsFetchResults.indexOf(collectionsFetchResult),
                            changeDetails.getFetchResultAfterChanges());
                    }
                }

                if (updatedCollectionsFetchResults != null) {
                    collectionsFetchResults = updatedCollectionsFetchResults;
                    getTableView().reloadData();
                }
            }
        });
    }

    private void addButtonPressed () {
        // Prompt user from new album title.
        final UIAlertController alertController = UIAlertController.create(NSString.getLocalizedString("New Album"), null,
            UIAlertControllerStyle.Alert);
        alertController.addAction(UIAlertAction.create(NSString.getLocalizedString("Cancel"), UIAlertActionStyle.Cancel, null));
        alertController.addTextField(new VoidBlock1<UITextField>() {
            @Override
            public void invoke (UITextField textField) {
                textField.setPlaceholder(NSString.getLocalizedString("Album Name"));
            }
        });
        alertController.addAction(UIAlertAction.create(NSString.getLocalizedString("Create"), UIAlertActionStyle.Default,
            new VoidBlock1<UIAlertAction>() {
                @Override
                public void invoke (UIAlertAction a) {
                    UITextField textField = alertController.getTextFields().first();
                    final String title = textField.getText();

                    // Create new album.
                    PHPhotoLibrary.getSharedPhotoLibrary().performChanges(new Runnable() {
                        @Override
                        public void run () {
                            PHAssetCollectionChangeRequest.createAssetCollectionCreationRequest(title);
                        }
                    }, new VoidBlock2<Boolean, NSError>() {
                        @Override
                        public void invoke (Boolean success, NSError error) {
                            if (!success) {
                                System.err.println("Error creating album: " + error);
                            }
                        }
                    });
                }
            }));

        presentViewController(alertController, true, null);
    }
}
