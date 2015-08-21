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
package org.robovm.samples.robopods.parse.anypic.ios.ui.views.tablecells;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIButtonType;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UITableViewCellAccessoryType;
import org.robovm.apple.uikit.UITableViewCellSelectionStyle;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UIViewContentMode;
import org.robovm.pods.parse.ui.PFTableViewCell;

public class PAPPhotoCell extends PFTableViewCell {
    private final UIButton photoButton;

    public PAPPhotoCell(UITableViewCellStyle style, String reuseIdentifier) {
        super(style, reuseIdentifier);

        setOpaque(false);
        setSelectionStyle(UITableViewCellSelectionStyle.None);
        setAccessoryType(UITableViewCellAccessoryType.None);
        setClipsToBounds(false);

        setBackgroundColor(UIColor.clear());

        getImageView().setFrame(new CGRect(0, 0, getBounds().getWidth(), getBounds().getWidth()));
        getImageView().setBackgroundColor(UIColor.black());
        getImageView().setContentMode(UIViewContentMode.ScaleAspectFit);

        photoButton = UIButton.create(UIButtonType.Custom);
        photoButton.setFrame(new CGRect(0, 0, getBounds().getWidth(), getBounds().getWidth()));
        photoButton.setBackgroundColor(UIColor.clear());
        getContentView().addSubview(photoButton);

        getContentView().bringSubviewToFront(getImageView());
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        getImageView().setFrame(new CGRect(0, 0, getBounds().getWidth(), getBounds().getWidth()));
        photoButton.setFrame(new CGRect(0, 0, getBounds().getWidth(), getBounds().getWidth()));
    }

    public UIButton getPhotoButton() {
        return photoButton;
    }
}
