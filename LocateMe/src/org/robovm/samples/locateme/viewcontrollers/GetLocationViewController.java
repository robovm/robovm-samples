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
import java.util.List;
import java.util.Map;

import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.corelocation.CLError;
import org.robovm.apple.corelocation.CLLocation;
import org.robovm.apple.corelocation.CLLocationManager;
import org.robovm.apple.corelocation.CLLocationManagerDelegateAdapter;
import org.robovm.apple.dispatch.Dispatch;
import org.robovm.apple.dispatch.DispatchQueue;
import org.robovm.apple.foundation.NSDateFormatter;
import org.robovm.apple.foundation.NSDateFormatterStyle;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.NSIndexPathExtensions;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIActivityIndicatorView;
import org.robovm.apple.uikit.UIActivityIndicatorViewStyle;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonItemStyle;
import org.robovm.apple.uikit.UIBarStyle;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UINavigationController;
import org.robovm.apple.uikit.UIRectEdge;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellAccessoryType;
import org.robovm.apple.uikit.UITableViewCellSelectionStyle;
import org.robovm.apple.uikit.UITableViewCellSeparatorStyle;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UITableViewDataSourceAdapter;
import org.robovm.apple.uikit.UITableViewDelegateAdapter;
import org.robovm.apple.uikit.UITableViewStyle;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewAutoresizing;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.samples.locateme.Str;

public class GetLocationViewController extends UIViewController {
    private CLLocationManager locationManager;
    private CLLocation bestEffortAtLocation;
    private final List<CLLocation> locationMeasurements = new ArrayList<>();
    private final NSDateFormatter dateFormatter;
    private String stateString = "";
    private boolean canTimeOut;

    private final LocationDetailViewController locationDetailViewController;
    private final UINavigationController setupNavigationController;
    private final SetupViewController setupViewController;
    private UITableView tableView;
    private UILabel descriptionLabel;
    private UIButton startButton;

    public GetLocationViewController () {
        super();

        setupViewController = new SetupViewController();
        setupViewController.setDelegate(new SetupViewControllerDelegate() {
            @Override
            public void didFinishSetup (SetupViewController viewController, Map<String, Double> setupInfo) {
                finishSetup(viewController, setupInfo);
            }
        });
        setupNavigationController = new UINavigationController(setupViewController);
        setupNavigationController.getNavigationBar().setBarStyle(UIBarStyle.Black);
        locationDetailViewController = new LocationDetailViewController(UITableViewStyle.Grouped);

        dateFormatter = new NSDateFormatter();
        dateFormatter.setDateStyle(NSDateFormatterStyle.Medium);
        dateFormatter.setTimeStyle(NSDateFormatterStyle.Long);
    }

