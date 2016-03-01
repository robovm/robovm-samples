package org.robovm.samples.robopods.settings;

import org.robovm.apple.uikit.*;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import org.robovm.objc.annotation.IBOutlet;

@CustomClass("MyViewController")
public class MyViewController extends UITableViewController {
    @IBOutlet
    private UISlider soundVolume;
    @IBOutlet
    private UISlider musicVolume;
    @IBOutlet
    private UIPickerView language;
    @IBOutlet
    private UIButton clickButton;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        AppPreferences prefs = AppPreferences.getInstance();

        soundVolume.setValue((float) prefs.soundVolume);
        musicVolume.setValue((float) prefs.musicVolume);

        language.setModel(new UIPickerViewModel() {
            @Override
            public long getNumberOfRows(UIPickerView pickerView, long component) {
                return Language.values().length;
            }

            @Override
            public long getNumberOfComponents(UIPickerView pickerView) {
                return 1;
            }

            @Override
            public String getRowTitle(UIPickerView pickerView, long row, long component) {
                return Language.values()[(int) row].name();
            }

            @Override
            public void didSelectRow(UIPickerView pickerView, long row, long component) {
                prefs.language = Language.values()[(int) row];
                prefs.save();
            }
        });
        language.selectRow(prefs.language.ordinal(), 0, false);

        if (prefs.buttonClickCount > 0) {
            clickButton.setTitle(String.format("%d Clicks!", prefs.buttonClickCount), UIControlState.Normal);
        }
    }

    @IBAction
    private void soundVolumeChanged() {
        // *NOTE* In Interface Builder untick "Continuous Updates" to be only notified on the last slider value.
        AppPreferences prefs = AppPreferences.getInstance();
        prefs.soundVolume = soundVolume.getValue();
        prefs.save();
    }

    @IBAction
    private void musicVolumeChanged() {
        AppPreferences prefs = AppPreferences.getInstance();
        prefs.musicVolume = musicVolume.getValue();
        prefs.save();
    }

    @IBAction
    private void buttonClicked() {
        AppPreferences prefs = AppPreferences.getInstance();
        clickButton.setTitle(String.format("%d Clicks!", ++prefs.buttonClickCount), UIControlState.Normal);
        prefs.save();
    }
}
