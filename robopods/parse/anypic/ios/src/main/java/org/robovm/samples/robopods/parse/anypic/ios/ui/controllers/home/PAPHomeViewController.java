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
package org.robovm.samples.robopods.parse.anypic.ios.ui.controllers.home;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.uikit.UIActionSheet;
import org.robovm.apple.uikit.UIActionSheetDelegateAdapter;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIButtonType;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UINavigationController;
import org.robovm.apple.uikit.UITableViewStyle;
import org.robovm.apple.uikit.UIView;
import org.robovm.samples.robopods.parse.anypic.ios.AnyPicApp;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPUser;
import org.robovm.samples.robopods.parse.anypic.ios.ui.controllers.settings.PAPFindFriendsViewController;
import org.robovm.samples.robopods.parse.anypic.ios.ui.views.PAPSettingsButtonItem;

public class PAPHomeViewController extends PAPPhotoTimelineViewController {
    private boolean firstLaunch;
    private UINavigationController presentingAccountNavController;
    private UINavigationController presentingFriendNavController;
    private UIView blankTimelineView;

    public PAPHomeViewController() {
        super(UITableViewStyle.Plain);
        setup();
    }

    public PAPHomeViewController(UITableViewStyle style) {
        super(style);
        setup();
    }

    private void setup() {
        PAPAccountViewController accountViewController = new PAPAccountViewController(PAPUser.getCurrentUser());
        presentingAccountNavController = new UINavigationController(accountViewController);

        PAPFindFriendsViewController findFriendsVC = new PAPFindFriendsViewController(UITableViewStyle.Plain);
        presentingFriendNavController = new UINavigationController(findFriendsVC);
    }

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        getNavigationItem().setTitleView(new UIImageView(UIImage.create("LogoNavigationBar")));

        getNavigationItem().setRightBarButtonItem(new PAPSettingsButtonItem(settingsButtonAction));

        blankTimelineView = new UIView(getTableView().getBounds());

        UIButton button = UIButton.create(UIButtonType.Custom);
        button.setFrame(new CGRect(33, 96, 253, 173));
        button.setBackgroundImage(UIImage.create("HomeTimelineBlank"), UIControlState.Normal);
        button.addOnTouchUpInsideListener(inviteFriendsButtonAction);
        blankTimelineView.addSubview(button);
    }

    @Override
    public void didLoadObjects(NSError error) {
        super.didLoadObjects(error);

        if (getObjects().size() == 0 && !getQuery().hasCachedResult() && !firstLaunch) {
            getTableView().setScrollEnabled(false);

            if (blankTimelineView.getSuperview() == null) {
                blankTimelineView.setAlpha(0);
                getTableView().setTableHeaderView(blankTimelineView);

                UIView.animate(0.2, new Runnable() {
                    @Override
                    public void run() {
                        blankTimelineView.setAlpha(1);
                    }
                });
            }
        } else {
            getTableView().setTableHeaderView(null);
            getTableView().setScrollEnabled(true);
        }
    }

    private final UIControl.OnTouchUpInsideListener settingsButtonAction = new UIControl.OnTouchUpInsideListener() {
        @Override
        public void onTouchUpInside(UIControl control, UIEvent event) {
            UIActionSheet actionSheet = new UIActionSheet(null, new UIActionSheetDelegateAdapter() {
                @Override
                public void clicked(UIActionSheet actionSheet, long buttonIndex) {
                    switch ((int) buttonIndex) {
                    case 0:
                        presentViewController(presentingAccountNavController, true, null);
                        break;
                    case 1:
                        presentViewController(presentingFriendNavController, true, null);
                        break;
                    case 2:
//                      // Log out user and present the login view controller
                        ((AnyPicApp) UIApplication.getSharedApplication().getDelegate()).logOut();
                        break;
                    default:
                        break;
                    }
                }
            }, "Cancel", null, "My Profile", "Find Friends", "Log Out");
            actionSheet.showFrom(getTabBarController().getTabBar());
        }
    };

    private final UIControl.OnTouchUpInsideListener inviteFriendsButtonAction = new UIControl.OnTouchUpInsideListener() {
        @Override
        public void onTouchUpInside(UIControl control, UIEvent event) {
            PAPFindFriendsViewController detailViewController = new PAPFindFriendsViewController(UITableViewStyle.Plain);
            getNavigationController().pushViewController(detailViewController, true);
        }
    };

    public boolean isFirstLaunch() {
        return firstLaunch;
    }

    public void setFirstLaunch(boolean firstLaunch) {
        this.firstLaunch = firstLaunch;
    }
}
