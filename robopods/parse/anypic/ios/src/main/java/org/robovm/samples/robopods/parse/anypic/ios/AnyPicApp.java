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
package org.robovm.samples.robopods.parse.anypic.ios;

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSNumber;
import org.robovm.apple.foundation.NSNumberFormatter;
import org.robovm.apple.foundation.NSPropertyList;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.foundation.NSTimer;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.systemconfiguration.SCNetworkReachabilityFlags;
import org.robovm.apple.uikit.NSAttributedStringAttributes;
import org.robovm.apple.uikit.UIAppearance;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UIApplicationState;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageRenderingMode;
import org.robovm.apple.uikit.UINavigationBar;
import org.robovm.apple.uikit.UINavigationController;
import org.robovm.apple.uikit.UIRemoteNotification;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UISearchBar;
import org.robovm.apple.uikit.UIStatusBarStyle;
import org.robovm.apple.uikit.UITabBarController;
import org.robovm.apple.uikit.UITabBarControllerDelegateAdapter;
import org.robovm.apple.uikit.UITabBarItem;
import org.robovm.apple.uikit.UITableViewStyle;
import org.robovm.apple.uikit.UIUserNotificationSettings;
import org.robovm.apple.uikit.UIUserNotificationType;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.apple.uikit.UIWindow;
import org.robovm.pods.facebook.core.FBSDKAppEvents;
import org.robovm.pods.facebook.core.FBSDKApplicationDelegate;
import org.robovm.pods.facebook.core.FBSDKProfile;
import org.robovm.pods.parse.PFACL;
import org.robovm.pods.parse.PFAnalytics;
import org.robovm.pods.parse.PFCachePolicy;
import org.robovm.pods.parse.PFCloud;
import org.robovm.pods.parse.PFFacebookUtils;
import org.robovm.pods.parse.PFFunctionCallback;
import org.robovm.pods.parse.PFGetCallback;
import org.robovm.pods.parse.PFInstallation;
import org.robovm.pods.parse.PFObject;
import org.robovm.pods.parse.PFQuery;
import org.robovm.pods.parse.PFUser;
import org.robovm.pods.parse.Parse;
import org.robovm.pods.reachability.NetworkReachability;
import org.robovm.pods.reachability.NetworkReachabilityListener;
import org.robovm.pods.reachability.NetworkStatus;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPActivity;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPCache;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPPhoto;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPUser;
import org.robovm.samples.robopods.parse.anypic.ios.ui.controllers.PAPTabBarController;
import org.robovm.samples.robopods.parse.anypic.ios.ui.controllers.activity.PAPActivityFeedViewController;
import org.robovm.samples.robopods.parse.anypic.ios.ui.controllers.home.PAPAccountViewController;
import org.robovm.samples.robopods.parse.anypic.ios.ui.controllers.home.PAPHomeViewController;
import org.robovm.samples.robopods.parse.anypic.ios.ui.controllers.login.PAPWelcomeViewController;
import org.robovm.samples.robopods.parse.anypic.ios.ui.controllers.photo.PAPPhotoDetailsViewController;
import org.robovm.samples.robopods.parse.anypic.ios.util.Log;
import org.robovm.samples.robopods.parse.anypic.ios.util.PAPNotification;
import org.robovm.samples.robopods.parse.anypic.ios.util.PAPNotificationManager;

public class AnyPicApp extends UIApplicationDelegateAdapter {
    private static final String APPLICATION_ID = "qFCDZFYvqQh8o362On3TS89BonXkTErHno9ES2VD";
    private static final String CLIENT_KEY = "c0pbdahCIdwqX8ORMRCYFCd3NdiiHPL76qixU5uX";

    private PAPTabBarController tabBarController;
    private UINavigationController navController;
    private PAPHomeViewController homeViewController;
    private PAPActivityFeedViewController activityViewController;
    private PAPWelcomeViewController welcomeViewController;

//    private MBProgressHUD hud; TODO
    private NSTimer autoFollowTimer;

    private NetworkStatus networkStatus;
    private boolean firstLaunch;

