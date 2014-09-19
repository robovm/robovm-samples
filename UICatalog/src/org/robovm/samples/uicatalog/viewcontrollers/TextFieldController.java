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

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.NSIndexPathExtensions;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIKeyboardType;
import org.robovm.apple.uikit.UIReturnKeyType;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellSelectionStyle;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.apple.uikit.UITextAutocorrectionType;
import org.robovm.apple.uikit.UITextBorderStyle;
import org.robovm.apple.uikit.UITextField;
import org.robovm.apple.uikit.UITextFieldDelegateAdapter;
import org.robovm.apple.uikit.UITextFieldViewMode;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewAutoresizing;
import org.robovm.objc.ObjCClass;
import org.robovm.rt.bro.annotation.MachineSizedFloat;
import org.robovm.rt.bro.annotation.MachineSizedSInt;
import org.robovm.samples.uicatalog.Constants;

/** The view controller for hosting the UITextField features of this sample. */
public class TextFieldController extends UITableViewController {

    // tableView cell id constants
    static String TEXT_FIELD_CELL_ID = "TextFieldCellID";
    static String SOURCE_CELL_ID = "SourceCellID";

    private final int viewTag = 1;

    private UITextField textFieldNormal;
    private UITextField textFieldRounded;
    private UITextField textFieldSecure;
    private UITextField textFieldLeftView;

    /** Adapter overloading default behaviour of shouldReturn to enable close-on-done when showing on screen keyboard */
    private class TextFieldAdapter extends UITextFieldDelegateAdapter {

        @Override
        public boolean shouldReturn (UITextField textField) {
            // the user pressed the "Done" button, so dismiss the keyboard
            textField.resignFirstResponder();
            return true;
        }

    }

    private final LinkedList<ListItem> dataSourceArray = new LinkedList<ListItem>();

    /** Item storing TextField meta data */
    private class ListItem {

        private final String sectionTitle;

        private final String source;

        private final UIView view;

        public ListItem (String sectionTitle, String source, UIView view) {
            super();
            this.sectionTitle = sectionTitle;
            this.source = source;
            this.view = view;
        }

        public String getSectionTitle () {
            return sectionTitle;
        }

        public String getSource () {
            return source;
        }

        public UIView getView () {
            return view;
        }

    }

    @Override
    public void viewDidLoad () {
        super.viewDidLoad();

        CGRect tableViewBounds = new CGRect(0.0, 64.0, 320, 247);
        setTableView(new UITableView(tableViewBounds));

        setTitle("TextFieldTitle");
        setEditing(false);

        dataSourceArray.add(new ListItem("UITextField", "textFieldNormal", getTextFieldNormal()));
        dataSourceArray.add(new ListItem("UITextField Rounded", "textFieldRounded", getTextFieldRounded()));
        dataSourceArray.add(new ListItem("UITextField Secure", "textFieldSecure", getTextFieldSecure()));
        dataSourceArray.add(new ListItem("UITextField Left", "textFieldLeft", getTextFieldLeftView()));

        // register our cell IDs for later when we are asked for
        // UITableViewCells
        getTableView().registerReusableCellClass(ObjCClass.getByType(UITableViewCell.class), TEXT_FIELD_CELL_ID);
        getTableView().registerReusableCellClass(ObjCClass.getByType(UITableViewCell.class), SOURCE_CELL_ID);

    }

    @Override
    public @MachineSizedSInt long getNumberOfSections (UITableView tableView) {
        return dataSourceArray.size();
    }

    @Override
    public String getSectionHeaderTitle (UITableView tableView, @MachineSizedSInt long section) {
        return dataSourceArray.get((int)section).getSectionTitle();
    }

    @Override
    public @MachineSizedSInt long getNumberOfRowsInSection (UITableView tableView, @MachineSizedSInt long section) {
        return 2;
    }

    @Override
    public @MachineSizedFloat double getRowHeight (UITableView tableView, NSIndexPath indexPath) {
        return (NSIndexPathExtensions.getRow(indexPath) == 0) ? 50.0 : 22.0;
    }

    @Override
    public UITableViewCell getRowCell (UITableView tableView, NSIndexPath indexPath) {

        UITableViewCell cell = null;

        if (NSIndexPathExtensions.getRow(indexPath) == 0) {
            cell = getTableView().dequeueReusableCell(TEXT_FIELD_CELL_ID, indexPath);

            cell.setSelectionStyle(UITableViewCellSelectionStyle.None);
            UIView viewToRemove = null;
            viewToRemove = cell.getContentView().getViewWithTag(viewTag);
            if (viewToRemove != null) {
                viewToRemove.removeFromSuperview();
            }

            UITextField textField = (UITextField)dataSourceArray.get((int)NSIndexPathExtensions.getSection(indexPath)).getView();

            CGRect newFrame = textField.getFrame();
            newFrame.size(new CGSize(cell.getContentView().getFrame().getWidth() - Constants.LEFT_MARGIN * 2, cell
                .getContentView().getFrame().getHeight()));
            textField.setFrame(newFrame);

            // if the cell is ever resized, keep the button over to the right
            textField.setAutoresizingMask(UIViewAutoresizing.FlexibleLeftMargin);
            cell.getContentView().addSubview(textField);
        } else {
            cell = getTableView().dequeueReusableCell(SOURCE_CELL_ID, indexPath);
            cell.setSelectionStyle(UITableViewCellSelectionStyle.None);
            cell.getTextLabel().setTextAlignment(NSTextAlignment.Left);
            cell.getTextLabel().setTextColor(UIColor.gray());
            cell.getTextLabel().setHighlightedTextColor(UIColor.black());
            cell.getTextLabel().setFont(UIFont.getSystemFont(12.0));
            cell.getTextLabel().setText(dataSourceArray.get((int)NSIndexPathExtensions.getSection(indexPath)).getSource());
        }

        return cell;
    }

