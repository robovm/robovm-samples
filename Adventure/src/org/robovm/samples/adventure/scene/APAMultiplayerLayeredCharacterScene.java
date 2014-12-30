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

package org.robovm.samples.adventure.scene;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.dispatch.DispatchQueue;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSMutableArray;
import org.robovm.apple.foundation.NSNotificationCenter;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSSet;
import org.robovm.apple.gamecontroller.GCController;
import org.robovm.apple.gamecontroller.GCControllerButtonInput;
import org.robovm.apple.gamecontroller.GCControllerDirectionPad;
import org.robovm.apple.spritekit.SKAction;
import org.robovm.apple.spritekit.SKEmitterNode;
import org.robovm.apple.spritekit.SKLabelHorizontalAlignmentMode;
import org.robovm.apple.spritekit.SKLabelNode;
import org.robovm.apple.spritekit.SKNode;
import org.robovm.apple.spritekit.SKScene;
import org.robovm.apple.spritekit.SKSpriteNode;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UITouch;
import org.robovm.objc.Selector;
import org.robovm.objc.annotation.Method;
import org.robovm.objc.block.VoidBlock1;
import org.robovm.objc.block.VoidBlock3;
import org.robovm.samples.adventure.sprites.APACharacter;
import org.robovm.samples.adventure.sprites.APACharacter.APAColliderType;
import org.robovm.samples.adventure.sprites.APACharacter.APAMoveDirection;
import org.robovm.samples.adventure.sprites.APAHeroCharacter;
import org.robovm.samples.adventure.util.APAUtils;

public abstract class APAMultiplayerLayeredCharacterScene extends SKScene {
    /** The layers in a scene. */
    public enum APAWorldLayer {
        Ground, BelowCharacter, Character, AboveCharacter, Top
    }

    /** Player states for the four players in the HUD. */
    public enum APAHUDState {
        Local, Connecting, Disconnected, Connected
    }

    private static final float MIN_TIME_INTERVAL = 1f / 60f;
    private static final int NUM_PLAYERS = 4;
    // minimum distance between hero and edge of camera before moving camera
    private static final int MIN_HERO_TO_EDGE_DISTANCE = 256;

    // list of player objects
    final List<APAPlayer> players = new ArrayList<>(NUM_PLAYERS);
    // player '1' controlled by keyboard/touch
    APAPlayer defaultPlayer;
    // root node to which all game renderables are attached
    SKNode world;
    // the point at which heroes are spawned
    CGPoint defaultSpawnPoint;
    // indicates the world moved before or during the current update
    boolean worldMovedForUpdate;
    final Selector clearWorldMoved = Selector.register("clearWorldMoved");

    // all heroes in the game
    final NSArray<APAHeroCharacter> heroes = new NSMutableArray<>();
    // different layer nodes within the world
    final NSArray<SKNode> layers = new NSMutableArray<>(APAWorldLayer.values().length);
    // keep track of the various nodes for the HUD
    private final NSArray<SKSpriteNode> hudAvatars = new NSMutableArray<>(NUM_PLAYERS);
    // there are always NUM_PLAYERS instances in each array
    private final NSArray<SKLabelNode> hudLabels = new NSMutableArray<>(NUM_PLAYERS);
    private final NSArray<SKLabelNode> hudScores = new NSMutableArray<>(NUM_PLAYERS);
    // an array of NSArrays of life hearts
    private final NSArray<NSArray<SKSpriteNode>> hudLifeHeartArrays = new NSMutableArray<>(NUM_PLAYERS);

    private NSObject controllerDidConnect;
    private NSObject controllerDidDisconnect;

    // the previous update: loop time interval
    private double lastUpdateTimeInterval;

    public APAMultiplayerLayeredCharacterScene (CGSize size) {
        super(size);
    }