    @Override
    public boolean didFinishLaunching(UIApplication application, UIApplicationLaunchOptions launchOptions) {
        setWindow(new UIWindow(UIScreen.getMainScreen().getBounds()));

        // ****************************************************************************
        // Parse initialization
        PFObject.registerSubclass(PAPUser.class);
        PFObject.registerSubclass(PAPPhoto.class);
        PFObject.registerSubclass(PAPActivity.class);

        Parse.initialize(APPLICATION_ID, CLIENT_KEY);
        FBSDKProfile.enableUpdatesOnAccessTokenChange(true);
        PFFacebookUtils.initializeFacebook(launchOptions);
        // ****************************************************************************

        // Track app open.
        PFAnalytics.trackAppOpened(launchOptions);

        if (application.getApplicationIconBadgeNumber() != 0) {
            application.setApplicationIconBadgeNumber(0);
            PFInstallation.getCurrentInstallation().saveInBackground();
        }

        PFACL defaultACL = new PFACL();

        // Enable public read access by default, with any newly created
        // PFObjects belonging to the current user
        defaultACL.setPublicReadAccess(true);
        PFACL.setDefaultACL(defaultACL, true);

        // Set up our app's global UIAppearance
        setupAppearance();

        // Use Reachability to monitor connectivity
        monitorReachability();

        welcomeViewController = new PAPWelcomeViewController();

        navController = new UINavigationController(welcomeViewController);
        navController.setNavigationBarHidden(true);

        getWindow().setRootViewController(navController);
        getWindow().makeKeyAndVisible();

        handlePush(launchOptions);

        return true;
    }

    @Override
    public boolean openURL(UIApplication application, NSURL url, String sourceApplication, NSPropertyList annotation) {
        boolean wasHandled = false;

        wasHandled |= FBSDKApplicationDelegate.getSharedInstance().openURL(application, url, sourceApplication,
                annotation);

        wasHandled |= handleActionURL(url);

        return wasHandled;
    }

    @Override
    public void didRegisterForRemoteNotifications(UIApplication application, NSData deviceToken) {
        if (application.getApplicationIconBadgeNumber() != 0) {
            application.setApplicationIconBadgeNumber(0);
        }

        PFInstallation currentInstallation = PFInstallation.getCurrentInstallation();
        currentInstallation.setDeviceToken(deviceToken);
        currentInstallation.saveInBackground();
    }

    @Override
    public void didFailToRegisterForRemoteNotifications(UIApplication application, NSError error) {
        if (error != null && error.getCode() != 3010) { // 3010 is for the
                                                        // iPhone Simulator
            Log.e("Application failed to register for push notifications: %s", error);
        }
    }

    @Override
    public void didReceiveRemoteNotification(UIApplication application, UIRemoteNotification userInfo) {
        PAPNotificationManager.postNotification(PAPNotification.DID_RECEIVE_REMOTE_NOTIFICATION, userInfo);

        if (UIApplication.getSharedApplication().getApplicationState() != UIApplicationState.Active) {
            // Track app opens due to a push notification being acknowledged
            // while the app wasn't active.
            PFAnalytics.trackAppOpened(userInfo);
        }

        if (PAPUser.getCurrentUser() != null) {
            if (tabBarController.getViewControllers().size() > 2) {
                UITabBarItem tabBarItem = tabBarController.getActivityFeedNavigationController().getTabBarItem();

                String currentBadgeValue = tabBarItem.getBadgeValue();

                if (currentBadgeValue != null && currentBadgeValue.length() > 0) {
                    NSNumberFormatter numberFormatter = new NSNumberFormatter();
                    NSNumber badgeValue = numberFormatter.parse(currentBadgeValue);
                    NSNumber newBadgeValue = NSNumber.valueOf(badgeValue.intValue() + 1);
                    tabBarItem.setBadgeValue(numberFormatter.format(newBadgeValue));
                } else {
                    tabBarItem.setBadgeValue("1");
                }
            }
        }
    }

