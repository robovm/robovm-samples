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
 * Portions of this code is based on Apple Inc's SamplePhotosApp sample (v2.0)
 * which is copyright (C) 2014 Apple Inc.
 */

package org.robovm.samples.samplephotosapp.ui;

import org.robovm.apple.uikit.UICollectionViewCell;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;

@CustomClass("AAPLGridViewCell")
public class AAPLGridViewCell extends UICollectionViewCell {
    private UIImage thumbnailImage;
    private UIImageView imageView;

    public void setThumbnailImage(UIImage thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
        imageView.setImage(thumbnailImage);
    }

    public UIImage getThumbnailImage() {
        return thumbnailImage;
    }

    @IBOutlet
    private void setImageView(UIImageView imageView) {
        this.imageView = imageView;
    }
}
