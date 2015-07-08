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
 * Portions of this code is based on Chartboost's iOS SDK sample
 * which is copyright (C) 2013 Chartboost.
 */
package org.robovm.samples.robopods.chartboost.ios;

import org.robovm.apple.adsupport.ASIdentifierManager;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.pods.chartboost.CBLoadError;
import org.robovm.pods.chartboost.CBLocation;
import org.robovm.pods.chartboost.Chartboost;
import org.robovm.pods.chartboost.ChartboostDelegateAdapter;
import org.robovm.samples.robopods.chartboost.ios.ui.ViewController;

public class ChartboostApp extends UIApplicationDelegateAdapter {

    @Override
    public boolean didFinishLaunching(UIApplication application, UIApplicationLaunchOptions launchOptions) {
        // Begin a user session. Must not be dependent on user actions or any
        // prior network requests.
        Chartboost.start("4f21c409cd1cb2fb7000001b", "92e2de2fd7070327bdeb54c15a5295309c6fcd2d",
                new ChartboostDelegateAdapter() {
                    /**
                     * This is used to control when an interstitial should or
                     * should not be displayed The default is true, and that
                     * will let an interstitial display as normal If it's not
                     * okay to display an interstitial, return false.
                     *
                     * For example: during gameplay, return false.
                     *
                     * Is fired on: -Interstitial is loaded & ready to display
                     */
                    @Override
                    public boolean shouldDisplayInterstitial(String location) {
                        System.out.println("about to display interstitial at location " + location);

                        // For example:
                        // if the user has left the main menu and is currently
                        // playing your game, return false;

                        // Otherwise return true to display the interstitial
                        return true;
                    }

                    /**
                     * This is called when an interstitial has failed to load.
                     * The error enum specifies the reason of the failure
                     */
                    @Override
                    public void didFailToLoadInterstitial(String location, CBLoadError error) {
                        switch (error) {
                        case InternetUnavailable:
                            System.out.println("Failed to load Interstitial, no Internet connection !");
                            break;
                        case Internal:
                            System.out.println("Failed to load Interstitial, internal error !");
                            break;
                        case NetworkFailure:
                            System.out.println("Failed to load Interstitial, network error !");
                            break;
                        case WrongOrientation:
                            System.out.println("Failed to load Interstitial, wrong orientation !");
                            break;
                        case TooManyConnections:
                            System.out.println("Failed to load Interstitial, too many connections !");
                            break;
                        case FirstSessionInterstitialsDisabled:
                            System.out.println("Failed to load Interstitial, first session !");
                            break;
                        case NoAdFound:
                            System.out.println("Failed to load Interstitial, no ad found !");
                            break;
                        case SessionNotStarted:
                            System.out.println("Failed to load Interstitial, session not started !");
                            break;
                        case NoLocationFound:
                            System.out.println("Failed to load Interstitial, missing location parameter !");
                            break;
                        default:
                            System.out.println("Failed to load Interstitial, unknown error !");
                            break;
                        }
                    }

                    /**
                     * Passes in the location name that has successfully been
                     * cached.
                     *
                     * Is fired on: - All assets loaded - Triggered by
                     * cacheInterstitial
                     *
                     * Notes: - Similar to this is:
                     * (BOOL)hasCachedInterstitial:(NSString *)location; Which
                     * will return true if a cached interstitial exists for that
                     * location
                     */
                    @Override
                    public void didCacheInterstitial(String location) {
                        System.out.println("interstitial cached at location " + location);
                    }

                    /**
                     * This is called when the more apps page has failed to load
                     * for any reason
                     *
                     * Is fired on: - No network connection - No more apps page
                     * has been created (add a more apps page in the dashboard)
                     * - No publishing campaign matches for that user (add more
                     * campaigns to your more apps page) -Find this inside the
                     * App > Edit page in the Chartboost dashboard
                     */
                    @Override
                    public void didFailToLoadMoreApps(CBLoadError error) {
                        switch (error) {
                        case InternetUnavailable:
                            System.out.println("Failed to load More Apps, no Internet connection !");
                            break;
                        case Internal:
                            System.out.println("Failed to load More Apps, internal error !");
                            break;
                        case NetworkFailure:
                            System.out.println("Failed to load More Apps, network error !");
                            break;
                        case WrongOrientation:
                            System.out.println("Failed to load More Apps, wrong orientation !");
                            break;
                        case TooManyConnections:
                            System.out.println("Failed to load More Apps, too many connections !");
                            break;
                        case FirstSessionInterstitialsDisabled:
                            System.out.println("Failed to load More Apps, first session !");
                            break;
                        case NoAdFound:
                            System.out.println("Failed to load More Apps, no ad found !");
                            break;
                        case SessionNotStarted:
                            System.out.println("Failed to load More Apps, session not started !");
                            break;
                        case NoLocationFound:
                            System.out.println("Failed to load More Apps, missing location parameter !");
                            break;
                        default:
                            System.out.println("Failed to load More Apps, unknown error !");
                            break;
                        }
                    }

                    /**
                     * This is called when an interstitial is dismissed
                     *
                     * Is fired on: - Interstitial click - Interstitial close
                     *
                     */
                    @Override
                    public void didDismissInterstitial(String location) {
                        System.out.println("dismissed interstitial at location " + location);
                    }

                    /**
                     * This is called when the more apps page is dismissed
                     *
                     * Is fired on: - More Apps click - More Apps close
                     *
                     */
                    @Override
                    public void didDismissMoreApps(String location) {
                        System.out.println("dismissed more apps page at location " + location);
                    }

                    /**
                     * This is called when a rewarded video has been viewed
                     *
                     * Is fired on: - Rewarded video completed view
                     *
                     */
                    @Override
                    public void didCompleteRewardedVideo(String location, int reward) {
                        System.out.println(String.format(
                                "completed rewarded video view at location %s with reward amount %d", location, reward));
                    }

                    /*
                     * 
                     * This is called when a Rewarded Video has failed to load.
                     * The error enum specifies the reason of the failure
                     */
                    @Override
                    public void didFailToLoadRewardedVideo(String location, CBLoadError error) {
                        switch (error) {
                        case InternetUnavailable:
                            System.out.println("Failed to load Rewarded Video, no Internet connection !");
                            break;
                        case Internal:
                            System.out.println("Failed to load Rewarded Video, internal error !");
                            break;
                        case NetworkFailure:
                            System.out.println("Failed to load Rewarded Video, network error !");
                            break;
                        case WrongOrientation:
                            System.out.println("Failed to load Rewarded Video, wrong orientation !");
                            break;
                        case TooManyConnections:
                            System.out.println("Failed to load Rewarded Video, too many connections !");
                            break;
                        case FirstSessionInterstitialsDisabled:
                            System.out.println("Failed to load Rewarded Video, first session !");
                            break;
                        case NoAdFound:
                            System.out.println("Failed to load Rewarded Video, no ad found !");
                            break;
                        case SessionNotStarted:
                            System.out.println("Failed to load Rewarded Video, session not started !");
                            break;
                        case NoLocationFound:
                            System.out.println("Failed to load Rewarded Video, missing location parameter !");
                            break;
                        default:
                            System.out.println("Failed to load Rewarded Video, unknown error !");
                            break;
                        }
                    }

                    /**
                     * Called after an interstitial has been displayed on the
                     * screen.
                     */
                    @Override
                    public void didDisplayInterstitial(String location) {
                        System.out.println("Did display interstitial");

// We might want to pause our in-game audio, lets double check that an ad is visible
                        if (Chartboost.isAnyViewVisible()) {
                            // Use this function anywhere in your logic where
                            // you need to know if an ad is visible or not.
                            System.out.println("Pause audio");
                        }
                    }

                    /**
                     * Called after an InPlay object has been loaded from the
                     * Chartboost API servers and cached locally.
                     * 
                     * Implement to be notified of when an InPlay object has
                     * been loaded from the Chartboost API servers and cached
                     * locally for a given CBLocation.
                     * 
                     * @param location The location for the Chartboost
                     *            impression type.
                     */
                    @Override
                    public void didCacheInPlay(String location) {
                        System.out.println("Successfully cached inPlay");
                        ViewController vc = (ViewController) getWindow().getRootViewController();
                        vc.renderInPlay(Chartboost.getInPlay(location));
                    }

                    /**
                     * Called after a InPlay has attempted to load from the
                     * Chartboost API servers but failed.
                     * 
                     * Implement to be notified of when an InPlay has attempted
                     * to load from the Chartboost API servers but failed for a
                     * given CBLocation.
                     * 
                     * @param location The location for the Chartboost
                     *            impression type.
                     * @param error The reason for the error defined via a
                     *            CBLoadError.
                     */
                    @Override
                    public void didFailToLoadInPlay(String location, CBLoadError error) {
                        String errorString = "";
                        switch (error) {
                        case InternetUnavailable:
                            errorString = "Failed to load In Play, no Internet connection !";
                            break;
                        case Internal:
                            errorString = "Failed to load In Play, internal error !";
                            break;
                        case NetworkFailure:
                            errorString = "Failed to load In Play, network error !";
                            break;
                        case WrongOrientation:
                            errorString = "Failed to load In Play, wrong orientation !";
                            break;
                        case TooManyConnections:
                            errorString = "Failed to load In Play, too many connections !";
                            break;
                        case FirstSessionInterstitialsDisabled:
                            errorString = "Failed to load In Play, first session !";
                            break;
                        case NoAdFound:
                            errorString = "Failed to load In Play, no ad found !";
                            break;
                        case SessionNotStarted:
                            errorString = "Failed to load In Play, session not started !";
                            break;
                        case NoLocationFound:
                            errorString = "Failed to load In Play, missing location parameter !";
                            break;
                        default:
                            errorString = "Failed to load In Play, unknown error !";
                            break;
                        }

                        System.out.println(errorString);

                        ViewController vc = (ViewController) getWindow().getRootViewController();
                        vc.renderInPlayError(errorString);
                    }
                });

        Chartboost.cacheRewardedVideo(CBLocation.MainMenu);
        Chartboost.cacheMoreApps(CBLocation.HomeScreen);

        return true;
    }

    @Override
    public void didBecomeActive(UIApplication application) {
        // Print IFA (Identifier for Advertising) in Output section. Add to
        // applicationDidBecomeActive. iOS 6+ devices only.
        String ifa = ASIdentifierManager.getSharedManager().getAdvertisingIdentifier().asString();
        if (ifa != null) {
            System.out.println(String.format("IFA: %s", ifa.replace("-", "").toLowerCase()));
        } else {
            System.out.println("IFA: No IFA returned from device");
        }

        // Show an interstitial whenever the app starts up
        Chartboost.showInterstitial(CBLocation.HomeScreen);
    }

    public static void main(String[] args) {
        try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
            UIApplication.main(args, null, ChartboostApp.class);
        }
    }
}
