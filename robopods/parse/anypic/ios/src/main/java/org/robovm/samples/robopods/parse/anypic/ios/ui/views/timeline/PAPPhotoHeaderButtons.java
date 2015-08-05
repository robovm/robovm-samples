package org.robovm.samples.robopods.parse.anypic.ios.ui.views.timeline;

import java.util.EnumSet;

public class PAPPhotoHeaderButtons {
    private final EnumSet<PAPPhotoHeaderButton> buttons;

    public static PAPPhotoHeaderButtons getDefault() {
        return new PAPPhotoHeaderButtons(PAPPhotoHeaderButton.Like, PAPPhotoHeaderButton.Comment,
                PAPPhotoHeaderButton.User);
    }

    public PAPPhotoHeaderButtons() {
        buttons = EnumSet.of(PAPPhotoHeaderButton.None);
    }

    public PAPPhotoHeaderButtons(PAPPhotoHeaderButton b1) {
        buttons = EnumSet.of(b1);
    }

    public PAPPhotoHeaderButtons(PAPPhotoHeaderButton b1, PAPPhotoHeaderButton b2) {
        buttons = EnumSet.of(b1, b2);
    }

    public PAPPhotoHeaderButtons(PAPPhotoHeaderButton b1, PAPPhotoHeaderButton b2, PAPPhotoHeaderButton b3) {
        buttons = EnumSet.of(b1, b2, b3);
    }

    public boolean contains(PAPPhotoHeaderButton button) {
        return buttons.contains(button);
    }

    public enum PAPPhotoHeaderButton {
        None, Like, Comment, User
    }
}
