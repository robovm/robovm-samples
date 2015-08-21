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
package org.robovm.samples.robopods.parse.anypic.ios.ui.views.photodetails;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSDate;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSMutableDictionary;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.NSAttributedStringAttributes;
import org.robovm.apple.uikit.NSLineBreakMode;
import org.robovm.apple.uikit.NSStringDrawingOptions;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIButtonType;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEdgeInsets;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewContentMode;
import org.robovm.pods.parse.PFFile;
import org.robovm.pods.parse.PFGetCallback;
import org.robovm.pods.parse.PFSaveCallback;
import org.robovm.pods.parse.PFUser;
import org.robovm.pods.parse.ui.PFImageView;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPCache;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPPhoto;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPUser;
import org.robovm.samples.robopods.parse.anypic.ios.ui.views.PAPProfileImageView;
import org.robovm.samples.robopods.parse.anypic.ios.util.PAPNotification;
import org.robovm.samples.robopods.parse.anypic.ios.util.PAPNotificationManager;
import org.robovm.samples.robopods.parse.anypic.ios.util.PAPUtility;
import org.robovm.samples.robopods.parse.anypic.ios.util.TTTTimeIntervalFormatter;

public class PAPPhotoDetailsHeaderView extends UIView {
    private static final double BASE_HORIZONTAL_OFFSET = 0;
    private static final double BASE_WIDTH = UIScreen.getMainScreen().getBounds().getWidth();

    private static final double HORI_BORDER_SPACING = 6;
    private static final double HORI_MEDIUM_SPACING = 8;

    private static final double VERT_BORDER_SPACING = 6;
    private static final double VERT_SMALL_SPACING = 2;

    private static final double NAME_HEADER_X = BASE_HORIZONTAL_OFFSET;
    private static final double NAME_HEADER_Y = 0;
    private static final double NAME_HEADER_WIDTH = BASE_WIDTH;
    private static final double NAME_HEADER_HEIGHT = 46;

    private static final double AVATAR_IMAGE_X = HORI_BORDER_SPACING;
    private static final double AVATAR_IMAGE_Y = VERT_BORDER_SPACING;
    private static final double AVATAR_IMAGE_DIM = 35;

    private static final double NAME_LABEL_X = AVATAR_IMAGE_X + AVATAR_IMAGE_DIM + HORI_MEDIUM_SPACING;
    private static final double NAME_LABEL_Y = AVATAR_IMAGE_Y + VERT_SMALL_SPACING;
    private static final double NAME_LABEL_MAX_WIDTH = 280 - (HORI_BORDER_SPACING + AVATAR_IMAGE_DIM
            + HORI_MEDIUM_SPACING + HORI_BORDER_SPACING);

    private static final double TIME_LABEL_X = NAME_LABEL_X;
    private static final double TIME_LABEL_MAX_WIDTH = NAME_LABEL_MAX_WIDTH;

    private static final double MAIN_IMAGE_X = BASE_HORIZONTAL_OFFSET;
    private static final double MAIN_IMAGE_Y = NAME_HEADER_HEIGHT;
    private static final double MAIN_IMAGE_WIDTH = BASE_WIDTH;
    private static final double MAIN_IMAGE_HEIGHT = 320;

    private static final double LIKE_BAR_X = BASE_HORIZONTAL_OFFSET;
    private static final double LIKE_BAR_Y = NAME_HEADER_HEIGHT + MAIN_IMAGE_HEIGHT;
    private static final double LIKE_BAR_WIDTH = BASE_WIDTH;
    private static final double LIKE_BAR_HEIGHT = 43;

    private static final double LIKE_BUTTON_X = 9;
    private static final double LIKE_BUTTON_Y = 8;
    private static final double LIKE_BUTTON_DIM = 28;

    private static final double LIKE_PROFILE_X_BASE = 46;
    private static final double LIKE_PROFILE_X_SPACE = 3;
    private static final double LIKE_PROFILE_Y = 6;
    private static final double LIKE_PROFILE_DIM = 30;

    private static final double VIEW_TOTAL_HEIGHT = LIKE_BAR_Y + LIKE_BAR_HEIGHT;
    private static final int numLikePics = 7;

    /**
     * The photo displayed in the view
     */
    private PAPPhoto photo;
    /**
     * The user that took the photo
     */
    private final PAPUser photographer;
    /**
     * Array of the users that liked the photo
     */
    private List<PAPUser> likeUsers;

    private PAPPhotoDetailsHeaderViewDelegate delegate;

    /**
     * Heart-shaped like button
     */
    private UIButton likeButton;
    private UIView nameHeaderView;

