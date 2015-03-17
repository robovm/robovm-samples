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
import org.robovm.apple.foundation.NSMutableArray;
import org.robovm.apple.spritekit.SKAction;
import org.robovm.apple.spritekit.SKEmitterNode;
import org.robovm.apple.spritekit.SKNode;
import org.robovm.apple.spritekit.SKPhysicsBody;
import org.robovm.apple.spritekit.SKSpriteNode;
import org.robovm.apple.spritekit.SKTexture;
import org.robovm.apple.spritekit.SKTextureAtlas;
import org.robovm.apple.uikit.UIColor;
import org.robovm.samples.adventure.ai.APASpawnAI;
import org.robovm.samples.adventure.scene.APAMultiplayerLayeredCharacterScene;
import org.robovm.samples.adventure.scene.APAMultiplayerLayeredCharacterScene.APAWorldLayer;
import org.robovm.samples.adventure.util.APAUtils;

public class APACave extends APAEnemyCharacter {
    private static final int COLLISION_RADIUS = 90;
    private static final int CAVE_CAPACITY = 50;

    private static SKNode sharedCaveBase;
    private static SKNode sharedCaveTop;
    private static SKSpriteNode sharedDeathSplort;
    private static SKEmitterNode sharedDamageEmitter;
    private static SKEmitterNode sharedDeathEmitter;
    private static SKAction sharedDamageAction;

    public NSArray<APAGoblin> activeGoblins = new NSMutableArray<>();
    public NSArray<APAGoblin> inactiveGoblins = new NSMutableArray<>();
    private SKEmitterNode smokeEmitter;

    public double timeUntilNextGenerate;

    public APACave (CGPoint position) {
        super(new NSArray<SKSpriteNode>((SKSpriteNode)sharedCaveBase.copy(), (SKSpriteNode)sharedCaveTop.copy()), position, 50.0);
        timeUntilNextGenerate = 5.0 + Math.random() * 5.0;

        for (int i = 0; i < CAVE_CAPACITY; i++) {
            APAGoblin goblin = new APAGoblin(getPosition());
            goblin.setCave(this);
            inactiveGoblins.add(goblin);
        }

        movementSpeed = 0.0;

        pickRandomFacing(position);

        setName("GoblinCave");

        // Make it AWARE!
        intelligence = new APASpawnAI(this, null);
    }

    private void pickRandomFacing (CGPoint position) {
        APAMultiplayerLayeredCharacterScene scene = getCharacterScene();

        // Pick best random facing from 8 test rays.
        double maxDoorCanSee = 0.0;
        double preferredZRotation = 0.0;
        for (int i = 0; i < 8; i++) {
            double testZ = Math.random() * (Math.PI * 2.0);
            CGPoint pos2 = new CGPoint(-Math.sin(testZ) * 1024 + position.getX(), Math.cos(testZ) * 1024 + position.getY());
            double dist = scene.getDistanceToWall(position, pos2);
            if (dist > maxDoorCanSee) {
                maxDoorCanSee = dist;
                preferredZRotation = testZ;
            }
        }

        setZRotation(preferredZRotation);
    }

    @Override
    void configurePhysicsBody () {
        SKPhysicsBody physicsBody = SKPhysicsBody.createCircle(COLLISION_RADIUS);
        physicsBody.setDynamic(false);

        animated = false;
        setZPosition(-0.85);

        // Our object type for collisions.
        physicsBody.setCategoryBitMask(APAColliderType.Cave);

        // Collides with these objects.
        physicsBody.setCollisionBitMask(APAColliderType.Projectile | APAColliderType.Hero);

        // We want notifications for colliding with these objects.
        physicsBody.setCollisionBitMask(APAColliderType.Projectile);

        setPhysicsBody(physicsBody);
    }

    @Override
    void reset () {
        super.reset();

        animated = false;
    }

    @Override
    public void collidedWith (SKPhysicsBody other) {
        if (health > 0.0) {
            if ((other.getCategoryBitMask() & APAColliderType.Projectile) == APAColliderType.Projectile) {
                double damage = 10.0;
                boolean killed = applyDamage(damage, other.getNode());
                if (killed) {
                    getCharacterScene().addToScore(25, other.getNode());
                }
            }
        }
    }

    @Override
    public boolean applyDamage (double damage) {
        boolean killed = super.applyDamage(damage);
        if (killed) {
            return true;
        }

        // Show damage.
        updateSmoke();

        // Show damage on parallax stacks.
        for (SKNode node : getChildren()) {
            node.runAction(getDamageAction());
        }
        return false;
    }

