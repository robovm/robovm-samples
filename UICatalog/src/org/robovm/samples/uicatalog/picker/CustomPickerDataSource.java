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
 * Portions of this code is based on Apple Inc's UICatalog sample (v2.11)
 * which is copyright (C) 2008-2013 Apple Inc.
 */

package org.robovm.samples.uicatalog.picker;

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIPickerView;
import org.robovm.apple.uikit.UIPickerViewDataSource;
import org.robovm.apple.uikit.UIPickerViewDelegateAdapter;
import org.robovm.apple.uikit.UIView;

/** The data source for the Custom Picker that displays text and images. */
public class CustomPickerDataSource extends UIPickerViewDelegateAdapter implements UIPickerViewDataSource {

    NSArray<CustomView> customPickerArray = new NSArray<CustomView>();

    /** Creates a custom picker prepopulated with images */
    public CustomPickerDataSource () {

        CustomView earlyMorningView = new CustomView("Early Morning", UIImage.create("12-6AM.png"));
        CustomView lateMorningView = new CustomView("Late Morning", UIImage.create("6-12AM.png"));
        CustomView afternoonView = new CustomView("Afternoon", UIImage.create("12-6PM.png"));
        CustomView eveningView = new CustomView("Evening", UIImage.create("6-12PM.png"));

        NSArray<CustomView> timeArray = new NSArray<CustomView>(earlyMorningView, lateMorningView, afternoonView, eveningView);

        customPickerArray = timeArray;
    }

    @Override
    public long getNumberOfComponents (UIPickerView pickerView) {
        return 1;
    }

    @Override
    public long getNumberOfRows (UIPickerView pickerView, long component) {
        return customPickerArray.size();
    }

    @Override
    public double getComponentWidth (UIPickerView pickerView, long component) {
        return CustomView.getViewWidth();
    }

    @Override
    public double getRowHeight (UIPickerView pickerView, long component) {
        return CustomView.getViewHeight();
    }

    @Override
    public UIView getRowView (UIPickerView pickerView, long row, long component, UIView view) {
        return customPickerArray.get((int)row);
    }

}
