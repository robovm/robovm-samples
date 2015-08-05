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
package org.robovm.samples.robopods.parse.anypic.ios.util;

import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSNotification;
import org.robovm.apple.foundation.NSNotificationCenter;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSOperationQueue;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.UIRemoteNotification;
import org.robovm.objc.block.VoidBlock1;

public class PAPNotificationManager {

    public static NSObject addObserver(PAPNotification notification, VoidBlock1<NSNotification> callback) {
        return NSNotificationCenter.getDefaultCenter().addObserver(notification.getName(), null,
                NSOperationQueue.getMainQueue(), callback);
    }

    public static NSObject addObserver(PAPNotification notification, NSObject object,
            VoidBlock1<NSNotification> callback) {
        return NSNotificationCenter.getDefaultCenter().addObserver(notification.getName(), object,
                NSOperationQueue.getMainQueue(), callback);
    }

    public static void removeObserver(NSObject observer) {
        NSNotificationCenter.getDefaultCenter().removeObserver(observer);
    }

    public static void postNotification(PAPNotification notification) {
        postNotification(notification, (NSDictionary<?, ?>) null);
    }

    public static void postNotification(PAPNotification notification, String object) {
        postNotification(notification, new NSString(object), null);
    }

    public static void postNotification(PAPNotification notification, NSObject object) {
        postNotification(notification, object, null);
    }

    public static void postNotification(PAPNotification notification, UIRemoteNotification userInfo) {
        NSNotificationCenter.getDefaultCenter().postNotification(notification.getName(), null, userInfo);
    }

    public static void postNotification(PAPNotification notification, NSDictionary<?, ?> userInfo) {
        postNotification(notification, null, userInfo);
    }

    public static void postNotification(PAPNotification notification, NSObject object, NSDictionary<?, ?> userInfo) {
        NSNotificationCenter.getDefaultCenter().postNotification(notification.getName(), object, userInfo);
    }

}
