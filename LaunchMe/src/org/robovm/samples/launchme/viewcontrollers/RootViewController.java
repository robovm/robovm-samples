
package org.robovm.samples.launchme.viewcontrollers;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSRange;
import org.robovm.apple.foundation.NSSet;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.uikit.NSLineBreakMode;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIButtonType;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UISlider;
import org.robovm.apple.uikit.UITapGestureRecognizer;
import org.robovm.apple.uikit.UITextAutocapitalizationType;
import org.robovm.apple.uikit.UITextView;
import org.robovm.apple.uikit.UITouch;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewContentMode;
import org.robovm.apple.uikit.UIViewController;

public class RootViewController extends UIViewController {
    private final UISlider redSlider;
    private final UISlider greenSlider;
    private final UISlider blueSlider;
    private final UITextView urlField;
    private final UILabel urlFieldHeader;
    private final UIView colorView;

    private UIColor selectedColor;

    public RootViewController () {
        super();

        UIView view = getView();
        view.setBackgroundColor(UIColor.white());

        UILabel redLabel = new UILabel(new CGRect(22, 278, 59, 21));
        redLabel.setText("Red:");
        redLabel.setTextAlignment(NSTextAlignment.Right);
        redLabel.setContentMode(UIViewContentMode.Left);
        redLabel.setFont(UIFont.getSystemFont(17));
        redLabel.setTextColor(UIColor.darkText());
        view.addSubview(redLabel);

        UILabel greenLabel = new UILabel(new CGRect(22, 318, 59, 21));
        greenLabel.setText("Green:");
        greenLabel.setTextAlignment(NSTextAlignment.Right);
        greenLabel.setContentMode(UIViewContentMode.Left);
        greenLabel.setFont(UIFont.getSystemFont(17));
        greenLabel.setTextColor(UIColor.darkText());
        view.addSubview(greenLabel);

        UILabel blueLabel = new UILabel(new CGRect(22, 357, 59, 21));
        blueLabel.setText("Blue:");
        blueLabel.setTextAlignment(NSTextAlignment.Right);
        blueLabel.setContentMode(UIViewContentMode.Left);
        blueLabel.setFont(UIFont.getSystemFont(17));
        blueLabel.setTextColor(UIColor.darkText());
        view.addSubview(blueLabel);

        UIControl.OnValueChangedListener sliderListener = new UIControl.OnValueChangedListener() {
            @Override
            public void onValueChanged (UIControl control) {
                sliderValueChanged();
            }
        };

        redSlider = new UISlider(new CGRect(87, 279, 200, 23));
        redSlider.setMinimumValue(0);
        redSlider.setMaximumValue(1);
        redSlider.addOnValueChangedListener(sliderListener);
        view.addSubview(redSlider);

        greenSlider = new UISlider(new CGRect(87, 319, 200, 23));
        greenSlider.setMinimumValue(0);
        greenSlider.setMaximumValue(1);
        greenSlider.addOnValueChangedListener(sliderListener);
        view.addSubview(greenSlider);

        blueSlider = new UISlider(new CGRect(87, 358, 200, 23));
        blueSlider.setMinimumValue(0);
        greenSlider.setMaximumValue(1);
        blueSlider.addOnValueChangedListener(sliderListener);
        view.addSubview(blueSlider);

        colorView = new UIView(new CGRect(110, 166, 100, 100));
        colorView.setBackgroundColor(UIColor.darkText());
        view.addSubview(colorView);

        urlField = new UITextView(new CGRect(101, 127, 199, 36));
        urlField.setText("launchme://#000000");
        urlField.setEditable(false);
        urlField.setScrollEnabled(false);
        urlField.setShowsHorizontalScrollIndicator(false);
        urlField.setShowsVerticalScrollIndicator(false);
        urlField.setMultipleTouchEnabled(true);
        urlField.setBackgroundColor(UIColor.white());
        urlField.setFont(UIFont.getSystemFont(17));
        urlField.setAutocapitalizationType(UITextAutocapitalizationType.Sentences);
        urlField.addGestureRecognizer(new UITapGestureRecognizer()); // TODO [self.urlField selectAll:self];
        view.addSubview(urlField);

        UILabel urlLabel = new UILabel(new CGRect(20, 127, 85, 36));
        urlLabel.setText("URL:");
        urlLabel.setTextAlignment(NSTextAlignment.Right);
        urlLabel.setFont(UIFont.getSystemFont(17));
        urlLabel.setTextColor(UIColor.darkText());
        view.addSubview(urlLabel);

        urlFieldHeader = new UILabel(new CGRect(20, 117, 280, 22));
        urlFieldHeader.setText("Tap to select the URL");
        urlFieldHeader.setTextAlignment(NSTextAlignment.Center);
        urlFieldHeader.setFont(UIFont.getSystemFont(10));
        urlFieldHeader.setTextColor(UIColor.darkText());
        view.addSubview(urlFieldHeader);

        UILabel descriptionLabel = new UILabel(new CGRect(20, 13, 280, 101));
        descriptionLabel
            .setText("Using this sample:\nDrag the sliders to configure the URL for a specific color.  Copy the displayed launchme URL and tap the button below to launch Mobile Safari.  Paste the URL into the address bar and tap Go.  Optionally, try modifying parts of the URL in Mobile Safari.");
        descriptionLabel.setTextAlignment(NSTextAlignment.Center);
        descriptionLabel.setLineBreakMode(NSLineBreakMode.TruncatingTail);
        descriptionLabel.setNumberOfLines(6);
        descriptionLabel.setFont(UIFont.getSystemFont(12));
        descriptionLabel.setTextColor(UIColor.darkText());
        view.addSubview(descriptionLabel);

        UIButton startSafariButton = UIButton.create(UIButtonType.RoundedRect);
        startSafariButton.setFrame(new CGRect(20, 396, 280, 44));
        startSafariButton.getTitleLabel().setFont(UIFont.getBoldSystemFont(15));
        startSafariButton.setTitle("Launch Mobile Safari", UIControlState.Normal);
        startSafariButton.setTintColor(UIColor.fromRGBA(0.196, 0.309, 0.521, 1));
        startSafariButton.setTitleShadowColor(UIColor.fromWhiteAlpha(0.5, 1), UIControlState.Normal);
        startSafariButton.setTitleColor(UIColor.white(), UIControlState.Highlighted);
        startSafariButton.addOnTouchUpInsideListener(new UIControl.OnTouchUpInsideListener() {
            @Override
            public void onTouchUpInside (UIControl control, UIEvent event) {
                UIApplication.getSharedApplication().openURL(new NSURL("http://www.apple.com"));
            }
        });
        view.addSubview(startSafariButton);
    }

