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
package org.robovm.samples.robopods.parse.anypic.ios.ui.controllers.photo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.dispatch.DispatchQueue;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSNotification;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIStatusBarStyle;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCellSelectionStyle;
import org.robovm.apple.uikit.UITableViewCellSeparatorStyle;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UITableViewScrollPosition;
import org.robovm.apple.uikit.UITableViewStyle;
import org.robovm.apple.uikit.UIView;
import org.robovm.objc.block.VoidBlock1;
import org.robovm.pods.parse.PFCachePolicy;
import org.robovm.pods.parse.PFFindCallback;
import org.robovm.pods.parse.PFQuery;
import org.robovm.pods.parse.PFSaveCallback;
import org.robovm.pods.parse.ui.PFQueryTableViewController;
import org.robovm.pods.parse.ui.PFTableViewCell;
import org.robovm.samples.robopods.parse.anypic.ios.AnyPicApp;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPActivity;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPActivityType;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPCache;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPPhoto;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPPhotoAttributes;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPUser;
import org.robovm.samples.robopods.parse.anypic.ios.ui.controllers.home.PAPAccountViewController;
import org.robovm.samples.robopods.parse.anypic.ios.ui.views.tablecells.PAPLoadMoreCell;
import org.robovm.samples.robopods.parse.anypic.ios.ui.views.tablecells.PAPPhotoCell;
import org.robovm.samples.robopods.parse.anypic.ios.ui.views.timeline.PAPPhotoHeaderButtons;
import org.robovm.samples.robopods.parse.anypic.ios.ui.views.timeline.PAPPhotoHeaderView;
import org.robovm.samples.robopods.parse.anypic.ios.ui.views.timeline.PAPPhotoHeaderViewDelegate;
import org.robovm.samples.robopods.parse.anypic.ios.util.Log;
import org.robovm.samples.robopods.parse.anypic.ios.util.PAPNotification;
import org.robovm.samples.robopods.parse.anypic.ios.util.PAPNotificationManager;
import org.robovm.samples.robopods.parse.anypic.ios.util.PAPUtility;

