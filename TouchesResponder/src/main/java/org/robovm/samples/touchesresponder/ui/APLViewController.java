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
 * Portions of this code is based on Apple Inc's Touches sample (v2.0)
 * which is copyright (C) 2008-2013 Apple Inc.
 */
package org.robovm.samples.touchesresponder.ui;

import org.robovm.apple.coregraphics.CGAffineTransform;
import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.foundation.NSSet;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UITouch;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;

@CustomClass("APLViewController")
public class APLViewController extends UIViewController {
    // Determines how fast a piece size grows when it is moved.
    private static final double GROW_ANIMATION_DURATION_SECONDS = 0.15;
    // Determines how fast a piece size shrinks when a piece stops moving.
    private static final double SHRINK_ANIMATION_DURATION_SECONDS = 0.15;

    private boolean piecesOnTop;// Keeps track of whether two or more pieces are
                                // on top of each other.

    // Views the user can move
    @IBOutlet
    private UIImageView firstPieceView;
    @IBOutlet
    private UIImageView secondPieceView;
    @IBOutlet
    private UIImageView thirdPieceView;

    // Displays the touch phase.
    @IBOutlet
    private UILabel touchPhaseText;
    // Displays touch information for multiple taps.
    @IBOutlet
    private UILabel touchInfoText;
    // Displays touch tracking information.
    @IBOutlet
    private UILabel touchTrackingText;
    // Displays instructions for how to split apart pieces that are on top of
    // each other.
    @IBOutlet
    private UILabel touchInstructionsText;

    /**
     * Handles the start of a touch.
     */
    @Override
    public void touchesBegan(NSSet<UITouch> touches, UIEvent event) {
        long numTaps = touches.any().getTapCount();

        touchPhaseText.setText("Phase: Touches began");
        touchInfoText.setText("");
        if (numTaps >= 2) {
            touchInfoText.setText(String.format("%d taps", numTaps));
            if ((numTaps == 2) && piecesOnTop) {
                // A double tap positions the three pieces in a diagonal.
                // The user will want to double tap when two or more pieces are
                // on top of each other
                if (firstPieceView.getCenter().getX() == secondPieceView.getCenter().getX()) {
                    secondPieceView.setCenter(new CGPoint(firstPieceView.getCenter().getX() - 50, firstPieceView
                            .getCenter().getY() - 50));
                }
                if (firstPieceView.getCenter().getX() == thirdPieceView.getCenter().getX()) {
                    thirdPieceView.setCenter(new CGPoint(firstPieceView.getCenter().getX() + 50, firstPieceView
                            .getCenter().getY() + 50));
                }
                if (secondPieceView.getCenter().getX() == thirdPieceView.getCenter().getX()) {
                    thirdPieceView.setCenter(new CGPoint(secondPieceView.getCenter().getX() + 50, secondPieceView
                            .getCenter().getY() + 50));
                }
                touchInstructionsText.setText("");
            }
        } else {
            touchTrackingText.setText("");
        }

        // Enumerate through all the touch objects.
        for (UITouch touch : touches) {
            // Send to the dispatch method, which will make sure the appropriate
            // subview is acted upon.
            dispatchFirstTouch(touch.getLocationInView(getView()), null);
        }
    }

    /**
     * Checks to see which view, or views, the point is in and then calls a
     * method to perform the opening animation, which makes the piece slightly
     * larger, as if it is being picked up by the user.
     */
    private void dispatchFirstTouch(CGPoint touchPoint, UIEvent event) {
        if (firstPieceView.getFrame().contains(touchPoint)) {
            animateFirstTouch(touchPoint, firstPieceView);
        }
        if (secondPieceView.getFrame().contains(touchPoint)) {
            animateFirstTouch(touchPoint, secondPieceView);
        }
        if (thirdPieceView.getFrame().contains(touchPoint)) {
            animateFirstTouch(touchPoint, thirdPieceView);
        }
    }

