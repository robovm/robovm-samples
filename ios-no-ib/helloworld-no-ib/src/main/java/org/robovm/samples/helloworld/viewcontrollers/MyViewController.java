/*
 * Copyright (C) 2014 RoboVM AB
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
 * Portions of this code is based on Apple Inc's HelloWorld sample (v1.8)
 * which is copyright (C) 2008-2010 Apple Inc.
 */

package org.robovm.samples.helloworld.viewcontrollers;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSSet;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIKeyboardType;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIReturnKeyType;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UITextBorderStyle;
import org.robovm.apple.uikit.UITextField;
import org.robovm.apple.uikit.UITextFieldDelegateAdapter;
import org.robovm.apple.uikit.UITextFieldViewMode;
import org.robovm.apple.uikit.UITouch;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;

public class MyViewController extends UIViewController {
    private UITextField textField;
    private UILabel label;
    private String string;

    public MyViewController() {
        UIView view = getView();

        // Setup background.
        UIImageView background = new UIImageView(UIImage.getImage("Background.png"));
        background.setFrame(UIScreen.getMainScreen().getBounds());
        view.addSubview(background);

        // Setup textfield.
        textField = new UITextField(new CGRect(44, 32, 232, 31));
        textField.setBorderStyle(UITextBorderStyle.RoundedRect);
        textField.setPlaceholder("Hello, World!");
        textField.setClearsOnBeginEditing(true);
        textField.setKeyboardType(UIKeyboardType.ASCIICapable);
        textField.setReturnKeyType(UIReturnKeyType.Done);
        // When the user starts typing, show the clear button in the text field.
        textField.setClearButtonMode(UITextFieldViewMode.WhileEditing);
        textField.setDelegate(new UITextFieldDelegateAdapter() {
            @Override
            public boolean shouldReturn(UITextField theTextField) {
                // When the user presses return, take focus away from the text
                // field so that the keyboard is dismissed.
                if (theTextField == textField) {
                    textField.resignFirstResponder();
                    // Invoke the method that changes the greeting.
                    updateString();
                }
                return true;
            }
        });
        view.addSubview(textField);

        // Setup label.
        label = new UILabel(new CGRect(20, 104, 280, 44));
        label.setFont(UIFont.getSystemFont(24));
        label.setTextColor(UIColor.white());
        label.setTextAlignment(NSTextAlignment.Center);
        // When the view first loads, display the placeholder text that's in the
        // text field in the label.
        label.setText(textField.getPlaceholder());
        view.addSubview(label);
    }

    @Override
    public void touchesBegan(NSSet<UITouch> touches, UIEvent event) {
        // Dismiss the keyboard when the view outside the text field is touched.
        textField.resignFirstResponder();
        // Revert the text field to the previous value.
        textField.setText(string);

        super.touchesBegan(touches, event);
    }

    public void updateString() {
        // Store the text of the text field in the 'string' instance variable.
        string = textField.getText();
        // Set the text of the label to the value of the 'string' instance
        // variable.
        label.setText(string);
    }
}
