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
 * Portions of this code is based on Apple Inc's Adventure sample (v1.3)
 * which is copyright (C) 2013-2014 Apple Inc.
 */

package org.robovm.samples.adventure.ai;

import org.robovm.samples.adventure.sprites.APACharacter;

public abstract class APAArtificialIntelligence {
    final APACharacter character;
    APACharacter target;

    public APAArtificialIntelligence (APACharacter character, APACharacter target) {
        this.character = character;
        this.target = target;
    }

    public abstract void update (double timeInterval);

    public void clearTarget (APACharacter target) {
        if (this.target == target) {
            this.target = null;
        }
    }

    public APACharacter getTarget () {
        return target;
    }
}
