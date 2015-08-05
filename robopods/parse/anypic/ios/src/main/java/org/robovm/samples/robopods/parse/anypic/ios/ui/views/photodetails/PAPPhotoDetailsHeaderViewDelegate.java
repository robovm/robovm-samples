package org.robovm.samples.robopods.parse.anypic.ios.ui.views.photodetails;

import org.robovm.apple.uikit.UIButton;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPUser;

public interface PAPPhotoDetailsHeaderViewDelegate {
    /**
     * Sent to the delegate when the photgrapher's name/avatar is tapped
     * 
     * @param button the tapped UIButton
     * @param user the PFUser for the photographer
     */
    void didTapUserButton(PAPPhotoDetailsHeaderView headerView, UIButton button, PAPUser user);
}
