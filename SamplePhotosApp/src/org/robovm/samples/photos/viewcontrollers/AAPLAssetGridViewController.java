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

import org.robovm.apple.coregraphics.CGBlendMode;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.dispatch.DispatchQueue;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSIndexSet;
import org.robovm.apple.foundation.NSMutableArray;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.photos.PHAsset;
import org.robovm.apple.photos.PHAssetChangeRequest;
import org.robovm.apple.photos.PHAssetCollection;
import org.robovm.apple.photos.PHAssetCollectionChangeRequest;
import org.robovm.apple.photos.PHCachingImageManager;
import org.robovm.apple.photos.PHChange;
import org.robovm.apple.photos.PHCollectionEditOperation;
import org.robovm.apple.photos.PHFetchResult;
import org.robovm.apple.photos.PHFetchResultChangeDetails;
import org.robovm.apple.photos.PHImageContentMode;
import org.robovm.apple.photos.PHPhotoLibrary;
import org.robovm.apple.photos.PHPhotoLibraryChangeObserver;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonSystemItem;
import org.robovm.apple.uikit.UICollectionView;
import org.robovm.apple.uikit.UICollectionViewCell;
import org.robovm.apple.uikit.UICollectionViewController;
import org.robovm.apple.uikit.UICollectionViewFlowLayout;
import org.robovm.apple.uikit.UICollectionViewLayoutAttributes;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIEdgeInsets;
import org.robovm.apple.uikit.UIGraphics;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIScrollView;
import org.robovm.objc.block.VoidBlock1;
import org.robovm.objc.block.VoidBlock2;
import org.robovm.rt.bro.ptr.BooleanPtr;
import org.robovm.samples.photos.views.AAPLGridViewCell;

public class AAPLAssetGridViewController extends UICollectionViewController implements PHPhotoLibraryChangeObserver {
    private static final String CellReuseIdentifier = "Cell";
    private static final UICollectionViewFlowLayout collectionViewLayout = new UICollectionViewFlowLayout();

    static {
        collectionViewLayout.setItemSize(new CGSize(80, 80));
        collectionViewLayout.setMinimumLineSpacing(0);
        collectionViewLayout.setMinimumInteritemSpacing(0);
        collectionViewLayout.setHeaderReferenceSize(new CGSize());
        collectionViewLayout.setFooterReferenceSize(new CGSize());
        collectionViewLayout.setSectionInset(new UIEdgeInsets());
    }

    private final CGSize ASSET_GRID_THUMBNAIL_SIZE;
    private final AAPLAssetViewController assetViewController;

    private PHFetchResult assetsFetchResults;
    private PHAssetCollection assetCollection;

    private final UIBarButtonItem addButton;
    private final PHCachingImageManager imageManager = new PHCachingImageManager();
    private CGRect previousPreheatRect;

    public AAPLAssetGridViewController () {
        super(collectionViewLayout);

        assetViewController = new AAPLAssetViewController();

        getCollectionView().registerReusableCellClass(AAPLGridViewCell.class, CellReuseIdentifier);
        getCollectionView().setBackgroundColor(UIColor.white());

        addButton = new UIBarButtonItem(UIBarButtonSystemItem.Add, new UIBarButtonItem.OnClickListener() {
            @Override
            public void onClick (UIBarButtonItem barButtonItem) {
                addButtonPressed();
            }
        });
        getNavigationItem().setRightBarButtonItem(addButton);

        resetCachedAssets();
        PHPhotoLibrary.getSharedPhotoLibrary().registerChangeObserver(this);

        double scale = UIScreen.getMainScreen().getScale();
        CGSize cellSize = ((UICollectionViewFlowLayout)getCollectionViewLayout()).getItemSize();
        ASSET_GRID_THUMBNAIL_SIZE = new CGSize(cellSize.getWidth() * scale, cellSize.getHeight() * scale);
    }

    @Override
    protected void dispose (boolean finalizing) {
        PHPhotoLibrary.getSharedPhotoLibrary().unregisterChangeObserver(this);
        super.dispose(finalizing);
    }

    @Override
    public void viewWillAppear (boolean animated) {
        super.viewWillAppear(animated);

        if (assetCollection == null || assetCollection.canPerformEditOperation(PHCollectionEditOperation.AddContent)) {
            getNavigationItem().setRightBarButtonItem(addButton);
        } else {
            getNavigationItem().setRightBarButtonItem(null);
        }
    }

    @Override
    public void viewDidAppear (boolean animated) {
        super.viewDidAppear(animated);
        updateCachedAssets();
    }

