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

import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControl.OnEditingChangedListener;
import org.robovm.apple.uikit.UITextField;
import org.robovm.samples.contractr.core.Client;

/**
 * 
 */
public abstract class AbstractClientViewController extends AbstractSettingsViewController {

    private UITextField nameTextField;
    private UICurrencyTextField hourlyRateTextField;
    
    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        nameTextField = new UITextField();
        nameTextField.setPlaceholder("Enter client name");
        nameTextField.setTextAlignment(NSTextAlignment.Right);
        nameTextField.addOnEditingChangedListener(new OnEditingChangedListener() {
            public void onEditingChanged(UIControl control) {
                updateSaveButtonEnabled();
            }
        });
        hourlyRateTextField = new UICurrencyTextField();
        hourlyRateTextField.setTextAlignment(NSTextAlignment.Right);

        setCells(
            cell("Name", nameTextField),
            cell("Hourly rate", hourlyRateTextField)
        );
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);
        updateViewValuesWithClient(null);
    }

    @Override
    public void viewWillDisappear(boolean animated) {
        nameTextField.resignFirstResponder();
        hourlyRateTextField.resignFirstResponder();
        
        super.viewWillDisappear(animated);
    }
    
    protected void onSave() {
        getNavigationController().popViewController(true);
    }
    
    protected void updateSaveButtonEnabled() {
        String name = nameTextField.getText();
        name = name == null ? "" : name.trim();
        getNavigationItem().getRightBarButtonItem().setEnabled(!name.isEmpty());
    }

    protected void updateViewValuesWithClient(Client client) {
        nameTextField.setText(client == null ? "" : client.getName());
        hourlyRateTextField.setAmount(client == null ? BigDecimal.ZERO : client.getHourlyRate());
        updateSaveButtonEnabled();
    }
    
    protected Client saveViewValuesToClient(Client client) {
        String name = nameTextField.getText();
        name = name == null ? "" : name.trim();

        client.setName(name);
        client.setHourlyRate(hourlyRateTextField.getAmount());
        
        return client;
    }
}