    public void setup () {
        defaultPlayer = new APAPlayer();
        players.add(defaultPlayer);

        world = new SKNode();
        world.setName("world");

        for (int i = 0, n = APAWorldLayer.values().length; i < n; i++) {
            SKNode layer = new SKNode();
            layer.setZPosition(i - n);
            world.addChild(layer);
            layers.add(layer);
        }

        addChild(world);

        buildHUD();
        updateHUD(defaultPlayer, APAHUDState.Local, null);
    }

    /** Start loading all the shared assets for the scene in the background. This method calls loadSceneAssets on a background
     * queue, then calls the callback handler on the main thread. */
    public void loadSceneAssets (final Runnable callback) {
        DispatchQueue.getGlobalQueue(DispatchQueue.PRIORITY_HIGH, 0).async(new Runnable() {
            @Override
            public void run () {
                // Load the shared assets in the background.
                loadSceneAssets();

                if (callback == null) {
                    return;
                }

                DispatchQueue.getMainQueue().async(new Runnable() {
                    @Override
                    public void run () {
                        // Call the completion handler back on the main queue.
                        callback.run();
                    }
                });
            }
        });
    }

    /** Overridden by subclasses to load scene-specific assets. */
    abstract void loadSceneAssets ();

    /** Overridden by subclasses to release assets used only by this scene. */
    abstract void releaseSceneAssets ();

    /** Overridden by subclasses to provide an emitter used to indicate when a new hero is spawned. */
    abstract SKEmitterNode getSharedSpawnEmitter ();

    /** This method should be called when the level is loaded to set up currently-connected game controllers, and register for the
     * relevant notifications to deal with new connections/disconnections. */
    public void configureGameControllers () {
        // Receive notifications when a controller connects or disconnects.
        controllerDidConnect = GCController.Notifications.observeDidConnect(new VoidBlock1<GCController>() {
            @Override
            public void invoke (GCController controller) {
                System.out.println("Connected game controller: " + controller);

                int playerIndex = (int)controller.getPlayerIndex();
                if (playerIndex == -1) {
                    assignUnknownController(controller);
                } else {
                    assignPresetController(controller, playerIndex);
                }
            }
        });
        controllerDidDisconnect = GCController.Notifications.observeDidDisconnect(new VoidBlock1<GCController>() {
            @Override
            public void invoke (GCController controller) {
                for (APAPlayer player : players) {
                    if (player == null) {
                        continue;
                    }

                    if (player.controller == controller) {
                        player.controller = null;
                    }
                }

                System.out.println("Disconnected game controller: " + controller);
            }
        });

        // Configure all the currently connected game controllers.
        configureConnectedGameControllers();

        // And start looking for any wireless controllers.
        GCController.startWirelessControllerDiscovery(new Runnable() {
            @Override
            public void run () {
                System.out.println("Finished finding controllers");
            }
        });
    }

    private void configureConnectedGameControllers () {
        // First deal with the controllers previously set to a player.
        for (GCController controller : GCController.getControllers()) {
            int playerIndex = (int)controller.getPlayerIndex();
            if (playerIndex == -1) {
                continue;
            }

            assignPresetController(controller, playerIndex);
        }

        // Now deal with the unset controllers.
        for (GCController controller : GCController.getControllers()) {
            long playerIndex = controller.getPlayerIndex();
            if (playerIndex == -1) {
                continue;
            }

            assignUnknownController(controller);
        }
    }

    private void assignUnknownController (GCController controller) {
        for (int playerIndex = 0; playerIndex < NUM_PLAYERS; playerIndex++) {
            APAPlayer player = players.get(playerIndex);

            if (player == null) {
                player = new APAPlayer();
                players.set(playerIndex, player);
                updateHUD(player, APAHUDState.Connected, "CONTROLLER");
            }

            if (player.controller != null) {
                continue;
            }

            // Found an unlinked player.
            controller.setPlayerIndex(playerIndex);
            configureController(controller, player);
            return;
        }
    }

