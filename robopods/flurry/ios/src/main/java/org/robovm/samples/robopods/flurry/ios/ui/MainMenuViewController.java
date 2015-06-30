/*
 * Copyright (C) 2013-2015 RoboVM AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robovm.samples.robopods.flurry.ios.ui;

import java.util.HashMap;
import java.util.Map;

import org.robovm.apple.corelocation.CLAuthorizationStatus;
import org.robovm.apple.corelocation.CLLocation;
import org.robovm.apple.corelocation.CLLocationManager;
import org.robovm.apple.corelocation.CLLocationManagerDelegateAdapter;
import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSMutableDictionary;
import org.robovm.apple.uikit.UIAlertView;
import org.robovm.apple.uikit.UIAlertViewDelegateAdapter;
import org.robovm.apple.uikit.UIAlertViewStyle;
import org.robovm.apple.uikit.UIKeyboardType;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.apple.uikit.UITextField;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.pods.flurry.analytics.Flurry;

@CustomClass("MainMenuViewController")
public class MainMenuViewController extends UITableViewController {

    @Override
    public void didSelectRow(UITableView tableView, NSIndexPath indexPath) {
        if (indexPath.getSection() == 0) {
            switch (indexPath.getRow()) {
            case 0:
                logEventAction();
                break;
            case 1:
                logEventWithParametersAction();
                break;
            case 2:
                logTimedEventAction();
                break;
            case 3:
                logErrorAction();
                break;
            case 4:
                setUserDataAction();
                break;
            case 5:
                setLocationAction();
                break;
            default:
                break;
            }
        } else {
            switch (indexPath.getRow()) {
            case 0:

                break;
            default:
                break;
            }
        }

        tableView.deselectRow(indexPath, true);
    }

    private void logEventAction() {
        UIAlertView alert = new UIAlertView("Log Event", "Select an event to send:", new UIAlertViewDelegateAdapter() {
            @Override
            public void clicked(UIAlertView alertView, long buttonIndex) {
                switch ((int) buttonIndex) {
                case 1:
                    Flurry.logEvent("Event_A");
                    break;
                case 2:
                    Flurry.logEvent("Event_B");
                    break;
                default:
                    break;
                }
            }
        }, "Cancel", "Event A", "Event B");
        alert.show();
    }

    private void logEventWithParametersAction() {
        UIAlertView alert = new UIAlertView("Log Event with Parameters", "Select parameters:",
                new UIAlertViewDelegateAdapter() {
                    @Override
                    public void clicked(UIAlertView alertView, long buttonIndex) {
                        final String eventName = "Event_with_Parameters";
                        switch ((int) buttonIndex) {
                        case 1:
                            Map<String, String> params1 = new HashMap<>();
                            params1.put("Param1", String.valueOf(101));
                            Flurry.logEvent(eventName, params1);
                            break;
                        case 2:
                            NSDictionary<?, ?> params2 = new NSMutableDictionary<>();
                            params2.put("Param1", "Test");
                            params2.put("Param2", 202);
                            Flurry.logEvent(eventName, params2);
                            break;
                        default:
                            break;
                        }
                    }
                }, "Cancel", "Param1 = 101", "Param1 = Test, Param2 = 202");
        alert.show();
    }

    private void logTimedEventAction() {
        UIAlertView alert = new UIAlertView("Log Timed Event", "Press OK to start the timed event.",
                new UIAlertViewDelegateAdapter() {
                    @Override
                    public void clicked(UIAlertView alertView, long buttonIndex) {
                        final String eventName = "Event_Timed";
                        if (buttonIndex == 1) {
                            Flurry.logEvent(eventName, true);
                            UIAlertView stopAlert = new UIAlertView("Stop Timed Event",
                                    "Press OK to stop the timed event.", new UIAlertViewDelegateAdapter() {
                                        @Override
                                        public void willDismiss(UIAlertView alertView, long buttonIndex) {
                                            Flurry.endTimedEvent(eventName);
                                        }
                                    }, "OK");
                            stopAlert.show();
                        }
                    }
                }, "Cancel", "OK");
        alert.show();
    }

    private void logErrorAction() {
        UIAlertView alert = new UIAlertView("Log Error", "Press OK to log an exception.",
                new UIAlertViewDelegateAdapter() {
                    @Override
                    public void clicked(UIAlertView alertView, long buttonIndex) {
                        if (buttonIndex == 1) {
                            try {
                                // Let's produce some silly error.
                                Integer.valueOf("Error");
                            } catch (NumberFormatException e) {
                                Flurry.logError(e);
                            }
                        }
                    }
                }, "Cancel", "OK");
        alert.show();
    }

    private void setUserDataAction() {
        setUserID();
    }

    private void setUserID() {
        UIAlertView alert = new UIAlertView("Set User ID", "", new UIAlertViewDelegateAdapter() {
            @Override
            public void clicked(UIAlertView alertView, long buttonIndex) {
                if (buttonIndex == 1) {
                    UITextField textField = alertView.getTextField(0);
                    if (textField.getText() != null && !textField.getText().isEmpty()) {
                        String userID = textField.getText();
                        Flurry.setUserID(userID);
                    }

                    setUserAge();
                }
            }
        }, "Cancel", "OK");
        alert.setAlertViewStyle(UIAlertViewStyle.PlainTextInput);
        alert.show();
    }

    private void setUserAge() {
        UIAlertView alert = new UIAlertView("Set User Age", "", new UIAlertViewDelegateAdapter() {
            @Override
            public void clicked(UIAlertView alertView, long buttonIndex) {
                if (buttonIndex == 1) {
                    UITextField textField = alertView.getTextField(0);
                    if (textField.getText() != null && !textField.getText().isEmpty()) {
                        int age = Integer.valueOf(textField.getText());
                        Flurry.setAge(age);
                    }

                    setUserGender();
                }
            }
        }, "Cancel", "OK");
        alert.setAlertViewStyle(UIAlertViewStyle.PlainTextInput);
        alert.getTextField(0).setKeyboardType(UIKeyboardType.NumberPad);
        alert.show();
    }

    private void setUserGender() {
        UIAlertView alert = new UIAlertView("Set User Gender", "", new UIAlertViewDelegateAdapter() {
            @Override
            public void clicked(UIAlertView alertView, long buttonIndex) {
                if (buttonIndex == 1) {
                    Flurry.setGender("m");
                } else if (buttonIndex == 2) {
                    Flurry.setGender("f");
                }
            }
        }, "Cancel", "Male", "Female");
        alert.show();
    }

    private void setLocationAction() {
        CLLocationManager locationManager = new CLLocationManager();
        locationManager.setDelegate(new CLLocationManagerDelegateAdapter() {
            @Override
            public void didUpdateLocations(CLLocationManager manager, NSArray<CLLocation> locations) {
                if (locations != null && locations.size() > 0) {
                    CLLocation location = locations.first();
                    Flurry.setLocation(location.getCoordinate().getLatitude(), location.getCoordinate().getLongitude(),
                            location.getHorizontalAccuracy(), location.getVerticalAccuracy());

                    manager.stopUpdatingLocation();

                    UIAlertView alert = new UIAlertView("Location received!", "", null, "OK");
                    alert.show();
                }
            }

            @SuppressWarnings("deprecation")
            @Override
            public void didChangeAuthorizationStatus(CLLocationManager manager, CLAuthorizationStatus status) {
                if (status == CLAuthorizationStatus.Authorized || status == CLAuthorizationStatus.AuthorizedAlways
                        || status == CLAuthorizationStatus.AuthorizedWhenInUse) {
                    manager.startUpdatingLocation();
                }
            }
        });
        if (Foundation.getMajorSystemVersion() >= 8
                && CLLocationManager.getAuthorizationStatus() != CLAuthorizationStatus.AuthorizedWhenInUse) {
            locationManager.requestWhenInUseAuthorization();
        } else {
            locationManager.startUpdatingLocation();
        }
    }

    @Override
    public void viewWillAppear(boolean animated) {
        getTableView().reloadData();
    }
}
