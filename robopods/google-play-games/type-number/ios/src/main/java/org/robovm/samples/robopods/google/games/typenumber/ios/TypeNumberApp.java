package org.robovm.samples.robopods.google.games.typenumber.ios;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSErrorException;
import org.robovm.apple.foundation.NSJSONReadingOptions;
import org.robovm.apple.foundation.NSJSONSerialization;
import org.robovm.apple.foundation.NSPropertyList;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.pods.google.opensource.GTLBase64;
import org.robovm.pods.google.plus.GPPDeepLink;
import org.robovm.pods.google.plus.GPPDeepLinkDelegate;
import org.robovm.pods.google.plus.GPPURLHandler;

public class TypeNumberApp extends UIApplicationDelegateAdapter implements GPPDeepLinkDelegate {
    private Runnable challengeReceivedHandler;
    private NSDictionary<?, ?> deepLinkParams;

    @Override
    public void didReceiveDeepLink(GPPDeepLink deepLink) {
        String deepLinkID = deepLink.getDeepLinkID();
        NSData decodedData = GTLBase64.decodeWebSafe(deepLinkID);
        if (decodedData == null)
            return;

        try {
            deepLinkParams = (NSDictionary<?, ?>) NSJSONSerialization.createJSONObject(decodedData,
                    NSJSONReadingOptions.None);
            Log.d("Deep link ID is %s", deepLinkID);
            Log.d("This is my dictionary %s", deepLinkParams);
        } catch (NSErrorException e) {
            e.printStackTrace();
        }

        if (challengeReceivedHandler != null) {
            challengeReceivedHandler.run();
        }
    }

    @Override
    public boolean didFinishLaunching(UIApplication application, UIApplicationLaunchOptions launchOptions) {
        // Check and see if I can sign in "silently" beause I've signed in
        // before,
        // and/or there's a saved token in our keychain. If so, go for it.
        // This will call our finished delegate if true.

        GPPDeepLink.setDelegate(this);
        GPPDeepLink.readDeepLinkAfterInstall();

        return true;
    }

    @Override
    public boolean openURL(UIApplication application, NSURL url, String sourceApplication, NSPropertyList annotation) {
        Log.d("I am receiving the URL %s", url.getAbsoluteString());
        boolean canRespond = GPPURLHandler.handleURL(url, sourceApplication, annotation);
        if (canRespond) {
            return true;
        } else {
            // There might be other things you'd want to do here
            return false;
        }
    }

    public NSDictionary<?, ?> getDeepLinkParams() {
        return deepLinkParams;
    }

    public void setDeepLinkParams(NSDictionary<?, ?> deepLinkParams) {
        this.deepLinkParams = deepLinkParams;
    }

    public void setChallengeReceivedHandler(Runnable challengeReceivedHandler) {
        this.challengeReceivedHandler = challengeReceivedHandler;
    }

    public static void main(String[] args) {
        try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
            UIApplication.main(args, null, TypeNumberApp.class);
        }
    }
}
