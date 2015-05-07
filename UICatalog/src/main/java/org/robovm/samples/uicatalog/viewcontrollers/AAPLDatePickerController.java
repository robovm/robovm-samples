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

import org.robovm.apple.foundation.NSCalendar;
import org.robovm.apple.foundation.NSCalendarOptions;
import org.robovm.apple.foundation.NSDate;
import org.robovm.apple.foundation.NSDateComponents;
import org.robovm.apple.foundation.NSDateFormatter;
import org.robovm.apple.foundation.NSDateFormatterStyle;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIDatePicker;
import org.robovm.apple.uikit.UIDatePickerMode;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;

@CustomClass("AAPLDatePickerController")
public class AAPLDatePickerController extends UIViewController implements UIControl.OnValueChangedListener {
    private UIDatePicker datePicker;
    private UILabel dateLabel;

    private NSDateFormatter dateFormatter;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        // Create a date formatter to be used to format the "date" property of
        // "datePicker".
        dateFormatter = new NSDateFormatter();
        dateFormatter.setDateStyle(NSDateFormatterStyle.Medium);
        dateFormatter.setTimeStyle(NSDateFormatterStyle.Short);

        configureDatePicker();
    }

    private void configureDatePicker() {
        datePicker.setDatePickerMode(UIDatePickerMode.DateAndTime);

        // Set min/max date for the date picker.
        // As an example we will limit the date between now and 7 days from now.
        NSDate now = NSDate.now();
        datePicker.setMinimumDate(now);

        NSCalendar currentCalendar = NSCalendar.getCurrentCalendar();

        NSDateComponents dateComponents = new NSDateComponents();
        dateComponents.setDay(7);

        NSDate sevenDaysFromNow = currentCalendar
                .newDateByAddingComponents(dateComponents, now, NSCalendarOptions.None);
        datePicker.setMaximumDate(sevenDaysFromNow);

        // Display the "minutes" interval by increments of 1 minute (this is the
        // default).
        datePicker.setMinuteInterval(1);

        datePicker.addOnValueChangedListener(this);

        onValueChanged(null);
    }

    @IBOutlet
    private void setDatePicker(UIDatePicker datePicker) {
        this.datePicker = datePicker;
    }

    @IBOutlet
    private void setDateLabel(UILabel dateLabel) {
        this.dateLabel = dateLabel;
    }

    @Override
    public void onValueChanged(UIControl control) {
        dateLabel.setText(dateFormatter.format(datePicker.getDate()));
    }
}