    @Override
    public void viewDidLoad () {
        setTitle("Get Location");
        setEdgesForExtendedLayout(UIRectEdge.None);

        UIView view = getView();
        view.setBackgroundColor(UIColor.groupTableViewBackgroundColor());

        /*
         * The table view has three sections. The first has 1 row which displays status information. The second has 1 row which
         * displays the most accurate valid location measurement received. The third has a row for each valid location object
         * received (including the one displayed in the second section) from the location manager.
         */
        tableView = new UITableView(new CGRect(0, 0, 320, 460), UITableViewStyle.Grouped);
        tableView.setAlpha(0);
        tableView.setBouncesZoom(false);
        tableView.setSeparatorStyle(UITableViewCellSeparatorStyle.SingleLine);
        tableView.setRowHeight(44);
        tableView.setSectionIndexMinimumDisplayRowCount(0);
        tableView.setSectionHeaderHeight(10);
        tableView.setSectionFooterHeight(10);
        view.addSubview(tableView);

        descriptionLabel = new UILabel(new CGRect(7, 30, 306, 150));
        descriptionLabel
            .setText("This approach attempts to acquire a location measurement that meets a minimum accuracy. A timeout is specified to avoid unnecessarily wasting power, in case a sufficiently accurate measurement cannot be acquired.");
        descriptionLabel.setFont(UIFont.getSystemFont(17));
        descriptionLabel.setTextColor(UIColor.black());
        descriptionLabel.setNumberOfLines(19);
        descriptionLabel.setTextAlignment(NSTextAlignment.Center);
        view.addSubview(descriptionLabel);

        startButton = new UIButton(new CGRect(124, 195, 72, 37));
        startButton.getTitleLabel().setFont(UIFont.getFont("Helvetica-Bold", 15));
        startButton.setTitle("Start", UIControlState.Normal);
        startButton.setTitleColor(UIColor.black(), UIControlState.Normal);
        startButton.setTitleShadowColor(UIColor.gray(), UIControlState.Normal);
        startButton.addOnTouchUpInsideListener(new UIControl.OnTouchUpInsideListener() {
            @Override
            public void onTouchUpInside (UIControl control, UIEvent event) {
                setupViewController.configure(false);
                getNavigationController().presentViewController(setupNavigationController, true, null);
            }
        });
        view.addSubview(startButton);

        tableView.setDataSource(new UITableViewDataSourceAdapter() {
            @Override
            public long getNumberOfSections (UITableView tableView) {
                return (bestEffortAtLocation != null) ? 3 : 1;
            }

            @Override
            public String getSectionHeaderTitle (UITableView tableView, long section) {
                switch ((int)section) {
                case 0:
                    return Str.getLocalizedString("Status");
                case 1:
                    return Str.getLocalizedString("Best Measurement");
                default:
                    return Str.getLocalizedString("All Measurements");
                }
            }

            @Override
            public long getNumberOfRowsInSection (UITableView tableView, long section) {
                switch ((int)section) {
                case 0:
                    return 1;
                case 1:
                    return 1;
                default:
                    return locationMeasurements.size();
                }
            }

            @Override
            public UITableViewCell getRowCell (UITableView tableView, NSIndexPath indexPath) {
                UITableViewCell cell;
                switch ((int)NSIndexPathExtensions.getSection(indexPath)) {
                case 0:
                    /*
                     * The cell for the status row uses the cell style "Value1", which has a label on the left side of the cell
                     * with left-aligned and black text; on the right side is a label that has smaller blue text and is
                     * right-aligned. An activity indicator has been added to the cell and is animated while the location manager
                     * is updating. The cell's text label displays the current state of the manager.
                     */
                    final String StatusCellID = "StatusCellID";
                    final int StatusCellActivityIndicatorTag = 2;
                    UIActivityIndicatorView activityIndicator = null;
                    cell = tableView.dequeueReusableCell(StatusCellID);
                    if (cell == null) {
                        cell = new UITableViewCell(UITableViewCellStyle.Value1, StatusCellID);
                        cell.setSelectionStyle(UITableViewCellSelectionStyle.None);
                        activityIndicator = new UIActivityIndicatorView(UIActivityIndicatorViewStyle.Gray);
                        CGRect frame = activityIndicator.getFrame();
                        frame.origin(new CGPoint(290, 12));
                        activityIndicator.setFrame(frame);
                        activityIndicator.setAutoresizingMask(UIViewAutoresizing.FlexibleLeftMargin);
                        activityIndicator.setTag(StatusCellActivityIndicatorTag);
                        cell.getContentView().addSubview(activityIndicator);
                    } else {
                        activityIndicator = (UIActivityIndicatorView)cell.getContentView().getViewWithTag(
                            StatusCellActivityIndicatorTag);
                    }
                    cell.getTextLabel().setText(stateString);
                    if (stateString != null && stateString.equals(Str.getLocalizedString("Updating"))) {
                        if (!activityIndicator.isAnimating()) activityIndicator.startAnimating();
                    } else {
                        if (activityIndicator.isAnimating()) activityIndicator.stopAnimating();
                    }
                    return cell;
                case 1:
                    /*
                     * The cells for the location rows use the cell style "Subtitle", which has a left-aligned label across the
                     * top and a left-aligned label below it in smaller gray text. The text label shows the coordinates for the
                     * location and the detail text label shows its timestamp.
                     */
                    final String BestMeasurementCellID = "BestMeasurementCellID";
                    cell = tableView.dequeueReusableCell(BestMeasurementCellID);
                    if (cell == null) {
                        cell = new UITableViewCell(UITableViewCellStyle.Subtitle, BestMeasurementCellID);
                        cell.setAccessoryType(UITableViewCellAccessoryType.DisclosureIndicator);
                    }
                    cell.getTextLabel().setText(Str.getLocalizedCoordinateString(bestEffortAtLocation));
                    cell.getDetailTextLabel().setText(dateFormatter.format(bestEffortAtLocation.getTimestamp()));
                    return cell;
                default:
                    /*
                     * The cells for the location rows use the cell style "UITableViewCellStyleSubtitle", which has a left-aligned
                     * label across the top and a left-aligned label below it in smaller gray text. The text label shows the
                     * coordinates for the location and the detail text label shows its timestamp.
                     */
                    final String OtherMeasurementsCellID = "OtherMeasurementsCellID";
                    cell = tableView.dequeueReusableCell(OtherMeasurementsCellID);
                    if (cell == null) {
                        cell = new UITableViewCell(UITableViewCellStyle.Subtitle, OtherMeasurementsCellID);
                        cell.setAccessoryType(UITableViewCellAccessoryType.DisclosureIndicator);
                    }
                    CLLocation location = locationMeasurements.get((int)NSIndexPathExtensions.getRow(indexPath));
                    cell.getTextLabel().setText(Str.getLocalizedCoordinateString(location));
                    cell.getDetailTextLabel().setText(dateFormatter.format(location.getTimestamp()));
                    return cell;
                }
            }
        });
        tableView.setDelegate(new UITableViewDelegateAdapter() {
            /** Delegate method invoked before the user selects a row. In this sample, we use it to prevent selection in the first
             * section of the table view. */
            @Override
            public NSIndexPath willSelectRow (UITableView tableView, NSIndexPath indexPath) {
                return (NSIndexPathExtensions.getSection(indexPath) == 0) ? null : indexPath;
            }

            /** Delegate method invoked after the user selects a row. Selecting a row containing a location object will navigate to
             * a new view controller displaying details about that location. */
            @Override
            public void didSelectRow (UITableView tableView, NSIndexPath indexPath) {
                tableView.deselectRow(indexPath, true);
                CLLocation location = locationMeasurements.get((int)NSIndexPathExtensions.getRow(indexPath));
                locationDetailViewController.setLocation(location);
                getNavigationController().pushViewController(locationDetailViewController, true);
            }
        });
    }

