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
 * Portions of this code is based on Apple Inc's LocateMe sample (v2.2)
 * which is copyright (C) 2008-2010 Apple Inc.
 */

package org.robovm.samples.locateme.viewcontrollers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.corelocation.CLLocationAccuracy;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonSystemItem;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UINavigationItem;
import org.robovm.apple.uikit.UIPickerView;
import org.robovm.apple.uikit.UIPickerViewDataSourceAdapter;
import org.robovm.apple.uikit.UIPickerViewDelegateAdapter;
import org.robovm.apple.uikit.UIRectEdge;
import org.robovm.apple.uikit.UISlider;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.samples.locateme.Str;

public class SetupViewController extends UIViewController {
    public static String SETUP_INFO_KEY_ACCURACY = "SetupInfoKeyAccuracy";
    public static String SETUP_INFO_KEY_DISTANCE_FILTER = "SetupInfoKeyDistanceFilter";
    public static String SETUP_INFO_KEY_TIMEOUT = "SetupInfoKeyTimeout";

    private class AccuracyOption {
        public String name;
        public double accuracy;

        public AccuracyOption (String name, double accuracy) {
            this.name = name;
            this.accuracy = accuracy;
        }
    }

    private SetupViewControllerDelegate delegate;
    private final Map<String, Double> setupInfo = new HashMap<>();
    private final List<AccuracyOption> accuracyOptions = new ArrayList<>();
    private boolean configureForTracking;

    private UIPickerView accuracyPicker;
    private UISlider slider;
    private UILabel sliderLabel;
    private UILabel label1;
    private UILabel label2;
    private UILabel label3;
    private UILabel label4;

    public SetupViewController () {
        super();

        accuracyOptions.add(new AccuracyOption(Str.getLocalizedString("AccuracyBest"), CLLocationAccuracy.Best));
        accuracyOptions.add(new AccuracyOption(Str.getLocalizedString("Accuracy10"), CLLocationAccuracy.NearestTenMeters));
        accuracyOptions.add(new AccuracyOption(Str.getLocalizedString("Accuracy100"), CLLocationAccuracy.HundredMeters));
        accuracyOptions.add(new AccuracyOption(Str.getLocalizedString("Accuracy1000"), CLLocationAccuracy.Kilometer));
        accuracyOptions.add(new AccuracyOption(Str.getLocalizedString("Accuracy3000"), CLLocationAccuracy.ThreeKilometers));

    }

    @Override
    public void viewDidLoad () {
        setTitle("Configure");
        setEdgesForExtendedLayout(UIRectEdge.None);

        UINavigationItem nav = getNavigationItem();
        nav.setRightBarButtonItem(new UIBarButtonItem(UIBarButtonSystemItem.Done, new UIBarButtonItem.OnClickListener() {
            @Override
            public void onClick (UIBarButtonItem barButtonItem) {
                dismissViewController(true, null);
                delegate.didFinishSetup(SetupViewController.this, setupInfo);
            }
        }));

        UIView view = getView();
        view.setBackgroundColor(UIColor.black());

        accuracyPicker = new UIPickerView(new CGRect(0, 79, 320, 216));
        accuracyPicker.setMultipleTouchEnabled(true);
        accuracyPicker.setShowsSelectionIndicator(true);
        accuracyPicker.setDataSource(new UIPickerViewDataSourceAdapter() {
            @Override
            public long getNumberOfComponents (UIPickerView pickerView) {
                return 1;
            }

            @Override
            public long getNumberOfRows (UIPickerView pickerView, long component) {
                return 5;
            }
        });
        accuracyPicker.setDelegate(new UIPickerViewDelegateAdapter() {
            @Override
            public UIView getRowView (UIPickerView pickerView, long row, long component, UIView view) {
                UILabel label = new UILabel(new CGRect(0, 0, pickerView.getFrame().size().width(), 44));
                label.setTextColor(UIColor.white());
                label.setTextAlignment(NSTextAlignment.Center);
                label.setFont(UIFont.getSystemFont(16));
                label.setText(accuracyOptions.get((int)row).name);
                return label;
            }

            @Override
            public void didSelectRow (UIPickerView pickerView, long row, long component) {
                setupInfo.put(SETUP_INFO_KEY_ACCURACY, accuracyOptions.get((int)row).accuracy);
            }
        });
        view.addSubview(accuracyPicker);

        UILabel desiredAccuracyLabel = new UILabel(new CGRect(20, 50, 151, 21));
        desiredAccuracyLabel.setText("Desired Accuracy:");
        desiredAccuracyLabel.setFont(UIFont.getFont("Helvetica-Bold", 17));
        desiredAccuracyLabel.setTextColor(UIColor.white());
        view.addSubview(desiredAccuracyLabel);

        sliderLabel = new UILabel(new CGRect(20, 328, 196, 21));
        sliderLabel.setFont(UIFont.getFont("Helvetica-Bold", 17));
        sliderLabel.setTextColor(UIColor.white());
        view.addSubview(sliderLabel);

        slider = new UISlider(new CGRect(18, 370, 284, 23));
        slider.setMultipleTouchEnabled(true);
        slider.addOnValueChangedListener(new UIControl.OnValueChangedListener() {
            @Override
            public void onValueChanged (UIControl control) {
                if (configureForTracking) {
                    setupInfo.put(SETUP_INFO_KEY_DISTANCE_FILTER, Math.pow(10, slider.getValue()));
                } else {
                    setupInfo.put(SETUP_INFO_KEY_TIMEOUT, (double)slider.getValue());
                }
            }
        });
        view.addSubview(slider);

        label4 = new UILabel(new CGRect(284, 400, 32, 18));
        label4.setFont(UIFont.getSystemFont(14));
        label4.setTextColor(UIColor.white());
        view.addSubview(label4);

        label3 = new UILabel(new CGRect(196, 400, 32, 18));
        label3.setFont(UIFont.getSystemFont(14));
        label3.setTextColor(UIColor.white());
        view.addSubview(label3);

        label2 = new UILabel(new CGRect(108, 400, 32, 18));
        label2.setFont(UIFont.getSystemFont(14));
        label2.setTextColor(UIColor.white());
        view.addSubview(label2);

        label1 = new UILabel(new CGRect(20, 400, 32, 18));
        label1.setFont(UIFont.getSystemFont(14));
        label1.setTextColor(UIColor.white());
        view.addSubview(label1);

        if (configureForTracking) {
            sliderLabel.setText("Distance Filter (meters):");
            label1.setText("1");
            label2.setText("10");
            label3.setText("100");
            label4.setText("1000");
        } else {
            sliderLabel.setText("Timeout (seconds):");
            slider.setMinimumValue(0);
            slider.setMaximumValue(3);
            slider.setValue(2, false);
            label1.setText("15");
            label2.setText("30");
            label3.setText("45");
            label4.setText("60");
        }
    }

    @Override
    public void viewWillAppear (boolean animated) {
        super.viewWillAppear(animated);
        accuracyPicker.selectRow(2, 0, false);

        setupInfo.put(SETUP_INFO_KEY_DISTANCE_FILTER, 100.0);
        setupInfo.put(SETUP_INFO_KEY_TIMEOUT, 45.0);
        setupInfo.put(SETUP_INFO_KEY_ACCURACY, CLLocationAccuracy.HundredMeters);
    }

    public void setDelegate (SetupViewControllerDelegate delegate) {
        this.delegate = delegate;
    }

    public void configure (boolean tracking) {
        configureForTracking = tracking;
    }
}
