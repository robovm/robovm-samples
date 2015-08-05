package org.robovm.samples.robopods.parse.anypic.ios.ui.controllers.login;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSMutableData;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.foundation.NSURLConnection;
import org.robovm.apple.foundation.NSURLConnectionDataDelegate;
import org.robovm.apple.foundation.NSURLConnectionDataDelegateAdapter;
import org.robovm.apple.foundation.NSURLRequest;
import org.robovm.apple.foundation.NSURLRequestCachePolicy;
import org.robovm.apple.foundation.NSURLResponse;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.block.VoidBlock1;
import org.robovm.objc.block.VoidBlock3;
import org.robovm.pods.facebook.core.FBSDKAccessToken;
import org.robovm.pods.facebook.core.FBSDKGraphRequest;
import org.robovm.pods.facebook.core.FBSDKGraphRequestConnection;
import org.robovm.pods.facebook.core.FBSDKProfile;
import org.robovm.pods.facebook.core.FBSDKProfileChangeNotification;
import org.robovm.pods.parse.PFError;
import org.robovm.pods.parse.PFErrorCode;
import org.robovm.pods.parse.PFGetCallback;
import org.robovm.pods.parse.PFSaveCallback;
import org.robovm.pods.parse.PFUser;
import org.robovm.samples.robopods.parse.anypic.ios.AnyPicApp;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPCache;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPUser;
import org.robovm.samples.robopods.parse.anypic.ios.util.Log;
import org.robovm.samples.robopods.parse.anypic.ios.util.PAPUtility;

public class PAPWelcomeViewController extends UIViewController implements PAPLogInViewControllerDelegate {
    private boolean presentedLoginViewController;
    private int facebookResponseCount;
    private int expectedFacebookResponseCount;

    private NSMutableData profilePicData;

