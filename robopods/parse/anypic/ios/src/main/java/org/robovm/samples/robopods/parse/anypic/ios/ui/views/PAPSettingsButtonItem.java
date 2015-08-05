package org.robovm.samples.robopods.parse.anypic.ios.ui.views;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIButtonType;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIImage;

public class PAPSettingsButtonItem extends UIBarButtonItem {
    public PAPSettingsButtonItem(UIControl.OnTouchUpInsideListener listener) {
        UIButton settingsButton = UIButton.create(UIButtonType.Custom);
        setCustomView(settingsButton);

        settingsButton.addOnTouchUpInsideListener(listener);
        settingsButton.setFrame(new CGRect(0, 0, 35, 32));
        settingsButton.setImage(UIImage.create("ButtonImageSettings"), UIControlState.Normal);
        settingsButton.setImage(UIImage.create("ButtonImageSettingsSelected"), UIControlState.Highlighted);
    }
}
