package org.robovm.samples.customframeworks;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;

public class HelloFrameworks extends UIApplicationDelegateAdapter {
    public static void main (String[] args) {
        try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
            UIApplication.main(args, null, HelloFrameworks.class);
        }
    }
}
