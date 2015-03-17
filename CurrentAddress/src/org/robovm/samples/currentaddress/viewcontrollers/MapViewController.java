/*
 * Copyright (C) 2014 RoboVM AB
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
 * Portions of this code is based on Apple Inc's CurrentAddress sample (v1.4)
 * which is copyright (C) 2009-2013 Apple Inc.
 */

package org.robovm.samples.currentaddress.viewcontrollers;

import java.util.HashMap;
import java.util.Map;

import org.robovm.apple.corelocation.CLGeocoder;
import org.robovm.apple.corelocation.CLLocationManager;
import org.robovm.apple.corelocation.CLPlacemark;
import org.robovm.apple.dispatch.Dispatch;
import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSObjectProtocol;
import org.robovm.apple.mapkit.MKMapView;
import org.robovm.apple.mapkit.MKMapViewDelegateAdapter;
import org.robovm.apple.mapkit.MKUserLocation;
import org.robovm.apple.uikit.NSLayoutConstraint;
import org.robovm.apple.uikit.NSLayoutFormatOptions;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonItemStyle;
import org.robovm.apple.uikit.UIBarButtonSystemItem;
import org.robovm.apple.uikit.UIBarStyle;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIToolbar;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.block.VoidBlock2;

public class MapViewController extends UIViewController {
    private final MKMapView mapView;
    private final UIBarButtonItem getAddressButton;
    private final CLGeocoder geocoder;
    private CLPlacemark placemark;
    private final CLLocationManager locationManager;

    private final PlaceMarkViewController placeMarkViewController;

    public MapViewController() {
        setTitle("Current Address");

        locationManager = new CLLocationManager();

        UIView view = getView();
        view.setBackgroundColor(UIColor.fromWhiteAlpha(0.75, 1));

        mapView = new MKMapView();
        mapView.setMultipleTouchEnabled(true);
        mapView.setShowsUserLocation(true);
        mapView.setTranslatesAutoresizingMaskIntoConstraints(false);
        view.addSubview(mapView);

        UIToolbar toolbar = new UIToolbar();
        toolbar.setBarStyle(UIBarStyle.Black);
        toolbar.setTranslatesAutoresizingMaskIntoConstraints(false);

        getAddressButton = new UIBarButtonItem("Get Current Address", UIBarButtonItemStyle.Plain,
                new UIBarButtonItem.OnClickListener() {
                    @Override
                    public void onClick(UIBarButtonItem barButtonItem) {
                        // Get the destination view controller and set the
                        // placemark data that it should display.
                        placeMarkViewController.setPlacemark(placemark);
                        getNavigationController().pushViewController(placeMarkViewController, true);
                    }
                });
        getAddressButton.setEnabled(false);
        toolbar.setItems(new NSArray<>(new UIBarButtonItem(UIBarButtonSystemItem.FlexibleSpace, null),
                getAddressButton,
                new UIBarButtonItem(UIBarButtonSystemItem.FlexibleSpace, null)));
        view.addSubview(toolbar);

        // Layout
        Map<String, NSObjectProtocol> views = new HashMap<>();
        views.put("top", getTopLayoutGuide());
        views.put("map", mapView);
        views.put("toolbar", toolbar);

        view.addConstraints(NSLayoutConstraint.create("H:|[map]|", NSLayoutFormatOptions.None, null, views));
        view.addConstraints(NSLayoutConstraint.create("H:|[toolbar]|", NSLayoutFormatOptions.None, null, views));
        view.addConstraints(NSLayoutConstraint
                .create("V:[top][map][toolbar]|", NSLayoutFormatOptions.None, null, views));

        mapView.setDelegate(new MKMapViewDelegateAdapter() {
            @Override
            public void didUpdateUserLocation(final MKMapView mapView, final MKUserLocation userLocation) {
                // Center the map the first time we get a real location change.
                if ((userLocation.getCoordinate().getLatitude() != 0.0)
                        && (userLocation.getCoordinate().getLongitude() != 0.0)) {
                    Dispatch.once(new Runnable() {
                        @Override
                        public void run() {
                            mapView.setCenterCoordinate(userLocation.getCoordinate(), true);
                        }
                    });
                }

                // Lookup the information for the current location of the user.
                geocoder.reverseGeocodeLocation(mapView.getUserLocation().getLocation(),
                        new VoidBlock2<NSArray<CLPlacemark>, NSError>() {
                            @Override
                            public void invoke(NSArray<CLPlacemark> placemarks, NSError error) {
                                if (placemarks != null && placemarks.size() > 0) {
                                    // If the placemark is not null then we have
                                    // at least one placemark. Typically there
                                    // will only be
                                    // one.
                                    placemark = placemarks.get(0);

                                    // we have received our current location, so
                                    // enable the "Get Current Address" button
                                    getAddressButton.setEnabled(true);
                                } else {
                                    // Handle the null case if necessary.
                                }
                            }
                        });
            }
        });

        placeMarkViewController = new PlaceMarkViewController();
        // Create a geocoder and save it for later.
        geocoder = new CLGeocoder();
    }

    @Override
    public void viewDidLoad() {
        if (Foundation.getMajorSystemVersion() >= 8) {
            locationManager.requestWhenInUseAuthorization();
        }
    }
}
