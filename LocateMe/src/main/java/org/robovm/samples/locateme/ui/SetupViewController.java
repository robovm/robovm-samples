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
 * Portions of this code is based on Apple Inc's LocateMe sample (v4.0)
 * which is copyright (C) 2008-2014 Apple Inc.
 */

package org.robovm.samples.locateme.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.robovm.apple.corelocation.CLLocationAccuracy;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIPickerView;
import org.robovm.apple.uikit.UIPickerViewDataSourceAdapter;
import org.robovm.apple.uikit.UIPickerViewDelegateAdapter;
import org.robovm.apple.uikit.UISlider;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import org.robovm.objc.annotation.IBOutlet;
import org.robovm.samples.locateme.util.Str;

@CustomClass("SetupViewController")
public class SetupViewController extends UIViewController {
    public static String SETUP_INFO_KEY_ACCURACY = "SetupInfoKeyAccuracy";
    public static String SETUP_INFO_KEY_DISTANCE_FILTER = "SetupInfoKeyDistanceFilter";
    public static String SETUP_INFO_KEY_TIMEOUT = "SetupInfoKeyTimeout";

    private class AccuracyOption {
        public String name;
        public double accuracy;

        public AccuracyOption(String name, double accuracy) {
            this.name = name;
            this.accuracy = accuracy;
        }
    }

    private SetupViewControllerDelegate delegate;
    private Map<String, Double> setupInfo;
    private List<AccuracyOption> accuracyOptions;
    private boolean configureForTracking;

    private UIPickerView accuracyPicker;
    private UISlider slider;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        accuracyOptions = new ArrayList<>();
        accuracyOptions.add(new AccuracyOption(Str.getLocalizedString("AccuracyBest"), CLLocationAccuracy.Best));
        accuracyOptions.add(new AccuracyOption(Str.getLocalizedString("Accuracy10"),
                CLLocationAccuracy.NearestTenMeters));
        accuracyOptions
                .add(new AccuracyOption(Str.getLocalizedString("Accuracy100"), CLLocationAccuracy.HundredMeters));
        accuracyOptions.add(new AccuracyOption(Str.getLocalizedString("Accuracy1000"), CLLocationAccuracy.Kilometer));
        accuracyOptions.add(new AccuracyOption(Str.getLocalizedString("Accuracy3000"),
                CLLocationAccuracy.ThreeKilometers));

        accuracyPicker.setDataSource(new UIPickerViewDataSourceAdapter() {
            @Override
            public long getNumberOfComponents(UIPickerView pickerView) {
                return 1;
            }

            @Override
            public long getNumberOfRows(UIPickerView pickerView, long component) {
                return 5;
            }
        });
        accuracyPicker.setDelegate(new UIPickerViewDelegateAdapter() {
            @Override
            public void didSelectRow(UIPickerView pickerView, long row, long component) {
                setupInfo.put(SETUP_INFO_KEY_ACCURACY, accuracyOptions.get((int) row).accuracy);
            }

            @Override
            public String getRowTitle(UIPickerView pickerView, long row, long component) {
                return accuracyOptions.get((int) row).name;
            }
        });
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);
        accuracyPicker.selectRow(2, 0, false);

        setupInfo = new HashMap<>();
        setupInfo.put(SETUP_INFO_KEY_DISTANCE_FILTER, 100.0);
        setupInfo.put(SETUP_INFO_KEY_TIMEOUT, 45.0);
        setupInfo.put(SETUP_INFO_KEY_ACCURACY, CLLocationAccuracy.HundredMeters);
    }

    @IBAction
    private void done(UIBarButtonItem sender) {
        dismissViewController(true, null);
        delegate.didFinishSetup(SetupViewController.this, setupInfo);
    }

    @IBAction
    private void sliderChangedValue(UISlider sender) {
        if (configureForTracking) {
            setupInfo.put(SETUP_INFO_KEY_DISTANCE_FILTER, Math.pow(10, slider.getValue()));
        } else {
            setupInfo.put(SETUP_INFO_KEY_TIMEOUT, (double) slider.getValue());
        }
    }

    public void setDelegate(SetupViewControllerDelegate delegate) {
        this.delegate = delegate;
    }

    public void configure(boolean tracking) {
        configureForTracking = tracking;
    }

    @IBOutlet
    private void setAccuracyPicker(UIPickerView accuracyPicker) {
        this.accuracyPicker = accuracyPicker;
    }

    @IBOutlet
    private void setSlider(UISlider slider) {
        this.slider = slider;
    }
}
