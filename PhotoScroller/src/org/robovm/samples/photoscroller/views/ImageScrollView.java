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
 * Portions of this code is based on Apple Inc's PhotoScroller sample (v1.3)
 * which is copyright (C) 2010-2012 Apple Inc.
 */

package org.robovm.samples.photoscroller.views;

import java.io.File;

import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSPropertyListMutabilityOptions;
import org.robovm.apple.foundation.NSPropertyListSerialization;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIScrollView;
import org.robovm.apple.uikit.UIScrollViewDelegateAdapter;
import org.robovm.apple.uikit.UIView;

public class ImageScrollView extends UIScrollView {
    private static final boolean TILE_IMAGES = true; // turn on to use tiled images, if off, we use whole images

    private static NSArray<?> imageData;

    private UIImageView zoomView; // if tiling, this contains a very low-res placeholder image,
    // otherwise it contains the full image.
    private CGSize imageSize;
    private TilingView tilingView;

    private int index;

    private CGPoint pointToCenterAfterResize;
    private double scaleToRestoreAfterResize;

    public ImageScrollView () {
        setShowsHorizontalScrollIndicator(false);
        setShowsHorizontalScrollIndicator(false);
        setBouncesZoom(true);
        setDecelerationRate(UIScrollView.DecelerationRateFast());
        setDelegate(new UIScrollViewDelegateAdapter() {
            @Override
            public UIView getViewForZooming (UIScrollView scrollView) {
                return zoomView;
            }
        });
    }

    public void setIndex (int index) {
        this.index = index;

        if (TILE_IMAGES) {
            displayTiledImage(getImageName(index), getImageSize(index));
        } else {
            displayImage(getImage(index));
        }
    }

    @Override
    public void layoutSubviews () {
        super.layoutSubviews();

        // center the zoom view as it becomes smaller than the size of the screen
        CGSize boundsSize = getBounds().size();
        CGRect frameToCenter = zoomView.getFrame();

        // center horizontally
        if (frameToCenter.size().width() < boundsSize.width())
            frameToCenter.origin().x((boundsSize.width() - frameToCenter.size().width()) / 2);
        else
            frameToCenter.origin().x(0);

        // center vertically
        if (frameToCenter.size().height() < boundsSize.height())
            frameToCenter.origin().y((boundsSize.height() - frameToCenter.size().height()) / 2);
        else
            frameToCenter.origin().y(0);

        zoomView.setFrame(frameToCenter);
    }

    @Override
    public void setFrame (CGRect frame) {
        boolean sizeChanging = !frame.size().equalToSize(getFrame().size());
        if (sizeChanging) {
            prepareToResize();
        }
        super.setFrame(frame);

        if (sizeChanging) {
            recoverFromResizing();
        }
    }

    private void displayTiledImage (String imageName, CGSize imageSize) {
        // clear views for the previous image
        if (zoomView != null) {
            zoomView.removeFromSuperview();
            zoomView = null;
        }
        tilingView = null;

        // reset our zoomScale to 1.0 before doing any further calculations
        setZoomScale(1);

        // make views to display the new image
        zoomView = new UIImageView(new CGRect(CGPoint.Zero(), imageSize));
        zoomView.setImage(getPlaceholderImage(imageName));
        addSubview(zoomView);

        tilingView = new TilingView(imageName, imageSize);
        tilingView.setFrame(zoomView.getBounds());
        zoomView.addSubview(tilingView);

        configureForImageSize(imageSize);
    }

    private void displayImage (UIImage image) {
        // clear the previous image
        if (zoomView != null) {
            zoomView.removeFromSuperview();
            zoomView = null;
        }

        // reset our zoomScale to 1.0 before doing any further calculations
        setZoomScale(1);

        // make a new UIImageView for the new image
        zoomView = new UIImageView(image);
        addSubview(zoomView);

        configureForImageSize(image.getSize());
    }

    private void configureForImageSize (CGSize imageSize) {
        this.imageSize = imageSize;
        setContentSize(imageSize);
        setMaxMinZoomScalesForCurrentBounds();
        setZoomScale(getMinimumZoomScale());
    }

