package org.robovm.samples.robopods.parse.anypic.ios.ui.views.tablecells;

import org.robovm.samples.robopods.parse.anypic.ios.model.PAPActivity;

public interface PAPActivityCellDelegate extends PAPBaseTextCellDelegate {
    void didTapActivityButton(PAPActivityCell cellView, PAPActivity activity);
}