    @Override
    public void didBecomeActive(UIApplication application) {
        FBSDKAppEvents.activateApp();

        // Clear badge and update installation, required for auto-incrementing
        // badges.
        if (application.getApplicationIconBadgeNumber() != 0) {
            application.setApplicationIconBadgeNumber(0);
            PFInstallation.getCurrentInstallation().saveInBackground();
        }

        // Clears out all notifications from Notification Center.
        UIApplication.getSharedApplication().cancelAllLocalNotifications();
        application.setApplicationIconBadgeNumber(1);
        application.setApplicationIconBadgeNumber(0);
    }

    public boolean isParseReachable() {
        return networkStatus != NetworkStatus.NotReachable;
    }

    private void presentLoginViewController() {
        presentLoginViewController(true);
    }

    public void presentLoginViewController(boolean animated) {
        welcomeViewController.presentLoginViewController(animated);
    }

    public void presentTabBarController() {
        tabBarController = new PAPTabBarController();
        homeViewController = new PAPHomeViewController();
        homeViewController.setFirstLaunch(firstLaunch);

        activityViewController = new PAPActivityFeedViewController(UITableViewStyle.Plain);

        UINavigationController homeNavigationController = new UINavigationController(homeViewController);
        UINavigationController emptyNavigationController = new UINavigationController();
        UINavigationController activityFeedNavigationController = new UINavigationController(activityViewController);

        UITabBarItem homeTabBarItem = new UITabBarItem(NSString.getLocalizedString("Home"), UIImage.getImage(
                "IconHome").newImage(UIImageRenderingMode.AlwaysOriginal), UIImage.getImage("IconHomeSelected")
                        .newImage(UIImageRenderingMode.AlwaysOriginal));
        homeTabBarItem.setTitleTextAttributes(new NSAttributedStringAttributes().setForegroundColor(UIColor.white())
                .setFont(UIFont.getBoldSystemFont(13)), UIControlState.Selected);
        homeTabBarItem.setTitleTextAttributes(
                new NSAttributedStringAttributes().setForegroundColor(
                        UIColor.fromRGBA(114f / 255f, 114f / 255f, 114f / 255f, 1)).setFont(
                                UIFont.getBoldSystemFont(13)),
                UIControlState.Normal);

        UITabBarItem activityFeedTabBarItem = new UITabBarItem(NSString.getLocalizedString("Activity"), UIImage
                .getImage(
                        "IconTimeline")
                .newImage(UIImageRenderingMode.AlwaysOriginal), UIImage.getImage(
                        "IconTimelineSelected").newImage(UIImageRenderingMode.AlwaysOriginal));
        activityFeedTabBarItem.setTitleTextAttributes(
                new NSAttributedStringAttributes().setForegroundColor(UIColor.white()).setFont(
                        UIFont.getBoldSystemFont(13)),
                UIControlState.Selected);
        activityFeedTabBarItem.setTitleTextAttributes(
                new NSAttributedStringAttributes().setForegroundColor(
                        UIColor.fromRGBA(114f / 255f, 114f / 255f, 114f / 255f, 1)).setFont(
                                UIFont.getBoldSystemFont(13)),
                UIControlState.Normal);

        homeNavigationController.setTabBarItem(homeTabBarItem);
        activityFeedNavigationController.setTabBarItem(activityFeedTabBarItem);

        tabBarController.setDelegate(new UITabBarControllerDelegateAdapter() {
            @Override
            public boolean shouldSelectViewController(UITabBarController tabBarController,
                    UIViewController viewController) {
                // The empty UITabBarItem behind our Camera button should not
                // load a view controller
                return !viewController.equals(AnyPicApp.this.tabBarController.getEmptyNavigationController());
            }
        });
        tabBarController.setViewControllers(new NSArray<UIViewController>(homeNavigationController,
                emptyNavigationController, activityFeedNavigationController));

        navController.setViewControllers(new NSArray<UIViewController>(welcomeViewController, tabBarController), false);

        // Register for Push Notitications
        UIUserNotificationType userNotificationTypes = UIUserNotificationType.with(UIUserNotificationType.Alert,
                UIUserNotificationType.Badge, UIUserNotificationType.Sound);
        UIUserNotificationSettings settings = new UIUserNotificationSettings(userNotificationTypes, null);
        UIApplication.getSharedApplication().registerUserNotificationSettings(settings);
        UIApplication.getSharedApplication().registerForRemoteNotifications();
    }

