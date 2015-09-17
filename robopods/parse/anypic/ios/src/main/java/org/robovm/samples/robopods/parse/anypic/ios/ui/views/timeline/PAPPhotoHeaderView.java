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
package org.robovm.samples.robopods.parse.anypic.ios.ui.views.timeline;

import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
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
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewContentMode;
import org.robovm.pods.parse.PFFile;
import org.robovm.pods.parse.ui.PFTableViewCell;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPPhoto;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPUser;
import org.robovm.samples.robopods.parse.anypic.ios.ui.views.PAPProfileImageView;
import org.robovm.samples.robopods.parse.anypic.ios.ui.views.timeline.PAPPhotoHeaderButtons.PAPPhotoHeaderButton;
import org.robovm.samples.robopods.parse.anypic.ios.util.PAPUtility;
import org.robovm.samples.robopods.parse.anypic.ios.util.TTTTimeIntervalFormatter;

public class PAPPhotoHeaderView extends PFTableViewCell {
    private PAPPhoto photo;
    private final PAPPhotoHeaderButtons buttons;

    private UIButton likeButton;
    private UIButton commentButton;
    private final UIView containerView;
    private final PAPProfileImageView avatarImageView;

    private PAPPhotoHeaderViewDelegate delegate;

    private UIButton userButton;
    private final UILabel timestampLabel;
    private final TTTTimeIntervalFormatter timeFormatter;

    /**
     * Initializes the view with the specified interaction elements.
     * 
     * @param buttons A bitmask specifying the interaction elements which are
     *            enabled in the view
     */
    public PAPPhotoHeaderView(CGRect frame, PAPPhotoHeaderButtons otherButtons) {
        super(frame);
        validateButtons(otherButtons);
        this.buttons = otherButtons;

        setClipsToBounds(false);
        setBackgroundColor(UIColor.clear());

        // translucent portion
        containerView = new UIView(
                new CGRect(0, 0, getBounds().getSize().getWidth(), getBounds().getSize().getHeight()));
        containerView.setClipsToBounds(false);
        addSubview(containerView);
        containerView.setBackgroundColor(UIColor.white());

        avatarImageView = new PAPProfileImageView(new CGRect(4, 4, 35, 35));
        avatarImageView.getProfileButton().addOnTouchUpInsideListener(didTapUserButton);
        containerView.addSubview(avatarImageView);

        if (buttons.contains(PAPPhotoHeaderButton.Comment)) {
            commentButton = new UIButton(UIButtonType.Custom);
            containerView.addSubview(commentButton);
            commentButton.setFrame(new CGRect(282, 10, 29, 29));
            commentButton.setBackgroundColor(UIColor.clear());
            commentButton.setTitle("", UIControlState.Normal);
            commentButton.setTitleColor(UIColor.fromRGBA(254f / 255f, 149f / 255f, 50f / 255f, 1),
                    UIControlState.Normal);
            commentButton.setTitleEdgeInsets(new UIEdgeInsets(-6, 0, 0, 0));
            commentButton.getTitleLabel().setFont(UIFont.getSystemFont(12));
            commentButton.getTitleLabel().setMinimumScaleFactor(0.8);
            commentButton.getTitleLabel().setAdjustsFontSizeToFitWidth(true);
            commentButton.setBackgroundImage(UIImage.getImage("IconComment"), UIControlState.Normal);
            commentButton.setSelected(false);
        }

        if (buttons.contains(PAPPhotoHeaderButton.Like)) {
            likeButton = new UIButton(UIButtonType.Custom);
            containerView.addSubview(likeButton);
            likeButton.setFrame(new CGRect(246, 9, 29, 29));
            likeButton.setBackgroundColor(UIColor.clear());
            likeButton.setTitle("", UIControlState.Normal);
            likeButton.setTitleColor(UIColor.fromRGBA(254f / 255f, 149f / 255f, 50f / 255f, 1), UIControlState.Normal);
            likeButton.setTitleColor(UIColor.white(), UIControlState.Selected);
            likeButton.setTitleEdgeInsets(new UIEdgeInsets(0, 0, 0, 0));
            likeButton.getTitleLabel().setFont(UIFont.getSystemFont(12));
            likeButton.getTitleLabel().setMinimumScaleFactor(0.8);
            likeButton.getTitleLabel().setAdjustsFontSizeToFitWidth(true);
            likeButton.setAdjustsImageWhenHighlighted(false);
            likeButton.setAdjustsImageWhenDisabled(false);
            likeButton.setBackgroundImage(UIImage.getImage("ButtonLike"), UIControlState.Normal);
            likeButton.setBackgroundImage(UIImage.getImage("ButtonLikeSelected"), UIControlState.Normal);
            likeButton.setSelected(false);
        }

        if (buttons.contains(PAPPhotoHeaderButton.User)) {
            // This is the user's display name, on a button so that we can tap
            // on it
            userButton = new UIButton(UIButtonType.Custom);
            containerView.addSubview(userButton);
            userButton.setBackgroundColor(UIColor.clear());
            userButton.getTitleLabel().setFont(UIFont.getBoldSystemFont(15));
            userButton.setTitleColor(UIColor.fromRGBA(34f / 255f, 34f / 255f, 34f / 255f, 1), UIControlState.Normal);
            userButton.setTitleColor(UIColor.black(), UIControlState.Highlighted);
            userButton.getTitleLabel().setLineBreakMode(NSLineBreakMode.TruncatingTail);
        }

        timeFormatter = new TTTTimeIntervalFormatter();

        // timestamp
        timestampLabel = new UILabel(new CGRect(50, 24, containerView.getBounds().getSize().getWidth() - 50 - 72, 18));
        containerView.addSubview(timestampLabel);
        timestampLabel.setTextColor(UIColor.fromRGBA(114f / 255f, 114f / 255f, 114f / 255f, 1));
        timestampLabel.setFont(UIFont.getSystemFont(11));
        timestampLabel.setBackgroundColor(UIColor.clear());
    }

