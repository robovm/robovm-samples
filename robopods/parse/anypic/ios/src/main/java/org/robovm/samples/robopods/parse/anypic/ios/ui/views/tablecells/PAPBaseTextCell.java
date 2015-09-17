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
import org.robovm.apple.foundation.NSDateComponentsFormatter;
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
import org.robovm.apple.uikit.UITableViewCellAccessoryType;
import org.robovm.apple.uikit.UITableViewCellSelectionStyle;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UIView;
import org.robovm.pods.parse.ui.PFTableViewCell;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPUser;
import org.robovm.samples.robopods.parse.anypic.ios.ui.views.PAPProfileImageView;
import org.robovm.samples.robopods.parse.anypic.ios.util.PAPUtility;

public class PAPBaseTextCell extends PFTableViewCell {
    private static final double VERT_BORDER_SPACING = 8;
    private static final double VERT_ELEM_SPACING = 0;

    private static final double HORI_BORDER_SPACING = 8;
    private static final double HORI_BORDER_SPACING_BOTTOM = 9;
    private static final double HORI_ELEM_SPACING = 5;

    private static final double VERT_TEXT_BORDER_SPACING = 10;

    private static final double AVATAR_X = HORI_BORDER_SPACING;
    private static final double AVATAR_Y = VERT_BORDER_SPACING;
    private static final double AVATAR_DIM = 33;

    private static final double NAME_X = AVATAR_X + AVATAR_DIM + HORI_ELEM_SPACING;
    private static final double NAME_Y = VERT_TEXT_BORDER_SPACING;
    protected static final double NAME_MAX_WIDTH = 200;

    private static final double TIME_X = AVATAR_X + AVATAR_DIM + HORI_ELEM_SPACING;

    private PAPBaseTextCellDelegate delegate;
    protected PAPUser user;

    protected final UIView mainView;
    protected final UIButton nameButton;
    protected final UIButton avatarImageButton;
    protected final PAPProfileImageView avatarImageView;
    protected final UILabel contentLabel;
    protected final UILabel timeLabel;
    protected final UIImageView separatorImage;

    private double cellInsetWidth;
    protected double horizontalTextSpace;

    protected static final NSDateComponentsFormatter timeFormatter = new NSDateComponentsFormatter();

    private boolean hideSeparator;

