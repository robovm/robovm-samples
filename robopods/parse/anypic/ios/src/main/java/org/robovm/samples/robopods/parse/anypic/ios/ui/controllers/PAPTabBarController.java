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
 * Portions of this code is based on Parse's AnyPic sample
 * which is copyright (C) 2013 Parse.
 */
package org.robovm.samples.robopods.parse.anypic.ios.ui.controllers;

import java.util.Arrays;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.mobilecoreservices.UTType;
import org.robovm.apple.uikit.UIActionSheet;
import org.robovm.apple.uikit.UIActionSheetDelegate;
import org.robovm.apple.uikit.UIActionSheetDelegateAdapter;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIButtonType;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIGestureRecognizer;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImagePickerController;
import org.robovm.apple.uikit.UIImagePickerControllerCameraDevice;
import org.robovm.apple.uikit.UIImagePickerControllerDelegate;
import org.robovm.apple.uikit.UIImagePickerControllerDelegateAdapter;
import org.robovm.apple.uikit.UIImagePickerControllerEditingInfo;
import org.robovm.apple.uikit.UIImagePickerControllerSourceType;
import org.robovm.apple.uikit.UIModalTransitionStyle;
import org.robovm.apple.uikit.UINavigationController;
import org.robovm.apple.uikit.UIStatusBarStyle;
import org.robovm.apple.uikit.UISwipeGestureRecognizer;
import org.robovm.apple.uikit.UISwipeGestureRecognizerDirection;
import org.robovm.apple.uikit.UITabBarController;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.samples.robopods.parse.anypic.ios.ui.controllers.photo.PAPEditPhotoViewController;

public class PAPTabBarController extends UITabBarController {
    private UINavigationController navController;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        getTabBar().setTintColor(UIColor.fromRGBA(254f / 255f, 149f / 255f, 50f / 255f, 1));
        getTabBar().setBarTintColor(UIColor.fromRGBA(0, 0, 0, 1));

