package org.robovm.samples.robopods.facebook.ios;

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
import org.robovm.objc.block.VoidBlock2;
import org.robovm.objc.block.VoidBlock3;
import org.robovm.pods.facebook.core.FBSDKAccessToken;
import org.robovm.pods.facebook.core.FBSDKGraphRequest;
import org.robovm.pods.facebook.core.FBSDKGraphRequestConnection;
import org.robovm.pods.facebook.core.FBSDKProfile;
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
    }

    public static FacebookHandler getInstance() {
        return instance;
    }

    public boolean isLoggedIn() {
        return FBSDKAccessToken.getCurrentAccessToken() != null;
    }

    public void logIn(final List<String> readPermissions, final LoginListener listener) {
        loginManager.logInWithReadPermissions(readPermissions,
                new VoidBlock2<FBSDKLoginManagerLoginResult, NSError>() {
                    @Override
                    public void invoke(FBSDKLoginManagerLoginResult result, NSError error) {
                        if (error != null) {
                            listener.onError("An unknown error happened!");
                        } else if (result.isCancelled()) {
                            listener.onCancel();
                        } else {
                            if (!result.getGrantedPermissions().containsAll(readPermissions)) {
                                listener.onError("The following permissions have been declined: "
                                        + result.getDeclinedPermissions().toString());
                            } else {
                                listener.onSuccess();
                            }
                        }
                    }
                });
    }

    public void logOut() {
        loginManager.logOut();
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
        if (isLoggedIn()) {
            requestGraph("me/permissions", null, "GET", new RequestListener() {
                @SuppressWarnings("unchecked")
                @Override
                public void onSuccess(NSObject result) {
                    int grantedPermissions = 0;

                    NSDictionary<NSString, ?> root = (NSDictionary<NSString, ?>) result;
                    NSArray<NSDictionary<NSString, ?>> p = (NSArray<NSDictionary<NSString, ?>>) root.get(new NSString(
                            "data"));

                    for (NSDictionary<NSString, ?> pData : p) {
                        String permission = pData.get(new NSString("permission")).toString();
                        boolean granted = "granted".equals(pData.get(new NSString("status")).toString());
                        if (granted && permissions.contains(permission)) {
                            grantedPermissions++;
                        }
                    }

                    if (grantedPermissions == permissions.size()) {
                        if (listener != null) {
                            listener.onSuccess(null);
                        }
                    } else {
                        requestPublishPermissions(permissions, new LoginListener() {
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
        } else {
            if (listener != null) {
                listener.onError(null);
            }
        }
    }

    public void requestPublishPermissions(final List<String> publishPermissions, final LoginListener listener) {
        loginManager.logInWithPublishPermissions(publishPermissions,
                new VoidBlock2<FBSDKLoginManagerLoginResult, NSError>() {
                    @Override
                    public void invoke(FBSDKLoginManagerLoginResult result, NSError error) {
                        if (error != null) {
                            listener.onError("An unknown error happened!");
                        } else if (result.isCancelled()) {
                            listener.onCancel();
                        } else {
                            if (!result.getGrantedPermissions().containsAll(publishPermissions)) {
                                listener.onError("The following permissions have been declined: "
                                        + result.getDeclinedPermissions().toString());
                            } else {
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
                connection.addRequest(request, new VoidBlock3<FBSDKGraphRequestConnection, NSObject, NSError>() {
                    @Override
                    public void invoke(FBSDKGraphRequestConnection connection, NSObject result, NSError error) {
                        if (listener != null) {
                            if (error != null) {
                                listener.onError(error.getLocalizedDescription());
                            } else {
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
