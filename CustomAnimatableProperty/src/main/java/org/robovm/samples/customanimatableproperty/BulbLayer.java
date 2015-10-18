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

package org.robovm.samples.customanimatableproperty;

import org.robovm.apple.coreanimation.CAAction;
import org.robovm.apple.coreanimation.CAAnimationCalculationMode;
import org.robovm.apple.coreanimation.CABasicAnimation;
import org.robovm.apple.coreanimation.CAKeyframeAnimation;
import org.robovm.apple.coreanimation.CALayer;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSMutableArray;
import org.robovm.apple.foundation.NSNumber;
import org.robovm.apple.foundation.NSObject;
import org.robovm.objc.annotation.Method;

/** The custom CALayer subclass which implements a custom implicitly animatable property. */
public class BulbLayer extends CALayer {

    private float brightness;

    /** For CALayer subclasses, always support initWithLayer: by copying over custom properties. */
    @Override
    protected long init (CALayer layer) {
        System.err.println("ujujuj");
        if (layer instanceof BulbLayer) {
            this.brightness = ((BulbLayer)layer).brightness;
        }
        return super.init(layer);
    }

    /** Instruct to Core Animation that a change in the custom "brightness" property should automatically trigger a redraw of the
     * layer
     * @param key
     * @return */
    @Method(selector = "needsDisplayForKey:")
    public static boolean needsDisplay (String key) {
        System.err.println("needs display");
        if (key.equals("brightness")) {
            return true;
        }
        return CALayer.needsDisplay(key);
    }

    /** Needed to support implicit animation of this property. Return the basic animation the implicit animation will leverage. */
    @Override
    public CAAction getAction (String event) {
        System.err.printf("event: %@", event);

        if (event.equals("brightness")) {

            if (Constants.ANIMATION_TYPE_KEYFRAME) {
                // Create a basic interpolation for "briteness" animation
                CAKeyframeAnimation theAnimation = new CAKeyframeAnimation();
                theAnimation.setKeyPath(event);

                // Hint: the previous value of the property is stored in the presentationLayer
                // Since for implicit animations, the model property is already set to the new value.
                theAnimation.setCalculationMode(CAAnimationCalculationMode.Discrete);
                NSArray<NSObject> animationValues = new NSMutableArray<>();

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

                theAnimation.setValues(animationValues);

            } else {

                // Create a basic interpolation for "brightness" animation
                CABasicAnimation theAnimation = new CABasicAnimation();
                // Hint: the previous value of the property is stored in the presentationLayer
                // Since for implicit animations, the model property is already set to the new value.
                theAnimation.setFromValue(getPresentationLayer().getKeyValueCoder().getValue(event));
                return theAnimation;

            }

        }
        return super.getAction(event);
    }

    public float getBrightness () {
        return brightness;
    }

    public void setBrightness (float brightness) {
        this.brightness = brightness;
    }

}
