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

import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.pods.dialog.AlertDialog;
import org.robovm.pods.heyzap.ads.HZAdsDelegateAdapter;
import org.robovm.pods.heyzap.ads.HZIncentivizedAd;
import org.robovm.pods.heyzap.ads.HZIncentivizedAdDelegateAdapter;
import org.robovm.pods.heyzap.ads.HZInterstitialAd;
import org.robovm.pods.heyzap.ads.HZVideoAd;
import org.robovm.pods.heyzap.ads.HeyzapAds;

@CustomClass("MainMenuViewController")
public class MainMenuViewController extends UITableViewController {

    public MainMenuViewController() {
        // Set various delegates for your ads.
        HZInterstitialAd.setDelegate(new HZAdsDelegateAdapter() {
            @Override
            public void didShowAd(String tag) {}

            @Override
            public void didFailToShowAd(String tag, NSError error) {}
        });
        HZVideoAd.setDelegate(new HZAdsDelegateAdapter() {
            @Override
            public void didShowAd(String tag) {}
        });
        HZIncentivizedAd.setDelegate(new HZIncentivizedAdDelegateAdapter() {
            @Override
            public void didCompleteAd(String tag) {
                new AlertDialog.Builder("Yeah!", "Here is your reward!", "Thanks").show();
            }

            @Override
            public void didFailToCompleteAd(String tag) {
                new AlertDialog.Builder("Not completed!", "Ad was not completed.", "OK").show();
            }
        });
    }

    @Override
    public void didSelectRow(UITableView tableView, NSIndexPath indexPath) {
        if (indexPath.getSection() == 0) {
            switch (indexPath.getRow()) {
            // Row 0 already has a segue in the storyboard.
            case 1: // Interstitial
                showInterstitial();
                break;
            case 2: // Video
                showVideo();
                break;
            case 3: // Rewarded Video
                showRewardedVideo();
                break;
            case 4: // Test integration
                testIntegration();
                break;
            default:
                break;
            }
        }

        tableView.deselectRow(indexPath, true);
    }

    private void showInterstitial() {
        // Check if the ad is ready. If so, display the ad.
        if (HZInterstitialAd.isAvailable()) {
            HZInterstitialAd.show();
        } else {
            // By default interstitial ads are prefetched, so this should not
            // happen unless there is no connectivity.
            HZInterstitialAd.fetch();
            new AlertDialog.Builder("No ad ready", "Please wait while a new ad is requested.", "OK").show();
        }
    }

    private void showVideo() {
        // Check if the ad is ready. If so, display the ad.
        if (HZVideoAd.isAvailable()) {
            HZVideoAd.show();
        } else {
            HZVideoAd.fetch();
            new AlertDialog.Builder("No ad ready", "Please wait while a new ad is requested.", "OK").show();
        }
    }

    private void showRewardedVideo() {
        // Check if the ad is ready. If so, display the ad.
        if (HZIncentivizedAd.isAvailable()) {
            HZIncentivizedAd.show();
        } else {
            HZIncentivizedAd.fetch();
            new AlertDialog.Builder("No ad ready", "Please wait while a new ad is requested.", "OK").show();
        }
    }

    private void testIntegration() {
        HeyzapAds.presentMediationDebugViewController();
    }

    @Override
    public void viewWillAppear(boolean animated) {
        getTableView().reloadData();
    }
}
