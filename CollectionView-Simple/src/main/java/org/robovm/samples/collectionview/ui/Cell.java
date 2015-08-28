package org.robovm.samples.collectionview.ui;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UICollectionViewCell;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UILabel;

public class Cell extends UICollectionViewCell {
    private UIImageView image;
    private UILabel label;

    @Override
    protected long init(CGRect frame) {
        long res = super.init(frame);
        // change to our custom selected background view
        CustomCellBackground backgroundView = new CustomCellBackground(CGRect.Zero());
        setSelectedBackgroundView(backgroundView);

        image = new UIImageView(new CGRect(5, 6, 144, 105));
        addSubview(image);
        label = new UILabel(new CGRect(0, 109, 153, 18));
        label.setFont(UIFont.getSystemFont(12));
        label.setTextColor(UIColor.white());
        label.setTextAlignment(NSTextAlignment.Center);
        addSubview(label);

        return res;
    }

    public UIImageView getImage() {
        return image;
    }

    public UILabel getLabel() {
        return label;
    }
}
