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

package org.robovm.samples.uicatalog.viewcontrollers;

import java.util.LinkedList;
import java.util.List;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSAttributedString;
import org.robovm.apple.foundation.NSDate;
import org.robovm.apple.foundation.NSMutableArray;
import org.robovm.apple.foundation.NSMutableAttributedString;
import org.robovm.apple.foundation.NSRange;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.NSAttributedStringAttribute;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonItemStyle;
import org.robovm.apple.uikit.UIBarButtonSystemItem;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIDatePicker;
import org.robovm.apple.uikit.UIDatePickerMode;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIPickerView;
import org.robovm.apple.uikit.UIPickerViewDataSourceAdapter;
import org.robovm.apple.uikit.UIPickerViewDelegateAdapter;
import org.robovm.apple.uikit.UIScrollView;
import org.robovm.apple.uikit.UISegmentedControl;
import org.robovm.apple.uikit.UIToolbar;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewAutoresizing;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.samples.uicatalog.Constants;
import org.robovm.samples.uicatalog.picker.CustomPickerDataSource;
import org.robovm.samples.uicatalog.picker.CustomView;

/** The view controller for hosting the UIPickerView of this sample. */
public class PickerViewController extends UIViewController {
    private static final float OPTIMUM_PICKER_HEIGHT = 216;
    private static final float OPTIMUM_PICKER_WIDTH = 320;

    private UIScrollView scrollView;

    private UIPickerView myPickerView;
    private UIDatePicker datePickerView;
    private NSMutableArray<NSString> pickerViewArray;

    private UILabel label;

    private UIPickerView customPickerView;
    private CustomPickerDataSource customPickerDataSource;

    private UIView currentPicker;

    private UISegmentedControl buttonBarSegmentedControl;
    private UISegmentedControl pickerStyleSegmentedControl;
    private UILabel segmentLabel;
    private UIToolbar toolbar;

    private final UIControl.OnValueChangedListener togglePickers = new UIControl.OnValueChangedListener() {
        /** for changing between UIPickerView, UIDatePickerView and custom picker
         * 
         * @param control */
        @Override
        public void onValueChanged (UIControl control) {
            UISegmentedControl segControl = (UISegmentedControl)control;
            switch ((int)segControl.getSelectedSegment()) {
            case 0: // UIPickerView
                pickerStyleSegmentedControl.setHidden(true);
                segmentLabel.setHidden(true);
                showPicker(myPickerView);

                // report the selection to the UI label
                String labelStr = String.format("%s - %d", pickerViewArray.get((int)myPickerView.getSelectedRow(0)),
                    myPickerView.getSelectedRow(1));
                label.setText(labelStr);
                break;
            case 1: // UIDatePicker
                pickerStyleSegmentedControl.setHidden(false);
                segmentLabel.setHidden(false);
                showPicker(datePickerView);
                togglePickerStyle.onValueChanged(pickerStyleSegmentedControl);
                break;

            case 2: // Custom
                pickerStyleSegmentedControl.setHidden(true);
                segmentLabel.setHidden(true);
                showPicker(customPickerView);
                break;
            }
        }
    };
    private final UIControl.OnValueChangedListener togglePickerStyle = new UIControl.OnValueChangedListener() {
        /** for changing the date picker's style
         * 
         * @param control */
        @Override
        public void onValueChanged (UIControl control) {
            UISegmentedControl segControl = (UISegmentedControl)control;

            switch ((int)segControl.getSelectedSegment()) {

            case 0: // Time
                datePickerView.setDatePickerMode(UIDatePickerMode.Time);
                segmentLabel.setText("UIDatePickerModeTime");
                break;
            case 1: // Date
                datePickerView.setDatePickerMode(UIDatePickerMode.Date);
                segmentLabel.setText("UIDatePickerModeDate");
                break;
            case 2: // Date & Time
                datePickerView.setDatePickerMode(UIDatePickerMode.DateAndTime);
                segmentLabel.setText("UIDatePickerModeDateAndTime");
                break;
            case 3: // Counter
                datePickerView.setDatePickerMode(UIDatePickerMode.CountDownTimer);
                segmentLabel.setText("UIDatePickerModeCountDownTimer");
                break;
            }

            // in case we previously chose the Counter style picker, make sure
            // the current date is restored
            // @TODO check that current date is inited by default
            NSDate today = new NSDate();
            datePickerView.setDate(today);
        }
    };

