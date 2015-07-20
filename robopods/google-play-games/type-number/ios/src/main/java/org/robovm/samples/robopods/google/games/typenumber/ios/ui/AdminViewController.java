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
