/*
 * Copyright (C) 2013-2015 RoboVM AB
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
 * Portions of this code is based on Apple Inc's DateCell sample (v1.6)
 * which is copyright (C) 2009-2014 Apple Inc.
 */

package org.robovm.samples.datecell.ui;

import java.util.ArrayList;
import java.util.List;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSDate;
import org.robovm.apple.foundation.NSDateFormatter;
import org.robovm.apple.foundation.NSDateFormatterStyle;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSLocale;
import org.robovm.apple.foundation.NSNotificationCenter;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIDatePicker;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellSelectionStyle;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.apple.uikit.UITableViewRowAnimation;
import org.robovm.apple.uikit.UIView;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import org.robovm.objc.annotation.IBOutlet;
import org.robovm.objc.block.VoidBooleanBlock;

@CustomClass("MyTableViewController")
public class MyTableViewController extends UITableViewController {
    /**
     * duration for the animation to slide the date picker into view
     */
    private static final double PICKER_ANIMATION_DURATION = 0.40;
    /**
     * view tag identifying the date picker view
     */
    private static final long DATE_PICKER_TAG = 99;

    // keep track of which rows have date cells
    private static final int DATE_START_ROW = 1;
    private static final int DATE_END_ROW = 2;

    /**
     * the cells with the start or end date
     */
    private static final String DATE_CELL_ID = "dateCell";
    /**
     * the cell containing the date picker
     */
    private static final String DATE_PICKER_ID = "datePicker";
    /**
     * the remaining cells at the end
     */
    private static final String OTHER_CELL_ID = "otherCell";

    public class CellData {
        String title;
        NSDate date;

        public CellData(String title, NSDate date) {
            this.title = title;
            this.date = date;
        }
    }

    private List<CellData> data;
    private NSDateFormatter dateFormatter;

    // keep track which indexPath points to the cell with UIDatePicker
    private NSIndexPath datePickerIndexPath;

    private double pickerCellRowHeight;

    private UIDatePicker pickerView;

    // this button appears only when the date picker is shown (iOS 6.1.x or
    // earlier)
    private UIBarButtonItem doneButton;

    private NSObject localeNotif;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        // setup our data source
        data = new ArrayList<>();
        data.add(new CellData("Tap a cell to change its date:", null));
        data.add(new CellData("Start Date", new NSDate()));
        data.add(new CellData("End Date", new NSDate()));
        data.add(new CellData("(other item1)", null));
        data.add(new CellData("(other item2)", null));

        dateFormatter = new NSDateFormatter();
        dateFormatter.setDateStyle(NSDateFormatterStyle.Short);
        dateFormatter.setTimeStyle(NSDateFormatterStyle.No);

        // obtain the picker view cell's height, works because the cell was
        // pre-defined in our storyboard
        UITableViewCell pickerViewCellToCheck = getTableView().dequeueReusableCell(DATE_PICKER_ID);
        pickerCellRowHeight = pickerViewCellToCheck.getFrame().getHeight();