    public PAPBaseTextCell(UITableViewCellStyle style, String reuseIdentifier) {
        super(style, reuseIdentifier);

        setClipsToBounds(true);
        horizontalTextSpace = PAPBaseTextCell.getHorizontalTextSpaceForInsetWidth(cellInsetWidth);

        setOpaque(true);
        setSelectionStyle(UITableViewCellSelectionStyle.None);
        setAccessoryType(UITableViewCellAccessoryType.None);
        setBackgroundColor(UIColor.clear());

        mainView = new UIView(getContentView().getFrame());
        mainView.setBackgroundColor(UIColor.white());

        avatarImageView = new PAPProfileImageView();
        avatarImageView.setBackgroundColor(UIColor.clear());
        avatarImageView.setOpaque(true);
        avatarImageView.getLayer().setCornerRadius(16);
        avatarImageView.getLayer().setMasksToBounds(true);
        mainView.addSubview(avatarImageView);

        nameButton = new UIButton(UIButtonType.Custom);
        nameButton.setBackgroundColor(UIColor.clear());

        if (reuseIdentifier.equals("ActivityCell")) {
            nameButton.setTitleColor(UIColor.white(), UIControlState.Normal);
            nameButton.setTitleColor(UIColor.fromRGBA(114f / 255f, 114f / 255f, 114f / 255f, 1),
                    UIControlState.Highlighted);
        } else {
            nameButton.setTitleColor(UIColor.fromRGBA(34f / 255f, 34f / 255f, 34f / 255f, 1), UIControlState.Normal);
            nameButton.setTitleColor(UIColor.fromRGBA(114f / 255f, 114f / 255f, 114f / 255f, 1),
                    UIControlState.Highlighted);
        }

        nameButton.getTitleLabel().setFont(UIFont.getBoldSystemFont(13));
        nameButton.getTitleLabel().setLineBreakMode(NSLineBreakMode.TruncatingTail);
        nameButton.addOnTouchUpInsideListener(didTapUserButton);
        mainView.addSubview(nameButton);

        contentLabel = new UILabel();
        contentLabel.setFont(UIFont.getSystemFont(13));
        if (reuseIdentifier.equals("ActivityCell")) {
            contentLabel.setTextColor(UIColor.white());
        } else {
            contentLabel.setTextColor(UIColor.fromRGBA(34f / 255f, 34f / 255f, 34f / 255f, 1));
        }
        contentLabel.setNumberOfLines(0);
        contentLabel.setLineBreakMode(NSLineBreakMode.WordWrapping);
        contentLabel.setBackgroundColor(UIColor.clear());
        mainView.addSubview(contentLabel);

        timeLabel = new UILabel();
        timeLabel.setFont(UIFont.getSystemFont(11));
        timeLabel.setTextColor(UIColor.fromRGBA(114f / 255f, 114f / 255f, 114f / 255f, 1));
        timeLabel.setBackgroundColor(UIColor.clear());
        mainView.addSubview(timeLabel);

        avatarImageButton = new UIButton(UIButtonType.Custom);
        avatarImageButton.setBackgroundColor(UIColor.clear());
        avatarImageButton.addOnTouchUpInsideListener(didTapUserButton);

        mainView.addSubview(avatarImageButton);

        separatorImage = new UIImageView(UIImage.getImage("SeparatorComments").newResizableImage(
                new UIEdgeInsets(0, 1, 0, 1)));

        getContentView().addSubview(mainView);
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();

        mainView.setFrame(new CGRect(cellInsetWidth, getContentView().getFrame().getOrigin().getY(), getContentView()
                .getFrame().getSize().getWidth()
                - 2 * cellInsetWidth, getContentView().getFrame().getSize().getHeight()));

        // Layout avatar image
        avatarImageView.setFrame(new CGRect(AVATAR_X, AVATAR_Y + 5, AVATAR_DIM, AVATAR_DIM));
        avatarImageButton.setFrame(new CGRect(AVATAR_X, AVATAR_Y + 5, AVATAR_DIM, AVATAR_DIM));

        // Layout the name button
        CGSize nameSize = NSString.getBoundingRect(
                nameButton.getTitleLabel().getText(),
                new CGSize(NAME_MAX_WIDTH, Float.MAX_VALUE),
                NSStringDrawingOptions.with(NSStringDrawingOptions.TruncatesLastVisibleLine,
                        NSStringDrawingOptions.UsesLineFragmentOrigin),
                new NSAttributedStringAttributes().setFont(UIFont.getBoldSystemFont(13)), null).getSize();
        nameButton.setFrame(new CGRect(NAME_X, NAME_Y + 6, nameSize.getWidth(), nameSize.getHeight()));

        // Layout the content
        CGSize contentSize = NSString.getBoundingRect(contentLabel.getText(),
                new CGSize(horizontalTextSpace, Float.MAX_VALUE), NSStringDrawingOptions.UsesLineFragmentOrigin,
                new NSAttributedStringAttributes().setFont(UIFont.getSystemFont(13)), null).getSize();
        contentLabel.setFrame(new CGRect(NAME_X, VERT_TEXT_BORDER_SPACING + 6, contentSize.getWidth(), contentSize
                .getHeight()));

        // Layout the timestamp label
        CGSize timeSize = NSString.getBoundingRect(
                timeLabel.getText(),
                new CGSize(horizontalTextSpace, Float.MAX_VALUE),
                NSStringDrawingOptions.with(NSStringDrawingOptions.TruncatesLastVisibleLine,
                        NSStringDrawingOptions.UsesLineFragmentOrigin),
                new NSAttributedStringAttributes().setFont(UIFont.getSystemFont(11)), null).getSize();
        timeLabel.setFrame(new CGRect(TIME_X, contentLabel.getFrame().getOrigin().getY()
                + contentLabel.getFrame().getSize().getHeight() + VERT_ELEM_SPACING, timeSize.getWidth(), timeSize
                .getHeight()));

        // Layour separator
        separatorImage.setFrame(new CGRect(0, getFrame().getSize().getHeight() - 1, getFrame().getSize().getWidth()
                - cellInsetWidth * 2, 1));
        separatorImage.setHidden(hideSeparator);
    }

