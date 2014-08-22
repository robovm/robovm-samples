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

import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.NSIndexPathExtensions;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIActionSheet;
import org.robovm.apple.uikit.UIActionSheetDelegateAdapter;
import org.robovm.apple.uikit.UIActionSheetStyle;
import org.robovm.apple.uikit.UIAlertView;
import org.robovm.apple.uikit.UIAlertViewDelegateAdapter;
import org.robovm.apple.uikit.UIAlertViewStyle;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellSelectionStyle;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.objc.ObjCClass;
import org.robovm.rt.bro.annotation.MachineSizedFloat;
import org.robovm.rt.bro.annotation.MachineSizedSInt;

/**
 * The view controller for hosting various kinds of alerts and action sheets
 *
 */
public class AlertsViewController extends UITableViewController {

    private static String ALERT_CELL_ID = "AlertCellID";
    private static String SOURCE_CELL_ID = "SourceCellID";
    
    private LinkedList<ListItem> dataSourceArray = new LinkedList<ListItem>();
    
    private class ListItem {
        private String sectionTitle;
        private String label;
        private String source;
        
        public ListItem(String sectionTitle, String label, String source) {
            super();
            this.sectionTitle = sectionTitle;
            this.label = label;
            this.source = source;
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

    }
    
    enum AlertTableSections
    {
            UIAction_Simple_Section,
            UIAction_OKCancel_Section,
            UIAction_Custom_Section,
            UIAlert_Simple_Section,
            UIAlert_OKCancel_Section,
            UIAlert_Custom_Section,
            UIAlert_SecureText_Section
    };

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        
        this.setTitle("");

        this.dataSourceArray.add(new ListItem("UIActionSheet", "Show Simple", "dialogSimpleAction"));
        this.dataSourceArray.add(new ListItem("UIActionSheet", "Show OK-Cancel", "dialogOKCancelAction"));
        this.dataSourceArray.add(new ListItem("UIActionSheet", "Show Customized", "dialogOtherAction"));
        this.dataSourceArray.add(new ListItem("UIAlertView", "Show Simple", "alertSimpleAction"));
        this.dataSourceArray.add(new ListItem("UIAlertView", "Show OK-Cancel", "alertOKCancelAction"));
        this.dataSourceArray.add(new ListItem("UIAlertView", "Show Custom", "alertOtherAction"));
        this.dataSourceArray.add(new ListItem("UIAlertView", "Secure Text Input", "alertSecureTextAction"));
        
        
        // register our cell IDs for later when we are asked for UITableViewCells
        getTableView().registerReusableCellClass(ObjCClass.getByType(UITableViewCell.class), ALERT_CELL_ID);
        getTableView().registerReusableCellClass(ObjCClass.getByType(UITableViewCell.class), SOURCE_CELL_ID);
        
    }
    
//    private void alertView(UIAlertView actionSheet, int buttonIndex) {
//            // use "buttonIndex" to decide your action
//            //
//        
//    }
    
    /**
     * open a dialog with just an OK button
     */
    void dialogSimpleAction() {
            
            UIActionSheet actionSheet = new UIActionSheet("UIActionSheetTitle", 
                    new UIActionSheetDelegateAdapter(){
                        
                        @Override
                        public void clicked(UIActionSheet actionSheet, long buttonIndex) {
                            System.err.println("ButtonIndex: " + buttonIndex);
                        }
                        
                    }
            , "Ok", null, new String[0]);

            actionSheet.setActionSheetStyle(UIActionSheetStyle.Default);
            actionSheet.showIn(this.getView());     // show from our table view (pops up in the middle of the table)
    }
    
    /**
     *  open a dialog with an OK and cancel button
     */
    private void dialogOKCancelAction() {
        UIActionSheet actionSheet = new UIActionSheet("UIActionSheetTitle", 
                new UIActionSheetDelegateAdapter(){
                    
                    @Override
                    public void clicked(UIActionSheet actionSheet, long buttonIndex) {
                        System.err.println("ButtonIndex: " + buttonIndex);
                    }
                    
                }
        , "Cancel", "Ok", new String[0]);

        actionSheet.setActionSheetStyle(UIActionSheetStyle.Default);
        actionSheet.showIn(this.getView());     // show from our table view (pops up in the middle of the table)
    }

