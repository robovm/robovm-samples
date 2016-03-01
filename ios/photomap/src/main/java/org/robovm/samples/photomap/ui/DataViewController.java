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
 * Portions of this code is based on Apple Inc's PhotoMap sample (v1.1)
 * which is copyright (C) 2011-2014 Apple Inc.
 */
package org.robovm.samples.photomap.ui;

import java.io.File;

import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;
import org.robovm.samples.photomap.PhotoAnnotation;

@CustomClass("DataViewController")
public class DataViewController extends UIViewController {
    private PhotoAnnotation dataObject;
    @IBOutlet
    private UIImageView imageView;

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);

        // we want for the title to only be the image name (obtained from the
        // file system path)
        String title = new File(dataObject.getImagePath()).getName();
        setTitle(title);

        imageView.setImage(dataObject.getImage());
    }

    public void setDataObject(PhotoAnnotation dataObject) {
        this.dataObject = dataObject;
    }

    public PhotoAnnotation getDataObject() {
        return dataObject;
    }
}