    public void logOut() {
        // clear cache
        PAPCache.getSharedCache().clear();

        // Unsubscribe from push notifications by removing the user association
        // from the current installation.
        PFInstallation.getCurrentInstallation().remove("user");
        PFInstallation.getCurrentInstallation().saveInBackground();

        // Clear all caches
        PFQuery.clearAllCachedResults();

        // Log out
        PFUser.logOutInBackground();

        PFFacebookUtils.getFacebookLoginManager().logOut();

        // clear out cached data, view controllers, etc
        navController.popToRootViewController(false);

        presentLoginViewController();

        homeViewController = null;
        activityViewController = null;
    }

    // Set up appearance parameters to achieve Anypic's custom look and feel
    private void setupAppearance() {
        UIApplication.getSharedApplication().setStatusBarStyle(UIStatusBarStyle.LightContent);

        UINavigationBar navigationBarAppearance = UIAppearance.getAppearance(UINavigationBar.class);
        navigationBarAppearance.setTintColor(UIColor.fromRGBA(254f / 255f, 149f / 255f, 50f / 255f, 1));
        navigationBarAppearance.setBarTintColor(UIColor.fromRGBA(0, 0, 0, 1));
        navigationBarAppearance.setTitleTextAttributes(new NSAttributedStringAttributes().setForegroundColor(UIColor
                .white()));

        UIButton buttonAppearance = UIAppearance.getAppearance(UIButton.class, UINavigationBar.class);
        buttonAppearance
                .setTitleColor(UIColor.fromRGBA(254f / 255f, 149f / 255f, 50f / 255f, 1), UIControlState.Normal);

        UIBarButtonItem barButtonItemAppearance = UIAppearance.getAppearance(UIBarButtonItem.class);
        barButtonItemAppearance.setTitleTextAttributes(new NSAttributedStringAttributes().setForegroundColor(
                UIColor.fromRGBA(254f / 255f, 149f / 255f, 50f / 255f, 1)), UIControlState.Normal);

        UISearchBar searchBarAppearance = UIAppearance.getAppearance(UISearchBar.class);
        searchBarAppearance.setTintColor(UIColor.fromRGBA(254f / 255f, 149f / 255f, 50f / 255f, 1));
    }

    private void monitorReachability() {
        NetworkReachability hostReach = NetworkReachability.forHostname("api.parse.com");
        hostReach.setListener(new NetworkReachabilityListener() {
            @Override
            public void onReachable(NetworkReachability reachability) {
                networkStatus = reachability.getCurrentReachabilityStatus();

                if (isParseReachable() && PFUser.getCurrentUser() != null
                        && homeViewController.getObjects().size() == 0) {
                    // Refresh home timeline on network restoration. Takes care
                    // of a freshly installed app that failed to load the main
                    // timeline under bad network conditions.
                    // In this case, they'd see the empty timeline placeholder
                    // and have no way of refreshing the timeline unless they
                    // followed someone.
                    homeViewController.loadObjects();
                }
            }

            @Override
            public void onUnreachable(NetworkReachability reachability) {
                networkStatus = reachability.getCurrentReachabilityStatus();
            }

            @Override
            public void onChange(NetworkReachability reachability, SCNetworkReachabilityFlags flags) {}
        });

        hostReach.startNotifier();
    }

