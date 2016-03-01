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
 * Portions of this code is based on Google Inc's Google Mobile Ads sample
 * which is copyright (C) 2015 Google Inc.
 */
package org.robovm.samples.robopods.google.mobileads.ios.ui;

import java.util.Arrays;

import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import org.robovm.objc.annotation.IBOutlet;
import org.robovm.pods.google.mobileads.GADBannerView;
import org.robovm.pods.google.mobileads.GADInterstitial;
import org.robovm.pods.google.mobileads.GADInterstitialDelegateAdapter;
import org.robovm.pods.google.mobileads.GADRequest;

@CustomClass("ViewController")
public class ViewController extends UIViewController {
    private GADBannerView bannerView;
    private GADInterstitial interstitial;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        bannerView.setAdUnitID("ca-app-pub-3940256099942544/2934735716");
        bannerView.setRootViewController(this);
        bannerView.loadRequest(createRequest());

        interstitial = createAndLoadInterstitial();
    }

    private GADInterstitial createAndLoadInterstitial() {
        GADInterstitial interstitial = new GADInterstitial("ca-app-pub-3940256099942544/4411468910");
        interstitial.setDelegate(new GADInterstitialDelegateAdapter() {
            @Override
            public void didDismissScreen(GADInterstitial ad) {
                ViewController.this.interstitial = createAndLoadInterstitial();
            }
        });
        interstitial.loadRequest(createRequest());
        return interstitial;
    }

    private GADRequest createRequest() {
        GADRequest request = new GADRequest();
        // To test on your devices, add their UDIDs here:
        request.setTestDevices(Arrays.asList(GADRequest.getSimulatorID()));
        return request;
    }

    @IBAction
    private void didTapInterstitialButton(UIButton sender) {
        if (interstitial.isReady()) {
            interstitial.present(this);
        } else {
            System.out.println("Interstitial not ready!");
        }
    }

    @IBOutlet
    private void setBannerView(GADBannerView bannerView) {
        this.bannerView = bannerView;
    }
}
