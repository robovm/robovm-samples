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

package org.robovm.samples.adventure.sprites;

import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.spritekit.SKNode;
import org.robovm.apple.spritekit.SKSpriteNode;
import org.robovm.samples.adventure.scene.APAMultiplayerLayeredCharacterScene;
import org.robovm.samples.adventure.util.APAUtils;

public class APATree extends APAParallaxSprite {
    public static final int OPAQUE_DISTANCE = 400;

    public APATree (NSArray<SKSpriteNode> sprites, double offset) {
        super(sprites, offset);
    }

    private boolean fadeAlpha;

    public boolean isFadingAlpha () {
        return fadeAlpha;
    }

    public void setFadesAlpha (boolean fadeAlpha) {
        this.fadeAlpha = fadeAlpha;
    }

    public void updateAlpha (APAMultiplayerLayeredCharacterScene scene) {
        if (!fadeAlpha) {
            return;
        }

        double closestHeroDistance = Float.MAX_VALUE;
        // See if there are any heroes nearby.
        CGPoint ourPosition = getPosition();
        for (SKNode hero : scene.getHeroes()) {
            CGPoint theirPos = hero.getPosition();
            double distance = APAUtils.getDistanceBetweenPoints(ourPosition, theirPos);

            if (distance < closestHeroDistance) {
                closestHeroDistance = distance;
            }
        }

        if (closestHeroDistance > OPAQUE_DISTANCE) {
            // No heroes nearby.
            setAlpha(1);
        } else {
            // Adjust the alpha based on how close the hero is.
            setAlpha(0.1 + ((closestHeroDistance / OPAQUE_DISTANCE) * (closestHeroDistance / OPAQUE_DISTANCE)) * 0.9);
        }
    }
}
