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

package org.robovm.sample.uicatalog.viewcontrollers;

import java.util.LinkedList;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.NSIndexPathExtensions;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIActivityIndicatorView;
import org.robovm.apple.uikit.UIActivityIndicatorViewStyle;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonItemStyle;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIPageControl;
import org.robovm.apple.uikit.UIProgressView;
import org.robovm.apple.uikit.UIProgressViewStyle;
import org.robovm.apple.uikit.UISlider;
import org.robovm.apple.uikit.UIStepper;
import org.robovm.apple.uikit.UISwitch;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellSelectionStyle;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewAutoresizing;
import org.robovm.objc.ObjCClass;
import org.robovm.objc.Selector;
import org.robovm.objc.annotation.Method;
import org.robovm.rt.bro.annotation.MachineSizedFloat;
import org.robovm.rt.bro.annotation.MachineSizedSInt;

/**
 * The view controller for hosting the UIControls features of this sample.
 */
public class ControlsViewController extends UITableViewController{

    private final float SLIDER_HEIGHT = 7.0f;
    private final float PROGRESS_INDICATOR_SIZE = 40.0f;
    private final float UI_PROGRESS_BAR_WIDTH = 160.0f;
    private final float UI_PROGRESS_BAR_HEIGHT = 24.0f;

    final static String DISPLAY_CELL_ID = "DisplayCellID";
    final static String SOURCE_CELL_ID = "SourceCellID";
    
    private UISwitch switchCtl;
    private UISlider sliderCtl;
    private UISlider customSlider;
    private UIPageControl pageControl;

    private UIActivityIndicatorView progressInd;
    private UIColor progressIndSavedColor;

    private UIProgressView progressBar;
    private UIStepper stepper;

    private int viewTag = 1;
    
    private LinkedList<ListItem> dataSourceArray = new LinkedList<ListItem>();
    
    /**
     * List item which stores controls meta data
     *
     */
    private class ListItem {
        
        private String sectionTitle;
        
        private String label;
        
        private String source;
        
        private UIView view;

        public ListItem(String sectionTitle, String label, String source,
                UIView view) {
            super();
            this.sectionTitle = sectionTitle;
            this.label = label;
            this.source = source;
            this.view = view;
        }

        public String getSectionTitle() {
            return sectionTitle;
        }

        public String getLabel() {
            return label;
        }

        public String getSource() {
            return source;
        }

        public UIView getView() {
            return view;
        }
        
    }
    
    /**
     * Setup view and load controls
     */
    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        
        CGRect tableViewBounds = new CGRect(0.0, 64.0, 320, 247);
        setTableView(new UITableView(tableViewBounds));
        
        UIBarButtonItem tintButton = new UIBarButtonItem();
        tintButton.setTitle("Tinted");
        tintButton.setStyle(UIBarButtonItemStyle.Bordered);
        tintButton.setAction(Selector.register("tintAction"));
        tintButton.setTarget(this);
        
        getNavigationItem().setRightBarButtonItem(tintButton);
        
        this.dataSourceArray.add(new ListItem("UISwitch", 
                "Standard Switch", 
                "switchCtl",
                getSwitchCtl()));
        
        this.dataSourceArray.add(new ListItem("Standard Slider", 
                "Standard Slider", 
                "switchCtl",
                getSliderCtl()));
        
        this.dataSourceArray.add(new ListItem("UISlider",
                "Custom Slider", 
                "sliderCtl",
                getCustomSlider()));
        
        this.dataSourceArray.add(new ListItem("UIPageControl",
                "Ten Pages",
                "pageControl",
                getPageControl()));

        this.dataSourceArray.add(new ListItem("UIActivityIndicatorView",
                "Style Gray",
                "progressInd",
                getProgressInd()));

        this.dataSourceArray.add(new ListItem("UIProgressView",
                "Style Default",
                "progressInd",
                getProgressBar()));

