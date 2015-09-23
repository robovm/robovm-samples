package org.robovm.samples.stackview;

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.uikit.*;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import org.robovm.objc.annotation.IBOutlet;

@CustomClass("MyViewController")
public class MyViewController extends UIViewController {
    private int rating;
    private UIStackView ratingsStack;

    @IBOutlet
    public void setRatingsStack(UIStackView ratingsStack) {
        this.ratingsStack = ratingsStack;
    }

    @IBAction
    private void increaseRating() {
        if (++this.rating > 5 ) {
            this.rating = 5;
            return;
        }

        UIImageView icon = new UIImageView (UIImage.getImage("icon.png"));
        icon.setContentMode(UIViewContentMode.ScaleAspectFit);
        this.ratingsStack.addArrangedSubview(icon);

        UIView.animate(0.25, () -> this.ratingsStack.layoutIfNeeded());
    }

    @IBAction
    private void decreaseRating() {
        if (--this.rating < 0) {
            this.rating = 0;
            return;
        }

        NSArray<UIView> icons = this.ratingsStack.getArrangedSubviews();
        UIView lastIcon = icons.get(icons.size()-1);
        this.ratingsStack.removeArrangedSubview(lastIcon);
        lastIcon.removeFromSuperview();

        UIView.animate(0.25, () -> this.ratingsStack.layoutIfNeeded());
    }
}
