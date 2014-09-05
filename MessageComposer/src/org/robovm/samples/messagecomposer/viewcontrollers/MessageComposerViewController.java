/*
 * Copyright (C) 2014 Trillian Mobile AB
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
 * Portions of this code is based on Apple Inc's MessageComposer sample (v1.2)
 * which is copyright (C) 2010-2013 Apple Inc.
 */

package org.robovm.samples.messagecomposer.viewcontrollers;

import java.io.File;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.messageui.MFMailComposeResult;
import org.robovm.apple.messageui.MFMailComposeViewController;
import org.robovm.apple.messageui.MFMailComposeViewControllerDelegateAdapter;
import org.robovm.apple.messageui.MFMessageComposeViewController;
import org.robovm.apple.messageui.MFMessageComposeViewControllerDelegateAdapter;
import org.robovm.apple.messageui.MessageComposeResult;
import org.robovm.apple.uikit.NSLineBreakMode;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIButtonType;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIInterfaceOrientation;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;

public class MessageComposerViewController extends UIViewController {
    /* UILabel for displaying the result of sending the message. */
    private final UILabel feedbackMsg;

    public MessageComposerViewController () {
        super();

        UIView view = getView();
        view.setBackgroundColor(UIColor.createFromWhiteAlpha(0.75, 1));

        UIButton mailButton = UIButton.create(UIButtonType.RoundedRect);
        mailButton.setFrame(new CGRect(20, 197, 135, 37));
        mailButton.getTitleLabel().setFont(UIFont.getSystemFont(15));
        mailButton.setTitle("Compose Mail", UIControlState.Normal);
        mailButton.setTitleColor(UIColor.createFromRGBA(0.19, 0.30, 0.52, 1), UIControlState.Normal);
        mailButton.setTitleShadowColor(UIColor.createFromWhiteAlpha(0, 1), UIControlState.Normal);
        mailButton.setTitleColor(UIColor.createFromRGBA(1, 1, 1, 1), UIControlState.Highlighted);
        mailButton.addOnTouchUpInsideListener(new UIControl.OnTouchUpInsideListener() {
            @Override
            public void onTouchUpInside (UIControl control, UIEvent event) {
                showMailPicker();
            }
        });
        view.addSubview(mailButton);

        UIButton smsButton = UIButton.create(UIButtonType.RoundedRect);
        smsButton.setFrame(new CGRect(163, 197, 137, 37));
        smsButton.getTitleLabel().setFont(UIFont.getSystemFont(15));
        smsButton.setTitle("Compose SMS", UIControlState.Normal);
        smsButton.setTitleColor(UIColor.createFromRGBA(0.19, 0.30, 0.52, 1), UIControlState.Normal);
        smsButton.setTitleShadowColor(UIColor.createFromWhiteAlpha(0, 1), UIControlState.Normal);
        smsButton.setTitleColor(UIColor.createFromRGBA(1, 1, 1, 1), UIControlState.Highlighted);
        smsButton.addOnTouchUpInsideListener(new UIControl.OnTouchUpInsideListener() {
            @Override
            public void onTouchUpInside (UIControl control, UIEvent event) {
                showSMSPicker();
            }
        });
        view.addSubview(smsButton);

        feedbackMsg = new UILabel(new CGRect(20, 269, 280, 117));
        feedbackMsg.setTextAlignment(NSTextAlignment.Center);
        feedbackMsg.setLineBreakMode(NSLineBreakMode.TruncatingTail);
        feedbackMsg.setNumberOfLines(7);
        feedbackMsg.setFont(UIFont.getSystemFont(15));
        feedbackMsg.setTextColor(UIColor.colorDarkText());
        view.addSubview(feedbackMsg);
    }

    @Override
    public boolean shouldAutorotate (UIInterfaceOrientation toInterfaceOrientation) {
        return toInterfaceOrientation == UIInterfaceOrientation.Portrait;
    }

    private void showMailPicker () {
        /*
         * You must check that the current device can send email messages before you attempt to create an instance of
         * MFMailComposeViewController. Otherwise your app will crash when it creates a new MFMailComposeViewController.
         */
        if (MFMailComposeViewController.canSendMail()) {
            // The device can send email.
            displayMailComposerSheet();
        } else {
            // The device can not send email.
            feedbackMsg.setHidden(false);
            feedbackMsg.setText("Device not configured to send mail.");
        }
    }

