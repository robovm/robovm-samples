package org.robovm.samples.notifications;

import java.util.Date;

import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSDate;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSErrorException;
import org.robovm.apple.foundation.NSMutableDictionary;
import org.robovm.apple.foundation.NSSet;
import org.robovm.apple.foundation.NSUserDefaults;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UILocalNotification;
import org.robovm.apple.uikit.UIMutableUserNotificationAction;
import org.robovm.apple.uikit.UIMutableUserNotificationCategory;
import org.robovm.apple.uikit.UIRemoteNotification;
import org.robovm.apple.uikit.UIRemoteNotificationType;
import org.robovm.apple.uikit.UIUserNotificationAction;
import org.robovm.apple.uikit.UIUserNotificationActionContext;
import org.robovm.apple.uikit.UIUserNotificationActivationMode;
import org.robovm.apple.uikit.UIUserNotificationCategory;
import org.robovm.apple.uikit.UIUserNotificationSettings;
import org.robovm.apple.uikit.UIUserNotificationType;

@SuppressWarnings("deprecation")
public class AppDelegate extends UIApplicationDelegateAdapter {
    private static final String DEVICE_TOKEN_USER_PREF = "deviceToken";
    private static final String LOCAL_NOTIFICATION_ID_KEY = "ID";
    private static final String NOTIFICATION_INVITE_ACCEPT_ID = "ACCEPT_ID";
    private static final String NOTIFICATION_INVITE_DECLINE_ID = "DECLINE_ID";
    public static final String NOTIFICATION_INVITE_CATEGORY = "INVITE_ID";

    private NotificationRegistrationListener notificationRegistrationListener;

    public static AppDelegate getInstance() {
        return (AppDelegate) UIApplication.getSharedApplication().getDelegate();
    }

    public MyViewController getMyViewController() {
        return (MyViewController) getWindow().getRootViewController();
    }

    @Override
    public boolean didFinishLaunching(UIApplication application, UIApplicationLaunchOptions launchOptions) {
        if (launchOptions != null) {
            UILocalNotification localNotification = launchOptions.getLocalNotification();
            if (localNotification != null) {
                // The application has been started from a local notification.

                // Get and print our id parameter.
                String id = localNotification.getUserInfo().getString(LOCAL_NOTIFICATION_ID_KEY);
                getMyViewController().setLabelText("App was started by local notification: " + id);
            }
            UIRemoteNotification remoteNotification = launchOptions.getRemoteNotification();
            if (remoteNotification != null) {
                // The application has been started from a remote/push
                // notification.

                getMyViewController().setLabelText("App was started by remote notification!");
            }
        }

        return true;
    }

