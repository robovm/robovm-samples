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
 * Portions of this code is based on Apple Inc's UICatalog sample (v11.3)
 * which is copyright (C) 2008-2015 Apple Inc.
 */
package org.robovm.samples.uicatalog.viewcontrollers;

import org.robovm.apple.foundation.NSAttributedString;
import org.robovm.apple.uikit.NSAttributedStringAttributes;
import org.robovm.apple.uikit.NSUnderlineStyle;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;
import org.robovm.samples.uicatalog.Colors;

@CustomClass("AAPLButtonViewController")
public class AAPLButtonViewController extends UITableViewController implements UIControl.OnTouchUpInsideListener {
    private UIButton systemTextButton;
    private UIButton systemContactAddButton;
    private UIButton systemDetailDisclosureButton;
    private UIButton imageButton;
    private UIButton attributedTextButton;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        // All of the buttons are created in the storyboard, but configured
        // below.
        configureSystemTextButton();
        configureSystemContactAddButton();
        configureSystemDetailDisclosureButton();
        configureImageButton();
        configureAttributedTextSystemButton();
    }

    private void configureSystemTextButton() {
        systemTextButton.setTitle("Button", UIControlState.Normal);
        systemTextButton.addOnTouchUpInsideListener(this);
    }

    private void configureSystemContactAddButton() {
        systemContactAddButton.setBackgroundColor(UIColor.clear());
        systemContactAddButton.addOnTouchUpInsideListener(this);
    }

    private void configureSystemDetailDisclosureButton() {
        systemDetailDisclosureButton.setBackgroundColor(UIColor.clear());
        systemDetailDisclosureButton.addOnTouchUpInsideListener(this);
    }

    private void configureImageButton() {
        // To create this button in code you can use UIButton.create() with a
        // parameter value of UIButtonType.Custom.

        // Remove the title text.
        imageButton.setTitle("", UIControlState.Normal);

        imageButton.setTintColor(Colors.PURPLE);

        UIImage imageButtonNormalImage = UIImage.getImage("x_icon");
        imageButton.setImage(imageButtonNormalImage, UIControlState.Normal);

        // Add an accessibility label to the image.
        imageButton.setAccessibilityLabel("X Button");

        imageButton.addOnTouchUpInsideListener(this);
    }

    private void configureAttributedTextSystemButton() {
        // Set the button's title for normal state.
        NSAttributedStringAttributes normalTitleAttributes = new NSAttributedStringAttributes().setForegroundColor(
                Colors.BLUE).setStrikethroughStyle(NSUnderlineStyle.StyleSingle);
        NSAttributedString normalAttributedTitle = new NSAttributedString("Button", normalTitleAttributes);

        attributedTextButton.setAttributedTitle(normalAttributedTitle, UIControlState.Normal);

        // Set the button's title for highlighted state.
        NSAttributedStringAttributes highlightedTitleAttributes = new NSAttributedStringAttributes()
                .setForegroundColor(Colors.GREEN).setStrikethroughStyle(NSUnderlineStyle.StyleThick);
        NSAttributedString highlightedAttributedTitle = new NSAttributedString("Button", highlightedTitleAttributes);

        attributedTextButton.setAttributedTitle(highlightedAttributedTitle, UIControlState.Highlighted);

        attributedTextButton.addOnTouchUpInsideListener(this);
    }

    @Override
    public void onTouchUpInside(UIControl control, UIEvent event) {
        System.out.println(String.format("A button was clicked: %s", control));
    }

    @IBOutlet
    private void setSystemTextButton(UIButton systemTextButton) {
        this.systemTextButton = systemTextButton;
    }

    @IBOutlet
    private void setSystemContactAddButton(UIButton systemContactAddButton) {
        this.systemContactAddButton = systemContactAddButton;
    }

    @IBOutlet
    private void setSystemDetailDisclosureButton(UIButton systemDetailDisclosureButton) {
        this.systemDetailDisclosureButton = systemDetailDisclosureButton;
    }

    @IBOutlet
    private void setImageButton(UIButton imageButton) {
        this.imageButton = imageButton;
    }

    @IBOutlet
    private void setAttributedTextButton(UIButton attributedTextButton) {
        this.attributedTextButton = attributedTextButton;
    }
}
