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
import org.robovm.apple.spritekit.SKNode;
import org.robovm.apple.spritekit.SKScene;
import org.robovm.apple.spritekit.SKSpriteNode;
import org.robovm.apple.spritekit.SKTexture;

public class APAParallaxSprite extends SKSpriteNode {
    boolean usesParallaxEffect;
    double virtualZRotation;
    double parallaxOffset;

    public APAParallaxSprite () {
    }

    public APAParallaxSprite (NSArray<SKSpriteNode> sprites, double offset) {
        usesParallaxEffect = true;

        // Make sure our z layering is correct for the stack.
        double zOffset = 1.0 / sprites.size();

        // All nodes in the stack are direct children, with ordered zPosition.
        double ourZPosition = getZPosition();
        int childNumber = 0;
        for (SKNode node : sprites) {
            node.setZPosition(ourZPosition + (zOffset + (zOffset * childNumber)));
            addChild(node);
            childNumber++;
        }

        parallaxOffset = offset;
    }

    public APAParallaxSprite (SKTexture texture) {
        super(texture);
    }

    @Override
    public void setZRotation (double rotation) {
        // Override to apply the zRotation just to the stack nodes, but only if the parallax effect is enabled.
        if (!usesParallaxEffect) {
            super.setZRotation(rotation);
            return;
        }

        if (rotation > 0.0) {
            super.setZRotation(0.0); // never rotate the group node

            // Instead, apply the desired rotation to each node in the stack.
            for (SKNode child : getChildren()) {
                child.setZRotation(rotation);
            }

            virtualZRotation = rotation;
        }
    }

    public void updateOffset () {
        SKScene scene = getScene();
        SKNode parent = getParent();

        if (!usesParallaxEffect || parent == null) {
            return;
        }

        CGPoint scenePos = scene.convertPointFromNode(getPosition(), parent);

        // Calculate the offset directions relative to the center of the screen.
        // Bias to (-0.5, 0.5) range.
        double offsetX = -1.0 + (2.0 * (scenePos.getX() / scene.getSize().getWidth()));
        double offsetY = -1.0 + (2.0 * (scenePos.getY() / scene.getSize().getHeight()));

        double delta = parallaxOffset / getChildren().size();

        int childNumber = 0;
        for (SKNode node : getChildren()) {
            node.setPosition(new CGPoint(offsetX * delta * childNumber, offsetY * delta * childNumber));
            childNumber++;
        }
    }

    public boolean isUsingParallaxEffect () {
        return usesParallaxEffect;
    }

    public double getVirtualZRotation () {
        return virtualZRotation;
    }
}
