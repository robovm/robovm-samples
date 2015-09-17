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
 * Portions of this code is based on Apple Inc's Regions sample (v1.1)
 * which is copyright (C) 2011 Apple Inc.
 */

package org.robovm.samples.regions.viewcontrollers;

import java.util.ArrayList;
import java.util.List;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.corelocation.CLLocation;
import org.robovm.apple.corelocation.CLLocationAccuracy;
import org.robovm.apple.corelocation.CLLocationCoordinate2D;
import org.robovm.apple.corelocation.CLLocationManager;
import org.robovm.apple.corelocation.CLLocationManagerDelegateAdapter;
import org.robovm.apple.corelocation.CLRegion;
import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSDate;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSOperationQueue;
import org.robovm.apple.foundation.NSSet;
import org.robovm.apple.mapkit.MKAnnotation;
import org.robovm.apple.mapkit.MKAnnotationView;
import org.robovm.apple.mapkit.MKAnnotationViewDragState;
import org.robovm.apple.mapkit.MKCircle;
import org.robovm.apple.mapkit.MKCircleView;
import org.robovm.apple.mapkit.MKCoordinateRegion;
import org.robovm.apple.mapkit.MKMapView;
import org.robovm.apple.mapkit.MKMapViewDelegateAdapter;
import org.robovm.apple.mapkit.MKOverlay;
import org.robovm.apple.mapkit.MKOverlayView;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonSystemItem;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIButtonType;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UISegmentedControl;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UITableViewDataSourceAdapter;
import org.robovm.apple.uikit.UITableViewDelegateAdapter;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewAutoresizing;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.samples.regions.RegionAnnotation;
import org.robovm.samples.regions.views.RegionAnnotationView;

public class RegionsViewController extends UIViewController {
    private MKMapView regionsMapView;
    private UITableView updatesTableView;
    private final List<String> updateEvents = new ArrayList<String>();
    private CLLocationManager locationManager;

    public RegionsViewController() {
        UISegmentedControl switchButton = new UISegmentedControl(new CGRect(96, 7, 128, 30));
        switchButton.insertSegment("Map", 0, false);
        switchButton.insertSegment("Update", 1, false);
        switchButton.setSelectedSegment(0);
        switchButton.addOnValueChangedListener(new UIControl.OnValueChangedListener() {
            @Override
            public void onValueChanged(UIControl control) {
                switchViews();
            }
        });
        getNavigationItem().setTitleView(switchButton);

        UIBarButtonItem addButton = new UIBarButtonItem(UIBarButtonSystemItem.Add,
                new UIBarButtonItem.OnClickListener() {
                    @Override
                    public void onClick(UIBarButtonItem barButtonItem) {
                        addRegion();
                    }
                });
        getNavigationItem().setRightBarButtonItem(addButton);

        UIView view = getView();

        updatesTableView = new UITableView(UIScreen.getMainScreen().getBounds());
        updatesTableView.setAutoresizingMask(UIViewAutoresizing.with(UIViewAutoresizing.FlexibleWidth,
                UIViewAutoresizing.FlexibleHeight));
        updatesTableView.setHidden(true);
        updatesTableView.setDataSource(new UITableViewDataSourceAdapter() {
            @Override
            public long getNumberOfSections(UITableView tableView) {
                return 1;
            }

            @Override
            public long getNumberOfRowsInSection(UITableView tableView, long section) {
                return updateEvents.size();
            }

            @Override
            public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
                final String cellIdentifier = "Cell";
                UITableViewCell cell = tableView.dequeueReusableCell(cellIdentifier);

                if (cell == null) {
                    cell = new UITableViewCell(UITableViewCellStyle.Default, cellIdentifier);
                }

                cell.getTextLabel().setFont(UIFont.getSystemFont(12));
                cell.getTextLabel().setText(updateEvents.get(indexPath.getRow()));
                cell.getTextLabel().setNumberOfLines(4);

                return cell;
            }
        });
        updatesTableView.setDelegate(new UITableViewDelegateAdapter() {
            @Override
            public double getHeightForRow(UITableView tableView, NSIndexPath indexPath) {
                return 60;
            }
        });
        view.addSubview(updatesTableView);

