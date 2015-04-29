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

import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControl.OnValueChangedListener;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UISlider;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;
import org.robovm.samples.uicatalog.Colors;

@CustomClass("AAPLSliderViewController")
public class AAPLSliderViewController extends UITableViewController implements OnValueChangedListener {
    private UISlider defaultSlider;
    private UISlider tintedSlider;
    private UISlider customSlider;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        configureDefaultSlider();
        configureTintedSlider();
        configureCustomSlider();
    }

    private void configureDefaultSlider() {
        defaultSlider.setMinimumValue(0);
        defaultSlider.setMaximumValue(100);
        defaultSlider.setValue(42);
        defaultSlider.setContinuous(true);

        defaultSlider.addOnValueChangedListener(this);
    }

    private void configureTintedSlider() {
        tintedSlider.setMinimumTrackTintColor(Colors.BLUE);
        tintedSlider.setMaximumTrackTintColor(Colors.PURPLE);

        tintedSlider.addOnValueChangedListener(this);
    }

    private void configureCustomSlider() {
        UIImage leftTrackImage = UIImage.create("slider_blue_track");
        customSlider.setMinimumTrackImage(leftTrackImage, UIControlState.Normal);

        UIImage rightTrackImage = UIImage.create("slider_green_track");
        customSlider.setMaximumTrackImage(rightTrackImage, UIControlState.Normal);

        UIImage thumbImage = UIImage.create("slider_thumb");
        customSlider.setThumbImage(thumbImage, UIControlState.Normal);

        customSlider.setMinimumValue(0);
        customSlider.setMaximumValue(100);
        customSlider.setContinuous(false);
        customSlider.setValue(84);

        customSlider.addOnValueChangedListener(this);
    }

    @IBOutlet
    private void setDefaultSlider(UISlider defaultSlider) {
        this.defaultSlider = defaultSlider;
    }

    @IBOutlet
    private void setTintedSlider(UISlider tintedSlider) {
        this.tintedSlider = tintedSlider;
    }

    @IBOutlet
    private void setCustomSlider(UISlider customSlider) {
        this.customSlider = customSlider;
    }

    @Override
    public void onValueChanged(UIControl control) {
        System.out.println(String.format("A slider changed its value: %s", control));
    }
}
