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
package org.robovm.samples.robopods.parse.anypic.ios.ui.views.tablecells;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSDate;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.NSAttributedStringAttributes;
import org.robovm.apple.uikit.NSStringDrawingOptions;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIButtonType;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UITableViewCellAccessoryType;
import org.robovm.apple.uikit.UITableViewCellSelectionStyle;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.pods.parse.PFFile;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPActivity;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPActivityType;
import org.robovm.samples.robopods.parse.anypic.ios.ui.views.PAPProfileImageView;
import org.robovm.samples.robopods.parse.anypic.ios.util.PAPUtility;

public class PAPActivityCell extends PAPBaseTextCell {
    private PAPActivity activity;

    private final PAPProfileImageView activityImageView;
    private final UIButton activityImageButton;

    /** Flag to remove the right-hand side image if not necessary */
    private boolean hasActivityImage;

    public PAPActivityCell(UITableViewCellStyle style, String reuseIdentifier) {
        super(style, reuseIdentifier);

        horizontalTextSpace = getHorizontalTextSpaceForInsetWidth(0);

        // Create subviews and set cell properties
        setOpaque(true);
        setSelectionStyle(UITableViewCellSelectionStyle.None);
        setAccessoryType(UITableViewCellAccessoryType.None);

        activityImageView = new PAPProfileImageView();
        activityImageView.setBackgroundColor(UIColor.clear());
        activityImageView.setOpaque(true);
        mainView.addSubview(activityImageView);

        activityImageButton = new UIButton(UIButtonType.Custom);
        activityImageButton.setBackgroundColor(UIColor.clear());
        activityImageButton.addOnTouchUpInsideListener(didTapActivityButton);
        mainView.addSubview(activityImageButton);
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();

        // Layout the activity image and show it if it is not nil (no image for
        // the follow activity).
        // Note that the image view is still allocated and ready to be dispalyed
        // since these cells
        // will be reused for all types of activity.
        activityImageView.setFrame(new CGRect(UIScreen.getMainScreen().getBounds().getSize().getWidth() - 46, 13, 33,
                33));
        activityImageButton.setFrame(new CGRect(UIScreen.getMainScreen().getBounds().getSize().getWidth() - 46, 13, 33,
                33));

        // Add activity image if one was set
        if (hasActivityImage) {
            activityImageView.setHidden(false);
            activityImageButton.setHidden(false);
        } else {
            activityImageView.setHidden(true);
            activityImageButton.setHidden(true);
        }

        // Change frame of the content text so it doesn't go through the
        // right-hand side picture
        CGSize contentSize = NSString.getBoundingRect(contentLabel.getText(),
                new CGSize(UIScreen.getMainScreen().getBounds().getSize().getWidth() - 72 - 46, Float.MAX_VALUE),
                NSStringDrawingOptions.UsesLineFragmentOrigin,
                new NSAttributedStringAttributes().setFont(UIFont.getSystemFont(13)), null).getSize();
        contentLabel.setFrame(new CGRect(46, 16, contentSize.getWidth(), contentSize.getHeight()));

        // Layout the timestamp label given new vertical
        CGSize timeSize = NSString.getBoundingRect(timeLabel.getText(),
                new CGSize(UIScreen.getMainScreen().getBounds().getSize().getWidth() - 72 - 46, Float.MAX_VALUE),
                NSStringDrawingOptions.with(NSStringDrawingOptions.TruncatesLastVisibleLine,
                        NSStringDrawingOptions.UsesLineFragmentOrigin),
                new NSAttributedStringAttributes().setFont(UIFont.getSystemFont(11)), null).getSize();
        timeLabel.setFrame(new CGRect(46, contentLabel.getFrame().getOrigin().getY()
                + contentLabel.getFrame().getSize().getHeight() + 7, timeSize.getWidth(), timeSize.getHeight()));

    }

    public void setIsNew(boolean isNew) {
        if (isNew) {
            mainView.setBackgroundColor(UIColor.fromRGBA(29f / 255f, 29f / 255f, 29f / 255f, 1));
        } else {
            mainView.setBackgroundColor(UIColor.black());
        }
    }

