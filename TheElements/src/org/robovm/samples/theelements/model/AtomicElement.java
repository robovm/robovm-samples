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

package org.robovm.samples.theelements.model;

import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSNumber;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIGraphics;
import org.robovm.apple.uikit.UIImage;

public class AtomicElement extends NSObject {
    private int atomicNumber;
    private String name;
    private String symbol;
    private String state;
    private String atomicWeight;
    private int group;
    private int period;
    private String discoveryYear;

    private int vertPos;
    private int horizPos;
    private boolean radioactive;

    public AtomicElement () {
    }

    public AtomicElement (NSDictionary<NSString, NSObject> data) {
        atomicNumber = ((NSNumber)data.get(key("atomicNumber"))).intValue();
        atomicWeight = ((NSString)data.get(key("atomicWeight"))).toString();
        discoveryYear = ((NSString)data.get(key("discoveryYear"))).toString();
        radioactive = Boolean.valueOf(((NSString)data.get(key("radioactive"))).toString());
        name = ((NSString)data.get(key("name"))).toString();
        symbol = ((NSString)data.get(key("symbol"))).toString();
        state = ((NSString)data.get(key("state"))).toString();
        group = ((NSNumber)data.get(key("group"))).intValue();
        period = ((NSNumber)data.get(key("period"))).intValue();
        vertPos = ((NSNumber)data.get(key("vertPos"))).intValue();
        horizPos = ((NSNumber)data.get(key("horizPos"))).intValue();
    }

    private static NSString key (String key) {
        return new NSString(key);
    }

    /** @return the position of the element in the classic periodic table locations. */
    public CGPoint getElementPosition () {
        return new CGPoint(horizPos * 26 - 8, vertPos * 26 + 35);
    }

    public UIImage getStateImageForAtomicElementTileView () {
        return UIImage.create(String.format("%s_37.png", state));
    }

    public UIImage getStateImageForAtomicElementView () {
        return UIImage.create(String.format("%s_256.png", state));
    }

    public UIImage getStateImageForPeriodicTableView () {
        return UIImage.create(String.format("%s_24.png", state));
    }

    /** @return a 30 x 30 image that is a reduced version of the AtomicElementTileView content this is used to display the flipper
     *         button in the navigation bar. */
    public UIImage getFlipperImageForAtomicElementNavigationItem () {
        CGSize itemSize = new CGSize(30, 30);
        UIGraphics.beginImageContext(itemSize);

        UIImage backgroundImage = UIImage.create(String.format("%s_30.png", state));
        CGRect elementSymbolRectangle = new CGRect(0, 0, itemSize.getWidth(), itemSize.getHeight());
        backgroundImage.draw(elementSymbolRectangle);

        // draw the element name
        UIColor.white().setFillAndStroke();

        // draw the element number
        UIFont font = UIFont.getBoldSystemFont(8);
        CGPoint point = new CGPoint(2, 1);
        new NSString(String.valueOf(atomicNumber)).draw(point, font);

        // draw the element symbol
        font = UIFont.getBoldSystemFont(13);
        NSString symbolString = new NSString(symbol);
        CGSize stringSize = symbolString.getSize(font);
        point = new CGPoint((elementSymbolRectangle.getSize().getWidth() - stringSize.getWidth()) / 2, 10);

        symbolString.draw(point, font);

        UIImage theImage = UIGraphics.getImageFromCurrentImageContext();
        UIGraphics.endImageContext();
        return theImage;
    }

    public int getAtomicNumber () {
        return atomicNumber;
    }

    public String getName () {
        return name.toString();
    }

    public String getSymbol () {
        return symbol.toString();
    }

    public String getState () {
        return state;
    }

    public String getAtomicWeight () {
        return atomicWeight;
    }

    public int getGroup () {
        return group;
    }

    public int getPeriod () {
        return period;
    }

    public String getDiscoveryYear () {
        return discoveryYear;
    }
}
