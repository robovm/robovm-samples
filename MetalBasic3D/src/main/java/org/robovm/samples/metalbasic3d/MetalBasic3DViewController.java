/*
 * Copyright (C) 2015 RoboVM AB
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
 * Portions of this code is based on Apple Inc's MetalBasic3D sample (v1.0)
 * which is copyright (C) 2014 Apple Inc.
 */
package org.robovm.samples.metalbasic3d;

import org.robovm.apple.coreanimation.CAAnimation;
import org.robovm.apple.coreanimation.CADisplayLink;
import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.foundation.NSCoder;
import org.robovm.apple.foundation.NSRunLoop;
import org.robovm.apple.foundation.NSRunLoopMode;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.Selector;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.Method;

@CustomClass("MetalBasic3DViewController")
public class MetalBasic3DViewController extends UIViewController {
    // app control
    private CADisplayLink timer;
    // boolean to determine if the first draw has occured
    boolean firstDrawOccurred;
    double timeSinceLastDrawPreviousTime;
    // pause/resume
    boolean gameLoopPaused;
    // our renderer instance
    private MetalBasic3DRenderer renderer;
    private MetalBasic3DViewControllerDelegate delegate;
    private double timeSinceLastDraw;
    private long interval;

    public double getTimeSinceLastDraw() {
        return timeSinceLastDraw;
    }
    
    private void initCommon() {
        renderer = new MetalBasic3DRenderer();
        delegate = renderer;
        UIApplication.Notifications.observeDidEnterBackground(this::pause);
        UIApplication.Notifications.observeWillEnterForeground(this::resume);
        interval = 1;
    }

    // called when loaded from nib
    @Override
    protected long init(String nibNameOrNil, NSBundle nibBundleOrNil) {
        long self = super.init(nibNameOrNil, nibBundleOrNil);
        if (self != 0) {
            initCommon();
        }
        return self;
    }

    // called when loaded from storyboard
    @Override
    protected long init(NSCoder aDecoder) {
        long self = super.init(aDecoder);
        if (self != 0) {
            initCommon();
        }
        return self;
    }

    public void setDelegate(MetalBasic3DViewControllerDelegate delegate) {
        this.delegate = delegate;
    }
    
    private void dispatchGameLoop() {
        // create a game loop timer using a display link
        timer = UIScreen.getMainScreen().getDisplayLink(this, Selector.register("gameloop"));
        timer.setFrameInterval(interval);
        timer.addToRunLoop(NSRunLoop.getMain(), NSRunLoopMode.Default);
    }

    // the main game loop called by the timer above
    @Method
    private void gameloop() {
        // tell our delegate to update itself here.
        delegate.update(this);

        if (!firstDrawOccurred) {
            // set up timing data for display since this is the first time
            // through this loop
            timeSinceLastDraw = 0.0;
            timeSinceLastDrawPreviousTime = CAAnimation.getCurrentMediaTime();
            firstDrawOccurred = true;
        } else {
            // figure out the time since we last we drew
            double currentTime = CAAnimation.getCurrentMediaTime();

            timeSinceLastDraw = currentTime - timeSinceLastDrawPreviousTime;

            // keep track of the time interval between draws
            timeSinceLastDrawPreviousTime = currentTime;
        }

        // display (render)

        // call the display method directly on the render view (setNeedsDisplay:
        // has been disabled in the renderview by default)
        ((MetalBasic3DView) getView()).display();
    }

    private void stopGameLoop() {
        if (timer != null) {
            timer.invalidate();
        }
    }

    private void pause() {

    }

    private void resume() {

    }

    public void setPaused(boolean pause) {
        if (gameLoopPaused == pause) {
            return;
        }

        if (timer != null) {
            // inform the delegate we are about to pause
            delegate.willPause(this, pause);

            if (pause == true) {
                gameLoopPaused = pause;
                timer.setPaused(true);

                // ask the view to release textures until its resumed
                ((MetalBasic3DView) getView()).releaseTextures();
            } else {
                gameLoopPaused = pause;
                timer.setPaused(false);
            }
        }
    }

    public boolean isPaused() {
        return gameLoopPaused;
    }

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        MetalBasic3DView renderView = (MetalBasic3DView) this.getView();
        renderView.setDelegate(renderer);;

        // load all renderer assets before starting game loop
        renderer.configure(renderView);
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);
        // run the game loop
        dispatchGameLoop();
    }

    @Override
    public void viewWillDisappear(boolean animated) {
        super.viewWillDisappear(animated);
        // end the gameloop
        stopGameLoop();
    }
}