    /**
     * Handles the continuation of a touch.
     */
    @Override
    public void touchesMoved(NSSet<UITouch> touches, UIEvent event) {
        int touchCount = 0;
        touchPhaseText.setText("Phase: Touches moved");
        // Enumerates through all touch objects
        for (UITouch touch : touches) {
            // Send to the dispatch method, which will make sure the appropriate
            // subview is acted upon
            dispatchTouchEvent(touch.getView(), touch.getLocationInView(getView()));
            touchCount++;
        }

        // When multiple touches, report the number of touches.
        if (touchCount > 1) {
            touchTrackingText.setText(String.format("Tracking %d touches", touchCount));
        } else {
            touchTrackingText.setText("Tracking 1 touch");
        }
    }

    /**
     * Checks to see which view, or views, the point is in and then sets the
     * center of each moved view to the new postion. If views are directly on
     * top of each other, they move together.
     */
    private void dispatchTouchEvent(UIView view, CGPoint position) {
        // Check to see which view, or views, the point is in and then move to
        // that position.
        if (firstPieceView.getFrame().contains(position)) {
            firstPieceView.setCenter(position);
        }
        if (secondPieceView.getFrame().contains(position)) {
            secondPieceView.setCenter(position);
        }
        if (thirdPieceView.getFrame().contains(position)) {
            thirdPieceView.setCenter(position);
        }
    }

    /**
     * Handles the end of a touch event.
     */
    @Override
    public void touchesEnded(NSSet<UITouch> touches, UIEvent event) {
        touchPhaseText.setText("Phase: Touches ended");
        // Enumerates through all touch object
        for (UITouch touch : touches) {
            // Sends to the dispatch method, which will make sure the
            // appropriate subview is acted upon
            dispatchTouchEndEvent(touch.getView(), touch.getLocationInView(getView()));
        }
    }

    /**
     * Checks to see which view, or views, the point is in and then calls a
     * method to perform the closing animation, which is to return the piece to
     * its original size, as if it is being put down by the user.
     */
    private void dispatchTouchEndEvent(UIView view, CGPoint position) {
        // Check to see which view, or views, the point is in and then animate
        // to that position.
        if (firstPieceView.getFrame().contains(position)) {
            animateView(firstPieceView, position);
        }
        if (secondPieceView.getFrame().contains(position)) {
            animateView(secondPieceView, position);
        }
        if (thirdPieceView.getFrame().contains(position)) {
            animateView(thirdPieceView, position);
        }

        // If one piece obscures another, display a message so the user can move
        // the pieces apart.
        if (firstPieceView.getCenter().equalsTo(secondPieceView.getCenter()) ||
                firstPieceView.getCenter().equalsTo(thirdPieceView.getCenter()) ||
                secondPieceView.getCenter().equalsTo(thirdPieceView.getCenter())) {
            touchInstructionsText.setText("Double tap the background to move the pieces apart.");
            piecesOnTop = true;
        } else {
            piecesOnTop = false;
        }
    }

    @Override
    public void touchesCancelled(NSSet<UITouch> touches, UIEvent event) {
        touchPhaseText.setText("Phase: Touches cancelled");
        // Enumerates through all touch objects.
        for (UITouch touch : touches) {
            // Sends to the dispatch method, which will make sure the
            // appropriate subview is acted upon.
            dispatchTouchEndEvent(touch.getView(), touch.getLocationInView(getView()));
        }
    }

    /**
     * Scales up a view slightly which makes the piece slightly larger, as if it
     * is being picked up by the user.
     */
    private void animateFirstTouch(CGPoint touchPoint, UIImageView view) {
        // Pulse the view by scaling up, then move the view to under the finger.
        UIView.beginAnimations(null, null);
        UIView.setAnimationDurationInSeconds(GROW_ANIMATION_DURATION_SECONDS);
        view.setTransform(CGAffineTransform.createScale(1.2, 1.2));
        UIView.commitAnimations();
    }

    /**
     * Scales down the view and moves it to the new position.
     */
    private void animateView(UIView view, CGPoint position) {
        UIView.beginAnimations(null, null);
        UIView.setAnimationDurationInSeconds(SHRINK_ANIMATION_DURATION_SECONDS);
        // Set the center to the final postion.
        view.setCenter(position);
        // Set the transform back to the identity, thus undoing the previous
        // scaling effect.
        view.setTransform(CGAffineTransform.Identity());
        UIView.commitAnimations();
    }
}