    public void setActivity(PAPActivity activity) {
        // Set the activity property
        this.activity = activity;
        if (activity.getType() == PAPActivityType.FOLLOW) {
            setActivityImageFile(null);
        } else {
            setActivityImageFile(activity.getPhoto().getThumbnail());
        }

        String activityString = activity.getType().getMessage();
        user = activity.getFromUser();

        // Set name button properties and avatar image
        if (PAPUtility.userHasProfilePictures(user)) {
            avatarImageView.setFile(user.getProfilePicSmall());
        } else {
            avatarImageView.setImage(PAPUtility.getDefaultProfilePicture());
        }

        String nameString = "Someone";
        if (user != null && user.getDisplayName() != null && user.getDisplayName().length() > 0) {
            nameString = user.getDisplayName();
        }

        nameButton.setTitle(nameString, UIControlState.Normal);
        nameButton.setTitle(nameString, UIControlState.Highlighted);

        // If user is set after the contentText, we reset the content to include
        // padding
        if (contentLabel.getText() != null) {
            setContentText(contentLabel.getText());
        }

        if (user != null) {
            CGSize nameSize = NSString.getBoundingRect(
                    nameButton.getTitleLabel().getText(),
                    new CGSize(NAME_MAX_WIDTH, Float.MAX_VALUE),
                    NSStringDrawingOptions.with(NSStringDrawingOptions.TruncatesLastVisibleLine,
                            NSStringDrawingOptions.UsesLineFragmentOrigin),
                    new NSAttributedStringAttributes().setFont(UIFont.getBoldSystemFont(13)), null).getSize();
            String paddedString = padString(activityString, UIFont.getSystemFont(13), nameSize.getWidth());
            contentLabel.setText(paddedString);
        } else {
            // Otherwise we ignore the padding and we'll add it after we set the
            // user
            contentLabel.setText(activityString);
        }

        timeLabel.setText(timeFormatter.format(new NSDate(), activity.getCreatedAt()));

        setNeedsDisplay();
    }

    @Override
    public void setCellInsetWidth(double insetWidth) {
        super.setCellInsetWidth(insetWidth);
        horizontalTextSpace = getHorizontalTextSpaceForInsetWidth(insetWidth);
    }

    @Override
    public PAPActivityCellDelegate getDelegate() {
        return (PAPActivityCellDelegate) super.getDelegate();
    }

    public void setDelegate(PAPActivityCellDelegate delegate) {
        super.setDelegate(delegate);
    }

    private static double getHorizontalTextSpaceForInsetWidth(double insetWidth) {
        return (UIScreen.getMainScreen().getBounds().getSize().getWidth() - (insetWidth * 2)) - 72 - 46;
    }

    public static double getHeightForCell(String name, String content) {
        return getHeightForCell(name, content, 0);
    }

    public static double getHeightForCell(String name, String content, double cellInset) {
        CGSize nameSize = NSString.getBoundingRect(name, new CGSize(200, Float.MAX_VALUE),
                NSStringDrawingOptions.with(NSStringDrawingOptions.TruncatesLastVisibleLine,
                        NSStringDrawingOptions.UsesLineFragmentOrigin),
                new NSAttributedStringAttributes().setFont(UIFont.getBoldSystemFont(13)), null).getSize();

        String paddedString = padString(content, UIFont.getSystemFont(13), nameSize.getWidth());
        double horizontalTextSpace = getHorizontalTextSpaceForInsetWidth(cellInset);

        CGSize contentSize = NSString.getBoundingRect(paddedString, new CGSize(horizontalTextSpace, Float.MAX_VALUE),
                NSStringDrawingOptions.UsesLineFragmentOrigin,
                new NSAttributedStringAttributes().setFont(UIFont.getSystemFont(13)), null).getSize();

        double singleLineHeight = NSString.getBoundingRect("test", new CGSize(Float.MAX_VALUE, Float.MAX_VALUE),
                NSStringDrawingOptions.UsesLineFragmentOrigin,
                new NSAttributedStringAttributes().setFont(UIFont.getSystemFont(13)), null).getSize().getHeight();

        // Calculate the added height necessary for multiline text. Ensure value
        // is not below 0.
        double multilineHeightAddition = contentSize.getHeight() - singleLineHeight;

        return 58 + Math.max(0, multilineHeightAddition);
    }

    private void setActivityImageFile(PFFile imageFile) {
        if (imageFile != null) {
            activityImageView.setFile(imageFile);
            hasActivityImage = true;
        } else {
            hasActivityImage = false;
        }
    }

    private final UIControl.OnTouchUpInsideListener didTapActivityButton = new UIControl.OnTouchUpInsideListener() {
        @Override
        public void onTouchUpInside(UIControl control, UIEvent event) {
            if (getDelegate() != null) {
                getDelegate().didTapActivityButton(PAPActivityCell.this, activity);
            }
        }
    };
}
