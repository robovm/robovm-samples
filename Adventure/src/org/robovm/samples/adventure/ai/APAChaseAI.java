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

package org.robovm.samples.adventure.ai;

import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.samples.adventure.scene.APAMultiplayerLayeredCharacterScene;
import org.robovm.samples.adventure.sprites.APACharacter;
import org.robovm.samples.adventure.sprites.APAHeroCharacter;
import org.robovm.samples.adventure.util.APAUtils;

public class APAChaseAI extends APAArtificialIntelligence {
    private static final int ENEMY_ALERT_RADIUS = APACharacter.CHARACTER_COLLISION_RADIUS * 500;

    private double chaseRadius;
    private double maxAlertRadius;

    public APAChaseAI (APACharacter character, APACharacter target) {
        super(character, target);

        maxAlertRadius = ENEMY_ALERT_RADIUS * 2.0;
        chaseRadius = APACharacter.CHARACTER_COLLISION_RADIUS * 2.0;
    }

    @Override
    public void update (double timeInterval) {
        APACharacter ourCharacter = character;

        if (ourCharacter.dying) {
            target = null;
            return;
        }

        CGPoint position = ourCharacter.getPosition();
        APAMultiplayerLayeredCharacterScene scene = ourCharacter.getCharacterScene();
        double closestHeroDistance = Double.MAX_VALUE;

        // Find the closest living hero, if any, within our alert distance.
        for (APAHeroCharacter hero : scene.getHeroes()) {
            CGPoint heroPosition = hero.getPosition();
            double distance = APAUtils.getDistanceBetweenPoints(position, heroPosition);
            if (distance < ENEMY_ALERT_RADIUS && distance < closestHeroDistance && !hero.dying) {
                closestHeroDistance = distance;
                target = hero;
            }
        }

        // If there's no target, don't do anything.
        if (target == null) {
            return;
        }

        // Otherwise chase or attack the target, if it's near enough.
        CGPoint heroPosition = target.getPosition();

        if (closestHeroDistance > maxAlertRadius) {
            target = null;
        } else if (closestHeroDistance > chaseRadius) {
            character.moveTowards(heroPosition, timeInterval);
        } else if (closestHeroDistance < chaseRadius) {
            character.faceTo(heroPosition);
            character.performAttackAction();
        }
    }

    public void setChaseRadius (double chaseRadius) {
        this.chaseRadius = chaseRadius;
    }

    public void setMaxAlertRadius (double maxAlertRadius) {
        this.maxAlertRadius = maxAlertRadius;
    }
}