    /** Sets up UI components */
    private void initUI () {
        setView(new UIView(new CGRect(0, 0, 320, 460)));

        scrollView = new UIScrollView(new CGRect(0, 0, 320, 416));

        getView().addSubview(scrollView);
        segmentLabel = new UILabel(new CGRect(20, 243, 320, 21));
        segmentLabel.setHidden(true);

        scrollView.addSubview(label);

        pickerStyleSegmentedControl = new UISegmentedControl(NSArray.fromStrings("1", "2", "3", "4"));

        pickerStyleSegmentedControl.setFrame(new CGRect(57, 266, 207, 30));
        pickerStyleSegmentedControl.setTintColor(UIColor.fromWhiteAlpha(0.3333333, 1.0));
        pickerStyleSegmentedControl.addOnValueChangedListener(togglePickerStyle);
        pickerStyleSegmentedControl.setHidden(true);

        scrollView.addSubview(pickerStyleSegmentedControl);

        List<UIBarButtonItem> buttonItems = new LinkedList<UIBarButtonItem>();

        buttonItems.add(new UIBarButtonItem(UIBarButtonSystemItem.FlexibleSpace, null));

        buttonBarSegmentedControl = new UISegmentedControl(NSArray.fromStrings("UIPicker", "UIDatePicker", "Custom"));
        buttonBarSegmentedControl.setFrame(new CGRect(11, 7, 299, 30));
        buttonBarSegmentedControl.setAutoresizingMask(UIViewAutoresizing.FlexibleRightMargin
            .set(UIViewAutoresizing.FlexibleBottomMargin));
        buttonBarSegmentedControl.addOnValueChangedListener(togglePickers);
        buttonBarSegmentedControl.setSelectedSegment(0);

        UIBarButtonItem plainButton = new UIBarButtonItem("", UIBarButtonItemStyle.Plain, null);
        plainButton.setCustomView(buttonBarSegmentedControl);

        buttonItems.add(plainButton);
        buttonItems.add(new UIBarButtonItem(UIBarButtonSystemItem.FlexibleSpace, null));

        toolbar = new UIToolbar(new CGRect(0.0, 416, 320, 44));
        toolbar.setTintColor(UIColor.fromWhiteAlpha(0.333333333333333, 1.0));
        toolbar.setItems(new NSArray<UIBarButtonItem>(buttonItems));

        getView().addSubview(toolbar);
    }

    private CGRect pickerFrameWithSize (CGSize size) {
        CGRect resultFrame;
        double height = size.getHeight();
        double width = size.getWidth();

        if (size.getHeight() < OPTIMUM_PICKER_HEIGHT) {
            // if in landscape, the picker height can be sized too small, so use
            // a optimum height
            height = OPTIMUM_PICKER_HEIGHT;
        }

        if (size.getWidth() > OPTIMUM_PICKER_WIDTH) {
            // keep the width an optimum size as well
            width = OPTIMUM_PICKER_WIDTH;
        }

        resultFrame = new CGRect(0.0, -1.0, width, height);
        return resultFrame;
    }

