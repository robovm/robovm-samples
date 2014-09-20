
package org.robovm.samples.streetscroller.viewcontrollers;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIInterfaceOrientation;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.samples.streetscroller.views.InfiniteScrollView;

public class StreetScrollerViewController extends UIViewController {
    private final InfiniteScrollView infiniteScrollView;

    public StreetScrollerViewController () {
        super();

        UIView view = getView();

        infiniteScrollView = new InfiniteScrollView(new CGRect(0, 0, 320, 460));
        infiniteScrollView.setBackgroundColor(UIColor.blue());
        view.addSubview(infiniteScrollView);
    }

    @Override
    public boolean shouldAutorotate (UIInterfaceOrientation toInterfaceOrientation) {
        return toInterfaceOrientation == UIInterfaceOrientation.Portrait;
    }
}
