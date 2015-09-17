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

package org.robovm.samples.adventure.sprites;

import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.spritekit.SKAction;
import org.robovm.apple.spritekit.SKEmitterNode;
import org.robovm.apple.spritekit.SKPhysicsBody;
import org.robovm.apple.spritekit.SKTexture;
import org.robovm.apple.spritekit.SKTextureAtlas;
import org.robovm.apple.uikit.UIColor;
import org.robovm.samples.adventure.ai.APAChaseAI;
import org.robovm.samples.adventure.util.APAUtils;

public class APABoss extends APAEnemyCharacter {
    private static final int WALK_FRAMES = 35;
    private static final int IDLE_FRAMES = 32;
    private static final int ATTACK_FRAMES = 42;
    private static final int DEATH_FRAMES = 45;
    private static final int GET_HIT_FRAMES = 22;

    private static final int COLLISION_RADIUS = 40;
    private static final int CHASE_RADIUS = COLLISION_RADIUS * 4;

    private static SKEmitterNode sharedDamageEmitter;
    private static SKAction sharedDamageAction;
    private static NSArray<SKTexture> sharedIdleAnimationFrames;
    private static NSArray<SKTexture> sharedWalkAnimationFrames;
    private static NSArray<SKTexture> sharedAttackAnimationFrames;
    private static NSArray<SKTexture> sharedGetHitAnimationFrames;
    private static NSArray<SKTexture> sharedDeathAnimationFrames;

    public APABoss(CGPoint position) {
        super(new SKTextureAtlas("Boss/Boss_Idle").getTexture("boss_idle_0001.png"), position);

        movementSpeed = MOVEMENT_SPEED * 0.35f;
        animationSpeed = 1.0 / 35.0;

        setZPosition(-0.25);
        setName("Boss");

        attacking = false;

        // Make it AWARE!
        APAChaseAI intelligence = new APAChaseAI(this, null);
        intelligence.setChaseRadius(CHASE_RADIUS);
        intelligence.setMaxAlertRadius(CHASE_RADIUS * 4.0);
        this.intelligence = intelligence;
    }

    @Override
    void configurePhysicsBody() {
        SKPhysicsBody physicsBody = SKPhysicsBody.createCircle(COLLISION_RADIUS);

        // Our object type for collisions.
        physicsBody.setCategoryBitMask(APAColliderType.GoblinOrBoss);

        // Collides with these objects.
        physicsBody.setCollisionBitMask(APAColliderType.GoblinOrBoss | APAColliderType.Hero
                | APAColliderType.Projectile
                | APAColliderType.Wall);

        // We want notifications for colliding with these objects.
        physicsBody.setContactTestBitMask(APAColliderType.Projectile);

        setPhysicsBody(physicsBody);
    }

    @Override
    void animationDidComplete(APAAnimationState animationState) {
        super.animationDidComplete(animationState);
        if (animationState == APAAnimationState.Death) {
            // In a real game, you'd complete the level here, maybe as shown by
            // commented code below.
            removeAllActions();
            runAction(SKAction.sequence(new NSArray<SKAction>(SKAction.wait(3.0), SKAction.fadeOut(2.0), SKAction
                    .removeFromParent()
                    /*
                     * SKAction.runBlock(new Runnable() {
                     * 
                     * @Override public void run () {
                     * getCharacterScene().gameOver(); } })
                     */
                    )));
        }
    }

    @Override
    public void collidedWith(SKPhysicsBody other) {
        if (dying) {
            return;
        }

        if ((other.getCategoryBitMask() & APAColliderType.Projectile) == APAColliderType.Projectile) {
            requestedAnimation = APAAnimationState.GetHit;
            double damage = 2.0;
            boolean killed = applyDamage(damage, other.getNode());
            if (killed) {
                getCharacterScene().addToScore(100, other.getNode());
            }
        }
    }

    @Override
    public void performDeath() {
        removeAllActions();

        super.performDeath();
    }

    public static void loadSharedAssets() {
        // Load only once.
        if (sharedDamageEmitter == null) {
            sharedIdleAnimationFrames = APAUtils.loadFramesFromAtlas("Boss/Boss_Idle", "boss_idle_", IDLE_FRAMES);
            sharedWalkAnimationFrames = APAUtils.loadFramesFromAtlas("Boss/Boss_Walk", "boss_walk_", WALK_FRAMES);
            sharedAttackAnimationFrames = APAUtils.loadFramesFromAtlas("Boss/Boss_Attack", "boss_attack_",
                    ATTACK_FRAMES);
            sharedGetHitAnimationFrames = APAUtils.loadFramesFromAtlas("Boss/Boss_GetHit", "boss_getHit_",
                    GET_HIT_FRAMES);
            sharedDeathAnimationFrames = APAUtils.loadFramesFromAtlas("Boss/Boss_Death", "boss_death_", DEATH_FRAMES);

            sharedDamageEmitter = APAUtils.getEmitterNodeByName("BossDamage");
            sharedDamageAction = SKAction.sequence(new NSArray<SKAction>(SKAction.colorize(UIColor.white(), 1.0, 0.0),
                    SKAction
                            .wait(0.5), SKAction.colorize(0.0, 0.1)));
        }
    }

    @Override
    NSArray<SKTexture> getIdleAnimationFrames() {
        return sharedIdleAnimationFrames;
    }

    @Override
    NSArray<SKTexture> getWalkAnimationFrames() {
        return sharedWalkAnimationFrames;
    }

    @Override
    NSArray<SKTexture> getAttackAnimationFrames() {
        return sharedAttackAnimationFrames;
    }

    @Override
    NSArray<SKTexture> getHitAnimationFrames() {
        return sharedGetHitAnimationFrames;
    }

    @Override
    NSArray<SKTexture> getDeathAnimationFrames() {
        return sharedDeathAnimationFrames;
    }

    @Override
    SKEmitterNode getDamageEmitter() {
        return sharedDamageEmitter;
    }

    @Override
    SKAction getDamageAction() {
        return sharedDamageAction;
    }
}