    private void showSMSPicker () {
        /*
         * You must check that the current device can send SMS messages before you attempt to create an instance of
         * MFMessageComposeViewController. Otherwise your app will crash when it creates a new MFMessageComposeViewController.
         */
        if (MFMessageComposeViewController.canSendText()) {
            // The device can send SMS.
            displaySMSComposerSheet();
        } else {
            // The device can not send email.
            feedbackMsg.setHidden(false);
            feedbackMsg.setText("Device not configured to send SMS.");
        }
    }

    /** Displays an email composition interface inside the application. Populates all the Mail fields. */
    private void displayMailComposerSheet () {
        MFMailComposeViewController picker = new MFMailComposeViewController();
        picker.setMailComposeDelegate(new MFMailComposeViewControllerDelegateAdapter() {
            /** Dismisses the email composition interface when users tap Cancel or Send. Proceeds to update the message field with
             * the result of the operation. */
            @Override
            public void didFinish (MFMailComposeViewController controller, MFMailComposeResult result, NSError error) {
                feedbackMsg.setHidden(false);

                // Notifies users about errors associated with the interface
                String resultText;
                switch (result) {
                case Cancelled:
                    resultText = "Result: Mail sending canceled";
                    break;
                case Saved:
                    resultText = "Result: Mail saved";
                    break;
                case Sent:
                    resultText = "Result: Mail sent";
                    break;
                case Failed:
                    resultText = "Result: Mail sending failed";
                    break;
                default:
                    resultText = "Result: Mail not sent";
                    break;
                }
                feedbackMsg.setText(resultText);

                dismissViewController(true, null);
            }
        });
        picker.setSubject("Hello from California!");

        // Set up recipients
        NSArray<NSString> toRecipients = NSArray.toNSArray("first@example.com");
        NSArray<NSString> ccRecipients = NSArray.toNSArray("second@example.com", "third@example.com");
        NSArray<NSString> bccRecipients = NSArray.toNSArray("fourth@example.com");

        picker.setToRecipients(toRecipients);
        picker.setCcRecipients(ccRecipients);
        picker.setBccRecipients(bccRecipients);

        // Attach an image to the email
        String path = NSBundle.getMainBundle().findResourcePath("rainy", "jpg");
        NSData myData = NSData.read(new File(path));
        picker.addAttachmentData(myData, "image/jpeg", "rainy");

        // Fill out the email body text
        String emailBody = "It is raining in sunny California!";
        picker.setMessageBody(emailBody, false);

        presentViewController(picker, true, null);
    }

    /** Displays an SMS composition interface inside the application. */
    private void displaySMSComposerSheet () {
        MFMessageComposeViewController picker = new MFMessageComposeViewController();
        picker.setMessageComposeDelegate(new MFMessageComposeViewControllerDelegateAdapter() {
            /** Dismisses the message composition interface when users tap Cancel or Send. Proceeds to update the feedback message
             * field with the result of the operation. */
            @Override
            public void didFinish (MFMessageComposeViewController controller, MessageComposeResult result) {
                feedbackMsg.setHidden(false);

                String resultText;
                // Notifies users about errors associated with the interface
                switch (result) {
                case Cancelled:
                    resultText = "Result: SMS sending canceled";
                    break;
                case Sent:
                    resultText = "Result: SMS sent";
                    break;
                case Failed:
                    resultText = "Result: SMS sending failed";
                    break;
                default:
                    resultText = "Result: SMS not sent";
                    break;
                }
                feedbackMsg.setText(resultText);

                dismissViewController(true, null);
            }
        });
        /*
         * You can specify one or more preconfigured recipients. The user has the option to remove or add recipients from the
         * message composer view controller.
         */
        /* picker.setRecipients(NSArray.toNSArray("Phone number here")); */

        /*
         * You can specify the initial message text that will appear in the message composer view controller.
         */
        picker.setBody("Hello from California!");

        presentViewController(picker, true, null);
    }
}
