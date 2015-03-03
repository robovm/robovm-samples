/*
 * Copyright (C) 2014 Trillian Mobile AB
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
 * Portions of this code is based on Apple Inc's VideoRecorder sample (v1.0.1)
 * which is copyright (C) 2010-2013 Apple Inc.
 */

package org.robovm.samples.videorecorder.viewcontrollers;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.robovm.apple.coregraphics.CGAffineTransform;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.dispatch.DispatchQueue;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIGestureRecognizer;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImagePickerController;
import org.robovm.apple.uikit.UIImagePickerControllerCameraCaptureMode;
import org.robovm.apple.uikit.UIImagePickerControllerCameraDevice;
import org.robovm.apple.uikit.UIImagePickerControllerCameraFlashMode;
import org.robovm.apple.uikit.UIImagePickerControllerDelegateAdapter;
import org.robovm.apple.uikit.UIImagePickerControllerEditingInfo;
import org.robovm.apple.uikit.UIImagePickerControllerQualityType;
import org.robovm.apple.uikit.UIImagePickerControllerSourceType;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UITapGestureRecognizer;
import org.robovm.apple.uikit.UIToolbar;
import org.robovm.apple.uikit.UIVideo;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewAnimationOptions;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.block.VoidBlock2;
import org.robovm.objc.block.VoidBooleanBlock;

public class RootViewController extends UIViewController {
    private final UIButton cameraSelectionButton;
    private final UIButton flashModeButton;
    private final UIButton videoQualitySelectionButton;
    private final UIImageView recordIndicatorView;

    private final UIView cameraOverlayView;

    private final UITapGestureRecognizer recordGestureRecognizer;
    private UIImagePickerController imagePicker;
    private boolean recording;
    private boolean showCameraSelection;
    private boolean showFlashMode;

    public RootViewController() {
        getNavigationItem().setTitle("Video Recorder");

        cameraOverlayView = new UIView(getView().getFrame());

        cameraSelectionButton = new UIButton(new CGRect(20, 10, 72, 37));
        cameraSelectionButton.setImage(UIImage.create("camera-toggle.png"), UIControlState.Normal);
        cameraSelectionButton.setImage(UIImage.create("camera-toggle-pressed.png"), UIControlState.Highlighted);
        cameraSelectionButton.setAlpha(0);
        cameraSelectionButton.addOnTouchUpInsideListener(new UIControl.OnTouchUpInsideListener() {
            @Override
            public void onTouchUpInside(UIControl control, UIEvent event) {
                changeCamera();
            }
        });
        cameraOverlayView.addSubview(cameraSelectionButton);

        flashModeButton = new UIButton(new CGRect(100, 10, 72, 37));
        flashModeButton.setImage(UIImage.create("flash-off.png"), UIControlState.Normal);
        flashModeButton.setAlpha(0);
        flashModeButton.addOnTouchUpInsideListener(new UIControl.OnTouchUpInsideListener() {
            @Override
            public void onTouchUpInside(UIControl control, UIEvent event) {
                changeFlashMode();
            }
        });
        cameraOverlayView.addSubview(flashModeButton);

        videoQualitySelectionButton = new UIButton(new CGRect(228, 10, 72, 37));
        videoQualitySelectionButton.setImage(UIImage.create("sd-selected.png"), UIControlState.Normal);
        videoQualitySelectionButton.setImage(UIImage.create("hd-selected.png"), UIControlState.Highlighted);
        videoQualitySelectionButton.addOnTouchUpInsideListener(new UIControl.OnTouchUpInsideListener() {
            @Override
            public void onTouchUpInside(UIControl control, UIEvent event) {
                changeVideoQuality();
            }
        });
        cameraOverlayView.addSubview(videoQualitySelectionButton);

        recordIndicatorView = new UIImageView(new CGRect(247, 15, 35, 26));
        recordIndicatorView.setImage(UIImage.create("record-indicator.png"));
        recordIndicatorView.setAlpha(0);
        cameraOverlayView.addSubview(recordIndicatorView);

        UIToolbar toolBar = new UIToolbar(new CGRect(0, UIScreen.getMainScreen().getBounds().getHeight() - 44, UIScreen
                .getMainScreen().getBounds().getWidth(), 44));
        toolBar.setBarTintColor(UIColor.black());

        UILabel tapLabel = new UILabel(toolBar.getBounds());
        tapLabel.setText("Double tap screen to start/stop recording");
        tapLabel.setTextColor(UIColor.white());
        tapLabel.setFont(UIFont.getSystemFont(17));
        toolBar.addSubview(tapLabel);
        cameraOverlayView.addSubview(toolBar);

        createImagePicker();

        recordGestureRecognizer = new UITapGestureRecognizer(new UIGestureRecognizer.OnGestureListener() {
            @Override
            public void onGesture(UIGestureRecognizer gestureRecognizer) {
                if (!recording) {
                    recording = true;
                    startRecording();
                } else {
                    recording = false;
                    stopRecording();
                }
            }
        });
        recordGestureRecognizer.setNumberOfTapsRequired(2);

        cameraOverlayView.addGestureRecognizer(recordGestureRecognizer);
    }

    @Override
    public void viewWillAppear(final boolean animated) {
        CGRect theRect = imagePicker.getView().getFrame();
        cameraOverlayView.setFrame(theRect);

        DispatchQueue.getMainQueue().after(100, TimeUnit.MILLISECONDS, new Runnable() {
            @Override
            public void run() {
                getNavigationController().presentViewController(imagePicker, animated, null);
                imagePicker.setCameraOverlayView(cameraOverlayView);
            }
        });
    }

