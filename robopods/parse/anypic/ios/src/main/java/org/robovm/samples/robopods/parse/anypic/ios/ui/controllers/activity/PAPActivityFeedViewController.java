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
package org.robovm.samples.robopods.parse.anypic.ios.ui.controllers.activity;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSComparisonResult;
import org.robovm.apple.foundation.NSDate;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSNotification;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIButtonType;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIStatusBarStyle;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCellSelectionStyle;
import org.robovm.apple.uikit.UITableViewCellSeparatorStyle;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UITableViewStyle;
import org.robovm.apple.uikit.UIView;
import org.robovm.objc.block.VoidBlock1;
import org.robovm.pods.parse.PFCachePolicy;
import org.robovm.pods.parse.PFQuery;
import org.robovm.pods.parse.ui.PFQueryTableViewController;
import org.robovm.pods.parse.ui.PFTableViewCell;
import org.robovm.samples.robopods.parse.anypic.ios.AnyPicApp;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPActivity;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPActivityType;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPCache;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPPhoto;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPUser;
import org.robovm.samples.robopods.parse.anypic.ios.ui.controllers.home.PAPAccountViewController;
import org.robovm.samples.robopods.parse.anypic.ios.ui.controllers.photo.PAPPhotoDetailsViewController;
import org.robovm.samples.robopods.parse.anypic.ios.ui.controllers.settings.PAPFindFriendsViewController;
import org.robovm.samples.robopods.parse.anypic.ios.ui.controllers.settings.PAPSettingsActionSheet;
import org.robovm.samples.robopods.parse.anypic.ios.ui.views.PAPSettingsButtonItem;
import org.robovm.samples.robopods.parse.anypic.ios.ui.views.tablecells.PAPActivityCell;
import org.robovm.samples.robopods.parse.anypic.ios.ui.views.tablecells.PAPActivityCellDelegate;
import org.robovm.samples.robopods.parse.anypic.ios.ui.views.tablecells.PAPBaseTextCell;
import org.robovm.samples.robopods.parse.anypic.ios.ui.views.tablecells.PAPLoadMoreCell;
import org.robovm.samples.robopods.parse.anypic.ios.util.Log;
import org.robovm.samples.robopods.parse.anypic.ios.util.PAPNotification;
import org.robovm.samples.robopods.parse.anypic.ios.util.PAPNotificationManager;

