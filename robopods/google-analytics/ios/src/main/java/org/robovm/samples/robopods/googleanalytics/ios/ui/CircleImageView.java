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
 * Portions of this code is based on Google Inc's Google Analytics sample
 * which is copyright (C) 2015 Google Inc.
 */
package org.robovm.samples.robopods.googleanalytics.ios.ui;

import org.robovm.apple.coreanimation.CALayer;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSCoder;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIViewContentMode;
import org.robovm.objc.annotation.CustomClass;

/**
 * CircleImageView is a UIImageView subclass that renders an image inside a
 * circle that has a drop shadow. It should be given equal width and height.
 */
@CustomClass("CircleImageView")
public class CircleImageView extends UIImageView {

    @Override
    protected long init(CGRect frame) {
        long handle = super.init(frame);
        sharedInit(); // TODO we need a fix, so we can use the constructor
                      // instead.
        return handle;
    }

    @Override
    protected long init(NSCoder aDecoder) {
        long handle = super.init(aDecoder);
        sharedInit();
        return handle;
    }

    private void sharedInit() {
        setBackgroundColor(UIColor.white());
        super.setContentMode(UIViewContentMode.Center);

        CALayer layer = getLayer();
        layer.setShadowOffset(new CGSize(0, 2));
        layer.setShadowOpacity(0.25f);
        layer.setShadowColor(UIColor.gray().getCGColor());
        layer.setShadowRadius(4.0);
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();

        CGSize size = getBounds().getSize();
        double dim = Math.max(size.getWidth(), size.getHeight());
        getLayer().setCornerRadius(dim / 2);
    }

    @Override
    public void setContentMode(UIViewContentMode v) {
        // ignore
    }
}