    /** Creates standard picker */
    private void createPicker () {

        pickerViewArray = new NSMutableArray<NSString>(NSArray.fromStrings("John Appleseed", "Serena Auroux", "Susan Bean",
            "Luis Becerra", "Kate Bell", "Alain Briere"));

        myPickerView = new UIPickerView(new CGRect(0, 0, 0, 0));

        myPickerView.sizeToFit();

        CGSize pickerSize = myPickerView.getFrame().getSize();
        myPickerView.setFrame(pickerFrameWithSize(pickerSize));

        myPickerView.setAutoresizingMask(UIViewAutoresizing.FlexibleLeftMargin.set(UIViewAutoresizing.FlexibleRightMargin));

        myPickerView.setShowsSelectionIndicator(true); // note this is
                                                       // defaulted to NO

        // this view controller is the data source and delegate
        myPickerView.setDelegate(new UIPickerViewDel());

        myPickerView.setDataSource(new UIPickerViewDataSourceAdpt());

        // add this picker to our view controller, initially hidden
        myPickerView.setHidden(true);
        scrollView.addSubview(myPickerView);
    }

    /** Creates date picker */
    private void createDatePicker () {
        datePickerView = new UIDatePicker(new CGRect(0, 0, 0, 0));
        datePickerView.setAutoresizingMask(UIViewAutoresizing.FlexibleLeftMargin.set(UIViewAutoresizing.FlexibleRightMargin));
        datePickerView.setDatePickerMode(UIDatePickerMode.Date);

        datePickerView.sizeToFit();
        CGSize pickerSize = datePickerView.getFrame().getSize();
        datePickerView.setFrame(pickerFrameWithSize(pickerSize));

        // add this picker to our view controller, initially hidden
        datePickerView.setHidden(true);
        scrollView.addSubview(datePickerView);
    }

    /** Creates a custom picker with images using custom constructs
     * 
     * @see CustomPickerDataSource
     * @see CustomView */
    private void createCustomPicker () {
        customPickerView = new UIPickerView(new CGRect(0, 0, 0, 0));
        customPickerView.setAutoresizingMask(UIViewAutoresizing.FlexibleLeftMargin.set(UIViewAutoresizing.FlexibleRightMargin));

        // setup the data source and delegate for this picker
        customPickerDataSource = new CustomPickerDataSource();
        customPickerView.setDataSource(customPickerDataSource);
        customPickerView.setDelegate(customPickerDataSource);

        // note we are using CGRectZero for the dimensions of our picker view,
        // this is because picker views have a built in optimum size,
        // you just need to set the correct origin in your view.
        customPickerView.sizeToFit();
        CGSize pickerSize = customPickerView.getFrame().getSize();
        customPickerView.setFrame(pickerFrameWithSize(pickerSize));
        customPickerView.setShowsSelectionIndicator(true);

        // add this picker to our view controller, initially hidden
        customPickerView.setHidden(true);
        scrollView.addSubview(customPickerView);
    }

    /** load controls once view has been shown */
    @Override
    public void viewDidLoad () {
        super.viewDidLoad();

        initUI();

        setTitle("PickerTitle");

        // set the content size of our scroll view to match the entire screen,
        // this will allow the content to scroll in landscape
        //
        scrollView.setContentSize(new CGSize(scrollView.getFrame().getWidth(), (scrollView.getBounds().getHeight())
            - getNavigationController().getNavigationBar().getFrame().getHeight()));

        // Create pickers
        createPicker();
        createDatePicker();
        createCustomPicker();

        showPicker(myPickerView);

        // label for picker selection output
        CGRect labelFrame = new CGRect(Constants.LEFT_MARGIN, myPickerView.getFrame().getMaxY() + 10.0, getView().getBounds()
            .getWidth() - (Constants.RIGHT_MARGIN * 2.0), 14.0);

        label = new UILabel(labelFrame);
        label.setFont(UIFont.getSystemFont(12.0));
        label.setTextAlignment(NSTextAlignment.Center);
        label.setTextColor(UIColor.black());
        label.setBackgroundColor(UIColor.clear());
        label.setAutoresizingMask(UIViewAutoresizing.FlexibleWidth);
        scrollView.addSubview(label);

        // start by showing the normal picker in date mode
        buttonBarSegmentedControl = new UISegmentedControl(new CGRect(11, 8, 299, 30));
        buttonBarSegmentedControl.setSelectedSegment(0);

        datePickerView.setDatePickerMode(UIDatePickerMode.Date);

        pickerStyleSegmentedControl.setSelectedSegment(1);
    }

