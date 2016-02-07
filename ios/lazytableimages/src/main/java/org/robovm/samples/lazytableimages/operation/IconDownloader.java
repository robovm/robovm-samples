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
 * Portions of this code is based on Apple Inc's LazyTableImages sample (v1.5)
 * which is copyright (C) 2010-2014 Apple Inc.
 */

package org.robovm.samples.lazytableimages.operation;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSMutableData;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.foundation.NSURLConnection;
import org.robovm.apple.foundation.NSURLConnectionDataDelegateAdapter;
import org.robovm.apple.foundation.NSURLRequest;
import org.robovm.apple.foundation.NSURLResponse;
import org.robovm.apple.uikit.UIGraphics;
import org.robovm.apple.uikit.UIImage;
import org.robovm.objc.block.VoidBlock1;

public class IconDownloader {
    private static final double APP_ICON_SIZE = 48;

    private final AppRecord appRecord;
    private final VoidBlock1<AppRecord> completionHandler;
    private NSMutableData activeDownload;
    private NSURLConnection imageConnection;

    public IconDownloader (AppRecord appRecord, VoidBlock1<AppRecord> completionHandler) {
        this.appRecord = appRecord;
        this.completionHandler = completionHandler;
    }

    public void startDownload () {
        NSURLRequest request = new NSURLRequest(new NSURL(appRecord.imageURLString));
        imageConnection = new NSURLConnection(request, new NSURLConnectionDataDelegateAdapter() {
            @Override
            public void didReceiveResponse (NSURLConnection connection, NSURLResponse response) {
                activeDownload = new NSMutableData();
            }

            @Override
            public void didReceiveData (NSURLConnection connection, NSData data) {
                activeDownload.append(data);
            }

            @Override
            public void didFail (NSURLConnection connection, NSError error) {
                // Clear the activeDownload property to allow later attempts
                activeDownload = null;

                // Release the connection now that it's finished
                imageConnection = null;
            }

            @Override
            public void didFinishLoading (NSURLConnection connection) {
                // Set appIcon and clear temporary data/image
                UIImage image = new UIImage(activeDownload);

                if (image.getSize().getWidth() != APP_ICON_SIZE || image.getSize().getHeight() != APP_ICON_SIZE) {
                    CGSize itemSize = new CGSize(APP_ICON_SIZE, APP_ICON_SIZE);
                    UIGraphics.beginImageContext(itemSize, false, 0.0f);
                    CGRect imageRect = new CGRect(0, 0, itemSize.getWidth(), itemSize.getHeight());
                    image.draw(imageRect);
                    appRecord.appIcon = UIGraphics.getImageFromCurrentImageContext();
                    UIGraphics.endImageContext();
                } else {
                    appRecord.appIcon = image;
                }

                activeDownload = null;

                // Release the connection now that it's finished
                imageConnection = null;

                // call our delegate and tell it that our icon is ready for display
                if (completionHandler != null) {
                    completionHandler.invoke(appRecord);
                }
            }
        });
    }

    public void cancelDownload () {
        imageConnection.cancel();
        imageConnection = null;
        activeDownload = null;
    }
}