        navController = new UINavigationController();
    }

    @Override
    public UIStatusBarStyle getPreferredStatusBarStyle() {
        return UIStatusBarStyle.LightContent;
    }

    @Override
    public void setViewControllers(NSArray<UIViewController> viewControllers, boolean animated) {
        super.setViewControllers(viewControllers, animated);

        UIButton cameraButton = UIButton.create(UIButtonType.Custom);
        cameraButton.setFrame(new CGRect(94, 0, 131, getTabBar().getBounds().getSize().getHeight()));
        cameraButton.setImage(UIImage.create("ButtonCamera"), UIControlState.Normal);
        cameraButton.setImage(UIImage.create("ButtonCameraSelected"), UIControlState.Highlighted);
        cameraButton.addOnTouchUpInsideListener(photoCaptureButtonAction);
        getTabBar().addSubview(cameraButton);

        UISwipeGestureRecognizer swipeUpGestureRecognizer = new UISwipeGestureRecognizer(
                new UIGestureRecognizer.OnGestureListener() {
                    @Override
                    public void onGesture(UIGestureRecognizer gestureRecognizer) {
                        shouldPresentPhotoCaptureController();
                    }
                });
        swipeUpGestureRecognizer.setDirection(UISwipeGestureRecognizerDirection.Up);
        swipeUpGestureRecognizer.setNumberOfTouchesRequired(1);
        cameraButton.addGestureRecognizer(swipeUpGestureRecognizer);
    }

    public boolean shouldPresentPhotoCaptureController() {
        boolean presentedPhotoCaptureController = shouldStartCameraController();

        if (!presentedPhotoCaptureController) {
            presentedPhotoCaptureController = shouldStartPhotoLibraryPickerController();
        }

        return presentedPhotoCaptureController;
    }

    private boolean shouldStartCameraController() {
        if (!UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.Camera)
                ||
                !UIImagePickerController.getAvailableMediaTypes(UIImagePickerControllerSourceType.Camera).contains(
                        UTType.ImageContent.Image())) {
            return false;
        }

        UIImagePickerController cameraUI = new UIImagePickerController();
        cameraUI.setMediaTypes(Arrays.asList(UTType.ImageContent.Image()));
        cameraUI.setSourceType(UIImagePickerControllerSourceType.Camera);

        if (UIImagePickerController.isCameraDeviceAvailable(UIImagePickerControllerCameraDevice.Rear)) {
            cameraUI.setCameraDevice(UIImagePickerControllerCameraDevice.Rear);
        } else if (UIImagePickerController.isCameraDeviceAvailable(UIImagePickerControllerCameraDevice.Front)) {
            cameraUI.setCameraDevice(UIImagePickerControllerCameraDevice.Front);
        }

        cameraUI.setAllowsEditing(true);
        cameraUI.setShowsCameraControls(true);
        cameraUI.setDelegate(imagePickerDelegate);

        presentViewController(cameraUI, true, null);

        return true;
    }

    private boolean shouldStartPhotoLibraryPickerController() {
        UIImagePickerController cameraUI = new UIImagePickerController();
        if (UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.PhotoLibrary)
                &&
                UIImagePickerController.getAvailableMediaTypes(UIImagePickerControllerSourceType.PhotoLibrary)
                        .contains(UTType.ImageContent.Image())) {
            cameraUI.setSourceType(UIImagePickerControllerSourceType.PhotoLibrary);
            cameraUI.setMediaTypes(Arrays.asList(UTType.ImageContent.Image()));
        } else if (UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.SavedPhotosAlbum)
                &&
                UIImagePickerController.getAvailableMediaTypes(UIImagePickerControllerSourceType.SavedPhotosAlbum)
                        .contains(UTType.ImageContent.Image())) {
            cameraUI.setSourceType(UIImagePickerControllerSourceType.SavedPhotosAlbum);
            cameraUI.setMediaTypes(Arrays.asList(UTType.ImageContent.Image()));
        } else {
            return false;
        }

        cameraUI.setAllowsEditing(true);
        cameraUI.setDelegate(imagePickerDelegate);

        presentViewController(cameraUI, true, null);

        return true;
    }

    private final UIControl.OnTouchUpInsideListener photoCaptureButtonAction = new UIControl.OnTouchUpInsideListener() {
        @Override
        public void onTouchUpInside(UIControl control, UIEvent event) {
            boolean cameraDeviceAvailable = UIImagePickerController
                    .isSourceTypeAvailable(UIImagePickerControllerSourceType.Camera);
            boolean photoLibraryAvailable = UIImagePickerController
                    .isSourceTypeAvailable(UIImagePickerControllerSourceType.PhotoLibrary);

            if (cameraDeviceAvailable && photoLibraryAvailable) {
                UIActionSheet actionSheet = new UIActionSheet(null, actionSheetDelegate, "Cancel", null, "Take Photo",
                        "Choose Photo");
                actionSheet.showFrom(getTabBar());
            } else {
                // if we don't have at least two options, we automatically show
                // whichever is available (camera or roll)
                shouldPresentPhotoCaptureController();
            }
        }
    };

    private final UIActionSheetDelegate actionSheetDelegate = new UIActionSheetDelegateAdapter() {
        @Override
        public void clicked(UIActionSheet actionSheet, long buttonIndex) {
            if (buttonIndex == 0) {
                shouldStartCameraController();
            } else if (buttonIndex == 1) {
                shouldStartPhotoLibraryPickerController();
            }
        }
    };

    private final UIImagePickerControllerDelegate imagePickerDelegate = new UIImagePickerControllerDelegateAdapter() {
        @Override
        public void didCancel(UIImagePickerController picker) {
            dismissViewController(true, null);
        }

        @Override
        public void didFinishPickingMedia(UIImagePickerController picker, UIImagePickerControllerEditingInfo info) {
            dismissViewController(false, null);

            UIImage image = info.getEditedImage();

            PAPEditPhotoViewController viewController = new PAPEditPhotoViewController(image);
            viewController.setModalTransitionStyle(UIModalTransitionStyle.CrossDissolve);

            navController.setModalTransitionStyle(UIModalTransitionStyle.CrossDissolve);
            navController.pushViewController(viewController, false);

            presentViewController(navController, true, null);
        }
    };

    public UINavigationController getHomeNavigationController() {
        return (UINavigationController) getViewControllers().get(0);
    }

    public UINavigationController getEmptyNavigationController() {
        return (UINavigationController) getViewControllers().get(1);
    }

    public UINavigationController getActivityFeedNavigationController() {
        return (UINavigationController) getViewControllers().get(2);
    }
}
