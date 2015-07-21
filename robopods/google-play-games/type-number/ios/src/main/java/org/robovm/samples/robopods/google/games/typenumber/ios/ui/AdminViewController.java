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
 * Portions of this code is based on Google Inc's Google Play Games 'Type a Number' sample
 * which is copyright (C) 2015 Google Inc.
 */
package org.robovm.samples.robopods.google.games.typenumber.ios.ui;

import org.robovm.apple.foundation.NSError;
import org.robovm.apple.uikit.UIAlertView;
import org.robovm.apple.uikit.UIResponder;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import org.robovm.pods.google.games.GPGAchievement;
import org.robovm.pods.google.games.GPGAchievementResetAllCallback;
import org.robovm.pods.google.games.GPGLeaderboard;
import org.robovm.pods.google.games.GPGLeaderboardScoreResetCallback;
import org.robovm.samples.robopods.google.games.typenumber.ios.leaderboards.Leaderboard;

@CustomClass("AdminViewController")
public class AdminViewController extends UIViewController {

    private void showActionCompleteAlert(NSError error, String gerund) {
        String alertMessage, alertTitle;
        if (error != null) {
            alertMessage = String.format("Error %s: %s", gerund, error.getLocalizedDescription());
            alertTitle = "Error";
        } else {
            alertMessage = String.format(
                    "All done %s! You may need to restart your application to see the changes take effect.", gerund);
            alertTitle = "Done!";
        }

        new UIAlertView(alertTitle, alertMessage, null, "Okay").show();
    }

    @IBAction
    private void resetAllAchievements(UIResponder sender) {
        GPGAchievement.resetAll(new GPGAchievementResetAllCallback() {
            @Override
            public void done(NSError error) {
                showActionCompleteAlert(error, "resetting achievements");
            }
        });
    }

    @IBAction
    private void resetEasyLeaderboard(UIResponder sender) {
        GPGLeaderboard easyLeaderboard = GPGLeaderboard.getLeaderboard(Leaderboard.Easy.getId());
        easyLeaderboard.resetScore(new GPGLeaderboardScoreResetCallback() {
            @Override
            public void done(NSError error) {
                showActionCompleteAlert(error, "resetting the Easy leaderboard");
            }
        });
    }

    @IBAction
    private void resetHardLeaderboard(UIResponder sender) {
        GPGLeaderboard hardLeaderboard = GPGLeaderboard.getLeaderboard(Leaderboard.Hard.getId());
        hardLeaderboard.resetScore(new GPGLeaderboardScoreResetCallback() {
            @Override
            public void done(NSError error) {
                showActionCompleteAlert(error, "resetting the Hard leaderboard");
            }
        });
    }
}