        regionsMapView = new MKMapView(UIScreen.getMainScreen().getBounds());
        regionsMapView.setAutoresizingMask(UIViewAutoresizing.with(UIViewAutoresizing.FlexibleWidth,
                UIViewAutoresizing.FlexibleHeight));
        regionsMapView.setShowsUserLocation(true);
        regionsMapView.setDelegate(new MKMapViewDelegateAdapter() {
            @Override
            public MKAnnotationView getAnnotationView(MKMapView mapView, MKAnnotation annotation) {
                if (annotation instanceof RegionAnnotation) {
                    RegionAnnotation currentAnnotation = (RegionAnnotation) annotation;
                    String annotationIdentifier = currentAnnotation.getTitle();

                    RegionAnnotationView regionView = (RegionAnnotationView) regionsMapView
                            .dequeueReusableAnnotationView(annotationIdentifier);

                    if (regionView == null) {
                        if (currentAnnotation.getCoordinate() != null) {
                            regionView = new RegionAnnotationView(currentAnnotation);
                            regionView.setMap(regionsMapView);

                            // Create a button for the left callout accessory
                            // view of each annotation to remove the annotation
                            // and
                            // region being monitored.
                            UIButton removeRegionButton = new UIButton(UIButtonType.Custom);
                            removeRegionButton.setFrame(new CGRect(0, 0, 25, 25));
                            removeRegionButton.setImage(UIImage.getImage("RemoveRegion"), UIControlState.Normal);

                            regionView.setLeftCalloutAccessoryView(removeRegionButton);
                            // Update or add the overlay displaying the radius
                            // of the region around the annotation.
                            regionView.updateRadiusOverlay();
                        }
                    } else {
                        regionView.setAnnotation(annotation);
                        // Update or add the overlay displaying the radius of
                        // the region around the annotation.
                        regionView.updateRadiusOverlay();
                    }

                    return regionView;
                }

                return null;
            }

            @Override
            public MKOverlayView getOverlayView(MKMapView mapView, MKOverlay overlay) {
                if (overlay instanceof MKCircle) {
                    // Create the view for the radius overlay.
                    MKCircleView circleView = new MKCircleView((MKCircle) overlay);
                    circleView.setStrokeColor(UIColor.purple());
                    circleView.setFillColor(UIColor.purple().addAlpha(0.4));

                    return circleView;
                }

                return null;
            }

            @Override
            public void didChangeDragState(MKMapView mapView, MKAnnotationView view,
                    MKAnnotationViewDragState newState,
                    MKAnnotationViewDragState oldState) {
                if (view instanceof RegionAnnotationView) {
                    RegionAnnotationView regionView = (RegionAnnotationView) view;
                    RegionAnnotation regionAnnotation = (RegionAnnotation) regionView.getAnnotation();

                    // If the annotation view is starting to be dragged, remove
                    // the overlay and stop monitoring the region.
                    if (newState == MKAnnotationViewDragState.Starting) {
                        regionView.removeRadiusOverlay();

                        locationManager.stopMonitoring(regionAnnotation.getRegion());
                    }

                    // Once the annotation view has been dragged and placed in a
                    // new location, update and add the overlay and
// begin monitoring the new region.
                    if (oldState == MKAnnotationViewDragState.Dragging && newState == MKAnnotationViewDragState.Ending) {
                        regionView.updateRadiusOverlay();

                        CLRegion newRegion = new CLRegion(regionAnnotation.getCoordinate(), 1000, String.format(
                                "%f, %f",
                                regionAnnotation.getCoordinate().getLatitude(), regionAnnotation.getCoordinate()
                                        .getLongitude()));
                        regionAnnotation.setRegion(newRegion);
                        newRegion.release();

                        locationManager.startMonitoring(regionAnnotation.getRegion(), CLLocationAccuracy.Best);
                    }
                }
            }

            @Override
            public void calloutAccessoryControlTapped(MKMapView mapView, final MKAnnotationView view, UIControl control) {
                NSOperationQueue.getMainQueue().addOperation(new Runnable() {
                    @Override
                    public void run() {
                        // Stop monitoring the region, remove the radius
                        // overlay, and finally remove the annotation from the
                        // map.
                        RegionAnnotationView regionView = (RegionAnnotationView) view;
                        RegionAnnotation regionAnnotation = (RegionAnnotation) regionView.getAnnotation();

                        locationManager.stopMonitoring(regionAnnotation.getRegion());
                        regionView.removeRadiusOverlay();
                        regionsMapView.removeAnnotation(regionAnnotation);
                    }
                });
            }
        });
        view.addSubview(regionsMapView);
    }

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        // Create location manager with filters set for battery efficiency.
        locationManager = new CLLocationManager();
        if (Foundation.getMajorSystemVersion() >= 8) {
            locationManager.requestAlwaysAuthorization();
        }
        locationManager.setDelegate(new CLLocationManagerDelegateAdapter() {
            @Override
            public void didFail(CLLocationManager manager, NSError error) {
                System.err.println("didFail: " + error);
            }

            @Override
            public void didUpdateToLocation(CLLocationManager manager, CLLocation newLocation, CLLocation oldLocation) {
                System.err.println(String.format("didUpdateToLocation %s from %s", newLocation, oldLocation));

                // Work around a bug in MapKit where user location is not
                // initially zoomed to.
                if (oldLocation == null) {
                    // Zoom to the current user location.
                    MKCoordinateRegion userLocation = new MKCoordinateRegion(newLocation.getCoordinate(), 1500, 1500);
                    regionsMapView.setRegion(userLocation, true);
                }
            }

            @Override
            public void didEnterRegion(CLLocationManager manager, CLRegion region) {
                String event = String.format("didEnterRegion %s at %s", region.getIdentifier(), NSDate.now());
                update(event);
            }

            @Override
            public void didExitRegion(CLLocationManager manager, CLRegion region) {
                String event = String.format("didExitRegion %s at %s", region.getIdentifier(), NSDate.now());
                update(event);
            }

            @Override
            public void monitoringDidFail(CLLocationManager manager, CLRegion region, NSError error) {
                String event = String.format("monitoringDidFailForRegion %s: %s", region.getIdentifier(), error);
                update(event);
            }

        });
        locationManager.setDistanceFilter(CLLocationAccuracy.HundredMeters);
        locationManager.setDesiredAccuracy(CLLocationAccuracy.Best);

        // Start updating location changes.
        locationManager.startUpdatingLocation();
    }

    @Override
    public void viewDidAppear(boolean animated) {
        // Get all regionsNSSet<NSObject>g monitored for this application.
        NSSet<CLRegion> regions = locationManager.getMonitoredRegions();

        // Iterate through the regions and add annotations to the map for each
        // of them.
        for (CLRegion region : regions) {
            RegionAnnotation annotation = new RegionAnnotation(region);
            regionsMapView.addAnnotation(annotation);
            annotation.release();
        }
    }

    /**
     * This method swaps the visibility of the map view and the table of region
     * events. The "add region" button in the navigation bar is also altered to
     * only be enabled when the map is shown.
     */
    private void switchViews() {
        // Swap the hidden status of the map and table view so that the
        // appropriate one is now showing.
        regionsMapView.setHidden(!regionsMapView.isHidden());
        updatesTableView.setHidden(!updatesTableView.isHidden());

        // Adjust the "add region" button to only be enabled when the map is
        // shown.
        UIBarButtonItem addRegionButton = getNavigationItem().getRightBarButtonItem();
        addRegionButton.setEnabled(!addRegionButton.isEnabled());

        // Reload the table data and update the icon badge number when the table
        // view is shown.
        if (!updatesTableView.isHidden()) {
            updatesTableView.reloadData();
        }
    }

    /*
     * This method creates a new region based on the center coordinate of the
     * map view. A new annotation is created to represent the region and then
     * the application starts monitoring the new region.
     */
    private void addRegion() {
        if (CLLocationManager.isRegionMonitoringAvailable()) {
            // Create a new region based on the center of the map view.
            CLLocationCoordinate2D coord = new CLLocationCoordinate2D(regionsMapView.getCenterCoordinate()
                    .getLatitude(),
                    regionsMapView.getCenterCoordinate().getLongitude());
            CLRegion newRegion = new CLRegion(coord, 1000, String.format("%f, %f", regionsMapView.getCenterCoordinate()
                    .getLatitude(), regionsMapView.getCenterCoordinate().getLongitude()));

            // Create an annotation to show where the region is located on the
            // map.
            RegionAnnotation regionAnnotation = new RegionAnnotation(newRegion);
            regionAnnotation.setCoordinate(newRegion.getCenter());
            regionAnnotation.setRadius(newRegion.getRadius());

            regionsMapView.addAnnotation(regionAnnotation);

            // Start monitoring the newly created region.
            locationManager.startMonitoring(newRegion, CLLocationAccuracy.Best);

            newRegion.release();
        } else {
            System.out.println("Region monitoring is not available.");
        }
    }

    /**
     * This method adds the region event to the events array and updates the
     * icon badge number.
     */
    private void update(String event) {
        // Add region event to the updates array.
        updateEvents.add(0, event);

        // Update the icon badge number.
        long badgeNumber = UIApplication.getSharedApplication().getApplicationIconBadgeNumber();
        UIApplication.getSharedApplication().setApplicationIconBadgeNumber(badgeNumber + 1);

        if (!updatesTableView.isHidden()) {
            updatesTableView.reloadData();
        }
    }

    public CLLocationManager getLocationManager() {
        return locationManager;
    }

    public UITableView getUpdatesTableView() {
        return updatesTableView;
    }
}