    /** Update the interface to display aColor. This includes modifying colorView to show aColor, moving the red, green, and blue
     * sliders to match the R, G, and B components of aColor, and updating urlLabel to display the corresponding URL for aColor. */
    public void update (UIColor aColor) {
        if (aColor == null) return;
        /*
         * There is a possibility that getRGBA could fail if aColor is not in a compatible color space. In such a case, the
         * arguments are not modified. Having default values will allow for a more graceful failure than picking up whatever is
         * currently on the stack.
         */
        float red = 0;
        float green = 0;
        float blue = 0;

        double[] rgba = aColor.getRGBA();

        if (rgba == null) {
            /*
             * While setting default values for red, green, blue and alpha guards against undefined results if getRGBA fails,
             * aColor will be assigned as the backgroundColor of colorView a few lines down. Initialize aColor to the black color
             * so it matches the color code that will be displayed in the urlLabel.
             */
            aColor = UIColor.black();
        } else {
            red = (float)rgba[0];
            green = (float)rgba[1];
            blue = (float)rgba[2];
        }

        redSlider.setValue(red);
        greenSlider.setValue(green);
        blueSlider.setValue(blue);

        colorView.setBackgroundColor(aColor);

        /*
         * Construct the URL for the specified color. This URL allows another app to start LauncMe with the specific color
         * displayed initially.
         */
        urlField.setText(String.format("launchme://#%02X%02X%02X", (int)(red * 255), (int)(green * 255), (int)(blue * 255)));

        urlFieldHeader.setText("Tap to select the URL");
    }

    /** Custom implementation of the setter for the selectedColor property.
     * @param selectedColor */
    public void setSelectedColor (UIColor selectedColor) {
        if (!selectedColor.equals(this.selectedColor)) {
            this.selectedColor = selectedColor;
            update(selectedColor);
        }
    }

    @Override
    public void viewDidLoad () {
        super.viewDidLoad();
        /*
         * The AppDelegate may have assigned a color to selectedColor that should be the color displayed initially. This would
         * have occurred before the view was actually loaded meaning that while update was executed, it had no effect. The
         * solution is to call it again here now that there is a UI to update.
         */
        update(selectedColor);
    }

    /** Deselects the text in the urlField if the user taps in the white space of this view controller's view. */
    @Override
    public void touchesEnded (NSSet<UITouch> touches, UIEvent event) {
        urlField.setSelectedRange(new NSRange(0, 0));
    }

    private void sliderValueChanged () {
        /*
         * Create a new UIColor object with the current value of all three sliders (it does not matter which one was actualy
         * modified).
         */
        setSelectedColor(UIColor.fromRGBA(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue(), 1));
    }

    public UILabel getUrlFieldHeader () {
        return urlFieldHeader;
    }
}