    private void assignPresetController (GCController controller, int playerIndex) {
        // Check whether this index is free.
        APAPlayer player = players.get(playerIndex);
        if (player == null) {
            player = new APAPlayer();
            players.set(playerIndex, player);
            updateHUD(player, APAHUDState.Connected, "CONTROLLER");
        }

        if (player.controller != null && player.controller != controller) {
            // Taken by another controller so reassign to another player.
            assignUnknownController(controller);
            return;
        }

        configureController(controller, player);
    }

    private void configureController (GCController controller, final APAPlayer player) {
        int playerIndex = players.indexOf(player);
        System.out.println(String.format("Assigning %s to player %s [%d]", controller.getVendorName(), player, playerIndex));

        // Assign the controller to the player.
        player.controller = controller;

        VoidBlock3<GCControllerDirectionPad, Float, Float> dpadMoveHandler = new VoidBlock3<GCControllerDirectionPad, Float, Float>() {
            @Override
            public void invoke (GCControllerDirectionPad dpad, Float xValue, Float yValue) {
                double length = Math.hypot(xValue, yValue);
                if (length > 0) {
                    double invLength = 1f / length;
                    player.heroMoveDirection = new CGPoint(xValue * invLength, yValue * invLength);
                } else {
                    player.heroMoveDirection = CGPoint.Zero();
                }
            }
        };

        // Use either the dpad or the left thumbstick to move the character.
        controller.getExtendedGamepad().getLeftThumbstick().setValueChangedHandler(dpadMoveHandler);
        controller.getGamepad().getDpad().setValueChangedHandler(dpadMoveHandler);

        VoidBlock3<GCControllerButtonInput, Float, Boolean> fireButtonHandler = new VoidBlock3<GCControllerButtonInput, Float, Boolean>() {
            @Override
            public void invoke (GCControllerButtonInput a, Float b, Boolean pressed) {
                player.fireAction = pressed;
            }
        };

        controller.getGamepad().getButtonA().setValueChangedHandler(fireButtonHandler);
        controller.getGamepad().getButtonB().setValueChangedHandler(fireButtonHandler);

        if (player != defaultPlayer && player.hero == null) {
            addHeroForPlayer(player);
        }
    }

    /** All sprites in the scene should be added through this method to ensure they are placed in the correct world layer. */
    public void addNode (SKNode node, APAWorldLayer layer) {
        SKNode layerNode = layers.get(layer.ordinal());
        layerNode.addChild(node);
    }

    public APAHeroCharacter addHeroForPlayer (APAPlayer player) {
        if (player == null) throw new NullPointerException("player");

        if (player.hero != null && !player.hero.dying) {
            player.hero.removeFromParent();
        }

        CGPoint spawnPos = defaultSpawnPoint;

        APAHeroCharacter hero = null;
        try {
            hero = player.heroClass.getConstructor(CGPoint.class, APAPlayer.class).newInstance(spawnPos, player);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
            | NoSuchMethodException e) {
            e.printStackTrace();
        }

        if (hero != null) {
            SKEmitterNode emitter = (SKEmitterNode)getSharedSpawnEmitter().copy();
            emitter.setPosition(spawnPos);
            addNode(emitter, APAWorldLayer.AboveCharacter);
            APAUtils.runOneShotEmitter(emitter, 0.15);

            hero.fadeIn(2.0);
            hero.addToScene(this);
            heroes.add(hero);
        }
        player.hero = hero;

        return hero;
    }

    public void heroWasKilled (APAHeroCharacter hero) {
        APAPlayer player = hero.getPlayer();

        heroes.remove(hero);

        // Disable touch movement, otherwise new hero will try to move to previously-touched location.
        player.moveRequested = false;

        if (--player.livesLeft < 1) {
            // In a real game, you'd want to end the game when there are no lives left.
            return;
        }

        updateHUDAfterHeroDeath(hero.getPlayer());

        hero = addHeroForPlayer(hero.getPlayer());
        centerWorldOnCharacter(hero);
    }

    @Override
    protected void dispose (boolean finalizing) {
        NSNotificationCenter.getDefaultCenter().removeObserver(controllerDidConnect);
        NSNotificationCenter.getDefaultCenter().removeObserver(controllerDidDisconnect);
        super.dispose(finalizing);
    }

