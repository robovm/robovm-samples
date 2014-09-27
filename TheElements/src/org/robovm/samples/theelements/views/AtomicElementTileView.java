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
 * Portions of this code is based on Apple Inc's TheElements sample (v1.12)
 * which is copyright (C) 2008-2013 Apple Inc.
 */

package org.robovm.samples.theelements.views;

import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIView;
import org.robovm.samples.theelements.model.AtomicElement;

public class AtomicElementTileView extends UIView {
    private AtomicElement element;

    public AtomicElementTileView (CGRect rect) {
        super(rect);
        setBackgroundColor(UIColor.white());
    }

    @Override
    public void draw (CGRect rect) {
        if (element == null) return;
        // get the image that represents the element physical state and draw it
        UIImage backgroundImage = element.getStateImageForAtomicElementTileView();
        CGRect elementSymbolRectangle = new CGRect(0, 0, backgroundImage.getSize().width(), backgroundImage.getSize().height());
        backgroundImage.draw(elementSymbolRectangle);

        UIColor.white().setFillAndStroke();
        // draw the element number
        UIFont font = UIFont.getBoldSystemFont(11);
        CGPoint point = new CGPoint(3, 2);
        new NSString(String.valueOf(element.getAtomicNumber())).draw(point, font);
        // draw the element symbol
        font = UIFont.getBoldSystemFont(18);
        CGSize stringSize = new NSString(element.getSymbol()).getSize(font);
        point = new CGPoint((elementSymbolRectangle.size().width() - stringSize.width()) / 2, 14);
        new NSString(element.getSymbol()).draw(point, font);
    }

    public void setElement (AtomicElement element) {
        this.element = element;
    }
}
