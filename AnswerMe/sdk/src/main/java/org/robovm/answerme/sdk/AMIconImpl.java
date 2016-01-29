/*
 * Copyright (C) 2015 RoboVM AB
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
package org.robovm.answerme.sdk;

import org.robovm.answerme.core.api.Icon;
import org.robovm.apple.foundation.NSObject;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.Property;

/**
 * Wraps an {@link Icon} instance so that it can be used from Objective-C.
 */
@CustomClass("AMIconImpl")
public class AMIconImpl extends NSObject {
    private final Icon icon;

    /**
     * Creates a new instance wrapping the specified {@link Icon}.
     */
    public AMIconImpl(Icon icon) {
        this.icon = icon;
    }

    /**
     * Exposes the {@link Icon#url} field as an Objective-C property named
     * {@code url}.
     */
    @Property(selector = "url")
    public String getUrl() {
        return icon.url;
    }

    /**
     * Returns {@link Icon#toString()} when {@code [o description]} is called
     * from the Objective-C side on an instance of this class.
     */
    @Override
    public String description() {
        return icon.toString();
    }
}
