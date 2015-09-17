/*
 * Copyright (C) 2015 RoboVM AB
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
 * Portions of this code is based on Apple Inc's MetalBasic3D sample (v1.0)
 * which is copyright (C) 2014 Apple Inc.
 */
package org.robovm.samples.metalbasic3d;

public interface MetalBasic3DViewControllerDelegate {

    /**
     * Note this method is called from the thread the main game loop is run
     */
    void update(MetalBasic3DViewController controller);

    /**
     * called whenever the main game loop is paused, such as when the app is
     * backgrounded
     */
    void willPause(MetalBasic3DViewController controller, boolean pause);

}
