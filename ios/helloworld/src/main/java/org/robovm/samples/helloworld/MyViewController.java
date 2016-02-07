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
 * Portions of this code is based on Apple Inc's HelloWorld sample (v1.8)
 * which is copyright (C) 2008-2010 Apple Inc.
 */
package org.robovm.samples.helloworld;

import org.robovm.apple.foundation.NSSet;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UITextField;
import org.robovm.apple.uikit.UITextFieldDelegateAdapter;
import org.robovm.apple.uikit.UITouch;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;

@CustomClass("MyViewController")
public class MyViewController extends UIViewController {
    @IBOutlet
    private UITextField textField;
    @IBOutlet
    private UILabel label;
    private String string;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        textField.setDelegate(new UITextFieldDelegateAdapter() {
            @Override
            public boolean shouldReturn(UITextField textField) {
                // When the user presses return, take focus away from the text
                // field so that the keyboard is dismissed.
                textField.resignFirstResponder();
                // Invoke the method that changes the greeting.
                updateString();

                return true;
            }
        });

        // When the view first loads, display the placeholder text that's in the
        // text field in the label.
        label.setText(textField.getPlaceholder());
    }

    private void updateString() {
        // Store the text of the text field in the 'string' instance variable.
        string = textField.getText();
        // Set the text of the label to the value of the 'string' instance
        // variable.
        label.setText(string);
    }

    @Override
    public void touchesBegan(NSSet<UITouch> touches, UIEvent event) {
        // Dismiss the keyboard when the view outside the text field is touched.
        textField.resignFirstResponder();
        // Revert the text field to the previous value.
        textField.setText(string);
        super.touchesBegan(touches, event);
    }
}
