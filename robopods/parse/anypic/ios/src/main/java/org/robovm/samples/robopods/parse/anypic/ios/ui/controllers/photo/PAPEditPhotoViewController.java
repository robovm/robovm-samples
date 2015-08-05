package org.robovm.samples.robopods.parse.anypic.ios.ui.controllers.photo;

import java.util.ArrayList;

import org.robovm.apple.coregraphics.CGInterpolationQuality;
import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSMutableDictionary;
import org.robovm.apple.foundation.NSNotificationCenter;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.uikit.UIAlertView;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonItemStyle;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIKeyboardAnimation;
import org.robovm.apple.uikit.UINavigationItem;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIScrollView;
import org.robovm.apple.uikit.UIScrollViewDelegateAdapter;
import org.robovm.apple.uikit.UITextField;
import org.robovm.apple.uikit.UITextFieldDelegateAdapter;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewContentMode;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.apple.uikit.UIWindow;
import org.robovm.objc.block.VoidBlock1;
import org.robovm.pods.parse.PFACL;
import org.robovm.pods.parse.PFFile;
import org.robovm.pods.parse.PFObject;
import org.robovm.pods.parse.PFSaveCallback;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPActivity;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPActivityType;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPCache;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPPhoto;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPPhotoAttributes;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPUser;
import org.robovm.samples.robopods.parse.anypic.ios.ui.views.photodetails.PAPPhotoDetailsFooterView;
import org.robovm.samples.robopods.parse.anypic.ios.util.Log;
import org.robovm.samples.robopods.parse.anypic.ios.util.PAPNotification;
import org.robovm.samples.robopods.parse.anypic.ios.util.PAPNotificationManager;
import org.robovm.samples.robopods.parse.anypic.ios.util.UIImageUtility;

public class PAPEditPhotoViewController extends UIViewController {
    private UIScrollView scrollView;
    private UIImage image;
    private UITextField commentTextField;
    private PFFile photoFile;
    private PFFile thumbnailFile;
    private long fileUploadBackgroundTaskId;
    private long photoPostBackgroundTaskId;

    private NSObject willShowKeyboardNotification;
    private NSObject willHideKeyboardNotification;

    public PAPEditPhotoViewController(UIImage image) {
        super(null, null);

        if (image != null) {
            this.image = image;
            fileUploadBackgroundTaskId = UIApplication.getInvalidBackgroundTask();
            photoPostBackgroundTaskId = UIApplication.getInvalidBackgroundTask();
        }
    }

    @Override
    protected void dispose(boolean finalizing) {
        super.dispose(finalizing);

        NSNotificationCenter.getDefaultCenter().removeObserver(willShowKeyboardNotification);
        NSNotificationCenter.getDefaultCenter().removeObserver(willHideKeyboardNotification);
    }

    @Override
    public void didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning();

