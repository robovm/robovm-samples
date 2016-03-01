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

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.NSAttributedStringAttributes;
import org.robovm.apple.uikit.NSStringDrawingOptions;
import org.robovm.apple.uikit.UIActivityIndicatorView;
import org.robovm.apple.uikit.UIActivityIndicatorViewStyle;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIView;
import org.robovm.objc.block.VoidBooleanBlock;

public class LoadingStatus extends UIView {
    private final UIActivityIndicatorView progress;
    private final UILabel loadingLabel;

    public static LoadingStatus getDefaultLoadingStatus(double width) {
        return new LoadingStatus(new CGRect(0, 0, width, 40));
    }

    public LoadingStatus(CGRect frame) {
        super(frame);

        setBackgroundColor(UIColor.fromRGBA(0, 0, 0, 0.4));
        String loadingString = "Loading Photos…";

        UIFont loadingFont = UIFont.getBoldSystemFont(17);

        NSAttributedStringAttributes attrs = new NSAttributedStringAttributes().setFont(loadingFont);
        CGRect rect = new NSString(loadingString).getBoundingRect(new CGSize(frame.getWidth(), frame.getHeight()),
                NSStringDrawingOptions.with(NSStringDrawingOptions.UsesLineFragmentOrigin,
                        NSStringDrawingOptions.UsesFontLeading), attrs, null);
        CGSize labelSize = rect.getSize();

        double centerX = Math.floor((frame.getWidth() / 2) - (labelSize.getWidth() / 2));
        double centerY = Math.floor((frame.getHeight() / 2) - (labelSize.getHeight() / 2));
        loadingLabel = new UILabel(new CGRect(centerX, centerY, labelSize.getWidth(), labelSize.getHeight()));
        loadingLabel.setBackgroundColor(UIColor.clear());
        loadingLabel.setTextColor(UIColor.white());
        loadingLabel.setText(loadingString);
        loadingLabel.setFont(loadingFont);

        progress = new UIActivityIndicatorView(UIActivityIndicatorViewStyle.White);
        CGRect progressFrame = progress.getFrame();
        progressFrame.getOrigin().setX(centerX - progressFrame.getWidth() - 8);
        progressFrame.getOrigin().setY(centerY);
        progress.setFrame(progressFrame);

        addSubview(progress);
        addSubview(loadingLabel);
    }

    @Override
    public void willRemoveSubview(UIView subview) {
        if (subview == progress)
            progress.stopAnimating();

        super.willRemoveSubview(subview);
    }

    @Override
    public void didMoveToWindow() {
        super.didMoveToWindow();

        progress.startAnimating();
    }

    public void removeFromSuperviewWithFade() {
        UIView.animate(0.3, new Runnable() {
            @Override
            public void run() {
                setAlpha(0);
            }
        }, new VoidBooleanBlock() {
            @Override
            public void invoke(boolean finished) {
                if (finished) {
                    removeFromSuperview();
                }
            }
        });
    }
}