    /**
     * Register for notifications. We want Alert, Badge and Sound notification
     * types.
     * 
     * @param listener Will be called when the user accepts or denies app
     *            notifications.
     */
    public void registerForRemoteNotifications(NotificationRegistrationListener listener) {
        this.notificationRegistrationListener = listener;

        // On iOS 8+ we have to register user notification settings first.
        if (Foundation.getMajorSystemVersion() >= 8) {
            // Specify the allowed notification types.
            UIUserNotificationType types = UIUserNotificationType.with(UIUserNotificationType.Alert,
                    UIUserNotificationType.Badge, UIUserNotificationType.Sound);

            // Let's create a few sample actions and categories for our
            // notifications.
            UIUserNotificationAction acceptAction = new UIMutableUserNotificationAction();
            acceptAction.setIdentifier(NOTIFICATION_INVITE_ACCEPT_ID);
            acceptAction.setTitle("Accept");
            // Specifies whether the app must be in the foreground to perform
            // the action.
            acceptAction.setActivationMode(UIUserNotificationActivationMode.Background);

            UIUserNotificationAction declineAction = new UIMutableUserNotificationAction();
            declineAction.setIdentifier(NOTIFICATION_INVITE_DECLINE_ID);
            declineAction.setTitle("Decline");
            declineAction.setDestructive(true);
            declineAction.setActivationMode(UIUserNotificationActivationMode.Background);

            UIUserNotificationCategory inviteCategory = new UIMutableUserNotificationCategory();
            inviteCategory.setIdentifier(NOTIFICATION_INVITE_CATEGORY);
            inviteCategory.setActions(new NSArray<>(acceptAction, declineAction),
                    UIUserNotificationActionContext.Default);

            UIUserNotificationSettings notificationSettings = new UIUserNotificationSettings(
                    types, new NSSet<>(inviteCategory));

            // This will prompt the user to allow app notifications.
            UIApplication.getSharedApplication().registerUserNotificationSettings(notificationSettings);
            /*
             * **NOTE** Only the first time you call this method the user will
             * be prompted. On subsequent calls the delegate method
             * didRegisterUserNotificationSettings will be called immediately
             * with the active settings.
             */
            /*
             * **NOTE** If you are not interested in what notification types the
             * user did allow or not, you can directly register for remote
             * notifications here.
             */
        } else {
            // Specify the allowed notification types.
            UIRemoteNotificationType types = UIRemoteNotificationType.with(UIRemoteNotificationType.Badge,
                    UIRemoteNotificationType.Alert,
                    UIRemoteNotificationType.Sound);

            // This will prompt the user to allow app notifications.
            UIApplication.getSharedApplication().registerForRemoteNotificationTypes(types);
        }
    }

    /**
     * @return true if the app is registered for remote notifications.
     */
    public boolean isRegisteredForRemoteNotifications() {
        if (Foundation.getMajorSystemVersion() >= 8) {
            return UIApplication.getSharedApplication().isRegisteredForRemoteNotifications();
        } else {
            UIRemoteNotificationType types = UIApplication.getSharedApplication().getEnabledRemoteNotificationTypes();
            return types.contains(UIRemoteNotificationType.Badge) && types.contains(UIRemoteNotificationType.Alert)
                    && types.contains(UIRemoteNotificationType.Sound);
        }
    }

    /**
     * Schedule a local notification.
     * 
     * @param id
     * @param title
     * @param message
     * @param action
     * @param fireDate
     */
    public void scheduleLocalNotification(String id, String category, String title, String message, String action,
            Date fireDate) {
        UILocalNotification notification = new UILocalNotification();
        NSDictionary<?, ?> userInfo = new NSMutableDictionary<>();
        userInfo.put(LOCAL_NOTIFICATION_ID_KEY, id);
        notification.setUserInfo(userInfo);
        notification.setAlertTitle(title);
        notification.setAlertBody(message);
        notification.setAlertAction(action);
        notification.setFireDate(new NSDate(fireDate));

        if (category != null) {
            // This will make the notification actionable.
            notification.setCategory(category);
        }

        UIApplication.getSharedApplication().scheduleLocalNotification(notification);
    }

    /**
     * @return our stored device token or null.
     */
    public NSData getDeviceToken() {
        return NSUserDefaults.getStandardUserDefaults().getData(DEVICE_TOKEN_USER_PREF);
    }

    @Override
    public void didRegisterUserNotificationSettings(UIApplication application,
            UIUserNotificationSettings notificationSettings) {
        // On iOS 8+ we will get here when the user grant or decline
        // notification access.

        // Check if the user granted notification access.
        UIUserNotificationType types = notificationSettings.getTypes();
        if (types.contains(UIUserNotificationType.Alert) && types.contains(UIUserNotificationType.Badge)
                && types.contains(UIUserNotificationType.Sound)) {
            // User granted notification access. Register for remote
            // notifications.
            UIApplication.getSharedApplication().registerForRemoteNotifications();
        } else {
            // User declined notification access.

            // Call the cancel callback.
            if (notificationRegistrationListener != null) {
                notificationRegistrationListener.onCancel();
                notificationRegistrationListener = null;
            }
        }
    }

