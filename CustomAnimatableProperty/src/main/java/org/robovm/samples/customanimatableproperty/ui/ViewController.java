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
 * Portions of this code is based on Apple Inc's QuickContacts sample (v1.0)
 * which is copyright (C) 2008-2013 Apple Inc.
 * 
 * The view controller creates a few bulb views which host the custom layer subclass.
 */

package org.robovm.samples.customanimatableproperty.ui;

import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UIAlertView;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.samples.customanimateproperty.BulbView;

/**
 * The view controller creates a few blub views which host the custom layer
 * subclass.
 */
public class ViewController extends UIViewController {

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        this.getView().setFrame(new CGRect(0.0, 0.0, 320, 568));
        // Do any additional setup after loading the view, typically from a nib.

        this.getView().setBackgroundColor(UIColor.yellow());

        // Load the bulb image.
        UIImage bulb = UIImage.getImage("bulb.png");

        // Base the size of the bulb views on the screen size.
        double screenWidth = UIScreen.getMainScreen().getBounds().getSize().getWidth();
        double screenHeight = UIScreen.getMainScreen().getBounds().getSize().getHeight();
        double bulbHeight = screenHeight / 2.1;

        // Maintain image proportions by basing width on the width-to-height
        // ratio.
        double widthHeightRatio = bulb.getSize().getWidth() / bulb.getSize().getHeight();
        System.err.printf("widthHeightRatio: %f", widthHeightRatio);
        double bulbWidth = (bulbHeight * widthHeightRatio) * 1.5; // times 1.5
                                                                  // to fatten
                                                                  // up the bulb
                                                                  // for added
                                                                  // visual
                                                                  // effect.

        // Define our view hierarchy.
        BulbView bulbview;
        CGRect startingFrame = new CGRect(0, 0, bulbWidth, bulbHeight);
        bulbview = new BulbView(startingFrame);
        bulbview.setColor(UIColor.red());
        bulbview.setCenter(new CGPoint(.25 * screenWidth, .20 * screenHeight));
        this.getView().addSubview(bulbview);
        bulbview = new BulbView(startingFrame);
        bulbview.setColor(UIColor.green());
        bulbview.setCenter(new CGPoint(.5 * screenWidth, .5 * screenHeight));
        this.getView().addSubview(bulbview);
        bulbview = new BulbView(startingFrame);
        bulbview.setColor(UIColor.blue());
        bulbview.setCenter(new CGPoint(.75 * screenWidth, .80 * screenHeight));
        this.getView().addSubview(bulbview);

        // Display an alert view that explains usability.
        UIAlertView alert = new UIAlertView("Tap bulbs to animate.", null, null, "OK", new String[0]);
        // optional - add more buttons:
        // [alert addButtonWithTitle:@"Ok"];
        alert.show();
    }

    @Override
    public void didReceiveMemoryWarning() {
        didReceiveMemoryWarning();
        // Dispose of any resources that can be recreated.
    }

}
