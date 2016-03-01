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
 * Portions of this code is based on Apple Inc's PhotoPicker sample (v2.0)
 * which is copyright (C) 2010-2013 Apple Inc.
 */
package org.robovm.samples.teslameter.ui;

import org.robovm.apple.corelocation.CLErrorCode;
import org.robovm.apple.corelocation.CLHeading;
import org.robovm.apple.corelocation.CLLocationManager;
import org.robovm.apple.corelocation.CLLocationManagerDelegateAdapter;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.uikit.UIAlertView;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIStatusBarStyle;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;

@CustomClass("TeslameterViewController")
public class TeslameterViewController extends UIViewController {
    @IBOutlet
    private UILabel magnitudeLabel;
    @IBOutlet
    private UILabel xLabel;
    @IBOutlet
    private UILabel yLabel;
    @IBOutlet
    private UILabel zLabel;
    @IBOutlet
    private GraphView graphView;

    private CLLocationManager locationManager;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        // setup the location manager
        locationManager = new CLLocationManager();

        // check if the hardware has a compass
        if (!CLLocationManager.isHeadingAvailable()) {
            // No compass is available. This application cannot function without
            // a compass,
            // so a dialog will be displayed and no magnetic data will be
            // measured.
            locationManager = null;
            UIAlertView noCompassAlert = new UIAlertView("No Compass!",
                    "This device does not have the ability to measure magnetic fields.", null, "OK");
            noCompassAlert.show();
        } else {
            // heading service configuration
            locationManager.setHeadingFilter(CLLocationManager.getHeadingFilterNone());

            // setup delegate callbacks
            locationManager.setDelegate(new CLLocationManagerDelegateAdapter() {
                /**
                 * This delegate method is invoked when the location manager has
                 * heading data.
                 */
                @Override
                public void didUpdateHeading(CLLocationManager manager, CLHeading newHeading) {
                    double x = newHeading.getX();
                    double y = newHeading.getY();
                    double z = newHeading.getZ();

                    // Update the labels with the raw x, y, and z values.
                    xLabel.setText(String.format("%.1f", x));
                    yLabel.setText(String.format("%.1f", y));
                    zLabel.setText(String.format("%.1f", z));

                    // Compute and display the magnitude (size or strength) of
                    // the vector.
                    // magnitude = sqrt(x^2 + y^2 + z^2)
                    double magnitute = Math.sqrt(x * x + y * y + z * z);
                    magnitudeLabel.setText(String.format("%.1f", magnitute));

                    // Update the graph with the new magnetic reading.
                    graphView.updateHistory(x, y, z);
                }

                /**
                 * This delegate method is invoked when the location managed
                 * encounters an error condition.
                 */
                @Override
                public void didFail(CLLocationManager manager, NSError error) {
                    if (error.getErrorCode() == CLErrorCode.Denied) {
                        // This error indicates that the user has denied the
                        // application's request to use location services.
                        manager.stopUpdatingHeading();
                    } else if (error.getErrorCode() == CLErrorCode.HeadingFailure) {
                        // This error indicates that the heading could not be
                        // determined, most likely because of strong magnetic
                        // interference.
                    }
                }
            });

            // start the compass
            locationManager.startUpdatingHeading();
        }
    }

    @Override
    protected void dispose(boolean finalizing) {
        // Stop the compass
        locationManager.stopUpdatingHeading();
        super.dispose(finalizing);
    }

    @Override
    public UIStatusBarStyle getPreferredStatusBarStyle() {
        // Status bar text should be white.
        return UIStatusBarStyle.LightContent;
    }
}
