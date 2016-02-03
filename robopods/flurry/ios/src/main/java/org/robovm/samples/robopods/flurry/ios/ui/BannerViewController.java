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
package org.robovm.samples.robopods.flurry.ios.ui;

import org.robovm.apple.foundation.NSError;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.pods.flurry.ads.FlurryAdBanner;
import org.robovm.pods.flurry.ads.FlurryAdBannerDelegateAdapter;
import org.robovm.pods.flurry.ads.FlurryAdError;
import org.robovm.pods.flurry.ads.FlurryAdTargeting;

@CustomClass("BannerViewController")
public class BannerViewController extends UIViewController {
    private static final String AD_SPACE_NAME = "Banner Test";

    private FlurryAdBanner adBanner;

    @Override
    public void viewDidAppear(boolean animated) {
        super.viewDidAppear(animated);

        FlurryAdTargeting targeting = new FlurryAdTargeting();
        targeting.setTestAdsEnabled(true);

        // Fetch and display banner ad for a given ad space. Note: Choose an
        // ad space name that will uniquely identify the ad's placement within
        // your app.
        adBanner = new FlurryAdBanner(AD_SPACE_NAME);
        adBanner.setTargeting(targeting);
        adBanner.setAdDelegate(new FlurryAdBannerDelegateAdapter() {
            @Override
            public void didFetchAd(FlurryAdBanner bannerAd) {
                adBanner.displayAd(getView(), BannerViewController.this);
            }

            @Override
            public void didFail(FlurryAdBanner bannerAd, FlurryAdError adError, NSError errorDescription) {
                // Fetch a new ad.
                adBanner.fetchAd(getView().getFrame());
            }
        });
        adBanner.fetchAd(getView().getFrame());
    }
}
