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
 * Portions of this code is based on Apple Inc's DocInteraction sample (v1.6)
 * which is copyright (C) 2010-2014 Apple Inc.
 */

package org.robovm.samples.docinteraction.viewcontrollers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.foundation.NSByteCountFormatter;
import org.robovm.apple.foundation.NSByteCountFormatterCountStyle;
import org.robovm.apple.foundation.NSFileAttributes;
import org.robovm.apple.foundation.NSFileManager;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSPathUtilities;
import org.robovm.apple.foundation.NSSearchPathDirectory;
import org.robovm.apple.foundation.NSSearchPathDomainMask;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.quicklook.QLPreviewController;
import org.robovm.apple.quicklook.QLPreviewControllerDataSourceAdapter;
import org.robovm.apple.quicklook.QLPreviewControllerDelegateAdapter;
import org.robovm.apple.quicklook.QLPreviewItem;
import org.robovm.apple.uikit.UIDocumentInteractionController;
import org.robovm.apple.uikit.UIDocumentInteractionControllerDelegateAdapter;
import org.robovm.apple.uikit.UIGestureRecognizer;
import org.robovm.apple.uikit.UIGestureRecognizerState;
import org.robovm.apple.uikit.UILongPressGestureRecognizer;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellAccessoryType;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.samples.docinteraction.QLBasicPreviewItem;

public class DITableViewController extends UITableViewController {
    private static String[] documents = new String[] {"Text Document.txt", "Image Document.jpg", "PDF Document.pdf",
        "HTML Document.html"};

    private final List<NSURL> documentURLs = new ArrayList<>();
    private UIDocumentInteractionController docInteractionController;

    public DITableViewController () {
        setTitle("DocInteraction");

        // scan for existing documents
        readDirectory();
    }

    private void setupDocumentController (NSURL url) {
        // checks if docInteractionController has been initialized with the URL
        if (docInteractionController == null) {
            docInteractionController = UIDocumentInteractionController.create(url);
            docInteractionController.setDelegate(new UIDocumentInteractionControllerDelegateAdapter() {
                @Override
                public UIViewController getViewControllerForPreview (UIDocumentInteractionController controller) {
                    return DITableViewController.this;
                }
            });
        } else {
            docInteractionController.setURL(url);
        }
    }

    private String getApplicationDocumentsDirectory () {
        List<String> paths = NSPathUtilities.getSearchPathForDirectoriesInDomains(NSSearchPathDirectory.DocumentDirectory,
            NSSearchPathDomainMask.UserDomainMask, true);
        return paths.get(paths.size() - 1);
    }

    private void readDirectory () {
        documentURLs.clear();

        String documentsDirectoryPath = getApplicationDocumentsDirectory();

        NSArray<NSURL> documentsDirectoryContents = NSFileManager.getDefaultManager().getContentsOfDirectoryAtPath(
            documentsDirectoryPath);

        for (NSURL url : documentsDirectoryContents) {
            String filePath = documentsDirectoryPath + "/" + url.getLastPathComponent();
            NSURL fileURL = new NSURL(new File(filePath));

            boolean isDirectory = NSFileManager.getDefaultManager().isDirectoryAtPath(filePath);

            // proceed to add the document URL to our list (ignore the "Inbox" folder)
            if (!(isDirectory && url.getLastPathComponent().equals("Inbox"))) {
                documentURLs.add(fileURL);
            }
        }

        getTableView().reloadData();
    }

    @Override
    public long getNumberOfSections (UITableView tableView) {
        return 2;
    }

    @Override
    public long getNumberOfRowsInSection (UITableView tableView, long section) {
        // Initializing each section with a set of rows
        if (section == 0) {
            return documents.length;
        } else {
            return documentURLs.size();
        }
    }

    @Override
    public String getTitleForHeader (UITableView tableView, long section) {
        String title = null;
        // setting headers for each section
        if (section == 0) {
            title = "Example Documents";
        } else {
            if (documentURLs.size() > 0) {
                title = "Documents folder";
            }
        }

        return title;
    }

