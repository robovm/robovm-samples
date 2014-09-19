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
 * Portions of this code is based on Apple Inc's UICatalog sample (v2.11)
 * which is copyright (C) 2008-2013 Apple Inc.
 */

package org.robovm.samples.uicatalog.viewcontrollers;

import java.util.LinkedList;
import java.util.List;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSMutableArray;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.NSAttributedStringAttributes;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonItemStyle;
import org.robovm.apple.uikit.UIBarButtonSystemItem;
import org.robovm.apple.uikit.UIBarMetrics;
import org.robovm.apple.uikit.UIBarPosition;
import org.robovm.apple.uikit.UIBarStyle;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIPickerView;
import org.robovm.apple.uikit.UIPickerViewDataSourceAdapter;
import org.robovm.apple.uikit.UIPickerViewDelegateAdapter;
import org.robovm.apple.uikit.UIScrollView;
import org.robovm.apple.uikit.UISegmentedControl;
import org.robovm.apple.uikit.UISwitch;
import org.robovm.apple.uikit.UIToolbar;
import org.robovm.apple.uikit.UIViewAutoresizing;
import org.robovm.apple.uikit.UIViewController;

/** The view controller for hosting the UIToolbar and UIBarButtonItem features of this sample. */
public class ToolbarViewController extends UIViewController {
    private UIScrollView scrollView;

    private UISegmentedControl barStyleSegControl;
    private UISwitch tintSwitch;

    private UISwitch imageSwitch;
    private UILabel imageSwitchLabel;

    private UISegmentedControl buttonItemStyleSegControl;
    private UIPickerView systemButtonPicker;

    private UIToolbar toolbar;
    private NSArray<NSString> pickerViewArray;

    private UIBarButtonSystemItem currentSystemItem;

    private double savedContentHightSize;

    private final UIBarButtonItem.OnClickListener action = new UIBarButtonItem.OnClickListener() {
        @Override
        public void onClick (UIBarButtonItem barButtonItem) {
            System.err.println("action!");
        }
    };

