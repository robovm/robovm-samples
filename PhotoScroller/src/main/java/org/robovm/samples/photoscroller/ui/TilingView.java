/*
 * Copyright (C) 2013-2015 RoboVM AB
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
 * Portions of this code is based on Apple Inc's PhotoScroller sample (v1.3)
 * which is copyright (C) 2010-2012 Apple Inc.
 */

package org.robovm.samples.photoscroller.ui;

import java.io.File;

import org.robovm.apple.coreanimation.CALayer;
import org.robovm.apple.coreanimation.CATiledLayer;
import org.robovm.apple.coregraphics.CGContext;
import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.uikit.UIGraphics;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIView;
import org.robovm.objc.annotation.Method;

public class TilingView extends UIView {
    private final String imageName;

    @Method(selector = "layerClass")
    public static Class<? extends CALayer> getLayerClass() {
        return CATiledLayer.class;
    }

    public TilingView(String imageName, CGSize size) {
        super(new CGRect(CGPoint.Zero(), size));
        this.imageName = imageName;

        CATiledLayer tiledLayer = (CATiledLayer) getLayer();
        tiledLayer.setLevelsOfDetail(4);
    }

    /**
     * To handle the interaction between CATiledLayer and high resolution
     * screens, we need to always keep the tiling view's contentScaleFactor at
     * 1.0. UIKit will try to set it back to 2.0 on retina displays, which is
     * the right call in most cases, but since we're backed by a CATiledLayer it
     * will actually cause us to load the wrong sized tiles.
     */
    @Override
    public void setContentScaleFactor(double v) {
        super.setContentScaleFactor(1);
    }

    @Override
    public void draw(CGRect rect) {
        CGContext context = UIGraphics.getCurrentContext();
        /*
         * get the scale from the context by getting the current transform
         * matrix, then asking for its "a" component, which is one of the two
         * scale components. We could also ask for "d". This assumes (safely)
         * that the view is being scaled equally in both dimensions.
         */

        double scale = context.getCTM().getA();

        CATiledLayer tiledLayer = (CATiledLayer) getLayer();
        CGSize tileSize = tiledLayer.getTileSize();
        /*
         * Even at scales lower than 100%, we are drawing into a rect in the
         * coordinate system of the full image. One tile at 50% covers the width
         * (in original image coordinates) of two tiles at 100%. So at 50% we
         * need to stretch our tiles to double the width and height; at 25% we
         * need to stretch them to quadruple the width and height; and so on.
         * (Note that this means that we are drawing very blurry images as the
         * scale gets low. At 12.5%, our lowest scale, we are stretching about 6
         * small tiles to fill the entire original image area. But this is okay,
         * because the big blurry image we're drawing here will be scaled way
         * down before it is displayed.)
         */
        tileSize.setWidth(tileSize.getWidth() / scale);
        tileSize.setHeight(tileSize.getHeight() / scale);

        // calculate the rows and columns of tiles that intersect the rect we
        // have been asked to draw
        int firstCol = (int) Math.floor(rect.getMinX() / tileSize.getWidth());
        int lastCol = (int) Math.floor((rect.getMaxX() - 1) / tileSize.getWidth());
        int firstRow = (int) Math.floor(rect.getMinY() / tileSize.getHeight());
        int lastRow = (int) Math.floor((rect.getMaxY() - 1) / tileSize.getHeight());

        for (int row = firstRow; row <= lastRow; row++) {
            for (int col = firstCol; col <= lastCol; col++) {
                UIImage tile = getTile(scale, row, col);
                CGRect tileRect = new CGRect(tileSize.getWidth() * col, tileSize.getHeight() * row,
                        tileSize.getWidth(),
                        tileSize.getHeight());
                /*
                 * if the tile would stick outside of our bounds, we need to
                 * truncate it so as to avoid stretching out the partial tiles
                 * at the right and bottom edges
                 */
                tileRect = getBounds().intersection(tileRect);

                tile.draw(tileRect);
            }
        }
    }

    private UIImage getTile(double scale, int row, int col) {
        String tileName = String.format("Image Tiles/%s_%d_%d_%d", imageName, (int) (scale * 1000), col, row);
        String path = NSBundle.getMainBundle().findResourcePath(tileName, "png");
        UIImage image = UIImage.create(new File(path));
        return image;
    }
}
