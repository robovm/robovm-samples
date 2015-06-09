package org.robovm.samples.robopods.facebook.ios;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.foundation.NSPropertyList;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.pods.facebook.core.FBSDKAppEvents;
import org.robovm.pods.facebook.core.FBSDKApplicationDelegate;

public class FacebookApp extends UIApplicationDelegateAdapter {

    @Override
    public boolean didFinishLaunching(UIApplication application, UIApplicationLaunchOptions launchOptions) {
        FBSDKApplicationDelegate.getSharedInstance().didFinishLaunching(application, launchOptions);
        return true;
    }

    @Override
    public boolean openURL(UIApplication application, NSURL url, String sourceApplication, NSPropertyList annotation) {
        return FBSDKApplicationDelegate.getSharedInstance().openURL(application, url, sourceApplication, annotation);
    }

    @Override
    public void didBecomeActive(UIApplication application) {
        FBSDKAppEvents.activateApp();
    }

    public static void main(String[] args) {
        try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
            UIApplication.main(args, null, FacebookApp.class);
        }
    }
}
