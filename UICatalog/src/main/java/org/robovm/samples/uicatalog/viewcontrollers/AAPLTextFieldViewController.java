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
import org.robovm.apple.foundation.NSRange;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIButtonType;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEdgeInsets;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIKeyboardType;
import org.robovm.apple.uikit.UIReturnKeyType;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.apple.uikit.UITextAutocorrectionType;
import org.robovm.apple.uikit.UITextBorderStyle;
import org.robovm.apple.uikit.UITextField;
import org.robovm.apple.uikit.UITextFieldDelegate;
import org.robovm.apple.uikit.UITextFieldViewMode;
import org.robovm.apple.uikit.UIView;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;
import org.robovm.samples.uicatalog.Colors;

@CustomClass("AAPLTextFieldViewController")
public class AAPLTextFieldViewController extends UITableViewController implements UITextFieldDelegate {
    private UITextField textField;
    private UITextField tintedTextField;
    private UITextField secureTextField;
    private UITextField specificKeyboardTextField;
    private UITextField customTextField;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        configureTextField();
        configureTintedTextField();
        configureSecureTextField();
        configureSpecificKeyboardTextField();
        configureCustomTextField();
    }

    private void configureTextField() {
        textField.setPlaceholder("Placeholder text");
        textField.setAutocorrectionType(UITextAutocorrectionType.Yes);
        textField.setReturnKeyType(UIReturnKeyType.Done);
        textField.setClearButtonMode(UITextFieldViewMode.Never);
    }

    private void configureTintedTextField() {
        tintedTextField.setTintColor(Colors.BLUE);
        tintedTextField.setTextColor(Colors.GREEN);

        tintedTextField.setPlaceholder("Placeholder text");
        tintedTextField.setReturnKeyType(UIReturnKeyType.Done);
        tintedTextField.setClearButtonMode(UITextFieldViewMode.Never);
    }

    private void configureSecureTextField() {
        secureTextField.setSecureTextEntry(true);

        secureTextField.setPlaceholder("Placeholder text");
        secureTextField.setReturnKeyType(UIReturnKeyType.Done);
        secureTextField.setClearButtonMode(UITextFieldViewMode.Always);
    }

    /**
     * There are many different types of keyboards that you may choose to use.
     * This example shows how to display a keyboard to help enter email
     * addresses.
     */
    private void configureSpecificKeyboardTextField() {
        specificKeyboardTextField.setKeyboardType(UIKeyboardType.EmailAddress);

        specificKeyboardTextField.setPlaceholder("Placeholder text");
        specificKeyboardTextField.setReturnKeyType(UIReturnKeyType.Done);
    }

    private void configureCustomTextField() {
        // Text fields with custom image backgrounds must have no border.
        customTextField.setBorderStyle(UITextBorderStyle.None);

        customTextField.setBackground(UIImage.create("text_field_background"));

        // Create a purple button that, when selected, turns the custom text
        // field's text color to purple.
        UIImage purpleImage = UIImage.create("text_field_purple_right_view");
        UIButton purpleImageButton = UIButton.create(UIButtonType.Custom);
        purpleImageButton.setBounds(new CGRect(0, 0, purpleImage.getSize().getWidth(), purpleImage.getSize()
                .getHeight()));
        purpleImageButton.setImageEdgeInsets(new UIEdgeInsets(0, 0, 0, 5));
        purpleImageButton.setImage(purpleImage, UIControlState.Normal);
        purpleImageButton.addOnTouchUpInsideListener(new UIControl.OnTouchUpInsideListener() {
            @Override
            public void onTouchUpInside(UIControl control, UIEvent event) {
                customTextField.setTextColor(Colors.PURPLE);

                System.out.println("The custom text field's purple right view button was clicked.");
            }
        });
        customTextField.setRightView(purpleImageButton);
        customTextField.setRightViewMode(UITextFieldViewMode.Always);

        // Add an empty view as the left view to ensure inset between the text
        // and the bounding rectangle.
        UIView leftPaddingView = new UIView(new CGRect(0, 0, 10, 0));
        leftPaddingView.setBackgroundColor(UIColor.clear());
        customTextField.setLeftView(leftPaddingView);
        customTextField.setLeftViewMode(UITextFieldViewMode.Always);

        customTextField.setPlaceholder("Placeholder text");
        customTextField.setAutocorrectionType(UITextAutocorrectionType.No);
        customTextField.setReturnKeyType(UIReturnKeyType.Done);
    }

    @Override
    public boolean shouldReturn(UITextField textField) {
        textField.resignFirstResponder();
        return true;
    }

    @Override
    public boolean shouldBeginEditing(UITextField textField) {
        return true;
    }

    @Override
    public void didBeginEditing(UITextField textField) {}

    @Override
    public boolean shouldEndEditing(UITextField textField) {
        return true;
    }

    @Override
    public void didEndEditing(UITextField textField) {}

    @Override
    public boolean shouldChangeCharacters(UITextField textField, NSRange range, String string) {
        return true;
    }

    @Override
    public boolean shouldClear(UITextField textField) {
        return true;
    }

    @IBOutlet
    private void setTextField(UITextField textField) {
        this.textField = textField;
    }

    @IBOutlet
    private void setTintedTextField(UITextField tintedTextField) {
        this.tintedTextField = tintedTextField;
    }

    @IBOutlet
    private void setSecureTextField(UITextField secureTextField) {
        this.secureTextField = secureTextField;
    }

    @IBOutlet
    private void setSpecificKeyboardTextField(UITextField specificKeyboardTextField) {
        this.specificKeyboardTextField = specificKeyboardTextField;
    }

    @IBOutlet
    private void setCustomTextField(UITextField customTextField) {
        this.customTextField = customTextField;
    }
}