    private final UIControl.OnTouchUpInsideListener didTapUserButton = new UIControl.OnTouchUpInsideListener() {
        @Override
        public void onTouchUpInside(UIControl control, UIEvent event) {
            if (delegate != null) {
                delegate.didTapUserButton(PAPBaseTextCell.this, user);
            }
        }
    };

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
        double multilineHeightAddition = (contentSize.getHeight() - singleLineHeight) > 0 ? (contentSize.getHeight() - singleLineHeight)
                : 0;

        return HORI_BORDER_SPACING + AVATAR_DIM + HORI_BORDER_SPACING_BOTTOM + multilineHeightAddition;
    }

    private static double getHorizontalTextSpaceForInsetWidth(double insetWidth) {
        return (320 - (insetWidth * 2)) - (HORI_BORDER_SPACING + AVATAR_DIM + HORI_ELEM_SPACING + HORI_BORDER_SPACING);
    }

    protected static String padString(String string, UIFont font, double width) {
        // Find number of spaces to pad
        StringBuilder sb = new StringBuilder();
        CGSize size = new CGSize(Float.MAX_VALUE, Float.MAX_VALUE);
        NSStringDrawingOptions options = NSStringDrawingOptions.with(NSStringDrawingOptions.TruncatesLastVisibleLine,
                NSStringDrawingOptions.UsesLineFragmentOrigin);
        NSAttributedStringAttributes attr = new NSAttributedStringAttributes().setFont(font);
        while (true) {
            sb.append(" ");
            CGSize resultSize = NSString.getBoundingRect(sb.toString(), size, options, attr, null).getSize();
            if (resultSize.getWidth() >= width) {
                break;
            }
        }

        // Add final spaces to be ready for first word
        sb.append(String.format(" %s", string));
        return sb.toString();
    }

    public void setUser(PAPUser user) {
        this.user = user;

        // Set name button properties and avatar image
        if (PAPUtility.userHasProfilePictures(user)) {
            avatarImageView.setFile(user.getProfilePicSmall());
        } else {
            avatarImageView.setImage(PAPUtility.getDefaultProfilePicture());
        }

        nameButton.setTitle(user.getDisplayName(), UIControlState.Normal);
        nameButton.setTitle(user.getDisplayName(), UIControlState.Highlighted);

        // If user is set after the contentText, we reset the content to include
        // padding
        if (contentLabel.getText() != null) {
            setContentText(contentLabel.getText());
        }
        setNeedsDisplay();
    }

    public void setContentText(String contentString) {
        // If we have a user we pad the content with spaces to make room for the
        // name
        if (user != null) {
            CGSize nameSize = NSString.getBoundingRect(
                    nameButton.getTitleLabel().getText(),
                    new CGSize(NAME_MAX_WIDTH, Float.MAX_VALUE),
                    NSStringDrawingOptions.with(NSStringDrawingOptions.TruncatesLastVisibleLine,
                            NSStringDrawingOptions.UsesLineFragmentOrigin),
                    new NSAttributedStringAttributes().setFont(UIFont.getBoldSystemFont(13)), null).getSize();
            String paddedString = padString(contentString, UIFont.getSystemFont(13), nameSize.getWidth());
            contentLabel.setText(paddedString);
        } else {
            // Otherwise we ignore the padding and we'll add it after we set the
            // user
            contentLabel.setText(contentString);
        }
        setNeedsDisplay();
    }

    public void setDate(NSDate date) {
        // Set the label with a human readable time
        timeLabel.setText(timeFormatter.format(new NSDate(), date));
        setNeedsDisplay();
    }

    public void setCellInsetWidth(double insetWidth) {
        // Change the mainView's frame to be insetted by insetWidth and update
        // the content text space
        cellInsetWidth = insetWidth;
        mainView.setFrame(new CGRect(insetWidth, mainView.getFrame().getOrigin().getY(), mainView.getFrame().getSize()
                .getWidth()
                - 2 * insetWidth, mainView.getFrame().getSize().getHeight()));
        horizontalTextSpace = getHorizontalTextSpaceForInsetWidth(insetWidth);
        setNeedsDisplay();
    }

    public PAPBaseTextCellDelegate getDelegate() {
        return delegate;
    }

    public void setDelegate(PAPBaseTextCellDelegate delegate) {
        this.delegate = delegate;
    }

    public void hideSeparator(boolean hide) {
        hideSeparator = hide;
    }
}
