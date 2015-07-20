package org.robovm.samples.robopods.google.games.typenumber.ios.game;

import org.robovm.apple.foundation.NSError;
import org.robovm.pods.google.games.GPGScoreReport;

public interface ScoreSubmitCallback {
    void onSuccess(GPGScoreReport report);

    void onError(NSError error);
}