    @Override
    public void performDeath () {
        super.performDeath();

        SKNode splort = (SKNode)sharedDeathSplort.copy();
        splort.setZPosition(-1.0);
        splort.setZPosition(-1.0);
        splort.setZRotation(virtualZRotation);
        splort.setPosition(getPosition());
        splort.setAlpha(0.1);
        splort.runAction(SKAction.fadeAlphaTo(1.0, 0.5));

        APAMultiplayerLayeredCharacterScene scene = getCharacterScene();

        scene.addNode(splort, APAWorldLayer.BelowCharacter);

        runAction(SKAction.sequence(new NSArray<SKAction>(SKAction.fadeAlphaTo(0.0, 0.5), SKAction.removeFromParent())));

        smokeEmitter.runAction(SKAction.sequence(new NSArray<SKAction>(SKAction.wait(2.0), SKAction.runBlock(new Runnable() {
            @Override
            public void run () {
                smokeEmitter.setParticleBirthRate(2.0);
            }
        }), SKAction.wait(2.0), SKAction.runBlock(new Runnable() {
            @Override
            public void run () {
                smokeEmitter.setParticleBirthRate(0.0);
            }
        }), SKAction.wait(10.0), SKAction.fadeAlphaTo(0.0, 0.5), SKAction.removeFromParent())));
        inactiveGoblins.clear();
    }

    private void updateSmoke () {
        // Add smoke if health is < 75.
        if (health > 75.0 || smokeEmitter != null) {
            return;
        }

        smokeEmitter = (SKEmitterNode)sharedDeathEmitter.copy();
        smokeEmitter.setPosition(getPosition());
        smokeEmitter.setZPosition(-0.8);
        APAMultiplayerLayeredCharacterScene scene = getCharacterScene();
        scene.addNode(smokeEmitter, APAWorldLayer.AboveCharacter);
    }

    @Override
    public void update (double interval) {
        super.update(interval);// this will update the SpawnAI

        // Update our goblins.
        for (APAGoblin goblin : activeGoblins) {
            goblin.update(interval);
        }
    }

    public void stopGoblinsFromTargettingHero (APACharacter target) {
        for (APAGoblin goblin : activeGoblins) {
            goblin.intelligence.clearTarget(target);
        }
    }

    private static int globalCap;
    private static int globalAllocation;

    public static void setGlobalGoblinCap (int globalCap) {
        APACave.globalCap = globalCap;
    }

    public void generate () {
        if (globalCap > 0 && globalAllocation >= globalCap) {
            return;
        }

        APACharacter object = inactiveGoblins.last();
        if (object == null) {
            return;
        }

        double offset = COLLISION_RADIUS * 0.75;
        double rot = APAUtils.polarAdjust(getVirtualZRotation());
        object.setPosition(APAUtils.getPointByAddingPoints(getPosition(), new CGPoint(Math.cos(rot) * offset, Math.sin(rot)
            * offset)));

        APAMultiplayerLayeredCharacterScene scene = getCharacterScene();
        object.addToScene(scene);

        object.setZPosition(-1.0);

        object.fadeIn(0.5);

        inactiveGoblins.remove(object);
        activeGoblins.add((APAGoblin)object);
        globalAllocation++;
    }

    public void recycle (APAGoblin goblin) {
        goblin.reset();
        activeGoblins.remove(goblin);
        inactiveGoblins.add(goblin);

        globalAllocation--;
    }

    public static void loadSharedAssets () {
        // Load only once
        if (sharedCaveBase == null) {
            SKTextureAtlas atlas = SKTextureAtlas.create("Environment");

            SKEmitterNode fire = APAUtils.getEmitterNodeByName("CaveFire");
            fire.setZPosition(1);

            SKEmitterNode smoke = APAUtils.getEmitterNodeByName("CaveFireSmoke");

            SKNode torch = new SKNode();
            torch.addChild(fire);
            torch.addChild(smoke);

            sharedCaveBase = SKSpriteNode.create(atlas.getTexture("cave_base.png"));

            // Add two torches either side of the entrance.
            torch.setPosition(new CGPoint(83, 83));
            sharedCaveBase.addChild(torch);
            SKNode torchB = (SKNode)torch.copy();
            torchB.setPosition(new CGPoint(-83, 83));
            sharedCaveBase.addChild(torchB);

            sharedCaveTop = SKSpriteNode.create(atlas.getTexture("cave_top.png"));

            sharedDeathSplort = SKSpriteNode.create(atlas.getTexture("cave_destroyed.png"));

            sharedDamageEmitter = APAUtils.getEmitterNodeByName("CaveDamage");
            sharedDeathEmitter = APAUtils.getEmitterNodeByName("CaveDeathSmoke");

            sharedDamageAction = SKAction.sequence(new NSArray<SKAction>(SKAction.colorize(UIColor.red(), 1.0, 0.0), SKAction
                .wait(0.25), SKAction.colorize(0.0, 0.1)));
        }
    }

    @Override
    NSArray<SKTexture> getIdleAnimationFrames () {
        return null;
    }

    @Override
    NSArray<SKTexture> getWalkAnimationFrames () {
        return null;
    }

    @Override
    NSArray<SKTexture> getAttackAnimationFrames () {
        return null;
    }

    @Override
    NSArray<SKTexture> getHitAnimationFrames () {
        return null;
    }

    @Override
    NSArray<SKTexture> getDeathAnimationFrames () {
        return null;
    }

    @Override
    SKEmitterNode getDamageEmitter () {
        return sharedDamageEmitter;
    }

    @Override
    SKAction getDamageAction () {
        return sharedDamageAction;
    }
}
