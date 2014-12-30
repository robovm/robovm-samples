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
 * Portions of this code is based on Apple Inc's Adventure sample (v1.3)
 * which is copyright (C) 2013-2014 Apple Inc.
 */

package org.robovm.samples.adventure;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.spritekit.SKSceneScaleMode;
import org.robovm.apple.spritekit.SKView;
import org.robovm.apple.uikit.UIActivityIndicatorView;
import org.robovm.apple.uikit.UIActivityIndicatorViewStyle;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIDevice;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIInterfaceOrientationMask;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIUserInterfaceIdiom;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewAnimationOptions;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.samples.adventure.scene.APAAdventureScene;
import org.robovm.samples.adventure.scene.APAAdventureScene.APAHeroType;

public class APAViewController extends UIViewController {
    // Use this line to show debug info in the Sprite Kit view:
    private static final boolean SHOW_DEBUG_INFO = true;

    private final SKView skView;
    private final UIImageView gameLogo;
    private final UIActivityIndicatorView loadingProgressIndicator;
    private final UIButton archerButton;
    private final UIButton warriorButton;
    private APAAdventureScene scene;

    public APAViewController () {
        UIView view = getView();
        view.setBackgroundColor(UIColor.white());

        skView = new SKView();
        skView.setFrame(UIScreen.getMainScreen().getApplicationFrame());
        view.addSubview(skView);

        gameLogo = new UIImageView(new CGRect(97, -10, 375, 220));
        gameLogo.setImage(UIImage.create("logo.png"));
        skView.addSubview(gameLogo);

        archerButton = new UIButton(new CGRect(29, 238, 186, 38));
        archerButton.setImage(UIImage.create("button_archer.png"), UIControlState.Normal);
        archerButton.setTitleColor(UIColor.fromRGBA(0.196, 0.309, 0.521, 1), UIControlState.Normal);
        archerButton.setTitleShadowColor(UIColor.fromWhiteAlpha(0.5, 1), UIControlState.Normal);
        archerButton.setTitleColor(UIColor.white(), UIControlState.Highlighted);
        archerButton.addOnTouchUpInsideListener(new UIControl.OnTouchUpInsideListener() {
            @Override
            public void onTouchUpInside (UIControl control, UIEvent event) {
                startGame(APAHeroType.Archer);
            }
        });
        skView.addSubview(archerButton);

        warriorButton = new UIButton(new CGRect(353, 238, 186, 38));
        warriorButton.setImage(UIImage.create("button_warrior.png"), UIControlState.Normal);
        warriorButton.setTitleColor(UIColor.fromRGBA(0.196, 0.31, 0.522, 1), UIControlState.Normal);
        warriorButton.setTitleShadowColor(UIColor.fromWhiteAlpha(0.5, 1), UIControlState.Normal);
        warriorButton.setTitleColor(UIColor.white(), UIControlState.Highlighted);
        warriorButton.addOnTouchUpInsideListener(new UIControl.OnTouchUpInsideListener() {
            @Override
            public void onTouchUpInside (UIControl control, UIEvent event) {
                startGame(APAHeroType.Warrior);
            }
        });
        skView.addSubview(warriorButton);

        loadingProgressIndicator = new UIActivityIndicatorView(UIActivityIndicatorViewStyle.Gray);
        loadingProgressIndicator.setFrame(new CGRect(274, 195, 20, 20));
        skView.addSubview(loadingProgressIndicator);
    }

    @Override
    public void viewWillAppear (boolean animated) {
        // Start the progress indicator animation.
        loadingProgressIndicator.startAnimating();

        CGSize viewSize = getView().getBounds().getSize();

        // On iPhone/iPod touch we want to see a similar amount of the scene as on iPad.
        // So, we set the size of the scene to be double the size of the view, which is
        // the whole screen, 3.5- or 4- inch. This effectively scales the scene to 50%.
        if (UIDevice.getCurrentDevice().getUserInterfaceIdiom() == UIUserInterfaceIdiom.Phone) {
            viewSize.setHeight(viewSize.getHeight() * 2);
            viewSize.setWidth(viewSize.getWidth() * 2);
        }
        scene = new APAAdventureScene(viewSize);

        // Load the shared assets of the scene.
        scene.loadSceneAssets(new Runnable() {
            @Override
            public void run () {
                scene.setup();

                scene.setScaleMode(SKSceneScaleMode.AspectFill);

                scene.configureGameControllers();

                loadingProgressIndicator.stopAnimating();
                loadingProgressIndicator.setHidden(true);

                skView.presentScene(scene);

                UIView.animate(2, 0, UIViewAnimationOptions.CurveEaseInOut, new Runnable() {
                    @Override
                    public void run () {
                        archerButton.setAlpha(1);
                        warriorButton.setAlpha(1);
                    }
                }, null);
            }
        });

        if (SHOW_DEBUG_INFO) {
            // Show debug information.
            skView.setShowsFPS(true);
            skView.setShowsDrawCount(true);
            skView.setShowsNodeCount(true);
        }
    }

    @Override
    public boolean prefersStatusBarHidden () {
        return true;
    }

    @Override
    public boolean shouldAutorotate () {
        return true;
    }

    @Override
    public UIInterfaceOrientationMask getSupportedInterfaceOrientations () {
        return UIInterfaceOrientationMask.Landscape;
    }

    private void hideUIElements (boolean shouldHide, boolean shouldAnimate) {
        final double alpha = shouldHide ? 0 : 1;

        if (shouldAnimate) {
            UIView.animate(2.0, 0, UIViewAnimationOptions.CurveEaseInOut, new Runnable() {
                @Override
                public void run () {
                    gameLogo.setAlpha(alpha);
                    archerButton.setAlpha(alpha);
                    warriorButton.setAlpha(alpha);
                }
            }, null);
        } else {
            gameLogo.setAlpha(alpha);
            archerButton.setAlpha(alpha);
            warriorButton.setAlpha(alpha);
        }
    }

    private void startGame (APAHeroType type) {
        hideUIElements(true, true);
        scene.setDefaultPlayerHeroType(type);
        scene.startLevel();
    }
}