    private void buildHUD () {
        String iconNames[] = new String[] {"iconWarrior_blue", "iconWarrior_green", "iconWarrior_pink", "iconWarrior_red"};
        UIColor colors[] = new UIColor[] {UIColor.green(), UIColor.blue(), UIColor.yellow(), UIColor.red()};

        double hudX = 30;
        double hudY = getFrame().getSize().getHeight() - 30;
        double hudD = getFrame().getSize().getWidth() / NUM_PLAYERS;

        SKNode hud = new SKNode();

        for (int i = 0; i < NUM_PLAYERS; i++) {
            SKSpriteNode avatar = SKSpriteNode.create(iconNames[i]);
            avatar.setScale(0.5);
            avatar.setAlpha(0.5);
            avatar.setPosition(new CGPoint(hudX + i * hudD + (avatar.getSize().getWidth() * 0.5), getFrame().getSize()
                .getHeight() - avatar.getSize().getHeight() * 0.5 - 8));
            hudAvatars.add(avatar);
            hud.addChild(avatar);

            SKLabelNode label = SKLabelNode.create("Copperplate");
            label.setText("NO PLAYER");
            label.setFontColor(colors[i]);
            label.setFontSize(16);
            label.setHorizontalAlignmentMode(SKLabelHorizontalAlignmentMode.Left);
            label.setPosition(new CGPoint(hudX + i * hudD + (avatar.getSize().getWidth() * 1.0), hudY + 10));
            hudLabels.add(label);
            hud.addChild(label);

            SKLabelNode score = SKLabelNode.create("Copperplate");
            score.setText("SCORE: 0");
            score.setFontColor(colors[i]);
            score.setFontSize(16);
            score.setHorizontalAlignmentMode(SKLabelHorizontalAlignmentMode.Left);
            score.setPosition(new CGPoint(hudX + i * hudD + (avatar.getSize().getWidth() * 1.0), hudY - 40));
            hudScores.add(score);
            hud.addChild(score);

            hudLifeHeartArrays.add(new NSMutableArray<SKSpriteNode>(APAPlayer.START_LIVES));
            for (int j = 0; j < APAPlayer.START_LIVES; j++) {
                SKSpriteNode heart = SKSpriteNode.create("lives.png");
                heart.setScale(0.4);
                heart.setPosition(new CGPoint(hudX + i * hudD + (avatar.getSize().getWidth() * 1.0) + 18
                    + ((heart.getSize().getWidth() + 5) * j), hudY - 10));
                heart.setAlpha(0.1);
                hudLifeHeartArrays.get(i).set(j, heart);
                hud.addChild(heart);
            }
        }

        addChild(hud);
    }

    private void updateHUD (APAPlayer player, APAHUDState state, String message) {
        int playerIndex = players.indexOf(player);

        SKSpriteNode avatar = hudAvatars.get(playerIndex);
        avatar.runAction(SKAction.sequence(new NSArray<SKAction>(SKAction.fadeAlphaTo(1.0, 1.0), SKAction.fadeAlphaTo(0.2, 1.0),
            SKAction.fadeAlphaTo(1.0, 1.0))));

        SKLabelNode label = hudLabels.get(playerIndex);
        double heartAlpha = 1;

        switch (state) {
        case Local:
            label.setText("ME");
            break;
        case Connecting:
            heartAlpha = 0.25;
            if (message != null) {
                label.setText(message);
            } else {
                label.setText("AVAILABLE");
            }
            break;
        case Disconnected:
            avatar.setAlpha(0.5);
            heartAlpha = 0.1;
            label.setText("NO PLAYER");
            break;
        case Connected:
            if (message != null) {
                label.setText(message);
            } else {
                label.setText("CONNECTED");
            }
            break;
        default:
            break;
        }

        for (int i = 0; i < player.livesLeft; i++) {
            SKSpriteNode heart = hudLifeHeartArrays.get(playerIndex).get(i);
            heart.setAlpha(heartAlpha);
        }
    }

