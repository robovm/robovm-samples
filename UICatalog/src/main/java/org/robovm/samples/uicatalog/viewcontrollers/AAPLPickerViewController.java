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
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIPickerView;
import org.robovm.apple.uikit.UIPickerViewAccessibilityDelegateAdapter;
import org.robovm.apple.uikit.UIPickerViewDataSource;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;

@CustomClass("AAPLPickerViewController")
public class AAPLPickerViewController extends UIViewController implements UIPickerViewDataSource {
    // The maximum RGB color
    private static final double RGB_MAX = 255;
    // The offset of each color value (from 0 to 255) for red, green, and blue.
    private static final double COLOR_VALUE_OFFSET = 5;
    // The number of colors within a color component.
    private static final int NUMBER_OF_COLOR_VALUES_PER_COMPONENT = (int) (Math.ceil(RGB_MAX / COLOR_VALUE_OFFSET) + 1);

    private UIPickerView pickerView;
    private UIView colorSwatchView;

    private double redColorComponent;
    private double greenColorComponent;
    private double blueColorComponent;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        // Show that a given row is selected. This is off by default.
        pickerView.setShowsSelectionIndicator(true);

        configurePickerView();
    }

    private void configurePickerView() {
        pickerView.setDelegate(new UIPickerViewAccessibilityDelegateAdapter() {
            @Override
            public NSAttributedString getAttributedRowTitle(UIPickerView pickerView, long row, long component) {
                int colorValue = (int) (row * COLOR_VALUE_OFFSET);

                double colorComponent = colorValue / RGB_MAX;
                double redColorComponent = 0;
                double greenColorComponent = 0;
                double blueColorComponent = 0;

                switch ((int) component) {
                case 0: // RED
                    redColorComponent = colorComponent;
                    break;
                case 1: // GREEN
                    greenColorComponent = colorComponent;
                    break;
                case 2: // BLUE
                    blueColorComponent = colorComponent;
                    break;
                default:
                    System.out.println("Invalid row/component combination for picker view.");
                    break;
                }

                UIColor foregroundColor = UIColor.fromRGBA(redColorComponent, greenColorComponent, blueColorComponent,
                        1);

                String titleText = String.valueOf(colorValue);

                // Set the foreground color for the attributed string.
                NSAttributedStringAttributes attributes = new NSAttributedStringAttributes()
                        .setForegroundColor(foregroundColor);
                NSAttributedString title = new NSAttributedString(titleText, attributes);

                return title;
            }

            @Override
            public void didSelectRow(UIPickerView pickerView, long row, long component) {
                AAPLPickerViewController.this.didSelectRow((int) row, (int) component);
            }

            @Override
            public String getAccessibilityLabel(UIPickerView pickerView, long component) {
                String accessiblityLabel = null;

                switch ((int) component) {
                case 0: // RED
                    accessiblityLabel = "Red color component valu";
                    break;
                case 1: // GREEN
                    accessiblityLabel = "Green color component valu";
                    break;
                case 2: // BLUE
                    accessiblityLabel = "Blue color component valu";
                    break;
                default:
                    System.out.println("Invalid row/component combination for picker view.");
                    break;
                }

                return accessiblityLabel;
            }
        });

        // Set the default selected rows (the desired rows to initially select
        // will vary by use case).
        selectRowInPickerView(13, 0);
        selectRowInPickerView(41, 1);
        selectRowInPickerView(24, 2);
    }

    private void selectRowInPickerView(int row, int colorComponent) {
        // Note that the delegate method on UIPickerViewDelegate is not
        // triggered when manually calling UIPickerView.selectRow().
        // To do this, we fire off the delegate method manually.
        pickerView.selectRow(row, colorComponent, true);

        didSelectRow(row, colorComponent);
    }

    private void updateColorSwatchViewBackgroundColor() {
        colorSwatchView.setBackgroundColor(UIColor.fromRGBA(redColorComponent, greenColorComponent, blueColorComponent,
                1));
    }

    @Override
    public long getNumberOfComponents(UIPickerView pickerView) {
        return 3;
    }

    @Override
    public long getNumberOfRows(UIPickerView pickerView, long component) {
        return NUMBER_OF_COLOR_VALUES_PER_COMPONENT;
    }

    private void didSelectRow(int row, int component) {
        double colorComponentValue = (COLOR_VALUE_OFFSET * row) / RGB_MAX;

        switch (component) {
        case 0: // RED
            redColorComponent = colorComponentValue;
            break;
        case 1: // GREEN
            greenColorComponent = colorComponentValue;
            break;
        case 2: // BLUE
            blueColorComponent = colorComponentValue;
            break;
        default:
            System.out.println("Invalid row/component combination selected for picker view.");
            break;
        }
        updateColorSwatchViewBackgroundColor();
    }

    @IBOutlet
    private void setPickerView(UIPickerView pickerView) {
        this.pickerView = pickerView;
    }

    @IBOutlet
    private void setColorSwatchView(UIView colorSwatchView) {
        this.colorSwatchView = colorSwatchView;
    }
}
