/*
 * Copyright (C) 2016 RoboVM AB
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
package org.robovm.samples.robopods.reachability.ios.ui;

import org.robovm.apple.systemconfiguration.SCNetworkReachabilityFlags;
import org.robovm.apple.uikit.*;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;
import org.robovm.pods.Log;
import org.robovm.pods.Log.LogLevel;
import org.robovm.pods.Platform;
import org.robovm.pods.reachability.NetworkReachability;
import org.robovm.pods.reachability.NetworkReachabilityListener;
import org.robovm.pods.reachability.NetworkStatus;

@CustomClass("MyViewController")
public class MyViewController extends UIViewController implements NetworkReachabilityListener {
    @IBOutlet
    private UILabel summaryLabel;
    @IBOutlet
    private UITextField remoteHostLabel;
    @IBOutlet
    private UIImageView remoteHostImageView;
    @IBOutlet
    private UITextField remoteHostStatusField;
    @IBOutlet
    private UIImageView internetConnectionImageView;
    @IBOutlet
    private UITextField internetConnectionStatusField;
    @IBOutlet
    private UIImageView localWiFiConnectionImageView;
    @IBOutlet
    private UITextField localWiFiConnectionStatusField;

    private NetworkReachability hostReachability;
    private NetworkReachability internetReachability;
    private NetworkReachability wifiReachability;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        // Enable debug logging.
        Log.setLogLevel(LogLevel.DEBUG);

        summaryLabel.setHidden(true);

        String remoteHostName = "www.apple.com";

        remoteHostLabel.setText(String.format("Remote host: %s", remoteHostName));

        hostReachability = NetworkReachability.forHostname(remoteHostName);
        hostReachability.setListener(this);
        hostReachability.startNotifier();
        updateInterfaceForReachability(hostReachability);

        internetReachability = NetworkReachability.forInternetConnection();
        internetReachability.setListener(this);
        internetReachability.startNotifier();
        updateInterfaceForReachability(internetReachability);

        wifiReachability = NetworkReachability.forLocalWiFi();
        wifiReachability.setListener(this);
        wifiReachability.startNotifier();
        updateInterfaceForReachability(wifiReachability);
    }

    private void updateInterfaceForReachability(NetworkReachability reachability) {
        Platform.getPlatform().runOnUIThread(() -> {
            if (reachability == hostReachability) {
                configureTextField(remoteHostStatusField, remoteHostImageView, reachability);
                NetworkStatus netStatus = reachability.getCurrentReachabilityStatus();
                boolean connectionRequired = reachability.isConnectionRequired();

                summaryLabel.setHidden(netStatus != NetworkStatus.ReachableViaWWAN);

                String baseLabelText = "";
                if (connectionRequired) {
                    baseLabelText = "Cellular data network is available.\nInternet traffic will be routed through it after a connection is established.";
                } else {
                    baseLabelText = "Cellular data network is active.\nInternet traffic will be routed through it.";
                }
                summaryLabel.setText(baseLabelText);
            } else if (reachability == internetReachability) {
                configureTextField(internetConnectionStatusField, internetConnectionImageView, reachability);
            } else if (reachability == wifiReachability) {
                configureTextField(localWiFiConnectionStatusField, localWiFiConnectionImageView, reachability);
            }
        });
    }

    private void configureTextField(UITextField textField, UIImageView imageView, NetworkReachability reachability) {
        NetworkStatus netStatus = reachability.getCurrentReachabilityStatus();
        boolean connectionRequired = reachability.isConnectionRequired();

        String statusString = "";

        switch (netStatus) {
        case NotReachable:
            statusString = "Access Not Available";
            imageView.setImage(UIImage.getImage("stop-32.png"));

            connectionRequired = false;
            break;
        case ReachableViaWWAN:
            statusString = "Reachable WWAN";
            imageView.setImage(UIImage.getImage("WWAN5.png"));
            break;
        case ReachableViaWiFi:
            statusString = "Reachable WiFi";
            imageView.setImage(UIImage.getImage("Airport.png"));
            break;
        }

        if (connectionRequired) {
            statusString = String.format("%s, Connection Required", statusString);
        }
        textField.setText(statusString);
    }

    @Override
    public void onReachable(NetworkReachability networkReachability) {
        Log.log("Reachable: " + networkReachability);
    }

    @Override
    public void onUnreachable(NetworkReachability networkReachability) {
        Log.log("Unreachable: " + networkReachability);
    }

    @Override
    public void onChange(NetworkReachability networkReachability,
            SCNetworkReachabilityFlags scNetworkReachabilityFlags) {
        updateInterfaceForReachability(networkReachability);
    }
}
