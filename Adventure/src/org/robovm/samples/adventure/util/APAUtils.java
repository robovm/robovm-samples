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

package org.robovm.samples.adventure.util;

import java.io.File;

import org.robovm.apple.coregraphics.CGBitmapContext;
import org.robovm.apple.coregraphics.CGBitmapInfo;
import org.robovm.apple.coregraphics.CGColorSpace;
import org.robovm.apple.coregraphics.CGImage;
import org.robovm.apple.coregraphics.CGImageAlphaInfo;
import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.foundation.NSKeyedUnarchiver;
import org.robovm.apple.foundation.NSMutableArray;
import org.robovm.apple.spritekit.SKAction;
import org.robovm.apple.spritekit.SKEmitterNode;
import org.robovm.apple.spritekit.SKTexture;
import org.robovm.apple.spritekit.SKTextureAtlas;
import org.robovm.apple.uikit.UIImage;
import org.robovm.rt.bro.Struct;
import org.robovm.rt.bro.annotation.StructMember;
import org.robovm.rt.bro.ptr.IntPtr;

public class APAUtils {

    public final class APADataMap extends Struct<APADataMap> {
        public APADataMap () {
        }

        @StructMember(0)
        public native byte getBossLocation ();

        @StructMember(0)
        public native APADataMap setBossLocation (byte bossLocation);

        @StructMember(1)
        public native byte getWall ();

        @StructMember(1)
        public native APADataMap setWall (byte wall);

        @StructMember(2)
        public native byte getGoblinCaveLocation ();

        @StructMember(2)
        public native APADataMap setGoblinCaveLocation (byte goblinCaveLocation);

        @StructMember(3)
        public native byte getHeroSpawnLocation ();

        @StructMember(3)
        public native APADataMap setHeroSpawnLocation (byte heroSpawnLocation);
    }

    public final class APATreeMap extends Struct<APATreeMap> {
        public APATreeMap () {
        }

        @StructMember(0)
        public native byte getUnusedA ();

        @StructMember(0)
        public native APATreeMap setUnusedA (byte unusedA);

        @StructMember(1)
        public native byte getBigTreeLocation ();

        @StructMember(1)
        public native APATreeMap setBigTreeLocation (byte bigTreeLocation);

        @StructMember(2)
        public native byte getSmallTreeLocation ();

        @StructMember(2)
        public native APATreeMap setSmallTreeLocation (byte smallTreeLocation);

        @StructMember(3)
        public native byte getUnusedB ();

        @StructMember(3)
        public native APATreeMap setUnusedB (byte unusedB);
    }

    private static CGImage getCGImage (String name) {
        int ix = name.lastIndexOf('/');
        if (ix != -1) name = name.substring(ix);
        UIImage uiImage = UIImage.create(name);
        return uiImage.getCGImage();
    }

    private static CGBitmapContext createARGBBitmapContext (CGImage inImage) {
        CGBitmapContext context = null;
        CGColorSpace colorSpace = null;

        int bitmapBytesPerRow = 0;

        // Get image width, height. We'll use the entire image.
        long pixelsWide = inImage.getWidth();
        long pixelsHigh = inImage.getHeight();

        // Declare the number of bytes per row. Each pixel in the bitmap in this
        // example is represented by 4 bytes; 8 bits each of red, green, blue, and
        // alpha.
        bitmapBytesPerRow = (int)(pixelsWide * 4);

        // Use the generic RGB color space.
        colorSpace = CGColorSpace.createDeviceRGB();
        if (colorSpace == null) {
            System.err.println("Error allocating color space");
            return null;
        }

        // Allocate memory for image data. This is the destination in memory
        // where any drawing to the bitmap context will be rendered.
        IntPtr bitmapData = Struct.malloc(IntPtr.class, (int)(bitmapBytesPerRow * pixelsHigh));
        if (bitmapData == null) {
            System.err.println("Memory not allocated!");
            return null;
        }

        // Create the bitmap context. We want pre-multiplied ARGB, 8-bits
        // per component. Regardless of what the source image format is
        // (CMYK, Grayscale, and so on) it will be converted over to the format
        // specified here by CGBitmapContextCreate.
        context = CGBitmapContext.create(bitmapData, pixelsWide, pixelsHigh, 8, bitmapBytesPerRow, colorSpace, new CGBitmapInfo(
            CGImageAlphaInfo.PremultipliedFirst.value()));
        if (context == null) {
            System.err.println("Context not created!");
        }

        return context;
    }