    private void handlePush(UIApplicationLaunchOptions launchOptions) {
        // If the app was launched in response to a push notification, we'll
        // handle the payload here
        if (launchOptions == null) {
            return;
        }
        UIRemoteNotification remoteNotificationPayload = launchOptions.getRemoteNotification();
        if (remoteNotificationPayload != null) {
            PAPNotificationManager.postNotification(PAPNotification.DID_RECEIVE_REMOTE_NOTIFICATION,
                    remoteNotificationPayload);

            if (PAPUser.getCurrentUser() == null) {
                return;
            }

            // If the push notification payload references a photo, we will
            // attempt to push this view controller into view
            String photoObjectId = remoteNotificationPayload.getString("pid");
            if (photoObjectId != null && photoObjectId.length() > 0) {
                shouldNavigateToPhoto(PFObject.createWithoutData(PAPPhoto.class, photoObjectId));
                return;
            }
            // If the push notification payload references a user, we will
            // attempt to push their profile into view
            String fromObjectId = remoteNotificationPayload.getString("fu");
            if (fromObjectId != null && fromObjectId.length() > 0) {
                PFQuery<PAPUser> query = PFQuery.getQuery(PAPUser.class);
                query.setCachePolicy(PFCachePolicy.CacheElseNetwork);
                query.getInBackground(fromObjectId, new PFGetCallback<PAPUser>() {
                    @Override
                    public void done(PAPUser user, NSError error) {
                        if (error == null) {
                            UINavigationController homeNavigationController = tabBarController
                                    .getHomeNavigationController();
                            tabBarController.setSelectedViewController(homeNavigationController);

                            PAPAccountViewController accountViewController = new PAPAccountViewController(
                                    UITableViewStyle.Plain);
                            Log.d("Presenting account view controller with user: %s", user);
                            accountViewController.setUser(user);
                            homeNavigationController.pushViewController(accountViewController, true);
                        }
                    }
                });
            }
        }
    }

    private void autoFollowTimerFired(NSTimer timer) {
//    [MBProgressHUD hideHUDForView:self.navController.presentedViewController.view animated:YES];
//    [MBProgressHUD hideHUDForView:self.homeViewController.view animated:YES]; TODO
        homeViewController.loadObjects();

    }

    private boolean shouldProceedToMainInterface(PAPUser user) {
//    [MBProgressHUD hideHUDForView:self.navController.presentedViewController.view animated:YES]; TODO
        presentTabBarController();

        navController.dismissViewController(true, null);
        return true;
    }

    private boolean handleActionURL(NSURL url) {
        if (url.getHost().equals("camera")) {
            if (PAPUser.getCurrentUser() != null) {
                return tabBarController.shouldPresentPhotoCaptureController();
            }
        } else {
            if (url.getFragment().matches("pic/[A-Za-z0-9]{10}")) {
                String photoObjectId = url.getFragment().substring(4, 10);
                if (photoObjectId != null && photoObjectId.length() > 0) {
                    Log.d("PHOTO: %s", photoObjectId);
                    shouldNavigateToPhoto(PFObject.createWithoutData(PAPPhoto.class, photoObjectId));
                    return true;
                }
            }
        }

        return false;
    }

    private void shouldNavigateToPhoto(PAPPhoto targetPhoto) {
        for (PAPPhoto photo : homeViewController.getObjects()) {
            if (photo.getObjectId().equals(targetPhoto.getObjectId())) {
                targetPhoto = photo;
                break;
            }
        }

        // if we have a local copy of this photo, this won't result in a network
        // fetch
        targetPhoto.fetchIfNeededInBackground(new PFGetCallback<PAPPhoto>() {
            @Override
            public void done(PAPPhoto object, NSError error) {
                if (error == null) {
                    UINavigationController homeNavigationController = tabBarController.getHomeNavigationController();
                    tabBarController.setSelectedViewController(homeNavigationController);

                    PAPPhotoDetailsViewController detailViewController = new PAPPhotoDetailsViewController(object);
                    homeNavigationController.pushViewController(detailViewController, true);
                }
            }
        });
    }

    public void autoFollowUsers() {
        firstLaunch = true;
        PFCloud.callFunctionInBackground("autoFollowUsers", null, new PFFunctionCallback<PFObject>() {
            @Override
            public void done(PFObject result, NSError error) {
                if (error != null) {
                    Log.e("Error auto following users: %s", error);
                }
//                [MBProgressHUD hideHUDForView:self.navController.presentedViewController.view animated:NO]; TODO
                homeViewController.loadObjects();
            }
        });
    }

    public static void main(String[] args) {
        try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
            UIApplication.main(args, null, AnyPicApp.class);
        }
    }
}
