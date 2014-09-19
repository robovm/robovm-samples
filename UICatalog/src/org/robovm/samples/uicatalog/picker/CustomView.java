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
 * Portions of this code is based on Apple Inc's UICatalog sample (v2.11)
 * which is copyright (C) 2008-2013 Apple Inc.
 */

package org.robovm.samples.uicatalog.picker;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIView;

/** The custom view holding the image and title for the custom picker. */
public class CustomView extends UIView {

    private static final float VIEW_WIDTH = 200.0f;
    private static final float VIEW_HEIGHT = 44.0f;
    private static final double LABEL_HEIGHT = 20;
    private static final double MARGIN_SIZE = 10;

    private final UILabel titleLabel;

    public static double getViewWidth () {
        return VIEW_WIDTH;
    }

    public static double getViewHeight () {
        return VIEW_HEIGHT;
    }

    public CustomView (String title, UIImage image) {
        super(new CGRect(0.0, 0.0, VIEW_WIDTH, VIEW_HEIGHT));

        double yCoord = (getBounds().size().height() - LABEL_HEIGHT) / 2;
        titleLabel = new UILabel(new CGRect(MARGIN_SIZE + image.getSize().width() + MARGIN_SIZE, yCoord, (getFrame().getWidth())
            - MARGIN_SIZE + image.getSize().width() + MARGIN_SIZE, LABEL_HEIGHT));

        titleLabel.setText(title.toString());
        titleLabel.setBackgroundColor(UIColor.clear());
        addSubview(titleLabel);

        yCoord = (getBounds().size().height() - image.getSize().height()) / 2;
        UIImageView imageView = new UIImageView(
            new CGRect(MARGIN_SIZE, yCoord, image.getSize().width(), image.getSize().height()));

        imageView.setImage(image);
        addSubview(imageView);
    }

    /** Enable accessibility for this view.
     * 
     * @return */
    boolean isAccessibilityElement () {
        return true;
    }

    /** Return a string that describes this view.
     * 
     * @return */
    String accessibilityLabel () {
        return titleLabel.getText();
    }

}
