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

package org.robovm.sample.uicatalog.viewcontrollers;

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
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonItemStyle;
import org.robovm.apple.uikit.UIBarButtonSystemItem;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControlEvents;
import org.robovm.apple.uikit.UIDatePicker;
import org.robovm.apple.uikit.UIDatePickerMode;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIKit;
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
import org.robovm.objc.Selector;
import org.robovm.objc.annotation.Method;
import org.robovm.sample.uicatalog.Constants;
import org.robovm.sample.uicatalog.picker.CustomPickerDataSource;

/**
 * The view controller for hosting the UIPickerView of this sample.
 */
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
    
    /**
     * Sets up UI components
     */
    private void initUI() {
        setView(new UIView(new CGRect(0, 0, 320, 460)));
        
        scrollView = new UIScrollView(new CGRect(0, 0, 320, 416));
        
        this.getView().addSubview(scrollView);
        this.segmentLabel = new UILabel(new CGRect(20, 243, 320, 21));
        this.segmentLabel.setHidden(true);

        scrollView.addSubview(label);
        
        pickerStyleSegmentedControl = new UISegmentedControl(NSArray.toNSArray("1","2",
                "3", "4"));
        
        pickerStyleSegmentedControl.setFrame(new CGRect(57, 266, 207, 30));
        pickerStyleSegmentedControl.setTintColor(UIColor.createFromWhiteAlpha(0.3333333, 1.0));
        pickerStyleSegmentedControl.addTarget(this, Selector.register("togglePickerStyle:"), UIControlEvents.ValueChanged);
        pickerStyleSegmentedControl.setHidden(true);
        
        scrollView.addSubview(pickerStyleSegmentedControl);
        
        List<UIBarButtonItem> buttonItems = new LinkedList<UIBarButtonItem>();
        
        buttonItems.add(new UIBarButtonItem(UIBarButtonSystemItem.FlexibleSpace, null, null));
        
        this.buttonBarSegmentedControl = new UISegmentedControl(NSArray.toNSArray("UIPicker", "UIDatePicker", "Custom"));
        this.buttonBarSegmentedControl.setFrame(new CGRect(11, 7, 299, 30));
        this.buttonBarSegmentedControl.setAutoresizingMask(UIViewAutoresizing.FlexibleRightMargin.set(UIViewAutoresizing.FlexibleBottomMargin));
        this.buttonBarSegmentedControl.addTarget(this, Selector.register("togglePickers:"), UIControlEvents.ValueChanged);
        this.buttonBarSegmentedControl.setSelectedSegment(0);
        
        UIBarButtonItem plainButton = new UIBarButtonItem("", UIBarButtonItemStyle.Plain, null, null);
        plainButton.setCustomView(this.buttonBarSegmentedControl);
        
        buttonItems.add(plainButton);
        buttonItems.add(new UIBarButtonItem(UIBarButtonSystemItem.FlexibleSpace, null, null));
       
        this.toolbar = new UIToolbar(new CGRect(0.0, 416, 320, 44));
        this.toolbar.setTintColor(UIColor.createFromWhiteAlpha(0.333333333333333, 1.0));
        this.toolbar.setItems(new NSArray<UIBarButtonItem>(buttonItems));
        
        this.getView().addSubview(toolbar);
    }
    
    private CGRect pickerFrameWithSize(CGSize size) {
        CGRect resultFrame;
        double height = size.height();
        double width = size.width();
            
        if (size.height() < OPTIMUM_PICKER_HEIGHT) {
            // if in landscape, the picker height can be sized too small, so use a optimum height
            height = OPTIMUM_PICKER_HEIGHT;
        }
        
        if (size.width() > OPTIMUM_PICKER_WIDTH) {
            // keep the width an optimum size as well
            width = OPTIMUM_PICKER_WIDTH;
        }
            
        resultFrame = new CGRect(0.0, -1.0, width, height);
        return resultFrame;
    }


    /**
     * Creates standard picker
     */
    private void createPicker(){

        pickerViewArray = new NSMutableArray<NSString>(NSArray.toNSArray("John Appleseed", "Serena Auroux",
                "Susan Bean", "Luis Becerra", "Kate Bell", "Alain Briere"));
        
        myPickerView = new UIPickerView(new CGRect(0, 0, 0, 0));
        
        this.myPickerView.resizeToFit();
        
        CGSize pickerSize = this.myPickerView.getFrame().size();
        this.myPickerView.setFrame(this.pickerFrameWithSize(pickerSize));
        
        this.myPickerView.setAutoresizingMask( UIViewAutoresizing.FlexibleLeftMargin.set(UIViewAutoresizing.FlexibleRightMargin));
        
        this.myPickerView.setShowsSelectionIndicator(true);        // note this is defaulted to NO
        
        // this view controller is the data source and delegate
        this.myPickerView.setDelegate(new UIPickerViewDel());
        
        this.myPickerView.setDataSource(new UIPickerViewDataSourceAdpt());
        
        // add this picker to our view controller, initially hidden
        this.myPickerView.setHidden(true);
        this.scrollView.addSubview(myPickerView);
    }
    
    /**
     * Creates date picker
     */
    private void createDatePicker() {
        datePickerView = new UIDatePicker(new CGRect(0, 0, 0, 0));
        datePickerView.setAutoresizingMask(UIViewAutoresizing.FlexibleLeftMargin.set(UIViewAutoresizing.FlexibleRightMargin));
        datePickerView.setDatePickerMode(UIDatePickerMode.Date);
        
        this.datePickerView.resizeToFit();
        CGSize pickerSize = datePickerView.getFrame().size();
        this.datePickerView.setFrame(this.pickerFrameWithSize(pickerSize));
      
      // add this picker to our view controller, initially hidden
        this.datePickerView.setHidden(true);
        this.scrollView.addSubview(this.datePickerView);
    }
    
    /**
     * Creates a custom picker with images using 
     * custom constructs
     * @see CustomPickerDataSource
     * @see CustomView
     */
    private void createCustomPicker() {
        customPickerView = new UIPickerView(new CGRect(0, 0, 0, 0));
        this.customPickerView.setAutoresizingMask(UIViewAutoresizing.FlexibleLeftMargin.set(UIViewAutoresizing.FlexibleRightMargin));

        // setup the data source and delegate for this picker
        this.customPickerDataSource = new CustomPickerDataSource();
        this.customPickerView.setDataSource(customPickerDataSource);
        this.customPickerView.setDelegate(customPickerDataSource);
        
        // note we are using CGRectZero for the dimensions of our picker view,
        // this is because picker views have a built in optimum size,
        // you just need to set the correct origin in your view.
        this.customPickerView.resizeToFit();
        CGSize pickerSize = this.customPickerView.getFrame().size();
        this.customPickerView.setFrame(pickerFrameWithSize(pickerSize));
        this.customPickerView.setShowsSelectionIndicator(true);
        
        // add this picker to our view controller, initially hidden
        this.customPickerView.setHidden(true);
        scrollView.addSubview(customPickerView);
    }
    
    /**
     * load controls once view has been shown
     */
    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        
        initUI();
        
        this.setTitle("PickerTitle");

        // set the content size of our scroll view to match the entire screen,
        // this will allow the content to scroll in landscape
        //
        this.scrollView.setContentSize(new CGSize(scrollView.getFrame().getWidth(),
                                               (this.scrollView.getBounds().getHeight()) - this.getNavigationController().getNavigationBar().getFrame().getHeight())
                                               );

        //Create pickers
        createPicker();
        createDatePicker();
        createCustomPicker();
        
        showPicker(this.myPickerView);

        // label for picker selection output
        CGRect labelFrame = new CGRect(Constants.LEFT_MARGIN,
                                   this.myPickerView.getFrame().getMaxY() + 10.0,
                                   getView().getBounds().getWidth() - (Constants.RIGHT_MARGIN * 2.0),
                                   14.0);
        
        label = new UILabel(labelFrame);
        label.setFont(UIFont.getSystemFont(12.0));
        label.setTextAlignment(NSTextAlignment.Center);
        label.setTextColor(UIColor.colorBlack());
        label.setBackgroundColor(UIColor.colorClear());
        label.setAutoresizingMask(UIViewAutoresizing.FlexibleWidth);
        scrollView.addSubview(label);

        // start by showing the normal picker in date mode
        this.buttonBarSegmentedControl = new UISegmentedControl(new CGRect(11, 8, 299, 30));
        this.buttonBarSegmentedControl.setSelectedSegment(0);

        this.datePickerView.setDatePickerMode(UIDatePickerMode.Date);

        this.pickerStyleSegmentedControl.setSelectedSegment(1);
    }

    private void showPicker(UIView picker) {
        // hide the current picker and show the new one
        if (currentPicker != null ) {
                currentPicker.setHidden(true);
                label.setText("");
        }
        picker.setHidden(false);
        currentPicker = picker;    // remember the current picker so we can remove it later when another one is chosen
    }

    /**
     * for changing the date picker's style
     * @param sender
     */
    @Method
    private void togglePickerStyle(UISegmentedControl sender) {
        UISegmentedControl segControl = sender;

        switch ((int) segControl.getSelectedSegment()) {

        case 0: // Time
            this.datePickerView.setDatePickerMode(UIDatePickerMode.Time);
            this.segmentLabel.setText("UIDatePickerModeTime");
            break;
        case 1: // Date
            this.datePickerView.setDatePickerMode(UIDatePickerMode.Date);
            this.segmentLabel.setText("UIDatePickerModeDate");
            break;
        case 2: // Date & Time
            this.datePickerView.setDatePickerMode(UIDatePickerMode.DateAndTime);
            this.segmentLabel.setText("UIDatePickerModeDateAndTime");
            break;
        case 3: // Counter
            this.datePickerView.setDatePickerMode(UIDatePickerMode.CountDownTimer);
            this.segmentLabel.setText("UIDatePickerModeCountDownTimer");
            break;
        }

        // in case we previously chose the Counter style picker, make sure
        // the current date is restored
        // @TODO check that current date is inited by default
        NSDate today = new NSDate();
        this.datePickerView.setDate(today);
    }

    /**
     * for changing between UIPickerView, UIDatePickerView and custom picker
     * @param sender
     */
    @Method
    private void togglePickers(UISegmentedControl sender)  {
        UISegmentedControl segControl = sender;
        switch ((int)segControl.getSelectedSegment()) {
            case 0: // UIPickerView
                pickerStyleSegmentedControl.setHidden(true);
                segmentLabel.setHidden(true);
                showPicker(this.myPickerView);
    
                // report the selection to the UI label
                String labelStr = String.format("%s - %d", this.pickerViewArray.get((int)myPickerView.getSelectedRow(0)), this.myPickerView.getSelectedRow(1));
                this.label.setText(labelStr);
                break;
            case 1: // UIDatePicker
                pickerStyleSegmentedControl.setHidden(false);
                this.segmentLabel.setHidden(false);
                this.showPicker(datePickerView);
                this.togglePickerStyle(pickerStyleSegmentedControl);
                break;
                    
            case 2: // Custom
                pickerStyleSegmentedControl.setHidden(true);
                this.segmentLabel.setHidden(true);
                showPicker(this.customPickerView);
                break;
        }
    }
    

    public void pickerView(UIPickerView pickerView, long row, long component) {
        if (pickerView == this.myPickerView) { // don't show selection for the custom picker
                // report the selection to the UI label
            String labelStr = String.format("%@ - %d", this.pickerViewArray.get((int)myPickerView.getSelectedRow(0)), this.myPickerView.getSelectedRow(1));
            this.label.setText(labelStr);
        }
    }


    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);
        togglePickers(buttonBarSegmentedControl); // make sure the last picker is still showing
        
        // for aesthetic reasons (the background is black), make the nav bar black for this particular page
        this.getNavigationController().getNavigationBar().setTintColor(UIColor.colorBlack());
    }


    @Override
    public void viewWillDisappear(boolean animated) {
        this.currentPicker.setHidden(animated);
    }

    public class UIPickerViewDataSourceAdpt extends UIPickerViewDataSourceAdapter{

        @Override
        public long getNumberOfComponents(UIPickerView pickerView) {
            return 2;
        }

        @Override
        public long getNumberOfRows(UIPickerView pickerView, long component) {
            return PickerViewController.this.pickerViewArray.size();
        }
        
    }
    
    public class UIPickerViewDel extends UIPickerViewDelegateAdapter {

        @Override
        public void didSelectRow(UIPickerView pickerView, long row, long component) {
            if (pickerView == PickerViewController.this.myPickerView) {   // don't show selection for the custom picker
                // report the selection to the UI label
                String labelStr = String.format("%s - %d", PickerViewController.this.pickerViewArray.get((int)myPickerView.getSelectedRow(0)), PickerViewController.this.myPickerView.getSelectedRow(1));
                PickerViewController.this.label.setText(labelStr);
            }
        }

        @Override
        public double getComponentWidth(UIPickerView pickerView, long component) {
            double componentWidth = 0.0;

            if (component == 0) {
                componentWidth = 240.0; // first column size is wider to hold names
            } else {
                componentWidth = 40.0; // second column is narrower to show numbers
            }
            return componentWidth;
        }

        @Override
        public double getRowHeight(UIPickerView pickerView, long component) {
            return 40.0;
        }

        @Override
        public String getRowTitle(UIPickerView pickerView, long row, long component) {
            String returnStr = "";
            
            // note: for the custom picker we use custom views instead of titles
            if (pickerView == PickerViewController.this.myPickerView) {
                if (component == 0) {
                    returnStr = PickerViewController.this.pickerViewArray.get((int)row).toString();
                } else {
                    returnStr = (String) String.valueOf(row);
                }
            }
            return returnStr;
        }

        @Override
        public NSAttributedString getAttributedRowTitle(UIPickerView pickerView, long row, long component) {
            NSMutableAttributedString attrTitle = null;
            
            // note: for the custom picker we use custom views instead of titles
            if (pickerView == PickerViewController.this.myPickerView) {
                if (row == 0) {
                    String title;
                    if (component == 0) {
                        title = pickerViewArray.get((int)row).toString();
                    } else {
                        title = (String) String.valueOf(row);
                    }

                    // apply red text for normal state
                    attrTitle = new NSMutableAttributedString(title);
                    attrTitle.addAttribute(UIKit.ForegroundColorAttributeName(),
                                      UIColor.colorRed(),
                                      new NSRange(0, attrTitle.getLength()));
                }
            }
            
            return attrTitle;
        }
    };
    
}
