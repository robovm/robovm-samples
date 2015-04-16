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

package org.robovm.samples.adventure.scene;

import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.coregraphics.CGVector;
import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSDate;
import org.robovm.apple.foundation.NSMutableArray;
import org.robovm.apple.spritekit.SKAction;
import org.robovm.apple.spritekit.SKBlendMode;
import org.robovm.apple.spritekit.SKEmitterNode;
import org.robovm.apple.spritekit.SKNode;
import org.robovm.apple.spritekit.SKPhysicsBody;
import org.robovm.apple.spritekit.SKPhysicsContact;
import org.robovm.apple.spritekit.SKPhysicsContactDelegate;
import org.robovm.apple.spritekit.SKPhysicsWorld;
import org.robovm.apple.spritekit.SKSpriteNode;
import org.robovm.apple.spritekit.SKTextureAtlas;
import org.robovm.objc.ObjCRuntime;
import org.robovm.samples.adventure.sprites.APAArcher;
import org.robovm.samples.adventure.sprites.APABoss;
import org.robovm.samples.adventure.sprites.APACave;
import org.robovm.samples.adventure.sprites.APACharacter;
import org.robovm.samples.adventure.sprites.APACharacter.APAColliderType;
import org.robovm.samples.adventure.sprites.APAGoblin;
import org.robovm.samples.adventure.sprites.APAHeroCharacter;
import org.robovm.samples.adventure.sprites.APAParallaxSprite;
import org.robovm.samples.adventure.sprites.APATree;
import org.robovm.samples.adventure.sprites.APAWarrior;
import org.robovm.samples.adventure.util.APAUtils;
import org.robovm.samples.adventure.util.APAUtils.APADataMap;
import org.robovm.samples.adventure.util.APAUtils.APATreeMap;

public class APAAdventureScene extends APAMultiplayerLayeredCharacterScene implements SKPhysicsContactDelegate {
    private static final int WORLD_TILE_DIVISOR = 32; // number of tiles
    private static final int WORLD_SIZE = 4096; // pixel size of world (square)
    private static final int WORLD_TILE_SIZE = WORLD_SIZE / WORLD_TILE_DIVISOR;

    private static final int WORLD_CENTER = 2048;

    private static final int LEVEL_MAP_SIZE = 256; // pixel size of level map
                                                   // (square)
    private static final int LEVEL_MAP_DIVISOR = WORLD_SIZE / LEVEL_MAP_SIZE;

    private static final boolean MOVE_NEAR_TO_BOSS = false; // Cheat to move
                                                            // near to boss.

    public enum APAHeroType {
        Archer, Warrior
    }

    private static SKEmitterNode sharedProjectileSparkEmitter;
    private static SKEmitterNode sharedSpawnEmitter;
    private static APATree sharedSmallTree;
    private static APATree sharedBigTree;
    private static SKEmitterNode sharedLeafEmitterA;
    private static SKEmitterNode sharedLeafEmitterB;
    private static NSArray<SKNode> sharedBackgroundTiles;

    private final NSArray<APACave> goblinCaves = new NSMutableArray<>(); // whence
                                                                         // cometh
                                                                         // goblins

    private APADataMap[] levelMap; // locations of caves/spawn points/etc
    private APATreeMap[] treeMap; // locations of trees

    private APABoss levelBoss; // the big boss character
    private final NSArray<SKEmitterNode> particleSystems = new NSMutableArray<>();// particle
                                                                                  // emitter
                                                                                  // nodes
    private final NSArray<APAParallaxSprite> parallaxSprites = new NSMutableArray<>(); // all
                                                                                       // the
                                                                                       // parallax
                                                                                       // sprites
                                                                                       // in
                                                                                       // this
                                                                                       // scene
    private final NSArray<APATree> trees = new NSMutableArray<>(); // all the
                                                                   // trees in
                                                                   // the scene

    public APAAdventureScene(CGSize size) {
        super(size);
    }

