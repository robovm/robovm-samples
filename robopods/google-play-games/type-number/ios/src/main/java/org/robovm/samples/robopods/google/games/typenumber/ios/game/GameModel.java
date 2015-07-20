package org.robovm.samples.robopods.google.games.typenumber.ios.game;

import org.robovm.samples.robopods.google.games.typenumber.ios.Log;
import org.robovm.samples.robopods.google.games.typenumber.ios.achievements.AchievementManager;
import org.robovm.samples.robopods.google.games.typenumber.ios.leaderboards.LeaderboardManager;

public class GameModel {
    public static final String CLIENT_ID = "513822948574-c2hta714kt6pimpo42brn2tdf9m7n3n7.apps.googleusercontent.com";

    private final AchievementManager achievementManager;
    private final LeaderboardManager leaderboardManager;

    private int gameScore;

    public GameModel() {
        achievementManager = new AchievementManager();
        leaderboardManager = new LeaderboardManager();
    }

    public int requestScore(int score, DifficultyLevel level) {
        // Manage any achievements that hinge on _requesting_ a score
        achievementManager.playerRequestedScore(score, level);

        // Return the score based on the difficulty level
        if (level == DifficultyLevel.Easy) {
            gameScore = score;
        } else {
            gameScore = (int) Math.ceil(score / 2.0);
        }
        return gameScore;
    }

    public void gameOver(int score, DifficultyLevel level, ScoreSubmitCallback callback) {
        // Manage any achievements that hinge on _receiving_ a score
        achievementManager.playerFinishedGame(score, level);

        // And submit your final score to leaderboards
        Log.d("Trying to submit to leaderboard...");
        leaderboardManager.playerFinishedGame(score, level, callback);
    }

    public int getGameScore() {
        return gameScore;
    }
}
