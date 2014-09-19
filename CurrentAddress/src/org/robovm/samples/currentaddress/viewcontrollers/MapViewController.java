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
 * Portions of this code is based on Apple Inc's CurrentAddress sample (v1.4)
 * which is copyright (C) 2009-2013 Apple Inc.
 */

package org.robovm.samples.currentaddress.viewcontrollers;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.corelocation.CLGeocoder;
import org.robovm.apple.corelocation.CLPlacemark;
import org.robovm.apple.dispatch.Dispatch;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.mapkit.MKMapView;
import org.robovm.apple.mapkit.MKMapViewDelegateAdapter;
import org.robovm.apple.mapkit.MKUserLocation;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonItemStyle;
import org.robovm.apple.uikit.UIBarButtonSystemItem;
import org.robovm.apple.uikit.UIBarStyle;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIToolbar;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.block.VoidBlock2;
import org.robovm.rt.bro.ptr.MachineSizedSIntPtr;

public class MapViewController extends UIViewController {
    private final MKMapView mapView;
    private final UIBarButtonItem getAddressButton;
    private CLGeocoder geocoder;
    private CLPlacemark placemark;

    private final PlaceMarkViewController placeMarkViewController;

    public MapViewController () {
        super();

        placeMarkViewController = new PlaceMarkViewController();

        getNavigationItem().setTitle("Current Address");

        UIView view = getView();
        view.setBackgroundColor(UIColor.fromWhiteAlpha(0.75, 1));

        mapView = new MKMapView();
        mapView.setFrame(new CGRect(0, 0, 320, 436));
        mapView.setMultipleTouchEnabled(true);
        mapView.setShowsUserLocation(true);
        view.addSubview(mapView);

        UIToolbar toolbar = new UIToolbar(new CGRect(0, 436, 320, 44));
        toolbar.setBarStyle(UIBarStyle.Black);

        UIBarButtonItem flexibleSpace1 = new UIBarButtonItem(UIBarButtonSystemItem.FlexibleSpace, null);
        getAddressButton = new UIBarButtonItem("Get Current Address", UIBarButtonItemStyle.Bordered,
            new UIBarButtonItem.OnClickListener() {
                @Override
                public void onClick (UIBarButtonItem barButtonItem) {
                    // Get the destination view controller and set the placemark data that it should display.
                    placeMarkViewController.setPlacemark(placemark);
                    getNavigationController().pushViewController(placeMarkViewController, true);
                }
            });
        getAddressButton.setEnabled(false);
        UIBarButtonItem flexibleSpace2 = new UIBarButtonItem(UIBarButtonSystemItem.FlexibleSpace, null);
        toolbar.setItems(new NSArray<>(flexibleSpace1, getAddressButton, flexibleSpace2));
        view.addSubview(toolbar);

        mapView.setDelegate(new MKMapViewDelegateAdapter() {
            @Override
            public void didUpdateUserLocation (final MKMapView mapView, final MKUserLocation userLocation) {
                // Center the map the first time we get a real location change.
                final MachineSizedSIntPtr centerMapFirstTime = new MachineSizedSIntPtr();

                if ((userLocation.getCoordinate().latitude() != 0.0) && (userLocation.getCoordinate().longitude() != 0.0)) {
                    Dispatch.once(centerMapFirstTime, new Runnable() {
                        @Override
                        public void run () {
                            mapView.setCenterCoordinate(userLocation.getCoordinate(), true);
                        }
                    });
                }

                // Lookup the information for the current location of the user.
                geocoder.reverseGeocodeLocation(mapView.getUserLocation().getLocation(),
                    new VoidBlock2<NSArray<CLPlacemark>, NSError>() {
                        @Override
                        public void invoke (NSArray<CLPlacemark> placemarks, NSError error) {
                            if (placemarks != null && placemarks.size() > 0) {
                                // If the placemark is not null then we have at least one placemark. Typically there will only be
                                // one.
                                placemark = placemarks.get(0);

                                // we have received our current location, so enable the "Get Current Address" button
                                getAddressButton.setEnabled(true);
                            } else {
                                // Handle the nil case if necessary.
                            }
                        }
                    });
            }
        });
    }

    @Override
    public void viewDidLoad () {
        super.viewDidLoad();

        // Create a geocoder and save it for later.
        geocoder = new CLGeocoder();
    }
}