    @Override
    public void setup() {
        super.setup();

        // Build level and tree maps.
        levelMap = APAUtils.createDataMap("map_level.png");
        treeMap = APAUtils.createTreeMap("map_trees.png");

        APACave.setGlobalGoblinCap(32);

        buildWorld();

        // Center the camera on the hero spawn point.
        CGPoint startPosition = defaultSpawnPoint;
        centerWorldOnPosition(startPosition);
    }

    private void buildWorld() {
        System.out.println("Building the world");

        // Configure physics for the world.
        SKPhysicsWorld physicsWorld = getPhysicsWorld();
        physicsWorld.setGravity(new CGVector(0.0, 0.0)); // no gravity
        physicsWorld.setContactDelegate(this);

        addBackgroundTiles();
        System.out.println(4);

        addSpawnPoints();
        System.out.println(5);

        addTrees();
        System.out.println(6);

        addCollisionWalls();
        System.out.println(7);
    }

    private void addBackgroundTiles() {
        // Tiles should already have been pre-loaded in loadSceneAssets.
        for (SKNode tileNode : sharedBackgroundTiles) {
            addNode(tileNode, APAWorldLayer.Ground);
        }
    }

    private void addSpawnPoints() {
        // Add goblin caves and set hero/boss spawn points.
        for (int y = 0; y < LEVEL_MAP_SIZE; y++) {
            for (int x = 0; x < LEVEL_MAP_SIZE; x++) {
                CGPoint location = new CGPoint(x, y);
                APADataMap spot = queryLevelMap(location);

                // Get the world space point for this level map pixel.
                CGPoint worldPoint = convertLevelMapPointToWorldPoint(location);

                if (spot.getBossLocation() <= 200) {
                    levelBoss = new APABoss(worldPoint);
                    levelBoss.addToScene(this);
                } else if (spot.getGoblinCaveLocation() >= 200) {
                    APACave cave = new APACave(worldPoint);
                    goblinCaves.add(cave);
                    parallaxSprites.add(cave);
                    cave.addToScene(this);
                } else if (spot.getHeroSpawnLocation() >= 200) {
                    defaultSpawnPoint = worldPoint; // there's only one
                }
            }
        }
    }

    private void addTrees() {
        for (int y = 0; y < LEVEL_MAP_SIZE; y++) {
            for (int x = 0; x < LEVEL_MAP_SIZE; x++) {
                CGPoint location = new CGPoint(x, y);
                APATreeMap spot = queryTreeMap(location);

                CGPoint treePos = convertLevelMapPointToWorldPoint(location);
                APAWorldLayer treeLayer = APAWorldLayer.Top;
                APATree tree = null;

                if (spot.getSmallTreeLocation() >= 200) {
                    // Create small tree at this location.
                    treeLayer = APAWorldLayer.AboveCharacter;
                    tree = (APATree) sharedSmallTree.copy();
                } else if (spot.getBigTreeLocation() >= 200) {
                    // Create big tree with leaf emitters at this position.
                    tree = (APATree) sharedBigTree.copy();

                    SKEmitterNode emitter = null;
                    // Pick one of the two leaf emitters for this tree.
                    if (Math.random() < 0.5) {
                        emitter = (SKEmitterNode) sharedLeafEmitterA.copy();
                    } else {
                        emitter = (SKEmitterNode) sharedLeafEmitterB.copy();
                    }

                    emitter.setPosition(treePos);
                    emitter.setPaused(true);
                    addNode(emitter, APAWorldLayer.Character);
                    particleSystems.add(emitter);
                } else {
                    continue;
                }

                tree.setPosition(treePos);
                tree.setZRotation(Math.random() * (Math.PI * 2.0));
                addNode(tree, treeLayer);
                parallaxSprites.add(tree);
                trees.add(tree);
            }
        }
    }

