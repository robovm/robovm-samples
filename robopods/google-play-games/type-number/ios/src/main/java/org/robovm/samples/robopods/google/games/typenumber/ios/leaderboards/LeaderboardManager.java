package org.robovm.samples.robopods.google.games.typenumber.ios.leaderboards;

import org.robovm.apple.foundation.NSError;
import org.robovm.pods.google.games.GPGScore;
import org.robovm.pods.google.games.GPGScoreReport;
import org.robovm.pods.google.games.GPGScoreReportCallback;
import org.robovm.samples.robopods.google.games.typenumber.ios.Log;
import org.robovm.samples.robopods.google.games.typenumber.ios.game.DifficultyLevel;
import org.robovm.samples.robopods.google.games.typenumber.ios.game.ScoreSubmitCallback;

public class LeaderboardManager {
    public static Leaderboard getLeaderboard(DifficultyLevel level) {
        return level == DifficultyLevel.Easy ? Leaderboard.Easy : Leaderboard.Hard;
    }

    public void playerFinishedGame(int score, DifficultyLevel level, final ScoreSubmitCallback callback) {
        Leaderboard leaderboard = getLeaderboard(level);

        final GPGScore submitMe = GPGScore.getScore(leaderboard.getId());
        submitMe.setValue(score);

        submitMe.submitScore(new GPGScoreReportCallback() {
            @Override
            public void done(GPGScoreReport report, NSError error) {
                if (error != null) {
                    Log.e("Received an error attempting to add to leaderboard %s: %s", submitMe, error);
                    callback.onError(error);
                } else {
                    if (report.isHighScoreForLocalPlayerToday()) {
                        Log.d("Woo hoo! Daily high score!");
                    }
                    callback.onSuccess(report);
                }
            }
        });
    }
}
