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
