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

import org.robovm.apple.avfoundation.AVLayerVideoGravity;
import org.robovm.apple.avfoundation.AVPlayer;
import org.robovm.apple.avfoundation.AVPlayerItem;
import org.robovm.apple.avfoundation.AVPlayerLayer;
import org.robovm.apple.coreanimation.CALayer;
import org.robovm.apple.coregraphics.CGImage;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.coreimage.CIContext;
import org.robovm.apple.coreimage.CIFilter;
import org.robovm.apple.coreimage.CIFilterInputParameters;
import org.robovm.apple.coreimage.CIImage;
import org.robovm.apple.dispatch.DispatchQueue;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.foundation.NSStringEncoding;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.imageio.CGImagePropertyOrientation;
import org.robovm.apple.opengles.EAGLContext;
import org.robovm.apple.opengles.EAGLRenderingAPI;
import org.robovm.apple.photos.PHAdjustmentData;
import org.robovm.apple.photos.PHAsset;
import org.robovm.apple.photos.PHAssetChangeRequest;
import org.robovm.apple.photos.PHAssetCollection;
import org.robovm.apple.photos.PHAssetCollectionChangeRequest;
import org.robovm.apple.photos.PHAssetEditOperation;
import org.robovm.apple.photos.PHAssetMediaType;
import org.robovm.apple.photos.PHChange;
import org.robovm.apple.photos.PHCollectionEditOperation;
import org.robovm.apple.photos.PHContentEditingInputRequestOptions;
import org.robovm.apple.photos.PHContentEditingOutput;
import org.robovm.apple.photos.PHImageContentMode;
import org.robovm.apple.photos.PHImageManager;
import org.robovm.apple.photos.PHImageRequestOptions;
import org.robovm.apple.photos.PHObjectChangeDetails;
import org.robovm.apple.photos.PHPhotoLibrary;
import org.robovm.apple.photos.PHPhotoLibraryChangeObserver;
import org.robovm.apple.uikit.UIAlertAction;
import org.robovm.apple.uikit.UIAlertActionStyle;
import org.robovm.apple.uikit.UIAlertController;
import org.robovm.apple.uikit.UIAlertControllerStyle;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageOrientation;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIModalPresentationStyle;
import org.robovm.apple.uikit.UIPopoverArrowDirection;
import org.robovm.apple.uikit.UIProgressView;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import org.robovm.objc.annotation.IBOutlet;
import org.robovm.objc.block.VoidBlock2;

@CustomClass("AAPLAssetViewController")
public class AAPLAssetViewController extends UIViewController implements PHPhotoLibraryChangeObserver {
    private static final String AdjustmentFormatIdentifier = "com.example.apple-samplecode.SamplePhotosApp";

    private PHAsset asset;
    private PHAssetCollection assetCollection;