public class PAPPhotoTimelineViewController extends PFQueryTableViewController<PAPPhoto> implements
        PAPPhotoHeaderViewDelegate {
    private boolean shouldReloadOnAppear;
    private List<PAPPhotoHeaderView> reusableSectionHeaderViews;
    private Map<Integer, Integer> outstandingSectionHeaderQueries;

    private NSObject[] notifications;

    public PAPPhotoTimelineViewController(UITableViewStyle style) {
        super(style, PAPPhoto.class);

        // Whether the built-in pull-to-refresh is enabled
        setPullToRefreshEnabled(true);

        // Whether the built-in pagination is enabled
        setPaginationEnabled(false);

        // The number of objects to show per page
        setObjectsPerPage(10);

        // The Loading text clashes with the dark Anypic design
        setLoadingViewEnabled(false);

        shouldReloadOnAppear = false;
    }

    @Override
    protected void dispose(boolean finalizing) {
        super.dispose(finalizing);

        if (notifications != null) {
            for (NSObject notification : notifications) {
                PAPNotificationManager.removeObserver(notification);
            }
        }
    }

    @Override
    public void viewDidLoad() {
        // Improve scrolling performance by reusing UITableView section headers
        reusableSectionHeaderViews = new ArrayList<>(3);

        outstandingSectionHeaderQueries = new HashMap<>();

        getTableView().setSeparatorStyle(UITableViewCellSeparatorStyle.None);

        super.viewDidLoad();

        UIView texturedBackgroundView = new UIView(getView().getBounds());
        texturedBackgroundView.setBackgroundColor(UIColor.fromRGBA(0f, 0f, 0f, 1));
        getTableView().setBackgroundView(texturedBackgroundView);

        notifications = new NSObject[6];
        notifications[0] = PAPNotificationManager.addObserver(PAPNotification.DID_FINISH_EDITING_PHOTO,
                new VoidBlock1<NSNotification>() {
                    @Override
                    public void invoke(NSNotification notification) {
                        if (getObjects().size() > 0) {
                            getTableView().scrollToRow(NSIndexPath.createWithRow(0, 0), UITableViewScrollPosition.Top,
                                    true);
                        }
                        loadObjects();
                    }
                });
        notifications[1] = PAPNotificationManager.addObserver(PAPNotification.USER_FOLLOWING_CHANGED,
                new VoidBlock1<NSNotification>() {
                    @Override
                    public void invoke(NSNotification notification) {
                        Log.d("User following changed.");
                        shouldReloadOnAppear = true;
                    }
                });
        notifications[2] = PAPNotificationManager.addObserver(PAPNotification.USER_DELETED_PHOTO,
                new VoidBlock1<NSNotification>() {
                    @Override
                    public void invoke(NSNotification notification) {
                        // refresh timeline after a delay
                        DispatchQueue.getMainQueue().after(1, TimeUnit.SECONDS, new Runnable() {
                            @Override
                            public void run() {
                                loadObjects();
                            }
                        });
                    }
                });
        notifications[3] = PAPNotificationManager.addObserver(PAPNotification.USER_LIKES_PHOTO,
                new VoidBlock1<NSNotification>() {
                    @Override
                    public void invoke(NSNotification notification) {
                        getTableView().beginUpdates();
                        getTableView().endUpdates();
                    }
                });
        notifications[4] = PAPNotificationManager.addObserver(PAPNotification.USER_LIKING_PHOTO_CALLBACK_FINISHED,
                new VoidBlock1<NSNotification>() {
                    @Override
                    public void invoke(NSNotification notification) {
                        getTableView().beginUpdates();
                        getTableView().endUpdates();
                    }
                });
        notifications[5] = PAPNotificationManager.addObserver(PAPNotification.USER_COMMENTED_ON_PHOTO,
                new VoidBlock1<NSNotification>() {
                    @Override
                    public void invoke(NSNotification notification) {
                        getTableView().beginUpdates();
                        getTableView().endUpdates();
                    }
                });
    }

    @Override
    public void viewDidAppear(boolean animated) {
        super.viewDidAppear(animated);

        if (shouldReloadOnAppear) {
            shouldReloadOnAppear = false;
            loadObjects();
        }
    }

    @Override
    public UIStatusBarStyle getPreferredStatusBarStyle() {
        return UIStatusBarStyle.LightContent;
    }

    @Override
    public long getNumberOfSections(UITableView tableView) {
        return 1;
    }

    @Override
    public long getNumberOfRowsInSection(UITableView tableView, long section) {
        return getObjects().size() * 2 + (isPaginationEnabled() ? 1 : 0);
    }

    @Override
    public double getHeightForRow(UITableView tableView, NSIndexPath indexPath) {
        if (isPaginationEnabled() && (getObjects().size() * 2) == indexPath.getRow()) {
            // Load More Section
            return 44;
        } else if (indexPath.getRow() % 2 == 0) {
            return 44;
        }

        return 320;
    }

    @Override
    public void didSelectRow(UITableView tableView, NSIndexPath indexPath) {
        super.didSelectRow(tableView, indexPath);

        if (indexPath.getRow() > getObjects().size() - 1) {
            return;
        }

        tableView.deselectRow(indexPath, true);

        if (getObject(indexPath) == null) {
            // Load More Cell
            loadNextPage();
        }
    }

    @Override
    public PAPPhoto getObject(NSIndexPath indexPath) {
        int index = getIndexForObjectAt(indexPath);
        NSArray<PAPPhoto> objects = getObjects();
        if (index < objects.size()) {
            return objects.get(index);
        }
        return null;
    }

    @Override
    public PFTableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath, PAPPhoto photo) {
        final String cellIdentifier = "Cell";

        int index = getIndexForObjectAt(indexPath);

        if (indexPath.getRow() % 2 == 0) {
            // Header
            return getDetailPhotoCellForRow(indexPath);
        } else {
            // Photo
            PAPPhotoCell cell = (PAPPhotoCell) getTableView().dequeueReusableCell(cellIdentifier);

            if (cell == null) {
                cell = new PAPPhotoCell(UITableViewCellStyle.Default, cellIdentifier);
                cell.getPhotoButton().addOnTouchUpInsideListener(didTapOnPhotoAction);
            }

            cell.getPhotoButton().setTag(index);
            cell.getImageView().setImage(UIImage.create("PlaceholderPhoto"));

            if (photo != null) {
                cell.getImageView().setFile(photo.getPicture());

                // PFQTVC will take care of asynchronously downloading files,
                // but will only load them when the tableview is not moving. If
                // the data is there, let's load it right away.
                if (cell.getImageView().getFile().isDataAvailable()) {
                    cell.getImageView().loadInBackground();
                }
            }

            return cell;
        }
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

    @Override
    public PFQuery<PAPPhoto> getQuery() {
        if (PAPUser.getCurrentUser() == null) {
            PFQuery<PAPPhoto> query = PFQuery.getQuery(PAPPhoto.class);
            query.setLimit(0);
            return query;
        }

        PFQuery<PAPActivity> followingActivitiesQuery = PFQuery.getQuery(PAPActivity.class);
        followingActivitiesQuery.whereEqualTo(PAPActivity.TYPE_KEY, PAPActivityType.FOLLOW.getKey());
        followingActivitiesQuery.whereEqualTo(PAPActivity.FROM_USER_KEY, PAPUser.getCurrentUser());
        followingActivitiesQuery.setCachePolicy(PFCachePolicy.NetworkOnly);
        followingActivitiesQuery.setLimit(1000);

        PFQuery<PAPUser> autoFollowUsersQuery = PFQuery.getQuery(PAPUser.class);
        autoFollowUsersQuery.whereEqualTo(PAPUser.AUTO_FOLLOW_KEY, true);

        PFQuery<PAPPhoto> photosFromFollowedUsersQuery = PFQuery.getQuery(PAPPhoto.class);
        photosFromFollowedUsersQuery.whereMatchesKeyInQuery(PAPPhoto.USER_KEY, PAPActivity.TO_USER_KEY,
                followingActivitiesQuery);
        photosFromFollowedUsersQuery.whereExists(PAPPhoto.PICTURE_KEY);

        PFQuery<PAPPhoto> photosFromCurrentUserQuery = PFQuery.getQuery(PAPPhoto.class);
        photosFromCurrentUserQuery.whereEqualTo(PAPPhoto.USER_KEY, PAPUser.getCurrentUser());
        photosFromCurrentUserQuery.whereExists(PAPPhoto.PICTURE_KEY);

        PFQuery<PAPPhoto> query = PFQuery.or(new NSArray<PFQuery<?>>(photosFromFollowedUsersQuery,
                photosFromCurrentUserQuery));
        query.setLimit(30);
        query.include(PAPPhoto.USER_KEY);
        query.orderByDescending("createdAt");

        // A pull-to-refresh should always trigger a network request.
        query.setCachePolicy(PFCachePolicy.NetworkOnly);

        // If no objects are loaded in memory, we look to the cache first to
        // fill the table
        // and then subsequently do a query against the network.
        //
        // If there is no network connection, we will hit the cache first.
        if (getObjects().size() == 0
                || ((AnyPicApp) UIApplication.getSharedApplication().getDelegate()).isParseReachable()) {
            query.setCachePolicy(PFCachePolicy.CacheThenNetwork);
        }

        /*
         * This query will result in an error if the schema hasn't been set
         * beforehand. While Parse usually handles this automatically, this is
         * not the case for a compound query such as this one. The error thrown
         * is:
         * 
         * Error: bad special key: __type
         * 
         * To set up your schema, you may post a photo with a caption. This will
         * automatically set up the Photo and Activity classes needed by this
         * query.
         * 
         * You may also use the Data Browser at Parse.com to set up your classes
         * in the following manner.
         * 
         * Create a User class: "User" (if it does not exist)
         * 
         * Create a Custom class: "Activity" - Add a column of type pointer to
         * "User", named "fromUser" - Add a column of type pointer to "User",
         * named "toUser" - Add a string column "type"
         * 
         * Create a Custom class: "Photo" - Add a column of type pointer to
         * "User", named "user"
         * 
         * You'll notice that these correspond to each of the fields used by the
         * preceding query.
         */
        return query;
    }

    private PAPPhotoHeaderView dequeueReusableSectionHeaderView() {
        for (PAPPhotoHeaderView sectionHeaderView : reusableSectionHeaderViews) {
            if (sectionHeaderView.getSuperview() == null) {
                // we found a section header that is no longer visible
                return sectionHeaderView;
            }
        }
        return null;
    }

    private PFTableViewCell getDetailPhotoCellForRow(NSIndexPath indexPath) {
        final String cellIdentifier = "DetailPhotoCell";

        if (isPaginationEnabled() && indexPath.getRow() == getObjects().size() * 2) {
            // Load More section
            return null;
        }

        final int index = getIndexForObjectAt(indexPath);

        PAPPhotoHeaderView headerView = (PAPPhotoHeaderView) getTableView().dequeueReusableCell(cellIdentifier);
        if (headerView == null) {
            headerView = new PAPPhotoHeaderView(new CGRect(0, 0, getView().getBounds().getSize().getWidth(), 44),
                    PAPPhotoHeaderButtons.getDefault());
            headerView.setDelegate(this);
            headerView.setSelectionStyle(UITableViewCellSelectionStyle.None);
        }

        final PAPPhoto photo = getObject(indexPath);
        headerView.setPhoto(photo);
        headerView.setTag(index);
        headerView.getLikeButton().setTag(index);

        PAPPhotoAttributes photoAttributes = PAPCache.getSharedCache().getPhotoAttributes(photo);
        if (photoAttributes != null) {
            headerView.setLikeStatus(photoAttributes.isLikedByCurrentUser());
            headerView.getLikeButton().setTitle(String.valueOf(photoAttributes.getLikeCount()), UIControlState.Normal);
            headerView.getCommentButton().setTitle(String.valueOf(photoAttributes.getCommentCount()),
                    UIControlState.Normal);

            final PAPPhotoHeaderView hv = headerView;

            if (headerView.getLikeButton().getAlpha() < 1 || headerView.getCommentButton().getAlpha() < 1) {
                UIView.animate(0.2, new Runnable() {
                    @Override
                    public void run() {
                        hv.getLikeButton().setAlpha(1);
                        hv.getCommentButton().setAlpha(1);
                    }
                });
            } else {
                headerView.getLikeButton().setAlpha(0);
                headerView.getCommentButton().setAlpha(0);

                synchronized (this) {
                    // check if we can update the cache
                    int outstandingSectionHeaderQueryStatus = outstandingSectionHeaderQueries.get(index);
                    if (outstandingSectionHeaderQueryStatus != 0) {
                        PFQuery<PAPActivity> query = PAPUtility.queryActivities(photo, PFCachePolicy.NetworkOnly);
                        query.findInBackground(new PFFindCallback<PAPActivity>() {
                            @Override
                            public void done(NSArray<PAPActivity> objects, NSError error) {
                                synchronized (PAPPhotoTimelineViewController.this) {
                                    outstandingSectionHeaderQueries.remove(index);

                                    if (error != null) {
                                        return;
                                    }

                                    List<PAPUser> likers = new ArrayList<>();
                                    List<PAPUser> commenters = new ArrayList<>();

                                    boolean isLikedByCurrentUser = false;

                                    for (PAPActivity activity : objects) {
                                        if (activity.getFromUser() != null) {
                                            if (activity.getType() == PAPActivityType.LIKE) {
                                                likers.add(activity.getFromUser());
                                            } else if (activity.getType() == PAPActivityType.COMMENT) {
                                                commenters.add(activity.getFromUser());
                                            }

                                            if (activity.getFromUser().getObjectId()
                                                    .equals(PAPUser.getCurrentUser().getObjectId())) {
                                                if (activity.getType() == PAPActivityType.LIKE) {
                                                    isLikedByCurrentUser = true;
                                                }
                                            }
                                        }
                                    }

                                    PAPPhotoAttributes photoAttributes = new PAPPhotoAttributes(likers, commenters,
                                            isLikedByCurrentUser);

                                    PAPCache.getSharedCache().setPhotoAttributes(photo, photoAttributes);

                                    if (hv.getTag() != index) {
                                        return;
                                    }

                                    hv.setLikeStatus(photoAttributes.isLikedByCurrentUser());
                                    hv.getLikeButton().setTitle(String.valueOf(photoAttributes.getLikeCount()),
                                            UIControlState.Normal);
                                    hv.getCommentButton().setTitle(
                                            String.valueOf(photoAttributes.getCommentCount()), UIControlState.Normal);

                                    if (hv.getLikeButton().getAlpha() < 1 || hv.getCommentButton().getAlpha() < 1) {
                                        UIView.animate(0.2, new Runnable() {
                                            @Override
                                            public void run() {
                                                hv.getLikeButton().setAlpha(1);
                                                hv.getCommentButton().setAlpha(1);
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                }
            }
        }

        return headerView;
    }

    private final UIControl.OnTouchUpInsideListener didTapOnPhotoAction = new UIControl.OnTouchUpInsideListener() {
        @Override
        public void onTouchUpInside(UIControl control, UIEvent event) {
            PAPPhoto photo = getObjects().get((int) control.getTag());
            if (photo != null) {
                PAPPhotoDetailsViewController photoDetailsVC = new PAPPhotoDetailsViewController(photo);
                getNavigationController().pushViewController(photoDetailsVC, true);
            }
        }
    };

    private int getIndexForObjectAt(NSIndexPath indexPath) {
        return indexPath.getRow() / 2;
    }

    @Override
    public void didTapUserButton(PAPPhotoHeaderView photoHeaderView, UIButton button, PAPUser user) {
        PAPAccountViewController accountViewController = new PAPAccountViewController(user);
        Log.d("Presenting account view controller with user: %s", user);
        accountViewController.setUser(user);
        getNavigationController().pushViewController(accountViewController, true);
    }

    @Override
    public void didTapLikePhotoButton(PAPPhotoHeaderView photoHeaderView, final UIButton button, PAPPhoto photo) {
        photoHeaderView.shouldEnableLikeButton(false);

        boolean liked = !button.isSelected();
        photoHeaderView.setLikeStatus(liked);

        final String originalButtonTitle = button.getTitleLabel().getText();

        int likeCount = Integer.valueOf(button.getTitleLabel().getText());
        if (liked) {
            likeCount = likeCount + 1;
            PAPCache.getSharedCache().incrementPhotoLikerCount(photo);
        } else {
            if (likeCount > 0) {
                likeCount = likeCount - 1;
            }
            PAPCache.getSharedCache().decrementPhotoLikerCount(photo);
        }

        PAPCache.getSharedCache().setPhotoIsLikedByCurrentUser(photo, liked);

        button.setTitle(String.valueOf(likeCount), UIControlState.Normal);

        if (liked) {
            PAPUtility.likePhotoInBackground(photo, new PFSaveCallback() {
                @Override
                public void done(boolean success, NSError error) {
                    PAPPhotoHeaderView actualHeaderView = (PAPPhotoHeaderView) getViewForHeader(getTableView(),
                            button.getTag());
                    actualHeaderView.shouldEnableLikeButton(true);
                    actualHeaderView.setLikeStatus(success);

                    if (!success) {
                        actualHeaderView.getLikeButton().setTitle(originalButtonTitle, UIControlState.Normal);
                    }
                }
            });
        } else {
            PAPUtility.unlikePhotoInBackground(photo, new PFSaveCallback() {
                @Override
                public void done(boolean success, NSError error) {
                    PAPPhotoHeaderView actualHeaderView = (PAPPhotoHeaderView) getViewForHeader(getTableView(),
                            button.getTag());
                    actualHeaderView.shouldEnableLikeButton(true);
                    actualHeaderView.setLikeStatus(!success);

                    if (!success) {
                        actualHeaderView.getLikeButton().setTitle(originalButtonTitle, UIControlState.Normal);
                    }
                }
            });
        }
    }

    @Override
    public void didTapCommentOnPhotoButton(PAPPhotoHeaderView photoHeaderView, UIButton button, PAPPhoto photo) {
        PAPPhotoDetailsViewController photoDetailsVC = new PAPPhotoDetailsViewController(photo);
        getNavigationController().pushViewController(photoDetailsVC, true);
    }
}