    private void addCollisionWalls() {
        NSDate startDate = new NSDate();
        boolean[] filled = new boolean[LEVEL_MAP_SIZE * LEVEL_MAP_SIZE];

        int numVolumes = 0;
        int numBlocks = 0;

        // Add horizontal collision walls.
        for (int y = 0; y < LEVEL_MAP_SIZE; y++) { // iterate in horizontal rows
            for (int x = 0; x < LEVEL_MAP_SIZE; x++) {
                CGPoint location = new CGPoint(x, y);
                APADataMap spot = queryLevelMap(location);

                // Get the world space point for this pixel.
                CGPoint worldPoint = convertLevelMapPointToWorldPoint(location);

                if (spot.getWall() < 200) {
                    continue; // no wall
                }

                int horizontalDistanceFromLeft = x;
                APADataMap nextSpot = spot;
                while (horizontalDistanceFromLeft < LEVEL_MAP_SIZE && nextSpot.getWall() >= 200
                        && !filled[(y * LEVEL_MAP_SIZE) + horizontalDistanceFromLeft]) {
                    horizontalDistanceFromLeft++;
                    nextSpot = queryLevelMap(new CGPoint(horizontalDistanceFromLeft, y));
                }

                int wallWidth = horizontalDistanceFromLeft - x;
                int verticalDistanceFromTop = y;

                if (wallWidth > 8) {
                    nextSpot = spot;
                    while (verticalDistanceFromTop < LEVEL_MAP_SIZE && nextSpot.getWall() >= 200) {
                        verticalDistanceFromTop++;
                        nextSpot = queryLevelMap(new CGPoint(x + (wallWidth / 2), verticalDistanceFromTop));
                    }

                    int wallHeight = verticalDistanceFromTop - y;
                    for (int j = y; j < verticalDistanceFromTop; j++) {
                        for (int i = x; i < horizontalDistanceFromLeft; i++) {
                            filled[(j * LEVEL_MAP_SIZE) + i] = true;
                            numBlocks++;
                        }
                    }

                    addCollisionWall(worldPoint, LEVEL_MAP_DIVISOR * wallWidth, LEVEL_MAP_DIVISOR * wallHeight);
                    numVolumes++;
                }
            }
        }

        // Add vertical collision walls.
        for (int x = 0; x < LEVEL_MAP_SIZE; x++) { // iterate in vertical rows
            for (int y = 0; y < LEVEL_MAP_SIZE; y++) {
                CGPoint location = new CGPoint(x, y);
                APADataMap spot = queryLevelMap(location);

                // Get the world space point for this pixel.
                CGPoint worldPoint = convertLevelMapPointToWorldPoint(location);

                if (spot.getWall() < 200 || filled[(y * LEVEL_MAP_SIZE) + x]) {
                    continue; // no wall, or already filled from X collision
                              // walls
                }

                int verticalDistanceFromTop = y;
                APADataMap nextSpot = spot;
                while (verticalDistanceFromTop < LEVEL_MAP_SIZE && nextSpot.getWall() >= 200
                        && !filled[(verticalDistanceFromTop * LEVEL_MAP_SIZE) + x]) {
                    verticalDistanceFromTop++;
                    nextSpot = queryLevelMap(new CGPoint(x, verticalDistanceFromTop));
                }

                int wallHeight = verticalDistanceFromTop - y;
                int horizontalDistanceFromLeft = x;

                if (wallHeight > 8) {
                    nextSpot = spot;
                    while (horizontalDistanceFromLeft < LEVEL_MAP_SIZE && nextSpot.getWall() >= 200) {
                        horizontalDistanceFromLeft++;
                        nextSpot = queryLevelMap(new CGPoint(horizontalDistanceFromLeft, y + (wallHeight / 2)));
                    }

                    int wallLength = horizontalDistanceFromLeft - x;
                    for (int j = y; j < verticalDistanceFromTop; j++) {
                        for (int i = x; i < horizontalDistanceFromLeft; i++) {
                            filled[(j * LEVEL_MAP_SIZE) + 1] = true;
                            numBlocks++;
                        }
                    }

                    addCollisionWall(worldPoint, LEVEL_MAP_DIVISOR * wallLength, LEVEL_MAP_DIVISOR * wallHeight);
                    numVolumes++;
                }
            }
        }

        System.out.println(String.format("converted %d collision blocks into %d volumes in %f seconds", numBlocks,
                numVolumes,
                new NSDate().getTimeIntervalSince(startDate)));
    }

