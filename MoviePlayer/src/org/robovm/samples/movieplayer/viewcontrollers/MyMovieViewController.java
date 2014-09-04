/*
 * Copyright (C) 2014 Trillian Mobile AB
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
 * Portions of this code is based on Apple Inc's MoviePlayer sample (v1.5)
 * which is copyright (C) 2008-2014 Apple Inc.
 */
package org.robovm.samples.movieplayer.viewcontrollers;

import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSNotificationCenter;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.mediaplayer.MPMediaPlayback;
import org.robovm.apple.mediaplayer.MPMovieFinishReason;
import org.robovm.apple.mediaplayer.MPMovieLoadState;
import org.robovm.apple.mediaplayer.MPMoviePlaybackState;
import org.robovm.apple.mediaplayer.MPMoviePlayerController;
import org.robovm.apple.mediaplayer.MPMovieSourceType;
import org.robovm.apple.uikit.UIAlertView;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIDevice;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIInterfaceOrientation;
import org.robovm.apple.uikit.UIRectEdge;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.block.VoidBlock1;
import org.robovm.objc.block.VoidBlock3;
import org.robovm.samples.movieplayer.MoviePlayerUserPrefs;

public class MyMovieViewController extends UIViewController {
    private static final float MOVIE_VIEW_OFFSET_X = 20;
    private static final float MOVIE_VIEW_OFFSET_Y = 20;

    private final UIImageView movieBackgroundImageView;
    private final UIView backgroundView;
    private final MyOverlayViewController overlayController;
    private MPMoviePlayerController moviePlayerController;

    private NSObject observerLoadStateDidChange;
    private NSObject observerPlaybackDidFinish;
    private NSObject observerIsPreparedToPlayDidChange;
    private NSObject observerPlaybackStateDidChange;

    public MyMovieViewController() {
        super();

        movieBackgroundImageView = new UIImageView(UIImage.createFromBundle("images/movieBackground.jpg"));
        movieBackgroundImageView.setFrame(new CGRect(0, 0, 240, 128));

        backgroundView = new UIView(new CGRect(0, 0, 320, 460));
        backgroundView.setBackgroundColor(UIColor.createFromWhiteAlpha(0.66, 1));

        overlayController = new MyOverlayViewController(this);

        if (Integer.valueOf(UIDevice.getCurrentDevice().getSystemVersion().substring(0, 1)) >= 7) {
            setEdgesForExtendedLayout(UIRectEdge.None);
        }
    }

    /** Sent to the view controller after the user interface rotates. */
    @Override
    public void didRotate(UIInterfaceOrientation fromInterfaceOrientation) {
        CGRect viewInsetRect = getView().getBounds().copy();
        moviePlayerController.getView().setFrame(viewInsetRect.inset(MOVIE_VIEW_OFFSET_X, MOVIE_VIEW_OFFSET_Y));
        /* Size the overlay view for the current orientation. */
        resizeOverlayWindow();
    }

    @Override
    public boolean shouldAutorotate(UIInterfaceOrientation toInterfaceOrientation) {
        return true;
    }

    @Override
    public boolean canBecomeFirstResponder() {
        return true;
    }

    @Override
    public void viewDidUnload() {
        deletePlayerAndNotificationObservers();
        super.viewDidUnload();
    }

