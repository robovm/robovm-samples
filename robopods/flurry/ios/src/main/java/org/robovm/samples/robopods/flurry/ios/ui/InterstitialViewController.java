package org.robovm.samples.robopods.flurry.ios.ui;

import org.robovm.apple.foundation.NSError;
import org.robovm.apple.uikit.UIAlertView;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import org.robovm.pods.flurry.ads.FlurryAdError;
import org.robovm.pods.flurry.ads.FlurryAdInterstitial;
import org.robovm.pods.flurry.ads.FlurryAdInterstitialDelegateAdapter;
import org.robovm.pods.flurry.ads.FlurryAdTargeting;

@CustomClass("InterstitialViewController")
public class InterstitialViewController extends UIViewController {
    private static final String AD_SPACE_NAME = "Interstitial Test";

    private FlurryAdInterstitial adInterstitial;

    @Override
    public void viewDidAppear(boolean animated) {
        super.viewDidAppear(animated);

        FlurryAdTargeting targeting = new FlurryAdTargeting();
        targeting.setTestAdsEnabled(true);

        // Fetch fullscreen ads early when a later display is likely. For
        // example, at the beginning of a level.
        adInterstitial = new FlurryAdInterstitial(AD_SPACE_NAME);
        adInterstitial.setTargeting(targeting);
        adInterstitial.setAdDelegate(new FlurryAdInterstitialDelegateAdapter() {
            @Override
            public void didFetchAd(FlurryAdInterstitial interstitialAd) {
                // You can choose to present the ad as soon as it is received.
                // adInterstitial.present(InterstitialViewController.this);
            }

            @Override
            public void didDismiss(FlurryAdInterstitial interstitialAd) {
                // Fetch a new ad.
                adInterstitial.fetchAd();
            }

            @Override
            public void didFail(FlurryAdInterstitial interstitialAd, FlurryAdError adError, NSError errorDescription) {
                // Fetch a new ad.
                adInterstitial.fetchAd();
            }
        });

        adInterstitial.fetchAd();
    }

    @Override
    public void viewDidDisappear(boolean animated) {
        super.viewDidDisappear(animated);
        // Do not set ad delegate to null and
        // Do not remove ad in the viewWillDisappear or viewDidDisappear method
    }

    /**
     * Invoke a takeover at a natural pause in your app. For example, when a
     * level is completed, an article is read or a button is pressed. We will
     * mock the display of a takeover when a button is pressed.
     */
    @IBAction
    private void showFullScreenAd(UIButton sender) {
        // Check if the ad is ready. If so, display the ad.
        if (adInterstitial.isReady()) {
            adInterstitial.present(this);
        } else {
            adInterstitial.fetchAd();

            UIAlertView alert = new UIAlertView("No ad ready", "Please wait while a new ad is requested.", null, "OK");
            alert.show();
        }
    }
}
