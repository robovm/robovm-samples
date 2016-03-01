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
 * Portions of this code is based on Apple Inc's TheElements sample (v1.12)
 * which is copyright (C) 2008-2013 Apple Inc.
 */

package org.robovm.samples.theelements.ui;

import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.NSAttributedStringAttributes;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIView;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.samples.theelements.model.AtomicElement;

@CustomClass("AtomicElementTileView")
public class AtomicElementTileView extends UIView {
    private AtomicElement element;

    @Override
    public void draw(CGRect rect) {
        if (element == null)
            return;
        // get the image that represents the element physical state and draw it
        UIImage backgroundImage = element.getStateImageForAtomicElementTileView();
        CGRect elementSymbolRectangle = new CGRect(0, 0, backgroundImage.getSize().getWidth(), backgroundImage
                .getSize()
                .getHeight());
        backgroundImage.draw(elementSymbolRectangle);

        // draw the element number
        NSAttributedStringAttributes attrs = new NSAttributedStringAttributes().setForegroundColor(UIColor.white());
        attrs.setFont(UIFont.getBoldSystemFont(11));
        CGPoint point = new CGPoint(3, 2);
        NSString.draw(String.valueOf(element.getAtomicNumber()), point, attrs);

        // draw the element symbol
        attrs.setFont(UIFont.getBoldSystemFont(18));
        CGSize stringSize = NSString.getSize(element.getSymbol(), attrs);
        point = new CGPoint((elementSymbolRectangle.getSize().getWidth() - stringSize.getWidth()) / 2, 14);
        NSString.draw(element.getSymbol(), point, attrs);
    }

    public void setElement(AtomicElement element) {
        this.element = element;
    }

    public AtomicElement getElement() {
        return element;
    }
}
