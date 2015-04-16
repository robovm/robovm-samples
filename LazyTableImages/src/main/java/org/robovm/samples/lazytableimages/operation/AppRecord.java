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
 * Portions of this code is based on Apple Inc's LazyTableImages sample (v1.5)
 * which is copyright (C) 2010-2014 Apple Inc.
 */

package org.robovm.samples.lazytableimages.operation;

import org.robovm.apple.uikit.UIImage;

public class AppRecord {
    public String appName;
    public UIImage appIcon;
    public String artist;
    public String imageURLString;
    public String appURLString;

    @Override
    public String toString () {
        return appName + " " + artist;
    }
}