    private void createImagePicker() {
        imagePicker = new UIImagePickerController();
        imagePicker.setSourceType(UIImagePickerControllerSourceType.Camera);

        imagePicker.setMediaTypes(Arrays.asList("public.movie"));
        imagePicker.setCameraCaptureMode(UIImagePickerControllerCameraCaptureMode.Video);

        imagePicker.setAllowsEditing(false);
        imagePicker.setShowsCameraControls(false);
        imagePicker.setCameraViewTransform(CGAffineTransform.Identity());

        // not all devices have two cameras or a flash so just check here
        if (UIImagePickerController.isCameraDeviceAvailable(UIImagePickerControllerCameraDevice.Rear)) {
            imagePicker.setCameraDevice(UIImagePickerControllerCameraDevice.Rear);
            if (UIImagePickerController.isCameraDeviceAvailable(UIImagePickerControllerCameraDevice.Front)) {
                cameraSelectionButton.setAlpha(1);
                showCameraSelection = true;
            }
        } else {
            imagePicker.setCameraDevice(UIImagePickerControllerCameraDevice.Front);
        }

        if (UIImagePickerController.isFlashAvailableForCameraDevice(imagePicker.getCameraDevice())) {
            imagePicker.setCameraFlashMode(UIImagePickerControllerCameraFlashMode.Off);
            flashModeButton.setAlpha(1);
            showFlashMode = true;
        }

        imagePicker.setVideoQuality(UIImagePickerControllerQualityType._640x480);

        imagePicker.setDelegate(new UIImagePickerControllerDelegateAdapter() {
            @Override
            public void didFinishPickingMedia(UIImagePickerController picker, UIImagePickerControllerEditingInfo info) {
                NSURL videoURL = info.getMediaURL();

                boolean okToSaveVideo = UIVideo.isCompatibleWithSavedPhotosAlbum(new File(videoURL.getPath()));
                if (okToSaveVideo) {
                    UIVideo.saveToPhotosAlbum(new File(videoURL.getPath()), new VoidBlock2<String, NSError>() {
                        @Override
                        public void invoke(String a, NSError b) {
                            showControls();
                        }
                    });
                } else {
                    showControls();
                }

            }
        });
    }

    private void changeVideoQuality() {
        if (imagePicker.getVideoQuality() == UIImagePickerControllerQualityType._640x480) {
            imagePicker.setVideoQuality(UIImagePickerControllerQualityType.High);
            videoQualitySelectionButton.setImage(UIImage.create("hd-selected.png"), UIControlState.Normal);
        } else {
            imagePicker.setVideoQuality(UIImagePickerControllerQualityType._640x480);
            videoQualitySelectionButton.setImage(UIImage.create("sd-selected.png"), UIControlState.Normal);
        }
    }

    private void changeFlashMode() {
        if (imagePicker.getCameraFlashMode() == UIImagePickerControllerCameraFlashMode.Off) {
            imagePicker.setCameraFlashMode(UIImagePickerControllerCameraFlashMode.On);
            flashModeButton.setImage(UIImage.create("flash-on.png"), UIControlState.Normal);
        } else {
            imagePicker.setCameraFlashMode(UIImagePickerControllerCameraFlashMode.Off);
            flashModeButton.setImage(UIImage.create("flash-off.png"), UIControlState.Normal);
        }
    }

    private void changeCamera() {
        if (imagePicker.getCameraDevice() == UIImagePickerControllerCameraDevice.Rear) {
            imagePicker.setCameraDevice(UIImagePickerControllerCameraDevice.Front);
        } else {
            imagePicker.setCameraDevice(UIImagePickerControllerCameraDevice.Rear);
        }

        if (!UIImagePickerController.isFlashAvailableForCameraDevice(imagePicker.getCameraDevice())) {
            UIView.animate(0.3, new Runnable() {
                @Override
                public void run() {
                    flashModeButton.setAlpha(0);
                }
            });
            showFlashMode = false;
        } else {
            UIView.animate(0.3, new Runnable() {
                @Override
                public void run() {
                    flashModeButton.setAlpha(1);
                }
            });
            showFlashMode = false;
        }
    }

    private void startRecording() {
        UIView.animate(0.3, 0, UIViewAnimationOptions.CurveEaseInOut, new Runnable() {
            @Override
            public void run() {
                cameraSelectionButton.setAlpha(0);
                flashModeButton.setAlpha(0);
                videoQualitySelectionButton.setAlpha(0);
                recordIndicatorView.setAlpha(1);
            }
        }, new VoidBooleanBlock() {
            @Override
            public void invoke(boolean v) {
                imagePicker.startVideoCapture();
            }
        });
    }

    private void stopRecording() {
        imagePicker.stopVideoCapture();
    }

    private void showControls() {
        UIView.animate(0.3, 0, UIViewAnimationOptions.CurveEaseInOut, new Runnable() {
            @Override
            public void run() {
                if (showCameraSelection)
                    cameraSelectionButton.setAlpha(1);
                if (showFlashMode)
                    flashModeButton.setAlpha(0);
                videoQualitySelectionButton.setAlpha(1);
                recordIndicatorView.setAlpha(0);
            }
        }, null);
    }
}