        Log.d("Memory warning on Edit");
    }

    @Override
    public void loadView() {
        scrollView = new UIScrollView(UIScreen.getMainScreen().getApplicationFrame());
        scrollView.setDelegate(new UIScrollViewDelegateAdapter() {
            @Override
            public void willBeginDragging(UIScrollView scrollView) {
                commentTextField.resignFirstResponder();
            }
        });
        scrollView.setBackgroundColor(UIColor.black());
        setView(scrollView);

        UIImageView photoImageView = new UIImageView(new CGRect(0, 42, 320, 320));
        photoImageView.setBackgroundColor(UIColor.black());
        photoImageView.setImage(image);
        photoImageView.setContentMode(UIViewContentMode.ScaleAspectFit);

        scrollView.addSubview(photoImageView);

        CGRect footerRect = PAPPhotoDetailsFooterView.getRectForView();
        footerRect.getOrigin().setY(
                photoImageView.getFrame().getOrigin().getY() + photoImageView.getFrame().getSize().getHeight());

        PAPPhotoDetailsFooterView footerView = new PAPPhotoDetailsFooterView(footerRect);
        commentTextField = footerView.getCommentField();
        commentTextField.setDelegate(new UITextFieldDelegateAdapter() {
            @Override
            public boolean shouldReturn(UITextField textField) {
                doneButtonAction.onClick(null);
                textField.resignFirstResponder();
                return true;
            }
        });
        scrollView.addSubview(footerView);

        scrollView.setContentSize(new CGSize(scrollView.getBounds().getSize().getWidth(), photoImageView.getFrame()
                .getOrigin().getY()
                + photoImageView.getFrame().getSize().getHeight() + footerView.getFrame().getSize().getHeight()));
    }

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        UINavigationItem navigationItem = getNavigationItem();

        navigationItem.setHidesBackButton(true);

        navigationItem.setTitleView(new UIImageView(UIImage.create("LogoNavigationBar")));
        navigationItem.setLeftBarButtonItem(new UIBarButtonItem("Cancel", UIBarButtonItemStyle.Plain,
                cancelButtonAction));
        navigationItem
                .setRightBarButtonItem(new UIBarButtonItem("Publish", UIBarButtonItemStyle.Done, doneButtonAction));

        willShowKeyboardNotification = UIWindow.Notifications
                .observeKeyboardWillShow(new VoidBlock1<UIKeyboardAnimation>() {
                    @Override
                    public void invoke(UIKeyboardAnimation animation) {
                        CGRect keyboardFrameEnd = animation.getEndFrame();
                        CGSize scrollViewContentSize = scrollView.getBounds().getSize();
                        scrollViewContentSize.setHeight(scrollViewContentSize.getHeight()
                                + keyboardFrameEnd.getSize().getHeight());
                        scrollView.setContentSize(scrollViewContentSize);

                        CGPoint scrollViewContentOffset = scrollView.getContentOffset();
                        // Align the bottom edge of the photo with the keyboard
                        scrollViewContentOffset.setY(scrollViewContentOffset.getY()
                                + keyboardFrameEnd.getSize().getHeight() * 3
                                - UIScreen.getMainScreen().getBounds().getSize().getHeight());

                        scrollView.setContentOffset(scrollViewContentOffset, true);
                    }
                });
        willHideKeyboardNotification = UIWindow.Notifications
                .observeKeyboardWillHide(new VoidBlock1<UIKeyboardAnimation>() {
                    @Override
                    public void invoke(UIKeyboardAnimation animation) {
                        CGRect keyboardFrameEnd = animation.getEndFrame();
                        final CGSize scrollViewContentSize = scrollView.getBounds().getSize();
                        scrollViewContentSize.setHeight(scrollViewContentSize.getHeight()
                                - keyboardFrameEnd.getSize().getHeight());
                        UIView.animate(0.2, new Runnable() {
                            @Override
                            public void run() {
                                scrollView.setContentSize(scrollViewContentSize);
                            }
                        });
                    }

                });

        shouldUploadImage(image);
    }

    private boolean shouldUploadImage(UIImage image) {
        UIImage resizedImage = UIImageUtility.resize(image, UIViewContentMode.ScaleAspectFit, new CGSize(560, 560),
                CGInterpolationQuality.High);
        UIImage thumbnailImage = UIImageUtility.createThumbnail(image, 86, 0, 10, CGInterpolationQuality.Default);

        // JPEG to decrease file size and enable faster uploads & downloads
        NSData imageData = resizedImage.toJPEGData(0.8);
        NSData thumbnailImageData = thumbnailImage.toPNGData();

        if (imageData == null || thumbnailImageData == null) {
            return false;
        }

        photoFile = new PFFile(imageData);
        thumbnailFile = new PFFile(thumbnailImageData);

        // Request a background execution task to allow us to finish uploading
        // the photo even if the app is backgrounded
        fileUploadBackgroundTaskId = UIApplication.getSharedApplication().beginBackgroundTask(new Runnable() {
            @Override
            public void run() {
                UIApplication.getSharedApplication().endBackgroundTask(fileUploadBackgroundTaskId);
            }
        });

        Log.d("Requested background expiration task with id %d for Anypic photo upload", fileUploadBackgroundTaskId);

        photoFile.saveInBackground(new PFSaveCallback() {
            @Override
            public void done(boolean success, NSError error) {
                if (success) {
                    Log.d("Photo uploaded successfully");
                    thumbnailFile.saveInBackground(new PFSaveCallback() {
                        @Override
                        public void done(boolean success, NSError error) {
                            if (success) {
                                Log.d("Thumbnail uploaded successfully");
                            }
                            UIApplication.getSharedApplication().endBackgroundTask(fileUploadBackgroundTaskId);
                        }
                    });
                } else {
                    UIApplication.getSharedApplication().endBackgroundTask(fileUploadBackgroundTaskId);
                }
            }
        });

        return true;
    }

    private final UIBarButtonItem.OnClickListener doneButtonAction = new UIBarButtonItem.OnClickListener() {
        @Override
        public void onClick(UIBarButtonItem barButtonItem) {
            final NSDictionary<?, ?> userInfo = new NSMutableDictionary<>();
            String trimmedComment = commentTextField.getText().trim();
            if (trimmedComment.length() != 0) {
                userInfo.put("comment", trimmedComment);
            }

            if (photoFile == null || thumbnailFile == null) {
                UIAlertView alert = new UIAlertView("Couldn't post your photo", null, null, null, "Dismiss");
                alert.show();
                return;
            }

            // both files have finished uploading
            final PAPPhoto photo = PFObject.create(PAPPhoto.class);
            photo.setUser(PAPUser.getCurrentUser());
            photo.setPicture(photoFile);
            photo.setThumbnail(thumbnailFile);

            // photos are public, but may only be modified by the user who
            // uploaded them
            PFACL photoACL = new PFACL(PAPUser.getCurrentUser());
            photoACL.setPublicReadAccess(true);
            photo.setACL(photoACL);

            // Request a background execution task to allow us to finish
            // uploading the photo even if the app is backgrounded
            photoPostBackgroundTaskId = UIApplication.getSharedApplication().beginBackgroundTask(new Runnable() {
                @Override
                public void run() {
                    UIApplication.getSharedApplication().endBackgroundTask(photoPostBackgroundTaskId);
                }
            });

            // save
            photo.saveInBackground(new PFSaveCallback() {
                @Override
                public void done(boolean success, NSError error) {
                    if (success) {
                        Log.d("Photo uploaded");

                        PAPCache.getSharedCache().setPhotoAttributes(photo,
                                new PAPPhotoAttributes(new ArrayList<PAPUser>(), new ArrayList<PAPUser>(), false));

                        // userInfo might contain any caption which might have
                        // been posted by the uploader
                        if (userInfo != null) {
                            String commentText = userInfo
                                    .getString("comment", null);

                            if (commentText != null && commentText.length() != 0) {
                                // create and save photo caption
                                PAPActivity comment = PFObject.create(PAPActivity.class);
                                comment.setType(PAPActivityType.COMMENT);
                                comment.setPhoto(photo);
                                comment.setFromUser(PAPUser.getCurrentUser());
                                comment.setToUser(PAPUser.getCurrentUser());
                                comment.setContent(commentText);

                                PFACL acl = new PFACL(PAPUser.getCurrentUser());
                                acl.setPublicReadAccess(true);
                                comment.setACL(acl);

                                comment.saveEventually();
                                PAPCache.getSharedCache().incrementPhotoCommentCount(photo);
                            }
                        }
                        PAPNotificationManager.postNotification(PAPNotification.DID_FINISH_EDITING_PHOTO, photo);
                    } else {
                        Log.e("Photo failed to save: %s", error);
                        UIAlertView alert = new UIAlertView("Couldn't post your photo", null, null, null, "Dismiss");
                        alert.show();
                    }
                    UIApplication.getSharedApplication().endBackgroundTask(photoPostBackgroundTaskId);
                }
            });

            getParentViewController().dismissViewController(true, null);
        }
    };

    private final UIBarButtonItem.OnClickListener cancelButtonAction = new UIBarButtonItem.OnClickListener() {
        @Override
        public void onClick(UIBarButtonItem barButtonItem) {
            getParentViewController().dismissViewController(true, null);
        }
    };
}
