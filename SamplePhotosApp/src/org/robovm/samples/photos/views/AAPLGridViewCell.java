/*
 * Copyright (C) 2015 Trillian Mobile AB
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
 * Portions of this code is based on Apple Inc's Example app using Photos framework (v2.0)
 * which is copyright (C) 2014 Apple Inc.
 */

package org.robovm.samples.photos.views;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UICollectionViewCell;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewContentMode;

public class AAPLGridViewCell extends UICollectionViewCell {
    private UIImageView imageView;

    public void setThumbnailImage (UIImage thumbnailImage) {
        if (imageView == null) {
            UIView contentView = getContentView();
            contentView.setBackgroundColor(UIColor.black());

            imageView = new UIImageView(new CGRect(0, 0, 80, 80));
            imageView.setContentMode(UIViewContentMode.ScaleAspectFill);
            imageView.setClipsToBounds(true);
            contentView.addSubview(imageView);
        }
        imageView.setImage(thumbnailImage);
    }
}
