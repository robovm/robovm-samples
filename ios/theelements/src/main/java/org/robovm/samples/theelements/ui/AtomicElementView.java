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
 * Portions of this code is based on Apple Inc's TheElements sample (v1.12)
 * which is copyright (C) 2008-2013 Apple Inc.
 */

package org.robovm.samples.theelements.ui;

import org.robovm.apple.coregraphics.CGBitmapContext;
import org.robovm.apple.coregraphics.CGBitmapInfo;
import org.robovm.apple.coregraphics.CGColor;
import org.robovm.apple.coregraphics.CGColorSpace;
import org.robovm.apple.coregraphics.CGGradient;
import org.robovm.apple.coregraphics.CGGradientDrawingOptions;
import org.robovm.apple.coregraphics.CGImage;
import org.robovm.apple.coregraphics.CGImageAlphaInfo;
import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.NSAttributedStringAttributes;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIGestureRecognizer;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UITapGestureRecognizer;
import org.robovm.apple.uikit.UIView;
import org.robovm.samples.theelements.model.AtomicElement;

public class AtomicElementView extends UIView {
    private AtomicElementViewController viewController;
    AtomicElement element;

    /**
     * @return the preferred size of this view is the size of the background
     *         image
     */
    public static CGSize getPreferredViewSize() {
        return new CGSize(256, 256);
    }

    public AtomicElementView(CGRect frame) {
        super(frame);
        // set the background color of the view to clear
        setBackgroundColor(UIColor.clear());

        // attach a tap gesture recognizer to this view so it can flip
        UITapGestureRecognizer tapGestureRecognizer = new UITapGestureRecognizer(
                new UIGestureRecognizer.OnGestureListener() {
                    @Override
                    public void onGesture(UIGestureRecognizer gestureRecognizer) {
                        // when a tap gesture occurs tell the view controller to
                        // flip this view to the
                        // back and show the AtomicElementFlippedView instead
                        viewController.flipCurrentView();
                    }
                });
        addGestureRecognizer(tapGestureRecognizer);
    }

    @Override
    public boolean canBecomeFirstResponder() {
        return true;
    }

    @Override
    public void draw(CGRect rect) {
        // get the background image for the state of the element
        // position it appropriately and draw the image
        UIImage backgroundImage = element.getStateImageForAtomicElementView();
        CGRect elementSymbolRectangle = new CGRect(0, 0, backgroundImage.getSize().getWidth(), backgroundImage
                .getSize()
                .getHeight());
        backgroundImage.draw(elementSymbolRectangle);

        // all the text is drawn in white
        UIColor.white().setFillAndStroke();

        // draw the element name
        NSAttributedStringAttributes attrs = new NSAttributedStringAttributes().setForegroundColor(UIColor.white());
        attrs.setFont(UIFont.getBoldSystemFont(36));

        CGSize stringSize = NSString.getSize(element.getName(), attrs);
        CGPoint point = new CGPoint((getBounds().getSize().getWidth() - stringSize.getWidth()) / 2, 256 / 2 - 50);
        NSString.draw(element.getName(), point, attrs);

        // draw the element number
        attrs.setFont(UIFont.getBoldSystemFont(48));
        point = new CGPoint(10, 0);
        NSString.draw(String.valueOf(element.getAtomicNumber()), point, attrs);

        // draw the element symbol
        attrs.setFont(UIFont.getBoldSystemFont(96));
        stringSize = NSString.getSize(element.getSymbol(), attrs);
        point = new CGPoint((getBounds().getSize().getWidth() - stringSize.getWidth()) / 2, 256 - 120);
        NSString.draw(element.getSymbol(), point, attrs);
    }

    public static CGImage createGradientImage(int pixelsWide, int pixelsHigh) {
        CGImage image = null;

        // Our gradient is always black-white and the mask
        // must be in the gray colorspace
        CGColorSpace colorSpace = CGColorSpace.createDeviceGray();

        // create the bitmap context
        CGBitmapContext gradientBitmapContext = CGBitmapContext.create(pixelsWide, pixelsHigh, 8, 0, colorSpace,
                new CGBitmapInfo(CGImageAlphaInfo.None.value()));
        if (gradientBitmapContext != null) {
            // define the start and end grayscale values (with the alpha, even
            // though
            // our bitmap context doesn't support alpha the gradient requires
            // it)
            CGColor[] colors = new CGColor[] { CGColor.create(colorSpace, new double[] { 0.0, 1.0 }),
                CGColor.create(colorSpace, new double[] { 1.0, 1.0 }) };
            // create the CGGradient
            CGGradient grayScaleGradient = CGGradient.create(colorSpace, colors, (double[]) null);
            // create the start and end points for the gradient vector (straight
            // down)
            CGPoint gradientStartPoint = CGPoint.Zero();
            CGPoint gradientEndPoint = new CGPoint(0, pixelsHigh);
            // draw the gradient into the gray bitmap context
            gradientBitmapContext.drawLinearGradient(grayScaleGradient, gradientStartPoint, gradientEndPoint,
                    CGGradientDrawingOptions.AfterEndLocation);
            // convert the context into a CGImage
            image = gradientBitmapContext.toImage();
        }

        // return the image containing the gradient
        return image;
    }

    public UIImage getReflectedImageRepresentation(int height) {
        CGColorSpace colorSpace = CGColorSpace.createDeviceRGB();

        // create a bitmap graphics context the size of the image
        CGBitmapContext mainViewContentContext = CGBitmapContext.create((long) getBounds().getSize().getWidth(),
                height, 8, 0,
                colorSpace, new CGBitmapInfo(CGImageAlphaInfo.PremultipliedLast.value()));

        if (mainViewContentContext == null)
            return null;
        // offset the context. This is necessary because, by default, the layer
        // created by a view for
        // caching its content is flipped. But when you actually access the
        // layer content and have
        // it rendered it is inverted. Since we're only creating a context the
        // size of our
        // reflection view (a fraction of the size of the main view) we have to
        // translate the context the
        // delta in size, render it, and then translate back
        double translateVertical = getBounds().getSize().getHeight() - height;
        mainViewContentContext.translateCTM(0, -translateVertical);

        // render the layer into the bitmap context
        getLayer().render(mainViewContentContext);

        // translate the context back
        mainViewContentContext.translateCTM(0, translateVertical);

        // Create CGImage of the main view bitmap content
        CGImage mainViewContentBitmapContext = mainViewContentContext.toImage();

        // create a 2 bit CGImage containing a gradient that will be used for
        // masking the
        // main view content to create the 'fade' of the reflection. The
        // CGImageCreateWithMask
        // function will stretch the bitmap image as required, so we can create
        // a 1 pixel wide gradient
        CGImage gradientMaskImage = createGradientImage(1, height);

        // Create an image by masking the bitmap of the mainView content with
        // the gradient view
        CGImage reflectionImage = CGImage.createWithMask(mainViewContentBitmapContext, gradientMaskImage);

        // convert the finished reflection image to a UIImage
        UIImage image = new UIImage(reflectionImage);

        return image;
    }

    public void setViewController(AtomicElementViewController viewController) {
        this.viewController = viewController;
    }

    public void setElement(AtomicElement element) {
        this.element = element;
    }
}
