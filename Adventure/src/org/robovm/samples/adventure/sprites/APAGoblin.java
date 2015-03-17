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
import org.robovm.apple.spritekit.SKSpriteNode;
import org.robovm.apple.spritekit.SKTexture;
import org.robovm.apple.spritekit.SKTextureAtlas;
import org.robovm.apple.uikit.UIColor;
import org.robovm.samples.adventure.ai.APAChaseAI;
import org.robovm.samples.adventure.scene.APAMultiplayerLayeredCharacterScene.APAWorldLayer;
import org.robovm.samples.adventure.util.APAUtils;

public class APAGoblin extends APAEnemyCharacter {
    private static final double MINIMUM_SIZE = 0.5;
    private static final double SIZE_VARIANCE = 0.35;
    private static final double COLLISION_RADIUS = 10;
    private static final int ATTACK_FRAMES = 33;
    private static final int DEATH_FRAMES = 31;
    private static final int GET_HIT_FRAMES = 25;

    private static SKEmitterNode sharedDamageEmitter;
    private static SKAction sharedDamageAction;
    private static NSArray<SKTexture> sharedIdleAnimationFrames;
    private static NSArray<SKTexture> sharedWalkAnimationFrames;
    private static NSArray<SKTexture> sharedAttackAnimationFrames;
    private static NSArray<SKTexture> sharedGetHitAnimationFrames;
    private static NSArray<SKTexture> sharedDeathAnimationFrames;
    private static SKSpriteNode sharedDeathSplort;

    private APACave cave;

    public APAGoblin(CGPoint position) {
        super(SKTextureAtlas.create("Goblin/Goblin_Idle").getTexture("goblin_idle_0001.png"), position);

        movementSpeed = MOVEMENT_SPEED * Math.random(); // set a random movement
                                                        // speed
        setScale(MINIMUM_SIZE + (Math.random() * SIZE_VARIANCE)); // and a
                                                                  // random
                                                                  // goblin size
        setZPosition(-0.25);
        setName("Enemy");

        // Make it AWARE!
        intelligence = new APAChaseAI(this, null);
    }

    @Override
    void configurePhysicsBody() {
        SKPhysicsBody physicsBody = SKPhysicsBody.createCircle(COLLISION_RADIUS);

// Our object type for collisions.
        physicsBody.setCategoryBitMask(APAColliderType.GoblinOrBoss);

// Collides with these objects.
        physicsBody.setCollisionBitMask(APAColliderType.GoblinOrBoss | APAColliderType.Hero
                | APAColliderType.Projectile
                | APAColliderType.Wall | APAColliderType.Cave);

        // We want notifications for colliding with these objects.
        physicsBody.setContactTestBitMask(APAColliderType.Projectile);

        setPhysicsBody(physicsBody);
    }

    @Override
    void reset() {
        super.reset();

        setAlpha(1.0);
        removeAllChildren();

        configurePhysicsBody();
    }

    @Override
    void animationDidComplete(APAAnimationState animationState) {
        super.animationDidComplete(animationState);

        switch (animationState) {
        case Death:
            removeAllActions();
            runAction(SKAction.sequence(new NSArray<SKAction>(SKAction.wait(0.75), SKAction.fadeOut(1.0), SKAction
                    .runBlock(new Runnable() {
                        @Override
                        public void run() {
                            removeFromParent();
                            cave.recycle(APAGoblin.this);
                        }
                    }))));
            break;
        default:
            break;
        }
    }

    @Override
    public void collidedWith(SKPhysicsBody other) {
        if (dying) {
            return;
        }

        if ((other.getCategoryBitMask() & APAColliderType.Projectile) == APAColliderType.Projectile) {
            // Apply random damage of either 100% or 50%.
            requestedAnimation = APAAnimationState.GetHit;
            double damage = 100.0;
            if (Math.random() < 0.5) {
                damage = 50.0;
            }

            boolean killed = applyDamage(damage, other.getNode());
            if (killed) {
                getCharacterScene().addToScore(10, other.getNode());
            }
        }
    }

    @Override
    public void performDeath() {
        removeAllActions();

        SKSpriteNode splort = (SKSpriteNode) sharedDeathSplort.copy();
        splort.setZPosition(-1.0);
        splort.setZRotation(Math.random() * Math.PI);
        splort.setPosition(getPosition());
        splort.setAlpha(0.5);
        getCharacterScene().addNode(splort, APAWorldLayer.Ground);
        splort.runAction(SKAction.fadeOut(10.0));

        super.performDeath();

        SKPhysicsBody physicsBody = getPhysicsBody();
        physicsBody.setCollisionBitMask(0);
        physicsBody.setContactTestBitMask(0);
        physicsBody.setCategoryBitMask(0);
        setPhysicsBody(null);
    }

    public static void loadSharedAssets() {
        // Load only once
        if (sharedDamageEmitter == null) {
            SKTextureAtlas atlas = SKTextureAtlas.create("Environment");

            sharedIdleAnimationFrames = APAUtils
                    .loadFramesFromAtlas("Goblin/Goblin_Idle", "goblin_idle_", DEFAULT_NUMBER_OF_IDLE_FRAMES);
            sharedWalkAnimationFrames = APAUtils
                    .loadFramesFromAtlas("Goblin/Goblin_Walk", "goblin_walk_", DEFAULT_NUMBER_OF_WALK_FRAMES);
            sharedAttackAnimationFrames = APAUtils.loadFramesFromAtlas("Goblin/Goblin_Attack", "goblin_attack_",
                    ATTACK_FRAMES);
            sharedGetHitAnimationFrames = APAUtils.loadFramesFromAtlas("Goblin/Goblin_GetHit", "goblin_getHit_",
                    GET_HIT_FRAMES);
            sharedDeathAnimationFrames = APAUtils.loadFramesFromAtlas("Goblin/Goblin_Death", "goblin_death_",
                    DEATH_FRAMES);
            sharedDamageEmitter = APAUtils.getEmitterNodeByName("Damage");
            sharedDeathSplort = SKSpriteNode.create(atlas.getTexture("minionSplort.png"));
            sharedDamageAction = SKAction.sequence(new NSArray<SKAction>(SKAction.colorize(UIColor.white(), 1.0, 0.0),
                    SKAction
                            .wait(0.75), SKAction.colorize(0.0, 0.1)));

        }
    }

    public void setCave(APACave cave) {
        this.cave = cave;
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