    private void addCollisionWall(CGPoint worldPoint, double width, double height) {
        CGRect rect = new CGRect(0, 0, width, height);

        SKNode wallNode = new SKNode();
        wallNode.setPosition(new CGPoint(worldPoint.getX() + rect.getSize().getWidth() * 0.5, worldPoint.getY()
                - rect.getSize().getHeight() * 0.5));
        SKPhysicsBody physicsBody = SKPhysicsBody.createRectangle(rect.getSize());
        physicsBody.setDynamic(false);
        physicsBody.setCategoryBitMask(APAColliderType.Wall);
        physicsBody.setCollisionBitMask(0);
        wallNode.setPhysicsBody(physicsBody);

        addNode(wallNode, APAWorldLayer.Ground);
    }

    public void startLevel() {
        APAHeroCharacter hero = addHeroForPlayer(defaultPlayer);

        if (MOVE_NEAR_TO_BOSS) {
            CGPoint bossPosition = levelBoss.getPosition(); // set earlier from
                                                            // buildWorld in
                                                            // addSpawnPoints
            bossPosition.setX(bossPosition.getX() + 128);
            bossPosition.setY(bossPosition.getY() + 512);
            hero.setPosition(bossPosition);
        }

        centerWorldOnCharacter(hero);
    }

    public void setDefaultPlayerHeroType(APAHeroType heroType) {
        switch (heroType) {
        case Archer:
            defaultPlayer.heroClass = APAArcher.class;
            break;
        case Warrior:
            defaultPlayer.heroClass = APAWarrior.class;
            break;
        }
    }

    @Override
    public void heroWasKilled(APAHeroCharacter hero) {
        for (APACave cave : goblinCaves) {
            cave.stopGoblinsFromTargettingHero(hero);
        }
        super.heroWasKilled(hero);
    }

    @Override
    void updateScene(double timeSinceLast) {
        // Update all players' heroes.
        for (APAHeroCharacter hero : heroes) {
            hero.update(timeSinceLast);
        }

        // Update the level boss.
        levelBoss.update(timeSinceLast);

        // Update the caves (and in turn, their goblins).
        for (APACave cave : goblinCaves) {
            cave.update(timeSinceLast);
        }
    }

    @Override
    public void didSimulatePhysics() {
        super.didSimulatePhysics();

        // Get the position either of the default hero or the hero spawn point.
        APAHeroCharacter defaultHero = defaultPlayer.hero;
        CGPoint position = CGPoint.Zero();
        if (defaultHero != null && heroes.contains(defaultHero)) {
            position = defaultHero.getPosition();
        } else {
            position = defaultSpawnPoint;
        }

        // Update the alphas of any trees that are near the hero (center of the
        // camera) and therefore visible or soon to be
// visible.
        for (APATree tree : trees) {
            if (APAUtils.getDistanceBetweenPoints(tree.getPosition(), position) < 1024) {
                tree.updateAlpha(this);
            }
        }

        if (!worldMovedForUpdate) {
            return;
        }

        // Show any nearby hidden particle systems and hide those that are too
        // far away to be seen.
        for (SKEmitterNode particles : particleSystems) {
            boolean particlesAreVisible = APAUtils.getDistanceBetweenPoints(particles.getPosition(), position) < 1024;

            if (!particlesAreVisible && !particles.isPaused()) {
                particles.setPaused(true);
            } else if (particlesAreVisible && particles.isPaused()) {
                particles.setPaused(false);
            }
        }

        // Update nearby parallax sprites.
        for (APAParallaxSprite sprite : parallaxSprites) {
            if (APAUtils.getDistanceBetweenPoints(sprite.getPosition(), position) >= 1024) {
                continue;
            }

            sprite.updateOffset();
        }
    }

    private APADataMap queryLevelMap(CGPoint point) {
        // Grab the level map pixel for a given x,y (upper left).
        return levelMap[(int) point.getY() * LEVEL_MAP_SIZE + (int) point.getX()];
    }