        // if the locale changes while in the background, we need to be notified
        // so we can update the date format in the table view cells
        localeNotif = NSLocale.Notifications.observeCurrentLocaleDidChange(new Runnable() {
            @Override
            public void run() {
                // the user changed the locale (region format) in Settings, so
                // we are notified here to
                // update the date format in the table view cells
                getTableView().reloadData();
            }
        });
    }

    @Override
    protected void dispose(boolean finalizing) {
        NSNotificationCenter.getDefaultCenter().removeObserver(localeNotif);
        super.dispose(finalizing);
    }

    /**
     * Determines if the given indexPath has a cell below it with a
     * UIDatePicker.
     * 
     * @param indexPath The indexPath to check if its cell has a UIDatePicker
     *            below it.
     */
    private boolean hasPickerForIndexPath(NSIndexPath indexPath) {
        boolean hasDatePicker = false;

        long targetedRow = indexPath.getRow();
        targetedRow++;

        UITableViewCell checkDatePickerCell = getTableView().getCellForRow(NSIndexPath.createWithRow(targetedRow, 0));
        UIDatePicker checkDatePicker = (UIDatePicker) checkDatePickerCell.getViewWithTag(DATE_PICKER_TAG);

        hasDatePicker = checkDatePicker != null;
        return hasDatePicker;
    }

    /**
     * Updates the UIDatePicker's value to match with the date of the cell above
     * it.
     */
    private void updateDatePicker() {
        if (datePickerIndexPath != null) {
            UITableViewCell associatedDatePickerCell = getTableView().getCellForRow(datePickerIndexPath);

            UIDatePicker targetedDatePicker = (UIDatePicker) associatedDatePickerCell.getViewWithTag(DATE_PICKER_TAG);
            if (targetedDatePicker != null) {
                // we found a UIDatePicker in this cell, so update it's date
                // value
                CellData itemData = data.get((int)datePickerIndexPath.getRow() - 1);
                targetedDatePicker.setDate(itemData.date, false);
            }
        }
    }

    /**
     * @return if the UITableViewController has a UIDatePicker in any of its
     *         cells.
     */
    private boolean hasInlineDatePicker() {
        return datePickerIndexPath != null;
    }

    /**
     * Determines if the given indexPath points to a cell that contains the
     * UIDatePicker.
     * 
     * @param indexPath The indexPath to check if it represents a cell with the
     *            UIDatePicker.
     * @return
     */
    private boolean indexPathHasPicker(NSIndexPath indexPath) {
        return hasInlineDatePicker() && datePickerIndexPath.getRow() == indexPath.getRow();
    }

    /**
     * Determines if the given indexPath points to a cell that contains the
     * start/end dates.
     * 
     * @param indexPath The indexPath to check if it represents start/end date
     *            cell.
     * @return
     */
    private boolean indexPathHasDate(NSIndexPath indexPath) {
        boolean hasDate = false;

        if ((indexPath.getRow() == DATE_START_ROW)
                || (indexPath.getRow() == DATE_END_ROW || (hasInlineDatePicker() && (indexPath.getRow() == DATE_END_ROW + 1)))) {
            hasDate = true;
        }

        return hasDate;
    }

    @Override
    public double getHeightForRow(UITableView tableView, NSIndexPath indexPath) {
        return indexPathHasPicker(indexPath) ? pickerCellRowHeight : tableView.getRowHeight();
    }

    @Override
    public long getNumberOfRowsInSection(UITableView tableView, long section) {
        if (hasInlineDatePicker()) {
            // we have a date picker, so allow for it in the number of rows in
            // this section
            int numRows = data.size();
            return ++numRows;
        }
        return data.size();
    }

    @Override
    public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
        UITableViewCell cell = null;

        String cellID = OTHER_CELL_ID;

        if (indexPathHasPicker(indexPath)) {
            // the indexPath is the one containing the inline date picker
            cellID = DATE_PICKER_ID; // the current/opened date picker cell
        } else if (indexPathHasDate(indexPath)) {
            // the indexPath is one that contains the date information
            cellID = DATE_CELL_ID; // the start/end date cells
        }

        cell = getTableView().dequeueReusableCell(cellID);

        if (indexPath.getRow() == 0) {
            // we decide here that first cell in the table is not selectable
            // (it's just an indicator)
            cell.setSelectionStyle(UITableViewCellSelectionStyle.None);
        }

        // if we have a date picker open whose cell is above the cell we want to
        // update,
        // then we have one more cell than the model allows
        int modelRow = (int)indexPath.getRow();
        if (datePickerIndexPath != null && datePickerIndexPath.getRow() <= indexPath.getRow()) {
            modelRow--;
        }

        CellData itemData = data.get(modelRow);

        // proceed to configure our cell
        switch (cellID) {
        case DATE_CELL_ID:
            // we have either start or end date cells, populate their date field
            cell.getDetailTextLabel().setText(dateFormatter.format(itemData.date));
        case OTHER_CELL_ID:
            // this cell is a non-date cell, just assign it's text label
            cell.getTextLabel().setText(itemData.title);
            break;
        default:
            break;
        }

        return cell;
    }

    /**
     * Adds or removes a UIDatePicker cell below the given indexPath.
     * 
     * @param indexPath The indexPath to reveal the UIDatePicker.
     */
    private void toggleDatePicker(NSIndexPath indexPath) {
        getTableView().beginUpdates();

        NSArray<NSIndexPath> indexPaths = new NSArray<>(NSIndexPath.createWithRow(indexPath.getRow() + 1, 0));

        // check if 'indexPath' has an attached date picker below it
        if (hasPickerForIndexPath(indexPath)) {
            // found a picker below it, so remove it
            getTableView().deleteRows(indexPaths, UITableViewRowAnimation.Fade);
        } else {
            // didn't find a picker below it, so we should insert it
            getTableView().insertRows(indexPaths, UITableViewRowAnimation.Fade);
        }

        getTableView().endUpdates();
    }

    /**
     * Reveals the date picker inline for the given indexPath, called by
     * "didSelectRowAtIndexPath".
     * 
     * @param indexPath The indexPath to reveal the UIDatePicker.
     */
    private void displayInlineDatePickerForRow(NSIndexPath indexPath) {
        // display the date picker inline with the table content
        getTableView().beginUpdates();

        boolean before = false; // indicates if the date picker is below
                                // "indexPath", help us determine which row to
                                // reveal
        if (hasInlineDatePicker()) {
            before = datePickerIndexPath.getRow() < indexPath.getRow();
        }

        boolean sameCellClicked = datePickerIndexPath != null && datePickerIndexPath.getRow() - 1 == indexPath.getRow();

        // remove any date picker cell if it exists
        if (hasInlineDatePicker()) {
            getTableView().deleteRows(
                    new NSArray<NSIndexPath>(NSIndexPath.createWithRow(datePickerIndexPath.getRow(), 0)),
                    UITableViewRowAnimation.Fade);
            datePickerIndexPath = null;
        }

        if (!sameCellClicked) {
            // hide the old date picker and display the new one
            long rowToReveal = before ? indexPath.getRow() - 1 : indexPath.getRow();
            NSIndexPath indexPathToReveal = NSIndexPath.createWithRow(rowToReveal, 0);

            toggleDatePicker(indexPathToReveal);
            datePickerIndexPath = NSIndexPath.createWithRow(indexPathToReveal.getRow() + 1, 0);
        }

        // always deselect the row containing the start or end date
        getTableView().deselectRow(indexPath, true);

        getTableView().endUpdates();

        // inform our date picker of the current date to match the current cell
        updateDatePicker();
    }

    /**
     * Reveals the UIDatePicker as an external slide-in view, iOS 6.1.x and
     * earlier, called by "didSelectRow".
     * 
     * @param indexPath The indexPath used to display the UIDatePicker.
     */
    private void displayExternalDatePickerForRow(NSIndexPath indexPath) {
        // first update the date picker's date value according to our model
        CellData itemData = data.get((int)indexPath.getRow());
        pickerView.setDate(itemData.date, true);

        // the date picker might already be showing, so don't add it to our view
        if (pickerView.getSuperview() == null) {
            CGRect startFrame = pickerView.getFrame();
            final CGRect endFrame = pickerView.getFrame();

            // the start position is below the bottom of the visible frame
            startFrame.getOrigin().setY(getView().getFrame().getHeight());

            // the end position is slid up by the height of the view
            endFrame.getOrigin().setY(startFrame.getOrigin().getY() - endFrame.getHeight());

            pickerView.setFrame(startFrame);

            getView().addSubview(pickerView);

            // animate the date picker into view
            UIView.animate(PICKER_ANIMATION_DURATION, new Runnable() {
                @Override
                public void run() {
                    pickerView.setFrame(endFrame);
                }
            }, new VoidBooleanBlock() {
                @Override
                public void invoke(boolean v) {
                    // add the "Done" button to the nav bar
                    getNavigationItem().setRightBarButtonItem(doneButton);
                }
            });
        }
    }

    @Override
    public void didSelectRow(UITableView tableView, NSIndexPath indexPath) {
        UITableViewCell cell = tableView.getCellForRow(indexPath);
        if (cell.getReuseIdentifier().equals(DATE_CELL_ID)) {
            if (Foundation.getMajorSystemVersion() >= 7) {
                displayInlineDatePickerForRow(indexPath);
            } else {
                displayExternalDatePickerForRow(indexPath);
            }
        } else {
            tableView.deselectRow(indexPath, true);
        }
    }

    /**
     * User chose to change the date by changing the values inside the
     * UIDatePicker.
     * 
     * @param control
     */
    @IBAction
    private void dateAction(UIControl sender) {
        NSIndexPath targetedCellIndexPath = null;

        if (hasInlineDatePicker()) {
            // inline date picker: update the cell's date "above" the
            // date picker cell
            targetedCellIndexPath = NSIndexPath.createWithRow(datePickerIndexPath.getRow() - 1, 0);
        } else {
            // external date picker: update the current "selected"
            // cell's date
            targetedCellIndexPath = getTableView().getIndexPathForSelectedRow();
        }

        UITableViewCell cell = getTableView().getCellForRow(targetedCellIndexPath);
        UIDatePicker targetedDatePicker = (UIDatePicker) sender;

        // update our data model
        CellData itemData = data.get((int)targetedCellIndexPath.getRow());
        itemData.date = targetedDatePicker.getDate();

        // update the cell's date string
        cell.getDetailTextLabel().setText(dateFormatter.format(targetedDatePicker.getDate()));
    }

    /**
     * User chose to finish using the UIDatePicker by pressing the "Done" button
     * (used only for "non-inline" date picker, iOS 6.1.x or earlier)
     * 
     * @param control
     */
    @IBAction
    private void doneAction(UIBarButtonItem sender) {
        final CGRect pickerFrame = pickerView.getFrame();
        pickerFrame.getOrigin().setY(getView().getFrame().getHeight());

        // animate the date picker out of view
        UIView.animate(PICKER_ANIMATION_DURATION, new Runnable() {
            @Override
            public void run() {
                pickerView.setFrame(pickerFrame);
            }
        }, new VoidBooleanBlock() {
            @Override
            public void invoke(boolean v) {
                pickerView.removeFromSuperview();
            }
        });

        // remove the "Done" button in the navigation bar
        getNavigationItem().setRightBarButtonItem(null);

        // deselect the current table cell
        NSIndexPath indexPath = getTableView().getIndexPathForSelectedRow();
        getTableView().deselectRow(indexPath, true);
    }

    @IBOutlet
    private void setPickerView(UIDatePicker pickerView) {
        this.pickerView = pickerView;
    }

    @IBOutlet
    private void setDoneButton(UIBarButtonItem doneButton) {
        this.doneButton = doneButton;
    }
}
