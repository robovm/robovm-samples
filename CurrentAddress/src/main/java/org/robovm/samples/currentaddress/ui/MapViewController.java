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
 * Portions of this code is based on Apple Inc's CurrentAddress sample (v1.4)
 * which is copyright (C) 2009 - 2013 Apple Inc.
 */

package org.robovm.samples.currentaddress.ui;

import org.robovm.apple.corelocation.CLGeocoder;
import org.robovm.apple.corelocation.CLLocationManager;
import org.robovm.apple.corelocation.CLPlacemark;
import org.robovm.apple.dispatch.Dispatch;
import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.mapkit.MKMapView;
import org.robovm.apple.mapkit.MKMapViewDelegateAdapter;
import org.robovm.apple.mapkit.MKUserLocation;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIStoryboardSegue;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;
import org.robovm.objc.block.VoidBlock2;

@CustomClass("MapViewController")
public class MapViewController extends UIViewController {
    private MKMapView mapView;
    private UIBarButtonItem getAddressButton;

    private CLGeocoder geocoder;
    private CLPlacemark placemark;

    @Override
    public void viewDidLoad() {
        // Create a geocoder and save it for later.
        geocoder = new CLGeocoder();

        if (Foundation.getMajorSystemVersion() >= 8) {
            CLLocationManager locationManager = new CLLocationManager();
            locationManager.requestWhenInUseAuthorization();
        }

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
    }

    @Override
    public void prepareForSegue(UIStoryboardSegue segue, NSObject sender) {
        if (segue.getIdentifier().equals("pushToDetail")) {
            // Get the destination view controller and set the placemark data
            // that it should display.
            PlacemarkViewController viewController = (PlacemarkViewController) segue.getDestinationViewController();
            viewController.setPlacemark(placemark);
        }
    }

    @IBOutlet
    private void setMapView(MKMapView mapView) {
        this.mapView = mapView;
    }

    @IBOutlet
    private void setGetAddressButton(UIBarButtonItem getAddressButton) {
        this.getAddressButton = getAddressButton;
    }
}
