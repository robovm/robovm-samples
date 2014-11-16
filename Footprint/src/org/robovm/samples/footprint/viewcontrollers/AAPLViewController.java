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
 * Portions of this code is based on Apple Inc's Footprint sample (v1.0)
 * which is copyright (C) 2014 Apple Inc.
 */

package org.robovm.samples.footprint.viewcontrollers;

import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.corelocation.CLActivityType;
import org.robovm.apple.corelocation.CLAuthorizationStatus;
import org.robovm.apple.corelocation.CLLocation;
import org.robovm.apple.corelocation.CLLocationAccuracy;
import org.robovm.apple.corelocation.CLLocationCoordinate2D;
import org.robovm.apple.corelocation.CLLocationManager;
import org.robovm.apple.corelocation.CLLocationManagerDelegateAdapter;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIInterfaceOrientation;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewAutoresizing;
import org.robovm.apple.uikit.UIViewContentMode;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.samples.footprint.viewcontrollers.AAPLCoordinateConverter.AAPLGeoAnchor;
import org.robovm.samples.footprint.viewcontrollers.AAPLCoordinateConverter.AAPLGeoAnchorPair;

public class AAPLViewController extends UIViewController {
    private final UIImageView imageView;
    private final UIImageView pinView;
    private final UIImageView radiusView;

    private final CLLocationManager locationManager;
    private final AAPLCoordinateConverter coordinateConverter;

    private double displayScale;
    private CGPoint displayOffset;

    private final AAPLGeoAnchorPair anchorPair;

    public AAPLViewController () {
        UIView view = getView();
        view.setBackgroundColor(UIColor.white());

        imageView = new UIImageView(UIScreen.getMainScreen().getApplicationFrame());
        imageView.setImage(UIImage.create("FLOORPLAN_IMAGE_PORTRAIT.png"));
        imageView.setAutoresizingMask(UIViewAutoresizing
            .with(UIViewAutoresizing.FlexibleWidth, UIViewAutoresizing.FlexibleHeight));
        imageView.setContentMode(UIViewContentMode.ScaleAspectFit);
        view.addSubview(imageView);

        radiusView = new UIImageView(new CGRect(6, 7, 62, 62));
        radiusView.setImage(UIImage.create("radius.png"));
        view.addSubview(radiusView);

        pinView = new UIImageView(new CGRect(0, 0, 75, 75));
        pinView.setImage(UIImage.create("pin.png"));
        view.addSubview(pinView);

        // Setup a reference to location manager.
        locationManager = new CLLocationManager();
        locationManager.setDelegate(new CLLocationManagerDelegateAdapter() {
            @Override
            public void didChangeAuthorizationStatus (CLLocationManager manager, CLAuthorizationStatus status) {
                switch (status) {
                case AuthorizedAlways:
                case AuthorizedWhenInUse:
                    System.out.println("Got authorization, start tracking location");
                    startTrackingLocation();
                    break;
                case NotDetermined:
                    locationManager.requestWhenInUseAuthorization();
                    break;
                default:
                    break;
                }
            }

            @Override
            public void didUpdateLocations (CLLocationManager manager, NSArray<CLLocation> locations) {
                // Pass location updates to the map view.
                for (CLLocation location : locations) {
                    System.out.println(String.format("Location (Floor %s): %s", location.getFloor(), location));
                    updateView(location);
                }
            }

        });
        locationManager.setDesiredAccuracy(CLLocationAccuracy.Best);
        locationManager.setActivityType(CLActivityType.Other);

        // We setup a pair of anchors that will define how the floorplan image, maps to geographic co-ordinates
        // Change these coordinates to local coordinates in your area.
        AAPLGeoAnchor anchor1 = new AAPLGeoAnchor();
        anchor1.latitudeLongitude = new CLLocationCoordinate2D(37.770511, -122.465810);
        anchor1.pixel = new CGPoint(12, 18);

        AAPLGeoAnchor anchor2 = new AAPLGeoAnchor();
        anchor2.latitudeLongitude = new CLLocationCoordinate2D(37.769125, -122.466356);
        anchor2.pixel = new CGPoint(481, 815);

        anchorPair = new AAPLGeoAnchorPair();
        anchorPair.fromAnchor = anchor1;
        anchorPair.toAnchor = anchor2;

        // Initialize the coordinate system converter with two anchor points.
        coordinateConverter = new AAPLCoordinateConverter(anchorPair);
    }

    @Override
    public void viewDidAppear (boolean animated) {
        setScaleAndOffset();
        startTrackingLocation();
    }

    private void setScaleAndOffset () {
        CGSize imageViewFrameSize = imageView.getFrame().size();
        CGSize imageSize = imageView.getImage().getSize();

        // Calculate how much we'll be scaling the image to fit on screen.
        displayScale = Math.min(imageViewFrameSize.width() / imageSize.width(), imageViewFrameSize.height() / imageSize.height());
        System.out.println(String.format("Scale Factor: %f", displayScale));

        // Depending on whether we're constrained by width or height,
        // figure out how much our floorplan pixels need to be offset to adjust for the image being centered
        if (imageViewFrameSize.width() / imageSize.width() < imageViewFrameSize.height() / imageSize.height()) {
            System.out.println("Contrained by width");
            displayOffset = new CGPoint(0, (imageViewFrameSize.height() - imageSize.height() * displayScale) / 2);
        } else {
            System.out.println("Contrained by height");
            displayOffset = new CGPoint((imageViewFrameSize.width() - imageSize.width() * displayScale) / 2, 0);
        }

        System.out.println(String.format("Offset: %f, %f", displayOffset.x(), displayOffset.y()));
    }

    private void startTrackingLocation () {
        CLAuthorizationStatus status = CLLocationManager.getAuthorizationStatus();
        if (status == CLAuthorizationStatus.NotDetermined) {
            locationManager.requestWhenInUseAuthorization();
        } else if (status == CLAuthorizationStatus.AuthorizedWhenInUse || status == CLAuthorizationStatus.AuthorizedAlways) {
            locationManager.startUpdatingLocation();
        }
    }

    private void updateView (final CLLocation location) {
        // We animate transition from one position to the next, this makes the dot move smoothly over the map
        UIView.animate(0.75, new Runnable() {
            @Override
            public void run () {
                // Call the converter to find these coordinates on our floorplan.
                CGPoint pointOnImage = coordinateConverter.getPointFromCoordinate(location.getCoordinate());

                // These coordinates need to be scaled based on how much the image has been scaled
                CGPoint scaledPoint = new CGPoint(pointOnImage.x() * displayScale + displayOffset.x(), pointOnImage.y()
                    * displayScale + displayOffset.y());

                // Calculate and set the size of the radius
                double radiusFrameSize = location.getHorizontalAccuracy() * coordinateConverter.getPixelsPerMeter() * 2;
                radiusView.setFrame(new CGRect(0, 0, radiusFrameSize, radiusFrameSize));

                // Move the pin and radius to the user's location
                pinView.setCenter(scaledPoint);
                radiusView.setCenter(scaledPoint);
            }
        });
    }

    @Override
    public void willAnimateRotation (UIInterfaceOrientation toInterfaceOrientation, double duration) {
        // Upon rotation, we want to resize the image and center it appropriately.
        setScaleAndOffset();
    }
}
