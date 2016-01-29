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

import org.robovm.answerme.core.api.Topic;
import org.robovm.apple.foundation.NSObject;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.Property;

/**
 * Wraps a {@link Topic} instance so that it can be used from Objective-C.
 */
@CustomClass("AMTopicImpl")
public class AMTopicImpl extends NSObject {
    private final Topic topic;

    /**
     * Creates a new instance wrapping the specified {@link Topic}.
     */
    public AMTopicImpl(Topic topic) {
        this.topic = topic;
    }

    /**
     * Exposes the {@link Topic#text} field as an Objective-C property named
     * {@code text}.
     */
    @Property(selector = "text")
    public String getText() {
        return topic.text;
    }

    /**
     * Exposes the {@link Topic#getDisplayText()} method as an Objective-C
     * property named {@code displayText}.
     */
    @Property(selector = "displayText")
    public String getDisplayText() {
        return topic.getDisplayText();
    }

    /**
     * Exposes the {@link Topic#result} field as an Objective-C property named
     * {@code result}.
     */
    @Property(selector = "result")
    public String getResult() {
        return topic.result;
    }

    /**
     * Exposes the {@link Topic#icon} field as an Objective-C property named
     * {@code icon}.
     */
    @Property(selector = "icon")
    public AMIconImpl getIcon() {
        return new AMIconImpl(topic.icon);
    }

    /**
     * Returns {@link Topic#toString()} when {@code [o description]} is called
     * from the Objective-C side on an instance of this class.
     */
    @Override
    public String description() {
        return topic.toString();
    }
}
