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
package org.robovm.samples.robopods.parse.anypic.ios.ui.controllers.friends;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.robovm.apple.addressbook.ABPerson;
import org.robovm.apple.addressbook.ABPersonProperty;
import org.robovm.apple.addressbook.ABProperty;
import org.robovm.apple.addressbookui.ABPeoplePickerNavigationController;
import org.robovm.apple.addressbookui.ABPeoplePickerNavigationControllerDelegate;
import org.robovm.apple.addressbookui.ABPeoplePickerNavigationControllerDelegateAdapter;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.dispatch.DispatchQueue;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSDate;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSMutableArray;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.foundation.NSTimer;
import org.robovm.apple.messageui.MFMailComposeResult;
import org.robovm.apple.messageui.MFMailComposeViewController;
import org.robovm.apple.messageui.MFMailComposeViewControllerDelegate;
import org.robovm.apple.messageui.MFMailComposeViewControllerDelegateAdapter;
import org.robovm.apple.messageui.MFMessageComposeViewController;
import org.robovm.apple.messageui.MFMessageComposeViewControllerDelegate;
import org.robovm.apple.messageui.MFMessageComposeViewControllerDelegateAdapter;
import org.robovm.apple.messageui.MessageComposeResult;
import org.robovm.apple.uikit.NSAttributedStringAttributes;
import org.robovm.apple.uikit.NSStringDrawingOptions;
import org.robovm.apple.uikit.UIActionSheet;
import org.robovm.apple.uikit.UIActionSheetDelegateAdapter;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonItemStyle;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIButtonType;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCellSelectionStyle;
import org.robovm.apple.uikit.UITableViewCellSeparatorStyle;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UITableViewRowAnimation;
import org.robovm.apple.uikit.UITableViewStyle;
import org.robovm.apple.uikit.UIView;
import org.robovm.objc.block.VoidBlock1;
import org.robovm.pods.parse.PFCachePolicy;
import org.robovm.pods.parse.PFCountCallback;
import org.robovm.pods.parse.PFQuery;
import org.robovm.pods.parse.PFSaveCallback;
import org.robovm.pods.parse.ui.PFQueryTableViewController;
import org.robovm.pods.parse.ui.PFTableViewCell;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPActivity;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPActivityType;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPCache;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPPhoto;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPUser;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPUserAttributes;
import org.robovm.samples.robopods.parse.anypic.ios.ui.controllers.home.PAPAccountViewController;
import org.robovm.samples.robopods.parse.anypic.ios.ui.views.tablecells.PAPFindFriendsCell;
import org.robovm.samples.robopods.parse.anypic.ios.ui.views.tablecells.PAPFindFriendsCellDelegate;
import org.robovm.samples.robopods.parse.anypic.ios.ui.views.tablecells.PAPLoadMoreCell;
import org.robovm.samples.robopods.parse.anypic.ios.util.Log;
import org.robovm.samples.robopods.parse.anypic.ios.util.PAPNotification;
import org.robovm.samples.robopods.parse.anypic.ios.util.PAPNotificationManager;
import org.robovm.samples.robopods.parse.anypic.ios.util.PAPUtility;

