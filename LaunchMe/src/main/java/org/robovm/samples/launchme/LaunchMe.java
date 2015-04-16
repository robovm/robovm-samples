
package org.robovm.samples.launchme;

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.foundation.NSMatchingOptions;
import org.robovm.apple.foundation.NSPropertyList;
import org.robovm.apple.foundation.NSRange;
import org.robovm.apple.foundation.NSRegularExpression;
import org.robovm.apple.foundation.NSRegularExpressionOptions;
import org.robovm.apple.foundation.NSStringEncoding;
import org.robovm.apple.foundation.NSTextCheckingResult;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIWindow;
import org.robovm.samples.launchme.viewcontrollers.RootViewController;

public class LaunchMe extends UIApplicationDelegateAdapter {
    private UIWindow window;
    private RootViewController rootViewController;

    @Override
    public boolean didFinishLaunching (UIApplication application, UIApplicationLaunchOptions launchOptions) {
        // Set up the view controller.
        rootViewController = new RootViewController();

        // Create a new window at screen size.
        window = new UIWindow(UIScreen.getMainScreen().getBounds());
        // Set our viewcontroller as the root controller for the window.
        window.setRootViewController(rootViewController);
        // Make the window visible.
        window.makeKeyAndVisible();

        /*
         * Retains the window object until the application is deallocated. Prevents Java GC from collecting the window object too
         * early.
         */
        addStrongRef(window);

        return true;
    }

    @Override
    public boolean openURL (UIApplication application, NSURL url, String sourceApplication, NSPropertyList annotation) {
        /*
         * You should be extremely careful when handling URL requests. Take steps to validate the URL before handling it.
         */

        // Check if the incoming URL is null.
        if (url == null) return false;

        // Invoke our helper method to parse the incoming URL and extract the color to display.
        UIColor launchColor = extractColorFromLaunchURL(url);
        // Stop if the url could not be parsed.
        if (launchColor == null) return true;

        // Assign the created color object a the selected color for display in RootViewController.
        ((RootViewController)window.getRootViewController()).setSelectedColor(launchColor);

        // Update the UI of RootViewController to notify the user that the app was launched from an incoming URL request.
        ((RootViewController)window.getRootViewController()).getUrlFieldHeader().setText(
            "The app was launched with the following URL");

        return true;
    }

    /** Helper method that parses a URL and returns a UIColor object representing the first HTML color code it finds or nil if a
     * valid color code is not found. This logic is specific to this sample. Your URL handling code will differ. */
    private UIColor extractColorFromLaunchURL (NSURL url) {
        /*
         * Hexadecimal color codes begin with a number sign (#) followed by six hexadecimal digits. Thus, a color in this format
         * is represented by three bytes (the number sign is ignored). The value of each byte corresponds to the intensity of
         * either the red, blue or green color components, in that order from left to right. Additionally, there is a shorthand
         * notation with the number sign (#) followed by three hexadecimal digits. This notation is expanded to the six digit
         * notation by doubling each digit: #123 becomes #112233.
         */

        // Convert the incoming URL into a string. The '#' character will be percent escaped. That must be undone.
        String urlString = NSURL.decodeURLString(url.getAbsoluteString(), NSStringEncoding.UTF8);
        // Stop if the conversion failed.
        if (urlString == null) return null;

        /*
         * Create a regular expression to locate hexadecimal color codes in the incoming URL. Incoming URLs can be malicious. It
         * is best to use vetted technology, such as NSRegularExpression, to handle the parsing instead of writing your own
         * parser.
         */
        NSRegularExpression regex;
        try {
            regex = new NSRegularExpression("#[0-9a-f]{3}([0-9a-f]{3})?", NSRegularExpressionOptions.CaseInsensitive);
        } catch (Exception e) {
            // Check for any error returned. This can be a result of incorrect regex syntax.
            System.err.println(e);
            return null;
        }

        /*
         * Extract all the matches from the incoming URL string. There must be at least one for the URL to be valid (though
         * matches beyond the first are ignored.)
         */
        NSArray<NSTextCheckingResult> regexMatches = regex.getMatches(urlString, new NSMatchingOptions(0), new NSRange(0,
            urlString.length()));
        if (regexMatches.size() < 1) return null;

        // Extract the first matched string
        int start = (int)regexMatches.get(0).getRange().getLocation();
        int end = start + (int)regexMatches.get(0).getRange().getLength();
        String matchedString = urlString.substring(start, end);

        /*
         * At this point matchedString will look similar to either #FFF or #FFFFFF. The regular expression has guaranteed that
         * matchedString will be no longer than seven characters.
         */

        // Convert matchedString into a long. The '#' character should not be included.
        long hexColorCode = Long.parseLong(matchedString.substring(1), 16);

        float red, green, blue;

        // If the color code is in six digit notation...
        if (matchedString.length() - 1 > 3) {
            /*
             * Extract each color component from the integer representation of the color code. Each component has a value of
             * [0-255] which must be converted into a normalized float for consumption by UIColor.
             */

            red = ((hexColorCode & 0x00FF0000) >> 16) / 255.0f;
            green = ((hexColorCode & 0x0000FF00) >> 8) / 255.0f;
            blue = (hexColorCode & 0x000000FF) / 255.0f;
        }
        // The color code is in shorthand notation...
        else {
            /*
             * Extract each color component from the integer representation of the color code. Each component has a value of
             * [0-255] which must be converted into a normalized float for consumption by UIColor.
             */
            red = (((hexColorCode & 0x00000F00) >> 8) | ((hexColorCode & 0x00000F00) >> 4)) / 255.0f;
            green = (((hexColorCode & 0x000000F0) >> 4) | (hexColorCode & 0x000000F0)) / 255.0f;
            blue = ((hexColorCode & 0x0000000F) | ((hexColorCode & 0x0000000F) << 4)) / 255.0f;
        }
        // Create and return a UIColor object with the extracted components.
        return UIColor.fromRGBA(red, green, blue, 1);
    }

    public static void main (String[] args) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(args, null, LaunchMe.class);
        pool.close();
    }
}
