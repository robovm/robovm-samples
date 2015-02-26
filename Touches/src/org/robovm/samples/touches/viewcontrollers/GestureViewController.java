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
 * Portions of this code is based on Apple Inc's Touches sample (v2.0)
 * which is copyright (C) 2008-2013 Apple Inc.
 */

package org.robovm.samples.touches.viewcontrollers;

import org.robovm.apple.coregraphics.CGAffineTransform;
import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIGestureRecognizer;
import org.robovm.apple.uikit.UIGestureRecognizerDelegate;
import org.robovm.apple.uikit.UIGestureRecognizerDelegateAdapter;
import org.robovm.apple.uikit.UIGestureRecognizerState;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UILongPressGestureRecognizer;
import org.robovm.apple.uikit.UIMenuController;
import org.robovm.apple.uikit.UIMenuItem;
import org.robovm.apple.uikit.UIPanGestureRecognizer;
import org.robovm.apple.uikit.UIPinchGestureRecognizer;
import org.robovm.apple.uikit.UIRotationGestureRecognizer;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.Selector;

public class GestureViewController extends UIViewController {
    private final UIImageView firstPieceView;
    private final UIImageView secondPieceView;
    private final UIImageView thirdPieceView;

    private UIView pieceForReset;

    public GestureViewController () {
        getTabBarItem().setTitle("Gestures");

        createGestureRecognizers();

        UIView view = getView();
        view.setBackgroundColor(UIColor.darkText());

        firstPieceView = new UIImageView(UIImage.create("CyanSquare.png"));
        firstPieceView.setFrame(new CGRect(110, 190, 100, 100));
        firstPieceView.setUserInteractionEnabled(true);
        firstPieceView.setAlpha(0.9);
        firstPieceView.setGestureRecognizers(createGestureRecognizers());
        view.addSubview(firstPieceView);

        secondPieceView = new UIImageView(UIImage.create("MagentaSquare.png"));
        secondPieceView.setFrame(new CGRect(210, 290, 100, 100));
        secondPieceView.setUserInteractionEnabled(true);
        secondPieceView.setAlpha(0.9);
        secondPieceView.setGestureRecognizers(createGestureRecognizers());
        view.addSubview(secondPieceView);

        thirdPieceView = new UIImageView(UIImage.create("YellowSquare.png"));
        thirdPieceView.setFrame(new CGRect(10, 90, 100, 100));
        thirdPieceView.setUserInteractionEnabled(true);
        thirdPieceView.setAlpha(0.9);
        thirdPieceView.setGestureRecognizers(createGestureRecognizers());
        view.addSubview(thirdPieceView);

        UILabel title = new UILabel(new CGRect(10, 24, 300, 21));
        title.setText("\"Touches\"; shows use of");
        title.setFont(UIFont.getSystemFont(16));
        title.setTextColor(UIColor.white());
        title.setTextAlignment(NSTextAlignment.Center);
        view.addSubview(title);

        UILabel subtitle = new UILabel(new CGRect(10, 45, 300, 21));
        subtitle.setText("UIGestureRecognizers to move pieces.");
        subtitle.setFont(UIFont.getSystemFont(16));
        subtitle.setTextColor(UIColor.white());
        subtitle.setTextAlignment(NSTextAlignment.Center);
        view.addSubview(subtitle);
    }

    private NSArray<UIGestureRecognizer> createGestureRecognizers () {
        UIGestureRecognizerDelegate gestureRecognizerDelegate = new UIGestureRecognizerDelegateAdapter() {
            /** Ensure that the pinch, pan and rotate gesture recognizers on a particular view can all recognize simultaneously.
             * Prevent other gesture recognizers from recognizing simultaneously. */
            @Override
            public boolean shouldRecognizeSimultaneously (UIGestureRecognizer gestureRecognizer,
                UIGestureRecognizer otherGestureRecognizer) {

                // If the gesture recognizers's view isn't one of our pieces, don't allow simultaneous recognition.
                if (gestureRecognizer.getView() != firstPieceView && gestureRecognizer.getView() != secondPieceView
                    && gestureRecognizer.getView() != thirdPieceView) {
                    return false;
                }

                // If the gesture recognizers are on different views, don't allow simultaneous recognition.
                if (gestureRecognizer.getView() != otherGestureRecognizer.getView()) {
                    return false;
                }

                // If either of the gesture recognizers is the long press, don't allow simultaneous recognition.
                if (gestureRecognizer instanceof UILongPressGestureRecognizer
                    || otherGestureRecognizer instanceof UILongPressGestureRecognizer) {
                    return false;
                }

                return true;
            }
        };

        UIRotationGestureRecognizer rotationGestureRecognizer = new UIRotationGestureRecognizer(
            new UIGestureRecognizer.OnGestureListener() {
                @Override
                public void onGesture (UIGestureRecognizer gestureRecognizer) {
                    rotatePiece((UIRotationGestureRecognizer)gestureRecognizer);
                }
            });
        UIPinchGestureRecognizer pinchGestureRecognizer = new UIPinchGestureRecognizer(
            new UIGestureRecognizer.OnGestureListener() {
                @Override
                public void onGesture (UIGestureRecognizer gestureRecognizer) {
                    scalePiece((UIPinchGestureRecognizer)gestureRecognizer);
                }
            });
        UIPanGestureRecognizer panGestureRecognizer = new UIPanGestureRecognizer(new UIGestureRecognizer.OnGestureListener() {
            @Override
            public void onGesture (UIGestureRecognizer gestureRecognizer) {
                panPiece((UIPanGestureRecognizer)gestureRecognizer);
            }
        });
        UILongPressGestureRecognizer longPressGestureRecognizer = new UILongPressGestureRecognizer(
            new UIGestureRecognizer.OnGestureListener() {
                @Override
                public void onGesture (UIGestureRecognizer gestureRecognizer) {
                    showResetMenu((UILongPressGestureRecognizer)gestureRecognizer);
                }
            });

        NSArray<UIGestureRecognizer> gestureRecognizers = new NSArray<>(rotationGestureRecognizer, pinchGestureRecognizer,
            panGestureRecognizer, longPressGestureRecognizer);
        for (UIGestureRecognizer gestureRecognizer : gestureRecognizers) {
            gestureRecognizer.setDelegate(gestureRecognizerDelegate);
        }
        return gestureRecognizers;
    }

