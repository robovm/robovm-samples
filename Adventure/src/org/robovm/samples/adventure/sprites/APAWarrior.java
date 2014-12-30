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
import org.robovm.apple.spritekit.SKAction;
import org.robovm.apple.spritekit.SKEmitterNode;
import org.robovm.apple.spritekit.SKPhysicsBody;
import org.robovm.apple.spritekit.SKSpriteNode;
import org.robovm.apple.spritekit.SKTexture;
import org.robovm.apple.spritekit.SKTextureAtlas;
import org.robovm.apple.uikit.UIColor;
import org.robovm.samples.adventure.scene.APAPlayer;
import org.robovm.samples.adventure.util.APAUtils;

public class APAWarrior extends APAHeroCharacter {
    private static final int IDLE_FRAMES = 29;
    private static final int THROW_FRAMES = 10;
    private static final int GET_HIT_FRAMES = 20;
    private static final int DEATH_FRAMES = 90;

    private static SKSpriteNode sharedProjectile;
    private static SKEmitterNode sharedProjectileEmitter;
    private static NSArray<SKTexture> sharedIdleAnimationFrames;
    private static NSArray<SKTexture> sharedWalkAnimationFrames;
    private static NSArray<SKTexture> sharedAttackAnimationFrames;
    private static NSArray<SKTexture> sharedGetHitAnimationFrames;
    private static NSArray<SKTexture> sharedDeathAnimationFrames;
    private static SKAction sharedDamageAction;

    public APAWarrior (CGPoint position, APAPlayer player) {
        super(SKTextureAtlas.create("Warrior_Idle").getTexture("warrior_idle_0001.png"), position, player);
    }

    public static void loadSharedAssets () {
        // Load only once
        if (sharedProjectile == null) {
            SKTextureAtlas atlas = SKTextureAtlas.create("Environment");

            sharedProjectile = SKSpriteNode.create(atlas.getTexture("warrior_throw_hammer.png"));
            sharedProjectile.setPhysicsBody(SKPhysicsBody.createCircle(PROJECTILE_COLLISION_RADIUS));
            sharedProjectile.setName("Projectile");
            sharedProjectile.getPhysicsBody().setCategoryBitMask(APAColliderType.Projectile);
            sharedProjectile.getPhysicsBody().setCollisionBitMask(APAColliderType.Wall);
            sharedProjectile.getPhysicsBody().setContactTestBitMask(sharedProjectile.getPhysicsBody().getCollisionBitMask());

            sharedProjectileEmitter = APAUtils.getEmitterNodeByName("WarriorProjectile");
            sharedIdleAnimationFrames = APAUtils.loadFramesFromAtlas("Warrior_Idle", "warrior_idle_", IDLE_FRAMES);
            sharedWalkAnimationFrames = APAUtils.loadFramesFromAtlas("Warrior_Walk", "warrior_walk_",
                DEFAULT_NUMBER_OF_WALK_FRAMES);
            sharedAttackAnimationFrames = APAUtils.loadFramesFromAtlas("Warrior_Attack", "warrior_attack_", THROW_FRAMES);
            sharedGetHitAnimationFrames = APAUtils.loadFramesFromAtlas("Warrior_GetHit", "warrior_getHit_", GET_HIT_FRAMES);
            sharedDeathAnimationFrames = APAUtils.loadFramesFromAtlas("Warrior_Death", "warrior_death_", DEATH_FRAMES);

            sharedDamageAction = SKAction.sequence(new NSArray<SKAction>(SKAction.colorize(UIColor.red(), 10, 0), SKAction
                .wait(0.5), SKAction.colorize(0.0, 0.25)));
        }
    }

    @Override
    NSArray<SKTexture> getIdleAnimationFrames () {
        return sharedIdleAnimationFrames;
    }

    @Override
    NSArray<SKTexture> getWalkAnimationFrames () {
        return sharedWalkAnimationFrames;
    }

    @Override
    NSArray<SKTexture> getAttackAnimationFrames () {
        return sharedAttackAnimationFrames;
    }

    @Override
    NSArray<SKTexture> getHitAnimationFrames () {
        return sharedGetHitAnimationFrames;
    }

    @Override
    NSArray<SKTexture> getDeathAnimationFrames () {
        return sharedDeathAnimationFrames;
    }

    @Override
    SKAction getDamageAction () {
        return sharedDamageAction;
    }

    @Override
    SKSpriteNode getProjectile () {
        return sharedProjectile;
    }

    @Override
    SKEmitterNode getProjectileEmitter () {
        return sharedProjectileEmitter;
    }
}
