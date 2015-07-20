package org.robovm.samples.robopods.google.games.typenumber.ios.ui;

import java.io.UnsupportedEncodingException;

import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSErrorException;
import org.robovm.apple.foundation.NSJSONSerialization;
import org.robovm.apple.foundation.NSJSONWritingOptions;
import org.robovm.apple.foundation.NSMutableDictionary;
import org.robovm.apple.foundation.NSNumberFormatter;
import org.robovm.apple.foundation.NSRange;
import org.robovm.apple.foundation.NSSet;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.foundation.NSStringEncoding;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.uikit.UIActivityIndicatorView;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIInterfaceOrientation;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIResponder;
import org.robovm.apple.uikit.UITextField;
import org.robovm.apple.uikit.UITextFieldDelegateAdapter;
import org.robovm.apple.uikit.UITouch;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import org.robovm.objc.annotation.IBOutlet;
import org.robovm.pods.google.games.GPGLauncherController;
import org.robovm.pods.google.games.GPGPlayer;
import org.robovm.pods.google.games.GPGPlayerGetCallback;
import org.robovm.pods.google.games.GPGScoreReport;
import org.robovm.pods.google.opensource.GTLBase64;
import org.robovm.pods.google.plus.GPPShare;
import org.robovm.pods.google.plus.GPPShareBuilder;
import org.robovm.pods.google.plus.GPPShareDelegateAdapter;
import org.robovm.samples.robopods.google.games.typenumber.ios.Log;
import org.robovm.samples.robopods.google.games.typenumber.ios.game.DifficultyLevel;
import org.robovm.samples.robopods.google.games.typenumber.ios.game.GameModel;
import org.robovm.samples.robopods.google.games.typenumber.ios.game.ScoreSubmitCallback;
import org.robovm.samples.robopods.google.games.typenumber.ios.leaderboards.Leaderboard;
import org.robovm.samples.robopods.google.games.typenumber.ios.leaderboards.LeaderboardManager;

@CustomClass("GameViewController")
public class GameViewController extends UIViewController {
    private static final String WEB_GAME_URL = "https://www.example.com/typeanumber/index.html";

    private UILabel titleLabel;
    private UILabel playerMessage;
    private UILabel finalScoreLabel;
    private UITextField scoreRequestTextField;
    private UIButton bigActionButton;
    private UIButton seeHighScoresButton;
    private UIButton bragButton;
    private UILabel highScoreLabel;
    private UILabel incomingChallengeLabel;
    private UIActivityIndicatorView waitingForHighScore;

    private boolean gameOver;
    private GameModel gameModel;

    private DifficultyLevel difficulty;
    private NSDictionary<?, ?> incomingChallenge;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        gameModel = new GameModel();

