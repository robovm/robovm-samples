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
 * Portions of this code is based on Apple Inc's UICatalog sample (v2.11)
 * which is copyright (C) 2008-2013 Apple Inc.
 */

package org.robovm.sample.uicatalog.viewcontrollers;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSMutableAttributedString;
import org.robovm.apple.foundation.NSNotification;
import org.robovm.apple.foundation.NSNotificationCenter;
import org.robovm.apple.foundation.NSNumber;
import org.robovm.apple.foundation.NSRange;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.foundation.NSValue;
import org.robovm.apple.uikit.NSValueExtensions;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonSystemItem;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIInterfaceOrientation;
import org.robovm.apple.uikit.UIKeyboardType;
import org.robovm.apple.uikit.UIKit;
import org.robovm.apple.uikit.UIReturnKeyType;
import org.robovm.apple.uikit.UITextAutocorrectionType;
import org.robovm.apple.uikit.UITextView;
import org.robovm.apple.uikit.UITextViewDelegateAdapter;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewAutoresizing;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.Selector;
import org.robovm.objc.annotation.Method;

/**
 * The view controller for hosting the UITextView features of this sample. 
 */
public class TextViewController extends UIViewController {
    private UITextView textView;
    
    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        
        this.setTitle("");
        this.setupTextView();
    }
    
    /**
     * setup components and load to UI
     */
    private void setupTextView() {
        getView().setFrame(new CGRect(0, 0, 320, 460));
        textView = new UITextView(getView().getFrame());
        textView.setTextColor(UIColor.colorBlack());
        textView.setFont(UIFont.getFont("Arial", 18.0));
        textView.setDelegate(new UITextViewDelegateAdapter() {
            @Override
            public void didBeginEditing(UITextView textView) {
                Selector saveAction = Selector.register("saveAction");
                UIBarButtonItem saveItem = new UIBarButtonItem(UIBarButtonSystemItem.Done, null, saveAction);
                saveItem.setTarget(TextViewController.this);
                getNavigationItem().setRightBarButtonItem(saveItem);
            }
        });
        textView.setBackgroundColor(UIColor.colorWhite());
        textView.setAutoresizingMask(UIViewAutoresizing.FlexibleWidth.set(UIViewAutoresizing.FlexibleHeight));

        String textToAdd = "Now is the time for all good developers to come to serve their country.\n\nNow is the time for all good developers to come to serve their country.\r\rThis text view can also use attributed strings.";
        NSMutableAttributedString attrString = new NSMutableAttributedString(textToAdd);
        attrString.addAttribute(UIKit.ForegroundColorAttributeName(), UIColor.colorRed(), new NSRange(textToAdd.length() -19, 19));
        attrString.addAttribute(UIKit.ForegroundColorAttributeName(), UIColor.colorBlue(), new NSRange(textToAdd.length() - 23, 3));
        attrString.addAttribute(UIKit.UnderlineStyleAttributeName(), NSNumber.valueOf(1l) , new NSRange(textToAdd.length() - 23, 3));
        this.textView.setAttributedText(attrString);

        textView.setReturnKeyType(UIReturnKeyType.Default);
        textView.setKeyboardType(UIKeyboardType.Default);
        textView.setScrollEnabled(true);
        textView.setAutocorrectionType(UITextAutocorrectionType.No);
        
        getView().addSubview(textView);
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);
        Selector willShow = Selector.register("keyboardWillShow:");
        Selector willHide = Selector.register("keyboardWillHide:");
        NSNotificationCenter center = NSNotificationCenter.getDefaultCenter();
        center.addObserver(this, willShow, UIKit.KeyboardWillShowNotification(), null);
        center.addObserver(this, willHide, UIKit.KeyboardWillHideNotification(), null);
    }
    

    /**
     * Called when to finish typing text/dismiss the keyboard by removing it as the first responder
     */
    @Method(selector = "saveAction")
    private void saveAction() {
            this.textView.resignFirstResponder();
            this.getNavigationItem().setRightBarButtonItem(null);   // this will remove the "save" button
    }
    
    private boolean isPortrait(UIInterfaceOrientation orientation) {
        return ((orientation == UIInterfaceOrientation.Portrait) || (orientation == UIInterfaceOrientation.PortraitUpsideDown));
    }
    
    /**
     * Modifies keyboards size to fit screen
     * @param showKeyboard
     * @param notificationInfo
     */
    private void adjustViewForKeyboardReveal(boolean showKeyboard, NSDictionary<NSString, ?> notificationInfo) {//notificationInfo:(NSDictionary *)notificationInfo
        // the keyboard is showing so resize the table's height

        CGRect keyboardRect = NSValueExtensions.getRectValue((NSValue)notificationInfo.get(UIKit.KeyboardFrameEndUserInfoKey()));
        double animationDuration = ((NSNumber) notificationInfo.get(UIKit.KeyboardAnimationDurationUserInfoKey())).doubleValue();

        CGRect frame = this.textView.getFrame();

        // the keyboard rect's width and height are reversed in landscape
        double adjustDelta = isPortrait(this.getInterfaceOrientation()) ? keyboardRect.getHeight() : keyboardRect.getWidth();
        
        if (showKeyboard) {
            frame.size().height(frame.size().height() - adjustDelta);
        } else {
            frame.size().height(frame.size().height() + adjustDelta);
        }
        
        UIView.beginAnimations("ResizeForKeyboard", null);
        UIView.setDurationForAnimation(animationDuration);
        this.textView.setFrame(frame);
        UIView.commitAnimations();
    }

    @Method
    void keyboardWillShow(NSNotification aNotification) {
        adjustViewForKeyboardReveal(true, aNotification.getUserInfo());
    }
    
    @Method
    void keyboardWillHide(NSNotification aNotification) {
        adjustViewForKeyboardReveal(false, aNotification.getUserInfo());
    }

    @Override
    public void viewDidDisappear(boolean animated) {
        super.viewDidDisappear(animated);
        
        NSNotificationCenter.getDefaultCenter().removeObserver(UIKit.KeyboardWillShowNotification());
        NSNotificationCenter.getDefaultCenter().removeObserver(UIKit.KeyboardWillHideNotification());

    }

}