    /**
     *  open a dialog with two custom buttons
     */
    private void dialogOtherAction() {
            // open a dialog with two custom buttons
            UIActionSheet actionSheet = new UIActionSheet("UIActionSheetTitle", 
                    new UIActionSheetDelegateAdapter(){
                        
                        @Override
                        public void clicked(UIActionSheet actionSheet, long buttonIndex) {
                            System.err.println("ButtonIndex: " + buttonIndex);
                        }
                        
                    }
            , null, null, new String[]{"ButtonTitle1","ButtonTitle2"});
        
            actionSheet.setActionSheetStyle(UIActionSheetStyle.Default);
            actionSheet.setDestructiveButtonIndex(1); // make the second button red (destructive)
            actionSheet.showIn(this.getView()); // show from our table view (pops up in the middle of the table)
    }


    /**
     *  open an alert with just an OK button
     */
    private void alertSimpleAction() {
            // open an alert with just an OK button

            UIAlertView alert = new UIAlertView("UIAlertViewTitle", "AlertViewMessageGeneric",
                    new UIAlertViewDelegateAdapter(){
                
                @Override
                public void clicked(UIAlertView actionSheet, long buttonIndex) {
                    System.err.println("ButtonIndex: " + buttonIndex);
                }
        
            }
            , "Ok", new String[0]);
            alert.show();   
    }

    /**
     *  open a alert with an OK and cancel button
     */
    private void alertOKCancelAction(){
        UIAlertView alert = new UIAlertView("UIAlertViewTitle", "AlertViewMessageGeneric",
                new UIAlertViewDelegateAdapter(){
            
            @Override
            public void clicked(UIAlertView actionSheet, long buttonIndex) {
                System.err.println("ButtonIndex: " + buttonIndex);
            }
    
        }
        , "Cancel", "Ok");
        alert.show();   
    }

    /**
     *  open an alert with two custom buttons
     */
    private void alertOtherAction() {
            // open an alert with two custom buttons

            UIAlertView alert = new UIAlertView("UIAlertViewTitle", "AlertViewMessageGeneric",
                    new UIAlertViewDelegateAdapter(){

                public void clicked(UIAlertView actionSheet, long buttonIndex) {
                    System.err.println("ButtonIndex: " + buttonIndex);
                }
        
            }
            , "Cancel", new String[]{"ButtonTitle1", "ButtonTitle2"});
            alert.show();
    }
    
    /**
     * open an alert with two custom buttons
     */
    private void alertSecureTextAction() {
            // open an alert with two custom buttons
            UIAlertView alert = new UIAlertView("UIAlertViewTitle", "AlertViewMessageGeneric",
                new UIAlertViewDelegateAdapter(){
            
            @Override
            public void clicked(UIAlertView actionSheet, long buttonIndex) {
                System.err.println("ButtonIndex: " + buttonIndex);
            }
    
        }
        , "Cancel", "Ok");
        alert.setAlertViewStyle(UIAlertViewStyle.SecureTextInput);
        alert.show();
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
        return (NSIndexPathExtensions.getRow(indexPath) == 0) ? 50.0 : 22.0;
    }    

    /**
     * the table's selection has changed, show the alert or action sheet
     */
    @Override
    public void didSelectRow(UITableView tableView, NSIndexPath indexPath) {

        NSIndexPath tableSelection = this.getTableView().getIndexPathForSelectedRow();
        this.getTableView().deselectRow(tableSelection, false);

        if (NSIndexPathExtensions.getRow(indexPath) == 0) {

            switch ((int) NSIndexPathExtensions.getSection(indexPath)) {
                case 0:
                    this.dialogSimpleAction();
                    break;
    
                case 1:
                    this.dialogOKCancelAction();
                    break;
    
                case 2:
                    this.dialogOtherAction();
                    break;
    
                case 3:
                    this.alertSimpleAction();
                    break;
    
                case 4:
                    this.alertOKCancelAction();
                    break;
    
                case 5:
                    this.alertOtherAction();
                    break;
    
                case 6:
                    this.alertSecureTextAction();
                    break;
                }
        }
    }

    @Override
    public UITableViewCell getRowCell(UITableView tableView, NSIndexPath indexPath) {
        
        UITableViewCell cell = null;
        if (NSIndexPathExtensions.getRow(indexPath) == 0) { 
            cell = getTableView().dequeueReusableCell(ALERT_CELL_ID, indexPath);
            cell.getTextLabel().setText(this.dataSourceArray.get((int)NSIndexPathExtensions.getSection(indexPath)).getLabel());
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
    
}
