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
