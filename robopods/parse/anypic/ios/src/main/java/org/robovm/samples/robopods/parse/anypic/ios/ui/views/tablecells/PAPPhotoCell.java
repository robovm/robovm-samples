package org.robovm.samples.robopods.parse.anypic.ios.ui.views.tablecells;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIButtonType;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UITableViewCellAccessoryType;
import org.robovm.apple.uikit.UITableViewCellSelectionStyle;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UIViewContentMode;
import org.robovm.pods.parse.ui.PFTableViewCell;

public class PAPPhotoCell extends PFTableViewCell {
    private final UIButton photoButton;

    public PAPPhotoCell(UITableViewCellStyle style, String reuseIdentifier) {
        super(style, reuseIdentifier);

        setOpaque(false);
        setSelectionStyle(UITableViewCellSelectionStyle.None);
        setAccessoryType(UITableViewCellAccessoryType.None);
        setClipsToBounds(false);

        setBackgroundColor(UIColor.clear());

        getImageView().setFrame(new CGRect(0, 0, getBounds().getSize().getWidth(), getBounds().getSize().getWidth()));
        getImageView().setBackgroundColor(UIColor.black());
        getImageView().setContentMode(UIViewContentMode.ScaleAspectFit);

        photoButton = UIButton.create(UIButtonType.Custom);
        photoButton.setFrame(new CGRect(0, 0, getBounds().getSize().getWidth(), getBounds().getSize().getWidth()));
        photoButton.setBackgroundColor(UIColor.clear());
        getContentView().addSubview(photoButton);

        getContentView().bringSubviewToFront(getImageView());
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        getImageView().setFrame(new CGRect(0, 0, getBounds().getSize().getWidth(), getBounds().getSize().getWidth()));
        photoButton.setFrame(new CGRect(0, 0, getBounds().getSize().getWidth(), getBounds().getSize().getWidth()));
    }

    public UIButton getPhotoButton() {
        return photoButton;
    }
}
