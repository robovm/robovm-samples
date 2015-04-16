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
 * Portions of this code is based on Apple Inc's QuickContacts sample (v1.0)
 * which is copyright (C) 2008-2013 Apple Inc.
 * 
 * The view controller creates a few bulb views which host the custom layer subclass.
 */

package org.robovm.samples.customanimateproperty;

import org.robovm.apple.coreanimation.CAAnimation;
import org.robovm.apple.coreanimation.CAAnimationCalculationMode;
import org.robovm.apple.coreanimation.CABasicAnimation;
import org.robovm.apple.coreanimation.CAKeyframeAnimation;
import org.robovm.apple.coreanimation.CALayer;
import org.robovm.apple.coreanimation.CALayerDelegateAdapter;
import org.robovm.apple.coreanimation.CAMediaTimingFunction;
import org.robovm.apple.coreanimation.CAMediaTimingFunctionName;
import org.robovm.apple.coreanimation.CATransaction;
import org.robovm.apple.coregraphics.CGColor;
import org.robovm.apple.coregraphics.CGContext;
import org.robovm.apple.coregraphics.CGImage;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSMutableArray;
import org.robovm.apple.foundation.NSNumber;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSSet;
import org.robovm.apple.uikit.UIBezierPath;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIGraphics;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UITouch;
import org.robovm.apple.uikit.UIView;
import org.robovm.objc.annotation.Method;

/** View that hosts the custom CALayer subclass. Since the view hosts the layer in this case, it executes the animations when
 * explicit animations are the enabled animation trigger. */
public class BulbView extends UIView {

    private double red, green, blue;
    private boolean on;
    private CGContext offscreenContext;
    private CGContext currentContext;
    private UIImage image;
    private UIColor color;

    public BulbView () {
        super();
        // define a default frame.
        this.image = UIImage.create("bulb.png");
        this.setFrame(new CGRect(0, 0, this.image.getSize().getWidth(), this.image.getSize().getHeight()));
        System.err.println("bulbview");
        generalInit();
    }

    public BulbView (CGRect frame) {
        super(frame);
        generalInit();
    }

    // General bulb initialization.
    void generalInit () {

        // Grab the bulb image and log whether or not we succeeded to load the image.
        this.image = UIImage.create("bulb.png");

        // Get our layer to do a small custom configuration.
        CALayer layer = this.getLayer();
        this.addStrongRef(layer);
        BulbLayer.needsDisplay("brightness");

        layer.setDelegate(new CALayerDelegateAdapter() {
            @Override
            public void drawLayer (CALayer layer, CGContext ctx) {
                System.err.println("delegate called!");
                // super.drawLayer(layer, ctx);
                // Get the current state of the bulb's "brightness"
                // Core Animation is animating this value on our behalf.
                double brightness = ((BulbLayer)layer).getBrightness();

                // Calculate the bulbs current color (via RGB components) based
                // on
                // the bulb's current "brightness".
                double redDiff = 255 - red;
                double greenDiff = 255 - green;
                double blueDiff = 255 - blue;
                double curRed = red + redDiff * (brightness / 255.0);
                double curGreen = green + greenDiff * (brightness / 255.0);
                double curBlue = blue + blueDiff * (brightness / 255.0);

                // Start an offscreen graphics context
                UIGraphics.beginImageContext(image.getSize(), true, 1.0f);
                currentContext = UIGraphics.getCurrentContext();
                CGRect imageRect = currentContext.getClipBoundingBox();

                UIColor color = new UIColor(curRed / 255.0, curGreen / 255.0, curBlue / 255.0, 1.0);
                color.setFillAndStroke(); // @TODO check this

                UIBezierPath path = UIBezierPath.createFromRect(currentContext.getClipBoundingBox());
                path.fill();

                // Draw the bulb image into the context.
                currentContext.drawImage(imageRect, image.getCGImage());
                UIImage image = UIGraphics.getImageFromCurrentImageContext();

                UIGraphics.endImageContext();
                double[] maskingColors = new double[] {248.0, 255.0, 248.0, 255.0, 248.0, 255.0};
                CGImage finalImage = CGImage.createWithMaskingColors(image.getCGImage(), maskingColors);

                CGRect contextRect = currentContext.getClipBoundingBox();
                ctx.drawImage(contextRect, finalImage);
                System.err.println("delegate called!");
            }

        });

        // By setting opaque to NO it defines our backing store to include an alpha channel.
        layer.setOpaque(false);
        // The default bulb color is red.
        this.setColor(UIColor.red());
        System.err.println("General Init");
    }