    private PFImageView photoImageView;
    private UIView likeBarView;
    private List<PAPProfileImageView> currentLikeAvatars;

    private final TTTTimeIntervalFormatter timeFormatter;

    public PAPPhotoDetailsHeaderView(CGRect frame, PAPPhoto photo) {
        super(frame);
        this.photo = photo;

        timeFormatter = new TTTTimeIntervalFormatter();

        photographer = photo.getUser();

        setBackgroundColor(UIColor.clear());
        createView();
    }

    public static CGRect getRectForView() {
        return new CGRect(0, 0, UIScreen.getMainScreen().getBounds().getSize().getWidth(), VIEW_TOTAL_HEIGHT);
    }

    public void setPhoto(PAPPhoto photo) {
        this.photo = photo;

        if (photo != null && photographer != null && likeUsers != null) {
            createView();
            setNeedsDisplay();
        }
    }

    public void setLikeUsers(List<PAPUser> users) {
        Collections.sort(users);

        currentLikeAvatars = new ArrayList<>(likeUsers.size());

        for (PAPProfileImageView image : currentLikeAvatars) {
            image.removeFromSuperview();
        }

        likeButton.setTitle(String.valueOf(likeUsers.size()), UIControlState.Normal);

        int numOfPics = numLikePics > likeUsers.size() ? likeUsers.size() : numLikePics;

        for (int i = 0; i < numOfPics; i++) {
            PAPProfileImageView profilePic = new PAPProfileImageView(new CGRect(LIKE_PROFILE_X_BASE + i
                    * (LIKE_PROFILE_X_SPACE + LIKE_PROFILE_DIM), LIKE_PROFILE_Y, LIKE_PROFILE_DIM, LIKE_PROFILE_DIM));
            profilePic.getProfileButton().addOnTouchUpInsideListener(didTapLikerButton);
            profilePic.getProfileButton().setTag(i);

            if (PAPUtility.userHasProfilePictures(likeUsers.get(i))) {
                profilePic.setFile(likeUsers.get(i).getProfilePicSmall());
            } else {
                profilePic.setImage(PAPUtility.getDefaultProfilePicture());
            }

            likeBarView.addSubview(profilePic);
            currentLikeAvatars.add(profilePic);
        }

        setNeedsDisplay();
    }

    private void setLikeButtonState(boolean selected) {
        if (selected) {
            likeButton.setTitleEdgeInsets(new UIEdgeInsets(-1, 0, 0, 0));
        } else {
            likeButton.setTitleEdgeInsets(new UIEdgeInsets(0, 0, 0, 0));
        }
        likeButton.setSelected(selected);
    }

    public void reloadLikeBar() {
        likeUsers = PAPCache.getSharedCache().getPhotoLikers(photo);
        setLikeButtonState(PAPCache.getSharedCache().isPhotoLikedByCurrentUser(photo));
        likeButton.addOnTouchUpInsideListener(didTapLikePhotoButton);
    }

