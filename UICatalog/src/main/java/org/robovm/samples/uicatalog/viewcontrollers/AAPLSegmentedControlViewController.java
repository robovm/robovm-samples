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

import java.util.HashMap;
import java.util.Map;

import org.robovm.apple.uikit.NSAttributedStringAttributes;
import org.robovm.apple.uikit.UIBarMetrics;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIFontDescriptor;
import org.robovm.apple.uikit.UIFontTextStyle;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UISegmentedControl;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;
import org.robovm.samples.uicatalog.Colors;

@CustomClass("AAPLSegmentedControlViewController")
public class AAPLSegmentedControlViewController extends UITableViewController implements
        UIControl.OnValueChangedListener {
    private UISegmentedControl defaultSegmentedControl;
    private UISegmentedControl tintedSegmentedControl;
    private UISegmentedControl customSegmentsSegmentedControl;
    private UISegmentedControl customBackgroundSegmentedControl;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        configureDefaultSegmentedControl();
        configureTintedSegmentedControl();
        configureCustomSegmentsSegmentedControl();
        configureCustomBackgroundSegmentedControl();
    }

    private void configureDefaultSegmentedControl() {
        defaultSegmentedControl.setMomentary(true);

        defaultSegmentedControl.setEnabled(false, 0);

        defaultSegmentedControl.addOnValueChangedListener(this);
    }

    private void configureTintedSegmentedControl() {
        tintedSegmentedControl.setTintColor(Colors.BLUE);

        tintedSegmentedControl.setSelectedSegment(1);

        tintedSegmentedControl.addOnValueChangedListener(this);
    }

    private void configureCustomSegmentsSegmentedControl() {
        Map<String, String> imageToAccessibilityLabelMappings = new HashMap<>();
        imageToAccessibilityLabelMappings.put("checkmark_icon", "Done");
        imageToAccessibilityLabelMappings.put("search_icon", "Search");
        imageToAccessibilityLabelMappings.put("tools_icon", "Settings");

        int i = 0;
        for (Map.Entry<String, String> entry : imageToAccessibilityLabelMappings.entrySet()) {
            UIImage image = UIImage.create(entry.getKey());
            image.setAccessibilityLabel(entry.getValue());

            customSegmentsSegmentedControl.setImage(image, i);
            i++;
        }

        customSegmentsSegmentedControl.setSelectedSegment(0);

        customSegmentsSegmentedControl.addOnValueChangedListener(this);
    }

    private void configureCustomBackgroundSegmentedControl() {
        customBackgroundSegmentedControl.setSelectedSegment(2);

        // Set the background images for each control state.
        UIImage normalSegmentBackgroundImage = UIImage.create("stepper_and_segment_background");
        customBackgroundSegmentedControl.setBackgroundImage(normalSegmentBackgroundImage, UIControlState.Normal,
                UIBarMetrics.Default);

        UIImage disabledSegmentBackgroundImage = UIImage.create("stepper_and_segment_background_disabled");
        customBackgroundSegmentedControl.setBackgroundImage(disabledSegmentBackgroundImage, UIControlState.Disabled,
                UIBarMetrics.Default);

        UIImage highlightedSegmentBackgroundImage = UIImage.create("stepper_and_segment_background_highlighted");
        customBackgroundSegmentedControl.setBackgroundImage(highlightedSegmentBackgroundImage,
                UIControlState.Highlighted, UIBarMetrics.Default);

        // Set the divider image.
        UIImage segmentDividerImage = UIImage.create("stepper_and_segment_segment_divider");
        customBackgroundSegmentedControl.setDividerImage(segmentDividerImage, UIControlState.Normal,
                UIControlState.Normal, UIBarMetrics.Default);

        // Create a font to use for the attributed title (both normal and
        // highlighted states).
        UIFontDescriptor captionFontDescriptor = UIFontDescriptor.getPreferredFontDescriptor(UIFontTextStyle.Caption1);
        UIFont font = UIFont.getFont(captionFontDescriptor, 0);

        NSAttributedStringAttributes normalTextAttributes = new NSAttributedStringAttributes().setForegroundColor(
                Colors.PURPLE).setFont(font);
        customBackgroundSegmentedControl.setTitleTextAttributes(normalTextAttributes, UIControlState.Normal);

        NSAttributedStringAttributes highlightedTextAttributes = new NSAttributedStringAttributes().setForegroundColor(
                Colors.GREEN).setFont(font);
        customBackgroundSegmentedControl.setTitleTextAttributes(highlightedTextAttributes, UIControlState.Highlighted);

        customBackgroundSegmentedControl.addOnValueChangedListener(this);
    }

    @Override
    public void onValueChanged(UIControl control) {
        System.out.println(String.format("The selected segment changed for: %s.", control));
    }

    @IBOutlet
    private void setDefaultSegmentedControl(UISegmentedControl defaultSegmentedControl) {
        this.defaultSegmentedControl = defaultSegmentedControl;
    }

    @IBOutlet
    private void setTintedSegmentedControl(UISegmentedControl tintedSegmentedControl) {
        this.tintedSegmentedControl = tintedSegmentedControl;
    }

    @IBOutlet
    private void setCustomSegmentsSegmentedControl(UISegmentedControl customSegmentsSegmentedControl) {
        this.customSegmentsSegmentedControl = customSegmentsSegmentedControl;
    }

    @IBOutlet
    private void setCustomBackgroundSegmentedControl(UISegmentedControl customBackgroundSegmentedControl) {
        this.customBackgroundSegmentedControl = customBackgroundSegmentedControl;
    }
}