    private void updateHUD (APAPlayer player) {
        int playerIndex = players.indexOf(player);
        SKLabelNode label = hudScores.get(playerIndex);
        label.setText(String.format("SCORE: %d", player.score));
    }

    private void updateHUDAfterHeroDeath (APAPlayer player) {
        int playerIndex = players.indexOf(player);

        // Fade out the relevant heart - one-based livesLeft has already been decremented.
        int heartNumber = player.livesLeft;

        NSArray<SKSpriteNode> heartArray = hudLifeHeartArrays.get(playerIndex);
        SKSpriteNode heart = heartArray.get(heartNumber);
        heart.runAction(SKAction.fadeAlphaTo(0.0, 3.0));
    }

    public void addToScore (long amount, SKNode projectile) {
        APAPlayer player = (APAPlayer)projectile.getUserData().get(APAHeroCharacter.PLAYER_KEY);

        player.score += amount;

        updateHUD(player);
    }

    void centerWorldOnPosition (CGPoint position) {
        world.setPosition(new CGPoint(-position.getX() + getFrame().getMidX(), -position.getY() + getFrame().getMidY()));

        worldMovedForUpdate = true;
    }

    void centerWorldOnCharacter (APACharacter character) {
        centerWorldOnPosition(character.getPosition());
    }

    public abstract double getDistanceToWall (CGPoint pos0, CGPoint pos1);

    public abstract boolean canSee (CGPoint pos0, CGPoint pos1);

    @Override
    public void update (double currentTime) {
        // Handle time delta.
        // If we drop below 60fps, we still want everything to move the same distance.
        double timeSinceLast = currentTime - lastUpdateTimeInterval;
        lastUpdateTimeInterval = currentTime;
        if (timeSinceLast > 1) { // more than a second since last update
            timeSinceLast = MIN_TIME_INTERVAL;
            lastUpdateTimeInterval = currentTime;
            worldMovedForUpdate = true;
        }

        updateScene(timeSinceLast);

        APAHeroCharacter hero = null;
        if (heroes.size() > 0) {
            hero = defaultPlayer.hero;
        }

        if (!hero.dying) {
            if (!defaultPlayer.targetLocation.equalToPoint(CGPoint.Zero())) {
                if (defaultPlayer.fireAction) {
                    hero.faceTo(defaultPlayer.targetLocation);
                }

                if (defaultPlayer.moveRequested) {
                    if (!defaultPlayer.targetLocation.equalToPoint(hero.getPosition())) {
                        hero.moveTowards(defaultPlayer.targetLocation, timeSinceLast);
                    } else {
                        defaultPlayer.moveRequested = false;
                    }
                }
            }
        }

        for (APAPlayer player : players) {
            if (player == null) {
                continue;
            }

            hero = player.hero;
            if (hero == null || hero.dying) {
                continue;
            }

            // heroMoveDirection is used by game controllers.
            CGPoint heroMoveDirection = player.heroMoveDirection;
            if (Math.hypot(heroMoveDirection.getX(), heroMoveDirection.getY()) > 0.0) {
                hero.moveInDirection(heroMoveDirection, timeSinceLast);
            } else {
                if (player.moveForward) {
                    hero.move(APAMoveDirection.Forward, timeSinceLast);
                } else if (player.moveBack) {
                    hero.move(APAMoveDirection.Back, timeSinceLast);
                }

                if (player.moveLeft) {
                    hero.move(APAMoveDirection.Left, timeSinceLast);
                } else if (player.moveRight) {
                    hero.move(APAMoveDirection.Right, timeSinceLast);
                }
            }

            if (player.fireAction) {
                hero.performAttackAction();
            }
        }
    }

    /** Overridden by subclasses to update the scene - called once per frame. */
    abstract void updateScene (double timeSinceLast);

