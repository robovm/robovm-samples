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

/** The view controller for showing transitions with UIView. */
public class TransitionViewController extends UIViewController {

    private final float imageHeight = 200.0f;
    private final float imageWidth = 250.0f;
    private final float transitionDuration = 0.75f;
    private final float topPlacement = 120.0f; // y coord for the images

    private UIView containerView;
    private UIImageView mainView;
    private UIImageView flipToView;
    private UIToolbar toolBar;

    private UIBarButtonItem flipItem;
    private UIBarButtonItem curlItem;

    /** setup buttons controls with associated behaviour and load images */
    @Override
    public void viewDidLoad () {
        super.viewDidLoad();
        setTitle("");

        flipItem = new UIBarButtonItem();
        flipItem.setTitle("Flip Image");
        flipItem.setStyle(UIBarButtonItemStyle.Bordered);
        flipItem.setOnClickListener(new UIBarButtonItem.OnClickListener() {
            @Override
            public void onClick (UIBarButtonItem barButtonItem) {
                UIView.beginAnimations(null, null);
                UIView.setDurationForAnimation(transitionDuration);

                UIView.setAnimationTransition(mainView.getSuperview() != null ? UIViewAnimationTransition.FlipFromLeft
                    : UIViewAnimationTransition.FlipFromRight, containerView, true);
                if (flipToView.getSuperview() != null) {
                    flipToView.removeFromSuperview();
                    containerView.addSubview(mainView);
                } else {
                    mainView.removeFromSuperview();
                    containerView.addSubview(flipToView);
                }
                UIView.commitAnimations();
            }
        });

        curlItem = new UIBarButtonItem();
        curlItem.setTitle("Curl Image");
        curlItem.setStyle(UIBarButtonItemStyle.Bordered);
        curlItem.setOnClickListener(new UIBarButtonItem.OnClickListener() {
            @Override
            public void onClick (UIBarButtonItem barButtonItem) {
                UIView.beginAnimations(null, null);
                UIView.setDurationForAnimation(transitionDuration);

                UIView.setAnimationTransition((mainView.getSuperview() != null ? UIViewAnimationTransition.CurlUp
                    : UIViewAnimationTransition.CurlDown), containerView, false);

                if (flipToView.getSuperview() != null) {
                    flipToView.removeFromSuperview();
                    containerView.addSubview(mainView);
                } else {
                    mainView.removeFromSuperview();
                    containerView.addSubview(flipToView);
                }

                UIView.commitAnimations();
            }
        });

        // create the container view which we will use for transition animation
        // (centered horizontally)
        CGRect frame = new CGRect(((getView().getBounds().getWidth() - imageWidth) / 2.0f), topPlacement, imageWidth, imageHeight);

        containerView = new UIView(frame);
        getView().addSubview(containerView);
        containerView.setAutoresizingMask(UIViewAutoresizing.FlexibleLeftMargin.set(UIViewAutoresizing.FlexibleRightMargin));

        // create the initial image view
        frame = new CGRect(0.0, 0.0, imageWidth, imageHeight);
        mainView = new UIImageView(frame);
        mainView.setImage(UIImage.create("scene1.jpg"));
        containerView.addSubview(mainView);

        // create the alternate image view (to transition between), we don't add
        // it as a subview yet
        CGRect imageFrame = new CGRect(0.0, 0.0, imageWidth, imageHeight);
        flipToView = new UIImageView(imageFrame);
        flipToView.setImage(UIImage.create("scene2.jpg"));
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
        getView().setBackgroundColor(UIColor.black());
        getView().addSubview(toolBar);
    }

    // called after this controller's view will appear
    @Override
    public void viewWillAppear (boolean animated) {
        super.viewWillAppear(animated);

        // for aesthetic reasons (the background is black), make the nav bar
        // black for this particular page
        getNavigationController().getNavigationBar().setTintColor(UIColor.black());
    }
}
