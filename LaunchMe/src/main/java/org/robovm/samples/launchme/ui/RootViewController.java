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
 * Portions of this code is based on Apple Inc's LaunchMe sample (v170)
 * which is copyright (C) 2008-2013 Apple Inc.
 */
package org.robovm.samples.launchme.ui;

import org.robovm.apple.foundation.NSRange;
import org.robovm.apple.foundation.NSSet;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIMenuController;
import org.robovm.apple.uikit.UIResponder;
import org.robovm.apple.uikit.UISlider;
import org.robovm.apple.uikit.UITextView;
import org.robovm.apple.uikit.UITouch;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import org.robovm.objc.annotation.IBOutlet;

@CustomClass("RootViewController")
public class RootViewController extends UIViewController {
    private UISlider redSlider;
    private UISlider greenSlider;
    private UISlider blueSlider;
    private UITextView urlField;
    private UILabel urlFieldHeader;
    private UIView colorView;

    private UIColor selectedColor;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        /*
         * The AppDelegate may have assigned a color to selectedColor that
         * should be the color displayed initially. This would have occurred
         * before the view was actually loaded meaning that while update was
         * executed, it had no effect. The solution is to call it again here now
         * that there is a UI to update.
         */
        update(selectedColor);
    }

    /**
     * Update the interface to display aColor. This includes modifying colorView
     * to show aColor, moving the red, green, and blue sliders to match the R,
     * G, and B components of aColor, and updating urlLabel to display the
     * corresponding URL for aColor.
     */
    public void update(UIColor aColor) {
        if (aColor == null)
            return;
        /*
         * There is a possibility that getRGBA could fail if aColor is not in a
         * compatible color space. In such a case, the arguments are not
         * modified. Having default values will allow for a more graceful
         * failure than picking up whatever is currently on the stack.
         */
        float red = 0;
        float green = 0;
        float blue = 0;

        double[] rgba = aColor.getRGBA();

        if (rgba == null) {
            /*
             * While setting default values for red, green, blue and alpha
             * guards against undefined results if getRGBA fails, aColor will be
             * assigned as the backgroundColor of colorView a few lines down.
             * Initialize aColor to the black color so it matches the color code
             * that will be displayed in the urlLabel.
             */
            aColor = UIColor.black();
        } else {
            red = (float) rgba[0];
            green = (float) rgba[1];
            blue = (float) rgba[2];
        }

        redSlider.setValue(red);
        greenSlider.setValue(green);
        blueSlider.setValue(blue);

        colorView.setBackgroundColor(aColor);

        /*
         * Construct the URL for the specified color. This URL allows another
         * app to start LauncMe with the specific color displayed initially.
         */
        urlField.setText(String.format("launchme://#%02X%02X%02X", (int) (red * 255), (int) (green * 255),
                (int) (blue * 255)));

        urlFieldHeader.setText("Tap to select the URL");
    }

    /**
     * Custom implementation of the setter for the selectedColor property.
     * 
     * @param selectedColor
     */
    public void setSelectedColor(UIColor selectedColor) {
        if (!selectedColor.equals(this.selectedColor)) {
            this.selectedColor = selectedColor;
            update(selectedColor);
        }
    }

    /**
     * Deselects the text in the urlField if the user taps in the white space of
     * this view controller's view.
     */
    @Override
    public void touchesEnded(NSSet<UITouch> touches, UIEvent event) {
        urlField.setSelectedRange(new NSRange(0, 0));
    }

    @IBAction
    private void urlFieldWasTapped(UITextView sender) {
        // Select the url.
        urlField.setSelectedRange(new NSRange(0, urlField.getText().length()));
        // Show the copy menu.
        UIMenuController.getSharedMenuController().setTargetRect(urlField.getBounds(), urlField);
        UIMenuController.getSharedMenuController().setMenuVisible(true, true);

    }

    @IBAction
    private void startMobileSafari(UIResponder sender) {
        UIApplication.getSharedApplication().openURL(new NSURL("http://www.apple.com"));
    }

    @IBAction
    private void sliderValueDidChange(UISlider sender) {
        /*
         * Create a new UIColor object with the current value of all three
         * sliders (it does not matter which one was actualy modified).
         */
        setSelectedColor(UIColor.fromRGBA(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue(), 1));
    }

    public UILabel getUrlFieldHeader() {
        return urlFieldHeader;
    }

    @IBOutlet
    private void setRedSlider(UISlider redSlider) {
        this.redSlider = redSlider;
    }

    @IBOutlet
    private void setGreenSlider(UISlider greenSlider) {
        this.greenSlider = greenSlider;
    }

    @IBOutlet
    private void setBlueSlider(UISlider blueSlider) {
        this.blueSlider = blueSlider;
    }

    @IBOutlet
    private void setUrlField(UITextView urlField) {
        this.urlField = urlField;
    }

    @IBOutlet
    private void setUrlFieldHeader(UILabel urlFieldHeader) {
        this.urlFieldHeader = urlFieldHeader;
    }

    @IBOutlet
    private void setColorView(UIView colorView) {
        this.colorView = colorView;
    }
}