    /**
     * Will be called when the user accepted remote notifications and whenever
     * the device token changes.
     * 
     * @param application
     * @param deviceToken will be used to identify this device from remote push
     *            notification servers.
     */
    @Override
    public void didRegisterForRemoteNotifications(UIApplication application, NSData deviceToken) {
        if (deviceToken != null) {
            /*
             * **IMPORTANT** Normally you would send the deviceToken to a push
             * notification server to be able to send push notifications to this
             * device.
             */

            // Let's store the device token in the app preferences.
            NSUserDefaults.getStandardUserDefaults().put(DEVICE_TOKEN_USER_PREF, deviceToken);
            NSUserDefaults.getStandardUserDefaults().synchronize();
        }

        // Call the success callback.
        if (notificationRegistrationListener != null) {
            notificationRegistrationListener.onSuccess();
            notificationRegistrationListener = null;
        }
    }

    /**
     * Will be called when the registration for remote notifications failed.
     * 
     * @param application
     * @param error
     */
    @Override
    public void didFailToRegisterForRemoteNotifications(UIApplication application, NSError error) {
        // Call the error callback.
        if (notificationRegistrationListener != null) {
            notificationRegistrationListener.onError(new NSErrorException(error));
            notificationRegistrationListener = null;
        }
    }

    /**
     * If the app is in front this callback will be called.
     * <p>
     * If the app is in back an alert will be displayed and this callback will
     * only be called if the user taps on the alert.
     * 
     * @param application
     * @param notification
     */
    @Override
    public void didReceiveLocalNotification(UIApplication application, UILocalNotification notification) {
        // Get and print our id parameter.
        String id = notification.getUserInfo().getString(LOCAL_NOTIFICATION_ID_KEY);
        getMyViewController().setLabelText("Did receive local notification: " + id);
    }

    /**
     * Will be called when the app receives a remote notification.
     * 
     * @param application
     * @param userInfo
     */
    @Override
    public void didReceiveRemoteNotification(UIApplication application, UIRemoteNotification userInfo) {
        getMyViewController().setLabelText("Did receive remote notification.");
    }

    /**
     * Will be called when the user taps on an local notification action button.
     * 
     * @param application
     * @param identifier
     * @param notification
     * @param completionHandler
     */
    @Override
    public void handleLocalNotificationAction(UIApplication application, String identifier,
            UILocalNotification notification, Runnable completionHandler) {
        // Handle the different notification actions we defined.
        if (identifier.equals(NOTIFICATION_INVITE_ACCEPT_ID)) {
            getMyViewController().setLabelText("Handle local notification action: ACCEPT INVITE");
        } else if (identifier.equals(NOTIFICATION_INVITE_DECLINE_ID)) {
            getMyViewController().setLabelText("Handle local notification action: DECLINE INVITE");
        }

        // Must be called when you are done handling the notification.
        completionHandler.run();
    }

    /**
     * Will be called when the user taps on an remote notification action
     * button.
     * 
     * @param application
     * @param identifier
     * @param userInfo
     * @param completionHandler
     */
    @Override
    public void handleRemoteNotificationAction(UIApplication application, String identifier,
            UIRemoteNotification userInfo, Runnable completionHandler) {
        // Handle the different notification actions we defined.
        if (identifier.equals(NOTIFICATION_INVITE_ACCEPT_ID)) {
            getMyViewController().setLabelText("Handle remote notification action: ACCEPT INVITE");
        } else if (identifier.equals(NOTIFICATION_INVITE_DECLINE_ID)) {
            getMyViewController().setLabelText("Handle remote notification action: DECLINE INVITE");
        }

        // Must be called when you are done handling the notification.
        completionHandler.run();
    }

    public static void main(String[] args) {
        try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
            UIApplication.main(args, null, AppDelegate.class);
        }
    }
}
