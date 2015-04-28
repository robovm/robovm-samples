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
 * Portions of this code is based on Apple Inc's PhotoPicker sample (v2.0)
 * which is copyright (C) 2010-2013 Apple Inc.
 */
package org.robovm.samples.photopicker.ui;

import java.util.ArrayList;
import java.util.List;

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.foundation.NSDate;
import org.robovm.apple.foundation.NSMutableArray;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSRunLoop;
import org.robovm.apple.foundation.NSRunLoopMode;
import org.robovm.apple.foundation.NSTimer;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImagePickerController;
import org.robovm.apple.uikit.UIImagePickerControllerDelegateAdapter;
import org.robovm.apple.uikit.UIImagePickerControllerEditingInfo;
import org.robovm.apple.uikit.UIImagePickerControllerSourceType;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIModalPresentationStyle;
import org.robovm.apple.uikit.UIToolbar;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import org.robovm.objc.annotation.IBOutlet;
import org.robovm.objc.block.VoidBlock1;

@CustomClass("PhotoViewController")
public class PhotoViewController extends UIViewController implements VoidBlock1<NSTimer> {
    private UIImageView imageView;
    private UIToolbar toolBar;
    private UIView overlayView;

    private UIBarButtonItem takePictureButton;
    private UIBarButtonItem startStopButton;
    private UIBarButtonItem delayedPhotoButton;
    private UIBarButtonItem doneButton;

    private UIImagePickerController imagePickerController;

    private NSTimer cameraTimer;
    private List<UIImage> capturedImages;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        capturedImages = new ArrayList<>();

