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
package org.robovm.samples.robopods.heyzap.ios.ui;

import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.pods.heyzap.ads.HZBannerAd;
import org.robovm.pods.heyzap.ads.HZBannerAdOptions;
import org.robovm.pods.heyzap.ads.HZBannerPosition;

@CustomClass("BannerViewController")
public class BannerViewController extends UIViewController {
    private HZBannerAd banner;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        HZBannerAdOptions options = new HZBannerAdOptions();
        HZBannerAd.placeBannerInView(getView(), HZBannerPosition.Bottom, options, banner -> {
            // The banner is a UIView, so you can move it, animate it, etc...
            this.banner = banner;
            System.out.println("Banner added to view.");
        } , error -> {
            System.err.println("Banner could not be added: " + error);
        });
    }
}
