/*
 * Copyright (C) 2014 RoboVM AB
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

package org.robovm.samples.adventure.scene;

import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.gamecontroller.GCController;
import org.robovm.apple.uikit.UITouch;
import org.robovm.samples.adventure.sprites.APAArcher;
import org.robovm.samples.adventure.sprites.APAHeroCharacter;
import org.robovm.samples.adventure.sprites.APAWarrior;

public class APAPlayer extends NSObject {
    public static final int START_LIVES = 3;

    public APAHeroCharacter hero;
    public Class<? extends APAHeroCharacter> heroClass;
    public boolean moveForward;
    public boolean moveLeft;
    public boolean moveRight;
    public boolean moveBack;
    public boolean fireAction;

    public CGPoint heroMoveDirection;
    public int livesLeft = START_LIVES;
    public int score;

    public GCController controller;

    public UITouch movementTouch;
    public CGPoint targetLocation;
    public boolean moveRequested;

    public APAPlayer () {
        // Pick one of the two hero classes at random.
        if (Math.random() < 0.5) {
            heroClass = APAWarrior.class;
        } else {
            heroClass = APAArcher.class;
        }
    }

}
