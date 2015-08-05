package org.robovm.samples.robopods.parse.anypic.ios.ui.views.tablecells;

import org.robovm.samples.robopods.parse.anypic.ios.model.PAPUser;

public interface PAPFindFriendsCellDelegate {
    void didTapUserButton(PAPFindFriendsCell cellView, PAPUser user);

    void didTapFollowButton(PAPFindFriendsCell cellView, PAPUser user);
}
