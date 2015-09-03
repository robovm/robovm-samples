/*
 * Copyright (C) 2013-2015 RoboVM AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robovm.samples.robopods.facebook.ios;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSMutableDictionary;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSOperationQueue;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.UIAlertView;
import org.robovm.objc.block.VoidBlock1;
import org.robovm.objc.block.VoidBlock2;
import org.robovm.objc.block.VoidBlock3;
import org.robovm.pods.facebook.core.FBSDKAccessToken;
import org.robovm.pods.facebook.core.FBSDKGraphRequest;
import org.robovm.pods.facebook.core.FBSDKGraphRequestConnection;
import org.robovm.pods.facebook.core.FBSDKProfile;
import org.robovm.pods.facebook.core.FBSDKProfileChangeNotification;
import org.robovm.pods.facebook.login.FBSDKDefaultAudience;
import org.robovm.pods.facebook.login.FBSDKLoginManager;
import org.robovm.pods.facebook.login.FBSDKLoginManagerLoginResult;

public class FacebookHandler {
    private static FacebookHandler instance = new FacebookHandler();

    private final FBSDKLoginManager loginManager;

    private FacebookHandler() {
        loginManager = new FBSDKLoginManager();
        loginManager.setDefaultAudience(FBSDKDefaultAudience.Everyone);
        FBSDKProfile.enableUpdatesOnAccessTokenChange(true);
        FBSDKProfile.Notifications.observeCurrentProfileDidChange(new VoidBlock1<FBSDKProfileChangeNotification>() {
            @Override
            public void invoke(FBSDKProfileChangeNotification notification) {
                if (notification != null && notification.getNewProfile() != null) {
                    FBSDKProfile currentProfile = notification.getNewProfile();
                    // TODO Store the profile and update the profile ui.
                }
            }
        });
    }

    public static FacebookHandler getInstance() {
        return instance;
    }

    public boolean isLoggedIn() {
        return FBSDKAccessToken.getCurrentAccessToken() != null;
    }

    public void logIn(final List<String> readPermissions, final LoginListener listener) {
        log("Trying to login with read permissions (%s)...", readPermissions);
        loginManager.logInWithReadPermissions(readPermissions,
                new VoidBlock2<FBSDKLoginManagerLoginResult, NSError>() {
                    @Override
                    public void invoke(FBSDKLoginManagerLoginResult result, NSError error) {
                        if (error != null) {
                            log("Failed to login: %s", error.getLocalizedDescription());
                            listener.onError("An unknown error happened!");
                        } else if (result.isCancelled()) {
                            log("Cancelled login!");
                            listener.onCancel();
                        } else {
                            if (!result.getGrantedPermissions().containsAll(readPermissions)) {
                                log("Failed to login: Permissions declined (%s)", result.getDeclinedPermissions());
                                listener.onError("The following permissions have been declined: "
                                        + result.getDeclinedPermissions().toString());
                            } else {
                                log("Successfully logged in!");
                                listener.onSuccess();
                            }
                        }
                    }
                });
    }

    public void logOut() {
        loginManager.logOut();
        log("Successfully logged out!");
    }

    public FBSDKProfile getCurrentProfile() {
        return FBSDKProfile.getCurrentProfile();
    }

    public void alertError(String title, String message) {
        UIAlertView alert = new UIAlertView(title, message, null, "OK");
        alert.show();
    }

    public void requestFriends(RequestListener listener) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("fields", "installed,name");
        requestGraph("me/friends", params, "GET", listener);
    }

    public void requestPermissionsIfNecessary(final List<String> permissions, final RequestListener listener) {
        log("Checking for required permissions (%s)...", permissions);
        if (isLoggedIn()) {
            requestGraph("me/permissions", null, "GET", new RequestListener() {
                @SuppressWarnings("unchecked")
                @Override
                public void onSuccess(NSObject result) {
                    log("Successfully fetched permissions...");
                    List<String> declinedPermissions = new ArrayList<String>(permissions);

                    NSDictionary<NSString, ?> root = (NSDictionary<NSString, ?>) result;
                    NSArray<NSDictionary<NSString, ?>> p = (NSArray<NSDictionary<NSString, ?>>) root.get(new NSString(
                            "data"));

                    for (NSDictionary<NSString, ?> pData : p) {
                        String permission = pData.get(new NSString("permission")).toString();
                        boolean granted = "granted".equals(pData.get(new NSString("status")).toString());
                        if (granted && declinedPermissions.contains(permission)) {
                            declinedPermissions.remove(permission);
                        }
                    }

                    if (declinedPermissions.size() == 0) {
                        log("Required permissions are all granted!");
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    } else {
                        log("Missing required permission!");
                        requestPublishPermissions(declinedPermissions, new LoginListener() {
                            @Override
                            public void onSuccess() {
                                if (listener != null) {
                                    listener.onSuccess(null);
                                }
                            }

                            @Override
                            public void onError(String message) {
                                if (listener != null) {
                                    listener.onError(message);
                                }
                            }

                            @Override
                            public void onCancel() {
                                if (listener != null) {
                                    listener.onCancel();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onError(String message) {
                    log("Failed to fetch permissions: %s", message);
                    if (listener != null) {
                        listener.onError(message);
                    }
                }

                @Override
                public void onCancel() {
                    log("Cancelled fetch for permissions!");
                    if (listener != null) {
                        listener.onCancel();
                    }
                }
            });
        } else {
            log("Not logged in!");
            if (listener != null) {
                listener.onError(null);
            }
        }
    }

    public void requestPublishPermissions(final List<String> publishPermissions, final LoginListener listener) {
        log("Requesting publish permissions (%s)...", publishPermissions);
        loginManager.logInWithPublishPermissions(publishPermissions,
                new VoidBlock2<FBSDKLoginManagerLoginResult, NSError>() {
                    @Override
                    public void invoke(FBSDKLoginManagerLoginResult result, NSError error) {
                        if (error != null) {
                            log("Failed to request publish permissions: %s", error);
                            listener.onError("An unknown error happened!");
                        } else if (result.isCancelled()) {
                            log("Cancelled request for publish permissions!");
                            listener.onCancel();
                        } else {
                            if (!result.getGrantedPermissions().containsAll(publishPermissions)) {
                                log("Failed to request publish permissions: Permissions declined (%s)",
                                        result.getDeclinedPermissions());
                                listener.onError("The following permissions have been declined: "
                                        + result.getDeclinedPermissions().toString());
                            } else {
                                log("Successfully requested publish permissions");
                                listener.onSuccess();
                            }
                        }
                    }
                });
    }

    public void publishFeed(final String name, final String description, final String message, final String link,
            final String pictureUrl, final RequestListener listener) {
        requestPermissionsIfNecessary(Arrays.asList("publish_actions"), new RequestListener() {
            @Override
            public void onSuccess(NSObject result) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("description", description);
                params.put("message", message);
                params.put("link", link);
                params.put("picture", pictureUrl);

                requestGraph("me/feed", params, "POST", listener);
            }

            @Override
            public void onError(String message) {
                if (listener != null) {
                    listener.onError(message);
                }
            }

            @Override
            public void onCancel() {
                if (listener != null) {
                    listener.onCancel();
                }
            }
        });
    }

    public void requestGraph(final String path, final Map<String, String> params, final String httpMethod,
            final RequestListener listener) {
        NSOperationQueue.getMainQueue().addOperation(new Runnable() {
            @Override
            public void run() {
                FBSDKGraphRequestConnection connection = new FBSDKGraphRequestConnection();
                FBSDKGraphRequest request = new FBSDKGraphRequest(path, convertStringMapToDictionary(params),
                        httpMethod);
                log("Requesting graph path %s...", path);
                connection.addRequest(request, new VoidBlock3<FBSDKGraphRequestConnection, NSObject, NSError>() {
                    @Override
                    public void invoke(FBSDKGraphRequestConnection connection, NSObject result, NSError error) {
                        if (error != null) {
                            log("Failed to request graph path: %s", error.getLocalizedDescription());
                            if (listener != null) {
                                listener.onError(error.getLocalizedDescription());
                            }
                        } else {
                            log("Successfully requested graph path %s!", path);
                            if (listener != null) {
                                listener.onSuccess(result);
                            }
                        }
                    }
                });
                connection.start();
            }
        });
    }

    private NSDictionary<NSString, NSString> convertStringMapToDictionary(Map<String, String> map) {
        NSDictionary<NSString, NSString> result = new NSMutableDictionary<>();
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                result.put(new NSString(entry.getKey()), new NSString(entry.getValue()));
            }
        }
        return result;
    }

    public static void log(String message, Object... args) {
        System.out.println(String.format(message, args));
    }

    public interface LoginListener {
        void onSuccess();

        void onError(String message);

        void onCancel();
    }

    public interface RequestListener {
        void onSuccess(NSObject result);

        void onError(String message);

        void onCancel();
    }
}
