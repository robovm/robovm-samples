/*
 * Copyright (C) 2013-2016 RoboVM AB
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
 * Portions of this code is based on Apple Inc's TVMLAudioVideo sample (v1.0)
 * which is copyright (C) 2015 Apple Inc.
 */
package org.robovm.sample.tvmlaudiovideo;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.javascriptcore.JSContext;
import org.robovm.apple.tvmlkit.TVApplicationController;
import org.robovm.apple.tvmlkit.TVApplicationControllerContext;
import org.robovm.apple.tvmlkit.TVApplicationControllerDelegate;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIWindow;

public class Main extends UIApplicationDelegateAdapter implements TVApplicationControllerDelegate {
    private static final String TV_BASE_URL = "http://localhost:9001/";
    private static final String TV_BOOT_URL = TV_BASE_URL + "js/application.js";

    private UIWindow window;
    private TVApplicationController appController;

    @Override
    public boolean didFinishLaunching(UIApplication application, UIApplicationLaunchOptions launchOptions) {
        window = new UIWindow(UIScreen.getMainScreen().getBounds());

        /*
         * Create the TVApplicationControllerContext for this application and
         * set the properties that will be passed to the `App.onLaunch` function
         * in JavaScript.
         */
        TVApplicationControllerContext appControllerContext = new TVApplicationControllerContext();

        /*
         * The JavaScript URL is used to create the JavaScript context for your
         * TVMLKit application. Although it is possible to separate your
         * JavaScript into separate files, to help reduce the launch time of
         * your application we recommend creating minified and compressed
         * version of this resource. This will allow for the resource to be
         * retrieved and UI presented to the user quickly.
         */
        NSURL javaScriptURL = new NSURL(TV_BOOT_URL);
        appControllerContext.setJavaScriptApplicationURL(javaScriptURL);

        UIApplicationLaunchOptions tvLaunchOptions = new UIApplicationLaunchOptions();
        tvLaunchOptions.getDictionary().put("BASEURL", TV_BASE_URL);

        if (launchOptions != null) {
            tvLaunchOptions.getDictionary().putAll(launchOptions.getDictionary());
        }
        appControllerContext.setLaunchOptions(tvLaunchOptions);

        appController = new TVApplicationController(appControllerContext, window, this);

        return true;
    }

    public static void main(String[] args) {
        try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
            UIApplication.main(args, null, Main.class);
        }
    }

    @Override
    public void didFail(TVApplicationController appController, NSError error) {
        System.out.println("did fail with error: " + error);
    }

    @Override
    public void didFinishLaunching(TVApplicationController appController, UIApplicationLaunchOptions options) {
        System.out.println("did finish launching with options: " + options);
    }

    @Override
    public void didStop(TVApplicationController appController, UIApplicationLaunchOptions options) {
        System.out.println("did stop with options: " + options);
    }

    @Override
    public void evaluateAppJavaScript(TVApplicationController appController, JSContext jsContext) {
        // TODO Auto-generated method stub

    }
}