        gameOver = false;
        scoreRequestTextField.setDelegate(new UITextFieldDelegateAdapter() {
            // Code to limit our text field to 4 characters.
            @Override
            public boolean shouldChangeCharacters(UITextField textField, NSRange range, String string) {
                final int maxLength = 4;

                int oldLength = textField.getText().length();
                int replacementLength = string.length();
                long rangeLength = range.getLength();
                long newLength = oldLength - rangeLength + replacementLength;

                boolean returnKey = string.indexOf('\n') != -1;

                return newLength <= maxLength || returnKey;
            }
        });
    }

    private void reportOnHighScore(GPGScoreReport scoreReport) {
        waitingForHighScore.stopAnimating();
        if (scoreReport.isHighScoreForLocalPlayerThisWeek() && gameOver) {
            highScoreLabel.setText("New high score for this week!");
            highScoreLabel.setHidden(false);
            bragButton.setHidden(false);
        } else {
            highScoreLabel.setText(String.format("This weeks high score: %s", scoreReport
                    .getHighScoreForLocalPlayerThisWeek().getFormattedScore()));
            highScoreLabel.setHidden(false);
            bragButton.setHidden(true);
        }
    }

    private void presentGameOver(int finalScore) {
        if (gameOver) {
            finalScoreLabel.setText(String.format("%d", finalScore));
            finalScoreLabel.setHidden(false);
            scoreRequestTextField.setHidden(true);
            if (difficulty == DifficultyLevel.Easy) {
                playerMessage.setText("Good choice! Your final score is...");
            } else {
                playerMessage.setText("What, you thought it would be that easy? Your final score is...");
            }
            if (incomingChallenge != null) {
                if (finalScore > incomingChallenge.getInt("scoreToBeat")) {
                    incomingChallengeLabel.setText("Challenge beaten! Good work!");
                } else {
                    incomingChallengeLabel.setText("Challenge not beaten.");
                }
            }

            bigActionButton.setTitle("New Game", UIControlState.Normal);
            waitingForHighScore.startAnimating();
            gameModel.gameOver(finalScore, difficulty, new ScoreSubmitCallback() {
                @Override
                public void onSuccess(GPGScoreReport report) {
                    reportOnHighScore(report);
                }

                @Override
                public void onError(NSError error) {}
            });
            seeHighScoresButton.setHidden(false);
        }
    }

    private void presentNewGame() {
        // Fix the title
        titleLabel.setText(difficulty == DifficultyLevel.Easy ? "Type-a-Number: Easy" : "Type-a-Number: Hard");

        if (!gameOver) {
            highScoreLabel.setHidden(true);
            finalScoreLabel.setHidden(true);
            bragButton.setHidden(true);
            scoreRequestTextField.setHidden(false);
            playerMessage.setText("What score do you think you deserve?");
            bigActionButton.setTitle("Request", UIControlState.Normal);
            seeHighScoresButton.setHidden(false);

            if (incomingChallenge == null) {
                incomingChallengeLabel.setHidden(true);
            } else {
                incomingChallengeLabel.setText(String.format("Incoming challenge! Beat %s's score of %d",
                        incomingChallenge.getString("challenger"), incomingChallenge.getInt("scoreToBeat")));
                incomingChallengeLabel.setHidden(false);
            }
        }
    }

    @IBAction
    private void bigButtonClicked(UIResponder sender) {
        if (!gameOver) {
            NSNumberFormatter nf = new NSNumberFormatter();
            int userScore = nf.parse(scoreRequestTextField.getText()).intValue();

            // Just to be sure
            userScore = Math.min(Math.max(0, userScore), 9999);

            int finalScore = gameModel.requestScore(userScore, difficulty);
            gameOver = true;
            presentGameOver(finalScore);
        } else {
            gameOver = false;
            presentNewGame();
        }
    }

    @IBAction
    private void bragButtonClicked(UIResponder sender) {
        // Let's initiate a share object.
        String difficultyString = difficulty == DifficultyLevel.Easy ? "Easy" : "Hard";
        final String prefillText = String.format(
                "I just got a score of %d on the %s level of the Type-a-Number Challenge. Can you beat it?",
                gameModel.getGameScore(), difficultyString);

        // All of your data is going to be passed in through a URL that look
        // something like
        // com.example.mygame://google/link/?deep_link_id=xxxxx&gplus_source=stream
        // There are lots of ways to encode this. I'm going to go for
        // url-encoded JSON, which is
        // pretty well established and has nice library support.
        GPGPlayer.getLocalPlayer(new GPGPlayerGetCallback() {
            @Override
            public void done(GPGPlayer player, NSError error) {
                String playerName = player.getDisplayName();
                NSDictionary<?, ?> parameters = new NSMutableDictionary<>();
                parameters.put("difficulty", difficulty.ordinal());
                parameters.put("challenger", playerName);
                parameters.put("scoreToBeat", gameModel.getGameScore());

                try {
                    NSData jsonified = NSJSONSerialization.createJSONData(parameters, NSJSONWritingOptions.None);
                    String deepLinkID = new String(jsonified.getBytes(), "UTF-8");

                    String encodedID = GTLBase64.encodeWebSafe(NSString.toData(deepLinkID, NSStringEncoding.UTF8));
                    Log.d("Deeplink id is %s \nEncoded it looks like %s", deepLinkID, encodedID);

                    // If you're on a platform that doesn't support
                    // deep-linking,
                    // you can
                    // try adding a link to the web version of your game (if you
                    // have one), or
                    // a product marketing page (if you don't). I'm going to be
                    // optimistic and
                    // assume we'll eventually have this working on the
                    // Type-a-number web sample
                    NSURL webLink = new NSURL(String.format("%s?gamedata=%s", WEB_GAME_URL, encodedID));

                    GPPShare share = GPPShare.getSharedInstance();
                    share.setDelegate(new GPPShareDelegateAdapter() {
                        @Override
                        public void finishedSharing(boolean shared) {
                            if (shared) {
                                Log.d("User successfully shared!");
                            } else {
                                Log.d("User didn't share.");
                            }
                        }
                    });

                    // Let's create the share dialog now!
                    GPPShareBuilder shareDialog = share.getNativeShareDialog();
                    shareDialog.setContentDeepLinkID(encodedID);
                    // This line is unused
                    shareDialog.setContent("Oh yeah", "You will never see this", null);
                    shareDialog.setPrefillText(prefillText);
                    shareDialog.setURLToShare(webLink);
                    shareDialog.setCallToActionButton("PLAY", webLink, encodedID);
                    shareDialog.open();
                } catch (NSErrorException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @IBAction
    private void seeHighScoresClicked(UIButton sender) {
        Leaderboard targetLeaderboard = LeaderboardManager.getLeaderboard(difficulty);

        GPGLauncherController.getSharedInstance().presentLeaderboard(targetLeaderboard.getId());
    }

    @Override
    public void touchesBegan(NSSet<UITouch> touches, UIEvent event) {
        scoreRequestTextField.resignFirstResponder();
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);
        presentNewGame();
    }

    @Override
    public boolean shouldAutorotate(UIInterfaceOrientation toInterfaceOrientation) {
        return toInterfaceOrientation == UIInterfaceOrientation.Portrait;
    }

    public void setDifficulty(DifficultyLevel difficulty) {
        this.difficulty = difficulty;
    }

    public void setIncomingChallenge(NSDictionary<?, ?> incomingChallenge) {
        this.incomingChallenge = incomingChallenge;
    }

    /* TODO remove when not needed */
    @IBOutlet
    private void setTitleLabel(UILabel titleLabel) {
        this.titleLabel = titleLabel;
    }

    @IBOutlet
    private void setPlayerMessage(UILabel playerMessage) {
        this.playerMessage = playerMessage;
    }

    @IBOutlet
    private void setFinalScoreLabel(UILabel finalScoreLabel) {
        this.finalScoreLabel = finalScoreLabel;
    }

    @IBOutlet
    private void setScoreRequestTextField(UITextField scoreRequestTextField) {
        this.scoreRequestTextField = scoreRequestTextField;
    }

    @IBOutlet
    private void setBigActionButton(UIButton bigActionButton) {
        this.bigActionButton = bigActionButton;
    }

    @IBOutlet
    private void setSeeHighScoresButton(UIButton seeHighScoresButton) {
        this.seeHighScoresButton = seeHighScoresButton;
    }

    @IBOutlet
    private void setBragButton(UIButton bragButton) {
        this.bragButton = bragButton;
    }

    @IBOutlet
    private void setHighScoreLabel(UILabel highScoreLabel) {
        this.highScoreLabel = highScoreLabel;
    }

    @IBOutlet
    private void setIncomingChallengeLabel(UILabel incomingChallengeLabel) {
        this.incomingChallengeLabel = incomingChallengeLabel;
    }

    @IBOutlet
    private void setWaitingForHighScore(UIActivityIndicatorView waitingForHighScore) {
        this.waitingForHighScore = waitingForHighScore;
    }
}