    private void initUI () {
        scrollView = new UIScrollView(new CGRect(0, 0, 320, 460));

        UILabel barLabel = new UILabel(new CGRect(20, 86 + 60, 280, 21));
        barLabel.setText("Button Style");
        barLabel.setFont(UIFont.getBoldSystemFont(12));

        barStyleSegControl = new UISegmentedControl(new CGRect(20, 23 + 60, 280, 30));
        barStyleSegControl = new UISegmentedControl(new CGRect(20, 23, 280, 30));
        barStyleSegControl.insertSegment("Default", 0, false);
        barStyleSegControl.insertSegment("Black", 1, false);
        barStyleSegControl.insertSegment("Translucent", 2, false);
        barStyleSegControl.setSelectedSegment(0);
        barStyleSegControl.addOnValueChangedListener(new UIControl.OnValueChangedListener() {
            @Override
            public void onValueChanged (UIControl control) {
                toggleBarStyle(control);
            }
        });

        scrollView.addSubview(barLabel);
        scrollView.addSubview(barStyleSegControl);

        UILabel tintLabel = new UILabel(new CGRect(30, 57 + 60, 48, 21));
        tintLabel.setText("Tint:");
        tintSwitch = new UISwitch(new CGRect(64, 56 + 55, 94, 27));
        tintSwitch.addOnValueChangedListener(new UIControl.OnValueChangedListener() {
            @Override
            public void onValueChanged (UIControl control) {
                toggleTintColor(control);
            }
        });
        tintLabel.setFont(UIFont.getBoldSystemFont(12));

        scrollView.addSubview(tintLabel);
        scrollView.addSubview(tintSwitch);

        imageSwitchLabel = new UILabel(new CGRect(150, 57 + 60, 40, 21));
        imageSwitchLabel.setText("Image:");
        imageSwitch = new UISwitch(new CGRect(200, 56 + 55, 94, 27));
        imageSwitch.addOnValueChangedListener(new UIControl.OnValueChangedListener() {
            @Override
            public void onValueChanged (UIControl control) {
                toggleImage(control);
            }
        });
        imageSwitchLabel.setFont(UIFont.getBoldSystemFont(12));

        scrollView.addSubview(imageSwitchLabel);
        scrollView.addSubview(imageSwitch);

        UILabel buttonLabel = new UILabel(new CGRect(20, 5, 280, 21));
        buttonLabel.setText("Bar Style:");
        buttonLabel.setFont(UIFont.getBoldSystemFont(12));

        buttonItemStyleSegControl = new UISegmentedControl(new CGRect(20, 103 + 60, 280, 30));
        buttonItemStyleSegControl.insertSegment("Plain", 0, false);
        buttonItemStyleSegControl.insertSegment("Bordered", 1, false);
        buttonItemStyleSegControl.insertSegment("Done", 2, false);
        buttonItemStyleSegControl.setSelectedSegment(0);
        buttonItemStyleSegControl.addOnValueChangedListener(new UIControl.OnValueChangedListener() {
            @Override
            public void onValueChanged (UIControl control) {
                toggleStyle(control);
            }
        });

        scrollView.addSubview(buttonLabel);
        scrollView.addSubview(buttonItemStyleSegControl);

        systemButtonPicker = new UIPickerView(new CGRect(0, 159 + 120, 320, 216));
        systemButtonPicker.setDelegate(new UIPickerViewDelegateAdapter() {
            @Override
            public double getComponentWidth (UIPickerView pickerView, long component) {
                return 240.0;
            }

            @Override
            public double getRowHeight (UIPickerView pickerView, long component) {
                return 40.0;
            }

            @Override
            public String getRowTitle (UIPickerView pickerView, long row, long component) {
                return pickerViewArray.get((int)row).toString();
            }

            @Override
            public void didSelectRow (UIPickerView pickerView, long row, long component) {
                currentSystemItem = UIBarButtonSystemItem.valueOf(pickerView.getSelectedRow(component));
                createToolbarItems();
            }
        });

        systemButtonPicker.setDataSource(new UIPickerViewDataSourceAdapter() {
            @Override
            public long getNumberOfComponents (UIPickerView pickerView) {
                return 1l;
            }

            @Override
            public long getNumberOfRows (UIPickerView pickerView, long component) {
                return pickerViewArray.size();
            }
        });

        UILabel pickerLabel = new UILabel(new CGRect(20, 142 + 60, 280, 17));
        pickerLabel.setText("Picker");
        pickerLabel.setFont(UIFont.getBoldSystemFont(12));

        scrollView.addSubview(pickerLabel);
        scrollView.addSubview(systemButtonPicker);
        getView().addSubview(scrollView);

        pickerViewArray = NSArray.fromStrings("Done", "Cancel", "Edit", "Save", "Add", "FlexibleSpace", "FixedSpace", "Compose",
            "Reply", "Action", "Organize", "Bookmarks", "Search", "Refresh", "Stop", "Camera", "Trash", "Play", "Pause",
            "Rewind", "FastForward", "Undo", "Redo", "PageCurl");
    }