    private void setMaxMinZoomScalesForCurrentBounds () {
        CGSize boundsSize = getBounds().size();

        // calculate min/max zoomscale
        double xScale = boundsSize.width() / imageSize.width(); // the scale needed to perfectly fit the image width-wise
        double yScale = boundsSize.height() / imageSize.height(); // the scale needed to perfectly fit the image height-wise

        // fill width if the image and phone are both portrait or both landscape; otherwise take smaller scale
        boolean imagePortrait = imageSize.height() > imageSize.width();
        boolean phonePortrait = boundsSize.height() > boundsSize.width();
        double minScale = imagePortrait == phonePortrait ? xScale : Math.min(xScale, yScale);

        // on high resolution screens we have double the pixel density, so we will be seeing every pixel if we limit the
        // maximum zoom scale to 0.5.
        double maxScale = 1.0 / UIScreen.getMainScreen().getScale();

        // don't let minScale exceed maxScale. (If the image is smaller than the screen, we don't want to force it to be zoomed.)
        if (minScale > maxScale) {
            minScale = maxScale;
        }
        setMaximumZoomScale(maxScale);
        setMinimumZoomScale(minScale);
    }

    private void prepareToResize () {
        CGPoint boundsCenter = new CGPoint(getBounds().getMidX(), getBounds().getMidY());
        pointToCenterAfterResize = convertPointToView(boundsCenter, zoomView);
        scaleToRestoreAfterResize = getZoomScale();

        // If we're at the minimum zoom scale, preserve that by returning 0, which will be converted to the minimum
        // allowable scale when the scale is restored.
        if (scaleToRestoreAfterResize <= getMinimumZoomScale() + 1.19209290E-07F) {
            scaleToRestoreAfterResize = 0;
        }
    }

    private void recoverFromResizing () {
        setMaxMinZoomScalesForCurrentBounds();

        // Step 1: restore zoom scale, first making sure it is within the allowable range.
        double maxZoomScale = Math.max(getMinimumZoomScale(), scaleToRestoreAfterResize);
        setZoomScale(Math.min(getMaximumZoomScale(), maxZoomScale));

        // Step 2: restore center point, first making sure it is within the allowable range.

        // 2a: convert our desired center point back to our own coordinate space
        CGPoint boundsCenter = convertPointFromView(pointToCenterAfterResize, zoomView);

        // 2b: calculate the content offset that would yield that center point
        CGPoint offset = new CGPoint(boundsCenter.x() - getBounds().size().width() / 2.0, boundsCenter.y()
            - getBounds().size().height() / 2.0);

        // 2c: restore offset, adjusted to be within the allowable range
        CGPoint maxOffset = getMaximumContentOffset();
        CGPoint minOffset = getMinimumContentOffset();

        double realMaxOffset = Math.min(maxOffset.x(), offset.x());
        offset.x(Math.max(minOffset.x(), realMaxOffset));

        realMaxOffset = Math.min(maxOffset.y(), offset.y());
        offset.y(Math.max(minOffset.y(), realMaxOffset));

        setContentOffset(offset);
    }

    private CGPoint getMaximumContentOffset () {
        CGSize contentSize = getContentSize();
        CGSize boundsSize = getBounds().size();
        return new CGPoint(contentSize.width() - boundsSize.width(), contentSize.height() - boundsSize.height());
    }

    private CGPoint getMinimumContentOffset () {
        return CGPoint.Zero();
    }

    private static NSArray<?> getImageData () {
        if (imageData == null) {
            String path = NSBundle.getMainBundle().findResourcePath("ImageData", "plist");
            NSData plistData = NSData.read(new File(path));
            imageData = (NSArray<?>)NSPropertyListSerialization.getPropertyListFromData(plistData,
                NSPropertyListMutabilityOptions.Immutable);

            if (imageData == null) {
                System.err.println("Unable to read image data: "); // TODO error
            }
        }
        return imageData;
    }

    public static int getImageCount () {
        NSArray<?> imageData = getImageData();
        if (imageData == null) return 0;
        return imageData.size();
    }

    private static String getImageName (int index) {
        NSDictionary<NSString, NSObject> info = (NSDictionary<NSString, NSObject>)getImageData().get(index);
        return info.get(new NSString("name")).toString();
    }

    private static UIImage getImage (int index) {
        String imageName = getImageName(index);
        String path = NSBundle.getMainBundle().findResourcePath(String.format("Full_Images/%s", imageName), "jpg");
        return UIImage.create(new File(path));
    }

    private static CGSize getImageSize (int index) {
        NSDictionary<NSString, NSObject> info = (NSDictionary<NSString, NSObject>)getImageData().get(index);
        float width = Float.valueOf(info.get(new NSString("width")).toString());
        float height = Float.valueOf(info.get(new NSString("height")).toString());
        return new CGSize(width, height);
    }

    private static UIImage getPlaceholderImage (String name) {
        return UIImage.create(String.format("Placeholder_Images/%s_Placeholder", name));
    }

    public int getPageIndex () {
        return index;
    }
}
