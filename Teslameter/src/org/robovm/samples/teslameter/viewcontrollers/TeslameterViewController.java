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
 * Portions of this code is based on Apple Inc's Teslameter sample (v1.3)
 * which is copyright (C) 2009-2014 Apple Inc.
 */

package org.robovm.samples.teslameter.viewcontrollers;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.corelocation.CLError;
import org.robovm.apple.corelocation.CLHeading;
import org.robovm.apple.corelocation.CLLocationManager;
import org.robovm.apple.corelocation.CLLocationManagerDelegateAdapter;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.uikit.NSLineBreakMode;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIAlertView;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIStatusBarStyle;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewContentMode;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.samples.teslameter.views.GraphView;

public class TeslameterViewController extends UIViewController {
    private final UILabel magnitudeLabel;
    private final UILabel xLabel;
    private final UILabel yLabel;
    private final UILabel zLabel;
    private final GraphView graphView;

    private CLLocationManager locationManager;

    public TeslameterViewController () {
        super();

        UIView view = getView();
        view.setBackgroundColor(UIColor.fromRGBA(0.01, 0.01, 0.01, 1));

        UIImageView backgroundImageView = new UIImageView(UIImage.createFromBundle("Background.png"));
        backgroundImageView.setFrame(new CGRect(0, -20, 320, 480));
        backgroundImageView.setContentMode(UIViewContentMode.Center);
        view.addSubview(backgroundImageView);

        magnitudeLabel = new UILabel(new CGRect(20, 20, 280, 124));
        magnitudeLabel.setAlpha(0.7);
        magnitudeLabel.setText("--.-");
        magnitudeLabel.setTextAlignment(NSTextAlignment.Right);
        magnitudeLabel.setLineBreakMode(NSLineBreakMode.TruncatingTail);
        magnitudeLabel.setFont(UIFont.getSystemFont(96));
        magnitudeLabel.setTextColor(UIColor.black());
        view.addSubview(magnitudeLabel);

        xLabel = new UILabel(new CGRect(20, 386, 71, 33));
        xLabel.setText("0.0");
        xLabel.setTextAlignment(NSTextAlignment.Right);
        xLabel.setLineBreakMode(NSLineBreakMode.TruncatingTail);
        xLabel.setFont(UIFont.getFont("Helvetica-Bold", 18));
        xLabel.setTextColor(UIColor.fromRGBA(1, 0, 0, 1));
        view.addSubview(xLabel);

        yLabel = new UILabel(new CGRect(120, 386, 74, 33));
        yLabel.setText("0.0");
        yLabel.setTextAlignment(NSTextAlignment.Right);
        yLabel.setLineBreakMode(NSLineBreakMode.TruncatingTail);
        yLabel.setFont(UIFont.getFont("Helvetica-Bold", 18));
        yLabel.setTextColor(UIColor.fromRGBA(0.14, 0.77, 0.012, 1));
        view.addSubview(yLabel);

        zLabel = new UILabel(new CGRect(224, 386, 75, 32));
        zLabel.setText("0.0");
        zLabel.setTextAlignment(NSTextAlignment.Right);
        zLabel.setLineBreakMode(NSLineBreakMode.TruncatingTail);
        zLabel.setFont(UIFont.getFont("Helvetica-Bold", 18));
        zLabel.setTextColor(UIColor.fromRGBA(0.077, 0.065, 1, 1));
        view.addSubview(zLabel);

        graphView = new GraphView(new CGRect(17, 199, 286, 134));
        view.addSubview(graphView);
    }

    @Override
    public void viewDidLoad () {
        super.viewDidLoad();

        // setup the location manager
        locationManager = new CLLocationManager();

        // check if the hardware has a compass
        if (!CLLocationManager.isHeadingAvailable()) {
            // No compass is available. This application cannot function without a compass,
            // so a dialog will be displayed and no magnetic data will be measured.
            locationManager = null;
            UIAlertView noCompassAlert = new UIAlertView("No Compass!",
                "This device does not have the ability to measure magnetic fields.", null, "OK");
            noCompassAlert.show();
        } else {
            // heading service configuration
            locationManager.setHeadingFilter(CLLocationManager.HeadingFilterNone());

            // setup delegate callbacks
            locationManager.setDelegate(new CLLocationManagerDelegateAdapter() {
                /** This delegate method is invoked when the location manager has heading data. */
                @Override
                public void didUpdateHeading (CLLocationManager manager, CLHeading newHeading) {
                    double x = newHeading.getX();
                    double y = newHeading.getY();
                    double z = newHeading.getZ();

                    // Update the labels with the raw x, y, and z values.
                    xLabel.setText(String.format("%.1f", x));
                    yLabel.setText(String.format("%.1f", y));
                    zLabel.setText(String.format("%.1f", z));

                    // Compute and display the magnitude (size or strength) of the vector.
                    // magnitude = sqrt(x^2 + y^2 + z^2)
                    double magnitute = Math.sqrt(x * x + y * y + z * z);
                    magnitudeLabel.setText(String.format("%.1f", magnitute));

                    // Update the graph with the new magnetic reading.
                    graphView.updateHistory(x, y, z);
                }

                /** This delegate method is invoked when the location managed encounters an error condition. */
                @Override
                public void didFail (CLLocationManager manager, NSError error) {
                    if (error.getCode() == CLError.Denied.value()) {
                        // This error indicates that the user has denied the application's request to use location services.
                        manager.stopUpdatingHeading();
                    } else if (error.getCode() == CLError.HeadingFailure.value()) {
                        // This error indicates that the heading could not be determined, most likely because of strong magnetic
                        // interference.
                    }
                }
            });

            // start the compass
            locationManager.startUpdatingHeading();
        }
    }

    @Override
    protected void dispose (boolean finalizing) {
        // Stop the compass
        locationManager.stopUpdatingHeading();
        super.dispose(finalizing);
    }

    @Override
    public UIStatusBarStyle getPreferredStatusBarStyle () {
        // Status bar text should be white.
        return UIStatusBarStyle.LightContent;
    }
}
