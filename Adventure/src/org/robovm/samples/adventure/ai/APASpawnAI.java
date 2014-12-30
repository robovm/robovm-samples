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

package org.robovm.samples.adventure.ai;

import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.spritekit.SKNode;
import org.robovm.samples.adventure.scene.APAMultiplayerLayeredCharacterScene;
import org.robovm.samples.adventure.sprites.APACave;
import org.robovm.samples.adventure.sprites.APACharacter;
import org.robovm.samples.adventure.util.APAUtils;

public class APASpawnAI extends APAArtificialIntelligence {
    private static final int MINIMUM_HERO_DISTANCE = 2048;

    public APASpawnAI (APACharacter character, APACharacter target) {
        super(character, target);
    }

    @Override
    public void update (double timeInterval) {
        APACave cave = (APACave)character;

        if (cave.health <= 0.0) {
            return;
        }

        APAMultiplayerLayeredCharacterScene scene = cave.getCharacterScene();

        double closestHeroDistance = MINIMUM_HERO_DISTANCE;
        CGPoint closestHeroPosition = CGPoint.Zero();

        CGPoint cavePosition = cave.getPosition();

        for (SKNode hero : scene.getHeroes()) {
            CGPoint heroPosition = hero.getPosition();
            double distance = APAUtils.getDistanceBetweenPoints(cavePosition, heroPosition);
            if (distance < closestHeroDistance) {
                closestHeroDistance = distance;
                closestHeroPosition = heroPosition;
            }
        }

        double distScale = closestHeroDistance / MINIMUM_HERO_DISTANCE;

        // Generate goblins more quickly if the closest hero is getting closer.
        cave.timeUntilNextGenerate -= timeInterval;

        // Either time to generate or the hero is so close we need to respond ASAP!
        int goblinCount = cave.activeGoblins.size();
        if (goblinCount < 1 || cave.timeUntilNextGenerate <= 0.0 || (distScale < 0.35 && cave.timeUntilNextGenerate > 5.0)) {
            if (goblinCount < 1
                || (goblinCount < 4 && closestHeroPosition.equalToPoint(CGPoint.Zero()) && scene.canSee(closestHeroPosition,
                    cave.getPosition()))) {
                cave.generate();
            }
            cave.timeUntilNextGenerate = 4.0 * distScale;
        }
    }
}
