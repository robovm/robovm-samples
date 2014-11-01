/*
 * Copyright (C) 2014 Trillian Mobile AB
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
 * Portions of this code is based on Apple Inc's MoviePlayer sample (v1.5)
 * which is copyright (C) 2008-2014 Apple Inc.
 */

package org.robovm.samples.movieplayer.viewcontrollers;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.NSLineBreakMode;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIButtonType;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewContentMode;
import org.robovm.apple.uikit.UIViewController;

public class MyOverlayViewController extends UIViewController {
    private final UIButton closeButton;
    private final UILabel moviePlaybackStateText;
    private final UILabel movieLoadStateText;

    public MyOverlayViewController (final MyMovieViewController movieViewController) {
        UIView view = getView();
        view.setFrame(new CGRect(0, 0, 123, 57));
        view.setAlpha(0.8);
        view.setBackgroundColor(UIColor.fromWhiteAlpha(0.33, 1));

        closeButton = UIButton.create(UIButtonType.RoundedRect);
        closeButton.setFrame(new CGRect(26, 32, 74, 18));
        closeButton.setTitle("Close Movie", UIControlState.Normal);
        closeButton.setTitleColor(UIColor.white(), UIControlState.Highlighted);
        closeButton.getTitleLabel().setFont(UIFont.getSystemFont(11));
        closeButton.addOnTouchUpInsideListener(new UIControl.OnTouchUpInsideListener() {
            @Override
            public void onTouchUpInside (UIControl control, UIEvent event) {
                movieViewController.closeOverlay();
            }
        });
        view.addSubview(closeButton);

        UILabel playStateLabel = new UILabel(new CGRect(8, 15, 51, 15));
        playStateLabel.setText("Play State:");
        playStateLabel.setTextColor(UIColor.darkText());
        playStateLabel.setFont(UIFont.getSystemFont(9));
        view.addSubview(playStateLabel);

        moviePlaybackStateText = new UILabel(new CGRect(57, 18, 62, 11));
        moviePlaybackStateText.setTextColor(UIColor.darkText());
        moviePlaybackStateText.setContentMode(UIViewContentMode.Left);
        moviePlaybackStateText.setFont(UIFont.getSystemFont(9));
        moviePlaybackStateText.setLineBreakMode(NSLineBreakMode.TruncatingTail);
        view.addSubview(moviePlaybackStateText);

        UILabel loadStateLabel = new UILabel(new CGRect(5, 3, 51, 15));
        loadStateLabel.setText("Load State:");
        loadStateLabel.setTextColor(UIColor.darkText());
        loadStateLabel.setFont(UIFont.getSystemFont(9));
        view.addSubview(loadStateLabel);

        movieLoadStateText = new UILabel(new CGRect(57, 6, 62, 11));
        movieLoadStateText.setTextColor(UIColor.darkText());
        movieLoadStateText.setContentMode(UIViewContentMode.Left);
        movieLoadStateText.setFont(UIFont.getSystemFont(9));
        movieLoadStateText.setLineBreakMode(NSLineBreakMode.TruncatingTail);
        view.addSubview(movieLoadStateText);
    }

    public void setPlaybackStateDisplayString (String playBackText) {
        moviePlaybackStateText.setText(playBackText);
    }

    public void setLoadStateDisplayString (String loadStateText) {
        movieLoadStateText.setText(loadStateText);
    }
}