public class PAPActivityFeedViewController extends PFQueryTableViewController<PAPActivity> implements
        PAPActivityCellDelegate {
    private NSDate lastRefresh;
    private UIView blankTimelineView;

    private NSObject applicationDidReceiveRemoteNotification;

    public PAPActivityFeedViewController(UITableViewStyle style) {
        super(style, PAPActivity.class);

        // Whether the built-in pagination is enabled
        setPaginationEnabled(true);

        // Whether the built-in pull-to-refresh is enabled
        setPullToRefreshEnabled(true);

        // The number of objects to show per page
        setObjectsPerPage(15);

        // The Loading text clashes with the dark Anypic design
        setLoadingViewEnabled(false);
    }

    @Override
    protected void dispose(boolean finalizing) {
        super.dispose(finalizing);

        PAPNotificationManager.removeObserver(applicationDidReceiveRemoteNotification);
    }

    @Override
    public void viewDidLoad() {
        getTableView().setSeparatorStyle(UITableViewCellSeparatorStyle.SingleLine);

        super.viewDidLoad();

        UIView texturedBackgroundView = new UIView(getView().getBounds());
        texturedBackgroundView.setBackgroundColor(UIColor.black());
        getTableView().setBackgroundView(texturedBackgroundView);

        getNavigationItem().setTitleView(new UIImageView(UIImage.create("LogoNavigationBar")));

        // Add Settings button
        getNavigationItem().setRightBarButtonItem(new PAPSettingsButtonItem(settingsButtonAction));

        applicationDidReceiveRemoteNotification = PAPNotificationManager.addObserver(
                PAPNotification.DID_RECEIVE_REMOTE_NOTIFICATION, new VoidBlock1<NSNotification>() {
                    @Override
                    public void invoke(NSNotification notification) {
                        loadObjects();
                    }
                });

        blankTimelineView = new UIView(getTableView().getBounds());

        UIButton button = UIButton.create(UIButtonType.Custom);
        button.setBackgroundImage(UIImage.create("ActivityFeedBlank"), UIControlState.Normal);
        button.setFrame(new CGRect(24, 113, 271, 140));
        button.addOnTouchUpInsideListener(inviteFriendsButtonAction);
        blankTimelineView.addSubview(button);

        lastRefresh = PAPCache.getSharedCache().getLastActivityFeedRefresh();
    }

    @Override
    public UIStatusBarStyle getPreferredStatusBarStyle() {
        return UIStatusBarStyle.LightContent;
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);
        getTableView().setSeparatorColor(UIColor.fromRGBA(30f / 255f, 30f / 255f, 30f / 255f, 1));
    }

    @Override
    public double getHeightForRow(UITableView tableView, NSIndexPath indexPath) {
        NSArray<PAPActivity> objects = getObjects();
        if (indexPath.getRow() < objects.size()) {
            PAPActivity activity = objects.get(indexPath.getRow());
            String activityString = activity.getType().getMessage();

            PAPUser user = activity.getFromUser();
            String nameString = "Someone";
            if (user != null && user.getDisplayName() != null && user.getDisplayName().length() > 0) {
                nameString = user.getDisplayName();
            }

            return PAPActivityCell.getHeightForCell(nameString, activityString);
        } else {
            return 44;
        }
    }

    @Override
    public void didSelectRow(UITableView tableView, NSIndexPath indexPath) {
        tableView.deselectRow(indexPath, true);
        NSArray<PAPActivity> objects = getObjects();
        if (indexPath.getRow() < objects.size()) {
            PAPActivity activity = objects.get(indexPath.getRow());
            if (activity.getPhoto() != null) {
                PAPPhotoDetailsViewController detailViewController = new PAPPhotoDetailsViewController(
                        activity.getPhoto());
                getNavigationController().pushViewController(detailViewController, true);
            } else if (activity.getFromUser() != null) {
                PAPAccountViewController detailViewController = new PAPAccountViewController(UITableViewStyle.Plain);
                Log.d("Presenting account view controller with user: %s", activity.getFromUser());
                detailViewController.setUser(activity.getFromUser());
                getNavigationController().pushViewController(detailViewController, true);
            }
        } else if (isPaginationEnabled()) {
            // load more
            loadNextPage();
        }
    }

    @Override
    public PFQuery<PAPActivity> getQuery() {
        if (PAPUser.getCurrentUser() != null) {
            PFQuery<PAPActivity> query = PFQuery.getQuery(PAPActivity.class);
            query.setLimit(0);
            return query;
        }

        PFQuery<PAPActivity> query = PFQuery.getQuery(PAPActivity.class);
        query.whereEqualTo(PAPActivity.TO_USER_KEY, PAPUser.getCurrentUser());
        query.whereNotEqualTo(PAPActivity.FROM_USER_KEY, PAPUser.getCurrentUser());
        query.whereExists(PAPActivity.FROM_USER_KEY);
        query.include(PAPActivity.FROM_USER_KEY);
        query.include(PAPActivity.PHOTO_KEY);
        query.orderByDescending("createdAt");

        query.setCachePolicy(PFCachePolicy.NetworkOnly);

        // If no objects are loaded in memory, we look to the cache first to
        // fill the table
        // and then subsequently do a query against the network.
        //
        // If there is no network connection, we will hit the cache first.
        if (getObjects().size() == 0
                || !((AnyPicApp) UIApplication.getSharedApplication().getDelegate()).isParseReachable()) {
            query.setCachePolicy(PFCachePolicy.CacheThenNetwork);
        }

        return query;
    }

    @Override
    public void didLoadObjects(NSError error) {
        super.didLoadObjects(error);

        lastRefresh = new NSDate();
        PAPCache.getSharedCache().setLastActivityFeedRefresh(lastRefresh);

//        [MBProgressHUD hideHUDForView:self.view animated:YES]; TODO

        NSArray<PAPActivity> objects = getObjects();

        if (objects.size() == 0 && !getQuery().hasCachedResult()) {
            getTableView().setScrollEnabled(false);
            getNavigationController().getTabBarItem().setBadgeValue(null);

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

            int unreadCount = 0;
            for (PAPActivity activity : objects) {
                if (lastRefresh.compare(activity.getCreatedAt()) == NSComparisonResult.Ascending
                        && activity.getType() != PAPActivityType.JOINED) {
                    unreadCount++;
                }
            }

            if (unreadCount > 0) {
                getNavigationController().getTabBarItem().setBadgeValue(String.valueOf(unreadCount));
            } else {
                getNavigationController().getTabBarItem().setBadgeValue(null);
            }
        }
    }

    @Override
    public PFTableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath, PAPActivity activity) {
        final String cellIdentifier = "ActivityCell";

        PAPActivityCell cell = (PAPActivityCell) tableView.dequeueReusableCell(cellIdentifier);
        if (cell == null) {
            cell = new PAPActivityCell(UITableViewCellStyle.Default, cellIdentifier);
            cell.setDelegate(this);
            cell.setSelectionStyle(UITableViewCellSelectionStyle.None);
        }

        cell.setActivity(activity);

        cell.setIsNew(lastRefresh.compare(activity.getCreatedAt()) == NSComparisonResult.Ascending);

        cell.hideSeparator(indexPath.getRow() == getObjects().size() - 1);

        return cell;
    }

    @Override
    public PFTableViewCell getCellForNextPage(UITableView tableView, NSIndexPath indexPath) {
        final String loadMoreCellIdentifier = "LoadMoreCell";

        PAPLoadMoreCell cell = (PAPLoadMoreCell) tableView.dequeueReusableCell(loadMoreCellIdentifier);
        if (cell == null) {
            cell = new PAPLoadMoreCell(UITableViewCellStyle.Default, loadMoreCellIdentifier);
            cell.setSelectionStyle(UITableViewCellSelectionStyle.None);
            cell.setHideSeparatorBottom(true);
            cell.getMainView().setBackgroundColor(UIColor.clear());
        }
        return cell;
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
            PAPFindFriendsViewController detailViewController = new PAPFindFriendsViewController();
            getNavigationController().pushViewController(detailViewController, true);
        }
    };

    @Override
    public void didTapUserButton(PAPBaseTextCell cellView, PAPUser user) {
        // Push account view controller
        PAPAccountViewController accountViewController = new PAPAccountViewController(UITableViewStyle.Plain);
        Log.d("Presenting account view controller with user: %s", user);
        accountViewController.setUser(user);
        getNavigationController().pushViewController(accountViewController, true);
    }

    @Override
    public void didTapActivityButton(PAPActivityCell cellView, PAPActivity activity) {
        // Get image associated with the activity
        PAPPhoto photo = activity.getPhoto();

        // Push single photo view controller
        PAPPhotoDetailsViewController photoViewController = new PAPPhotoDetailsViewController(photo);
        getNavigationController().pushViewController(photoViewController, true);
    }
}
