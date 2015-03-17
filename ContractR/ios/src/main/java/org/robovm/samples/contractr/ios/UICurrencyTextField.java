/*
 * Copyright (C) 2014 RoboVM AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robovm.samples.contractr.ios;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import org.robovm.apple.foundation.NSRange;
import org.robovm.apple.uikit.UIControlEvents;
import org.robovm.apple.uikit.UITextField;
import org.robovm.apple.uikit.UITextFieldDelegateAdapter;
import org.robovm.apple.uikit.UITextPosition;

/**
 * 
 */
public class UICurrencyTextField extends UITextField {

    private final NumberFormat formatter;
    
    public UICurrencyTextField() {
        formatter = NumberFormat.getCurrencyInstance(Locale.US);
        
        setDelegate(new UITextFieldDelegateAdapter() {
            @Override
            public boolean shouldChangeCharacters(UITextField textField,
                    NSRange range, String string) {

                if (string.isEmpty()
                        && range.getLength() == 1
                        && !Character.isDigit(textField.getText().charAt(
                                (int) range.getLocation()))) {

                    setCaretPosition(textField, (int) range.getLocation());
                    return false;
                }
                
                String oldText = textField.getText();
                String newText = oldText.substring(0, (int) range.getLocation())
                        + string + oldText.substring((int) (range.getLocation() + range.getLength()));
                textField.setText(newText);

                int distanceFromEnd = oldText.length() - (int) (range.getLocation() + range.getLength());
                int caretPos = newText.length() - distanceFromEnd;
                if (caretPos >= 0 && caretPos <= newText.length()) {
                    setCaretPosition(textField, caretPos);
                }
                
                textField.sendControlEventsActions(UIControlEvents.EditingChanged);
                
                return false;
            }
        });
    }

    private BigDecimal parseAmount(String s) {
        s = s.replaceAll("\\D", "");
        if (s.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(s).scaleByPowerOfTen(-formatter.getMinimumFractionDigits());
    }
    
    @Override
    public void setText(String v) {
        BigDecimal amount = parseAmount(v);
        super.setText(formatter.format(amount));
    }
    
    private static void setCaretPosition(UITextField tf, int position) {
        UITextPosition start = tf.getPosition(tf.getBeginningOfDocument(), position);
        UITextPosition end = tf.getPosition(start, 0);
        tf.setSelectedTextRange(tf.getTextRange(start, end));
    }
    
    public BigDecimal getAmount() {
        return parseAmount(getText());
    }

    public void setAmount(BigDecimal amount) {
        setText(formatter.format(amount));
    }
    
}