    private void createView() {
        /*
         * Create middle section of the header view; the image
         */
        photoImageView = new PFImageView(new CGRect(MAIN_IMAGE_X, MAIN_IMAGE_Y, MAIN_IMAGE_WIDTH, MAIN_IMAGE_HEIGHT));
        photoImageView.setImage(UIImage.create("PlaceholderPhoto"));
        photoImageView.setBackgroundColor(UIColor.black());
        photoImageView.setContentMode(UIViewContentMode.ScaleAspectFit);

        PFFile imageFile = photo.getPicture();
        if (imageFile != null) {
            photoImageView.setFile(imageFile);
            photoImageView.loadInBackground();
        }
        addSubview(photoImageView);

        /*
         * Create top of header view with name and avatar
         */
        nameHeaderView = new UIView(new CGRect(NAME_HEADER_X, NAME_HEADER_Y, NAME_HEADER_WIDTH, NAME_HEADER_HEIGHT));
        nameHeaderView.setBackgroundColor(UIColor.white());
        addSubview(nameHeaderView);

        // Load data for header
        photographer.fetchIfNeededInBackground(new PFGetCallback<PFUser>() {
            @Override
            public void done(PFUser object, NSError error) {
                // Create avatar view
                PAPProfileImageView avatarImageView = new PAPProfileImageView(new CGRect(AVATAR_IMAGE_X,
                        AVATAR_IMAGE_Y, AVATAR_IMAGE_DIM, AVATAR_IMAGE_DIM));

                if (PAPUtility.userHasProfilePictures(photographer)) {
                    avatarImageView.setFile(photographer.getProfilePicSmall());
                } else {
                    avatarImageView.setImage(PAPUtility.getDefaultProfilePicture());
                }

                avatarImageView.setBackgroundColor(UIColor.clear());
                avatarImageView.setOpaque(false);
                avatarImageView.getProfileButton().addOnTouchUpInsideListener(didTapUserNameButton);
                avatarImageView.setContentMode(UIViewContentMode.ScaleAspectFill);
                avatarImageView.getLayer().setCornerRadius(AVATAR_IMAGE_DIM / 2.0);
                avatarImageView.getLayer().setMasksToBounds(true);
                nameHeaderView.addSubview(avatarImageView);

                // Create name label
                String nameString = photographer.getDisplayName();
                UIButton userButton = UIButton.create(UIButtonType.Custom);
                nameHeaderView.addSubview(userButton);
                userButton.setBackgroundColor(UIColor.clear());
                userButton.getTitleLabel().setFont(UIFont.getBoldSystemFont(15));
                userButton.setTitle(nameString, UIControlState.Normal);
                userButton.setTitleColor(UIColor.fromRGBA(34f / 255f, 34f / 255f, 34f / 255f, 1), UIControlState.Normal);
                userButton.setTitleColor(UIColor.fromRGBA(114f / 255f, 114f / 255f, 114f / 255f, 1),
                        UIControlState.Highlighted);
                userButton.getTitleLabel().setLineBreakMode(NSLineBreakMode.TruncatingTail);
                userButton.addOnTouchUpInsideListener(didTapUserNameButton);

                // we resize the button to fit the user's name to avoid having a
                // huge touch area
                CGPoint userButtonPoint = new CGPoint(50, 6);
                double constrainWidth = nameHeaderView.getBounds().getSize().getWidth()
                        - (avatarImageView.getBounds().getOrigin().getX() + avatarImageView.getBounds().getSize()
                                .getWidth());

                CGSize constrainSize = new CGSize(constrainWidth, nameHeaderView.getBounds().getSize().getHeight()
                        - userButtonPoint.getY() * 2);
                CGSize userButtonSize = NSString.getBoundingRect(userButton.getTitleLabel().getText(), constrainSize,
                        NSStringDrawingOptions.TruncatesLastVisibleLine,
                        new NSAttributedStringAttributes().setFont(userButton.getTitleLabel().getFont()), null)
                        .getSize();

                CGRect userButtonFrame = new CGRect(userButtonPoint.getX(), userButtonPoint.getY(), userButtonSize
                        .getWidth(), userButtonSize.getHeight());
                userButton.setFrame(userButtonFrame);

                // Create time label
                String timeString = timeFormatter.format(new NSDate(), photo.getCreatedAt());
                CGSize timeLabelSize = NSString.getBoundingRect(timeString,
                        new CGSize(TIME_LABEL_MAX_WIDTH, Float.MAX_VALUE),
                        NSStringDrawingOptions.with(NSStringDrawingOptions.TruncatesLastVisibleLine,
                                NSStringDrawingOptions.UsesLineFragmentOrigin),
                        new NSAttributedStringAttributes().setFont(UIFont.getSystemFont(11)), null).getSize();

                UILabel timeLabel = new UILabel(new CGRect(TIME_LABEL_X, NAME_LABEL_Y + userButtonSize.getHeight(),
                        timeLabelSize.getWidth(), timeLabelSize.getHeight()));
                timeLabel.setText(timeString);
                timeLabel.setFont(UIFont.getSystemFont(11));
                timeLabel.setTextColor(UIColor.fromRGBA(114f / 255f, 114f / 255f, 114f / 255f, 1));
                timeLabel.setBackgroundColor(UIColor.clear());
                nameHeaderView.addSubview(timeLabel);

                setNeedsDisplay();
            }
        });

        /*
         * Create bottom section fo the header view; the likes
         */
        likeBarView = new UIView(new CGRect(LIKE_BAR_X, LIKE_BAR_Y, LIKE_BAR_WIDTH, LIKE_BAR_HEIGHT));
        likeBarView.setBackgroundColor(UIColor.white());
        addSubview(likeBarView);

        // Create the heart-shaped like button
        likeButton = UIButton.create(UIButtonType.Custom);
        likeButton.setFrame(new CGRect(LIKE_BUTTON_X, LIKE_BUTTON_Y, LIKE_BUTTON_DIM, LIKE_BUTTON_DIM));
        likeButton.setBackgroundColor(UIColor.clear());
        likeButton.setTitleColor(UIColor.fromRGBA(254f / 255f, 149f / 255f, 50f / 255f, 1), UIControlState.Normal);
        likeButton.setTitleColor(UIColor.white(), UIControlState.Selected);
        likeButton.setTitleEdgeInsets(new UIEdgeInsets(0, 0, 0, 0));
        likeButton.getTitleLabel().setFont(UIFont.getSystemFont(12));
        likeButton.getTitleLabel().setMinimumScaleFactor(0.8);
        likeButton.getTitleLabel().setAdjustsFontSizeToFitWidth(true);
        likeButton.setAdjustsImageWhenDisabled(false);
        likeButton.setAdjustsImageWhenHighlighted(false);
        likeButton.setBackgroundImage(UIImage.create("ButtonLike"), UIControlState.Normal);
        likeButton.setBackgroundImage(UIImage.create("ButtonLikeSelected"), UIControlState.Selected);
        likeButton.addOnTouchUpInsideListener(didTapLikePhotoButton);
        likeBarView.addSubview(likeButton);

        reloadLikeBar();

        UIImageView separator = new UIImageView(UIImage.create("SeparatorComments").createResizable(
                new UIEdgeInsets(0, 1, 0, 1)));
        separator.setFrame(new CGRect(0, likeBarView.getFrame().getSize().getHeight() - 1, likeBarView.getFrame()
                .getSize().getWidth(), 1));
    }