    /** The reset method allows the user to repeatedly test the location functionality. In addition to discarding all of the
     * location measurements from the previous "run", it animates a transition in the user interface between the table which
     * displays location data and the start button and description label presented at launch. */
    private void reset () {
        bestEffortAtLocation = null;
        locationMeasurements.clear();
        UIView.beginAnimations("Reset", null);
        UIView.setDurationForAnimation(0.6);
        startButton.setAlpha(1);
        descriptionLabel.setAlpha(1);
        tableView.setAlpha(0);
        getNavigationItem().setLeftBarButtonItem(null, true);
        UIView.commitAnimations();
    }

    /** This method is invoked when the user hits "Done" in the setup view controller. The options chosen by the user are passed in
     * as a map. The keys for this map are declared in SetupViewController. */
    private void finishSetup (SetupViewController controller, Map<String, Double> setupInfo) {
        startButton.setAlpha(0);
        descriptionLabel.setAlpha(0);
        tableView.setAlpha(1);
        // Create the manager object
        locationManager = new CLLocationManager();
        locationManager.setDelegate(new CLLocationManagerDelegateAdapter() {
            /** We want to get and store a location measurement that meets the desired accuracy. For this example, we are going to
             * use horizontal accuracy as the deciding factor. In other cases, you may wish to use vertical accuracy, or both
             * together. */
            @Override
            public void didUpdateToLocation (CLLocationManager manager, CLLocation newLocation, CLLocation oldLocation) {
                // store all of the measurements, just so we can see what kind of data we might receive
                locationMeasurements.add(newLocation);
                // test the age of the location measurement to determine if the measurement is cached
                // in most cases you will not want to rely on cached measurements
                double locationAge = -newLocation.getTimestamp().getTimeIntervalSinceNow();
                if (locationAge > 5) return;
                // test that the horizontal accuracy does not indicate an invalid measurement
                if (newLocation.getHorizontalAccuracy() < 0) return;
                // test the measurement to see if it is more accurate than the previous measurement
                if (bestEffortAtLocation == null
                    || bestEffortAtLocation.getHorizontalAccuracy() > newLocation.getHorizontalAccuracy()) {
                    // store the location as the "best effort"
                    bestEffortAtLocation = newLocation;
                    // test the measurement to see if it meets the desired accuracy
                    // IMPORTANT! CLLocationAccuracy.Best should not be used for comparison with location coordinate or altitude
                    // accuracy because it is a negative value. Instead, compare against some predetermined "real" measure of
                    // acceptable accuracy, or depend on the timeout to stop updating. This sample depends on the timeout.
                    //
                    if (newLocation.getHorizontalAccuracy() <= locationManager.getDesiredAccuracy()) {
                        // we have a measurement that meets our requirements, so we can stop updating the location
                        //
                        // IMPORTANT!!! Minimize power usage by stopping the location manager as soon as possible.
                        //
                        stopUpdatingLocation(Str.getLocalizedString("Acquired Location"));
                    }
                }
                // update the display with the new location data
                tableView.reloadData();
                // abort our timeout timer
                canTimeOut = false;
            }

            @Override
            public void didFail (CLLocationManager manager, NSError error) {
                // The location "unknown" error simply means the manager is currently unable to get the location.
                // We can ignore this error for the scenario of getting a single location fix, because we already have a
                // timeout that will stop the location manager to save power.
                if (error.getCode() != CLError.LocationUnknown.value()) {
                    stopUpdatingLocation(Str.getLocalizedString("Error"));
                }
            }
        });
        // This is the most important property to set for the manager. It ultimately determines how the manager will
        // attempt to acquire location and thus, the amount of power that will be consumed.
        locationManager.setDesiredAccuracy(setupInfo.get(SetupViewController.SETUP_INFO_KEY_ACCURACY));
        // Once configured, the location manager must be "started".
        locationManager.startUpdatingLocation();

        canTimeOut = true;
        DispatchQueue.after(
            Dispatch.time(Dispatch.TIME_NOW, (long)(setupInfo.get(SetupViewController.SETUP_INFO_KEY_TIMEOUT) * 1000000000)),
            DispatchQueue.getMainQueue(), new Runnable() {
                @Override
                public void run () {
                    if (canTimeOut) {
                        stopUpdatingLocation("Timed Out");
                    }
                }
            });
        stateString = Str.getLocalizedString("Updating");
        tableView.reloadData();
    }

    private void stopUpdatingLocation (String state) {
        stateString = state;
        tableView.reloadData();
        locationManager.stopUpdatingLocation();

        UIBarButtonItem resetItem = new UIBarButtonItem(Str.getLocalizedString("Reset"), UIBarButtonItemStyle.Bordered,
            new UIBarButtonItem.OnClickListener() {
                @Override
                public void onClick (UIBarButtonItem barButtonItem) {
                    reset();
                }
            });
        getNavigationItem().setLeftBarButtonItem(resetItem, true);
    }
}
