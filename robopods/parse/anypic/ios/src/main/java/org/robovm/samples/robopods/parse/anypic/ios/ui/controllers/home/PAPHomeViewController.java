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
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIButtonType;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UITableViewStyle;
import org.robovm.apple.uikit.UIView;
import org.robovm.samples.robopods.parse.anypic.ios.ui.controllers.PAPSettingsActionSheet;
import org.robovm.samples.robopods.parse.anypic.ios.ui.controllers.friends.PAPFindFriendsViewController;
import org.robovm.samples.robopods.parse.anypic.ios.ui.controllers.photo.PAPPhotoTimelineViewController;
import org.robovm.samples.robopods.parse.anypic.ios.ui.views.PAPSettingsButtonItem;

public class PAPHomeViewController extends PAPPhotoTimelineViewController {
    private boolean firstLaunch;
    private UIView blankTimelineView;

    public PAPHomeViewController() {
        super(UITableViewStyle.Plain);
    }

    public PAPHomeViewController(UITableViewStyle style) {
        super(style);
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
            PAPSettingsActionSheet actionSheet = new PAPSettingsActionSheet(getNavigationController());
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
