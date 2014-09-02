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
package org.robovm.samples.movieplayer;

import java.io.File;

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSNumber;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.foundation.NSUserDefaults;
import org.robovm.apple.mediaplayer.MPMovieControlStyle;
import org.robovm.apple.mediaplayer.MPMovieRepeatMode;
import org.robovm.apple.mediaplayer.MPMovieScalingMode;
import org.robovm.apple.uikit.UIColor;

public class MoviePlayerUserPrefs {
    // Application preference keys
    private static final String SCALING_MODE_KEY = "scalingMode";
    private static final String CONTROL_STYLE_KEY = "controlStyle";
    private static final String BACKGROUND_COLOR_KEY = "backgroundColor";
    private static final String REPEAT_MODE_KEY = "repeatMode";
    private static final String MOVIE_BACKGROUND_IMAGE_KEY = "useMovieBackgroundImage";

    private static void registerDefaults() {
        /*
         * First get the movie player settings defaults (scaling, controller
         * type, background color, repeat mode, application audio session) set
         * by the user via the built-in iPhone Settings application
         */
        String testValue = NSUserDefaults.getStandardUserDefaults().getString(SCALING_MODE_KEY);
        if (testValue == null) {
            // No default movie player settings values have been set, create
            // them here based on our
            // settings bundle info.
            //
            // The values to be set for movie playback are:
            //
            // - scaling mode (None, Aspect Fill, Aspect Fit, Fill)
            // - controller style (None, Fullscreen, Embedded)
            // - background color (Any UIColor value)
            // - repeat mode (None, One)
            // - use application audio session (On, Off)
            // - background image

            String pathStr = NSBundle.getMainBundle().getBundlePath();
            String finalPath = pathStr + "/Settings.bundle" + "/Root.plist";
            NSDictionary<NSString, NSObject> settingsDict = (NSDictionary<NSString, NSObject>) NSDictionary
                    .read(new File(finalPath));
            NSArray<NSDictionary<NSString, NSObject>> prefSpecifierArray = (NSArray<NSDictionary<NSString, NSObject>>) settingsDict
                    .get(new NSString("PreferenceSpecifiers"));
            NSNumber controlStyleDefault = null;
            NSNumber scalingModeDefault = null;
            NSNumber backgroundColorDefault = null;
            NSNumber repeatModeDefault = null;
            NSNumber movieBackgroundImageDefault = null;

            NSString keyStr = new NSString("Key");
            NSString defaultStr = new NSString("DefaultValue");

            for (NSDictionary<NSString, NSObject> prefItem : prefSpecifierArray) {
                NSString key = (NSString) prefItem.get(keyStr);
                if (key != null) {
                    String keyValueStr = key.toString();
                    NSNumber defaultValue = (NSNumber) prefItem.get(defaultStr);

                    if (keyValueStr.equals(SCALING_MODE_KEY)) {
                        scalingModeDefault = defaultValue;
                    } else if (keyValueStr.equals(CONTROL_STYLE_KEY)) {
                        controlStyleDefault = defaultValue;
                    } else if (keyValueStr.equals(BACKGROUND_COLOR_KEY)) {
                        backgroundColorDefault = defaultValue;
                    } else if (keyValueStr.equals(REPEAT_MODE_KEY)) {
                        repeatModeDefault = defaultValue;
                    } else if (keyValueStr.equals(MOVIE_BACKGROUND_IMAGE_KEY)) {
                        movieBackgroundImageDefault = defaultValue;
                    }
                }
            }

            // Since no default values have been set, create them here.
            NSDictionary<NSString, NSObject> appDefaults = new NSDictionary<NSString, NSObject>(new NSString(
                    SCALING_MODE_KEY), scalingModeDefault, new NSString(CONTROL_STYLE_KEY), controlStyleDefault,
                    new NSString(BACKGROUND_COLOR_KEY), backgroundColorDefault, new NSString(REPEAT_MODE_KEY),
                    repeatModeDefault, new NSString(MOVIE_BACKGROUND_IMAGE_KEY), movieBackgroundImageDefault);

            NSUserDefaults.getStandardUserDefaults().registerDefaults(appDefaults);
            NSUserDefaults.getStandardUserDefaults().synchronize();
        } else {
            /*
             * Writes any modifications to the persistent domains to disk and
             * updates all unmodified persistent domains to what is on disk.
             */
            NSUserDefaults.getStandardUserDefaults().synchronize();
        }
    }

    /**
     * Movie scaling mode can be one of: MPMovieScalingModeNone,
     * MPMovieScalingModeAspectFit, MPMovieScalingModeAspectFill,
     * MPMovieScalingModeFill.
     * 
     * Movie scaling mode describes how the movie content is scaled to fit the
     * frame of its view. It may be one of:
     * 
     * MPMovieScalingModeNone, MPMovieScalingModeAspectFit,
     * MPMovieScalingModeAspectFill, MPMovieScalingModeFill.
     */
    public static MPMovieScalingMode getScalingMode() {
        registerDefaults();

        return MPMovieScalingMode.valueOf(NSUserDefaults.getStandardUserDefaults().getInteger(SCALING_MODE_KEY));
    }

    /**
     * Movie control style can be one of: MPMovieControlStyleNone,
     * MPMovieControlStyleEmbedded, MPMovieControlStyleFullscreen.
     * 
     * Movie control style describes the style of the playback controls. It can
     * be one of:
     * 
     * MPMovieControlStyleNone, MPMovieControlStyleEmbedded,
     * MPMovieControlStyleFullscreen, MPMovieControlStyleDefault,
     * MPMovieControlStyleFullscreen
     */
    public static MPMovieControlStyle getControlStyle() {
        registerDefaults();

        return MPMovieControlStyle.valueOf(NSUserDefaults.getStandardUserDefaults().getInteger(CONTROL_STYLE_KEY));
    }

    /**
     * The color of the background area behind the movie can be any UIColor
     * value.
     */
    public static UIColor getBackgroundColor() {
        registerDefaults();

        UIColor[] colors = new UIColor[] { UIColor.colorBlack(), UIColor.colorDarkGray(), UIColor.colorLightGray(),
                UIColor.colorWhite(),
                UIColor.colorGray(), UIColor.colorRed(), UIColor.colorGreen(), UIColor.colorBlue(),
                UIColor.colorCyan(),
                UIColor.colorYellow(), UIColor.colorMagenta(), UIColor.colorOrange(), UIColor.colorPurple(),
                UIColor.colorBrown(),
                UIColor.colorClear() };

        return colors[(int) NSUserDefaults.getStandardUserDefaults().getInteger(BACKGROUND_COLOR_KEY)];
    }

    /**
     * Movie repeat mode describes how the movie player repeats content at the
     * end of playback.
     * 
     * Movie repeat mode can be one of: MPMovieRepeatModeNone,
     * MPMovieRepeatModeOne.
     */
    public static MPMovieRepeatMode getRepeatMode() {
        registerDefaults();

        return MPMovieRepeatMode.valueOf(NSUserDefaults.getStandardUserDefaults().getInteger(REPEAT_MODE_KEY));
    }

    public static boolean useMovieBackground() {
        registerDefaults();

        return NSUserDefaults.getStandardUserDefaults().getInteger(MOVIE_BACKGROUND_IMAGE_KEY) != 0;
    }
}
