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
 * Portions of this code is based on Apple Inc's BatteryStatus sample (v1.2)
 * which is copyright (C) 2009-2013 Apple Inc.
 */
package org.robovm.samples.batterystatus.ui;

import org.robovm.apple.foundation.NSNumber;
import org.robovm.apple.foundation.NSNumberFormatter;
import org.robovm.apple.foundation.NSNumberFormatterStyle;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.UIDevice;
import org.robovm.apple.uikit.UIDeviceBatteryState;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UISwitch;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellAccessoryType;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import org.robovm.objc.annotation.IBOutlet;

@CustomClass("BatStatViewController")
public class BatStatViewController extends UITableViewController {
    private UISwitch monitorSwitch;
    private UILabel levelLabel;
    private UITableViewCell unknownCell;
    private UITableViewCell unpluggedCell;
    private UITableViewCell chargingCell;
    private UITableViewCell fullCell;

    private void updateBatteryLevel() {
        float batteryLevel = UIDevice.getCurrentDevice().getBatteryLevel();
        if (batteryLevel < 0) {
            // -1.0 means battery state is UIDeviceBatteryState.Unknown
            levelLabel.setText(NSString.getLocalizedString("Unknown"));
        } else {
            NSNumberFormatter numberFormatter = null;
            if (numberFormatter == null) {
                numberFormatter = new NSNumberFormatter();
                numberFormatter.setNumberStyle(NSNumberFormatterStyle.Percent);
                numberFormatter.setMaximumFractionDigits(1);
            }

            NSNumber levelObj = NSNumber.valueOf(batteryLevel);
            levelLabel.setText(numberFormatter.format(levelObj));
        }
    }

    private void updateBatteryState() {
        UITableViewCell[] batteryStateCells = new UITableViewCell[] { unknownCell, unpluggedCell, chargingCell,
            fullCell };

        UIDeviceBatteryState currentState = UIDevice.getCurrentDevice().getBatteryState();

        for (int i = 0; i < batteryStateCells.length; i++) {
            UITableViewCell cell = batteryStateCells[i];

            if (i + UIDeviceBatteryState.Unknown.value() == currentState.value()) {
                cell.setAccessoryType(UITableViewCellAccessoryType.Checkmark);
            } else {
                cell.setAccessoryType(UITableViewCellAccessoryType.None);
            }
        }
    }

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        // Register for battery level and state change notifications.
        UIDevice.Notifications.observeBatteryLevelDidChange(new Runnable() {
            @Override
            public void run() {
                updateBatteryLevel();
            }
        });
        UIDevice.Notifications.observeBatteryStateDidChange(new Runnable() {
            @Override
            public void run() {
                updateBatteryLevel();
                updateBatteryState();
            }
        });
    }

    @Override
    public void viewDidLayoutSubviews() {
        // Enable battery monitoring.
        UIDevice.getCurrentDevice().setBatteryMonitoringEnabled(true);
        updateBatteryLevel();
        updateBatteryState();
    }

    @IBAction
    private void switchAction(UISwitch sender) {
        UIDevice.getCurrentDevice().setBatteryMonitoringEnabled(sender.isOn());
        updateBatteryLevel();
        updateBatteryState();
    }

    @IBOutlet
    private void setMonitorSwitch(UISwitch monitorSwitch) {
        this.monitorSwitch = monitorSwitch;
    }

    @IBOutlet
    private void setLevelLabel(UILabel levelLabel) {
        this.levelLabel = levelLabel;
    }

    @IBOutlet
    private void setUnknownCell(UITableViewCell unknownCell) {
        this.unknownCell = unknownCell;
    }

    @IBOutlet
    private void setUnpluggedCell(UITableViewCell unpluggedCell) {
        this.unpluggedCell = unpluggedCell;
    }

    @IBOutlet
    private void setChargingCell(UITableViewCell chargingCell) {
        this.chargingCell = chargingCell;
    }

    @IBOutlet
    private void setFullCell(UITableViewCell fullCell) {
        this.fullCell = fullCell;
    }
}
