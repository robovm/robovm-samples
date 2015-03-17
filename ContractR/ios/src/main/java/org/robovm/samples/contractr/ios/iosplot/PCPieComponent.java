/*
 * Copyright (C) 2014 RoboVM AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robovm.samples.contractr.ios.iosplot;

import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.uikit.UIColor;
import org.robovm.objc.annotation.Method;
import org.robovm.objc.annotation.NativeClass;
import org.robovm.objc.annotation.Property;
import org.robovm.rt.bro.annotation.Library;
import org.robovm.rt.bro.annotation.Pointer;

/**
 * 
 */
@Library(Library.INTERNAL)
@NativeClass
public class PCPieComponent extends NSObject {

    @Property
    public native float getValue();

    @Property
    public native void setValue(float value);

    @Property
    public native UIColor getColour();

    @Property
    public native void setColour(UIColor colour);

    @Property
    public native String getTitle();

    @Property
    public native void setTitle(String title);

    public PCPieComponent(String title, float value) {
        super((SkipInit) null);
        initObject(initWithTitle$value(title, value));
    }

    public PCPieComponent(String title, float value, UIColor colour) {
        this(title, value);
        setColour(colour);
    }
    
    @Method(selector = "initWithTitle:value:")
    private native @Pointer long initWithTitle$value(String title, float value);
}
