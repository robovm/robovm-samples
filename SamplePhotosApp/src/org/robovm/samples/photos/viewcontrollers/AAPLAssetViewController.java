/*
 * Copyright (C) 2015 RoboVM AB
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

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.robovm.apple.avfoundation.AVAsset;
import org.robovm.apple.avfoundation.AVAudioMix;
import org.robovm.apple.avfoundation.AVLayerVideoGravity;
import org.robovm.apple.avfoundation.AVPlayer;
import org.robovm.apple.avfoundation.AVPlayerItem;
import org.robovm.apple.avfoundation.AVPlayerLayer;
import org.robovm.apple.coreanimation.CALayer;
import org.robovm.apple.coregraphics.CGImage;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.coreimage.CIContext;
import org.robovm.apple.coreimage.CIFilter;
import org.robovm.apple.coreimage.CIFilterInputParameters;
import org.robovm.apple.coreimage.CIImage;
import org.robovm.apple.dispatch.DispatchQueue;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSString;
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
import org.robovm.apple.photos.PHContentEditingInput;
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
import org.robovm.apple.uikit.UIBarButtonSystemItem;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageOrientation;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIModalPresentationStyle;
import org.robovm.apple.uikit.UIPopoverArrowDirection;
import org.robovm.apple.uikit.UIProgressView;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewContentMode;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.block.Block1;
import org.robovm.objc.block.VoidBlock1;
import org.robovm.objc.block.VoidBlock2;
import org.robovm.objc.block.VoidBlock3;
import org.robovm.objc.block.VoidBlock4;
import org.robovm.rt.bro.ptr.BooleanPtr;

public class AAPLAssetViewController extends UIViewController implements PHPhotoLibraryChangeObserver {
    private PHAsset asset;
    private PHAssetCollection assetCollection;
    private final UIImageView imageView;
    private final UIBarButtonItem playButton;
    private final UIBarButtonItem space;
    private final UIBarButtonItem trashButton;
    private final UIBarButtonItem editButton;
    private final UIProgressView progressView;
    private AVPlayerLayer playerLayer;
    private CGSize lastImageViewSize;

    private static final String AdjustmentFormatIdentifier = "com.example.apple-samplecode.SamplePhotosApp";

    public AAPLAssetViewController () {
        UIView view = getView();
        view.setBackgroundColor(UIColor.white());

        imageView = new UIImageView(view.getBounds());
        imageView.setContentMode(UIViewContentMode.ScaleAspectFit);
        view.addSubview(imageView);

        playButton = new UIBarButtonItem(UIBarButtonSystemItem.Play, new UIBarButtonItem.OnClickListener() {
            @Override
            public void onClick (UIBarButtonItem barButtonItem) {
                playButtonPressed();
            }
        });
        space = new UIBarButtonItem(UIBarButtonSystemItem.FlexibleSpace, null);
        trashButton = new UIBarButtonItem(UIBarButtonSystemItem.Trash, new UIBarButtonItem.OnClickListener() {
            @Override
            public void onClick (UIBarButtonItem barButtonItem) {
                trashButtonPressed();
            }
        });

        progressView = new UIProgressView(new CGRect(8, 21, 268, 3));
        getNavigationItem().setTitleView(progressView);

        editButton = new UIBarButtonItem(UIBarButtonSystemItem.Edit, new UIBarButtonItem.OnClickListener() {
            @Override
            public void onClick (UIBarButtonItem barButtonItem) {
                editButtonPressed();
            }
        });
        getNavigationItem().setRightBarButtonItem(editButton);
    }

    @Override
    public void viewDidLoad () {
        super.viewDidLoad();
        PHPhotoLibrary.getSharedPhotoLibrary().registerChangeObserver(this);
    }

    @Override
    protected void dispose (boolean finalizing) {
        PHPhotoLibrary.getSharedPhotoLibrary().unregisterChangeObserver(this);
        super.dispose(finalizing);
    }

    @Override
    public void viewWillAppear (boolean animated) {
        super.viewWillAppear(animated);

        if (asset.getMediaType() == PHAssetMediaType.Video) {
            setToolbarItems(new NSArray<UIBarButtonItem>(playButton, space, trashButton));
        } else {
            setToolbarItems(new NSArray<UIBarButtonItem>(space, trashButton));
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
    public void viewWillLayoutSubviews () {
        super.viewWillLayoutSubviews();

        if (lastImageViewSize == null || !imageView.getBounds().getSize().equalsTo(lastImageViewSize)) {
            updateImage();
        }
    }

    private void updateImage () {
        lastImageViewSize = imageView.getBounds().getSize();

        double scale = UIScreen.getMainScreen().getScale();
        CGSize targetSize = new CGSize(imageView.getBounds().getWidth() * scale, imageView.getBounds().getHeight() * scale);

        PHImageRequestOptions options = new PHImageRequestOptions();

        // Download from cloud if necessary
        options.setNetworkAccessAllowed(true);
        options.setProgressHandler(new VoidBlock4<Double, NSError, BooleanPtr, NSDictionary<NSString, NSObject>>() {
            @Override
            public void invoke (final Double progress, NSError error, BooleanPtr c, NSDictionary<NSString, NSObject> d) {
                DispatchQueue.getMainQueue().async(new Runnable() {
                    @Override
                    public void run () {
                        progressView.setProgress(progress.floatValue());
                        progressView.setHidden(progress <= 0 || progress >= 1);
                    };
                });
            }
        });

        PHImageManager.getDefaultManager().requestImageForAsset(asset, targetSize, PHImageContentMode.AspectFill, options,
            new VoidBlock2<UIImage, NSDictionary<NSString, NSObject>>() {
                @Override
                public void invoke (UIImage result, NSDictionary<NSString, NSObject> info) {
                    if (result != null) {
                        imageView.setImage(result);
                    }
                }
            });
    }

    @Override
    public void didChange (final PHChange changeInstance) {
        // Call might come on any background queue. Re-dispatch to the main queue to handle it.
        DispatchQueue.getMainQueue().async(new Runnable() {
            @Override
            public void run () {
                // check if there are changes to the album we're interested on (to its metadata, not to its collection of assets)
                PHObjectChangeDetails changeDetails = changeInstance.getChangeDetailsForObject(asset);
                if (changeDetails != null) {
                    // it changed, we need to fetch a new one
                    asset = (PHAsset)changeDetails.getObjectAfterChanges();
                    if (changeDetails.assetContentChanged()) {
                        updateImage();

                        if (playerLayer != null) {
                            playerLayer.removeFromSuperlayer();
                            playerLayer = null;
                        }
                    }
                }
            }
        });
    }

    private void applyFilter (final String filterName) {
        PHContentEditingInputRequestOptions options = new PHContentEditingInputRequestOptions();
        options.setCanHandleAdjustmentData(new Block1<PHAdjustmentData, Boolean>() {
            @Override
            public Boolean invoke (PHAdjustmentData adjustmentData) {
                return adjustmentData.getFormatIdentifier().equals(AdjustmentFormatIdentifier)
                    && adjustmentData.getFormatVersion().equals("1.0");
            }
        });
        asset.requestContentEditingInput(options, new VoidBlock2<PHContentEditingInput, NSDictionary<NSString, NSObject>>() {
            @Override
            public void invoke (PHContentEditingInput contentEditingInput, NSDictionary<NSString, NSObject> info) {
                // Get full image
                NSURL url = contentEditingInput.getFullSizeImageURL();
                CGImagePropertyOrientation orientation = contentEditingInput.getFullSizeImageOrientation();
                CIImage inputImage = new CIImage(url, null);
                inputImage = inputImage.newImageByApplyingOrientation(orientation);

                // Add filter
                CIFilterInputParameters inputParameters = new CIFilterInputParameters().setInputImage(inputImage);
                CIFilter filter = CIFilter.create(filterName, inputParameters);
                filter.setDefaults();
                CIImage outputImage = filter.getOutputImage();

                // Create editing output
                NSData jpegData = getJPEGRepresentationWithCompressionQuality(outputImage, 0.9);
                PHAdjustmentData adjustmentData = null;
                try {
                    adjustmentData = new PHAdjustmentData(AdjustmentFormatIdentifier, "1.0", new NSData(filterName
                        .getBytes("UTF-8")));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                // TODO data:[filterName dataUsingEncoding:NSUTF8StringEncoding]]; is this correctly implemented?

                final PHContentEditingOutput contentEditingOutput = new PHContentEditingOutput(contentEditingInput);
                jpegData.write(new File(contentEditingOutput.getRenderedContentURL().getAbsoluteString()), true);
// jpegData.write(contentEditingOutput.getRenderedContentURL(), true); TODO
                contentEditingOutput.setAdjustmentData(adjustmentData);

                PHPhotoLibrary.getSharedPhotoLibrary().performChanges(new Runnable() {
                    @Override
                    public void run () {
                        PHAssetChangeRequest request = PHAssetChangeRequest.create(asset);
                        request.setContentEditingOutput(contentEditingOutput);
                    }
                }, new VoidBlock2<Boolean, NSError>() {
                    @Override
                    public void invoke (Boolean success, NSError error) {
                        if (!success) {
                            System.err.println("Error: " + error);
                        }
                    }
                });
            }
        });
    }

    private void editButtonPressed () {
        UIAlertController alertController = UIAlertController.create(null, null, UIAlertControllerStyle.ActionSheet);
        alertController.addAction(UIAlertAction.create(NSString.getLocalizedString("Cancel"), UIAlertActionStyle.Cancel, null));

        if (asset.canPerformEditOperation(PHAssetEditOperation.Properties)) {
            String favoriteActionTitle = !asset.isFavorite() ? NSString.getLocalizedString("Favorite") : NSString
                .getLocalizedString("Unfavorite");
            alertController.addAction(UIAlertAction.create(favoriteActionTitle, UIAlertActionStyle.Default,
                new VoidBlock1<UIAlertAction>() {
                    @Override
                    public void invoke (UIAlertAction a) {
                        PHPhotoLibrary.getSharedPhotoLibrary().performChanges(new Runnable() {
                            @Override
                            public void run () {
                                PHAssetChangeRequest request = PHAssetChangeRequest.create(asset);
                                request.setFavorite(!asset.isFavorite());
                            }
                        }, new VoidBlock2<Boolean, NSError>() {
                            @Override
                            public void invoke (Boolean success, NSError error) {
                                if (!success) {
                                    System.err.println("Error: " + error);
                                }
                            }
                        });
                    }
                }));
        }
        if (asset.canPerformEditOperation(PHAssetEditOperation.Content)) {
            if (asset.getMediaType() == PHAssetMediaType.Image) {
                alertController.addAction(UIAlertAction.create(NSString.getLocalizedString("Sepia"), UIAlertActionStyle.Default,
                    new VoidBlock1<UIAlertAction>() {
                        @Override
                        public void invoke (UIAlertAction a) {
                            applyFilter("CISepiaTone");
                        }
                    }));
                alertController.addAction(UIAlertAction.create(NSString.getLocalizedString("Chrome"), UIAlertActionStyle.Default,
                    new VoidBlock1<UIAlertAction>() {
                        @Override
                        public void invoke (UIAlertAction a) {
                            applyFilter("CIPhotoEffectChrome");
                        }
                    }));
            }
            alertController.addAction(UIAlertAction.create(NSString.getLocalizedString("Revert"), UIAlertActionStyle.Default,
                new VoidBlock1<UIAlertAction>() {
                    @Override
                    public void invoke (UIAlertAction a) {
                        PHPhotoLibrary.getSharedPhotoLibrary().performChanges(new Runnable() {
                            @Override
                            public void run () {
                                PHAssetChangeRequest request = PHAssetChangeRequest.create(asset);
                                request.revertAssetContentToOriginal();
                            }
                        }, new VoidBlock2<Boolean, NSError>() {
                            @Override
                            public void invoke (Boolean success, NSError error) {
                                if (!success) {
                                    System.err.println("Error: " + error);
                                }
                            }
                        });
                    }
                }));
        }
        alertController.setModalPresentationStyle(UIModalPresentationStyle.Popover);
        if (alertController.getPopoverPresentationController() != null) {
            alertController.getPopoverPresentationController().setBarButtonItem(editButton);
            alertController.getPopoverPresentationController().setPermittedArrowDirections(UIPopoverArrowDirection.Up);
        }
        presentViewController(alertController, true, null);
    }

    private void trashButtonPressed () {
        VoidBlock2<Boolean, NSError> completionHandler = new VoidBlock2<Boolean, NSError>() {
            @Override
            public void invoke (Boolean success, NSError error) {
                if (success) {
                    DispatchQueue.getMainQueue().async(new Runnable() {
                        @Override
                        public void run () {
                            getNavigationController().popViewController(true);
                        }
                    });
                } else {
                    System.err.println("Error: " + error);
                }
            }
        };
        if (assetCollection != null) {
            // Remove asset from album
            PHPhotoLibrary.getSharedPhotoLibrary().performChanges(new Runnable() {
                @Override
                public void run () {
                    PHAssetCollectionChangeRequest changeRequest = PHAssetCollectionChangeRequest.create(assetCollection);
                    changeRequest.removeAssets(new NSArray<PHAsset>(asset));
                }
            }, completionHandler);
        } else {
            // Delete asset from library
            PHPhotoLibrary.getSharedPhotoLibrary().performChanges(new Runnable() {
                @Override
                public void run () {
                    PHAssetChangeRequest.deleteAssets(new NSArray<PHAsset>(asset));
                }
            }, completionHandler);
        }
    }

    private void playButtonPressed () {
        if (playerLayer == null) {
            PHImageManager.getDefaultManager().requestAVAssetForVideo(asset, null,
                new VoidBlock3<AVAsset, AVAudioMix, NSDictionary<NSString, NSObject>>() {
                    @Override
                    public void invoke (final AVAsset avAsset, final AVAudioMix audioMix, NSDictionary<NSString, NSObject> info) {
                        DispatchQueue.getMainQueue().async(new Runnable() {
                            @Override
                            public void run () {
                                if (playerLayer == null) {
                                    AVPlayerItem playerItem = new AVPlayerItem(avAsset);
                                    playerItem.setAudioMix(audioMix);
                                    AVPlayer player = new AVPlayer(playerItem);
                                    AVPlayerLayer playerLayer = AVPlayerLayer.create(player);
                                    playerLayer.setVideoGravity(AVLayerVideoGravity.ResizeAspect);

                                    CALayer layer = getView().getLayer();
                                    layer.addSublayer(playerLayer);
                                    playerLayer.setFrame(layer.getBounds());
                                    player.play();
                                }
                            }
                        });
                    }
                });
        } else {
            playerLayer.getPlayer().play();
        }
    }

    private static CIContext ciContext;

    private NSData getJPEGRepresentationWithCompressionQuality (CIImage image, double compressionQuality) {
        if (ciContext == null) {
            EAGLContext eaglContext = new EAGLContext(EAGLRenderingAPI.OpenGLES2);
            ciContext = new CIContext(eaglContext);
        }
        CGImage outputImage = ciContext.createCGImage(image, image.getExtent());
        UIImage uiImage = new UIImage(outputImage, 1.0, UIImageOrientation.Up);
        if (outputImage != null) {
            outputImage.release();
        }
        NSData jpegRepresentation = uiImage.toJPEGData(compressionQuality);
        return jpegRepresentation;
    }

    public void setAsset (PHAsset asset) {
        this.asset = asset;
    }

    public void setAssetCollection (PHAssetCollection assetCollection) {
        this.assetCollection = assetCollection;
    }
}
