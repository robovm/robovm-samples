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
 * Portions of this code is based on Apple Inc's UICatalog sample (v2.11)
 * which is copyright (C) 2008-2013 Apple Inc.
 */

package org.robovm.samples.uicatalog.viewcontrollers;

import java.util.LinkedList;
import java.util.List;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSMutableArray;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonItemStyle;
import org.robovm.apple.uikit.UIBarButtonSystemItem;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIToolbar;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewAnimationTransition;
import org.robovm.apple.uikit.UIViewAutoresizing;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.Selector;
import org.robovm.objc.annotation.Method;

/**
 * The view controller for showing transitions with UIView.
 */
public class TransitionViewController extends UIViewController {

    private float imageHeight = 200.0f;
    private float imageWidth = 250.0f;
    private float transitionDuration = 0.75f;
    private float topPlacement = 120.0f; // y coord for the images

    private UIView containerView;
    private UIImageView mainView;
    private UIImageView flipToView;
    private UIToolbar toolBar;

    private UIBarButtonItem flipItem;
    private UIBarButtonItem curlItem;

    /**
     * setup buttons controls with associated behaviour and load images
     */
    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        setTitle("");

        this.flipItem = new UIBarButtonItem();
        this.flipItem.setTitle("Flip Image");
        this.flipItem.setStyle(UIBarButtonItemStyle.Bordered);
        this.flipItem.setAction(Selector.register("flipAction"));
        this.flipItem.setTarget(TransitionViewController.this);

        this.curlItem = new UIBarButtonItem();
        this.curlItem.setTitle("Curl Image");
        this.curlItem.setStyle(UIBarButtonItemStyle.Bordered);
        this.curlItem.setAction(Selector.register("curlAction"));
        this.curlItem.setTarget(TransitionViewController.this);

        // create the container view which we will use for transition animation
        // (centered horizontally)
        CGRect frame = new CGRect(((this.getView().getBounds().getWidth() - imageWidth) / 2.0f), topPlacement,
                imageWidth, imageHeight);

        containerView = new UIView(frame);
        this.getView().addSubview(this.containerView);
        this.containerView.setAutoresizingMask(UIViewAutoresizing.FlexibleLeftMargin
                .set(UIViewAutoresizing.FlexibleRightMargin));

        // create the initial image view
        frame = new CGRect(0.0, 0.0, imageWidth, imageHeight);
        this.mainView = new UIImageView(frame);
        this.mainView.setImage(UIImage.createFromBundle("scene1.jpg"));
        this.containerView.addSubview(this.mainView);

        // create the alternate image view (to transition between), we don't add
        // it as a subview yet
        CGRect imageFrame = new CGRect(0.0, 0.0, imageWidth, imageHeight);
        this.flipToView = new UIImageView(imageFrame);
        this.flipToView.setImage(UIImage.createFromBundle("scene2.jpg"));
        toolBar = new UIToolbar(new CGRect(0, 416, 320, 44));

        UIBarButtonItem flexSpace1 = new UIBarButtonItem(UIBarButtonSystemItem.FlexibleSpace, null, null);
        UIBarButtonItem flexSpace2 = new UIBarButtonItem(UIBarButtonSystemItem.FlexibleSpace, null, null);

        List<UIBarButtonItem> items = new LinkedList<UIBarButtonItem>();
        items.add(flexSpace1);
        items.add(flipItem);
        items.add(curlItem);
        items.add(flexSpace2);

        NSMutableArray<UIBarButtonItem> itemArray = new NSMutableArray<UIBarButtonItem>(items);

        toolBar.setItems(itemArray);
        getView().setBackgroundColor(UIColor.colorBlack());
        getView().addSubview(toolBar);
    }

    // called after this controller's view will appear
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);

        // for aesthetic reasons (the background is black), make the nav bar
        // black for this particular page
        this.getNavigationController().getNavigationBar().setTintColor(UIColor.colorBlack());
    }

    /**
     * Curls image
     */
    @Method
    public void curlAction() {
        UIView.beginAnimations(null, null);
        UIView.setDurationForAnimation(transitionDuration);

        UIView.setAnimationTransition((this.mainView.getSuperview() != null ?
                UIViewAnimationTransition.CurlUp : UIViewAnimationTransition.CurlDown), this.containerView, false);

        if (this.flipToView.getSuperview() != null) {
            this.flipToView.removeFromSuperview();
            this.containerView.addSubview(this.mainView);
        } else {
            this.mainView.removeFromSuperview();
            this.containerView.addSubview(this.flipToView);
        }

        UIView.commitAnimations();
    }

    /**
     * Flips image
     */
    @Method
    public void flipAction() {
        UIView.beginAnimations(null, null);
        UIView.setDurationForAnimation(transitionDuration);

        UIView.setAnimationTransition(this.mainView.getSuperview() != null ? UIViewAnimationTransition.FlipFromLeft
                : UIViewAnimationTransition.FlipFromRight, this.containerView, true);
        if (this.flipToView.getSuperview() != null) {
            this.flipToView.removeFromSuperview();
            this.containerView.addSubview(this.mainView);
        } else {
            this.mainView.removeFromSuperview();
            this.containerView.addSubview(this.flipToView);
        }
        UIView.commitAnimations();
    }

}
