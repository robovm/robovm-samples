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
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIButtonType;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControlContentHorizontalAlignment;
import org.robovm.apple.uikit.UIControlContentVerticalAlignment;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIImage;

public class AtomicElementFlippedView extends AtomicElementView {
    private UIButton wikipediaButton;

    public AtomicElementFlippedView (CGRect frame) {
        super(frame);

        setAutoresizesSubviews(true);
        setupUserInterface();

        // set the background color of the view to clear
        setBackgroundColor(UIColor.clear());
    }

    private void setupUserInterface () {
        CGRect buttonFrame = new CGRect(10, 209, 234, 37);

        // create the button
        wikipediaButton = UIButton.create(UIButtonType.RoundedRect);
        wikipediaButton.setFrame(buttonFrame);

        wikipediaButton.setTitle("View at Wikipedia", UIControlState.Normal);

        // Center the text on the button, considering the button's shadow
        wikipediaButton.setContentHorizontalAlignment(UIControlContentHorizontalAlignment.Center);
        wikipediaButton.setContentVerticalAlignment(UIControlContentVerticalAlignment.Center);

        wikipediaButton.addOnTouchUpInsideListener(new UIControl.OnTouchUpInsideListener() {
            @Override
            public void onTouchUpInside (UIControl control, UIEvent event) {
                // create the string that points to the correct Wikipedia page for the element name
                String wikiPageString = String.format("http://en.wikipedia.org/wiki/%s", element.getName());
                if (!UIApplication.getSharedApplication().openURL(new NSURL(wikiPageString))) {
                    // there was an error trying to open the URL. for the moment we'll simply ignore it.
                }
            }
        });
        addSubview(wikipediaButton);
    }

    @Override
    public void draw (CGRect rect) {
        // get the background image for the state of the element
        // position it appropriately and draw the image
        UIImage backgroundImage = element.getStateImageForAtomicElementView();
        CGRect elementSymbolRectangle = new CGRect(0, 0, backgroundImage.getSize().getWidth(), backgroundImage.getSize()
            .getHeight());
        backgroundImage.draw(elementSymbolRectangle);

        // all the text is drawn in white
        UIColor.white().setFillAndStroke();

        // draw the element number
        UIFont font = UIFont.getBoldSystemFont(32);
        CGPoint point = new CGPoint(10, 5);
        NSString atomicNumberString = new NSString(String.valueOf(element.getAtomicNumber()));
        atomicNumberString.draw(point, font);

        // draw the element symbol
        NSString symbolString = new NSString(element.getSymbol());
        CGSize stringSize = symbolString.getSize(font);
        point = new CGPoint(getBounds().getSize().getWidth() - stringSize.getWidth() - 10, 5);
        symbolString.draw(point, font);

        // draw the element name
        font = UIFont.getBoldSystemFont(36);
        NSString nameString = new NSString(element.getName());
        stringSize = nameString.getSize(font);
        point = new CGPoint((getBounds().getSize().getWidth() - stringSize.getWidth()) / 2, 50);
        nameString.draw(point, font);

        float verticalStartingPoint = 95;

        // draw the element weight
        font = UIFont.getBoldSystemFont(14);
        NSString atomicWeightString = new NSString(String.format("Atomic Weight: %s", element.getAtomicWeight()));
        point = new CGPoint((getBounds().getSize().getWidth() - stringSize.getWidth()) / 2, verticalStartingPoint);
        atomicWeightString.draw(point, font);

        // draw the element state
        font = UIFont.getBoldSystemFont(14);
        NSString stateString = new NSString(String.format("State: %s", element.getState()));
        stringSize = stateString.getSize(font);
        point = new CGPoint((getBounds().getSize().getWidth() - stringSize.getWidth()) / 2, verticalStartingPoint + 20);
        stateString.draw(point, font);

        // draw the element period
        font = UIFont.getBoldSystemFont(14);
        NSString periodString = new NSString(String.format("Period: %d", element.getPeriod()));
        stringSize = periodString.getSize(font);
        point = new CGPoint((getBounds().getSize().getWidth() - stringSize.getWidth()) / 2, verticalStartingPoint + 40);
        periodString.draw(point, font);

        // draw the element group
        font = UIFont.getBoldSystemFont(14);
        NSString groupString = new NSString(String.format("Group: %d", element.getGroup()));
        stringSize = groupString.getSize(font);
        point = new CGPoint((getBounds().getSize().getWidth() - stringSize.getWidth()) / 2, verticalStartingPoint + 60);
        groupString.draw(point, font);

        // draw the discovery year
        NSString discoveryYearString = new NSString(String.format("Discovered: %s", element.getDiscoveryYear()));
        stringSize = discoveryYearString.getSize(font);
        point = new CGPoint((getBounds().getSize().getWidth() - stringSize.getWidth()) / 2, verticalStartingPoint + 80);
        discoveryYearString.draw(point, font);
    }
}
