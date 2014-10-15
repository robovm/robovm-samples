/*
 * Copyright (C) 2014 Trillian Mobile AB
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
 * Portions of this code is based on Apple Inc's MoveMe sample (v3.0)
 * which is copyright (C) 2008-2013 Apple Inc.
 */

package org.robovm.samples.moveme.views;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIView;

public class APLPlacardView extends UIView {
    private final UIImageView background;
    private final UILabel textLabel;

    public APLPlacardView () {
        setFrame(new CGRect(43, 131, 228, 98));

        background = new UIImageView(UIImage.create("Placard.png"));
        background.setFrame(new CGRect(0, 0, 228, 98));
        addSubview(background);

        textLabel = new UILabel(new CGRect(20, 38, 188, 22));
        textLabel.setText("PlacardView");
        textLabel.setFont(UIFont.getSystemFont(17));
        textLabel.setTextColor(UIColor.darkText());
        textLabel.setShadowColor(UIColor.lightText());
        textLabel.setShadowOffset(new CGSize(1, 1));
        textLabel.setTextAlignment(NSTextAlignment.Center);
        addSubview(textLabel);
    }

    public void setDisplayString (String displayString) {
        textLabel.setText(displayString);
    }

}
