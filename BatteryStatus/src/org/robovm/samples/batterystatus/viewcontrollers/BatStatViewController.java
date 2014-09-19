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
 * Portions of this code is based on Apple Inc's BatteryStatus sample (v1.2)
 * which is copyright (C) 2009-2013 Apple Inc.
 */

package org.robovm.samples.batterystatus.viewcontrollers;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSNumber;
import org.robovm.apple.foundation.NSNumberFormatter;
import org.robovm.apple.foundation.NSNumberFormatterStyle;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.NSIndexPathExtensions;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIDevice;
import org.robovm.apple.uikit.UIDeviceBatteryState;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UISwitch;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellAccessoryType;
import org.robovm.apple.uikit.UITableViewCellSeparatorStyle;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.apple.uikit.UITableViewDataSourceAdapter;
import org.robovm.apple.uikit.UITableViewStyle;
import org.robovm.apple.uikit.UIView;

public class BatStatViewController extends UITableViewController {
    private UISwitch monitorSwitch;
    private final UIControl.OnValueChangedListener switchAction;

    private UILabel levelLabel;
    private UITableViewCell unknownCell;
    private UITableViewCell unpluggedCell;
    private UITableViewCell chargingCell;
    private UITableViewCell fullCell;

    public BatStatViewController () {
        super();

        getNavigationItem().setTitle("Battery Status");

        UITableView tableView = new UITableView(new CGRect(0, 0, 320, 568), UITableViewStyle.Grouped);
        tableView.setAlwaysBounceVertical(true);
        tableView.setSeparatorStyle(UITableViewCellSeparatorStyle.SingleLineEtched);
        tableView.setRowHeight(44);
        tableView.setSectionHeaderHeight(20);
        tableView.setSectionFooterHeight(10);
        tableView.setBackgroundColor(UIColor.groupTableViewBackground());
        tableView.setDataSource(new UITableViewDataSourceAdapter() {
            @Override
            public long getNumberOfSections (UITableView tableView) {
                return 3;
            }

            @Override
            public long getNumberOfRowsInSection (UITableView tableView, long section) {
                switch ((int)section) {
                case 0:
                case 1:
                    return 1;
                default:
                    return 4;
                }
            }

            @Override
            public UITableViewCell getRowCell (UITableView tableView, NSIndexPath indexPath) {
                UIView contentView;

                switch ((int)NSIndexPathExtensions.getSection(indexPath)) {
                case 0:
                    UITableViewCell switchCell = new UITableViewCell(new CGRect(0, 99, 320, 44));
                    contentView = switchCell.getContentView();

                    UILabel monitoringLabel = new UILabel(new CGRect(20, 11, 83, 21));
                    monitoringLabel.setText("Monitoring");
                    monitoringLabel.setFont(UIFont.getSystemFont(17));
                    monitoringLabel.setTextColor(UIColor.darkText());
                    contentView.addSubview(monitoringLabel);

                    monitorSwitch = new UISwitch(new CGRect(251, 6, 51, 31));
                    monitorSwitch.setOn(true);
                    monitorSwitch.addOnValueChangedListener(switchAction);
                    contentView.addSubview(monitorSwitch);

                    return switchCell;
                case 1:
                    UITableViewCell levelCell = new UITableViewCell(new CGRect(0, 173, 320, 44));
                    contentView = levelCell.getContentView();

                    UILabel levelCaptionLabel = new UILabel(new CGRect(20, 11, 42, 21));
                    levelCaptionLabel.setText("Level");
                    levelCaptionLabel.setFont(UIFont.getSystemFont(17));
                    levelCaptionLabel.setTextColor(UIColor.darkText());
                    contentView.addSubview(levelCaptionLabel);

                    levelLabel = new UILabel(new CGRect(220, 11, 80, 21));
                    levelLabel.setFont(UIFont.getSystemFont(17));
                    levelLabel.setTextColor(UIColor.darkText());
                    contentView.addSubview(levelLabel);

                    return levelCell;
                default:
                    switch ((int)NSIndexPathExtensions.getRow(indexPath)) {
                    case 0:
                        unknownCell = new UITableViewCell(new CGRect(0, 265, 320, 44));
                        contentView = unknownCell.getContentView();

                        UILabel unknownLabel = new UILabel(new CGRect(20, 11, 80, 21));
                        unknownLabel.setText("Unknown");
                        unknownLabel.setFont(UIFont.getSystemFont(17));
                        contentView.addSubview(unknownLabel);

                        return unknownCell;
                    case 1:
                        unpluggedCell = new UITableViewCell(new CGRect(0, 309, 320, 44));
                        contentView = unpluggedCell.getContentView();

                        UILabel unpluggedLabel = new UILabel(new CGRect(20, 11, 90, 21));
                        unpluggedLabel.setText("Unplugged");
                        unpluggedLabel.setFont(UIFont.getSystemFont(17));
                        contentView.addSubview(unpluggedLabel);

                        return unpluggedCell;
                    case 2:
                        chargingCell = new UITableViewCell(new CGRect(0, 353, 320, 44));
                        contentView = chargingCell.getContentView();

                        UILabel chargingLabel = new UILabel(new CGRect(20, 11, 74, 21));
                        chargingLabel.setText("Charging");
                        chargingLabel.setFont(UIFont.getSystemFont(17));
                        contentView.addSubview(chargingLabel);

                        return chargingCell;
                    default:
                        fullCell = new UITableViewCell(new CGRect(0, 397, 320, 44));
                        contentView = fullCell.getContentView();

                        UILabel fullLabel = new UILabel(new CGRect(20, 11, 42, 21));
                        fullLabel.setText("Full");
                        fullLabel.setFont(UIFont.getSystemFont(17));
                        contentView.addSubview(fullLabel);

                        return fullCell;
                    }
                }
            }

            @Override
            public String getSectionHeaderTitle (UITableView tableView, long section) {
                switch ((int)section) {
                case 2:
                    return "Battery State";
                default:
                    return null;
                }
            }
        });

        switchAction = new UIControl.OnValueChangedListener() {
            @Override
            public void onValueChanged (UIControl control) {
                if (((UISwitch)control).isOn()) {
                    UIDevice.getCurrentDevice().setBatteryMonitoringEnabled(true);
                    // The UI will be updated as a result of the first UIDeviceBatteryStateDidChangeNotification notification.
                    // Note that enabling monitoring only triggers a UIDeviceBatteryStateDidChangeNotification;
                    // a UIDeviceBatteryLevelDidChangeNotification is not sent.
                } else {
                    UIDevice.getCurrentDevice().setBatteryMonitoringEnabled(false);

                    updateBatteryLevel();
                    updateBatteryState();
                }
            }
        };
        setTableView(tableView);
    }

    private void updateBatteryLevel () {
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
            levelLabel.setText(numberFormatter.stringFromNumber$(levelObj));
        }
    }

    private void updateBatteryState () {
        UITableViewCell[] batteryStateCells = new UITableViewCell[] {unknownCell, unpluggedCell, chargingCell, fullCell};

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
    public void viewDidLoad () {
        super.viewDidLoad();

        // Register for battery level and state change notifications.
        UIDevice.Notifications.observeBatteryLevelDidChange(new Runnable() {
            @Override
            public void run () {
                updateBatteryLevel();
            }
        });
        UIDevice.Notifications.observeBatteryStateDidChange(new Runnable() {
            @Override
            public void run () {
                updateBatteryLevel();
                updateBatteryState();
            }
        });
    }

    @Override
    public void viewDidLayoutSubviews () {
        super.viewDidLayoutSubviews();
        switchAction.onValueChanged(monitorSwitch);
    }
}
