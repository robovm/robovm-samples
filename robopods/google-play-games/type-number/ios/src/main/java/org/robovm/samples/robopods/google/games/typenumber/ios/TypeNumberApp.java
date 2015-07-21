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
 * Portions of this code is based on Google Inc's Google Play Games 'Type a Number' sample
 * which is copyright (C) 2015 Google Inc.
 */
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
