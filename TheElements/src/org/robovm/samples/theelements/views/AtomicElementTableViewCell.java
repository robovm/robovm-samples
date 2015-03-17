/*
 * Copyright (C) 2014 RoboVM AB
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
 * Portions of this code is based on Apple Inc's TheElements sample (v1.12)
 * which is copyright (C) 2008-2013 Apple Inc.
 */

package org.robovm.samples.theelements.views;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellAccessoryType;
import org.robovm.apple.uikit.UITableViewCellSelectionStyle;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UIView;
import org.robovm.samples.theelements.model.AtomicElement;

public class AtomicElementTableViewCell extends UITableViewCell {
    private AtomicElement element;
    private final AtomicElementTileView elementTileView;
    private final UILabel labelView;

    public AtomicElementTableViewCell () {
        super(UITableViewCellStyle.Default, "AtomicElementTableViewCell");

        setSelectionStyle(UITableViewCellSelectionStyle.Blue);
        setAccessoryType(UITableViewCellAccessoryType.DisclosureIndicator);

        UIView view = getContentView();

        elementTileView = new AtomicElementTileView(new CGRect(10, 3, 41, 38));
        view.addSubview(elementTileView);

        labelView = new UILabel(new CGRect(52, 11, 248, 22));
        labelView.setFont(UIFont.getBoldSystemFont(20));
        labelView.setTextColor(UIColor.darkText());
        view.addSubview(labelView);
    }

    public void setElement (AtomicElement element) {
        this.element = element;

        elementTileView.setElement(element);
        labelView.setText(element.getName());
        elementTileView.setNeedsDisplay();
        labelView.setNeedsDisplay();
    }

    public AtomicElement getElement () {
        return element;
    }
}