    @Override
    public void loadView() {
        UIImageView backgroundImageView = new UIImageView(UIScreen.getMainScreen().getApplicationFrame());
        backgroundImageView.setImage(UIImage.create("Default.png"));
        setView(backgroundImageView);
    }

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        FBSDKProfile.Notifications.observeCurrentProfileDidChange(new VoidBlock1<FBSDKProfileChangeNotification>() {
            @Override
            public void invoke(FBSDKProfileChangeNotification notification) {
                if (FBSDKProfile.getCurrentProfile() != null && PAPUser.getCurrentUser() != null) {
                    PAPUser.getCurrentUser().fetchInBackground(new PFGetCallback<PAPUser>() {
                        @Override
                        public void done(PAPUser object, NSError error) {
                            refreshCurrentUser(object, error);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);

        if (PAPUser.getCurrentUser() == null) {
            presentLoginViewController(false);
            return;
        }

        // Present Anypic UI
        ((AnyPicApp) UIApplication.getSharedApplication().getDelegate()).presentTabBarController();

        // Refresh current user with server side data -- checks if user is still
        // valid and so on
        facebookResponseCount = 0;
        PAPUser.getCurrentUser().fetchInBackground(new PFGetCallback<PAPUser>() {
            @Override
            public void done(PAPUser object, NSError error) {
                if (FBSDKProfile.getCurrentProfile() != null) {
                    refreshCurrentUser(object, error);
                }
            }
        });
    }

    public void presentLoginViewController(boolean animated) {
        if (presentedLoginViewController) {
            return;
        }
        presentedLoginViewController = true;
        PAPLoginViewController loginViewController = new PAPLoginViewController();
        loginViewController.setDelegate(this);
        presentViewController(loginViewController, animated, null);
    }

    @Override
    public void didLogin(PAPLoginViewController logInViewController) {
        if (presentedLoginViewController) {
            presentedLoginViewController = false;
            dismissViewController(true, null);
        }
    }

    private void processedFacebookResponse() {
        // Once we handled all necessary facebook batch responses, save
        // everything necessary and continue
        synchronized (this) {
            facebookResponseCount++;
            if (facebookResponseCount != expectedFacebookResponseCount) {
                return;
            }
        }
        facebookResponseCount = 0;
        Log.d("done processing all Facebook requests");

        PAPUser.getCurrentUser().saveInBackground(new PFSaveCallback() {
            @Override
            public void done(boolean success, NSError error) {
                if (!success) {
                    Log.e("Failed save in background of user, %s", error);
                } else {
                    Log.d("saved current parse user");
                }
            }
        });
    }

    private void refreshCurrentUser(PFUser refreshedUser, NSError error) {
        // This fetches the most recent data from FB, and syncs up all data with
        // the server including profile pic and friends list from FB.

        if (error != null && error instanceof PFError) {
            PFError e = (PFError) error;
            // A PFErrorCode.ObjectNotFound error on currentUser refresh signals
            // a deleted user
            if (e.getErrorCode() == PFErrorCode.ObjectNotFound) {
                Log.d("User does not exist.");
                ((AnyPicApp) UIApplication.getSharedApplication().getDelegate()).logOut();
                return;
            }
        }

        if (FBSDKAccessToken.getCurrentAccessToken() == null) {
            Log.e("FB access token does not exist, logout");
            ((AnyPicApp) UIApplication.getSharedApplication().getDelegate()).logOut();
            return;
        }

        if (FBSDKProfile.getCurrentProfile() == null) {
            Log.e("FB user profile does not exist, logout");
            ((AnyPicApp) UIApplication.getSharedApplication().getDelegate()).logOut();
            return;
        }

        final PAPUser currentParseUser = PAPUser.getCurrentUser();
        if (currentParseUser == null) {
            Log.d("Current Parse user does not exist, logout");
            ((AnyPicApp) UIApplication.getSharedApplication().getDelegate()).logOut();
            return;
        }

        String facebookId = currentParseUser.getFacebookId();
        if (facebookId == null || facebookId.length() == 0) {
            // set the parse user's FBID
            currentParseUser.setFacebookId(FBSDKProfile.getCurrentProfile().getUserID());
        }

        if (!PAPUtility.userHasValidFacebookData(currentParseUser)) {
            Log.d("User does not have valid facebook ID. PFUser's FBID: %s, Facebook FBID: %s. logout",
                    currentParseUser.getFacebookId(), FBSDKProfile.getCurrentProfile().getUserID());
            ((AnyPicApp) UIApplication.getSharedApplication().getDelegate()).logOut();
            return;
        }

        // Finished checking for invalid stuff
        expectedFacebookResponseCount = 0;
        Set<String> permissions = FBSDKAccessToken.getCurrentAccessToken().getPermissions();
        if (permissions.contains("public_profile")) {
            // Logged in with FB
            // Create batch request for all the stuff

            FBSDKGraphRequestConnection connection = new FBSDKGraphRequestConnection();
            expectedFacebookResponseCount++;
            connection.addRequest(new FBSDKGraphRequest("me", null),
                    new VoidBlock3<FBSDKGraphRequestConnection, NSObject, NSError>() {
                        @Override
                        public void invoke(FBSDKGraphRequestConnection connection, NSObject result, NSError error) {
                            if (error != null) {
                                // Failed to fetch me data.. logout to be safe
                                Log.e("couldn't fetch facebook /me data: %s, logout", error);
                                ((AnyPicApp) UIApplication.getSharedApplication().getDelegate()).logOut();
                                return;
                            }
                            NSDictionary data = (NSDictionary) result;

                            String facebookName = data.getString("name");
                            if (facebookName != null && facebookName.length() > 0) {
                                currentParseUser.setDisplayName(facebookName);
                            }

                            processedFacebookResponse();
                        }
                    });

            // profile pic request
            // Now add the data to the UI elements
            NSURL profilePictureURL = new NSURL(String.format(
                    "https://graph.facebook.com/%s/picture?type=large&return_ssl_resources=1", facebookId));
            NSURLRequest profilePictureURLRequest = new NSURLRequest(profilePictureURL,
                    NSURLRequestCachePolicy.UseProtocolCachePolicy, 10);
            NSURLConnection.create(profilePictureURLRequest, profilePictureRequestDelegate).start();

            if (permissions.contains("user_friends")) {
                // Fetch FB Friends + me
                expectedFacebookResponseCount++;
                connection.addRequest(new FBSDKGraphRequest("me/friends", null),
                        new VoidBlock3<FBSDKGraphRequestConnection, NSObject, NSError>() {
                            @Override
                            public void invoke(FBSDKGraphRequestConnection connection, NSObject result, NSError error) {
                                Log.d("processing Facebook friends");
                                if (error != null) {
                                    // just clear the FB friend cache
                                    PAPCache.getSharedCache().clear();
                                } else {
                                    NSDictionary object = (NSDictionary) result;
                                    NSArray<NSDictionary> data = (NSArray<NSDictionary>) object.get("data");
                                    List<String> facebookIds = new ArrayList<>(data.size());
                                    for (NSDictionary friendData : data) {
                                        String friendId = friendData.getString("id");
                                        if (friendId != null) {
                                            facebookIds.add(friendId);
                                        }
                                    }
                                    // cache friend data
                                    PAPCache.getSharedCache().setFacebookFriends(facebookIds);

                                    currentParseUser.removeFacebookFriends();

                                    if (currentParseUser.hasAlreadyAutoFollowedFacebookFriends()) {
                                        ((AnyPicApp) UIApplication.getSharedApplication().getDelegate())
                                                .autoFollowUsers();
                                    }
                                }
                                processedFacebookResponse();
                            }
                        });
            }
            connection.start();
        } else {
            NSData profilePictureData = UIImage.create("AvatarPlaceholder").toPNGData();
            PAPUtility.processFacebookProfilePictureData(profilePictureData);

            PAPCache.getSharedCache().clear();
            currentParseUser.setDisplayName("Someone");
            expectedFacebookResponseCount++;
            processedFacebookResponse();
        }
    }

    private final NSURLConnectionDataDelegate profilePictureRequestDelegate = new NSURLConnectionDataDelegateAdapter() {
        @Override
        public void didReceiveResponse(NSURLConnection connection, NSURLResponse response) {
            profilePicData = new NSMutableData();
        }

        @Override
        public void didReceiveData(NSURLConnection connection, NSData data) {
            profilePicData.append(data);
        }

        @Override
        public void didFinishLoading(NSURLConnection connection) {
            PAPUtility.processFacebookProfilePictureData(profilePicData);
        }

        @Override
        public void didFail(NSURLConnection connection, NSError error) {
            Log.e("Connection error downloading profile pic data: %s", error);
        }
    };
}
