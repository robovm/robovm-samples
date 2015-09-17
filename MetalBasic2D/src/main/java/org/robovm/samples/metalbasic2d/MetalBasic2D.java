package org.robovm.samples.metalbasic2d;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIWindow;

public class MetalBasic2D extends UIApplicationDelegateAdapter {
    private UIWindow window;
    private MetalBasic2DViewController rootViewController;

    @Override
    public boolean didFinishLaunching(UIApplication application, UIApplicationLaunchOptions launchOptions) {
        // Set up the view controller.
        rootViewController = new MetalBasic2DViewController();

        // Create a new window at screen size.
        window = new UIWindow(UIScreen.getMainScreen().getBounds());
        // Set the view controller as the root controller for the window.
        window.setRootViewController(rootViewController);
        // Make the window visible.
        window.makeKeyAndVisible();

        return true;
    }

    public static void main(String[] args) {
        try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
            UIApplication.main(args, null, MetalBasic2D.class);
        }
    }
}
