
package org.robovm.samples.helloworld;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIViewContentMode;
import org.robovm.apple.uikit.UIWindow;
import org.robovm.samples.helloworld.viewcontrollers.MyViewController;

public class HelloWorld extends UIApplicationDelegateAdapter {
    private UIWindow window;
    private MyViewController myViewController;

    @Override
    public void didFinishLaunching (UIApplication application) {
        // Set up the view controller
        myViewController = new MyViewController();

// UIApplication.getSharedApplication().setStatusBarStyle(UIStatusBarStyle.BlackOpaque);

        // Create a new window with our specified viewport.
        window = new UIWindow(new CGRect(0, 0, 320, 480));
        // Scale the window contents to fill the entire screen, regardless of resolution.
        window.setContentMode(UIViewContentMode.ScaleToFill);
        // Set our viewcontroller as the root controller for the window.
        window.setRootViewController(myViewController);
        // Make the window visible.
        window.makeKeyAndVisible();

        /*
         * Retains the window object until the application is deallocated. Prevents Java GC from collecting the window object too
         * early.
         */
        application.addStrongRef(window);
    }

    public static void main (String[] args) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(args, null, HelloWorld.class);
        pool.close();
    }
}
