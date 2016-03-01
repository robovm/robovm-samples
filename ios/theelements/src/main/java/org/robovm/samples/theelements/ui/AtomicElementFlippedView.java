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
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.uikit.NSAttributedStringAttributes;
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

    public AtomicElementFlippedView(CGRect frame) {
        super(frame);

        setAutoresizesSubviews(true);
        setupUserInterface();

        // set the background color of the view to clear
        setBackgroundColor(UIColor.clear());
    }

    private void setupUserInterface() {
        CGRect buttonFrame = new CGRect(10, 209, 234, 37);

        // create the button
        wikipediaButton = new UIButton(UIButtonType.RoundedRect);
        wikipediaButton.setFrame(buttonFrame);

        wikipediaButton.setTitle("View at Wikipedia", UIControlState.Normal);

        // Center the text on the button, considering the button's shadow
        wikipediaButton.setContentHorizontalAlignment(UIControlContentHorizontalAlignment.Center);
        wikipediaButton.setContentVerticalAlignment(UIControlContentVerticalAlignment.Center);

        wikipediaButton.addOnTouchUpInsideListener(new UIControl.OnTouchUpInsideListener() {
            @Override
            public void onTouchUpInside(UIControl control, UIEvent event) {
                // create the string that points to the correct Wikipedia page
                // for the element name
                String wikiPageString = String.format("http://en.wikipedia.org/wiki/%s", element.getName());
                if (!UIApplication.getSharedApplication().openURL(new NSURL(wikiPageString))) {
                    // there was an error trying to open the URL. for the moment
                    // we'll simply ignore it.
                }
            }
        });
        addSubview(wikipediaButton);
    }

    @Override
    public void draw(CGRect rect) {
        // get the background image for the state of the element
        // position it appropriately and draw the image
        UIImage backgroundImage = element.getStateImageForAtomicElementView();
        CGRect elementSymbolRectangle = new CGRect(0, 0, backgroundImage.getSize().getWidth(), backgroundImage
                .getSize()
                .getHeight());
        backgroundImage.draw(elementSymbolRectangle);

        // all the text is drawn in white
        UIColor.white().setFillAndStroke();

        NSAttributedStringAttributes attr = new NSAttributedStringAttributes().setForegroundColor(UIColor.white());
        attr.setFont(UIFont.getBoldSystemFont(32));

        // draw the element number
        CGPoint point = new CGPoint(10, 5);
        NSString.draw(String.valueOf(element.getAtomicNumber()), point, attr);

        // draw the element symbol
        CGSize stringSize = NSString.getSize(element.getSymbol(), attr);
        point = new CGPoint(getBounds().getSize().getWidth() - stringSize.getWidth() - 10, 5);
        NSString.draw(element.getSymbol(), point, attr);

        // draw the element name
        attr.setFont(UIFont.getBoldSystemFont(36));
        stringSize = NSString.getSize(element.getName(), attr);
        point = new CGPoint((getBounds().getSize().getWidth() - stringSize.getWidth()) / 2, 50);
        NSString.draw(element.getName(), point, attr);

        float verticalStartingPoint = 95;

        // draw the element weight
        attr.setFont(UIFont.getBoldSystemFont(14));
        point = new CGPoint((getBounds().getSize().getWidth() - stringSize.getWidth()) / 2, verticalStartingPoint);
        NSString.draw(String.format("Atomic Weight: %s", element.getAtomicWeight()), point, attr);

        // draw the element state
        attr.setFont(UIFont.getBoldSystemFont(14));
        String stateString = String.format("State: %s", element.getState());
        stringSize = NSString.getSize(stateString, attr);
        point = new CGPoint((getBounds().getSize().getWidth() - stringSize.getWidth()) / 2, verticalStartingPoint + 20);
        NSString.draw(stateString, point, attr);

        // draw the element period
        attr.setFont(UIFont.getBoldSystemFont(14));
        String periodString = String.format("Period: %d", element.getPeriod());
        stringSize = NSString.getSize(periodString, attr);
        point = new CGPoint((getBounds().getSize().getWidth() - stringSize.getWidth()) / 2, verticalStartingPoint + 40);
        NSString.draw(periodString, point, attr);

        // draw the element group
        attr.setFont(UIFont.getBoldSystemFont(14));
        String groupString = String.format("Group: %d", element.getGroup());
        stringSize = NSString.getSize(groupString, attr);
        point = new CGPoint((getBounds().getSize().getWidth() - stringSize.getWidth()) / 2, verticalStartingPoint + 60);
        NSString.draw(groupString, point, attr);

        // draw the discovery year
        String discoveryYearString = String.format("Discovered: %s", element.getDiscoveryYear());
        stringSize = NSString.getSize(discoveryYearString, attr);
        point = new CGPoint((getBounds().getSize().getWidth() - stringSize.getWidth()) / 2, verticalStartingPoint + 80);
        NSString.draw(discoveryYearString, point, attr);
    }
}