    /** Creates default text field
     * 
     * @return */
    UITextField getTextFieldNormal () {

        if (textFieldNormal == null) {
            CGRect frame = new CGRect(Constants.LEFT_MARGIN, 4.0f, Constants.TEXT_FIELD_WIDTH, Constants.TEXT_FIELD_HEIGHT);
            textFieldNormal = new UITextField(frame);

            textFieldNormal.setBorderStyle(UITextBorderStyle.Bezel);
            textFieldNormal.setTextColor(UIColor.gray());

            textFieldNormal.setFont(UIFont.getSystemFont(17.0f));
            textFieldNormal.setPlaceholder("<enter text>");
            textFieldNormal.setBackgroundColor(UIColor.white());

            textFieldNormal.setAutocorrectionType(UITextAutocorrectionType.No);
            textFieldNormal.setKeyboardType(UIKeyboardType.Default);

            textFieldNormal.setReturnKeyType(UIReturnKeyType.Default);
            textFieldNormal.setClearButtonMode(UITextFieldViewMode.WhileEditing);
            textFieldNormal.setTag(viewTag); // tag this control so we can
                                             // remove it later for recycled
                                             // cells
            textFieldNormal.setDelegate(new TextFieldAdapter()); // let us be
                                                                 // the delegate
                                                                 // so we know
                                                                 // when the
                                                                 // keyboard's
                                                                 // "Done"
                                                                 // button is
                                                                 // pressed

        }
        return textFieldNormal;
    }

    /** Create textfield with rounded corners
     * 
     * @return */
    public UITextField getTextFieldRounded () {
        if (textFieldRounded == null) {
            CGRect frame = new CGRect(Constants.LEFT_MARGIN, 4.0, Constants.TEXT_FIELD_WIDTH, Constants.TEXT_FIELD_HEIGHT);
            textFieldRounded = new UITextField(frame);
            textFieldRounded.setBorderStyle(UITextBorderStyle.RoundedRect);
            textFieldRounded.setTextColor(UIColor.black());

            textFieldRounded.setFont(UIFont.getSystemFont(17.0f));
            textFieldRounded.setPlaceholder("<enter text>");
            textFieldRounded.setBackgroundColor(UIColor.white());

            textFieldRounded.setAutocorrectionType(UITextAutocorrectionType.No);
            textFieldRounded.setKeyboardType(UIKeyboardType.Default);

            textFieldRounded.setReturnKeyType(UIReturnKeyType.Default);
            textFieldRounded.setClearButtonMode(UITextFieldViewMode.WhileEditing);
            textFieldRounded.setTag(viewTag); // tag this control so we can
                                              // remove it later for recycled
                                              // cells
            textFieldRounded.setDelegate(new TextFieldAdapter()); // let us be
                                                                  // the
                                                                  // delegate so
                                                                  // we know
                                                                  // when the
                                                                  // keyboard's
                                                                  // "Done"
                                                                  // button is
                                                                  // pressed

        }
        return textFieldRounded;
    }

    /** Creates text field secure
     * 
     * @return */
    public UITextField getTextFieldSecure () {

        if (textFieldSecure == null) {
            CGRect frame = new CGRect(Constants.LEFT_MARGIN, 4.0, Constants.TEXT_FIELD_WIDTH, Constants.TEXT_FIELD_HEIGHT);
            textFieldSecure = new UITextField(frame);
            textFieldSecure.setBorderStyle(UITextBorderStyle.Bezel);
            textFieldSecure.setTextColor(UIColor.black());

            textFieldSecure.setFont(UIFont.getSystemFont(17.0f));
            textFieldSecure.setPlaceholder("<enter text>");
            textFieldSecure.setBackgroundColor(UIColor.white());

            // textFieldSecure.setAutocorrectionType(UITextAutocorrectionType.No);
            textFieldSecure.setKeyboardType(UIKeyboardType.Default);

            textFieldSecure.setReturnKeyType(UIReturnKeyType.Done);
            textFieldSecure.setSecureTextEntry(true); // make the text entry
                                                      // secure (bullets)

            textFieldSecure.setClearButtonMode(UITextFieldViewMode.WhileEditing);
            textFieldSecure.setTag(viewTag); // tag this control so we can
                                             // remove it later for recycled
                                             // cells
            textFieldSecure.setDelegate(new TextFieldAdapter());

        }
        return textFieldSecure;
    }

    /** Creates TextField with left view
     * 
     * @return */
    UITextField getTextFieldLeftView () {
        if (textFieldLeftView == null) {
            CGRect frame = new CGRect(Constants.LEFT_MARGIN, 4.0, Constants.TEXT_FIELD_WIDTH, Constants.TEXT_FIELD_HEIGHT);
            textFieldLeftView = new UITextField(frame);

            textFieldLeftView.setBorderStyle(UITextBorderStyle.Bezel);
            textFieldLeftView.setTextColor(UIColor.black());

            textFieldLeftView.setFont(UIFont.getSystemFont(17.0f));
            textFieldLeftView.setPlaceholder("<enter text>");
            textFieldLeftView.setBackgroundColor(UIColor.white());

            // textFieldLeftView.setAutocorrectionType(UITextAutocorrectionType.No);
            textFieldLeftView.setKeyboardType(UIKeyboardType.Default);
            textFieldLeftView.setDelegate(new TextFieldAdapter());

            textFieldLeftView.setReturnKeyType(UIReturnKeyType.Done);
            textFieldLeftView.setClearButtonMode(UITextFieldViewMode.WhileEditing);

            textFieldLeftView.setTag(viewTag);
        }
        return textFieldLeftView;
    }

}
