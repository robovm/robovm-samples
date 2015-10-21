package org.robovm.samples.collectionview.ui;

import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIViewContentMode;
import org.robovm.apple.uikit.UIViewController;

public class DetailViewController extends UIViewController {
    private UIImage image;
    private final UIImageView imageView;

    public DetailViewController() {
        getView().setBackgroundColor(UIColor.black());

        imageView = new UIImageView();
        imageView.setContentMode(UIViewContentMode.ScaleAspectFit);
        imageView.setFrame(getView().getFrame());
        getView().addSubview(imageView);
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);
        imageView.setImage(image);
    }

    public void setImage(UIImage image) {
        this.image = image;
    }
}
