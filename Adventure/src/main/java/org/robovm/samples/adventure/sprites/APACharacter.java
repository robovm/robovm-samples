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
import org.robovm.apple.spritekit.SKNode;
import org.robovm.apple.spritekit.SKPhysicsBody;
import org.robovm.apple.spritekit.SKSpriteNode;
import org.robovm.apple.spritekit.SKTexture;
import org.robovm.apple.spritekit.SKTextureAtlas;
import org.robovm.samples.adventure.scene.APAMultiplayerLayeredCharacterScene;
import org.robovm.samples.adventure.scene.APAMultiplayerLayeredCharacterScene.APAWorldLayer;
import org.robovm.samples.adventure.util.APAUtils;

public abstract class APACharacter extends APAParallaxSprite {

    /** Used by the move method to move a character in a given direction. */
    public enum APAMoveDirection {
        Forward, Left, Right, Back
    }

    /** The different animation states of an animated character. */
    public enum APAAnimationState {
        Idle, Walk, Attack, GetHit, Death
    }

    /** Bitmask for the different entities with physics bodies. */
    public static class APAColliderType {
        public static final int Hero = 1;
        public static final int GoblinOrBoss = 2;
        public static final int Projectile = 4;
        public static final int Wall = 8;
        public static final int Cave = 16;
    }

    public static final double MOVEMENT_SPEED = 200;
    public static final double ROTATION_SPEED = 0.06;

    public static final int CHARACTER_COLLISION_RADIUS = 40;
    public static final int PROJECTILE_COLLISION_RADIUS = 15;

    static final int DEFAULT_NUMBER_OF_WALK_FRAMES = 28;
    static final int DEFAULT_NUMBER_OF_IDLE_FRAMES = 28;

    public boolean dying;
    boolean attacking;
    public double health;
    boolean animated;
    double animationSpeed;
    double movementSpeed;

    private String activeAnimationKey;
    APAAnimationState requestedAnimation;

    private SKSpriteNode shadowBlob;

    public APACharacter (SKTexture texture, CGPoint position) {
        super(texture);
        usesParallaxEffect = false; // standard sprite - there's no parallax
        init(position);
    }

    public APACharacter (NSArray<SKSpriteNode> sprites, CGPoint position, double offset) {
        super(sprites, offset);
        init(position);
    }

    private void init (CGPoint position) {
        SKTextureAtlas atlas = new SKTextureAtlas("Environment");

        shadowBlob = new SKSpriteNode(atlas.getTexture("blobShadow.png"));
        shadowBlob.setZPosition(-1.0);

        setPosition(position);

        health = 100.0;
        movementSpeed = MOVEMENT_SPEED;
        animated = true;
        animationSpeed = 1 / 28.0;

        configurePhysicsBody();
    }

    void reset () {
        // Reset some base states (used when recycling character instances).
        health = 100;
        dying = false;
        attacking = false;
        animated = true;
        requestedAnimation = APAAnimationState.Idle;
        shadowBlob.setAlpha(1.0);
    }

    /** Overridden by subclasses to create a physics body with relevant collision settings for this character. */
    abstract void configurePhysicsBody ();

    /** Called when a requested animation has completed
     * @param animation */
    abstract void animationDidComplete (APAAnimationState animationState);

    public void performAttackAction () {
        if (attacking) {
            return;
        }
        attacking = true;
        requestedAnimation = APAAnimationState.Attack;
    }

    /** Handle a collision with another character, projectile, wall, etc
     * @param other */
    public abstract void collidedWith (SKPhysicsBody other);

    public void performDeath () {
        health = 0.0;
        dying = true;
        requestedAnimation = APAAnimationState.Death;
    }

    public boolean applyDamage (double damage, SKNode projectile) {
        return applyDamage(damage * projectile.getAlpha());
    }

    /** Apply damage and return true of death.
     * @param damage
     * @return */
    public boolean applyDamage (double damage) {
        health -= damage;

        if (health > 0) {
            APAMultiplayerLayeredCharacterScene scene = getCharacterScene();

            // Build up "one shot" particle.
            SKEmitterNode emitter = getDamageEmitter();
            if (emitter != null) {
                emitter = (SKEmitterNode)emitter.copy();
                scene.addNode(emitter, APAWorldLayer.AboveCharacter);

                emitter.setPosition(getPosition());
                APAUtils.runOneShotEmitter(emitter, 0.15);
            }

            // Show the damage.
            SKAction damageAction = getDamageAction();
            if (damageAction != null) {
                runAction(damageAction);
            }
            return false;
        }

        performDeath();

        return true;
    }

    @Override
    public void setScale (double scale) {
        super.setScale(scale);

        shadowBlob.setScale(scale);
    }

    @Override
    public void setAlpha (double alpha) {
        super.setAlpha(alpha);

        shadowBlob.setAlpha(alpha);
    }

    public void update (double interval) {
        // Shadow always follows our main sprite.
        shadowBlob.setPosition(getPosition());

        if (animated) {
            resolveRequestedAnimation();
        }
    }

    private void resolveRequestedAnimation () {
        // Determine the animation we want to play.
        String animationKey = null;
        NSArray<SKTexture> animationFrames = null;
        APAAnimationState animationState = requestedAnimation;

        switch (animationState) {
        default:
        case Idle:
            animationKey = "anim_idle";
            animationFrames = getIdleAnimationFrames();
            break;
        case Walk:
            animationKey = "anim_walk";
            animationFrames = getWalkAnimationFrames();
            break;
        case Attack:
            animationKey = "anim_attack";
            animationFrames = getAttackAnimationFrames();
            break;
        case GetHit:
            animationKey = "anim_gethit";
            animationFrames = getHitAnimationFrames();
            break;
        case Death:
            animationKey = "anim_death";
            animationFrames = getDeathAnimationFrames();
            break;
        }

        if (animationKey != null) {
            fireAnimation(animationState, animationFrames, animationKey);
        }

        requestedAnimation = dying ? APAAnimationState.Death : APAAnimationState.Idle;
    }

