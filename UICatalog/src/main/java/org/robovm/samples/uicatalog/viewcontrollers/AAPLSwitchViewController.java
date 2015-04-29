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

import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UISwitch;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;
import org.robovm.samples.uicatalog.Colors;

@CustomClass("AAPLSwitchViewController")
public class AAPLSwitchViewController extends UITableViewController implements UIControl.OnValueChangedListener {
    private UISwitch defaultSwitch;
    private UISwitch tintedSwitch;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        configureDefaultSwitch();
        configureTintedSwitch();
    }

    private void configureDefaultSwitch() {
        defaultSwitch.setOn(true, true);
        defaultSwitch.addOnValueChangedListener(this);
    }

    private void configureTintedSwitch() {
        tintedSwitch.setTintColor(Colors.BLUE);
        tintedSwitch.setOnTintColor(Colors.GREEN);
        tintedSwitch.setThumbTintColor(Colors.PURPLE);

        tintedSwitch.addOnValueChangedListener(this);
    }

    @Override
    public void onValueChanged(UIControl control) {
        System.out.println(String.format("A switch changed its value: %s.", control));
    }

    @IBOutlet
    private void setDefaultSwitch(UISwitch defaultSwitch) {
        this.defaultSwitch = defaultSwitch;
    }

    @IBOutlet
    private void setTintedSwitch(UISwitch tintedSwitch) {
        this.tintedSwitch = tintedSwitch;
    }
}
