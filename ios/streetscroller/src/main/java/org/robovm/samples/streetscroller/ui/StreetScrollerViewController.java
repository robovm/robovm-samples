package org.robovm.samples.streetscroller.ui;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;

public class StreetScrollerViewController extends UIViewController {
    private final InfiniteScrollView infiniteScrollView;

    public StreetScrollerViewController() {
        UIView view = getView();

        infiniteScrollView = new InfiniteScrollView(new CGRect(0, 0, 320, 460));
        infiniteScrollView.setBackgroundColor(UIColor.blue());
        view.addSubview(infiniteScrollView);
    }
}