    @Override
    public void didChange (final PHChange changeInstance) {
        // Call might come on any background queue. Re-dispatch to the main queue to handle it.
        DispatchQueue.getMainQueue().async(new Runnable() {
            @Override
            public void run () {
                // check if there are changes to the assets (insertions, deletions, updates)
                final PHFetchResultChangeDetails collectionChanges = changeInstance
                    .getChangeDetailsForFetchResult(assetsFetchResults);
                if (collectionChanges != null) {
                    // get the new fetch result
                    assetsFetchResults = collectionChanges.getFetchResultAfterChanges();

                    final UICollectionView collectionView = getCollectionView();

                    if (!collectionChanges.hasIncrementalChanges() || collectionChanges.hasMoves()) {
                        // we need to reload all if the incremental diffs are not available
                        collectionView.reloadData();
                    } else {
                        // if we have incremental diffs, tell the collection view to animate insertions and deletions
                        collectionView.performBatchUpdates(new Runnable() {
                            @Override
                            public void run () {
                                NSIndexSet removedIndexes = collectionChanges.getRemovedIndexes();
                                if (removedIndexes != null && removedIndexes.size() > 0) {
                                    collectionView.deleteItems(getIndexPathsFromIndexesWithSection(removedIndexes, 0));
                                }
                                NSIndexSet insertedIndexes = collectionChanges.getInsertedIndexes();
                                if (insertedIndexes != null && insertedIndexes.size() > 0) {
                                    collectionView.insertItems(getIndexPathsFromIndexesWithSection(insertedIndexes, 0));
                                }
                                NSIndexSet changedIndexes = collectionChanges.getChangedIndexes();
                                if (changedIndexes != null && changedIndexes.size() > 0) {
                                    collectionView.reloadItems(getIndexPathsFromIndexesWithSection(changedIndexes, 0));
                                }
                            }
                        }, null);
                    }
                }
                resetCachedAssets();
            }
        });
    }

    @Override
    public long getNumberOfItemsInSection (UICollectionView collectionView, long section) {
        return assetsFetchResults.size();
    }

    @Override
    public UICollectionViewCell getCellForItem (UICollectionView collectionView, NSIndexPath indexPath) {
        final AAPLGridViewCell cell = (AAPLGridViewCell)collectionView.dequeueReusableCell(CellReuseIdentifier, indexPath);

        // Increment the cell's tag
        final long currentTag = cell.getTag() + 1;
        cell.setTag(currentTag);

        PHAsset asset = (PHAsset)assetsFetchResults.get((int)indexPath.getItem());
        imageManager.requestImageForAsset(asset, ASSET_GRID_THUMBNAIL_SIZE, PHImageContentMode.AspectFill, null,
            new VoidBlock2<UIImage, NSDictionary<NSString, NSObject>>() {
                @Override
                public void invoke (UIImage result, NSDictionary<NSString, NSObject> b) {
                    // Only update the thumbnail if the cell tag hasn't changed. Otherwise, the cell has been re-used.
                    if (cell.getTag() == currentTag) {
                        cell.setThumbnailImage(result);
                    }
                }
            });
        return cell;
    }

    @Override
    public void didSelectItem (UICollectionView collectionView, NSIndexPath indexPath) {
        assetViewController.setAsset((PHAsset)assetsFetchResults.get((int)indexPath.getItem()));
        assetViewController.setAssetCollection(assetCollection);

        getNavigationController().pushViewController(assetViewController, true);
    }

    @Override
    public void didScroll (UIScrollView scrollView) {
        updateCachedAssets();
    }

    private void resetCachedAssets () {
        imageManager.stopCachingImagesForAllAssets();
        previousPreheatRect = CGRect.Zero();
    }

    private void updateCachedAssets () {
        boolean isViewVisible = isViewLoaded() && getView().getWindow() != null;
        if (!isViewVisible) return;

        // The preheat window is twice the height of the visible rect
        CGRect preheatRect = getCollectionView().getBounds();
        preheatRect = preheatRect.inset(0.0, -0.5 * preheatRect.getHeight());

        // If scrolled by a "reasonable" amount...
        double delta = Math.abs(preheatRect.getMidY() - previousPreheatRect.getMidY());
        if (delta > getCollectionView().getBounds().getHeight() / 3.0) {
            // Compute the assets to start caching and to stop caching.
            final NSArray<NSIndexPath> addedIndexPaths = new NSMutableArray<>();
            final NSArray<NSIndexPath> removedIndexPaths = new NSMutableArray<>();

            computeDifferenceBetweenRects(previousPreheatRect, preheatRect, new VoidBlock1<CGRect>() {
                @Override
                public void invoke (CGRect removedRect) {
                    NSArray<NSIndexPath> indexPaths = getIndexPathsForElementsInRect(removedRect);
                    if (indexPaths != null) {
                        removedIndexPaths.addAll(indexPaths);
                    }
                }
            }, new VoidBlock1<CGRect>() {
                @Override
                public void invoke (CGRect addedRect) {
                    NSArray<NSIndexPath> indexPaths = getIndexPathsForElementsInRect(addedRect);
                    if (indexPaths != null) {
                        addedIndexPaths.addAll(indexPaths);
                    }
                }
            });

            NSArray<PHAsset> assetsToStartCaching = getAssetsAtIndexPaths(addedIndexPaths);
            NSArray<PHAsset> assetsToStopCaching = getAssetsAtIndexPaths(removedIndexPaths);

            imageManager.startCachingImagesForAssets(assetsToStartCaching, ASSET_GRID_THUMBNAIL_SIZE,
                PHImageContentMode.AspectFill, null);
            imageManager.stopCachingImagesForAssets(assetsToStopCaching, ASSET_GRID_THUMBNAIL_SIZE,
                PHImageContentMode.AspectFill, null);

            previousPreheatRect = preheatRect;
        }
    }

