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
 * Portions of this code is based on Apple Inc's ApplicationShortcuts sample (v1.0)
 * which is copyright (C) 2015 Apple Inc.
 */
package org.robovm.samples.applicationshortcuts;

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSMutableDictionary;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.UIAlertAction;
import org.robovm.apple.uikit.UIAlertActionStyle;
import org.robovm.apple.uikit.UIAlertController;
import org.robovm.apple.uikit.UIAlertControllerStyle;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UIApplicationShortcutIcon;
import org.robovm.apple.uikit.UIApplicationShortcutIconType;
import org.robovm.apple.uikit.UIApplicationShortcutItem;
import org.robovm.apple.uikit.UIMutableApplicationShortcutItem;
import org.robovm.objc.block.VoidBooleanBlock;

public class ApplicationShortcuts extends UIApplicationDelegateAdapter {
    public static final String APPLICATION_SHORTCUT_USER_INFO_ICON_KEY = "applicationShortcutUserInfoIconKey";

    private static final String SHORTCUT_FIRST = "org.robovm.samples.applicationshortcuts.first";
    private static final String SHORTCUT_SECOND = "org.robovm.samples.applicationshortcuts.second";
    private static final String SHORTCUT_THIRD = "org.robovm.samples.applicationshortcuts.third";
    private static final String SHORTCUT_FOURTH = "org.robovm.samples.applicationshortcuts.fourth";

    /**
     * Saved shortcut item used as a result of an app launch, used later when
     * app is activated.
     */
    private UIApplicationShortcutItem launchedShortcutItem;

    private boolean handleShortCutItem(UIApplicationShortcutItem shortcutItem) {
        boolean handled = false;

        String shortCuttype = shortcutItem.getType();
        if (shortCuttype == null) {
            return false;
        }

        switch (shortCuttype) {
        case SHORTCUT_FIRST:
            // Handle shortcut 1 (static).
            handled = true;
            break;
        case SHORTCUT_SECOND:
            // Handle shortcut 2 (static).
            handled = true;
            break;
        case SHORTCUT_THIRD:
            // Handle shortcut 3 (dynamic).
            handled = true;
            break;
        case SHORTCUT_FOURTH:
            // Handle shortcut 4 (dynamic).
            handled = true;
            break;
        default:
            break;
        }

        // Construct an alert using the details of the shortcut used to open the
        // application.
        UIAlertController alertController = new UIAlertController("Shortcut Handled",
                shortcutItem.getLocalizedTitle(), UIAlertControllerStyle.Alert);

        UIAlertAction okAction = new UIAlertAction("OK", UIAlertActionStyle.Default, null);
        alertController.addAction(okAction);

        // Display an alert indicating the shortcut selected from the home
        // screen.
        getWindow().getRootViewController().presentViewController(alertController, true, null);

        return handled;
    }

    @Override
    public boolean didFinishLaunching(UIApplication application, UIApplicationLaunchOptions launchOptions) {
        // Override point for customization after application launch.
        boolean shouldPerformAdditionalDelegateHandling = true;

        // If a shortcut was launched, display its information and take the
        // appropriate action
        if (launchOptions != null) {
            launchedShortcutItem = launchOptions.getShortcutItem();
        }

        // This will block "performActionForShortcutItem:completionHandler" from
        // being called.
        shouldPerformAdditionalDelegateHandling = false;

        // Install initial versions of our two extra dynamic shortcuts.
        NSArray<UIApplicationShortcutItem> shortcutItems = application.getShortcutItems();
        if (shortcutItems == null || shortcutItems.isEmpty()) {
            // Construct the items.
            UIApplicationShortcutItem shortcut3 = new UIMutableApplicationShortcutItem(SHORTCUT_THIRD, "Play");
            shortcut3.setLocalizedSubtitle("Will Play an item");
            shortcut3.setIcon(new UIApplicationShortcutIcon(UIApplicationShortcutIconType.Play));
            NSDictionary<NSString, ?> info3 = new NSMutableDictionary<>();
            info3.put(APPLICATION_SHORTCUT_USER_INFO_ICON_KEY, UIApplicationShortcutIconType.Play.ordinal());
            shortcut3.setUserInfo(info3);

            UIApplicationShortcutItem shortcut4 = new UIMutableApplicationShortcutItem(SHORTCUT_FOURTH, "Pause");
            shortcut4.setLocalizedSubtitle("Will Pause an item");
            shortcut4.setIcon(new UIApplicationShortcutIcon(UIApplicationShortcutIconType.Pause));
            NSDictionary<NSString, ?> info4 = new NSMutableDictionary<>();
            info4.put(APPLICATION_SHORTCUT_USER_INFO_ICON_KEY, UIApplicationShortcutIconType.Pause.ordinal());
            shortcut4.setUserInfo(info4);

            // Update the application providing the initial 'dynamic' shortcut
            // items.
            application.setShortcutItems(new NSArray<>(shortcut3, shortcut4));
        }

        return shouldPerformAdditionalDelegateHandling;
    }

    @Override
    public void didBecomeActive(UIApplication application) {
        if (launchedShortcutItem != null) {
            handleShortCutItem(launchedShortcutItem);
            launchedShortcutItem = null;
        }
    }

    /**
     * Called when the user activates your application by selecting a shortcut
     * on the home screen, except when
     * {@link #willFinishLaunching(UIApplication, UIApplicationLaunchOptions)}
     * or {@link #didFinishLaunching(UIApplication, UIApplicationLaunchOptions)}
     * returns false. You should handle the shortcut in those callbacks and
     * return false if possible. In that case, this callback is used if your
     * application is already launched in the background.
     */
    @Override
    public void performAction(UIApplication application, UIApplicationShortcutItem shortcutItem,
            VoidBooleanBlock completionHandler) {
        completionHandler.invoke(handleShortCutItem(shortcutItem));
    }

    public static void main(String[] args) {
        try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
            UIApplication.main(args, null, ApplicationShortcuts.class);
        }
    }
}
