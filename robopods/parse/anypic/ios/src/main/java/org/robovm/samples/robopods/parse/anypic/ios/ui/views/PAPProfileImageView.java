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
package org.robovm.samples.robopods.parse.anypic.ios.ui.views;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIButtonType;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIView;
import org.robovm.pods.parse.PFFile;
import org.robovm.pods.parse.ui.PFImageView;

public class PAPProfileImageView extends UIView {
    private UIButton profileButton;
    private PFImageView profileImageView;
    private UIImageView borderImageview;

    public PAPProfileImageView() {
        setup(new CGRect());
    }

    public PAPProfileImageView(CGRect frame) {
        super(frame);
        setup(frame);
    }

    private void setup(CGRect frame) {
        setBackgroundColor(UIColor.clear());

        profileImageView = new PFImageView(frame);
        addSubview(profileImageView);

        profileButton = UIButton.create(UIButtonType.Custom);
        addSubview(profileButton);

        addSubview(borderImageview);
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        bringSubviewToFront(borderImageview);

        CGRect frame = new CGRect(0, 0, getFrame().getSize().getWidth(), getFrame().getSize().getHeight());
        profileImageView.setFrame(frame);
    }

    public void setFile(PFFile file) {
        if (file == null) {
            return;
        }

        profileImageView.setImage(UIImage.create("AvatarPlaceholder"));
        profileImageView.setFile(file);
        profileImageView.loadInBackground();
    }

    public void setImage(UIImage image) {
        profileImageView.setImage(image);
    }

    public UIButton getProfileButton() {
        return profileButton;
    }
}