    /** Scale and rotation transforms are applied relative to the layer's anchor point this method moves a gesture recognizer's
     * view's anchor point between the user's fingers. */
    private void adjustAnchorPointForGestureRecognizer (UIGestureRecognizer gestureRecognizer) {
        if (gestureRecognizer.getState() == UIGestureRecognizerState.Began) {
            UIView piece = gestureRecognizer.getView();
            CGPoint locationInView = gestureRecognizer.getLocationInView(piece);
            CGPoint locationInSuperview = gestureRecognizer.getLocationInView(piece.getSuperview());

            piece.getLayer().setAnchorPoint(
                new CGPoint(locationInView.getX() / piece.getBounds().getSize().getWidth(), locationInView.getY()
                    / piece.getBounds().getSize().getHeight()));
            piece.setCenter(locationInSuperview);
        }
    }

    /** Display a menu with a single item to allow the piece's transform to be reset. */
    private void showResetMenu (final UILongPressGestureRecognizer gestureRecognizer) {
        if (gestureRecognizer.getState() == UIGestureRecognizerState.Began) {
            becomeFirstResponder();
            pieceForReset = gestureRecognizer.getView();

            /*
             * Set up the reset menu.
             */
            UIMenuItem resetMenuItem = new UIMenuItem("Reset", new UIMenuItem.OnActionListener() {
                @Override
                public void onAction (UIMenuController menuController, UIMenuItem item) {
                    resetPiece(menuController);
                }
            });

            UIMenuController menuController = UIMenuController.getSharedMenuController();
            menuController.setMenuItems(new NSArray<>(resetMenuItem));

            CGPoint location = gestureRecognizer.getLocationInView(gestureRecognizer.getView());
            CGRect menuLocation = new CGRect(location.getX(), location.getY(), 0, 0);
            menuController.setTargetRect(menuLocation, gestureRecognizer.getView());

            menuController.setMenuVisible(true, true);
        }
    }

    /** Animate back to the default anchor point and transform. */
    private void resetPiece (UIMenuController controller) {
        CGPoint centerPoint = new CGPoint(pieceForReset.getBounds().getMidX(), pieceForReset.getBounds().getMidY());
        CGPoint locationInSuperview = pieceForReset.convertPointToView(centerPoint, pieceForReset.getSuperview());

        pieceForReset.getLayer().setAnchorPoint(new CGPoint(0.5, 0.5));
        pieceForReset.setCenter(locationInSuperview);

        UIView.beginAnimations(null, null);
        pieceForReset.setTransform(CGAffineTransform.Identity());
        UIView.commitAnimations();
    }

    @Override
    public boolean canBecomeFirstResponder () {
        // UIMenuController requires that we can become first responder or it won't display
        return true;
    }

    @Override
    public boolean canPerformAction (Selector action, NSObject sender) {
        if (action.getName().equals("action:")) return true; // FIXME: Is it possible to simplify this?
        return false;
    }

    /** Shift the piece's center by the pan amount. Reset the gesture recognizer's translation to {0, 0} after applying so the next
     * callback is a delta from the current position. */
    private void panPiece (UIPanGestureRecognizer gestureRecognizer) {
        UIView piece = gestureRecognizer.getView();

        adjustAnchorPointForGestureRecognizer(gestureRecognizer);

        if (gestureRecognizer.getState() == UIGestureRecognizerState.Began
            || gestureRecognizer.getState() == UIGestureRecognizerState.Changed) {
            CGPoint translation = gestureRecognizer.getTranslation(piece.getSuperview());

            piece.setCenter(new CGPoint(piece.getCenter().getX() + translation.getX(), piece.getCenter().getY()
                + translation.getY()));
            gestureRecognizer.setTranslation(CGPoint.Zero(), piece.getSuperview());
        }
    }

    /** Rotate the piece by the current rotation. Reset the gesture recognizer's rotation to 0 after applying so the next callback
     * is a delta from the current rotation. */
    private void rotatePiece (UIRotationGestureRecognizer gestureRecognizer) {
        adjustAnchorPointForGestureRecognizer(gestureRecognizer);

        if (gestureRecognizer.getState() == UIGestureRecognizerState.Began
            || gestureRecognizer.getState() == UIGestureRecognizerState.Changed) {
            gestureRecognizer.getView().setTransform(
                gestureRecognizer.getView().getTransform().rotate(gestureRecognizer.getRotation()));
            gestureRecognizer.setRotation(0);
        }
    }

    /** Scale the piece by the current scale. Reset the gesture recognizer's rotation to 0 after applying so the next callback is a
     * delta from the current scale. */
    private void scalePiece (UIPinchGestureRecognizer gestureRecognizer) {
        adjustAnchorPointForGestureRecognizer(gestureRecognizer);

        if (gestureRecognizer.getState() == UIGestureRecognizerState.Began
            || gestureRecognizer.getState() == UIGestureRecognizerState.Changed) {
            gestureRecognizer.getView().setTransform(
                gestureRecognizer.getView().getTransform().scale(gestureRecognizer.getScale(), gestureRecognizer.getScale()));
            gestureRecognizer.setScale(1);
        }
    }
}