public class PAPFindFriendsViewController extends PFQueryTableViewController<PAPUser> implements
        PAPFindFriendsCellDelegate {
    private static final List<String> PARSE_EMPLOYEE_ACCOUNTS = Arrays.asList("400680", "403902", "1225726", "4806789",
            "6409809", "12800553", "121800083", "500011038", "558159381", "723748661");

    private UIView headerView;
    private PAPFindFriendsFollowStatus followStatus;
    private String selectedEmailAddress;
    private final Map<NSIndexPath, Boolean> outstandingFollowQueries;
    private final Map<NSIndexPath, Boolean> outstandingCountQueries;

    public PAPFindFriendsViewController() {
        this(UITableViewStyle.Plain);
    }

    public PAPFindFriendsViewController(UITableViewStyle style) {
        super(style, PAPUser.class);

        outstandingFollowQueries = new HashMap<>();
        outstandingCountQueries = new HashMap<>();

        selectedEmailAddress = "";

        // Whether the built-in pull-to-refresh is enabled
        setPullToRefreshEnabled(true);

        // The number of objects to show per page
        setObjectsPerPage(15);

        // Used to determine Follow/Unfollow All button status
        followStatus = PAPFindFriendsFollowStatus.SOME;
    }

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        getTableView().setSeparatorStyle(UITableViewCellSeparatorStyle.None);
        getTableView().setBackgroundColor(UIColor.black());

        getNavigationItem().setTitleView(new UIImageView(UIImage.create("TitleFindFriends")));

        if (getNavigationController().getViewControllers().first() == this) {
            UIBarButtonItem dismissLeftBarButtonItem = new UIBarButtonItem("Back", UIBarButtonItemStyle.Plain,
                    new UIBarButtonItem.OnClickListener() {
                        @Override
                        public void onClick(UIBarButtonItem barButtonItem) {
                            getNavigationController().dismissViewController(true, null);
                        }
                    });
            getNavigationItem().setLeftBarButtonItem(dismissLeftBarButtonItem);
        } else {
            getNavigationItem().setLeftBarButtonItem(null);
        }

        if (MFMailComposeViewController.canSendMail() || MFMessageComposeViewController.canSendText()) {
            headerView = new UIView(new CGRect(0, 0, 320, 67));
            headerView.setBackgroundColor(UIColor.black());
            UIButton clearButton = UIButton.create(UIButtonType.Custom);
            clearButton.setBackgroundColor(UIColor.clear());
            clearButton.addOnTouchUpInsideListener(inviteFriendsButtonAction);
            clearButton.setFrame(headerView.getFrame());
            headerView.addSubview(clearButton);
            String inviteString = "Invite friends";
            CGRect boundingRect = NSString.getBoundingRect(inviteString, new CGSize(310, Float.MAX_VALUE),
                    NSStringDrawingOptions.with(NSStringDrawingOptions.TruncatesLastVisibleLine,
                            NSStringDrawingOptions.UsesLineFragmentOrigin),
                    new NSAttributedStringAttributes().setFont(UIFont.getBoldSystemFont(18)), null);
            CGSize inviteStringSize = boundingRect.getSize();

            UILabel inviteLabel = new UILabel(new CGRect(10,
                    (headerView.getFrame().getSize().getHeight() - inviteStringSize.getHeight()) / 2,
                    inviteStringSize.getWidth(), inviteStringSize.getHeight()));
            inviteLabel.setText(inviteString);
            inviteLabel.setFont(UIFont.getBoldSystemFont(18));
            inviteLabel.setTextColor(UIColor.white());
            inviteLabel.setBackgroundColor(UIColor.clear());
            headerView.addSubview(inviteLabel);
            getTableView().setTableHeaderView(headerView);
        }
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);
        getTableView().setSeparatorColor(UIColor.fromRGBA(30f / 255f, 30f / 255f, 30f / 255f, 1));
    }

    @Override
    public double getHeightForRow(UITableView tableView, NSIndexPath indexPath) {
        if (indexPath.getRow() < getObjects().size()) {
            return PAPFindFriendsCell.getHeightForCell();
        } else {
            return 44;
        }
    }

    @Override
    public PFQuery<PAPUser> getQuery() {
        // Use cached facebook friend ids
        List<String> facebookFriends = PAPCache.getSharedCache().getFacebookFriends();

        // Query for all friends you have on facebook and who are using the app
        PFQuery<PAPUser> friendsQuery = PFQuery.getQuery(PAPUser.class);
        friendsQuery.whereContainedIn(PAPUser.FACEBOOK_ID_KEY, facebookFriends);

        // Query for all Parse employees
        List<String> parseEmployees = new ArrayList<>(PARSE_EMPLOYEE_ACCOUNTS);
        parseEmployees.remove(PAPUser.getCurrentUser().getFacebookId());
        PFQuery<PAPUser> parseEmployeeQuery = PFQuery.getQuery(PAPUser.class);
        parseEmployeeQuery.whereContainedIn(PAPUser.FACEBOOK_ID_KEY, parseEmployees);

        PFQuery<PAPUser> query = PFQuery.or(new NSArray<PFQuery<?>>(friendsQuery, parseEmployeeQuery));
        query.setCachePolicy(PFCachePolicy.NetworkOnly);

        if (getObjects().size() == 0) {
            query.setCachePolicy(PFCachePolicy.CacheThenNetwork);
        }

        query.orderByAscending(PAPUser.DISPLAY_NAME_KEY);

        return query;
    }

    @Override
    public void didLoadObjects(NSError error) {
        super.didLoadObjects(error);

        PFQuery<PAPActivity> isFollowingQuery = PFQuery.getQuery(PAPActivity.class);
        isFollowingQuery.whereEqualTo(PAPActivity.FROM_USER_KEY, PAPUser.getCurrentUser());
        isFollowingQuery.whereEqualTo(PAPActivity.TYPE_KEY, PAPActivityType.FOLLOW.getKey());
        isFollowingQuery.whereContainedIn(PAPActivity.TO_USER_KEY, getObjects());
        isFollowingQuery.whereContainedIn(PAPActivity.TO_USER_KEY, getObjects());
        isFollowingQuery.setCachePolicy(PFCachePolicy.NetworkOnly);

        isFollowingQuery.countInBackground(new PFCountCallback() {
            @Override
            public void done(int count, NSError error) {
                NSArray<PAPUser> objects = getObjects();
                if (error == null) {
                    if (count == objects.size()) {
                        followStatus = PAPFindFriendsFollowStatus.ALL;
                        configureUnfollowAllButton();
                        for (PAPUser user : objects) {
                            PAPCache.getSharedCache().setUserFollowStatus(user, true);
                        }
                    } else if (count == 0) {
                        followStatus = PAPFindFriendsFollowStatus.NONE;
                        configureFollowAllButton();
                        for (PAPUser user : objects) {
                            PAPCache.getSharedCache().setUserFollowStatus(user, false);
                        }
                    } else {
                        followStatus = PAPFindFriendsFollowStatus.SOME;
                        configureFollowAllButton();
                    }
                }

                if (objects.size() == 0) {
                    getNavigationItem().setRightBarButtonItem(null);
                }
            }
        });

        if (getObjects().size() == 0) {
            getNavigationItem().setRightBarButtonItem(null);
        }
    }

    @Override
    public PFTableViewCell getCellForRow(final UITableView tableView, final NSIndexPath indexPath, final PAPUser user) {
        final String friendCellIdentifier = "FriendCell";

        PAPFindFriendsCell cell = (PAPFindFriendsCell) tableView.dequeueReusableCell(friendCellIdentifier);
        if (cell == null) {
            cell = new PAPFindFriendsCell(UITableViewCellStyle.Default, friendCellIdentifier);
            cell.setDelegate(this);
        }

        cell.setUser(user);

        cell.getPhotoLabel().setText("0 photos");

        PAPUserAttributes attributes = PAPCache.getSharedCache().getUserAttributes(user);

        if (attributes != null) {
            // set them now
            int count = PAPCache.getSharedCache().getUserPhotoCount(user);
            cell.getPhotoLabel().setText(String.format("%d photo%s", count, count == 1 ? "" : "s"));
        } else {
            synchronized (this) {
                Boolean outstandingCountQueryStatus = outstandingCountQueries.get(indexPath);
                if (outstandingCountQueryStatus == null || !outstandingCountQueryStatus) {
                    outstandingCountQueries.put(indexPath, true);
                    PFQuery<PAPPhoto> photoNumQuery = PFQuery.getQuery(PAPPhoto.class);
                    photoNumQuery.whereEqualTo(PAPPhoto.USER_KEY, user);
                    photoNumQuery.setCachePolicy(PFCachePolicy.CacheThenNetwork);
                    photoNumQuery.countInBackground(new PFCountCallback() {
                        @Override
                        public void done(int count, NSError error) {
                            synchronized (PAPFindFriendsViewController.this) {
                                PAPCache.getSharedCache().setUserPhotoCount(user, count);
                                outstandingCountQueries.remove(indexPath);
                            }
                            PAPFindFriendsCell actualCell = (PAPFindFriendsCell) tableView.getCellForRow(indexPath);
                            actualCell.getPhotoLabel().setText(
                                    String.format("%d photo%s", count, count == 1 ? "" : "s"));
                        }
                    });
                }
            }
        }

        cell.getFollowButton().setSelected(false);
        cell.setTag(indexPath.getRow());

        if (followStatus == PAPFindFriendsFollowStatus.SOME) {
            if (attributes != null) {
                cell.getFollowButton().setSelected(PAPCache.getSharedCache().getUserFollowStatus(user));
            } else {
                synchronized (this) {
                    final PAPFindFriendsCell c = cell;

                    Boolean outstandingQuery = outstandingFollowQueries.get(indexPath);
                    if (outstandingQuery == null || !outstandingQuery) {
                        outstandingFollowQueries.put(indexPath, true);
                        PFQuery<PAPActivity> isFollowingQuery = PFQuery.getQuery(PAPActivity.class);
                        isFollowingQuery.whereEqualTo(PAPActivity.FROM_USER_KEY, PAPUser.getCurrentUser());
                        isFollowingQuery.whereEqualTo(PAPActivity.TYPE_KEY, PAPActivityType.FOLLOW.getKey());
                        isFollowingQuery.whereEqualTo(PAPActivity.TO_USER_KEY, user);
                        isFollowingQuery.setCachePolicy(PFCachePolicy.CacheThenNetwork);

                        isFollowingQuery.countInBackground(new PFCountCallback() {
                            @Override
                            public void done(int count, NSError error) {
                                synchronized (this) {
                                    outstandingFollowQueries.remove(indexPath);
                                    PAPCache.getSharedCache().setUserFollowStatus(user, error == null && count > 0);
                                }
                                if (c.getTag() == indexPath.getRow()) {
                                    c.getFollowButton().setSelected(error == null && count > 0);
                                }
                            }
                        });
                    }
                }
            }
        } else {
            cell.getFollowButton().setSelected(followStatus == PAPFindFriendsFollowStatus.ALL);
        }

        return cell;
    }

    @Override
    public PFTableViewCell getCellForNextPage(UITableView tableView, NSIndexPath indexPath) {
        final String cellID = "NextPageCell";

        PAPLoadMoreCell cell = (PAPLoadMoreCell) tableView.dequeueReusableCell(cellID);

        if (cell == null) {
            cell = new PAPLoadMoreCell(UITableViewCellStyle.Default, cellID);
            cell.getMainView().setBackgroundColor(UIColor.black());
            cell.setHideSeparatorBottom(true);
            cell.setHideSeparatorTop(true);
        }

        cell.setSelectionStyle(UITableViewCellSelectionStyle.None);

        return cell;
    }

    @Override
    public void didTapUserButton(PAPFindFriendsCell cellView, PAPUser user) {
        // Push account view controller
        PAPAccountViewController accountViewController = new PAPAccountViewController(UITableViewStyle.Plain);
        Log.d("Presenting account view controller with user: %s", user);
        accountViewController.setUser(user);
        getNavigationController().pushViewController(accountViewController, true);
    }

    @Override
    public void didTapFollowButton(PAPFindFriendsCell cellView, PAPUser user) {
        shouldToggleFollowFriendForCell(cellView);
    }

    private final ABPeoplePickerNavigationControllerDelegate peoplePickerDelegate = new ABPeoplePickerNavigationControllerDelegateAdapter() {
        /**
         * Called when the user cancels the address book view controller. We
         * simply dismiss it.
         */
        @Override
        public void didCancel(ABPeoplePickerNavigationController peoplePicker) {
            dismissViewController(true, null);
        }

        /**
         * Called when a member of the address book is selected, we return true
         * to display the member's details.
         */
        @Override
        public boolean shouldContinueAfterSelectingPerson(ABPeoplePickerNavigationController peoplePicker,
                ABPerson person) {
            return true;
        };

        /**
         * Called when the user selects a property of a person in their address
         * book (ex. phone, email, location,...) This method will allow them to
         * send a text or email inviting them to Anypic.
         */
        @Override
        public boolean shouldContinueAfterSelectingPerson(ABPeoplePickerNavigationController peoplePicker,
                ABPerson person, ABProperty property, int identifier) {

            if (property == ABPersonProperty.Email) {
                String email = person.getEmailAddresses().get(identifier).getAddress();
                selectedEmailAddress = email;

                if (MFMailComposeViewController.canSendMail() && MFMessageComposeViewController.canSendText()) {
                    // ask user
                    UIActionSheet actionSheet = new UIActionSheet("Invite", new UIActionSheetDelegateAdapter() {
                        @Override
                        public void clicked(UIActionSheet actionSheet, long buttonIndex) {
                            if (buttonIndex == actionSheet.getCancelButtonIndex()) {
                                return;
                            }

                            if (buttonIndex == 0) {
                                presentMailComposeViewController(selectedEmailAddress);
                            } else if (buttonIndex == 1) {
                                presentMessageComposeViewController(selectedEmailAddress);
                            }
                        }
                    }, "Cancel", null, "Email", "iMessage");
                    actionSheet.showFrom(getTabBarController().getTabBar());
                } else if (MFMailComposeViewController.canSendMail()) {
                    // go directly to mail
                    presentMailComposeViewController(email);
                } else if (MFMessageComposeViewController.canSendText()) {
                    // go directly to iMessage
                    presentMessageComposeViewController(email);
                }
            } else if (property == ABPersonProperty.Phone) {
                String phone = person.getPhoneNumbers().get(identifier).getNumber();

                if (MFMessageComposeViewController.canSendText()) {
                    presentMessageComposeViewController(phone);
                }
            }

            return false;
        }

    };

    private final UIControl.OnTouchUpInsideListener inviteFriendsButtonAction = new UIControl.OnTouchUpInsideListener() {
        @Override
        public void onTouchUpInside(UIControl control, UIEvent event) {
            ABPeoplePickerNavigationController addressBook = new ABPeoplePickerNavigationController();
            addressBook.setPeoplePickerDelegate(peoplePickerDelegate);

            if (MFMailComposeViewController.canSendMail() && MFMessageComposeViewController.canSendText()) {
                addressBook.setDisplayedProperties(Arrays.asList(ABPersonProperty.Email, ABPersonProperty.Phone));
            } else if (MFMailComposeViewController.canSendMail()) {
                addressBook.setDisplayedProperties(Arrays.asList(ABPersonProperty.Email));
            } else if (MFMessageComposeViewController.canSendText()) {
                addressBook.setDisplayedProperties(Arrays.asList(ABPersonProperty.Phone));
            }
            presentViewController(addressBook, true, null);
        }
    };

    private final UIBarButtonItem.OnClickListener followAllFriendsButtonAction = new UIBarButtonItem.OnClickListener() {
        @Override
        public void onClick(UIBarButtonItem barButtonItem) {
//    [MBProgressHUD showHUDAddedTo:[UIApplication sharedApplication].keyWindow animated:YES]; TODO
            followStatus = PAPFindFriendsFollowStatus.ALL;
            configureUnfollowAllButton();

            DispatchQueue.getMainQueue().after(10, TimeUnit.MILLISECONDS, new Runnable() {
                @Override
                public void run() {
                    getNavigationItem().setRightBarButtonItem(
                            new UIBarButtonItem("Unfollow All", UIBarButtonItemStyle.Plain,
                                    unfollowAllFriendsButtonAction));

                    NSArray<NSIndexPath> indexPaths = new NSMutableArray<>();
                    NSArray<PAPUser> objects = getObjects();
                    UITableView tableView = getTableView();
                    for (int i = 0, n = objects.size(); i < n; i++) {
                        PAPUser user = objects.get(i);
                        NSIndexPath indexPath = NSIndexPath.createWithRow(i, 0);
                        PAPFindFriendsCell cell = (PAPFindFriendsCell) getCellForRow(tableView, indexPath, user);
                        cell.getFollowButton().setSelected(true);
                        indexPaths.add(indexPath);
                    }

                    tableView.reloadRows(indexPaths, UITableViewRowAnimation.None);
//        [MBProgressHUD hideAllHUDsForView:[UIApplication sharedApplication].keyWindow animated:YES]; TODO

                    final NSTimer timer = NSTimer.createScheduled(2, followUsersTimerRunnable, false);
                    PAPUtility.followUsersEventually(objects, new PFSaveCallback() {
                        @Override
                        public void done(boolean success, NSError error) {
                            // note -- this block is called once for every user
                            // that is followed successfully. We use a timer to
                            // only execute the completion block once no more
                            // saveEventually blocks have been called in 2
                            // seconds
                            timer.setFireDate(NSDate.createWithTimeIntervalSinceNow(2));
                        }
                    });
                }
            });
        }
    };

    private final UIBarButtonItem.OnClickListener unfollowAllFriendsButtonAction = new UIBarButtonItem.OnClickListener() {
        @Override
        public void onClick(UIBarButtonItem barButtonItem) {
//    [MBProgressHUD showHUDAddedTo:[UIApplication sharedApplication].keyWindow animated:YES]; TODO
            configureFollowAllButton();

            DispatchQueue.getMainQueue().after(10, TimeUnit.MILLISECONDS, new Runnable() {
                @Override
                public void run() {
                    getNavigationItem()
                            .setRightBarButtonItem(
                                    new UIBarButtonItem("Follow All", UIBarButtonItemStyle.Plain,
                                            followAllFriendsButtonAction));

                    NSArray<NSIndexPath> indexPaths = new NSMutableArray<>();
                    NSArray<PAPUser> objects = getObjects();
                    UITableView tableView = getTableView();
                    for (int i = 0, n = objects.size(); i < n; i++) {
                        PAPUser user = objects.get(i);
                        NSIndexPath indexPath = NSIndexPath.createWithRow(i, 0);
                        PAPFindFriendsCell cell = (PAPFindFriendsCell) getCellForRow(tableView, indexPath, user);
                        cell.getFollowButton().setSelected(false);
                        indexPaths.add(indexPath);
                    }

                    tableView.reloadRows(indexPaths, UITableViewRowAnimation.None);
//        [MBProgressHUD hideAllHUDsForView:[UIApplication sharedApplication].keyWindow animated:YES]; TODO
                    PAPUtility.unfollowUsersEventually(objects);

                    PAPNotificationManager.postNotification(PAPNotification.USER_FOLLOWING_CHANGED);
                }
            });
        }
    };

    private void shouldToggleFollowFriendForCell(final PAPFindFriendsCell cell) {
        PAPUser cellUser = cell.getUser();
        if (cell.getFollowButton().isSelected()) {
            // Unfollow
            cell.getFollowButton().setSelected(false);
            PAPUtility.unfollowUserEventually(cellUser);
            PAPNotificationManager.postNotification(PAPNotification.USER_FOLLOWING_CHANGED);
        } else {
            // Follow
            cell.getFollowButton().setSelected(true);
            PAPUtility.followUserEventually(cellUser, new PFSaveCallback() {
                @Override
                public void done(boolean success, NSError error) {
                    if (error == null) {
                        PAPNotificationManager.postNotification(PAPNotification.USER_FOLLOWING_CHANGED);
                    } else {
                        cell.getFollowButton().setSelected(false);
                    }
                }
            });
        }
    }

    private void configureUnfollowAllButton() {
        getNavigationItem().setRightBarButtonItem(
                new UIBarButtonItem("Unfollow All", UIBarButtonItemStyle.Plain, unfollowAllFriendsButtonAction));
    }

    private void configureFollowAllButton() {
        getNavigationItem().setRightBarButtonItem(
                new UIBarButtonItem("Follow All", UIBarButtonItemStyle.Plain, followAllFriendsButtonAction));
    }

    private void presentMailComposeViewController(String recipient) {
        // Create the compose email view controller
        MFMailComposeViewController composeEmailViewController = new MFMailComposeViewController();

        // Set the recipient to the selected email and a default text
        composeEmailViewController.setMailComposeDelegate(mailComposeDelegate);
        composeEmailViewController.setSubject("Join me on Anypic");
        composeEmailViewController.setToRecipients(Arrays.asList(recipient));
        composeEmailViewController
                .setMessageBody(
                        "<h2>Share your pictures, share your story.</h2><p><a href=\"http://anypic.org\">Anypic</a> is the easiest way to share photos with your friends. Get the app and share your fun photos with the world.</p><p><a href=\"http://anypic.org\">Anypic</a> is fully powered by <a href=\"http://parse.com\">Parse</a>.</p>",
                        true);

        // Dismiss the current modal view controller and display the compose
        // email one.
        // Note that we do not animate them. Doing so would require us to
        // present the compose
        // mail one only *after* the address book is dismissed.
        dismissViewController(false, null);
        presentViewController(composeEmailViewController, false, null);
    }

    private void presentMessageComposeViewController(String recipient) {
        // Create the compose text message view controller
        MFMessageComposeViewController composeTextViewController = new MFMessageComposeViewController();

        // Send the destination phone number and a default text
        composeTextViewController.setMessageComposeDelegate(messageComposeDelegate);
        composeTextViewController.setRecipients(Arrays.asList(recipient));
        composeTextViewController.setBody("Check out Anypic! http://anypic.org");

        // Dismiss the current modal view controller and display the compose
        // text one.
        // See previous use for reason why these are not animated.
        dismissViewController(false, null);
        presentViewController(composeTextViewController, false, null);
    }

    private final VoidBlock1<NSTimer> followUsersTimerRunnable = new VoidBlock1<NSTimer>() {
        @Override
        public void invoke(NSTimer timer) {
            getTableView().reloadData();
            PAPNotificationManager.postNotification(PAPNotification.USER_FOLLOWING_CHANGED);
        }
    };

    private final MFMailComposeViewControllerDelegate mailComposeDelegate = new MFMailComposeViewControllerDelegateAdapter() {
        @Override
        public void didFinish(MFMailComposeViewController controller, MFMailComposeResult result, NSError error) {
            dismissViewController(true, null);
        }
    };

    private final MFMessageComposeViewControllerDelegate messageComposeDelegate = new MFMessageComposeViewControllerDelegateAdapter() {
        @Override
        public void didFinish(MFMessageComposeViewController controller, MessageComposeResult result) {
            dismissViewController(true, null);
        }
    };
}
