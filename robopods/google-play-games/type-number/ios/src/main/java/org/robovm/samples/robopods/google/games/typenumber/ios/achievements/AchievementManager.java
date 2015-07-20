package org.robovm.samples.robopods.google.games.typenumber.ios.achievements;

import org.robovm.apple.foundation.NSError;
import org.robovm.pods.google.games.GPGAchievement;
import org.robovm.pods.google.games.GPGAchievementIncrementStepsCallback;
import org.robovm.pods.google.games.GPGAchievementUnlockCallback;
import org.robovm.samples.robopods.google.games.typenumber.ios.Log;
import org.robovm.samples.robopods.google.games.typenumber.ios.game.DifficultyLevel;

public class AchievementManager {

    private void unlockAchievement(Achievement achievement) {
        final GPGAchievement unlockMe = new GPGAchievement(achievement.getId());

        unlockMe.unlock(new GPGAchievementUnlockCallback() {
            @Override
            public void done(boolean newlyUnlocked, NSError error) {
                if (error != null) {
                    Log.e("Received an error attempting to unlock an achievement %s: %s", unlockMe, error);
                }
            }
        });
    }

    private void makeIncrementalProgress(Achievement achievement, int progressAmount) {
        Log.d("Your progress amount is %d", progressAmount);

        final GPGAchievement incrementMe = new GPGAchievement(achievement.getId());

        incrementMe.incrementSteps(progressAmount, new GPGAchievementIncrementStepsCallback() {
            @Override
            public void done(boolean newlyUnlocked, int currentSteps, NSError error) {
                if (error != null) {
                    Log.e("Received an error attempting to increment achievement %s: %s", incrementMe, error);
                } else if (newlyUnlocked) {
                    Log.d("Incremental achievement unlocked!");
                } else {
                    Log.d("You've completed %d steps total", currentSteps);
                }
            }
        });
    }

    public void playerRequestedScore(int score, DifficultyLevel level) {
        if (score == 0) {
            unlockAchievement(Achievement.Humble);
        } else if (score == 9999) {
            unlockAchievement(Achievement.Cocky);
        }
    }

    private boolean isPrime(int checkMe) {
        if (checkMe == 1)
            return false;

        int checkMax = (int) Math.floor(Math.sqrt(checkMe));
        for (int i = 2; i <= checkMax; i++) {
            if (checkMe % i == 0)
                return false;
        }
        return true;
    }

    public void playerFinishedGame(int score, DifficultyLevel level) {
        if (score == 1337) {
            unlockAchievement(Achievement.Leet);
        } else if (isPrime(score)) {
            unlockAchievement(Achievement.Prime);
        }
        makeIncrementalProgress(Achievement.Bored, 1);
        makeIncrementalProgress(Achievement.ReallyBored, 1);
    }
}
