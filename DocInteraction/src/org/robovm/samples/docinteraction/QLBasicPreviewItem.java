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
 * Portions of this code is based on Apple Inc's DocInteraction sample (v1.6)
 * which is copyright (C) 2010-2014 Apple Inc.
 */

package org.robovm.samples.docinteraction;

import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.quicklook.QLPreviewItem;

public class QLBasicPreviewItem extends NSObject implements QLPreviewItem {
    private final NSURL url;

    public QLBasicPreviewItem (NSURL url) {
        this.url = url;
    }

    @Override
    public NSURL getURL () {
        return url;
    }

    @Override
    public String getTitle () {
        return url.getLastPathComponent();
    }
}
