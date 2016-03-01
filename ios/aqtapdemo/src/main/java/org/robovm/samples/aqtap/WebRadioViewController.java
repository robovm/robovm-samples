/*
 * Copyright (C) 2013-2015 RoboVM AB
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
package org.robovm.samples.aqtap;

import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UISlider;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import org.robovm.objc.annotation.IBOutlet;

@CustomClass("WebRadioViewController")
public class WebRadioViewController extends UIViewController {
    private static final String RADIO_STATION_URL = "http://www.live365.com/play/csa5mojh2";

    private WebRadioPlayer player;

    private UILabel radioStationLabel;
    private UILabel pitchLabel;
    private UISlider pitchSlider;

    @Override
    public void viewDidAppear(boolean animated) {
        super.viewDidAppear(animated);

        radioStationLabel.setText(RADIO_STATION_URL);
        player = new WebRadioPlayer(new NSURL(RADIO_STATION_URL));
        player.start();
    }

    @IBAction
    private void pitchValueChanged() {
        updatePitch();
    }

    @IBAction
    private void resetPitch() {
        pitchSlider.setValue(1.0f);
        updatePitch();
    }

    private void updatePitch() {
        float pitch = pitchSlider.getValue();
        player.setPitch(pitch);
        pitchLabel.setText(String.format("%.3f", pitch));
    }

    @IBOutlet
    public void setRadioStationLabel(UILabel radioStationLabel) {
        this.radioStationLabel = radioStationLabel;
    }

    @IBOutlet
    public void setPitchLabel(UILabel pitchLabel) {
        this.pitchLabel = pitchLabel;
    }

    @IBOutlet
    public void setPitchSlider(UISlider pitchSlider) {
        this.pitchSlider = pitchSlider;
    }
}