    /**
     * Configures the Like Button to match the given like status.
     * 
     * @param liked a boolean indicating if the associated photo is liked by the
     *            user
     */
    public void setLikeStatus(boolean liked) {
        likeButton.setSelected(liked);

        if (liked) {
            likeButton.setTitleEdgeInsets(new UIEdgeInsets(-3, 0, 0, 0));
        } else {
            likeButton.setTitleEdgeInsets(new UIEdgeInsets(-3, 0, 0, 0));
        }
    }

    /**
     * Enable the like button to start receiving actions.
     * 
     * @param enable a boolean indicating if the like button should be enabled.
     */
    public void shouldEnableLikeButton(boolean enable) {
        if (enable) {
            likeButton.removeListener(didTapLikePhotoButton);
        } else {
            likeButton.addOnTouchUpInsideListener(didTapLikePhotoButton);
        }
    }

    public void setPhoto(PAPPhoto photo) {
        this.photo = photo;

        // user's avatar
        PAPUser user = photo.getUser();
        if (PAPUtility.userHasProfilePictures(user)) {
            PFFile profilePictureSmall = user.getProfilePicSmall();
            avatarImageView.setFile(profilePictureSmall);
        } else {
            avatarImageView.setImage(PAPUtility.getDefaultProfilePicture());
        }

        avatarImageView.setContentMode(UIViewContentMode.ScaleAspectFill);
        avatarImageView.getLayer().setCornerRadius(17.5);
        avatarImageView.getLayer().setMasksToBounds(true);

        String authorName = user.getDisplayName();
        userButton.setTitle(authorName, UIControlState.Normal);

        double constrainWidth = containerView.getBounds().getSize().getWidth();

        if (buttons.contains(PAPPhotoHeaderButton.User)) {
            userButton.addOnTouchUpInsideListener(didTapUserButton);
        }

        if (buttons.contains(PAPPhotoHeaderButton.Comment)) {
            constrainWidth = commentButton.getFrame().getOrigin().getX();
            commentButton.addOnTouchUpInsideListener(didTapCommentOnPhotoButton);
        }

        if (buttons.contains(PAPPhotoHeaderButton.Like)) {
            constrainWidth = likeButton.getFrame().getOrigin().getX();
            likeButton.addOnTouchUpInsideListener(didTapLikePhotoButton);
        }

        // we resize the button to fit the user's name to avoid having a huge
        // touch area
        CGPoint userButtonPoint = new CGPoint(50, 6);
        constrainWidth -= userButtonPoint.getX();
        CGSize constrainSize = new CGSize(constrainWidth, containerView.getBounds().getSize().getHeight()
                - userButtonPoint.getY() * 2f);

        CGSize userButtonSize = NSString.getBoundingRect(
                userButton.getTitleLabel().getText(),
                constrainSize,
                NSStringDrawingOptions.with(NSStringDrawingOptions.TruncatesLastVisibleLine,
                        NSStringDrawingOptions.UsesLineFragmentOrigin),
                new NSAttributedStringAttributes().setFont(userButton.getTitleLabel().getFont()), null).getSize();

        CGRect userButtonFrame = new CGRect(userButtonPoint.getX(), userButtonPoint.getY(), userButtonSize.getWidth(),
                userButtonSize.getHeight());
        userButton.setFrame(userButtonFrame);

        double timeInterval = photo.getCreatedAt().getTimeIntervalSinceNow();
        String timestamp = timeFormatter.format(timeInterval);
        timestampLabel.setText(timestamp);

        setNeedsDisplay();
    }

    private static void validateButtons(PAPPhotoHeaderButtons buttons) {
        if (buttons.contains(PAPPhotoHeaderButton.None)) {
            throw new RuntimeException("Buttons must be set before initializing PAPPhotoHeaderView.");
        }
    }

    public void setDelegate(PAPPhotoHeaderViewDelegate delegate) {
        this.delegate = delegate;
    }

    public UIButton getLikeButton() {
        return likeButton;
    }

    public UIButton getCommentButton() {
        return commentButton;
    }

    private final UIControl.OnTouchUpInsideListener didTapUserButton = new UIControl.OnTouchUpInsideListener() {
        @Override
        public void onTouchUpInside(UIControl control, UIEvent event) {
            if (delegate != null) {
                delegate.didTapUserButton(PAPPhotoHeaderView.this, (UIButton) control, photo.getUser());
            }
        }
    };

    private final UIControl.OnTouchUpInsideListener didTapLikePhotoButton = new UIControl.OnTouchUpInsideListener() {
        @Override
        public void onTouchUpInside(UIControl control, UIEvent event) {
            if (delegate != null) {
                delegate.didTapLikePhotoButton(PAPPhotoHeaderView.this, (UIButton) control, photo);
            }
        }
    };

    private final UIControl.OnTouchUpInsideListener didTapCommentOnPhotoButton = new UIControl.OnTouchUpInsideListener() {
        @Override
        public void onTouchUpInside(UIControl control, UIEvent event) {
            if (delegate != null) {
                delegate.didTapCommentOnPhotoButton(PAPPhotoHeaderView.this, (UIButton) control, photo);
            }
        }
    };
}
