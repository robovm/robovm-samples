package org.robovm.samples.robopods.parse.anypic.ios.ui.views.tablecells;

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
import org.robovm.apple.uikit.UITableViewCellSelectionStyle;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.pods.parse.ui.PFTableViewCell;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPUser;
import org.robovm.samples.robopods.parse.anypic.ios.ui.views.PAPProfileImageView;
import org.robovm.samples.robopods.parse.anypic.ios.util.PAPUtility;

public class PAPFindFriendsCell extends PFTableViewCell {
    private PAPFindFriendsCellDelegate delegate;

    private PAPUser user;
    private final UILabel photoLabel;
    private final UIButton followButton;

    private final UIButton nameButton;
    private final UIButton avatarImageButton;
    private final PAPProfileImageView avatarImageView;

    public PAPFindFriendsCell(UITableViewCellStyle style, String reuseIdentifier) {
        super(style, reuseIdentifier);

        setBackgroundColor(UIColor.black());
        setSelectionStyle(UITableViewCellSelectionStyle.None);

        avatarImageView = new PAPProfileImageView(new CGRect(10, 14, 40, 40));
        avatarImageView.getLayer().setCornerRadius(20);
        avatarImageView.getLayer().setMasksToBounds(true);
        getContentView().addSubview(avatarImageView);

        avatarImageButton = UIButton.create(UIButtonType.Custom);
        avatarImageButton.setBackgroundColor(UIColor.clear());
        avatarImageButton.setFrame(new CGRect(10, 14, 40, 40));
        avatarImageButton.addOnTouchUpInsideListener(didTapUserButton);
        getContentView().addSubview(avatarImageButton);

        nameButton = UIButton.create(UIButtonType.Custom);
        nameButton.setBackgroundColor(UIColor.clear());
        nameButton.getTitleLabel().setFont(UIFont.getBoldSystemFont(16));
        nameButton.getTitleLabel().setLineBreakMode(NSLineBreakMode.TruncatingTail);
        nameButton.setTitleColor(UIColor.white(), UIControlState.Normal);
        nameButton
                .setTitleColor(UIColor.fromRGBA(114f / 255f, 114f / 255f, 114f / 255f, 1), UIControlState.Highlighted);
        nameButton.addOnTouchUpInsideListener(didTapUserButton);
        getContentView().addSubview(nameButton);

        photoLabel = new UILabel();
        photoLabel.setFont(UIFont.getSystemFont(11));
        photoLabel.setTextColor(UIColor.gray());
        photoLabel.setBackgroundColor(UIColor.clear());
        getContentView().addSubview(photoLabel);

        followButton = UIButton.create(UIButtonType.Custom);
        followButton.getTitleLabel().setFont(UIFont.getBoldSystemFont(15));
        followButton.setTitleEdgeInsets(new UIEdgeInsets(0, 10, 0, 0));
        followButton.setBackgroundImage(UIImage.create("ButtonFollow"), UIControlState.Normal);
        followButton.setBackgroundImage(UIImage.create("ButtonFollowing"), UIControlState.Selected);
        followButton.setImage(UIImage.create("IconTick"), UIControlState.Selected);
        followButton.setTitle("Follow  ", UIControlState.Normal);
        followButton.setTitle("Following", UIControlState.Selected);
        followButton.setTitleColor(UIColor.fromRGBA(254f / 255f, 149f / 255f, 50f / 255f, 1), UIControlState.Normal);
        followButton.setTitleColor(UIColor.white(), UIControlState.Selected);
        followButton.addOnTouchUpInsideListener(didTapFollowButton);
        getContentView().addSubview(followButton);
    }

    public void setUser(PAPUser user) {
        this.user = user;

        // Configure the cell
        if (PAPUtility.userHasProfilePictures(user)) {
            avatarImageView.setFile(user.getProfilePicSmall());
        } else {
            avatarImageView.setImage(PAPUtility.getDefaultProfilePicture());
        }

        // Set name
        String nameString = user.getDisplayName();
        CGSize nameSize = NSString.getBoundingRect(nameString, new CGSize(144, Float.MAX_VALUE),
                NSStringDrawingOptions.with(NSStringDrawingOptions.TruncatesLastVisibleLine,
                        NSStringDrawingOptions.UsesLineFragmentOrigin),
                new NSAttributedStringAttributes().setFont(UIFont.getBoldSystemFont(16)), null).getSize();
        nameButton.setTitle(nameString, UIControlState.Normal);
        nameButton.setTitle(nameString, UIControlState.Highlighted);

        nameButton.setFrame(new CGRect(60, 17, nameSize.getWidth(), nameSize.getHeight()));

        // Set photo number label
        CGSize photoLabelSize = NSString.getBoundingRect("photos", new CGSize(144, Float.MAX_VALUE),
                NSStringDrawingOptions.with(NSStringDrawingOptions.TruncatesLastVisibleLine,
                        NSStringDrawingOptions.UsesLineFragmentOrigin),
                new NSAttributedStringAttributes().setFont(UIFont.getSystemFont(11)), null).getSize();
        photoLabel.setFrame(new CGRect(60, 17 + nameSize.getHeight(), 140, photoLabelSize.getHeight()));

        // Set follow button
        followButton.setFrame(new CGRect(208, 20, 103, 32));
    }

    public static double getHeightForCell() {
        return 67;
    }

    public void setDelegate(PAPFindFriendsCellDelegate delegate) {
        this.delegate = delegate;
    }

    public UILabel getPhotoLabel() {
        return photoLabel;
    }

    public UIButton getFollowButton() {
        return followButton;
    }

    public PAPUser getUser() {
        return user;
    }

    private final UIControl.OnTouchUpInsideListener didTapUserButton = new UIControl.OnTouchUpInsideListener() {
        @Override
        public void onTouchUpInside(UIControl control, UIEvent event) {
            if (delegate != null) {
                delegate.didTapUserButton(PAPFindFriendsCell.this, user);
            }
        }
    };

    private final UIControl.OnTouchUpInsideListener didTapFollowButton = new UIControl.OnTouchUpInsideListener() {
        @Override
        public void onTouchUpInside(UIControl control, UIEvent event) {
            if (delegate != null) {
                delegate.didTapFollowButton(PAPFindFriendsCell.this, user);
            }
        }
    };
}