    private APATreeMap queryTreeMap(CGPoint point) {
        // Grab the tree map pixel for a given x,y (upper left).
        return treeMap[(int) point.getY() * LEVEL_MAP_SIZE + (int) point.getX()];
    }

    @Override
    public double getDistanceToWall(CGPoint pos0, CGPoint pos1) {
        CGPoint a = convertWorldPointToLevelMapPoint(pos0);
        CGPoint b = convertWorldPointToLevelMapPoint(pos1);

        double deltaX = b.getX() - a.getX();
        double deltaY = b.getY() - a.getY();
        double dist = APAUtils.getDistanceBetweenPoints(a, b);
        double inc = 1.0 / dist;
        CGPoint p = CGPoint.Zero();

        for (double i = 0; i <= 1; i += inc) {
            p.setX(a.getX() + i * deltaX);
            p.setY(a.getY() + i * deltaY);

            APADataMap point = queryLevelMap(p);
            if (point.getWall() > 200) {
                CGPoint wpos2 = convertLevelMapPointToWorldPoint(p);
                return APAUtils.getDistanceBetweenPoints(pos0, wpos2);
            }
        }
        return Float.MAX_VALUE;
    }

    @Override
    public boolean canSee(CGPoint pos0, CGPoint pos1) {
        CGPoint a = convertWorldPointToLevelMapPoint(pos0);
        CGPoint b = convertWorldPointToLevelMapPoint(pos1);

        double deltaX = b.getX() - a.getX();
        double deltaY = b.getY() - a.getY();
        double dist = APAUtils.getDistanceBetweenPoints(a, b);
        double inc = 1.0 / dist;
        CGPoint p = CGPoint.Zero();

        for (double i = 0; i <= 1; i += inc) {
            p.setX(a.getX() + i * deltaX);
            p.setY(a.getY() + i * deltaY);

            APADataMap point = queryLevelMap(p);
            if (point.getWall() > 200) {
                return false;
            }
        }
        return true;
    }

    private CGPoint convertLevelMapPointToWorldPoint(CGPoint location) {
        // Given a level map pixel point, convert up to a world point.
        // This determines which "tile" the point falls in and centers within
        // that tile.
        int x = (int) ((location.getX() * LEVEL_MAP_DIVISOR) - (WORLD_CENTER * WORLD_TILE_SIZE / 2));
        int y = (int) -((location.getY() * LEVEL_MAP_DIVISOR) - (WORLD_CENTER * WORLD_TILE_SIZE / 2));
        location.setX(x);
        location.setY(y);
        return location;
    }

    private CGPoint convertWorldPointToLevelMapPoint(CGPoint location) {
        // Given a world based point, resolve to a pixel location in the level
        // map.
        int x = (int) ((location.getX() + WORLD_CENTER) / LEVEL_MAP_DIVISOR);
        int y = (int) ((WORLD_SIZE - (location.getY() + WORLD_CENTER)) / LEVEL_MAP_DIVISOR);
        location.setX(x);
        location.setY(y);
        return location;
    }

    @Override
    void loadSceneAssets() {
        SKTextureAtlas atlas = SKTextureAtlas.create("Environment");

        // Load archived emitters and create copyable sprites.
        sharedProjectileSparkEmitter = APAUtils.getEmitterNodeByName("ProjectileSplat");
        sharedSpawnEmitter = APAUtils.getEmitterNodeByName("Spawn");

        sharedSmallTree = new APATree(new NSArray<SKSpriteNode>(SKSpriteNode.create(atlas
                .getTexture("small_tree_base.png")),
                SKSpriteNode.create(atlas.getTexture("small_tree_middle.png")), SKSpriteNode.create(atlas
                        .getTexture("small_tree_top.png"))), 25.0);
        sharedBigTree = new APATree(new NSArray<SKSpriteNode>(
                SKSpriteNode.create(atlas.getTexture("big_tree_base.png")),
                SKSpriteNode.create(atlas.getTexture("big_tree_middle.png")), SKSpriteNode.create(atlas
                        .getTexture("big_tree_top.png"))), 150.0);
        sharedBigTree.setFadesAlpha(true);
        sharedLeafEmitterA = APAUtils.getEmitterNodeByName("Leaves_01");
        sharedLeafEmitterB = APAUtils.getEmitterNodeByName("Leaves_02");

        // Load the tiles that make up the ground layer.
        loadWorldTiles();

        // Load assets for all the sprites within this scene.
        APAArcher.loadSharedAssets();
        APABoss.loadSharedAssets();
        APACave.loadSharedAssets();
        APAGoblin.loadSharedAssets();
        APAHeroCharacter.loadSharedAssets();
        APAWarrior.loadSharedAssets();
    }