    public static APADataMap[] createDataMap (String mapName) {
        CGImage inImage = getCGImage(mapName);
        // Create the bitmap context.
        CGBitmapContext context = createARGBBitmapContext(inImage);

        if (context == null) {
            return null;
        }

        // Get image width, height. We'll use the entire image.
        long w = inImage.getWidth();
        long h = inImage.getHeight();
        CGRect rect = new CGRect(0, 0, w, h);

        // Draw the image to the bitmap context. Once we draw, the memory
        // allocated for the context for rendering will then contain the
        // raw image data in the specified color space.
        context.drawImage(rect, inImage);

        // Now we can get a pointer to the image data associated with the bitmap context.
        IntPtr data = context.getData();

        APADataMap map = data.as(APADataMap.class);
        return map.toArray((int)(w * h));
    }

    public static APATreeMap[] createTreeMap (String mapName) {
        CGImage inImage = getCGImage(mapName);
        // Create the bitmap context.
        CGBitmapContext context = createARGBBitmapContext(inImage);

        if (context == null) {
            return null;
        }

        // Get image width, height. We'll use the entire image.
        long w = inImage.getWidth();
        long h = inImage.getHeight();
        CGRect rect = new CGRect(0, 0, w, h);

        // Draw the image to the bitmap context. Once we draw, the memory
        // allocated for the context for rendering will then contain the
        // raw image data in the specified color space.
        context.drawImage(rect, inImage);

        // Now we can get a pointer to the image data associated with the bitmap context.
        IntPtr data = context.getData();

        // When finished, release the context.
        context.release();

        APATreeMap map = data.as(APATreeMap.class);
        return map.toArray((int)(w * h));
    }

    public static double polarAdjust (double x) {
        return x + Math.PI * 0.5f;
    }

    public static double getDistanceBetweenPoints (CGPoint first, CGPoint second) {
        return Math.hypot(second.getX() - first.getX(), second.getY() - first.getY());
    }

    public static double getRadiansBetweenPoints (CGPoint first, CGPoint second) {
        double deltaX = second.getX() - first.getX();
        double deltaY = second.getY() - first.getY();
        return Math.atan2(deltaY, deltaX);
    }

    public static CGPoint getPointByAddingPoints (CGPoint first, CGPoint second) {
        return new CGPoint(first.getX() + second.getX(), first.getY() + second.getY());
    }

    public static NSArray<SKTexture> loadFramesFromAtlas (String atlasName, String baseFileName, int numberOfFrames) {
        NSArray<SKTexture> frames = new NSMutableArray<>(numberOfFrames);

        SKTextureAtlas atlas = SKTextureAtlas.create(atlasName);
        for (int i = 1; i <= numberOfFrames; i++) {
            String fileName = String.format("%s%04d.png", baseFileName, i);
            SKTexture texture = atlas.getTexture(fileName);
            frames.add(texture);
        }

        return frames;
    }

    public static void runOneShotEmitter (final SKEmitterNode emitter, double duration) {
        emitter.runAction(SKAction.sequence(new NSArray<SKAction>(SKAction.wait(duration), SKAction.runBlock(new Runnable() {
            @Override
            public void run () {
                emitter.setParticleBirthRate(0);
            }
        }), SKAction.wait(emitter.getParticleLifetime() + emitter.getParticleLifetimeRange()), SKAction.removeFromParent())));
    }

    public static SKEmitterNode getEmitterNodeByName (String emitterFileName) {
        return (SKEmitterNode)NSKeyedUnarchiver.unarchive(new File(NSBundle.getMainBundle().findResourcePath(emitterFileName,
            "sks")));
    }
}