        this.dataSourceArray.add(new ListItem("UIStepper",
                "Stepper 1 to 10",
                "progressInd",
                getStepper()));
        
        
        // register our cell IDs for later when we are asked for UITableViewCells
        getTableView().registerReusableCellClass(ObjCClass.getByType(UITableViewCell.class), DISPLAY_CELL_ID);
        getTableView().registerReusableCellClass(ObjCClass.getByType(UITableViewCell.class), SOURCE_CELL_ID);
        
    }
    
    @Override
    public @MachineSizedSInt
    long getNumberOfSections(UITableView tableView) {
        return this.dataSourceArray.size();
    }

    @Override
    public String getSectionHeaderTitle(UITableView tableView,
            @MachineSizedSInt long section) {
        return this.dataSourceArray.get((int)section).getSectionTitle();
    }

    @Override
    public @MachineSizedSInt
    long getNumberOfRowsInSection(UITableView tableView,
            @MachineSizedSInt long section) {
        return 2;
    }

    @Override
    public @MachineSizedFloat
    double getRowHeight(UITableView tableView, NSIndexPath indexPath) {
        return (NSIndexPathExtensions.getRow(indexPath) == 0) ? 50.0 : 38.0;
    }    
    
    @Override
    public UITableViewCell getRowCell(UITableView tableView, NSIndexPath indexPath) {
        
        UITableViewCell cell = null;

        if (NSIndexPathExtensions.getRow(indexPath) == 0) {

            cell = getTableView().dequeueReusableCell(DISPLAY_CELL_ID, indexPath);
            
            cell.setSelectionStyle(UITableViewCellSelectionStyle.None);
            UIView viewToRemove = null;
            viewToRemove = cell.getContentView().getViewWithTag(viewTag);
            if (viewToRemove != null) {
                viewToRemove.removeFromSuperview();
            }

            cell.getTextLabel().setText(this.dataSourceArray.get((int)NSIndexPathExtensions.getSection(indexPath)).getLabel());
            
            UIView control = this.dataSourceArray.get((int)NSIndexPathExtensions.getSection(indexPath)).getView();

            CGRect newFrame = control.getFrame();
            newFrame.origin().x(cell.getContentView().getFrame().getWidth() - newFrame.getWidth() - 10.0);
            control.setFrame(newFrame);
            
            // if the cell is ever resized, keep the button over to the right
            control.setAutoresizingMask(UIViewAutoresizing.FlexibleLeftMargin);
            cell.getContentView().addSubview(control);
            
        } else {

            cell = getTableView().dequeueReusableCell(SOURCE_CELL_ID, indexPath);
            cell.setSelectionStyle(UITableViewCellSelectionStyle.None);
            cell.getTextLabel().setOpaque(false);
            cell.getTextLabel().setTextAlignment(NSTextAlignment.Center);
            cell.getTextLabel().setTextColor(UIColor.colorGray());
            cell.getTextLabel().setNumberOfLines(2);
            cell.getTextLabel().setHighlightedTextColor(UIColor.colorBlack());
            cell.getTextLabel().setFont(UIFont.getSystemFont(12.0));
            cell.getTextLabel().setText( this.dataSourceArray.get((int)NSIndexPathExtensions.getSection(indexPath)).getSource());

        }
        
        return cell;
    }
    
    /**
     * create UISwitch
     * @return UISwitch
     */
    public UISwitch getSwitchCtl() {
        if(switchCtl == null) {
            CGRect frame = new CGRect(0.0, 12.0, 94.0, 27.0);
            switchCtl = new UISwitch(frame);
            
            switchCtl.setBackgroundColor(UIColor.colorClear());
            switchCtl.setTag(viewTag);
        }
        return switchCtl;
    }
    
    /**
     * create Slider
     * @return new slider
     */
    public UISlider getSliderCtl() {
        if (sliderCtl == null) {
            CGRect frame = new CGRect(0.0, 12.0, 120.0, SLIDER_HEIGHT);
            sliderCtl = new UISlider(frame);
            sliderCtl.addOnValueChangedListener(new UIControl.OnValueChangedListener(){
                public void onValueChanged(UIControl control) {
                    System.err.println("Slider moved to:" + sliderCtl.getValue());
                }                
            });

            // in case the parent view draws with a custom color or gradient, use a transparent color
            sliderCtl.setBackgroundColor(UIColor.colorClear());
            sliderCtl.setMinimumValue(0.0f);
            sliderCtl.setMaximumValue(100.0f);
            sliderCtl.setContinuous(true);
            sliderCtl.setValue(50.0f);   
            sliderCtl.setTag(viewTag);      // tag this view for later so we can remove it from recycled table cells
        }
        return sliderCtl;
    }
    
    /**
     * gets custom slider
     * @return slider
     */
    private UISlider getCustomSlider() {
        if (customSlider == null) {
            CGRect frame = new CGRect(0.0, 12.0, 130.0, SLIDER_HEIGHT);
            customSlider = new UISlider(frame);
            customSlider.addOnValueChangedListener(new UIControl.OnValueChangedListener() {
                public void onValueChanged(UIControl control) {
                    System.err.println("custom slider moved to:" + customSlider.getValue());
                }
            });
            
            // in case the parent view draws with a custom color or gradient, use a transparent color
            customSlider.setBackgroundColor(UIColor.colorClear());
 
            UIImage stetchLeftTrack = UIImage.createFromBundle("orangeslide.png");
            stetchLeftTrack = stetchLeftTrack.newStretchable(10l, 0l);
            
            UIImage stetchRightTrack = UIImage.createFromBundle("yellowslide.png");
            stetchRightTrack = stetchRightTrack.newStretchable(10l, 0l);
            
            customSlider.setThumbImage(UIImage.createFromBundle("slider_ball.png"),UIControlState.Normal);
            customSlider.setMinimumTrackImage(stetchLeftTrack, UIControlState.Normal);
            customSlider.setMaximumTrackImage(stetchRightTrack, UIControlState.Normal);
            customSlider.setMinimumValue(0.0f);
            customSlider.setMaximumValue(100.0f);
            customSlider.setContinuous(true);
            customSlider.setValue(50.0f);
        }
        return customSlider;
    }
    
    /**
     * gets a page control
     * @return page control
     */
    private UIPageControl getPageControl() {
        if (pageControl == null) {
            CGRect frame = new CGRect(0.0, 14.0, 178.0, 20.0);
            pageControl = new UIPageControl(frame);
            pageControl.setBackgroundColor(UIColor.colorGray());
            pageControl.setNumberOfPages(10);
            pageControl.setTag(viewTag);
        }
        return pageControl;
    }
    
    /**
     * gets a progress indicator
     * @return progress indicator
     */
    private UIActivityIndicatorView getProgressInd() {
        
        if (this.progressInd == null){
            CGRect frame = new CGRect(0.0, 12.0, PROGRESS_INDICATOR_SIZE, PROGRESS_INDICATOR_SIZE);
            
            this.progressInd = new UIActivityIndicatorView(UIActivityIndicatorViewStyle.Gray);
            getProgressInd().setColor(progressInd.getColor());
            this.progressInd.setFrame(frame);
            getProgressInd().resizeToFit();
            getProgressInd().setActivityIndicatorViewStyle(UIActivityIndicatorViewStyle.Gray);
            
            getProgressInd().setTag(viewTag);    // tag this view for later so we can remove it from recycled table cells
            getProgressInd().startAnimating();
        }
        
        return progressInd;
    }
    
    /**
     * Returns progress bar
     * @return UIProgressView
     */
    private UIProgressView getProgressBar() {
        if (progressBar == null) {
            CGRect frame = new CGRect(0.0, 20.0, UI_PROGRESS_BAR_WIDTH, UI_PROGRESS_BAR_HEIGHT);
            progressBar = new UIProgressView(frame);
            progressBar.setProgressViewStyle(UIProgressViewStyle.Default);
            progressBar.setProgress(0.5f);
            progressBar.setTag(viewTag);    // tag this view for later so we can remove it from recycled table cells
        }
        return progressBar;
    }
    
    /**
     * Returns stepper
     * @return stepper
     */
    private UIStepper getStepper() {
        if (stepper == null) {
            CGRect frame = new CGRect(0.0, 10.0, 0.0, 0.0);
            stepper = new UIStepper(frame);
            stepper.resizeToFit();        // size the control to it's normal size
            stepper.setTag(viewTag);     // tag this view for later so we can remove it from recycled table cells
            stepper.setValue(0);
            stepper.setMinimumValue(0);
            stepper.setMaximumValue(10);
            stepper.setStepValue(1);
            
            stepper.addOnValueChangedListener(new UIControl.OnValueChangedListener() {
                public void onValueChanged(UIControl control) {
                    System.out.println("Stepper value modified!:" + stepper.getValue());
                }
            });
        }
        return stepper;
    }
    
    /**
     * Performs a tint action on applicable controls.
     */
    @Method
    private void tintAction() {
        UIColor tintColor = (this.getProgressBar().getProgressTintColor() != null) ? null : UIColor.colorBlue();

        this.getProgressBar().setProgressTintColor(tintColor);
        this.getProgressBar().setTrackTintColor(tintColor);
        this.sliderCtl.setMinimumTrackTintColor(tintColor);
        this.sliderCtl.setThumbTintColor(tintColor);
        this.switchCtl.setOnTintColor(tintColor);
        this.stepper.setTintColor(tintColor);
        
        UIColor thumbTintColor = (this.switchCtl.getThumbTintColor() != null) ? null : UIColor.colorRed();
        this.switchCtl.setOnTintColor(tintColor);
        this.switchCtl.setThumbTintColor(thumbTintColor);
        
        UIColor progressIndColor = (this.getProgressInd().getColor() != progressIndSavedColor) ? this.progressIndSavedColor : UIColor.colorBlue();
        this.progressInd.setColor(progressIndColor);
    }
    
}
