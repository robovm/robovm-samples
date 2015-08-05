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
 * Portions of this code is based on Parse's AnyPic sample
 * which is copyright (C) 2013 Parse.
 */
package org.robovm.samples.robopods.parse.anypic.ios.ui.controllers.settings;

import org.robovm.apple.uikit.UIActionSheet;
import org.robovm.apple.uikit.UIActionSheetDelegateAdapter;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UINavigationController;
import org.robovm.samples.robopods.parse.anypic.ios.AnyPicApp;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPUser;
import org.robovm.samples.robopods.parse.anypic.ios.ui.controllers.home.PAPAccountViewController;

public class PAPSettingsActionSheetDelegate extends UIActionSheetDelegateAdapter {
    /**
     * Navigation controller of calling view controller
     */
    private UINavigationController navController;

    public PAPSettingsActionSheetDelegate(UINavigationController navController) {
        this.navController = navController;
    }

    public PAPSettingsActionSheetDelegate() {}

    @Override
    public void clicked(UIActionSheet actionSheet, long buttonIndex) {
        if (navController == null) {
            throw new RuntimeException("navController cannot be null");
        }
        switch ((int) buttonIndex) {
        case 0: // PROFILE
            PAPAccountViewController accountViewController = new PAPAccountViewController(PAPUser.getCurrentUser());
            navController.pushViewController(accountViewController, true);
            break;
        case 1: // FIND FRIENDS
            PAPFindFriendsViewController findFriendsVC = new PAPFindFriendsViewController();
            navController.pushViewController(findFriendsVC, true);
            break;
        case 2: // LOGOUT
            // Log out user and present the login view controller
            ((AnyPicApp) UIApplication.getSharedApplication().getDelegate()).logOut();
            break;
        default:
            break;
        }
    }
}