    /**
     * Notifies the view controller that its view is about to be become visible.
     */
    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);
        /* Size the overlay view for the current orientation. */
        resizeOverlayWindow();
        /* Update user settings for the movie (in case they changed). */
        applyUserSettings();
    }

    /**
     * Notifies the view controller that its view is about to be dismissed,
     * covered, or otherwise hidden from view.
     */
    @Override
    public void viewWillDisappear(boolean animated) {
        super.viewWillDisappear(animated);

        /* Remove the movie view from the current view hierarchy. */
        removeMovieViewFromViewHierarchy();
        /* Removie the overlay view. */
        removeOverlayView();
        /* Remove the background view. */
        backgroundView.removeFromSuperview();
        /* Delete the movie player object and remove the notification observers. */
        deletePlayerAndNotificationObservers();
    }

    /** Remove the movie view from the view hierarchy. */
    private void removeMovieViewFromViewHierarchy() {
        if (moviePlayerController != null) {
            moviePlayerController.getView().removeFromSuperview();
        }
    }

    private void displayError(NSError error) {
        if (error != null) {
            UIAlertView alert = new UIAlertView("Error", error.getLocalizedDescription(), null, "OK");
            alert.show();
        }
    }

    /**
     * Add an overlay view on top of the movie. This view will display movie
     * play states and includes a 'Close Movie' button.
     */
    protected void addOverlayView() {
        MPMoviePlayerController player = moviePlayerController;

        if (!(overlayController.getView().isDescendantOf(getView()) && player.getView().isDescendantOf(getView()))) {
            // add an overlay view to the window view hierarchy
            getView().addSubview(overlayController.getView());
        }
    }

    /** Remove overlay view from the view hierarchy. */
    private void removeOverlayView() {
        overlayController.getView().removeFromSuperview();
    }

    private void resizeOverlayWindow() {
        CGRect frame = overlayController.getView().getFrame();
        frame.origin(new CGPoint(Math.round((getView().getFrame().getWidth() - frame.getWidth())) / 2.0, Math
                .round((getView().getFrame().getHeight() - frame.getHeight()) / 2.0)));
        overlayController.getView().setFrame(frame);
    }

    /**
     * Action method for the overlay view 'Close Movie' button. Remove the movie
     * view and overlay view from the window, dispose the movie object and
     * remove the notification handlers.
     */
    public void closeOverlay() {
        moviePlayerController.stop();
        removeMovieViewFromViewHierarchy();
        removeOverlayView();
        backgroundView.removeFromSuperview();
        deletePlayerAndNotificationObservers();
    }

    /**
     * Called by the MoviePlayerAppDelegate (UIApplicationDelegate protocol)
     * applicationWillEnterForeground when the app is about to enter the
     * foreground.
     */
    public void willEnterForeground() {
        /*
         * Set the movie object settings (control mode, background color, and so
         * on) in case these changed.
         */
        applyUserSettings();
    }

    /**
     * Called soon after the Play Movie button is pressed to play the local
     * movie.
     */
    protected void playMovieFile(NSURL movieFileURL) {
        createAndPlayMovie(movieFileURL, MPMovieSourceType.File);
    }

    /**
     * Called soon after the Play Movie button is pressed to play the streaming
     * movie.
     */
    protected void playMovieStream(NSURL movieFileURL) {
        MPMovieSourceType movieSourceType = MPMovieSourceType.Unknown;
        /* If we have a streaming url then specify the movie source type. */
        if (movieFileURL.getPathExtension().equals("m3u8")) {
            movieSourceType = MPMovieSourceType.Streaming;
        }
        createAndPlayMovie(movieFileURL, movieSourceType);
    }

    /**
     * Create a MPMoviePlayerController movie object for the specified URL and
     * add movie notification observers. Configure the movie object for the
     * source type, scaling mode, control style, background color, background
     * image, repeat mode and AirPlay mode. Add the view containing the movie
     * content and controls to the existing view hierarchy.
     */
    private void createAndConfigurePlayer(NSURL movieURL, MPMovieSourceType sourceType) {
        /* Create a new movie player object. */
        MPMoviePlayerController player = new MPMoviePlayerController(movieURL);
        if (player != null) {
            /* Save the movie object. */
            moviePlayerController = player;
            /*
             * Register the current object as an observer for the movie
             * notifications.
             */
            installMovieNotificationObservers();
            /* Specify the URL that points to the movie file. */
            player.setContentURL(movieURL);
            /*
             * If you specify the movie type before playing the movie it can
             * result in faster load times.
             */
            player.setMovieSourceType(sourceType);
            /*
             * Apply the user movie preference settings to the movie player
             * object.
             */
            applyUserSettings();
            /*
             * Add a background view as a subview to hide our other view
             * controls underneath during movie playback.
             */
            getView().addSubview(backgroundView);
            /* Inset the movie frame in the parent view frame. */
            CGRect viewInsetRect = getView().getBounds().copy();
            viewInsetRect.inset(MOVIE_VIEW_OFFSET_X, MOVIE_VIEW_OFFSET_Y);
            player.getView().setFrame(viewInsetRect);
            player.getView().setBackgroundColor(UIColor.colorLightGray());
            /*
             * To present a movie in your application, incorporate the view
             * contained in a movie player’s view property into your
             * application’s view hierarchy. Be sure to size the frame
             * correctly.
             */
            getView().addSubview(player.getView());
        }
    }

    /** Load and play the specified movie url with the given file type. */
    private void createAndPlayMovie(NSURL movieURL, MPMovieSourceType sourceType) {
        createAndConfigurePlayer(movieURL, sourceType);
        /* Play the movie! */
        moviePlayerController.play();
    }

    /*
     * Apply user movie preference settings (these are set from the Settings:
     * iPhone Settings->Movie Player) for scaling mode, control style,
     * background color, repeat mode, application audio session, background
     * image and AirPlay mode.
     */
    private void applyUserSettings() {
        MPMoviePlayerController player = moviePlayerController;
        if (player != null) {
            player.setScalingMode(MoviePlayerUserPrefs.getScalingMode());
            player.setControlStyle(MoviePlayerUserPrefs.getControlStyle());
            player.getBackgroundView().setBackgroundColor(MoviePlayerUserPrefs.getBackgroundColor());
            player.setRepeatMode(MoviePlayerUserPrefs.getRepeatMode());
            if (MoviePlayerUserPrefs.useMovieBackground()) {
                movieBackgroundImageView.setFrame(getView().getBounds());
                player.getBackgroundView().addSubview(movieBackgroundImageView);
            } else {
                movieBackgroundImageView.removeFromSuperview();
            }
            /* Indicate the movie player allows AirPlay movie playback. */
            player.setAllowsAirPlay(true);
        }
    }

    /** Register observers for the various movie object notifications. */
    private void installMovieNotificationObservers() {
        observerLoadStateDidChange = MPMoviePlayerController.Notifications.observeLoadStateDidChange(
                moviePlayerController,
                new VoidBlock1<MPMoviePlayerController>() {
                    @Override
                    public void invoke(MPMoviePlayerController player) {
                        MPMovieLoadState loadState = player.getLoadState();
                        /* The load state is not known at this time. */
                        if (loadState.contains(MPMovieLoadState.Unknown)) {
                            overlayController.setLoadStateDisplayString("unknown");
                        }
                        /*
                         * The buffer has enough data that playback can begin,
                         * but it may run out of data before playback finishes.
                         */
                        if (loadState.contains(MPMovieLoadState.Playable)) {
                            overlayController.setLoadStateDisplayString("playable");
                        }
                        /*
                         * Enough data has been buffered for playback to
                         * continue uninterrupted.
                         */
                        if (loadState.contains(MPMovieLoadState.PlaythroughOK)) {
                            // Add an overlay view on top of the movie view
                            addOverlayView();
                            overlayController.setLoadStateDisplayString("playthrough ok");
                        }
                        /* The buffering of data has stalled. */
                        if (loadState.contains(MPMovieLoadState.Stalled)) {
                            overlayController.setLoadStateDisplayString("stalled");
                        }
                    }
                });

        observerPlaybackDidFinish = MPMoviePlayerController.Notifications.observePlaybackDidFinish(
                moviePlayerController,
                new VoidBlock3<MPMoviePlayerController, MPMovieFinishReason, NSError>() {
                    @Override
                    public void invoke(MPMoviePlayerController player, MPMovieFinishReason reason, NSError error) {
                        switch (reason) {
                        /* The end of the movie was reached. */
                        case PlaybackEnded:
                            /*
                             * Add your code here to handle
                             * MPMovieFinishReasonPlaybackEnded.
                             */
                            break;
                        /* An error was encountered during playback. */
                        case PlaybackError:
                            Foundation.log("An error was encountered during playback");
                            displayError(error);
                            removeMovieViewFromViewHierarchy();
                            removeOverlayView();
                            backgroundView.removeFromSuperview();
                            break;
                        /* The user stopped playback. */
                        case UserExited:
                            removeMovieViewFromViewHierarchy();
                            removeOverlayView();
                            backgroundView.removeFromSuperview();
                            break;
                        default:
                            break;
                        }
                    }
                });

        observerIsPreparedToPlayDidChange = MPMoviePlayerController.Notifications.observeIsPreparedToPlayDidChange(
                moviePlayerController,
                new VoidBlock1<MPMediaPlayback>() {
                    @Override
                    public void invoke(MPMediaPlayback player) {
                        addOverlayView();
                    }
                });

        observerPlaybackStateDidChange = MPMoviePlayerController.Notifications.observePlaybackStateDidChange(
                moviePlayerController,
                new VoidBlock1<MPMoviePlayerController>() {
                    @Override
                    public void invoke(MPMoviePlayerController player) {
                        MPMoviePlaybackState playbackState = player.getPlaybackState();
                        /* Playback is currently stopped. */
                        if (playbackState ==
                        MPMoviePlaybackState.Stopped)
                        {
                            overlayController.setPlaybackStateDisplayString("stopped");
                        }
                        /* Playback is currently under way. */
                        else if (playbackState == MPMoviePlaybackState.Playing) {
                            overlayController.setPlaybackStateDisplayString("playing");

                        }
                        /* Playback is currently paused. */
                        else if (playbackState == MPMoviePlaybackState.Paused) {
                            overlayController.setPlaybackStateDisplayString("paused");
                        }
                        /*
                         * Playback is temporarily interrupted, perhaps because
                         * the buffer ran out of content.
                         */
                        else if (playbackState == MPMoviePlaybackState.Interrupted) {
                            overlayController.setPlaybackStateDisplayString("interrupted");
                        }
                    }
                });
    }

    /**
     * Delete the movie player object, and remove the movie notification
     * observers.
     */
    private void deletePlayerAndNotificationObservers() {
        removeMovieNotificationHandlers();
        moviePlayerController = null;
    }

    /** Remove the movie notification observers from the movie object. */
    private void removeMovieNotificationHandlers() {
        NSNotificationCenter.getDefaultCenter().removeObserver(observerLoadStateDidChange);
        NSNotificationCenter.getDefaultCenter().removeObserver(observerIsPreparedToPlayDidChange);
        NSNotificationCenter.getDefaultCenter().removeObserver(observerPlaybackDidFinish);
        NSNotificationCenter.getDefaultCenter().removeObserver(observerPlaybackStateDidChange);
    }
}