    // When a bulb color is set we define the color components used during our custom animation
    public void setColor (UIColor color) {
        this.color = color;
        CGColor cgColor = this.color.getCGColor();
        double[] colors = cgColor.getComponentsD();
        this.red = colors[0] * 255.0;
        this.green = colors[1] * 255.0;
        this.blue = colors[2] * 255.0;
    }

    @Method(selector = "layerClass")
    public static Class<? extends CALayer> getLayerClass () {
        System.err.println("gets layer class");
        return BulbLayer.class;
    }

    /** setOn
     * @param on
     * @param animated */
    private void setOn (boolean on, boolean animated) {
        System.err.println("on is set maybe");
        if (!animated) {
            // if (this.getLayer() instanceof BulbLayer) {
            ((BulbLayer)this.getLayer()).setBrightness((on == true) ? 255 : 0);
            // }
            return;
        }
        if (on) {
            if (this.on) {
                return;
            }
            this.on = on;
            if (Constants.ANIMATION_TRIGGER_EXPLICIT) {
                this.animateFrom(0.0, 255.0);
            }
        } else {
            if (!this.on) {
                return;
            }
            this.on = on;

            if (Constants.ANIMATION_TRIGGER_EXPLICIT) {
                this.animateFrom(255.0, 0.0);
            }
        }

        if (Constants.ANIMATION_TRIGGER_EXPLICIT) {
            CATransaction.begin();
            CATransaction.setDisablesActions(true);
        }

        // if (this.getLayer() instanceof BulbLayer) {
        ((BulbLayer)this.getLayer()).setBrightness((on == true) ? 255 : 0);
        // }

        if (Constants.ANIMATION_TRIGGER_EXPLICIT) {
            CATransaction.commit();
        }

    }

    private void animateFrom (double from, double to) {

        CAAnimation theAnimation = null;
        if (Constants.ANIMATION_TYPE_KEYFRAME) {

            // Create a basic interpolation for "briteness" animation
            theAnimation = CAKeyframeAnimation.create();
            theAnimation.setDuration(1.0);

            // Hint: the previous value of the property is stored in the presentationLayer
            // Since for implicit animations, the model property is already set to the new value.
            ((CAKeyframeAnimation)theAnimation).setCalculationMode(CAAnimationCalculationMode.Discrete);
            NSMutableArray<NSObject> animationValues = new NSMutableArray<>();
            animationValues.add(NSNumber.valueOf(0.0));
            animationValues.add(NSNumber.valueOf(255.0));
            animationValues.add(NSNumber.valueOf(0.0));
            animationValues.add(NSNumber.valueOf(255.0));
            animationValues.add(NSNumber.valueOf(0.0));
            animationValues.add(NSNumber.valueOf(255.0));
            animationValues.add(NSNumber.valueOf(0.0));
            animationValues.add(NSNumber.valueOf(255.0));
            animationValues.add(NSNumber.valueOf(0.0));
            animationValues.add(NSNumber.valueOf(255.0));
            animationValues.add(NSNumber.valueOf(0.0));
            animationValues.add(NSNumber.valueOf(255.0));
            animationValues.add(NSNumber.valueOf(0.0));
            animationValues.add(NSNumber.valueOf(255.0));
            animationValues.add(NSNumber.valueOf(0.0));
            animationValues.add(NSNumber.valueOf(255.0));

        } else {

            theAnimation = CABasicAnimation.create();
            theAnimation.setDuration(1.0);
            theAnimation.setTimingFunction(CAMediaTimingFunction.create(CAMediaTimingFunctionName.EaseOut));

            ((CABasicAnimation)theAnimation).setFromValue(NSNumber.valueOf(from));
            ((CABasicAnimation)theAnimation).setToValue(NSNumber.valueOf(to));

        }
        this.getLayer().addAnimation(theAnimation, "brightness");
    }

    @Override
    public void touchesBegan (NSSet<UITouch> touches, UIEvent event) {
        System.err.println("touch");
        this.setOn(this.on, true);
    }

}