    @Override
    public void viewDidLoad () {
        super.viewDidLoad();

        initUI();

        setTitle("");

        // create the UIToolbar at the bottom of the view controller
        //
        toolbar = new UIToolbar(new CGRect(0, 0, 0, 0));
        // toolbar.setS

        toolbar.setBarStyle(UIBarStyle.Default);
        adjustToolbarSize();

        // systemButtonPicker = new UIPickerView(new CGRect(0, 159, 320,
        // 216));
        // size up the toolbar and set its frame
        toolbar.setFrame(new CGRect(getView().getBounds().getMinX(), getView().getBounds().getMinY()
            + getView().getBounds().getHeight() - toolbar.getFrame().getHeight(), getView().getBounds().getWidth(), toolbar
            .getFrame().getHeight()));

        // make so the toolbar stays to the bottom and keep the width matching
        // the device's screen width
        toolbar.setAutoresizingMask(UIViewAutoresizing.FlexibleWidth.set(UIViewAutoresizing.FlexibleTopMargin));

        getView().addSubview(toolbar);

        currentSystemItem = UIBarButtonSystemItem.Done;
        createToolbarItems();

        // remember our scroll view's content height (a fixed size) later when
        // we set its content size in viewWillAppear
        savedContentHightSize = scrollView.getFrame().size().height()
            - getNavigationController().getNavigationBar().getFrame().getHeight() - toolbar.getFrame().size().height();

        // @TODO check ...
        getView().addSubview(systemButtonPicker);

    }

    /** adjusts toolbar to fit screen */
    private void adjustToolbarSize () {
        // size up the toolbar and set its frame
        toolbar.resizeToFit();

        // since the toolbar may have adjusted its height, it's origin will have
        // to be adjusted too
        CGRect mainViewBounds = getView().getBounds();
        toolbar.setFrame(new CGRect(mainViewBounds.getMinX(), mainViewBounds.getMinY() + mainViewBounds.getHeight()
            - toolbar.getBounds().getHeight(), mainViewBounds.getWidth(), toolbar.getFrame().getHeight()));
    }

    // return the picker frame based on its size, positioned at the bottom of
    // the page
    // private CGRect pickerFrameWithSize(CGSize size){
    // CGRect screenRect = UIScreen.getMainScreen().getApplicationFrame();
    // CGRect pickerRect = new CGRect(0.0, screenRect.getHeight()-84.0f -
    // size.height(), size.width(), size.height());
    // return pickerRect;
    // }

    /** Creates toolbar with associated buttons */
    private void createToolbarItems () {
        scrollView.addSubview(buttonItemStyleSegControl);

        UIBarButtonItemStyle style = UIBarButtonItemStyle.valueOf(buttonItemStyleSegControl.getSelectedSegment());

        // create the system-defined "OK or Done" button
        UIBarButtonItem systemItem = new UIBarButtonItem(currentSystemItem, action);
        systemItem.setStyle(style);

        // flex item used to separate the left groups items and right grouped
        // items
        UIBarButtonItem flexItem = new UIBarButtonItem(UIBarButtonSystemItem.FlexibleSpace, null, null);

        // create a special tab bar item with a custom image and title
        UIBarButtonItem infoItem = new UIBarButtonItem(UIImage.create("segment_tools.png"), style, action);

        // create a custom button with a background image with black text for
        // its title:
        UIBarButtonItem customItem1 = new UIBarButtonItem("Item1", UIBarButtonItemStyle.Bordered, action);

        UIImage baseImage = UIImage.create("whiteButton.png");
        UIImage backroundImage = baseImage.createStretchable(12, 0);
        customItem1.setBackgroundImage(backroundImage, UIControlState.Normal, UIBarMetrics.Default);

        NSAttributedStringAttributes textAttributes = new NSAttributedStringAttributes();
        textAttributes.setForegroundColor(UIColor.black());
        customItem1.setTitleTextAttributes(textAttributes, UIControlState.Normal);

        UIBarButtonItem customItem2 = new UIBarButtonItem("Item2", style, action);

        List<UIBarButtonItem> buttonSet = new LinkedList<UIBarButtonItem>();
        buttonSet.add(systemItem);
        buttonSet.add(flexItem);
        buttonSet.add(customItem1);
        buttonSet.add(customItem2);
        buttonSet.add(infoItem);

        NSMutableArray<UIBarButtonItem> array = new NSMutableArray<UIBarButtonItem>(buttonSet);
        toolbar.setItems(array, false);
    }

