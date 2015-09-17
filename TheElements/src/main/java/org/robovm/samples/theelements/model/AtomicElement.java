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

package org.robovm.samples.theelements.model;

import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.NSAttributedStringAttributes;
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

    public AtomicElement() {}

    public AtomicElement(NSDictionary<NSString, NSObject> data) {
        atomicNumber = data.getInt("atomicNumber");
        atomicWeight = data.getString("atomicWeight");
        discoveryYear = data.getString("discoveryYear");
        radioactive = Boolean.valueOf(data.getString("radioactive"));
        name = data.getString("name");
        symbol = data.getString("symbol");
        state = data.getString("state");
        group = data.getInt("group");
        period = data.getInt("period");
        vertPos = data.getInt("vertPos");
        horizPos = data.getInt("horizPos");
    }

    /**
     * @return the position of the element in the classic periodic table
     *         locations.
     */
    public CGPoint getElementPosition() {
        return new CGPoint(horizPos * 26 - 8, vertPos * 26 + 35);
    }

    public UIImage getStateImageForAtomicElementTileView() {
        return UIImage.getImage(String.format("%s_37.png", state));
    }

    public UIImage getStateImageForAtomicElementView() {
        return UIImage.getImage(String.format("%s_256.png", state));
    }

    public UIImage getStateImageForPeriodicTableView() {
        return UIImage.getImage(String.format("%s_24.png", state));
    }

    /**
     * @return a 30 x 30 image that is a reduced version of the
     *         AtomicElementTileView content this is used to display the flipper
     *         button in the navigation bar.
     */
    public UIImage getFlipperImageForAtomicElementNavigationItem() {
        CGSize itemSize = new CGSize(30, 30);
        UIGraphics.beginImageContext(itemSize);

        UIImage backgroundImage = UIImage.getImage(String.format("%s_30.png", state));
        CGRect elementSymbolRectangle = new CGRect(0, 0, itemSize.getWidth(), itemSize.getHeight());
        backgroundImage.draw(elementSymbolRectangle);

        // draw the element number
        NSAttributedStringAttributes attrs = new NSAttributedStringAttributes().setForegroundColor(UIColor.white());
        attrs.setFont(UIFont.getBoldSystemFont(8));

        CGPoint point = new CGPoint(2, 1);
        NSString.draw(String.valueOf(atomicNumber), point, attrs);

        // draw the element symbol
        attrs.setFont(UIFont.getBoldSystemFont(13));
        CGSize stringSize = NSString.getSize(symbol, attrs);
        point = new CGPoint((elementSymbolRectangle.getSize().getWidth() - stringSize.getWidth()) / 2, 10);

        NSString.draw(symbol, point, attrs);

        UIImage theImage = UIGraphics.getImageFromCurrentImageContext();
        UIGraphics.endImageContext();
        return theImage;
    }

    public int getAtomicNumber() {
        return atomicNumber;
    }

    public String getName() {
        return name.toString();
    }

    public String getSymbol() {
        return symbol.toString();
    }

    public String getState() {
        return state;
    }

    public String getAtomicWeight() {
        return atomicWeight;
    }

    public int getGroup() {
        return group;
    }

    public int getPeriod() {
        return period;
    }

    public String getDiscoveryYear() {
        return discoveryYear;
    }

    public boolean isRadioactive() {
        return radioactive;
    }
}
