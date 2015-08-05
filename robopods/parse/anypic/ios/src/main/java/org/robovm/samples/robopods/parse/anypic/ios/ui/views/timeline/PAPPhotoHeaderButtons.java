/*
 * Copyright (C) 2013-2015 RoboVM AB
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 * 
 * Portions of this code is based on Parse's AnyPic sample
 * which is copyright (C) 2013 Parse.
 */
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
