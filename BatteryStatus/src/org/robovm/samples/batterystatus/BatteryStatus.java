
package org.robovm.samples.batterystatus;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UINavigationController;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIWindow;
import org.robovm.samples.batterystatus.viewcontrollers.BatStatViewController;

public class BatteryStatus extends UIApplicationDelegateAdapter {
    private UIWindow window;
    private BatStatViewController batStatViewController;
    private UINavigationController navigationController;

    @Override
    public boolean didFinishLaunching (UIApplication application, UIApplicationLaunchOptions launchOptions) {
        // Set up the view controller.
        batStatViewController = new BatStatViewController();
        navigationController = new UINavigationController(batStatViewController);

        // Create a new window at screen size.
        window = new UIWindow(UIScreen.getMainScreen().getBounds());
        // Set our viewcontroller as the root controller for the window.
        window.setRootViewController(navigationController);
        // Make the window visible.
        window.makeKeyAndVisible();

        /*
         * Retains the window object until the application is deallocated. Prevents Java GC from collecting the window object too
         * early.
         */
        addStrongRef(window);
        return true;
    }

    public static void main (String[] args) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(args, null, BatteryStatus.class);
        pool.close();
    }
}
