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
 * Portions of this code is based on Apple Inc's MessageComposer sample (v1.2)
 * which is copyright (C) 2010-2013 Apple Inc.
 */
package org.robovm.samples.messagecomposer.ui;

import java.io.File;
import java.util.Arrays;

import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.messageui.MFMailComposeResult;
import org.robovm.apple.messageui.MFMailComposeViewController;
import org.robovm.apple.messageui.MFMailComposeViewControllerDelegate;
import org.robovm.apple.messageui.MFMessageComposeResult;
import org.robovm.apple.messageui.MFMessageComposeViewController;
import org.robovm.apple.messageui.MFMessageComposeViewControllerDelegate;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import org.robovm.objc.annotation.IBOutlet;

@CustomClass("MessageComposerViewController")
public class MessageComposerViewController extends UIViewController implements MFMailComposeViewControllerDelegate,
        MFMessageComposeViewControllerDelegate {
    /* UILabel for displaying the result of sending the message. */
    private UILabel feedbackMsg;

    @IBAction
    private void showMailPicker() {
        /*
         * You must check that the current device can send email messages before
         * you attempt to create an instance of MFMailComposeViewController.
         * Otherwise your app will crash when it creates a new
         * MFMailComposeViewController.
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

    @IBAction
    private void showSMSPicker() {
        /*
         * You must check that the current device can send SMS messages before
         * you attempt to create an instance of MFMessageComposeViewController.
         * Otherwise your app will crash when it creates a new
         * MFMessageComposeViewController.
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

    /**
     * Displays an email composition interface inside the application. Populates
     * all the Mail fields.
     */
    private void displayMailComposerSheet() {
        MFMailComposeViewController picker = new MFMailComposeViewController();
        picker.setMailComposeDelegate(this);
        picker.setSubject("Hello from California!");

        // Set up recipients
        picker.setToRecipients(Arrays.asList("first@example.com"));
        picker.setCcRecipients(Arrays.asList("second@example.com", "third@example.com"));
        picker.setBccRecipients(Arrays.asList("fourth@example.com"));

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
    private void displaySMSComposerSheet() {
        MFMessageComposeViewController picker = new MFMessageComposeViewController();
        picker.setMessageComposeDelegate(this);
        /*
         * You can specify one or more preconfigured recipients. The user has
         * the option to remove or add recipients from the message composer view
         * controller.
         */
        /* picker.setRecipients(NSArray.toNSArray("Phone number here")); */

        /*
         * You can specify the initial message text that will appear in the
         * message composer view controller.
         */
        picker.setBody("Hello from California!");

        presentViewController(picker, true, null);
    }

    @IBOutlet
    private void setFeedbackMsg(UILabel feedbackMsg) {
        this.feedbackMsg = feedbackMsg;
    }

    /**
     * Dismisses the email composition interface when users tap Cancel or Send.
     * Proceeds to update the message field with the result of the operation.
     */
    @Override
    public void didFinish(MFMailComposeViewController controller, MFMailComposeResult result, NSError error) {
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

    /**
     * Dismisses the message composition interface when users tap Cancel or
     * Send. Proceeds to update the feedback message field with the result of
     * the operation.
     */
    @Override
    public void didFinish(MFMessageComposeViewController controller, MFMessageComposeResult result) {
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
}