    private void loadWorldTiles() {
        System.out.println("Loading world tiles");
        NSDate startDate = new NSDate();

        SKTextureAtlas tileAtlas = SKTextureAtlas.create("Tiles");

        sharedBackgroundTiles = new NSMutableArray<>(1024);
        System.out.println("A");
        for (int y = 0; y < WORLD_TILE_DIVISOR; y++) {
            for (int x = 0; x < WORLD_TILE_DIVISOR; x++) {
                int tileNumber = (y * WORLD_TILE_DIVISOR) + x;
                SKSpriteNode tileNode = SKSpriteNode.create(tileAtlas.getTexture(String
                        .format("tile%d.png", tileNumber)));
                CGPoint position = new CGPoint((x * WORLD_TILE_SIZE) - WORLD_CENTER,
                        (WORLD_SIZE - (y * WORLD_TILE_SIZE))
                                - WORLD_CENTER);
                tileNode.setPosition(position);
                tileNode.setZPosition(-1.0);
                tileNode.setBlendMode(SKBlendMode.Replace);
                sharedBackgroundTiles.add(tileNode);
            }
        }
        System.out.println(String.format("Loaded all world tiles in %f seconds",
                new NSDate().getTimeIntervalSince(startDate)));
    }

    @Override
    public void releaseSceneAssets() {
        // Get rid of everything unique to this scene (but not the characters,
        // which might appear in other scenes).
        sharedBackgroundTiles = null;
        sharedProjectileSparkEmitter = null;
        sharedSpawnEmitter = null;
        sharedLeafEmitterA = null;
        sharedLeafEmitterB = null;
    }

    @Override
    public void didBeginContact(SKPhysicsContact contact) {
        // Either bodyA or bodyB in the collision could be a character.
        SKNode node = contact.getBodyA().getNode();
        if (node instanceof APACharacter) {
            ((APACharacter) node).collidedWith(contact.getBodyB());
        }

        // Check bodyB too.
        node = contact.getBodyB().getNode();
        if (node instanceof APACharacter) {
            ((APACharacter) node).collidedWith(contact.getBodyA());
        }

        // Handle collisions with projectiles.
        if ((contact.getBodyA().getCategoryBitMask() & APAColliderType.Projectile) == APAColliderType.Projectile
                || (contact.getBodyB().getCategoryBitMask() & APAColliderType.Projectile) == APAColliderType.Projectile) {
            SKNode projectile = ((contact.getBodyA().getCategoryBitMask() & APAColliderType.Projectile) == APAColliderType.Projectile) ? contact
                    .getBodyA().getNode()
                    : contact.getBodyB().getNode();

            projectile.runAction(SKAction.removeFromParent());

            // Build up a "one shot" particle to indicate where the projectile
            // hit.
            SKEmitterNode emitter = (SKEmitterNode) sharedProjectileSparkEmitter.copy();
            addNode(emitter, APAWorldLayer.AboveCharacter);
            emitter.setPosition(projectile.getPosition());
            APAUtils.runOneShotEmitter(emitter, 0.15);
        }
    }

    @Override
    public void didEndContact(SKPhysicsContact contact) {}

    @Override
    SKEmitterNode getSharedSpawnEmitter() {
        return sharedSpawnEmitter;
    }
}
