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
 * Portions of this code is based on Apple Inc's TheElements sample (v1.12)
 * which is copyright (C) 2008-2013 Apple Inc.
 */

package org.robovm.samples.theelements.viewcontrollers;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSNumber;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewAnimationTransition;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.Selector;
import org.robovm.objc.annotation.Method;
import org.robovm.rt.bro.ptr.VoidPtr;
import org.robovm.samples.theelements.model.AtomicElement;
import org.robovm.samples.theelements.views.AtomicElementFlippedView;
import org.robovm.samples.theelements.views.AtomicElementView;

public class AtomicElementViewController extends UIViewController {
    private static final double FLIP_TRANSITION_DURATION = 0.75;
    private static final double REFLECTION_FRACTION = 0.35;
    private static final double REFLECTION_OPACITY = 0.5;

    private AtomicElement element;

    private boolean frontViewIsVisible;
    private AtomicElementView atomicElementView;
    private UIImageView reflectionView;
    private AtomicElementFlippedView atomicElementFlippedView;
    private UIButton flipIndicatorButton;

    @Override
    public void viewDidLoad () {
        super.viewDidLoad();

        getView().setBackgroundColor(UIColor.black());

        frontViewIsVisible = true;
        CGSize preferredAtomicElementViewSize = AtomicElementView.getPreferredViewSize();
        CGRect viewRect = new CGRect((getView().getBounds().getWidth() - preferredAtomicElementViewSize.width()) / 2, (getView()
            .getBounds().getHeight() - preferredAtomicElementViewSize.height()) / 2 - 40, preferredAtomicElementViewSize.width(),
            preferredAtomicElementViewSize.height());
        // create the atomic element view
        atomicElementView = new AtomicElementView(viewRect);

        // add the atomic element view to the view controller's view
        atomicElementView.setElement(element);
        getView().addSubview(atomicElementView);
        atomicElementView.setViewController(this);

        // create the atomic element flipped view
        atomicElementFlippedView = new AtomicElementFlippedView(viewRect);
        atomicElementFlippedView.setElement(element);
        atomicElementFlippedView.setViewController(this);

        // create the reflection view
        CGRect reflectionRect = new CGRect(viewRect.origin(), viewRect.size());

        // the reflection is a fraction of the size of the view being reflected
        reflectionRect.size().height(reflectionRect.getHeight() * REFLECTION_FRACTION);
        // and is offset to be at the bottom of the view being reflected
        reflectionRect = reflectionRect.offset(0, viewRect.getHeight());
        reflectionView = new UIImageView(reflectionRect);

        // determine the size of the reflection to create
        int reflectionHeight = (int)(atomicElementView.getBounds().getHeight() * REFLECTION_FRACTION);

        // create the reflection image, assign it to the UIImageView and add the image view to the view controller's view
        reflectionView.setImage(atomicElementView.getReflectedImageRepresentation(reflectionHeight));
        reflectionView.setAlpha(REFLECTION_OPACITY);
        getView().addSubview(reflectionView);

        // setup our flip indicator button (placed as a nav bar item to the right)
        flipIndicatorButton = new UIButton(new CGRect(0, 0, 30, 30));
        flipIndicatorButton.addOnTouchUpInsideListener(new UIControl.OnTouchUpInsideListener() {
            @Override
            public void onTouchUpInside (UIControl control, UIEvent event) {
                flipCurrentView();
            }
        });

        // front view is always visible at first
        flipIndicatorButton.setBackgroundImage(UIImage.create("flipper_list_blue.png"), UIControlState.Normal);
        UIBarButtonItem flipButtonBarItem = new UIBarButtonItem(flipIndicatorButton);
        getNavigationItem().setRightBarButtonItem(flipButtonBarItem, true);
    }

    public void flipCurrentView () {
        // disable user interaction during the flip animation
        getView().setUserInteractionEnabled(false);
        flipIndicatorButton.setUserInteractionEnabled(false);

        // setup the animation group
        UIView.beginAnimations(null, null);
        UIView.setAnimationDurationInSeconds(FLIP_TRANSITION_DURATION);
        UIView.setAnimationDelegate(this);
        UIView.setAnimationDidStopSelector(Selector.register("myTransitionDidStop:finished:context:"));

        // swap the views and transition
        if (frontViewIsVisible) {
            UIView.setAnimationTransition(UIViewAnimationTransition.FlipFromRight, getView(), true);
            atomicElementView.removeFromSuperview();
            getView().addSubview(atomicElementFlippedView);

            // update the reflection image for the new view
            int reflectionHeight = (int)(atomicElementFlippedView.getBounds().getHeight() * REFLECTION_FRACTION);
            UIImage reflectedImage = atomicElementFlippedView.getReflectedImageRepresentation(reflectionHeight);
            reflectionView.setImage(reflectedImage);
        } else {
            UIView.setAnimationTransition(UIViewAnimationTransition.FlipFromLeft, getView(), true);
            atomicElementFlippedView.removeFromSuperview();
            getView().addSubview(atomicElementView);

            // update the reflection image for the new view
            int reflectionHeight = (int)(atomicElementView.getBounds().getHeight() * REFLECTION_FRACTION);
            UIImage reflectedImage = atomicElementView.getReflectedImageRepresentation(reflectionHeight);
            reflectionView.setImage(reflectedImage);
        }
        UIView.commitAnimations();

        // swap the nav bar button views
        UIView.beginAnimations(null, null);
        UIView.setAnimationDurationInSeconds(FLIP_TRANSITION_DURATION);
        UIView.setAnimationDelegate(this);
        UIView.setAnimationDidStopSelector(Selector.register("myTransitionDidStop:finished:context:"));

        if (frontViewIsVisible) {
            UIView.setAnimationTransition(UIViewAnimationTransition.FlipFromRight, flipIndicatorButton, true);
            flipIndicatorButton
                .setBackgroundImage(element.getFlipperImageForAtomicElementNavigationItem(), UIControlState.Normal);
        } else {
            UIView.setAnimationTransition(UIViewAnimationTransition.FlipFromLeft, flipIndicatorButton, true);
            flipIndicatorButton.setBackgroundImage(UIImage.create("flipper_list_blue.png"), UIControlState.Normal);
        }
        UIView.commitAnimations();

        // invert the front view state
        frontViewIsVisible = !frontViewIsVisible;
    }

    @Method(selector = "myTransitionDidStop:finished:context:")
    private void myTransitionDidStop (String animationID, NSNumber finished, VoidPtr context) {
        // re-enable user interaction when the flip animation is completed
        getView().setUserInteractionEnabled(true);
        flipIndicatorButton.setUserInteractionEnabled(true);
    }

    public void setElement (AtomicElement element) {
        this.element = element;
    }
}
