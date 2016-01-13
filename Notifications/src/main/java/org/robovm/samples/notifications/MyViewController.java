package org.robovm.samples.notifications;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.robovm.apple.foundation.NSDataBase64EncodingOptions;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import org.robovm.objc.annotation.IBOutlet;

@CustomClass("MyViewController")
public class MyViewController extends UIViewController {
    @IBOutlet
    private UILabel label;

    @IBAction
    private void registerForNotifications() {
        if (AppDelegate.getInstance().isRegisteredForRemoteNotifications()) {
            // Let's encode the device token to display it as a string.
            String base64DeviceToken = AppDelegate.getInstance().getDeviceToken()
                    .toBase64EncodedString(NSDataBase64EncodingOptions.None);

            setLabelText("App is already registered for remote notifications!\nDevice token: " + base64DeviceToken);
        } else {
            AppDelegate.getInstance().registerForRemoteNotifications(new NotificationRegistrationListener() {
                @Override
                public void onSuccess() {
                    // Let's encode the device token to display it as a string.
                    String base64DeviceToken = AppDelegate.getInstance().getDeviceToken()
                            .toBase64EncodedString(NSDataBase64EncodingOptions.None);

                    setLabelText(
                            "Successfully registered for remote notifications!\nDevice token: " + base64DeviceToken);
                }

                @Override
                public void onError(Throwable e) {
                    setLabelText("An error happened: " + e.getMessage());
                    e.printStackTrace();
                }

                @Override
                public void onCancel() {
                    setLabelText("Registration was cancelled!");
                }
            });
        }
    }

    @IBAction
    private void scheduleLocalNotification() {
        if (AppDelegate.getInstance().isRegisteredForRemoteNotifications()) {
            // Schedule our notification to fire in 5 seconds.
            Calendar calendar = new GregorianCalendar();
            calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) + 5);

            AppDelegate.getInstance().scheduleLocalNotification("OUR_NOTIFICATION", null, "RoboVM",
                    "This is a local notification", "START APP", calendar.getTime());

            setLabelText("Local notification has been scheduled and will fire in 5 seconds!");
        } else {
            setLabelText("Need to register for notifications first!");
        }
    }

    @IBAction
    private void scheduleLocalActionableNotification() {
        if (AppDelegate.getInstance().isRegisteredForRemoteNotifications()) {
            // Schedule our notification to fire in 5 seconds.
            Calendar calendar = new GregorianCalendar();
            calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) + 5);

            AppDelegate.getInstance().scheduleLocalNotification("OUR_ACTION_NOTIFICATION",
                    AppDelegate.NOTIFICATION_INVITE_CATEGORY, "RoboVM",
                    "This is a local actionable notification", "START APP", calendar.getTime());

            setLabelText("Local actionable notification has been scheduled and will fire in 5 seconds!");
        } else {
            setLabelText("Need to register for notifications first!");
        }
    }

    public void setLabelText(String text) {
        label.setText(text);
    }
}
