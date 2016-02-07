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
 * Portions of this code is based on Apple Inc's PhotoScroller sample (v1.3)
 * which is copyright (C) 2010-2012 Apple Inc.
 */

package org.robovm.samples.photoscroller.ui;

import org.robovm.apple.uikit.UIInterfaceOrientationMask;
import org.robovm.apple.uikit.UIViewAutoresizing;
import org.robovm.apple.uikit.UIViewController;

public class PhotoViewController extends UIViewController {
    private PhotoViewController(int pageIndex) {
        ImageScrollView scrollView = new ImageScrollView();
        scrollView.setIndex(pageIndex);
        scrollView.setAutoresizingMask(UIViewAutoresizing.with(UIViewAutoresizing.FlexibleWidth,
                UIViewAutoresizing.FlexibleHeight));
        setView(scrollView);
    }

    public static PhotoViewController create(int pageIndex) {
        if (pageIndex >= 0 && pageIndex < ImageScrollView.getImageCount()) {
            return new PhotoViewController(pageIndex);
        }
        return null;
    }

    public int getPageIndex() {
        ImageScrollView scrollView = (ImageScrollView) getView();
        return scrollView.getPageIndex();
    }

    // (this can also be defined in Info.plist via
    // UISupportedInterfaceOrientations)
    @Override
    public UIInterfaceOrientationMask getSupportedInterfaceOrientations() {
        return UIInterfaceOrientationMask.AllButUpsideDown;
    }
}
