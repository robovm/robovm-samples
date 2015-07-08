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
import org.robovm.apple.foundation.NSMutableDictionary;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.spritekit.SKAction;
import org.robovm.apple.spritekit.SKEmitterNode;
import org.robovm.apple.spritekit.SKPhysicsBody;
import org.robovm.apple.spritekit.SKSpriteNode;
import org.robovm.apple.spritekit.SKTexture;
import org.robovm.samples.adventure.scene.APAMultiplayerLayeredCharacterScene;
import org.robovm.samples.adventure.scene.APAMultiplayerLayeredCharacterScene.APAWorldLayer;
import org.robovm.samples.adventure.scene.APAPlayer;
import org.robovm.samples.adventure.util.APAUtils;

public abstract class APAHeroCharacter extends APACharacter {
    public static final NSString PLAYER_KEY = new NSString("kPlayer");
    private static final double HERO_PROJECTILE_SPEED = 480;
    private static final double HERO_PROJECTILE_LIFETIME = 1.0; // 1.0 seconds until the projectile disappears
    private static final double HERO_PROJECTILE_FADEOUT_TIME = 0.6; // 0.6 seconds until the projectile starts to fade out

    static SKAction sharedProjectileSoundAction;
    static SKEmitterNode sharedDeathEmitter;
    static SKEmitterNode sharedDamageEmitter;

    private final APAPlayer player;

    public APAHeroCharacter (CGPoint position, APAPlayer player) {
        this(null, position, player);
    }

    public APAHeroCharacter (SKTexture texture, CGPoint position, APAPlayer player) {
        super(texture, position);

        this.player = player;

        // Rotate by PI radians (180 degrees) so hero faces down rather than toward wall at start of game.
        setZRotation(Math.PI);
        setZPosition(-0.25);
        setName("Hero");
    }

    @Override
    void configurePhysicsBody () {
        SKPhysicsBody physicsBody = SKPhysicsBody.createCircle(CHARACTER_COLLISION_RADIUS);

        // Our object type for collisions.
        physicsBody.setCategoryBitMask(APAColliderType.Hero);

        // Collides with these objects.
        physicsBody.setCollisionBitMask(APAColliderType.GoblinOrBoss | APAColliderType.Hero | APAColliderType.Wall
            | APAColliderType.Cave);

        // We want notifications for colliding with these objects.
        physicsBody.setContactTestBitMask(APAColliderType.GoblinOrBoss);

        setPhysicsBody(physicsBody);
    }

    @Override
    public void collidedWith (SKPhysicsBody other) {
        if ((other.getCategoryBitMask() & APAColliderType.GoblinOrBoss) == APAColliderType.GoblinOrBoss) {
            APACharacter enemy = (APACharacter)other.getNode();
            if (!enemy.dying) {
                applyDamage(5.0);
                requestedAnimation = APAAnimationState.GetHit;
            }
        }
    }

    @Override
    void animationDidComplete (APAAnimationState animationState) {
        switch (animationState) {
        case Death:
            final APAMultiplayerLayeredCharacterScene scene = getCharacterScene();

            SKEmitterNode emitter = (SKEmitterNode)sharedDeathEmitter.copy();
            emitter.setZPosition(-0.8);
            addChild(emitter);
            APAUtils.runOneShotEmitter(emitter, 4.5);

            runAction(SKAction.sequence(new NSArray<SKAction>(SKAction.wait(4.0), SKAction.run(new Runnable() {
                @Override
                public void run () {
                    scene.heroWasKilled(APAHeroCharacter.this);
                }
            }), SKAction.removeFromParent())));
            break;
        case Attack:
            fireProjectile();
            break;
        default:
            break;
        }
    }

    private void fireProjectile () {
        APAMultiplayerLayeredCharacterScene scene = getCharacterScene();

        SKSpriteNode projectile = (SKSpriteNode)getProjectile().copy();
        projectile.setPosition(getPosition());
        projectile.setZRotation(getZRotation());

        SKEmitterNode emitter = (SKEmitterNode)getProjectileEmitter().copy();
        emitter.setTargetNode(scene.getChild("world"));
        projectile.addChild(emitter);

        scene.addNode(projectile, APAWorldLayer.Character);

        double rot = getZRotation();

        projectile.runAction(SKAction.moveBy(-Math.sin(rot) * HERO_PROJECTILE_SPEED * HERO_PROJECTILE_LIFETIME, Math.cos(rot)
            * HERO_PROJECTILE_SPEED * HERO_PROJECTILE_LIFETIME, HERO_PROJECTILE_LIFETIME));
        projectile.runAction(SKAction.sequence(new NSArray<SKAction>(SKAction.wait(HERO_PROJECTILE_FADEOUT_TIME), SKAction
            .fadeOut(HERO_PROJECTILE_LIFETIME - HERO_PROJECTILE_FADEOUT_TIME), SKAction.removeFromParent())));
        projectile.runAction(sharedProjectileSoundAction);

        NSMutableDictionary<NSString, NSObject> userData = new NSMutableDictionary<>();
        userData.put(PLAYER_KEY, player);
        projectile.setUserData(userData);
    }

    /** Overridden by subclasses to return a suitable projectile.
     * @return */
    abstract SKSpriteNode getProjectile ();

    /** Overridden by subclasses to return the particle emitter to attach to the projectile.
     * @return */
    abstract SKEmitterNode getProjectileEmitter ();

    public static void loadSharedAssets () {
        // Load only once.
        if (sharedProjectileSoundAction == null) {
            sharedProjectileSoundAction = SKAction.playSound("magicmissile.caf", false);
            sharedDeathEmitter = APAUtils.getEmitterNodeByName("Death");
            sharedDamageEmitter = APAUtils.getEmitterNodeByName("Damage");
        }
    }

    public APAPlayer getPlayer () {
        return player;
    }

    @Override
    SKEmitterNode getDamageEmitter () {
        return sharedDamageEmitter;
    }
}