    private void showPicker (UIView picker) {
        // hide the current picker and show the new one
        if (currentPicker != null) {
            currentPicker.setHidden(true);
            label.setText("");
        }
        picker.setHidden(false);
        currentPicker = picker; // remember the current picker so we can remove
                                // it later when another one is chosen
    }

    public void pickerView (UIPickerView pickerView, long row, long component) {
        if (pickerView == myPickerView) { // don't show selection for the
                                          // custom picker
            // report the selection to the UI label
            String labelStr = String.format("%@ - %d", pickerViewArray.get((int)myPickerView.getSelectedRow(0)),
                myPickerView.getSelectedRow(1));
            label.setText(labelStr);
        }
    }

    @Override
    public void viewWillAppear (boolean animated) {
        super.viewWillAppear(animated);
        togglePickers.onValueChanged(buttonBarSegmentedControl); // make sure the last picker
        // is still showing

        // for aesthetic reasons (the background is black), make the nav bar
        // black for this particular page
        getNavigationController().getNavigationBar().setTintColor(UIColor.black());
    }

    @Override
    public void viewWillDisappear (boolean animated) {
        super.viewWillDisappear(animated);
        currentPicker.setHidden(true);
    }

    public class UIPickerViewDataSourceAdpt extends UIPickerViewDataSourceAdapter {

        @Override
        public long getNumberOfComponents (UIPickerView pickerView) {
            return 2;
        }

        @Override
        public long getNumberOfRows (UIPickerView pickerView, long component) {
            return pickerViewArray.size();
        }
    }

    public class UIPickerViewDel extends UIPickerViewDelegateAdapter {

        @Override
        public void didSelectRow (UIPickerView pickerView, long row, long component) {
            if (pickerView == myPickerView) { // don't
                                              // show
                                              // selection
                                              // for
                                              // the
                                              // custom
                                              // picker
                // report the selection to the UI label
                String labelStr = String.format("%s - %d", pickerViewArray.get((int)myPickerView.getSelectedRow(0)),
                    myPickerView.getSelectedRow(1));
                label.setText(labelStr);
            }
        }

        @Override
        public double getComponentWidth (UIPickerView pickerView, long component) {
            double componentWidth = 0.0;

            if (component == 0) {
                componentWidth = 240.0; // first column size is wider to hold
                                        // names
            } else {
                componentWidth = 40.0; // second column is narrower to show
                                       // numbers
            }
            return componentWidth;
        }

        @Override
        public double getRowHeight (UIPickerView pickerView, long component) {
            return 40.0;
        }

        @Override
        public String getRowTitle (UIPickerView pickerView, long row, long component) {
            String returnStr = "";

            // note: for the custom picker we use custom views instead of titles
            if (pickerView == myPickerView) {
                if (component == 0) {
                    returnStr = pickerViewArray.get((int)row).toString();
                } else {
                    returnStr = String.valueOf(row);
                }
            }
            return returnStr;
        }

        @Override
        public NSAttributedString getAttributedRowTitle (UIPickerView pickerView, long row, long component) {
            NSMutableAttributedString attrTitle = null;

            // note: for the custom picker we use custom views instead of titles
            if (pickerView == myPickerView) {
                if (row == 0) {
                    String title;
                    if (component == 0) {
                        title = pickerViewArray.get((int)row).toString();
                    } else {
                        title = String.valueOf(row);
                    }

                    // apply red text for normal state
                    attrTitle = new NSMutableAttributedString(title);
                    attrTitle.addAttribute(NSAttributedStringAttribute.ForegroundColor, UIColor.red(),
                        new NSRange(0, attrTitle.length()));
                }
            }

            return attrTitle;
        }
    };

}
