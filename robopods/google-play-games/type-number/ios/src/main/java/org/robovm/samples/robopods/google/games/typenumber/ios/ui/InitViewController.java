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

import java.util.Arrays;
import java.util.List;

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.uikit.UIActivityIndicatorView;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIInterfaceOrientation;
import org.robovm.apple.uikit.UIResponder;
import org.robovm.apple.uikit.UIStoryboardSegue;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import org.robovm.objc.annotation.IBOutlet;
import org.robovm.pods.google.games.GPGAchievement;
import org.robovm.pods.google.games.GPGAchievementResetAllCallback;
import org.robovm.pods.google.games.GPGLauncherController;
import org.robovm.pods.google.games.GPGLeaderboard;
import org.robovm.pods.google.games.GPGLeaderboardLoadScoresCallback;
import org.robovm.pods.google.games.GPGManager;
import org.robovm.pods.google.games.GPGScore;
import org.robovm.pods.google.games.GPGStatusDelegate;
import org.robovm.samples.robopods.google.games.typenumber.ios.Log;
import org.robovm.samples.robopods.google.games.typenumber.ios.TypeNumberApp;
import org.robovm.samples.robopods.google.games.typenumber.ios.game.DifficultyLevel;
import org.robovm.samples.robopods.google.games.typenumber.ios.game.GameModel;
import org.robovm.samples.robopods.google.games.typenumber.ios.leaderboards.Leaderboard;

@CustomClass("InitViewController")
public class InitViewController extends UIViewController implements GPGStatusDelegate {
    private UIButton achButton;
    private UIButton adminButton;
    private UIButton leadsButton;
    private UIButton easyButton;
    private UIButton hardButton;
    private UIButton signInButton;
    private UIButton signOutButton;
    private UIButton peopleListButton;
    private UIView gameIcons;
    private UIActivityIndicatorView signingIn;

    private DifficultyLevel desiredDifficulty;
    private NSDictionary<?, ?> incomingChallenge;

    private boolean currentlySigningIn;
    private GPGLeaderboard testLeaderboard;

    private void refreshInterface() {
        boolean signedIn = GPGManager.getSharedInstance().isSignedIn();

        // We update most of our game interface when game services sign-in is
        // totally complete. In an
        // actual game, you probably will want to allow basic gameplay even if
        // the user isn't signed
        // in to Google Play Games.
        List<UIButton> buttonsToManage = Arrays.asList(achButton, leadsButton, easyButton, hardButton,
                peopleListButton, adminButton);
        for (UIButton flipMe : buttonsToManage) {
            flipMe.setEnabled(signedIn);
            flipMe.setHidden(!signedIn);
        }

        signingIn.stopAnimating();
        gameIcons.setHidden(!signedIn);

        signInButton.setHidden(signedIn);
        signInButton.setEnabled(!signedIn);
        signOutButton.setHidden(!signedIn);
        signOutButton.setEnabled(signedIn);

        TypeNumberApp app = (TypeNumberApp) UIApplication.getSharedApplication().getDelegate();

        signInButton.setEnabled(true);
        signInButton.setAlpha(1.0);

        // This would also be a good time to jump directly into our game
        // if we got here from a deep link
        if (signedIn) {
            currentlySigningIn = false;
            NSDictionary<?, ?> deepLinkParams = app.getDeepLinkParams();
            if (deepLinkParams != null && deepLinkParams.containsKey("difficulty")) {
                // So we don't jump muliple times
                incomingChallenge = deepLinkParams;
                app.setDeepLinkParams(null);

                setDifficultyAndStartGame(DifficultyLevel.getByOrdinal(deepLinkParams.getInt("difficulty")));
            }
        } else if (currentlySigningIn) {
            // This catches the case where we're not signed in, but the service
            // is in the
            // process of signing us in.
            signInButton.setEnabled(false);
            signInButton.setAlpha(0.4);
            signingIn.startAnimating();
        }
    }

    @Override
    public void didFinishGamesSignIn(NSError error) {
        if (error != null) {
            Log.e("ERROR during sign in: %s", error.getLocalizedDescription());
        }
        refreshInterface();
        currentlySigningIn = false;
    }

    @Override
    public void didFinishGamesSignOut(NSError error) {
        if (error != null) {
            Log.e("ERROR during sign out: %s", error.getLocalizedDescription());
        }
        refreshInterface();
        currentlySigningIn = false;
    }

    @IBAction
    private void signInClicked(UIResponder sender) {
        GPGManager.getSharedInstance().signIn(GameModel.CLIENT_ID, false);
    }

