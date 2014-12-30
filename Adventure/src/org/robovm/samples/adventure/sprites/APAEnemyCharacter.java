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
import org.robovm.apple.spritekit.SKSpriteNode;
import org.robovm.apple.spritekit.SKTexture;
import org.robovm.samples.adventure.ai.APAArtificialIntelligence;

public abstract class APAEnemyCharacter extends APACharacter {
    APAArtificialIntelligence intelligence;

    public APAEnemyCharacter (NSArray<SKSpriteNode> sprites, CGPoint position, double offset) {
        super(sprites, position, offset);
    }

    public APAEnemyCharacter (SKTexture texture, CGPoint position) {
        super(texture, position);
    }

    @Override
    public void update (double interval) {
        super.update(interval);
        intelligence.update(interval);
    }

    @Override
    void animationDidComplete (APAAnimationState animationState) {
        if (animationState == APAAnimationState.Attack) {
            // Attacking hero should apply same damage as collision with hero, so simply
            // tell the target that we collided with it.
            intelligence.getTarget().collidedWith(getPhysicsBody());
        }
    }
}
