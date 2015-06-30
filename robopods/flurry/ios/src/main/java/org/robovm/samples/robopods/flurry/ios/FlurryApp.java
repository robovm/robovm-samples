package org.robovm.samples.robopods.flurry.ios;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UINavigationController;
import org.robovm.pods.flurry.analytics.Flurry;
import org.robovm.pods.flurry.analytics.FlurryLogLevel;

public class FlurryApp extends UIApplicationDelegateAdapter {
    private static final String APP_KEY = "HXZMM38WG7K7HQFCKRJS";

    @Override
    public boolean didFinishLaunching(UIApplication application, UIApplicationLaunchOptions launchOptions) {
        // Remove the below lines to disable logging for release builds.
        Flurry.setDebugLogEnabled(true);
        Flurry.setLogLevel(FlurryLogLevel.All);

        // Enable crash reporting.
        Flurry.enableCrashReporting();

        // Start Flurry.
        Flurry.startSession(APP_KEY, launchOptions);

        // Automatically log page views for all controllers in the view
        // hierarchy of the root controller.
        UINavigationController navController = (UINavigationController) getWindow().getRootViewController();
        Flurry.logAllPageViews(navController);

        return true;
    }

    public static void main(String[] args) {
        try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
            UIApplication.main(args, null, FlurryApp.class);
        }
    }
}
