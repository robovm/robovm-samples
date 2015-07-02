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
 * Portions of this code is based on Chartboost's iOS SDK sample
 * which is copyright (C) 2013 Chartboost.
 */
package org.robovm.samples.robopods.chartboost.ios.ui;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.foundation.NSUserDefaults;
import org.robovm.apple.storekit.SKProduct;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIBarStyle;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIInterfaceOrientationMask;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIResponder;
import org.robovm.apple.uikit.UIToolbar;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import org.robovm.objc.block.VoidBooleanBlock;
import org.robovm.pods.chartboost.CBAnalytics;
import org.robovm.pods.chartboost.CBInPlay;
import org.robovm.pods.chartboost.CBLevelType;
import org.robovm.pods.chartboost.CBLocation;
import org.robovm.pods.chartboost.Chartboost;

@CustomClass("ViewController")
public class ViewController extends UIViewController {
    private static final String PIA_GLOBAL_LEVEL_NUMBER_KEY = "globalLevelNumber";
    private static final long IN_PLAY_SUCCESS_VIEW_TAG = 999;
    private static final long IN_PLAY_ERROR_VIEW_TAG = 888;
    private static final long PIA_VIEW_TAG = 888;

    private boolean inPlayShowing;
    private boolean piaShowing;
    private boolean inPlayShowingError;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        // Setup PIA number in NSUserDefaults
        long globalLevel = NSUserDefaults.getStandardUserDefaults().getInteger(PIA_GLOBAL_LEVEL_NUMBER_KEY);
        if (globalLevel <= 0) {
            NSUserDefaults.getStandardUserDefaults().put(PIA_GLOBAL_LEVEL_NUMBER_KEY, 0);
            NSUserDefaults.getStandardUserDefaults().synchronize();
        }

    }

    @IBAction
    private void showInterstitial() {
        Chartboost.showInterstitial(CBLocation.HomeScreen);
    }

    @IBAction
    private void showMoreApps() {
        Chartboost.showMoreApps(CBLocation.HomeScreen);
    }

    @IBAction
    private void cacheInterstitial() {
        Chartboost.cacheInterstitial(CBLocation.HomeScreen);
    }

    @IBAction
    private void cacheMoreApps() {
        Chartboost.cacheMoreApps(CBLocation.HomeScreen);
    }

    @IBAction
    private void cacheRewardedVideo() {
        Chartboost.cacheRewardedVideo(CBLocation.HomeScreen);
    }

    @IBAction
    private void showRewardedVideo() {
        Chartboost.showRewardedVideo(CBLocation.MainMenu);
    }

    @IBAction
    private void showSupport(UIResponder sender) {
        UIApplication.getSharedApplication().openURL(new NSURL("http://answers.chartboost.com"));
    }

    @IBAction
    private void showInPlay(UIResponder sender) {
        CBInPlay inPlay = Chartboost.getInPlay("native");
        if (inPlay != null) {
            System.out.println("Success, we have a valid inPlay item");
            renderInPlay(inPlay);
        }
    }

    public void renderInPlay(CBInPlay inPlay) {
        if (inPlayShowing) {
            return;
        }
        inPlayShowing = true;

        double screenWidth = getView().getBounds().getSize().getWidth();
        double screenHeight = getView().getBounds().getSize().getHeight();
        double width = screenWidth;
        double height = screenHeight;
        double x = 0;
        double y = 0;

        CGRect overlayFrame = new CGRect(x, y, width, height);
        UIView blur = null;

        if (Foundation.getMajorSystemVersion() < 7) {
            blur = new UIView(overlayFrame);
            blur.setBackgroundColor(UIColor.black().addAlpha(0.7));
        } else {
            blur = new UIToolbar(overlayFrame);
            ((UIToolbar) blur).setBarStyle(UIBarStyle.Black);
            ((UIToolbar) blur).setTranslucent(true);
        }

        // Add inPlay SubViews
        UIImage appIconImage = new UIImage(inPlay.getAppIcon());
        UIImageView appIcon = new UIImageView(appIconImage);
        appIcon.setFrame(new CGRect(screenWidth / 2.0 - appIconImage.getSize().getWidth() / 2.0, 30, appIconImage
                .getSize().getHeight(), appIconImage.getSize().getWidth()));
        UILabel appName = new UILabel(new CGRect(0, appIcon.getFrame().getOrigin().getY()
                + appIcon.getFrame().getSize().getHeight() + 50, 999, 999));
        appName.setFont(UIFont.getFont("Helvetica", 18));
        appName.setText(inPlay.getAppName());
        appName.sizeToFit();
        appName.setFrame(new CGRect(screenWidth / 2.0 - appName.getFrame().getSize().getWidth() / 2.0, appName
                .getFrame().getOrigin().getY(), appName.getFrame().getSize().getWidth(), appName.getFrame().getSize()
                .getHeight()));
        appName.setTextColor(UIColor.white());
        appName.setBackgroundColor(UIColor.clear());

        UILabel iconTitle = new UILabel(new CGRect(appIcon.getFrame().getOrigin().getX(), appIcon.getFrame()
                .getOrigin().getY()
                + appIcon.getFrame().getSize().getHeight() + 3, appIcon.getFrame().getSize().getWidth(), 10));
        iconTitle.setFont(UIFont.getSystemFont(9));
        iconTitle.setTextColor(UIColor.white());
        iconTitle.setTextAlignment(NSTextAlignment.Center);
        iconTitle.setText("App Icon");
        iconTitle.setBackgroundColor(UIColor.clear());

        UILabel nameTitle = new UILabel(new CGRect(appName.getFrame().getOrigin().getX(), appName.getFrame()
                .getOrigin().getY()
                + appName.getFrame().getSize().getHeight() + 3, appName.getFrame().getSize().getWidth(), 10));
        nameTitle.setFont(UIFont.getSystemFont(9));
        nameTitle.setTextColor(UIColor.white());
        nameTitle.setTextAlignment(NSTextAlignment.Center);
        nameTitle.setText("App Name");
        nameTitle.setBackgroundColor(UIColor.clear());

        double buttonWidth = 50.0;
        UIButton closeButton = new UIButton(new CGRect(screenWidth - buttonWidth - 5, 5, buttonWidth, buttonWidth));
        closeButton.addOnTouchUpInsideListener(new UIControl.OnTouchUpInsideListener() {
            @Override
            public void onTouchUpInside(UIControl control, UIEvent event) {
                UIView.animate(0.3, new Runnable() {
                    @Override
                    public void run() {
                        getView().getViewWithTag(IN_PLAY_SUCCESS_VIEW_TAG).setAlpha(0.0);
                    }
                }, new VoidBooleanBlock() {
                    @Override
                    public void invoke(boolean finished) {
                        getView().getViewWithTag(IN_PLAY_SUCCESS_VIEW_TAG).removeFromSuperview();
                        inPlayShowing = false;
                    }
                });
            }
        });

        UILabel closeButtonLabel = new UILabel(closeButton.getFrame());
        closeButtonLabel.setText(Character.toString((char) 0x2715));
        closeButtonLabel.setFont(UIFont.getSystemFont(35));
        closeButtonLabel.setTextColor(UIColor.white());
        closeButtonLabel.setTextAlignment(NSTextAlignment.Center);
        closeButtonLabel.setBackgroundColor(UIColor.clear());

        blur.addSubview(closeButton);
        blur.addSubview(closeButtonLabel);
        blur.addSubview(appIcon);
        blur.addSubview(appName);
        blur.addSubview(iconTitle);
        blur.addSubview(nameTitle);

        blur.setTag(IN_PLAY_SUCCESS_VIEW_TAG);
        blur.setAlpha(0.0);
        getView().addSubview(blur);

        UIView.animate(0.3, new Runnable() {
            @Override
            public void run() {
                getView().getViewWithTag(IN_PLAY_SUCCESS_VIEW_TAG).setAlpha(1.0);
            }
        });
        inPlayShowing = true;
    }

    @IBAction
    private void sendPIALevelTracking(UIResponder sender) {
        System.out.println("sendPIA level tracking");
        long highestLevel = NSUserDefaults.getStandardUserDefaults().getInteger(PIA_GLOBAL_LEVEL_NUMBER_KEY);
        CBLevelType levelType = CBLevelType.HIGHEST_LEVEL_REACHED;
        String eventLabel = "character level";
        String eventDescription = "highest level";
        long subLevel = 0;
        CBAnalytics.trackLevelInfo(eventLabel, levelType, highestLevel, subLevel, eventDescription);

        renderPIALevelTrackingAlert(eventLabel, levelType, highestLevel, subLevel, eventDescription);

        NSUserDefaults.getStandardUserDefaults().put(PIA_GLOBAL_LEVEL_NUMBER_KEY, highestLevel + 1);
        NSUserDefaults.getStandardUserDefaults().synchronize();
    }

    private void renderPIALevelTrackingAlert(String eventLabel, CBLevelType levelType, long mainLevel, long subLevel,
            String eventDescription) {
        if (piaShowing) {
            return;
        }
        piaShowing = true;

        double screenWidth = getView().getBounds().getSize().getWidth();
        double screenHeight = getView().getBounds().getSize().getHeight();
        double width = screenWidth;
        double height = screenHeight;
        double x = 0;
        double y = 0;

        CGRect overlayFrame = new CGRect(x, y, width, height);
        UIView blur = null;

        if (Foundation.getMajorSystemVersion() < 7) {
            blur = new UIView(overlayFrame);
            blur.setBackgroundColor(UIColor.black().addAlpha(0.7));
        } else {
            blur = new UIToolbar(overlayFrame);
            ((UIToolbar) blur).setBarStyle(UIBarStyle.Black);
            ((UIToolbar) blur).setTranslucent(true);
        }

        UILabel errorLabel = new UILabel(new CGRect(0, 0, width, height));
        errorLabel.setFont(UIFont.getFont("Helvetica", 18));
        errorLabel.setNumberOfLines(5);
        errorLabel.setTextAlignment(NSTextAlignment.Left);
        errorLabel.setText(String.format(
                "Event Label - %s\nMain Level - %d\nSub Level - %d\nDescription - %s\n Level Type - %s", eventLabel,
                mainLevel, subLevel, eventDescription, levelType));
        errorLabel.sizeToFit();
        errorLabel.setFrame(new CGRect(screenWidth / 2.0 - errorLabel.getFrame().getSize().getWidth() / 2.0,
                screenHeight / 2.0 - errorLabel.getFrame().getSize().getHeight() / 2.0, errorLabel.getFrame().getSize()
                        .getWidth(), errorLabel.getFrame().getSize().getHeight()));
        errorLabel.setTextColor(UIColor.white());
        errorLabel.setBackgroundColor(UIColor.clear());

        double buttonWidth = 50;
        UIButton closeButton = new UIButton(new CGRect(screenWidth - buttonWidth - 5, 5, buttonWidth, buttonWidth));
        closeButton.addOnTouchUpInsideListener(new UIControl.OnTouchUpInsideListener() {
            @Override
            public void onTouchUpInside(UIControl control, UIEvent event) {
                UIView.animate(0.3, new Runnable() {
                    @Override
                    public void run() {
                        getView().getViewWithTag(PIA_VIEW_TAG).setAlpha(0);
                    }
                }, new VoidBooleanBlock() {
                    @Override
                    public void invoke(boolean finished) {
                        getView().getViewWithTag(PIA_VIEW_TAG).removeFromSuperview();
                        piaShowing = false;
                    }
                });
            }
        });

        UILabel closeButtonLabel = new UILabel(closeButton.getFrame());
        closeButtonLabel.setText(Character.toString((char) 0x2715));
        closeButtonLabel.setFont(UIFont.getSystemFont(35));
        closeButtonLabel.setTextColor(UIColor.white());
        closeButtonLabel.setTextAlignment(NSTextAlignment.Center);
        closeButtonLabel.setBackgroundColor(UIColor.clear());

        blur.addSubview(closeButton);
        blur.addSubview(closeButtonLabel);
        blur.addSubview(errorLabel);
        blur.setTag(PIA_VIEW_TAG);
        blur.setAlpha(0);
        getView().addSubview(blur);

        UIView.animate(0.3, new Runnable() {
            @Override
            public void run() {
                getView().getViewWithTag(PIA_VIEW_TAG).setAlpha(1);
            }
        });
        piaShowing = true;
    }

    public void renderInPlayError(String error) {
        if (inPlayShowingError) {
            return;
        }
        inPlayShowingError = true;

        double screenWidth = getView().getBounds().getSize().getWidth();
        double screenHeight = getView().getBounds().getSize().getHeight();
        double width = screenWidth;
        double height = screenHeight;
        double x = 0;
        double y = 0;

        CGRect overlayFrame = new CGRect(x, y, width, height);
        UIView blur = null;

        if (Foundation.getMajorSystemVersion() < 7) {
            blur = new UIView(overlayFrame);
            blur.setBackgroundColor(UIColor.black().addAlpha(0.7));
        } else {
            blur = new UIToolbar(overlayFrame);
            ((UIToolbar) blur).setBarStyle(UIBarStyle.Black);
            ((UIToolbar) blur).setTranslucent(true);
        }

        // Add inPlay SubViews
        UILabel errorLabel = new UILabel(new CGRect(0, 0, 999, 999));
        errorLabel.setFont(UIFont.getFont("Helvetica", 18));
        errorLabel.setText(error);
        errorLabel.sizeToFit();
        errorLabel.setFrame(new CGRect(screenWidth / 2.0 - errorLabel.getFrame().getSize().getWidth() / 2.0,
                screenHeight / 2.0 - errorLabel.getFrame().getSize().getHeight() / 2.0, errorLabel.getFrame().getSize()
                        .getWidth(), errorLabel.getFrame().getSize().getHeight()));
        errorLabel.setTextColor(UIColor.white());
        errorLabel.setBackgroundColor(UIColor.clear());

        UILabel errorTitle = new UILabel(new CGRect(errorLabel.getFrame().getOrigin().getX(), errorLabel.getFrame()
                .getOrigin().getY()
                + errorLabel.getFrame().getSize().getHeight() + 3, errorLabel.getFrame().getSize().getWidth(), 10));
        errorTitle.setFont(UIFont.getSystemFont(9));
        errorTitle.setTextColor(UIColor.white());
        errorTitle.setTextAlignment(NSTextAlignment.Center);
        errorTitle.setText("Error");
        errorTitle.setBackgroundColor(UIColor.clear());

        double buttonWidth = 50;
        UIButton closeButton = new UIButton(new CGRect(screenWidth - buttonWidth - 5, 5, buttonWidth, buttonWidth));
        closeButton.addOnTouchUpInsideListener(new UIControl.OnTouchUpInsideListener() {
            @Override
            public void onTouchUpInside(UIControl control, UIEvent event) {
                UIView.animate(0.3, new Runnable() {
                    @Override
                    public void run() {
                        getView().getViewWithTag(IN_PLAY_ERROR_VIEW_TAG).setAlpha(0);
                    }
                }, new VoidBooleanBlock() {
                    @Override
                    public void invoke(boolean v) {
                        getView().getViewWithTag(IN_PLAY_ERROR_VIEW_TAG).removeFromSuperview();
                        inPlayShowingError = false;
                    }
                });
            }
        });

        UILabel closeButtonLabel = new UILabel(closeButton.getFrame());
        closeButtonLabel.setText(Character.toString((char) 0x2715));
        closeButtonLabel.setFont(UIFont.getSystemFont(35));
        closeButtonLabel.setTextColor(UIColor.white());
        closeButtonLabel.setTextAlignment(NSTextAlignment.Center);
        closeButtonLabel.setBackgroundColor(UIColor.clear());

        blur.addSubview(closeButton);
        blur.addSubview(closeButtonLabel);
        blur.addSubview(errorLabel);
        blur.addSubview(errorTitle);
        blur.setTag(IN_PLAY_ERROR_VIEW_TAG);
        blur.setAlpha(0);
        getView().addSubview(blur);

        UIView.animate(0.3, new Runnable() {
            @Override
            public void run() {
                getView().getViewWithTag(IN_PLAY_ERROR_VIEW_TAG).setAlpha(1);
            }
        });
        inPlayShowingError = true;
    }

    /**
     * This is an example of how to call the Chartboost Post Install Analytics
     * API. To fully use this feature you must implement the Apple In-App
     * Purchase
     *
     * Checkout https://developer.apple.com/in-app-purchase/ for information on
     * how to setup your app to use StoreKit
     */
    public void trackInAppPurchase(NSData transactionReceipt, SKProduct product) {
        CBAnalytics.trackInAppPurchaseEvent(transactionReceipt, product);
    }

    @Override
    public UIInterfaceOrientationMask getSupportedInterfaceOrientations() {
        return UIInterfaceOrientationMask.All;
    }
}
