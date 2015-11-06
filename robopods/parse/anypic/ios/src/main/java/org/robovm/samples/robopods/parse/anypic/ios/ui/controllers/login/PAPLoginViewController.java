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
package org.robovm.samples.robopods.parse.anypic.ios.ui.controllers.login;

import java.util.Arrays;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.uikit.UIAlertView;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIStatusBarStyle;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.pods.facebook.core.FBSDKAccessToken;
import org.robovm.pods.facebook.core.FBSDKError;
import org.robovm.pods.facebook.core.FBSDKErrorCode;
import org.robovm.pods.facebook.login.FBSDKLoginButton;
import org.robovm.pods.facebook.login.FBSDKLoginButtonDelegate;
import org.robovm.pods.facebook.login.FBSDKLoginButtonTooltipBehavior;
import org.robovm.pods.facebook.login.FBSDKLoginManagerLoginResult;
import org.robovm.pods.parse.PFFacebookUtils;
import org.robovm.pods.parse.PFLogInCallback;
import org.robovm.pods.parse.PFUser;
import org.robovm.samples.robopods.parse.anypic.ios.AnyPicApp;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPUser;
import org.robovm.samples.robopods.parse.anypic.ios.util.Log;

public class PAPLoginViewController extends UIViewController implements FBSDKLoginButtonDelegate {
    private PAPLogInViewControllerDelegate delegate;
    private FBSDKLoginButton facebookLoginButton;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        UIImageView backgroundImageView = new UIImageView(UIScreen.getMainScreen().getApplicationFrame());
        backgroundImageView.setImage(UIImage.getImage("BackgroundLogin"));
        getView().addSubview(backgroundImageView);

        // Position of the Facebook button
        double yPosition = 360;
        if (UIScreen.getMainScreen().getBounds().getSize().getHeight() > 480f) {
            yPosition = 450;
        }

        facebookLoginButton = new FBSDKLoginButton();
        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends", "email",
                "user_photos"));
        facebookLoginButton.setFrame(new CGRect((UIScreen.getMainScreen().getBounds().getWidth() - 244) / 2, yPosition,
                244, 44));
        facebookLoginButton.setDelegate(this);
        facebookLoginButton.setTooltipBehavior(FBSDKLoginButtonTooltipBehavior.Disable);
        getView().addSubview(facebookLoginButton);
    }

    @Override
    public UIStatusBarStyle getPreferredStatusBarStyle() {
        return UIStatusBarStyle.LightContent;
    }

    @Override
    public void didComplete(FBSDKLoginButton loginButton, FBSDKLoginManagerLoginResult result, NSError error) {
        if (error != null) {
            handleLogInError(error);
        } else {
            handleLogIn(result);
        }
    }

    @Override
    public void didLogOut(FBSDKLoginButton loginButton) {
        PFUser.logOut();
    }

    private void handleLogIn(FBSDKLoginManagerLoginResult result) {
        if (!result.isCancelled() && result.getToken() != null) {
            if (PAPUser.getCurrentUser() != null) {
                if (delegate != null) {
                    delegate.didLogin(this);
                }
                return;
            }

            FBSDKAccessToken accessToken = FBSDKAccessToken.getCurrentAccessToken();
            String facebookUserId = accessToken.getUserID();

            if (accessToken == null || facebookUserId == null) {
                Log.e("Login failure. FB Access Token or user ID does not exist");
                return;
            }

//                self.hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES]; TODO

            PFFacebookUtils.logInInBackground(accessToken, new PFLogInCallback() {
                @Override
                public void done(PFUser user, NSError error) {
                    if (error == null) {
//                        [self.hud removeFromSuperview]; TODO
                        if (delegate != null) {
                            delegate.didLogin(PAPLoginViewController.this);
                        }
                    } else {
                        cancelLogIn(error);
                    }
                }
            });
        }
    }

    private void cancelLogIn(NSError error) {
        if (error != null) {
            handleLogInError(error);
        }
//  [self.hud removeFromSuperview]; TODO
        PFFacebookUtils.getFacebookLoginManager().logOut();
        PFUser.logOut();
        ((AnyPicApp) UIApplication.getSharedApplication().getDelegate()).presentLoginViewController(false);
    }

    private void handleLogInError(NSError error) {
        Log.e("Error: %s", error);
        if (error instanceof FBSDKError) {
            FBSDKError e = (FBSDKError) error;

            String title = "Login Error";
            String message = "Something went wrong. Please try again.";
            String ok = "OK";

            if (e.getErrorCode() == FBSDKErrorCode.Network) {
                title = "Offile Error";
            }

            UIAlertView alertView = new UIAlertView(title, message, null, ok);
            alertView.show();
        }
    }

    public void setDelegate(PAPLogInViewControllerDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean willLogin(FBSDKLoginButton loginButton) {
        return true;
    }
}