    @IBOutlet
    private UIImageView imageView;
    @IBOutlet
    private UIBarButtonItem playButton;
    @IBOutlet
    private UIBarButtonItem space;
    @IBOutlet
    private UIBarButtonItem trashButton;
    @IBOutlet
    private UIBarButtonItem editButton;
    @IBOutlet
    private UIProgressView progressView;
    private AVPlayerLayer playerLayer;
    private CGSize lastImageViewSize;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        PHPhotoLibrary.getSharedPhotoLibrary().registerChangeObserver(this);
    }

    @Override
    protected void dispose(boolean finalizing) {
        PHPhotoLibrary.getSharedPhotoLibrary().unregisterChangeObserver(this);
        super.dispose(finalizing);
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);

        if (asset.getMediaType() == PHAssetMediaType.Video) {
            setToolbarItems(new NSArray<>(playButton, space, trashButton));
        } else {
            setToolbarItems(new NSArray<>(space, trashButton));
        }

        boolean isEditable = asset.canPerformEditOperation(PHAssetEditOperation.Properties)
                || asset.canPerformEditOperation(PHAssetEditOperation.Content);
        editButton.setEnabled(isEditable);

        boolean isTrashable = false;
        if (assetCollection != null) {
            isTrashable = assetCollection.canPerformEditOperation(PHCollectionEditOperation.RemoveContent);
        } else {
            isTrashable = asset.canPerformEditOperation(PHAssetEditOperation.Delete);
        }
        trashButton.setEnabled(isTrashable);

        getView().layoutIfNeeded();
        updateImage();
    }

    @Override
    public void viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews();

        if (lastImageViewSize == null || !imageView.getBounds().getSize().equalsTo(lastImageViewSize)) {
            updateImage();
        }
    }

    private void updateImage() {
        lastImageViewSize = imageView.getBounds().getSize();

        double scale = UIScreen.getMainScreen().getScale();
        CGSize targetSize = new CGSize(imageView.getBounds().getWidth() * scale, imageView.getBounds().getHeight()
                * scale);

        PHImageRequestOptions options = new PHImageRequestOptions();

        // Download from cloud if necessary
        options.setNetworkAccessAllowed(true);
        options.setProgressHandler((progress, error, c, d) -> {
            DispatchQueue.getMainQueue().async(() -> {
                progressView.setProgress(progress.floatValue());
                progressView.setHidden(progress <= 0 || progress >= 1);
            });
        });
        PHImageManager.getDefaultManager().requestImageForAsset(asset, targetSize, PHImageContentMode.AspectFill,
                options, (image, result) -> {
                    if (image != null) {
                        imageView.setImage(image);
                    }
                });
    }

    @Override
    public void didChange(final PHChange changeInstance) {
        // Call might come on any background queue. Re-dispatch to the main
        // queue to handle it.
        DispatchQueue.getMainQueue().async(() -> {
            // check if there are changes to the album we're interested on
            // (to its metadata, not to its collection of assets)
                PHObjectChangeDetails<PHAsset> changeDetails = changeInstance.getChangeDetailsForObject(asset);
                if (changeDetails != null) {
                    // it changed, we need to fetch a new one
                    asset = changeDetails.getObjectAfterChanges();
                    if (changeDetails.assetContentChanged()) {
                        updateImage();

                        if (playerLayer != null) {
                            playerLayer.removeFromSuperlayer();
                            playerLayer = null;
                        }
                    }
                }
            });
    }

    private void applyFilter(final String filterName) {
        PHContentEditingInputRequestOptions options = new PHContentEditingInputRequestOptions();
        options.setCanHandleAdjustmentData((data) -> {
            return data.getFormatIdentifier().equals(AdjustmentFormatIdentifier)
                    && data.getFormatVersion().equals("1.0");
        });
        asset.requestContentEditingInput(options, (input, result) -> {
            // Get full image
                NSURL url = input.getFullSizeImageURL();
                CGImagePropertyOrientation orientation = input.getFullSizeImageOrientation();
                CIImage inputImage = new CIImage(url, null);
                inputImage = inputImage.newImageByApplyingOrientation(orientation);

                // Add filter
                CIFilterInputParameters inputParameters = new CIFilterInputParameters()
                        .setInputImage(inputImage);
                CIFilter filter = new CIFilter(filterName, inputParameters);
                filter.setDefaults();
                CIImage outputImage = filter.getOutputImage();

                // Create editing output
                NSData jpegData = getJPEGRepresentationWithCompressionQuality(outputImage, 0.9);
                PHAdjustmentData adjustmentData = null;
                adjustmentData = new PHAdjustmentData(AdjustmentFormatIdentifier, "1.0", NSString.toData(
                        filterName, NSStringEncoding.UTF8));

                final PHContentEditingOutput contentEditingOutput = new PHContentEditingOutput(input);
                jpegData.write(contentEditingOutput.getRenderedContentURL(), true);
                contentEditingOutput.setAdjustmentData(adjustmentData);

                PHPhotoLibrary.getSharedPhotoLibrary().performChanges(() -> {
                    PHAssetChangeRequest request = new PHAssetChangeRequest(asset);
                    request.setContentEditingOutput(contentEditingOutput);
                }, (success, error) -> {
                    if (!success) {
                        System.err.println("Error: " + error);
                    }
                });
            });
    }

    @IBAction
    private void handleEditButtonItem(NSObject sender) {
        UIAlertController alertController = new UIAlertController(null, null, UIAlertControllerStyle.ActionSheet);
        alertController.addAction(new UIAlertAction(NSString.getLocalizedString("Cancel"),
                UIAlertActionStyle.Cancel, null));

        if (asset.canPerformEditOperation(PHAssetEditOperation.Properties)) {
            String favoriteActionTitle = !asset.isFavorite() ? NSString.getLocalizedString("Favorite") : NSString
                    .getLocalizedString("Unfavorite");
            alertController.addAction(new UIAlertAction(favoriteActionTitle, UIAlertActionStyle.Default,
                    (action) -> {
                        PHPhotoLibrary.getSharedPhotoLibrary().performChanges(() -> {
                            PHAssetChangeRequest request = new PHAssetChangeRequest(asset);
                            request.setFavorite(!asset.isFavorite());
                        }, (success, error) -> {
                            if (!success) {
                                System.err.println("Error: " + error);
                            }
                        });
                    }));
        }
        if (asset.canPerformEditOperation(PHAssetEditOperation.Content)) {
            if (asset.getMediaType() == PHAssetMediaType.Image) {
                alertController.addAction(new UIAlertAction(NSString.getLocalizedString("Sepia"),
                        UIAlertActionStyle.Default,
                        (action) -> applyFilter("CISepiaTone")));
                alertController.addAction(new UIAlertAction(NSString.getLocalizedString("Chrome"),
                        UIAlertActionStyle.Default,
                        (action) -> applyFilter("CIPhotoEffectChrome")));
            }
            alertController.addAction(new UIAlertAction(NSString.getLocalizedString("Revert"),
                    UIAlertActionStyle.Default,
                    (action) -> {
                        PHPhotoLibrary.getSharedPhotoLibrary().performChanges(() -> {
                            PHAssetChangeRequest request = new PHAssetChangeRequest(asset);
                            request.revertAssetContentToOriginal();
                        }, (success, error) -> {
                            if (!success) {
                                System.err.println("Error: " + error);
                            }
                        });
                    }));
        }
        alertController.setModalPresentationStyle(UIModalPresentationStyle.Popover);
        presentViewController(alertController, true, null);
        if (alertController.getPopoverPresentationController() != null) {
            alertController.getPopoverPresentationController().setBarButtonItem(editButton);
            alertController.getPopoverPresentationController().setPermittedArrowDirections(UIPopoverArrowDirection.Up);
        }
    }

    @IBAction
    private void handleTrashButtonItem(NSObject sender) {
        VoidBlock2<Boolean, NSError> completionHandler = (success, error) -> {
            if (success) {
                DispatchQueue.getMainQueue().async(() -> getNavigationController().popViewController(true));
            } else {
                System.err.println("Error: " + error);
            }
        };
        if (assetCollection != null) {
            // Remove asset from album
            PHPhotoLibrary.getSharedPhotoLibrary().performChanges(() -> {
                PHAssetCollectionChangeRequest changeRequest = new PHAssetCollectionChangeRequest(assetCollection);
                changeRequest.removeAssets(new NSArray<>(asset));
            }, completionHandler);
        } else {
            // Delete asset from library
            PHPhotoLibrary.getSharedPhotoLibrary().performChanges(
                    () -> PHAssetChangeRequest.deleteAssets(new NSArray<>(asset)), completionHandler);
        }
    }

    @IBAction
    private void handlePlayButtonItem(NSObject sender) {
        if (playerLayer == null) {
            PHImageManager.getDefaultManager().requestAVAssetForVideo(asset, null,
                    (asset, mix, result) -> {
                        DispatchQueue.getMainQueue().async(() -> {
                            if (playerLayer == null) {
                                AVPlayerItem playerItem = new AVPlayerItem(asset);
                                playerItem.setAudioMix(mix);
                                AVPlayer player = new AVPlayer(playerItem);
                                AVPlayerLayer playerLayer = new AVPlayerLayer(player);
                                playerLayer.setVideoGravity(AVLayerVideoGravity.ResizeAspect);

                                CALayer layer = getView().getLayer();
                                layer.addSublayer(playerLayer);
                                playerLayer.setFrame(layer.getBounds());
                                player.play();
                            }
                        });
                    });
        } else {
            playerLayer.getPlayer().play();
        }
    }

    private NSData getJPEGRepresentationWithCompressionQuality(CIImage image, double compressionQuality) {
        EAGLContext eaglContext = new EAGLContext(EAGLRenderingAPI.OpenGLES2);
        CIContext ciContext = new CIContext(eaglContext);
        CGImage outputImage = ciContext.createCGImage(image, image.getExtent());
        UIImage uiImage = new UIImage(outputImage, 1.0, UIImageOrientation.Up);
        NSData jpegRepresentation = uiImage.toJPEGData(compressionQuality);
        return jpegRepresentation;
    }

    public void setAsset(PHAsset asset) {
        this.asset = asset;
    }

    public void setAssetCollection(PHAssetCollection assetCollection) {
        this.assetCollection = assetCollection;
    }
}