    @Override
    public void viewWillAppear (boolean animated) {
        super.viewWillAppear(animated);
        adjustToolbarSize();

        // adjust the scroll view's height since the toolbar may have been
        // resized
        double adjustedHeight = getView().getBounds().getHeight() - toolbar.getFrame().getHeight();
        CGRect newFrame = scrollView.getFrame();
        newFrame.size().height(adjustedHeight);
        scrollView.setFrame(newFrame);

        // finally set the content size so that it scrolls in landscape but not
        // in portrait
        scrollView.setContentSize(new CGSize(scrollView.getFrame().getWidth(), savedContentHightSize));
    }

    /** Toggles styles of UIBarButtons
     * 
     * @param sender */
    private void toggleStyle (NSObject sender) {
        UIBarButtonItemStyle style = UIBarButtonItemStyle.Plain;

        switch ((int)((UISegmentedControl)sender).getSelectedSegment()) {
        case 0:
            style = UIBarButtonItemStyle.Plain;
            break;

        case 1:
            style = UIBarButtonItemStyle.Bordered;
            break;

        case 2:
            style = UIBarButtonItemStyle.Done;
            break;
        }

        // change all necessary bar button items to the given style
        NSArray<UIBarButtonItem> toolbarItems = toolbar.getItems();
        for (int i = 0; i < toolbarItems.size(); i++) {
            // skip setting the style of image-based UIBarButtonItems
            UIBarButtonItem item = toolbarItems.get(i);

            // @TODO check null for image here
            UIImage image = item.getBackgroundImage(UIControlState.Normal, UIBarMetrics.Default);

            // UIImage image = [item
            // backgroundImageForState:UIControlStateNormal
            // barMetrics:UIBarMetricsDefault];
            if (image == null) {
                item.setStyle(style);
            }
        }
    }

    /** toggles bar style
     * 
     * @param sender */
    private void toggleBarStyle (NSObject sender) {
        switch ((int)((UISegmentedControl)sender).getSelectedSegment()) {
        case 0:
            toolbar.setBarStyle(UIBarStyle.Default);
            break;
        case 1:
            toolbar.setBarStyle(UIBarStyle.BlackOpaque);
            break;
        case 2:
            toolbar.setBarStyle(UIBarStyle.BlackTranslucent);
            break;
        }
    }

    /** toggles tint color
     * 
     * @param sender */
    private void toggleTintColor (NSObject sender) {
        UISwitch switchCtl = (UISwitch)sender;
        if (switchCtl.isOn()) {
            toolbar.setTintColor(UIColor.red());
            imageSwitch.setEnabled(false);
            barStyleSegControl.setEnabled(false);
            imageSwitch.setAlpha(0.5);
            barStyleSegControl.setAlpha(0.50);

        } else {
            if (imageSwitch.isOn()) {
                imageSwitch.setEnabled(false);
                barStyleSegControl.setEnabled(false);
            } else {
                imageSwitch.setEnabled(true);
                barStyleSegControl.setEnabled(true);
                toolbar.setTintColor(null); // no color
                imageSwitch.setAlpha(1.0);
                barStyleSegControl.setAlpha(1.0);
            }
        }
    }

    /** toggles image
     * 
     * @param sender */
    private void toggleImage (NSObject sender) {
        UISwitch switchCtl = (UISwitch)sender;

        if (switchCtl.isOn()) {
            toolbar.setBackgroundImage(UIImage.create("toolbarBackground.png"), UIBarPosition.Bottom, UIBarMetrics.Default);

            tintSwitch.setEnabled(false);
            barStyleSegControl.setEnabled(false);
            tintSwitch.setAlpha(0.5f);
            barStyleSegControl.setAlpha(0.5f);
        } else {

            toolbar.setBackgroundImage(null, UIBarPosition.Bottom, UIBarMetrics.Default);

            tintSwitch.setEnabled(true);
            barStyleSegControl.setEnabled(true);
            tintSwitch.setAlpha(1.0);
            barStyleSegControl.setAlpha(1.0);
        }
    }

}