    private final UIControl.OnTouchUpInsideListener didTapLikePhotoButton = new UIControl.OnTouchUpInsideListener() {
        @Override
        public void onTouchUpInside(UIControl control, UIEvent event) {
            final UIButton button = (UIButton) control;
            boolean liked = !button.isSelected();
            button.removeListener(didTapLikePhotoButton);
            setLikeButtonState(liked);

            final List<PAPUser> originalLikeUsers = new ArrayList<>(likeUsers);
            final Set<PAPUser> newLikeUsersSet = new HashSet<>(likeUsers.size());

            for (PAPUser likeUser : likeUsers) {
                // add all current likeUsers BUT currentUser
                if (likeUser.getObjectId().equals(PAPUser.getCurrentUser().getObjectId())) {
                    newLikeUsersSet.add(likeUser);
                }
            }

            if (liked) {
                PAPCache.getSharedCache().incrementPhotoLikerCount(photo);
                newLikeUsersSet.add(PAPUser.getCurrentUser());
            } else {
                PAPCache.getSharedCache().decrementPhotoLikerCount(photo);
            }

            PAPCache.getSharedCache().setPhotoIsLikedByCurrentUser(photo, liked);

            setLikeUsers(new ArrayList<>(newLikeUsersSet));

            if (liked) {
                PAPUtility.likePhotoInBackground(photo, new PFSaveCallback() {
                    @Override
                    public void done(boolean success, NSError error) {
                        if (!success) {
                            button.addOnTouchUpInsideListener(didTapLikePhotoButton);
                            setLikeUsers(originalLikeUsers);
                            setLikeButtonState(false);
                        }
                    }
                });
            } else {
                PAPUtility.unlikePhotoInBackground(photo, new PFSaveCallback() {
                    @Override
                    public void done(boolean success, NSError error) {
                        if (!success) {
                            button.addOnTouchUpInsideListener(didTapLikePhotoButton);
                            setLikeUsers(originalLikeUsers);
                            setLikeButtonState(true);
                        }
                    }
                });
            }

            NSDictionary<?, ?> notificationPayload = new NSMutableDictionary<>();
            notificationPayload.put("liked", liked);
            PAPNotificationManager.postNotification(PAPNotification.USER_LIKES_PHOTO, photo, notificationPayload);
        }
    };

    public void setDelegate(PAPPhotoDetailsHeaderViewDelegate delegate) {
        this.delegate = delegate;
    }

    private final UIControl.OnTouchUpInsideListener didTapLikerButton = new UIControl.OnTouchUpInsideListener() {
        @Override
        public void onTouchUpInside(UIControl control, UIEvent event) {
            UIButton button = (UIButton) control;
            PAPUser user = likeUsers.get((int) button.getTag());
            if (delegate != null) {
                delegate.didTapUserButton(PAPPhotoDetailsHeaderView.this, button, user);
            }
        }
    };

    private final UIControl.OnTouchUpInsideListener didTapUserNameButton = new UIControl.OnTouchUpInsideListener() {
        @Override
        public void onTouchUpInside(UIControl control, UIEvent event) {
            if (delegate != null) {
                delegate.didTapUserButton(PAPPhotoDetailsHeaderView.this, (UIButton) control, photographer);
            }
        }
    };
}