        if (!UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.Camera)) {
            // There is not a camera on this device, so don't show the camera
            // button.
            @SuppressWarnings("unchecked") NSMutableArray<UIBarButtonItem> toolbarItems = (NSMutableArray<UIBarButtonItem>) toolBar
                    .getItems().mutableCopy();
            toolbarItems.remove(2);
            toolBar.setItems(toolbarItems, false);
        }
    }

    @IBAction
    private void showImagePickerForCamera(NSObject sender) {
        showImagePickerForSourceType(UIImagePickerControllerSourceType.Camera);
    }

    @IBAction
    private void showImagePickerForPhotoPicker(NSObject sender) {
        showImagePickerForSourceType(UIImagePickerControllerSourceType.PhotoLibrary);
    }

    private void showImagePickerForSourceType(UIImagePickerControllerSourceType sourceType) {
        if (imageView.isAnimating()) {
            imageView.stopAnimating();
        }

        if (capturedImages.size() > 0) {
            capturedImages.clear();
        }

        UIImagePickerController imagePickerController = new UIImagePickerController();
        imagePickerController.setModalPresentationStyle(UIModalPresentationStyle.CurrentContext);
        imagePickerController.setSourceType(sourceType);
        imagePickerController.setDelegate(new UIImagePickerControllerDelegateAdapter() {

            /**
             * This method is called when an image has been chosen from the
             * library or taken from the camera.
             * 
             * @param picker
             * @param info
             */
            @Override
            public void didFinishPickingMedia(UIImagePickerController picker, UIImagePickerControllerEditingInfo info) {
                UIImage image = info.getOriginalImage();

                capturedImages.add(image);

                if (cameraTimer != null && cameraTimer.isValid()) {
                    return;
                }

                finishAndUpdate();
            }

            @Override
            public void didCancel(UIImagePickerController picker) {
                dismissViewController(true, null);
            }
        });

        if (sourceType == UIImagePickerControllerSourceType.Camera) {
            /*
             * The user wants to use the camera interface. Set up our custom
             * overlay view for the camera.
             */
            imagePickerController.setShowsCameraControls(false);

            /*
             * Load the overlay view from the OverlayView nib file. This is the
             * File's Owner for the nib file, so the overlayView outlet is set
             * to the main view in the nib. Pass that view to the image picker
             * controller to use as its overlay view, and set self's reference
             * to the view to null.
             */
            NSBundle.getMainBundle().loadNib("OverlayView", this, null);
            overlayView.setFrame(imagePickerController.getCameraOverlayView().getFrame());
            imagePickerController.setCameraOverlayView(overlayView);
            setOverlayView(null);
        }

        this.imagePickerController = imagePickerController;
        presentViewController(imagePickerController, true, null);
    }

    @IBAction
    private void done(NSObject sender) {
        // Dismiss the camera.
        if (cameraTimer != null && cameraTimer.isValid()) {
            cameraTimer.invalidate();
        }
        finishAndUpdate();
    }

    @IBAction
    private void takePhoto(NSObject sender) {
        imagePickerController.takePicture();
    }

    @IBAction
    private void delayedTakePhoto(NSObject sender) {
        // These controls can't be used until the photo has been taken
        doneButton.setEnabled(false);
        takePictureButton.setEnabled(false);
        delayedPhotoButton.setEnabled(false);
        startStopButton.setEnabled(false);

        NSDate fireDate = NSDate.createWithTimeIntervalSinceNow(5);
        NSTimer cameraTimer = new NSTimer(fireDate, 1, this, false);

        NSRunLoop.getMain().addTimer(NSRunLoopMode.Default, cameraTimer);
        this.cameraTimer = cameraTimer;
    }

    @IBAction
    private void startTakingPicturesAtIntervals(NSObject sender) {
        /*
         * Start the timer to take a photo every 1.5 seconds.
         * 
         * CAUTION: for the purpose of this sample, we will continue to take
         * pictures indefinitely. Be aware we will run out of memory quickly.
         * You must decide the proper threshold number of photos allowed to take
         * from the camera. One solution to avoid memory constraints is to save
         * each taken photo to disk rather than keeping all of them in memory.
         * In low memory situations sometimes our "didReceiveMemoryWarning"
         * method will be called in which case we can recover some memory and
         * keep the app running.
         */
        startStopButton.setTitle("Stop");
        startStopButton.setOnClickListener(new UIBarButtonItem.OnClickListener() {
            @Override
            public void onClick(UIBarButtonItem barButtonItem) {
                stopTakingPicturesAtIntervals(startStopButton);
            }
        });

        doneButton.setEnabled(false);
        delayedPhotoButton.setEnabled(false);
        takePictureButton.setEnabled(false);

        cameraTimer = NSTimer.createScheduled(1.5, this, true);
        cameraTimer.fire(); // Start taking pictures right away.
    }

    @IBAction
    private void stopTakingPicturesAtIntervals(NSObject sender) {
        // Stop and reset the timer.
        cameraTimer.invalidate();
        cameraTimer = null;

        finishAndUpdate();
    }

    private void finishAndUpdate() {
        dismissViewController(true, null);
        if (capturedImages.size() > 0) {
            if (capturedImages.size() == 1) {
                // Camera took a single picture.
                imageView.setImage(capturedImages.get(0));
            } else {
                // Camera took multiple pictures; use the list of images for
                // animation.
                imageView.setAnimationImages(new NSArray<UIImage>(capturedImages));
                imageView.setAnimationDuration(5);// Show each captured photo
                                                  // for 5 seconds.
                imageView.setAnimationRepeatCount(0); // Animate forever (show
                                                      // all photos).
                imageView.startAnimating();
            }

            // To be ready to start again, clear the captured images array.
            capturedImages.clear();
        }

        imagePickerController = null;
    }

    @IBOutlet
    public void setImageView(UIImageView imageView) {
        this.imageView = imageView;
    }

    @IBOutlet
    public void setToolBar(UIToolbar toolBar) {
        this.toolBar = toolBar;
    }

    @IBOutlet
    public void setOverlayView(UIView overlayView) {
        this.overlayView = overlayView;
    }

    @IBOutlet
    public void setTakePictureButton(UIBarButtonItem takePictureButton) {
        this.takePictureButton = takePictureButton;
    }

    @IBOutlet
    public void setStartStopButton(UIBarButtonItem startStopButton) {
        this.startStopButton = startStopButton;
    }

    @IBOutlet
    public void setDelayedPhotoButton(UIBarButtonItem delayedPhotoButton) {
        this.delayedPhotoButton = delayedPhotoButton;
    }

    @IBOutlet
    public void setDoneButton(UIBarButtonItem doneButton) {
        this.doneButton = doneButton;
    }

    @Override
    public void invoke(NSTimer timer) {
        imagePickerController.takePicture();
    }
}