    @Override
    public UITableViewCell getCellForRow (UITableView tableView, NSIndexPath indexPath) {
        final String cellIdentifier = "cellID";
        UITableViewCell cell = tableView.dequeueReusableCell(cellIdentifier);

        if (cell == null) {
            cell = new UITableViewCell(UITableViewCellStyle.Subtitle, cellIdentifier);
            cell.setAccessoryType(UITableViewCellAccessoryType.DisclosureIndicator);
        }

        NSURL fileURL;
        if (indexPath.getSection() == 0) {
            // first section is our build-in documents
            fileURL = new NSURL(new File(NSBundle.getMainBundle().findResourcePath(documents[(int)indexPath.getRow()], null)));
        } else {
            // second section is the contents of the Documents folder
            fileURL = documentURLs.get((int)indexPath.getRow());
        }
        setupDocumentController(fileURL);

        // layout the cell
        cell.getTextLabel().setText(fileURL.getLastPathComponent());
        int iconCount = docInteractionController.getIcons().size();
        if (iconCount > 0) {
            cell.getImageView().setImage(docInteractionController.getIcons().get(iconCount - 1));
        }

        try {
            String fileURLString = docInteractionController.getURL().getPath();
            NSFileAttributes fileAttributes = NSFileManager.getDefaultManager().getAttributesOfItemAtPath(fileURLString);
            long fileSize = fileAttributes.getSize();
            String fileSizeStr = NSByteCountFormatter.format(fileSize, NSByteCountFormatterCountStyle.File);
            cell.getDetailTextLabel().setText(String.format("%s - %s", fileSizeStr, docInteractionController.getUTI()));

            // attach to our view any gesture recognizers that the UIDocumentInteractionController provides
            // cell.getImageView().setUserInteractionEnabled(true);
            // cell.getContentView().setGestureRecognizers(docInteractionController.getGestureRecognizers());
            // or
            // add a custom gesture recognizer in lieu of using the canned ones
            UILongPressGestureRecognizer longPressGesture = new UILongPressGestureRecognizer(
                new UIGestureRecognizer.OnGestureListener() {
                    // if we installed a custom UIGestureRecognizer (i.e. long-hold), then this would be called
                    @Override
                    public void onGesture (UIGestureRecognizer gestureRecognizer) {
                        UILongPressGestureRecognizer longPressGesture = (UILongPressGestureRecognizer)gestureRecognizer;
                        if (longPressGesture.getState() == UIGestureRecognizerState.Began) {
                            NSIndexPath cellIndexPath = getTableView().getIndexPathForRow(
                                longPressGesture.getLocationInView(getTableView()));

                            NSURL fileURL;
                            if (cellIndexPath.getSection() == 0) {
                                // for section 0, we preview the docs built into our app
                                fileURL = new NSURL(new File(NSBundle.getMainBundle().findResourcePath(
                                    documents[(int)cellIndexPath.getRow()], null)));
                            } else {
                                // for secton 1, we preview the docs found in the Documents folder
                                fileURL = documentURLs.get((int)cellIndexPath.getRow());
                            }
                            docInteractionController.setURL(fileURL);

                            docInteractionController.presentOptionsMenu(longPressGesture.getView().getFrame(),
                                longPressGesture.getView(), true);
                        }
                    }
                });
            cell.getImageView().addGestureRecognizer(longPressGesture);
            cell.getImageView().setUserInteractionEnabled(true);// this is by default false, so we need to turn it on
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cell;
    }

    @Override
    public double getHeightForRow (UITableView tableView, NSIndexPath indexPath) {
        return 58;
    }

    @Override
    public void didSelectRow (UITableView tableView, NSIndexPath indexPath) {
        // three ways to present a preview:
        // 1. Don't implement this method and simply attach the canned gestureRecognizers to the cell
        //
        // 2. Don't use canned gesture recognizers and simply use UIDocumentInteractionController's
        // presentPreview(animated) to get a preview for the document associated with this cell
        //
        // 3. Use the QLPreviewController to give the user preview access to the document associated
        // with this cell and all the other documents as well.

        // for case 2 use this, allowing UIDocumentInteractionController to handle the preview:
        /*
         * NSURL fileURL; if (indexPath.getSection() == 0) { fileURL = new NSURL(new
         * File(NSBundle.getMainBundle().findResourcePath(documents((int)indexPath.getRow()), null))); } else { fileURL =
         * documentURLs.get((int)indexPath.getRow()); } setupDocumentController(fileURL);
         * docInteractionController.presentPreview(true);
         */

        // for case 3 we use the QuickLook APIs directly to preview the document -
        QLPreviewController previewController = new QLPreviewController();
        previewController.setDataSource(new QLPreviewControllerDataSourceAdapter() {
            // Returns the number of items that the preview controller should preview
            @Override
            public long getNumberOfPreviewItems (QLPreviewController controller) {
                int numToPreview = 0;

                NSIndexPath selectedIndexPath = getTableView().getIndexPathForSelectedRow();
                if (selectedIndexPath.getSection() == 0) {
                    numToPreview = documents.length;
                } else {
                    numToPreview = documentURLs.size();
                }
                return numToPreview;
            }

            // returns the item that the preview controller should preview
            @Override
            public QLPreviewItem getPreviewItem (QLPreviewController controller, long index) {
                NSURL fileURL = null;

                NSIndexPath selectedIndexPath = getTableView().getIndexPathForSelectedRow();
                if (selectedIndexPath.getSection() == 0) {
                    fileURL = new NSURL(new File(NSBundle.getMainBundle().findResourcePath(documents[(int)index], null)));
                } else {
                    fileURL = documentURLs.get((int)index);
                }

                return new QLBasicPreviewItem(fileURL);
            }
        });
        previewController.setDelegate(new QLPreviewControllerDelegateAdapter() {
            @Override
            public void didDismiss (QLPreviewController controller) {
                // if the preview dismissed (done button touched), use this method to post-process previews
            }
        });

        // start previewing the document at the current section index
        previewController.setCurrentPreviewItemIndex(indexPath.getRow());
        getNavigationController().pushViewController(previewController, true);
    }
}