    private void computeDifferenceBetweenRects (CGRect oldRect, CGRect newRect, VoidBlock1<CGRect> removedHandler,
        VoidBlock1<CGRect> addedHandler) {
        if (newRect.intersects(oldRect)) {
            double oldMaxY = oldRect.getMaxY();
            double oldMinY = oldRect.getMinY();
            double newMaxY = newRect.getMaxY();
            double newMinY = newRect.getMinY();

            if (newMaxY > oldMaxY) {
                CGRect rectToAdd = new CGRect(newRect.getOrigin().getX(), oldMaxY, newRect.getSize().getWidth(), newMaxY
                    - oldMaxY);
                addedHandler.invoke(rectToAdd);
            }
            if (oldMinY > newMinY) {
                CGRect rectToAdd = new CGRect(newRect.getOrigin().getX(), newMinY, newRect.getSize().getWidth(), oldMinY
                    - newMinY);
                addedHandler.invoke(rectToAdd);
            }
            if (newMaxY < oldMaxY) {
                CGRect rectToRemove = new CGRect(newRect.getOrigin().getX(), newMaxY, newRect.getSize().getWidth(), oldMaxY
                    - newMaxY);
                removedHandler.invoke(rectToRemove);
            }
            if (oldMinY < newMinY) {
                CGRect rectToRemove = new CGRect(newRect.getOrigin().getX(), oldMinY, newRect.getSize().getWidth(), newMinY
                    - oldMinY);
                removedHandler.invoke(rectToRemove);
            }
        } else {
            addedHandler.invoke(newRect);
            removedHandler.invoke(oldRect);
        }
    }

    private NSArray<PHAsset> getAssetsAtIndexPaths (NSArray<NSIndexPath> indexPaths) {
        if (indexPaths.size() == 0) return null;

        NSArray<PHAsset> assets = new NSMutableArray<>();
        for (NSIndexPath indexPath : indexPaths) {
            PHAsset asset = (PHAsset)assetsFetchResults.get((int)indexPath.getItem());
            assets.add(asset);
        }
        return assets;
    }

    private void addButtonPressed () {
        // Create a random dummy image.
        CGRect rect = Math.random() % 2 == 0 ? new CGRect(0, 0, 400, 300) : new CGRect(0, 0, 300, 400);
        UIGraphics.beginImageContext(rect.getSize(), false, 1.0);
        UIColor.fromHSBA(Math.random() % 100 / 100, 1, 1, 1).setFill();
        UIGraphics.rectFill(rect, CGBlendMode.Normal);
        final UIImage image = UIGraphics.getImageFromCurrentImageContext();
        UIGraphics.endImageContext();

        // Add it to the photo library
        PHPhotoLibrary.getSharedPhotoLibrary().performChanges(new Runnable() {
            @Override
            public void run () {
                PHAssetChangeRequest assetChangeRequest = PHAssetChangeRequest.createImageAssetCreationRequest(image);

                if (assetCollection != null) {
                    PHAssetCollectionChangeRequest assetCollectionChangeRequest = PHAssetCollectionChangeRequest.create(assetCollection);
                    assetCollectionChangeRequest.addAssets(new NSArray<>(assetChangeRequest.getPlaceholderForCreatedAsset()));
                }
            }
        }, new VoidBlock2<Boolean, NSError>() {
            @Override
            public void invoke (Boolean success, NSError error) {
                if (!success) {
                    System.err.println("Error creating asset: " + error);
                }
            }
        });
    }

    private NSArray<NSIndexPath> getIndexPathsFromIndexesWithSection (NSIndexSet indexSet, final long section) {
        final NSArray<NSIndexPath> indexPaths = new NSMutableArray<>(indexSet.size());
        indexSet.enumerateIndexes(new VoidBlock2<Long, BooleanPtr>() {
            @Override
            public void invoke (Long idx, BooleanPtr stop) {
                indexPaths.add(NSIndexPath.createWithItem(idx, section));
            }
        });
        return indexPaths;
    }

    private NSArray<NSIndexPath> getIndexPathsForElementsInRect (CGRect rect) {
        NSArray<UICollectionViewLayoutAttributes> allLayoutAttributes = getCollectionViewLayout().getLayoutAttributesForElements(
            rect);
        if (allLayoutAttributes.size() == 0) return null;
        NSArray<NSIndexPath> indexPaths = new NSMutableArray<>(allLayoutAttributes.size());
        for (UICollectionViewLayoutAttributes layoutAttributes : allLayoutAttributes) {
            NSIndexPath indexPath = layoutAttributes.getIndexPath();
            indexPaths.add(indexPath);
        }
        return indexPaths;
    }

    public void setAssetsFetchResults (PHFetchResult assetsFetchResults) {
        this.assetsFetchResults = assetsFetchResults;
    }

    public void setAssetCollection (PHAssetCollection assetCollection) {
        this.assetCollection = assetCollection;
    }
}
