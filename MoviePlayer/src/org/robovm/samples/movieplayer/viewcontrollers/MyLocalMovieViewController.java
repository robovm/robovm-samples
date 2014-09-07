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

import java.io.File;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIButtonType;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UITabBarItem;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewContentMode;
import org.robovm.samples.movieplayer.views.MyImageView;

public class MyLocalMovieViewController extends MyMovieViewController {
    private final MyImageView imageView;
    private final UIButton playButton;

    public MyLocalMovieViewController() {
        super();

        UIView view = getView();
        view.setBackgroundColor(UIColor.createFromWhiteAlpha(0.66, 1));

        imageView = new MyImageView(new CGRect(57, 59, 205, 135));
        imageView.setMovieViewController(this);
        imageView.setUserInteractionEnabled(true);
        imageView.setImage(UIImage.createFromBundle("images/preview.jpg"));
        imageView.setContentMode(UIViewContentMode.ScaleAspectFit);
        view.addSubview(imageView);

        playButton = UIButton.create(UIButtonType.RoundedRect);
        playButton.setFrame(new CGRect(106, 201, 108, 44));
        playButton.setBackgroundColor(UIColor.createFromWhiteAlpha(0.66, 0.5));
        playButton.setTitle("Play Movie", UIControlState.Normal);
        playButton.getTitleLabel().setFont(UIFont.getSystemFont(18));
        playButton.addOnTouchUpInsideListener(new UIControl.OnTouchUpInsideListener() {
            @Override
            public void onTouchUpInside(UIControl control, UIEvent event) {
                playLocalMovieFile();
            }
        });
        view.addSubview(playButton);

        setTabBarItem(new UITabBarItem("Local", UIImage.createFromBundle("images/local.png"), 0));
    }

    /** Returns a URL to a local movie in the app bundle. */
    private NSURL getLocalMovieURL() {
        NSURL theMovieURL = null;
        NSBundle bundle = NSBundle.getMainBundle();
        if (bundle != null) {
            String moviePath = bundle.findResourcePath("Movie", "m4v");
            if (moviePath != null) {
                theMovieURL = new NSURL(new File(moviePath));
            }
        }
        return theMovieURL;
    }

    /** Play the movie at the specified URL. */
    public void playLocalMovieFile() {
        playMovieFile(getLocalMovieURL());
    }
}
