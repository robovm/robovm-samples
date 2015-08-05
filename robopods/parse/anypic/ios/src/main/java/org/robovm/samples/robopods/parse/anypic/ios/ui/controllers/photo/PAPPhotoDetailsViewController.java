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
import java.util.List;

import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSMutableDictionary;
import org.robovm.apple.foundation.NSNotification;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSTimer;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.uikit.UIActionSheet;
import org.robovm.apple.uikit.UIActionSheetDelegate;
import org.robovm.apple.uikit.UIActionSheetDelegateAdapter;
import org.robovm.apple.uikit.UIActivityViewController;
import org.robovm.apple.uikit.UIAlertView;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonSystemItem;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIKeyboardAnimation;
import org.robovm.apple.uikit.UIScrollView;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCellSeparatorStyle;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UITableViewStyle;
import org.robovm.apple.uikit.UITextField;
import org.robovm.apple.uikit.UITextFieldDelegateAdapter;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIWindow;
import org.robovm.objc.block.VoidBlock1;
import org.robovm.pods.parse.PFACL;
import org.robovm.pods.parse.PFCachePolicy;
import org.robovm.pods.parse.PFError;
import org.robovm.pods.parse.PFErrorCode;
import org.robovm.pods.parse.PFFindCallback;
import org.robovm.pods.parse.PFGetDataCallback;
import org.robovm.pods.parse.PFObject;
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
import org.robovm.samples.robopods.parse.anypic.ios.ui.views.photodetails.PAPPhotoDetailsFooterView;
import org.robovm.samples.robopods.parse.anypic.ios.ui.views.photodetails.PAPPhotoDetailsHeaderView;
import org.robovm.samples.robopods.parse.anypic.ios.ui.views.photodetails.PAPPhotoDetailsHeaderViewDelegate;
import org.robovm.samples.robopods.parse.anypic.ios.ui.views.tablecells.PAPActivityCell;
import org.robovm.samples.robopods.parse.anypic.ios.ui.views.tablecells.PAPBaseTextCell;
import org.robovm.samples.robopods.parse.anypic.ios.ui.views.tablecells.PAPBaseTextCellDelegate;
import org.robovm.samples.robopods.parse.anypic.ios.ui.views.tablecells.PAPLoadMoreCell;
import org.robovm.samples.robopods.parse.anypic.ios.util.Log;
import org.robovm.samples.robopods.parse.anypic.ios.util.PAPNotification;
import org.robovm.samples.robopods.parse.anypic.ios.util.PAPNotificationManager;
import org.robovm.samples.robopods.parse.anypic.ios.util.PAPUtility;

