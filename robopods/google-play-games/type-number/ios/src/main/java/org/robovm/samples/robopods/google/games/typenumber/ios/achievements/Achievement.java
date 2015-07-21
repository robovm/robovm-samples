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
 * Portions of this code is based on Google Inc's Google Play Games 'Type a Number' sample
 * which is copyright (C) 2015 Google Inc.
 */
package org.robovm.samples.robopods.google.games.typenumber.ios.achievements;

public enum Achievement {
    Prime("CgkI3vnvkfoOEAIQAQ"),
    Bored("CgkI3vnvkfoOEAIQAg"),
    Humble("CgkI3vnvkfoOEAIQAw"),
    Cocky("CgkI3vnvkfoOEAIQBA"),
    Leet("CgkI3vnvkfoOEAIQBQ"),
    ReallyBored("CgkI3vnvkfoOEAIQBg");

    private String id;

    Achievement(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
