package org.robovm.samples.robopods.parse.anypic.ios.ui.views.timeline;

import org.robovm.apple.uikit.UIButton;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPPhoto;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPUser;

public interface PAPPhotoHeaderViewDelegate {
    /**
     * Sent to the delegate when the user button is tapped
     * 
     * @param user the PFUser associated with this button
     */
    void didTapUserButton(PAPPhotoHeaderView photoHeaderView, UIButton button, PAPUser user);

    /**
     * Sent to the delegate when the like photo button is tapped
     * 
     * @param photo the PFObject for the photo that is being liked or disliked
     */
    void didTapLikePhotoButton(PAPPhotoHeaderView photoHeaderView, UIButton button, PAPPhoto photo);

    /**
     * Sent to the delegate when the comment on photo button is tapped
     * 
     * @param photo the PFObject for the photo that will be commented on
     */
    void didTapCommentOnPhotoButton(PAPPhotoHeaderView photoHeaderView, UIButton button, PAPPhoto photo);
}
