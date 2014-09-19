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
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIButtonType;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControlContentHorizontalAlignment;
import org.robovm.apple.uikit.UIControlContentVerticalAlignment;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIKeyboardType;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UITabBarItem;
import org.robovm.apple.uikit.UITextBorderStyle;
import org.robovm.apple.uikit.UITextField;
import org.robovm.apple.uikit.UITextFieldDelegateAdapter;
import org.robovm.apple.uikit.UITextFieldViewMode;
import org.robovm.apple.uikit.UIView;

public class MyStreamingMovieViewController extends MyMovieViewController {
    private UITextField movieURLTextField;
    private final UILabel enterURLLabel;
    private final UIButton playButton;

    public MyStreamingMovieViewController () {
        super();

        UIView view = getView();
        view.setBackgroundColor(UIColor.fromWhiteAlpha(0.66, 1));

        enterURLLabel = new UILabel(new CGRect(63, 93, 195, 29));
        enterURLLabel.setUserInteractionEnabled(false);
        enterURLLabel.setText("Enter a streaming movie URL:");
        enterURLLabel.setFont(UIFont.getSystemFont(14));
        enterURLLabel.setNumberOfLines(5);
        view.addSubview(enterURLLabel);

        playButton = UIButton.create(UIButtonType.RoundedRect);
        playButton.setFrame(new CGRect(106, 194, 108, 44));
        playButton.setBackgroundColor(UIColor.fromWhiteAlpha(0.66, 0.5));
        playButton.setTitle("Play Movie", UIControlState.Normal);
        playButton.getTitleLabel().setFont(UIFont.getSystemFont(18));
        playButton.addOnTouchUpInsideListener(new UIControl.OnTouchUpInsideListener() {
            @Override
            public void onTouchUpInside (UIControl control, UIEvent event) {
                playStreamingMovie();
            }
        });
        view.addSubview(playButton);

        movieURLTextField = new UITextField(new CGRect(21, 133, 279, 35));
        movieURLTextField.setKeyboardType(UIKeyboardType.URL);
        movieURLTextField.setContentHorizontalAlignment(UIControlContentHorizontalAlignment.Left);
        movieURLTextField.setContentVerticalAlignment(UIControlContentVerticalAlignment.Center);
        movieURLTextField.setBackgroundColor(UIColor.white());
        movieURLTextField.setBorderStyle(UITextBorderStyle.Bezel);
        movieURLTextField.setClearButtonMode(UITextFieldViewMode.Always);
        movieURLTextField.setTextAlignment(NSTextAlignment.Center);
        movieURLTextField.setMinimumFontSize(17);
        movieURLTextField.setText("http://devimages.apple.com/iphone/samples/bipbop/gear1/prog_index.m3u8");
        movieURLTextField.setDelegate(new UITextFieldDelegateAdapter() {
            @Override
            public boolean shouldReturn (UITextField textField) {
                /*
                 * When the user presses return, take focus away from the text field so that the keyboard is dismissed.
                 */
                if (textField == movieURLTextField) {
                    movieURLTextField.resignFirstResponder();
                }
                return true;
            }
        });
        view.addSubview(movieURLTextField);

        setTabBarItem(new UITabBarItem("Streaming", UIImage.create("images/streaming.png"), 0));
    }

    public void playStreamingMovie () {
        /* Has the user entered a movie URL? */
        if (movieURLTextField.getText().length() > 0) {
            NSURL theMovieURL = new NSURL(movieURLTextField.getText());
            if (theMovieURL != null) {
                if (theMovieURL.getScheme() != null) { // sanity check on the
                                                       // URL
                    playMovieStream(theMovieURL);
                }
            }
        }
    }
}