public class PAPPhotoDetailsViewController extends PFQueryTableViewController<PAPActivity> implements
        PAPBaseTextCellDelegate, PAPPhotoDetailsHeaderViewDelegate {
    private static final double CELL_INSET_WIDTH = 0;

    private UITextField commentTextField;
    private PAPPhotoDetailsHeaderView headerView;
    private boolean likersQueryInProgress;

    private final PAPPhoto photo;

    private NSObject willShowKeyboardNotification;
    private NSObject userDidLikePhotoNotification;

    @Override
    protected void dispose(boolean finalizing) {
        super.dispose(finalizing);

        PAPNotificationManager.removeObserver(willShowKeyboardNotification);
        PAPNotificationManager.removeObserver(userDidLikePhotoNotification);
    }

    public PAPPhotoDetailsViewController(PAPPhoto photo) {
        super(UITableViewStyle.Plain, PAPActivity.class);

        // Whether the built-in pull-to-refresh is enabled
        setPullToRefreshEnabled(true);

        // Whether the built-in pagination is enabled
        setPaginationEnabled(true);

        // The number of comments to show per page
        setObjectsPerPage(30);

        this.photo = photo;

        likersQueryInProgress = false;
    }

    @Override
    public void viewDidLoad() {
        getTableView().setSeparatorStyle(UITableViewCellSeparatorStyle.None);

        super.viewDidLoad();

        getNavigationItem().setTitleView(new UIImageView(UIImage.create("LogoNavigationBar")));

        // Set table view properties
        UIView texturedBackgroundView = new UIView(getView().getBounds());
        texturedBackgroundView.setBackgroundColor(UIColor.black());

        getTableView().setBackgroundView(texturedBackgroundView);

        // Set table header
        headerView = new PAPPhotoDetailsHeaderView(PAPPhotoDetailsHeaderView.getRectForView(), photo);
        headerView.setDelegate(this);

        getTableView().setTableHeaderView(headerView);

        // Set table footer
        PAPPhotoDetailsFooterView footerView = new PAPPhotoDetailsFooterView(PAPPhotoDetailsFooterView.getRectForView());
        commentTextField = footerView.getCommentField();
        commentTextField.setDelegate(new UITextFieldDelegateAdapter() {
            @Override
            public boolean shouldReturn(UITextField textField) {
                String trimmedComment = textField.getText().trim();
                if (trimmedComment.length() != 0 && photo.getUser() != null) {
                    PAPActivity comment = PFObject.create(PAPActivity.class);
                    comment.setContent(trimmedComment);
                    comment.setToUser(photo.getUser());
                    comment.setFromUser(PAPUser.getCurrentUser());
                    comment.setType(PAPActivityType.COMMENT);
                    comment.setPhoto(photo);

                    PFACL acl = new PFACL(PAPUser.getCurrentUser());
                    acl.setPublicReadAccess(true);
                    acl.setWriteAccess(photo.getUser(), true);
                    comment.setACL(acl);

                    PAPCache.getSharedCache().incrementPhotoCommentCount(photo);

                    // Show HUD view
//                    [MBProgressHUD showHUDAddedTo:self.view.superview animated:YES]; TODO

                    // If more than 5 seconds pass since we post a comment, stop
                    // waiting for the server to respond
                    final NSTimer timer = NSTimer.createScheduled(5, new VoidBlock1<NSTimer>() {
                        @Override
                        public void invoke(NSTimer timer) {
//                            [MBProgressHUD hideHUDForView:self.view.superview animated:YES]; TODO
                            UIAlertView alert = new UIAlertView("New Comment",
                                    "Your comment will be posted next time there is an Internet connection.",
                                    null, null, "Dismiss");
                            alert.show();
                        }
                    }, false);

                    comment.saveEventually(new PFSaveCallback() {
                        @Override
                        public void done(boolean success, NSError error) {
                            timer.invalidate();

                            if (error != null && error instanceof PFError
                                    && ((PFError) error).getErrorCode() == PFErrorCode.ObjectNotFound) {
                                PAPCache.getSharedCache().decrementPhotoCommentCount(photo);
                                UIAlertView alert = new UIAlertView("Could not post comment",
                                        "This photo is no longer available",
                                        null, null, "OK");
                                alert.show();
                                getNavigationController().popViewController(true);
                            }

                            NSDictionary<?, ?> notificationPayload = new NSMutableDictionary<>();
                            notificationPayload.put("comments", getObjects().size() + 1);
                            PAPNotificationManager.postNotification(PAPNotification.USER_COMMENTED_ON_PHOTO, photo,
                                    notificationPayload);

//                            [MBProgressHUD hideHUDForView:self.view.superview animated:YES]; TODO
                            loadObjects();
                        }
                    });
                }

                textField.setText("");
                return textField.resignFirstResponder();
            }
        });
        getTableView().setTableFooterView(footerView);

        getNavigationItem().setRightBarButtonItem(
                new UIBarButtonItem(UIBarButtonSystemItem.Action, activityButtonAction));

        // Register to be notified when the keyboard will be shown to scroll the
        // view
        willShowKeyboardNotification = UIWindow.Notifications
                .observeKeyboardWillShow(new VoidBlock1<UIKeyboardAnimation>() {
                    @Override
                    public void invoke(UIKeyboardAnimation animation) {
                        // Scroll the view to the comment text box

                        CGSize kbSize = animation.getStartFrame().getSize();
                        getTableView().setContentOffset(
                                new CGPoint(0, getTableView().getContentSize().getHeight() - kbSize.getHeight()), true);
                    }
                });
        userDidLikePhotoNotification = PAPNotificationManager.addObserver(
                PAPNotification.USER_LIKING_PHOTO_CALLBACK_FINISHED, photo, new VoidBlock1<NSNotification>() {
                    @Override
                    public void invoke(NSNotification a) {
                        headerView.reloadLikeBar();
                    }
                });
    }

    @Override
    public void viewDidAppear(boolean animated) {
        super.viewDidAppear(animated);

        headerView.reloadLikeBar();

        // we will only hit the network if we have no cached data for this photo
        boolean hasCachedLikers = PAPCache.getSharedCache().getPhotoAttributes(photo) != null;
        if (!hasCachedLikers) {
            loadLikers();
        }
    }

    @Override
    public double getHeightForRow(UITableView tableView, NSIndexPath indexPath) {
        NSArray<PAPActivity> objects = getObjects();
        if (indexPath.getRow() < objects.size()) { // A comment row
            PAPActivity object = objects.get(indexPath.getRow());

            if (object != null) {
                String commentString = object.getContent();

                PAPUser commentAuthor = object.getFromUser();

                String nameString = "";
                if (commentAuthor != null) {
                    nameString = commentAuthor.getDisplayName();
                }

                return PAPActivityCell.getHeightForCell(nameString, commentString, CELL_INSET_WIDTH);
            }
        }

        // The pagination row
        return 44;
    }

    @Override
    public PFQuery<PAPActivity> getQuery() {
        PFQuery<PAPActivity> query = PFQuery.getQuery(PAPActivity.class);
        query.whereEqualTo(PAPActivity.PHOTO_KEY, photo);
        query.include(PAPActivity.FROM_USER_KEY);
        query.whereEqualTo(PAPActivity.TYPE_KEY, PAPActivityType.COMMENT.getKey());
        query.orderByAscending("createdAt");

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

        return query;
    }

    @Override
    public void didLoadObjects(NSError error) {
        super.didLoadObjects(error);

        headerView.reloadLikeBar();
        loadLikers();
    }

    @Override
    public PFTableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath, PAPActivity activity) {
        final String cellID = "CommentCell";

        // Try to dequeue a cell and create one if necessary
        PAPBaseTextCell cell = (PAPBaseTextCell) tableView.dequeueReusableCell(cellID);
        if (cell == null) {
            cell = new PAPBaseTextCell(UITableViewCellStyle.Default, cellID);
            cell.setCellInsetWidth(CELL_INSET_WIDTH);
            cell.setDelegate(this);
        }

        cell.setUser(activity.getFromUser());
        cell.setContentText(activity.getContent());
        cell.setDate(activity.getCreatedAt());

        return cell;
    }

    @Override
    public PFTableViewCell getCellForNextPage(UITableView tableView, NSIndexPath indexPath) {
        final String cellIdentifier = "NextPageDetails";

        PAPLoadMoreCell cell = (PAPLoadMoreCell) tableView.dequeueReusableCell(cellIdentifier);

        if (cell == null) {
            cell = new PAPLoadMoreCell(UITableViewCellStyle.Default, cellIdentifier);
            cell.setCellInsetWidth(CELL_INSET_WIDTH);
            cell.setHideSeparatorTop(true);
        }

        return cell;
    }

    @Override
    public void willBeginDragging(UIScrollView scrollView) {
        commentTextField.resignFirstResponder();
    }

    @Override
    public void didTapUserButton(PAPBaseTextCell cellView, PAPUser user) {
        shouldPresentAccountView(user);
    }

    @Override
    public void didTapUserButton(PAPPhotoDetailsHeaderView headerView, UIButton button, PAPUser user) {
        shouldPresentAccountView(user);
    }

    private final UIBarButtonItem.OnClickListener actionButtonAction = new UIBarButtonItem.OnClickListener() {
        @Override
        public void onClick(UIBarButtonItem barButtonItem) {
            UIActionSheet actionSheet = new UIActionSheet();
            actionSheet.setDelegate(actionSheetDelegate);
            actionSheet.setTag(0);
            actionSheet.setDestructiveButtonIndex(actionSheet.addButton("Delete Photo"));
            if (Foundation.getMajorSystemVersion() >= 8) {
                actionSheet.addButton("Share Photo");
            }
            actionSheet.setCancelButtonIndex(actionSheet.addButton("Cancel"));
            actionSheet.showFrom(getTabBarController().getTabBar());
        }
    };

    private final UIActionSheetDelegate actionSheetDelegate = new UIActionSheetDelegateAdapter() {
        @Override
        public void clicked(UIActionSheet actionSheet, long buttonIndex) {
            if (actionSheet.getTag() == 0) {
                if (actionSheet.getDestructiveButtonIndex() == buttonIndex) {
                    // prompt to delete
                    UIActionSheet newSheet = new UIActionSheet("Are you sure you want to delete this photo?",
                            actionSheetDelegate,
                            "Cancel", "Yes, delete photo");
                    newSheet.setTag(1);
                    actionSheet.showFrom(getTabBarController().getTabBar());
                } else {
                    activityButtonAction.onClick(null);
                }
            } else if (actionSheet.getTag() == 1) {
                if (actionSheet.getDestructiveButtonIndex() == buttonIndex) {
                    shouldDeletePhoto();
                }
            }
        }
    };

    private final UIBarButtonItem.OnClickListener activityButtonAction = new UIBarButtonItem.OnClickListener() {
        @Override
        public void onClick(UIBarButtonItem barButtonItem) {
            if (photo.getPicture().isDataAvailable()) {
                showShareSheet();
            } else {
//        [MBProgressHUD showHUDAddedTo:self.view animated:YES]; TODO
                photo.getPicture().getDataInBackground(new PFGetDataCallback() {
                    @Override
                    public void done(NSData data, NSError error) {
//            [MBProgressHUD hideHUDForView:self.view animated:YES]; TODO
                        if (error == null) {
                            showShareSheet();
                        }
                    }
                });
            }
        }
    };

    private void showShareSheet() {
        photo.getPicture().getDataInBackground(new PFGetDataCallback() {
            @Override
            public void done(NSData data, NSError error) {
                if (error == null) {
                    List<Object> activityItems = new ArrayList<>(3);

                    NSArray<PAPActivity> activities = getObjects();

                    // Prefill caption if this is the original poster of the
                    // photo, and then only if they added a caption initially.
                    if (PAPUser.getCurrentUser().getObjectId().equals(photo.getUser().getObjectId())
                            && activities.size() > 0) {
                        PAPActivity firstActivity = activities.first();
                        if (firstActivity.getFromUser().getObjectId().equals(photo.getUser().getObjectId())) {
                            String commentString = firstActivity.getContent();
                            activityItems.add(commentString);
                        }
                    }

                    activityItems.add(UIImage.create(data));
                    activityItems.add(new NSURL(String.format("https://anypic.org/#pic/%s", photo.getObjectId())));

                    UIActivityViewController activityViewController = new UIActivityViewController(activityItems, null);
                    getNavigationController().presentViewController(activityViewController, true, null);
                }
            }
        });
    }

    private void shouldPresentAccountView(PAPUser user) {
        PAPAccountViewController accountViewController = new PAPAccountViewController(UITableViewStyle.Plain);
        Log.d("Presenting account view controller with user: %s", user);
        accountViewController.setUser(user);
        getNavigationController().pushViewController(accountViewController, true);
    }

    private void loadLikers() {
        if (likersQueryInProgress) {
            return;
        }

        likersQueryInProgress = true;
        PFQuery<PAPActivity> query = PAPUtility.queryActivities(photo, PFCachePolicy.NetworkOnly);
        query.findInBackground(new PFFindCallback<PAPActivity>() {
            @Override
            public void done(NSArray<PAPActivity> activities, NSError error) {
                likersQueryInProgress = false;
                if (error != null) {
                    headerView.reloadLikeBar();
                    return;
                }

                List<PAPUser> likers = new ArrayList<>();
                List<PAPUser> commenters = new ArrayList<>();

                boolean isLikedByCurrentUser = false;

                for (PAPActivity activity : activities) {
                    if (activity.getFromUser() != null) {
                        if (activity.getType() == PAPActivityType.LIKE) {
                            likers.add(activity.getFromUser());
                        } else if (activity.getType() == PAPActivityType.COMMENT) {
                            commenters.add(activity.getFromUser());
                        }

                        if (activity.getFromUser().getObjectId().equals(PAPUser.getCurrentUser().getObjectId())) {
                            if (activity.getType() == PAPActivityType.LIKE) {
                                isLikedByCurrentUser = true;
                            }
                        }
                    }
                }

                PAPCache.getSharedCache().setPhotoAttributes(photo,
                        new PAPPhotoAttributes(likers, commenters, isLikedByCurrentUser));
                headerView.reloadLikeBar();
            }
        });
    }

    private boolean currentUserOwnsPhoto() {
        return photo.getUser().getObjectId().equals(PAPUser.getCurrentUser().getObjectId());
    }

    private void shouldDeletePhoto() {
        // Delete all activites related to this photo
        PFQuery<PAPActivity> query = PFQuery.getQuery(PAPActivity.class);
        query.whereEqualTo(PAPActivity.PHOTO_KEY, photo);
        query.findInBackground(new PFFindCallback<PAPActivity>() {
            @Override
            public void done(NSArray<PAPActivity> objects, NSError error) {
                if (error == null) {
                    for (PAPActivity activity : objects) {
                        activity.deleteEventually();
                    }
                }

                // Delete photo
                photo.deleteEventually();
            }
        });
        PAPNotificationManager.postNotification(PAPNotification.USER_DELETED_PHOTO, photo.getObjectId());
        getNavigationController().popViewController(true);
    }
}
