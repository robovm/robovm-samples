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
 * Portions of this code is based on Parse's AnyPic sample
 * which is copyright (C) 2013 Parse.
 */
package org.robovm.samples.robopods.parse.anypic.ios.model;

public enum PAPActivityType {
    LIKE("like", "liked your photo"),
    FOLLOW("follow", "started following you"),
    COMMENT("comment", "commented on your photo"),
    JOINED("joined", "joined Anypic");

    private String key;
    private String message;

    PAPActivityType(String key, String message) {
        this.key = key;
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public String getMessage() {
        return message;
    }

    protected static PAPActivityType findByKey(String key) {
        if (key == null) {
            throw new NullPointerException("key");
        }
        PAPActivityType[] values = values();
        for (PAPActivityType type : values) {
            if (key.equals(type.key)) {
                return type;
            }
        }
        return null;
    }
}