    @Override
    public void didSimulatePhysics () {
        APAHeroCharacter defaultHero = defaultPlayer.hero;

        // Move the world relative to the default player position.
        if (defaultHero != null) {
            CGPoint heroPosition = defaultHero.getPosition();
            CGPoint worldPos = world.getPosition();
            double yCoordinate = worldPos.getY() + heroPosition.getY();
            if (yCoordinate < MIN_HERO_TO_EDGE_DISTANCE) {
                worldPos.setY(worldPos.getY() - yCoordinate + MIN_HERO_TO_EDGE_DISTANCE);
                worldMovedForUpdate = true;
            } else if (yCoordinate > (getFrame().getSize().getHeight() - MIN_HERO_TO_EDGE_DISTANCE)) {
                worldPos.setY(worldPos.getY() + (getFrame().getSize().getHeight() - yCoordinate) - MIN_HERO_TO_EDGE_DISTANCE);
                worldMovedForUpdate = true;
            }

            double xCoordinate = worldPos.getX() + heroPosition.getX();
            if (xCoordinate < MIN_HERO_TO_EDGE_DISTANCE) {
                worldPos.setX(worldPos.getX() - xCoordinate + MIN_HERO_TO_EDGE_DISTANCE);
                worldMovedForUpdate = true;
            } else if (xCoordinate > (getFrame().getSize().getHeight() - MIN_HERO_TO_EDGE_DISTANCE)) {
                worldPos.setX(worldPos.getX() + (getFrame().getSize().getWidth() - xCoordinate) - MIN_HERO_TO_EDGE_DISTANCE);
                worldMovedForUpdate = true;
            }
            world.setPosition(worldPos);
        }
        // Using performSelector:withObject:afterDelay: withg a delay of 0.0 means that the selector call occurs after
        // the current pass through the run loop.
        // This means the property will be cleared after the subclass implementation of didSimulatePhysics completes.
        performSelector(clearWorldMoved, null, 0.0);
    }

    @Method(selector = "clearWorldMoved")
    private void clearWorldMoved () {
        worldMovedForUpdate = false;
    }

    @Override
    public void touchesBegan (NSSet<UITouch> touches, UIEvent event) {
        if (heroes.size() < 1) {
            return;
        }
        UITouch touch = touches.any();

        if (defaultPlayer.movementTouch != null) {
            return;
        }

        defaultPlayer.targetLocation = touch.getLocationInNode(defaultPlayer.hero.getParent());

        boolean wantsAttack = false;
        NSArray<SKNode> nodes = getNodesAtPoint(touch.getLocationInNode(this));
        for (SKNode node : nodes) {
            if (((node.getPhysicsBody().getCategoryBitMask() & APAColliderType.Cave) == APAColliderType.Cave)
                || ((node.getPhysicsBody().getCategoryBitMask() & APAColliderType.GoblinOrBoss) == APAColliderType.GoblinOrBoss)) {
                wantsAttack = true;
            }
        }

        defaultPlayer.fireAction = wantsAttack;
        defaultPlayer.moveRequested = !wantsAttack;
        defaultPlayer.movementTouch = touch;
    }

    @Override
    public void touchesMoved (NSSet<UITouch> touches, UIEvent event) {
        if (heroes.size() < 1) {
            return;
        }
        UITouch touch = defaultPlayer.movementTouch;
        if (touches.contains(touch)) {
            defaultPlayer.targetLocation = touch.getLocationInNode(defaultPlayer.hero.getParent());
            if (!defaultPlayer.fireAction) {
                defaultPlayer.moveRequested = true;
            }
        }
    }

    @Override
    public void touchesEnded (NSSet<UITouch> touches, UIEvent event) {
        if (heroes.size() < 1) {
            return;
        }
        UITouch touch = defaultPlayer.movementTouch;

        if (touches.contains(touch)) {
            defaultPlayer.movementTouch = null;
            defaultPlayer.fireAction = false;
        }
    }

    public NSArray<APAHeroCharacter> getHeroes () {
        return heroes;
    }
}
