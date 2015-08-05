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
 * Portions of this code is based on Parse's AnyPic sample
 * which is copyright (C) 2013 Parse.
 */
package org.robovm.samples.robopods.parse.anypic.ios.ui.views;

import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.pods.parse.PFFile;
import org.robovm.pods.parse.PFGetDataCallback;
import org.robovm.samples.robopods.parse.anypic.ios.util.Log;

public class PAPImageView extends UIImageView {
    private String url;

    public void setFile(PFFile file) {
        final String requestURL = file.getUrl(); // Save copy of url locally
        url = file.getUrl(); // Save copy of url on the instance

        file.getDataInBackground(new PFGetDataCallback() {
            @Override
            public void done(NSData data, NSError error) {
                if (error == null) {
                    UIImage image = UIImage.create(data);
                    if (requestURL.equals(url)) {
                        setImage(image);
                        setNeedsDisplay();
                    }
                } else {
                    Log.e("Error on fetching file: %s", error);
                }
            }
        });
    }
}
