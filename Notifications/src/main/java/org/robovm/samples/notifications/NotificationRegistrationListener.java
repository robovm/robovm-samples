package org.robovm.samples.notifications;

public interface NotificationRegistrationListener {
    void onSuccess();

    void onError(Throwable e);

    void onCancel();
}
