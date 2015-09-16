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
 * Portions of this code is based on Apple Inc's UICatalog sample (v11.3)
 * which is copyright (C) 2008-2015 Apple Inc.
 */
package org.robovm.samples.uicatalog.viewcontrollers;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSAttributedString;
import org.robovm.apple.foundation.NSMutableAttributedString;
import org.robovm.apple.foundation.NSNotificationCenter;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSRange;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.NSAttributedStringAttribute;
import org.robovm.apple.uikit.NSAttributedStringAttributes;
import org.robovm.apple.uikit.NSLayoutConstraint;
import org.robovm.apple.uikit.NSTextAttachment;
import org.robovm.apple.uikit.NSUnderlineStyle;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonSystemItem;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIFontDescriptor;
import org.robovm.apple.uikit.UIFontDescriptorSymbolicTraits;
import org.robovm.apple.uikit.UIFontTextStyle;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIKeyboardAnimation;
import org.robovm.apple.uikit.UITextView;
import org.robovm.apple.uikit.UITextViewDelegateAdapter;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewAnimationOptions;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.apple.uikit.UIWindow;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;
import org.robovm.objc.block.VoidBlock1;
import org.robovm.samples.uicatalog.Colors;

@CustomClass("AAPLTextViewController")
public class AAPLTextViewController extends UIViewController implements UIBarButtonItem.OnClickListener {
    private UITextView textView;

    // Used to adjust the text view's height when the keyboard hides and shows.
    private NSLayoutConstraint textViewBottomLayoutGuideConstraint;

    private NSObject keyboardWillShowNotification;
    private NSObject keyboardWillHideNotification;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        configureTextView();
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);

        // Listen for changes to keyboard visibility so that we can adjust the
        // text view accordingly.
        keyboardWillShowNotification = UIWindow.Notifications
                .observeKeyboardWillShow(new VoidBlock1<UIKeyboardAnimation>() {
                    @Override
                    public void invoke(UIKeyboardAnimation a) {
                        handleKeyboardNotification(a);
                    }
                });
        keyboardWillHideNotification = UIWindow.Notifications
                .observeKeyboardWillHide(new VoidBlock1<UIKeyboardAnimation>() {
                    @Override
                    public void invoke(UIKeyboardAnimation a) {
                        handleKeyboardNotification(a);
                    }
                });
    }

    @Override
    public void viewDidDisappear(boolean animated) {
        super.viewDidDisappear(animated);

        NSNotificationCenter.getDefaultCenter().removeObserver(keyboardWillShowNotification);
        NSNotificationCenter.getDefaultCenter().removeObserver(keyboardWillHideNotification);
    }

    private void handleKeyboardNotification(UIKeyboardAnimation animation) {
        double animationDuration = animation.getAnimationDuration();

        // Convert the keyboard frame from screen to view coordinates.
        CGRect keyboardScreenBeginFrame = animation.getStartFrame();
        CGRect keyboardScreenEndFrame = animation.getEndFrame();

        CGRect keyboardViewBeginFrame = getView().convertRectFromView(keyboardScreenBeginFrame, getView().getWindow());
        CGRect keyboardViewEndFrame = getView().convertRectFromView(keyboardScreenEndFrame, getView().getWindow());
        double originDelta = keyboardViewEndFrame.getOrigin().getY() - keyboardViewBeginFrame.getOrigin().getY();

        // The text view should be adjusted, update the constant for this
        // constraint.
        textViewBottomLayoutGuideConstraint
                .setConstant(textViewBottomLayoutGuideConstraint.getConstant() - originDelta);

        getView().setNeedsUpdateConstraints();

        UIView.animate(animationDuration, 0, UIViewAnimationOptions.BeginFromCurrentState, new Runnable() {
            @Override
            public void run() {
                getView().layoutIfNeeded();
            }
        }, null);

        // Scroll to the selected text once the keyboard frame changes.
        NSRange selectedRange = textView.getSelectedRange();
        textView.scrollRangeToVisible(selectedRange);
    }

    private void configureTextView() {
        textView.setDelegate(new UITextViewDelegateAdapter() {
            @Override
            public void didBeginEditing(UITextView textView) {
                // Provide a "Done" button for the user to select to signify
                // completion with writing text in the text view.
                UIBarButtonItem doneBarButtonItem = new UIBarButtonItem(UIBarButtonSystemItem.Done,
                        AAPLTextViewController.this);

                getNavigationItem().setRightBarButtonItem(doneBarButtonItem, true);
            }
        });

        UIFontDescriptor bodyFontDescriptor = UIFontDescriptor.getPreferredFontDescriptor(UIFontTextStyle.Body);
        textView.setFont(UIFont.getFont(bodyFontDescriptor, 0));

        textView.setTextColor(UIColor.black());
        textView.setBackgroundColor(UIColor.white());
        textView.setScrollEnabled(true);

        // Let's modify some of the attributes of the attributed string.
        // You can modify these attributes yourself to get a better feel for
        // what they do.
        // Note that the initial text is visible in the storyboard.
        NSMutableAttributedString attributedText = new NSMutableAttributedString(textView.getAttributedText());

        String text = textView.getText();

        // Find the range of each element to modify.
        NSRange boldRange = NSString.rangeOf(text, "bold");
        NSRange highlightedRange = NSString.rangeOf(text, "highlighted");
        NSRange underlinedRange = NSString.rangeOf(text, "underlined");
        NSRange tintedRange = NSString.rangeOf(text, "tinted");

        // Add bold.
        UIFontDescriptor boldFontDescriptor = textView.getFont().getFontDescriptor()
                .newWithSymbolicTraits(UIFontDescriptorSymbolicTraits.TraitBold);
        UIFont boldFont = UIFont.getFont(boldFontDescriptor, 0);
        attributedText.addAttribute(NSAttributedStringAttribute.Font, boldFont, boldRange);

        // Add highlight.
        attributedText.addAttribute(NSAttributedStringAttribute.BackgroundColor, Colors.GREEN, highlightedRange);

        // Add underline.
        attributedText.addAttributes(
                new NSAttributedStringAttributes().setUnderlineStyle(NSUnderlineStyle.StyleSingle), underlinedRange);

        // Add tint.
        attributedText.addAttribute(NSAttributedStringAttribute.ForegroundColor, Colors.BLUE, tintedRange);

        // Add an image attachment.
        NSTextAttachment textAttachment = new NSTextAttachment();
        UIImage image = UIImage.getImage("text_view_attachment");
        textAttachment.setImage(image);
        textAttachment.setBounds(new CGRect(0, 0, image.getSize().getWidth(), image.getSize().getHeight()));

        NSAttributedString textAttachmentString = new NSAttributedString(textAttachment);
        attributedText.append(textAttachmentString);

        textView.setAttributedText(attributedText);
    }

    @Override
    public void onClick(UIBarButtonItem barButtonItem) {
        // Dismiss the keyboard by removing it as the first responder.
        textView.resignFirstResponder();

        getNavigationItem().setRightBarButtonItem(null, true);
    }

    @IBOutlet
    private void setTextView(UITextView textView) {
        this.textView = textView;
    }

    @IBOutlet
    private void setTextViewBottomLayoutGuideConstraint(NSLayoutConstraint textViewBottomLayoutGuideConstraint) {
        this.textViewBottomLayoutGuideConstraint = textViewBottomLayoutGuideConstraint;
    }
}