    private void fireAnimation (final APAAnimationState animationState, NSArray<SKTexture> frames, String key) {
        SKAction animAction = getAction(key);
        if (animAction != null | frames.size() < 1) {
            return;// we already have a running animation or there aren't any frames to animate
        }

        activeAnimationKey = key;

        runAction(SKAction.sequence(new NSArray<SKAction>(SKAction.animateFrames(frames, animationSpeed, true, false), SKAction
            .run(new Runnable() {
                @Override
                public void run () {
                    animationHasCompleted(animationState);
                }
            }))), key);
    }

    public void fadeIn (double duration) {
        // Fade in the main sprite and blob shadow.
        SKAction fadeAction = SKAction.fadeIn(duration);

        setAlpha(0.0);
        runAction(fadeAction);

        shadowBlob.setAlpha(0.0);
        shadowBlob.runAction(fadeAction);
    }

    private void animationHasCompleted (APAAnimationState animationState) {
        if (dying) {
            animated = false;
            shadowBlob.runAction(SKAction.fadeOut(1.5));
        }

        animationDidComplete(animationState);

        if (attacking) {
            attacking = false;
        }

        activeAnimationKey = null;
    }

    public void addToScene (APAMultiplayerLayeredCharacterScene scene) {
        scene.addNode(this, APAWorldLayer.Character);
        scene.addNode(shadowBlob, APAWorldLayer.BelowCharacter);
    }

    @Override
    public void removeFromParent () {
        shadowBlob.removeFromParent();
        super.removeFromParent();
    }

    public APAMultiplayerLayeredCharacterScene getCharacterScene () {
        if (getScene() instanceof APAMultiplayerLayeredCharacterScene) {
            return (APAMultiplayerLayeredCharacterScene)getScene();
        }
        return null;
    }

    public void move (APAMoveDirection direction, double timeInternal) {
        double rot = getZRotation();

        SKAction action = null;
        // Build up the movement action.
        switch (direction) {
        case Forward:
            action = SKAction.moveBy(-Math.sin(rot) * movementSpeed * timeInternal, Math.cos(rot) * movementSpeed * timeInternal,
                timeInternal);
            break;
        case Back:
            action = SKAction.moveBy(Math.sin(rot) * movementSpeed * timeInternal, -Math.cos(rot) * movementSpeed * timeInternal,
                timeInternal);
            break;
        case Left:
            action = SKAction.rotateBy(ROTATION_SPEED, timeInternal);
            break;
        case Right:
            action = SKAction.rotateBy(-ROTATION_SPEED, timeInternal);
            break;
        }

        // Play the resulting action.
        if (action != null) {
            requestedAnimation = APAAnimationState.Walk;
            runAction(action);
        }
    }

    public double faceTo (CGPoint position) {
        double ang = APAUtils.polarAdjust(APAUtils.getRadiansBetweenPoints(position, getPosition()));
        SKAction action = SKAction.rotateTo(ang, 0);
        runAction(action);
        return ang;
    }

    public void moveTowards (CGPoint position, double timeInterval) {
        CGPoint curPosition = getPosition();
        double dx = position.getX() - curPosition.getX();
        double dy = position.getY() - curPosition.getY();
        double dt = movementSpeed * timeInterval;

        double ang = APAUtils.polarAdjust(APAUtils.getRadiansBetweenPoints(position, curPosition));
        setZRotation(ang);

        double distRemaining = Math.hypot(dx, dy);
        if (distRemaining < dt) {
            setPosition(position);
        } else {
            setPosition(new CGPoint(curPosition.getX() - Math.sin(ang) * dt, curPosition.getY() + Math.cos(ang) * dt));
        }

        requestedAnimation = APAAnimationState.Walk;
    }

    public void moveInDirection (CGPoint direction, double timeInterval) {
        CGPoint curPosition = getPosition();
        double dx = movementSpeed * direction.getX();
        double dy = movementSpeed * direction.getY();
        double dt = movementSpeed * timeInterval;

        CGPoint targetPosition = new CGPoint(curPosition.getX() + dx, curPosition.getY() + dy);

        double ang = APAUtils.polarAdjust(APAUtils.getRadiansBetweenPoints(targetPosition, curPosition));
        setZRotation(ang);

        double distRemaining = Math.hypot(dx, dy);
        if (distRemaining < dt) {
            setPosition(targetPosition);
        } else {
            setPosition(new CGPoint(curPosition.getX() - Math.sin(ang) * dt, curPosition.getY() + Math.cos(ang) * dt));
        }

        // Don't change to a walk animation if we planning an attack.
        if (!attacking) {
            requestedAnimation = APAAnimationState.Walk;
        }
    }

    abstract NSArray<SKTexture> getIdleAnimationFrames ();

    abstract NSArray<SKTexture> getWalkAnimationFrames ();

    abstract NSArray<SKTexture> getAttackAnimationFrames ();

    abstract NSArray<SKTexture> getHitAnimationFrames ();

    abstract NSArray<SKTexture> getDeathAnimationFrames ();

    abstract SKEmitterNode getDamageEmitter ();

    abstract SKAction getDamageAction ();
}
