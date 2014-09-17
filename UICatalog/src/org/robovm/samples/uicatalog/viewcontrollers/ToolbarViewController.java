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
        this.scrollView = new UIScrollView(new CGRect(0, 0, 320, 460));

        UILabel barLabel = new UILabel(new CGRect(20, 86 + 60, 280, 21));
        barLabel.setText("Button Style");
        barLabel.setFont(UIFont.getBoldSystemFont(12));

        this.barStyleSegControl = new UISegmentedControl(new CGRect(20, 23 + 60, 280, 30));
        this.barStyleSegControl = new UISegmentedControl(new CGRect(20, 23, 280, 30));
        this.barStyleSegControl.insertSegment("Default", 0, false);
        this.barStyleSegControl.insertSegment("Black", 1, false);
        this.barStyleSegControl.insertSegment("Translucent", 2, false);
        this.barStyleSegControl.setSelectedSegment(0);
        this.barStyleSegControl.addOnValueChangedListener(new UIControl.OnValueChangedListener() {
            @Override
            public void onValueChanged (UIControl control) {
                toggleBarStyle(control);
            }
        });

        this.scrollView.addSubview(barLabel);
        this.scrollView.addSubview(barStyleSegControl);

        UILabel tintLabel = new UILabel(new CGRect(30, 57 + 60, 48, 21));
        tintLabel.setText("Tint:");
        this.tintSwitch = new UISwitch(new CGRect(64, 56 + 55, 94, 27));
        this.tintSwitch.addOnValueChangedListener(new UIControl.OnValueChangedListener() {
            @Override
            public void onValueChanged (UIControl control) {
                toggleTintColor(control);
            }
        });
        tintLabel.setFont(UIFont.getBoldSystemFont(12));

        scrollView.addSubview(tintLabel);
        scrollView.addSubview(this.tintSwitch);

        this.imageSwitchLabel = new UILabel(new CGRect(150, 57 + 60, 40, 21));
        this.imageSwitchLabel.setText("Image:");
        this.imageSwitch = new UISwitch(new CGRect(200, 56 + 55, 94, 27));
        this.imageSwitch.addOnValueChangedListener(new UIControl.OnValueChangedListener() {
            @Override
            public void onValueChanged (UIControl control) {
                toggleImage(control);
            }
        });
        this.imageSwitchLabel.setFont(UIFont.getBoldSystemFont(12));

        scrollView.addSubview(this.imageSwitchLabel);
        scrollView.addSubview(this.imageSwitch);

        UILabel buttonLabel = new UILabel(new CGRect(20, 5, 280, 21));
        buttonLabel.setText("Bar Style:");
        buttonLabel.setFont(UIFont.getBoldSystemFont(12));

        this.buttonItemStyleSegControl = new UISegmentedControl(new CGRect(20, 103 + 60, 280, 30));
        this.buttonItemStyleSegControl.insertSegment("Plain", 0, false);
        this.buttonItemStyleSegControl.insertSegment("Bordered", 1, false);
        this.buttonItemStyleSegControl.insertSegment("Done", 2, false);
        this.buttonItemStyleSegControl.setSelectedSegment(0);
        this.buttonItemStyleSegControl.addOnValueChangedListener(new UIControl.OnValueChangedListener() {
            @Override
            public void onValueChanged (UIControl control) {
                toggleStyle(control);
            }
        });

        scrollView.addSubview(buttonLabel);
        scrollView.addSubview(this.buttonItemStyleSegControl);

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

        this.setTitle("");

        // create the UIToolbar at the bottom of the view controller
        //
        toolbar = new UIToolbar(new CGRect(0, 0, 0, 0));
        // toolbar.setS

        toolbar.setBarStyle(UIBarStyle.Default);
        this.adjustToolbarSize();

        // this.systemButtonPicker = new UIPickerView(new CGRect(0, 159, 320,
        // 216));
        // size up the toolbar and set its frame
        this.toolbar.setFrame(new CGRect(this.getView().getBounds().getMinX(), this.getView().getBounds().getMinY()
            + this.getView().getBounds().getHeight() - this.toolbar.getFrame().getHeight(),
            this.getView().getBounds().getWidth(), this.toolbar.getFrame().getHeight()));

        // make so the toolbar stays to the bottom and keep the width matching
        // the device's screen width
        this.toolbar.setAutoresizingMask(UIViewAutoresizing.FlexibleWidth.set(UIViewAutoresizing.FlexibleTopMargin));

        this.getView().addSubview(this.toolbar);

        currentSystemItem = UIBarButtonSystemItem.Done;
        this.createToolbarItems();

        // remember our scroll view's content height (a fixed size) later when
        // we set its content size in viewWillAppear
        this.savedContentHightSize = this.scrollView.getFrame().size().height()
            - this.getNavigationController().getNavigationBar().getFrame().getHeight() - this.toolbar.getFrame().size().height();

        // @TODO check this....
        getView().addSubview(this.systemButtonPicker);

    }

    /** adjusts toolbar to fit screen */
    private void adjustToolbarSize () {
        // size up the toolbar and set its frame
        this.toolbar.resizeToFit();

        // since the toolbar may have adjusted its height, it's origin will have
        // to be adjusted too
        CGRect mainViewBounds = this.getView().getBounds();
        this.toolbar.setFrame(new CGRect(mainViewBounds.getMinX(), mainViewBounds.getMinY() + mainViewBounds.getHeight()
            - this.toolbar.getBounds().getHeight(), mainViewBounds.getWidth(), this.toolbar.getFrame().getHeight()));
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
        this.scrollView.addSubview(this.buttonItemStyleSegControl);

        UIBarButtonItemStyle style = UIBarButtonItemStyle.valueOf(this.buttonItemStyleSegControl.getSelectedSegment());

        // create the system-defined "OK or Done" button
        UIBarButtonItem systemItem = new UIBarButtonItem(this.currentSystemItem, action);
        systemItem.setStyle(style);

        // flex item used to separate the left groups items and right grouped
        // items
        UIBarButtonItem flexItem = new UIBarButtonItem(UIBarButtonSystemItem.FlexibleSpace, null, null);

        // create a special tab bar item with a custom image and title
        UIBarButtonItem infoItem = new UIBarButtonItem(UIImage.createFromBundle("segment_tools.png"), style, action);

        // create a custom button with a background image with black text for
        // its title:
        UIBarButtonItem customItem1 = new UIBarButtonItem("Item1", UIBarButtonItemStyle.Bordered, action);

        UIImage baseImage = UIImage.createFromBundle("whiteButton.png");
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
        this.adjustToolbarSize();

        // adjust the scroll view's height since the toolbar may have been
        // resized
        double adjustedHeight = this.getView().getBounds().getHeight() - this.toolbar.getFrame().getHeight();
        CGRect newFrame = this.scrollView.getFrame();
        newFrame.size().height(adjustedHeight);
        this.scrollView.setFrame(newFrame);

        // finally set the content size so that it scrolls in landscape but not
        // in portrait
        this.scrollView.setContentSize(new CGSize(this.scrollView.getFrame().getWidth(), this.savedContentHightSize));
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
        NSArray<UIBarButtonItem> toolbarItems = this.toolbar.getItems();
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
            this.toolbar.setBarStyle(UIBarStyle.Default);
            break;
        case 1:
            this.toolbar.setBarStyle(UIBarStyle.BlackOpaque);
            break;
        case 2:
            this.toolbar.setBarStyle(UIBarStyle.BlackTranslucent);
            break;
        }
    }

    /** toggles tint color
     * 
     * @param sender */
    private void toggleTintColor (NSObject sender) {
        UISwitch switchCtl = (UISwitch)sender;
        if (switchCtl.isOn()) {
            this.toolbar.setTintColor(UIColor.red());
            this.imageSwitch.setEnabled(false);
            this.barStyleSegControl.setEnabled(false);
            this.imageSwitch.setAlpha(0.5);
            this.barStyleSegControl.setAlpha(0.50);

        } else {
            if (this.imageSwitch.isOn()) {
                this.imageSwitch.setEnabled(false);
                this.barStyleSegControl.setEnabled(false);
            } else {
                this.imageSwitch.setEnabled(true);
                this.barStyleSegControl.setEnabled(true);
                this.toolbar.setTintColor(null); // no color
                this.imageSwitch.setAlpha(1.0);
                this.barStyleSegControl.setAlpha(1.0);
            }
        }
    }

    /** toggles image
     * 
     * @param sender */
    private void toggleImage (NSObject sender) {
        UISwitch switchCtl = (UISwitch)sender;

        if (switchCtl.isOn()) {
            this.toolbar.setBackgroundImage(UIImage.createFromBundle("toolbarBackground.png"), UIBarPosition.Bottom,
                UIBarMetrics.Default);

            this.tintSwitch.setEnabled(false);
            this.barStyleSegControl.setEnabled(false);
            this.tintSwitch.setAlpha(0.5f);
            this.barStyleSegControl.setAlpha(0.5f);
        } else {

            this.toolbar.setBackgroundImage(null, UIBarPosition.Bottom, UIBarMetrics.Default);

            this.tintSwitch.setEnabled(true);
            this.barStyleSegControl.setEnabled(true);
            this.tintSwitch.setAlpha(1.0);
            this.barStyleSegControl.setAlpha(1.0);
        }
    }

}