    @IBAction
    private void signOutClicked(UIResponder sender) {
        GPGManager.getSharedInstance().signOut();
    }

    private void setDifficultyAndStartGame(DifficultyLevel level) {
        desiredDifficulty = level;
        performSegue("playGame", this);
    }

    @Override
    public void prepareForSegue(UIStoryboardSegue segue, NSObject sender) {
        if ("playGame".equals(segue.getIdentifier())) {
            ((GameViewController) segue.getDestinationViewController()).setDifficulty(desiredDifficulty);
            if (incomingChallenge != null) {
                ((GameViewController) segue.getDestinationViewController()).setIncomingChallenge(incomingChallenge);
                incomingChallenge = null;
            }
        }
    }

    @IBAction
    private void testButtonClicked(UIResponder sender) {
        testLeaderboard = GPGLeaderboard.getLeaderboard(Leaderboard.Easy.getId());
        testLeaderboard.loadScores(new GPGLeaderboardLoadScoresCallback() {
            @Override
            public void done(NSArray<GPGScore> scores, NSError error) {
                for (GPGScore nextScore : scores) {
                    Log.d("Player %s has a score of %s", nextScore.getPlayer().getDisplayName(),
                            nextScore.getFormattedScore());
                }
            }
        });
    }

    @IBAction
    private void testAdminButtonClicked(UIButton sender) {
        GPGAchievement.resetAll(new GPGAchievementResetAllCallback() {
            @Override
            public void done(NSError error) {
                if (error != null) {
                    Log.e("***ERROR resetting achievements: %s ***", error.getLocalizedDescription());
                } else {
                    Log.d("Done! Restart the app to view your new data");
                }
            }
        });
    }

    @IBAction
    private void peopleListButtonClicked(UIButton sender) {
        // Nothing to do here, really...
    }

    @IBAction
    private void easyButtonClicked(UIButton sender) {
        setDifficultyAndStartGame(DifficultyLevel.Easy);
    }

    @IBAction
    private void hardButtonClicked(UIButton sender) {
        setDifficultyAndStartGame(DifficultyLevel.Hard);
    }

    @IBAction
    private void showAchievements(UIButton sender) {
        GPGLauncherController.getSharedInstance().presentAchievementList();
    }

    @IBAction
    private void showAllLeaderboards(UIButton sender) {
        GPGLauncherController.getSharedInstance().presentLeaderboardList();
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);
        refreshInterface();
    }

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        GPGManager.getSharedInstance().setStatusDelegate(this);
        currentlySigningIn = GPGManager.getSharedInstance().signIn(GameModel.CLIENT_ID, true);

        TypeNumberApp app = (TypeNumberApp) UIApplication.getSharedApplication().getDelegate();
        app.setChallengeReceivedHandler(new Runnable() {
            @Override
            public void run() {
                refreshInterface();
            }
        });
    }

    @Override
    public boolean shouldAutorotate(UIInterfaceOrientation toInterfaceOrientation) {
        return toInterfaceOrientation == UIInterfaceOrientation.Portrait;
    }

    /* TODO remove when not needed */
    @IBOutlet
    private void setAchButton(UIButton achButton) {
        this.achButton = achButton;
    }

    @IBOutlet
    private void setAdminButton(UIButton adminButton) {
        this.adminButton = adminButton;
    }

    @IBOutlet
    private void setLeadsButton(UIButton leadsButton) {
        this.leadsButton = leadsButton;
    }

    @IBOutlet
    private void setEasyButton(UIButton easyButton) {
        this.easyButton = easyButton;
    }

    @IBOutlet
    private void setHardButton(UIButton hardButton) {
        this.hardButton = hardButton;
    }

    @IBOutlet
    private void setSignInButton(UIButton signInButton) {
        this.signInButton = signInButton;
    }

    @IBOutlet
    private void setSignOutButton(UIButton signOutButton) {
        this.signOutButton = signOutButton;
    }

    @IBOutlet
    private void setPeopleListButton(UIButton peopleListButton) {
        this.peopleListButton = peopleListButton;
    }

    @IBOutlet
    private void setGameIcons(UIView gameIcons) {
        this.gameIcons = gameIcons;
    }

    @IBOutlet
    private void setSigningIn(UIActivityIndicatorView signingIn) {
        this.signingIn = signingIn;
    }

    @Override
    public void didFinishGoogleAuth(NSError error) {}

    @Override
    public boolean shouldReauthenticate(NSError error) {
        return false;
    }

    @Override
    public void willReauthenticate(NSError error) {}

    @Override
    public void didDisconnect(NSError error) {}
}
