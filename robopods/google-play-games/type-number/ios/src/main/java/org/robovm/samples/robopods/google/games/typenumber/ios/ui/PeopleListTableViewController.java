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
 * Portions of this code is based on Google Inc's Google Play Games 'Type a Number' sample
 * which is copyright (C) 2015 Google Inc.
 */
package org.robovm.samples.robopods.google.games.typenumber.ios.ui;

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.pods.google.opensource.GTLPlusPeopleFeed;
import org.robovm.pods.google.opensource.GTLPlusPerson;
import org.robovm.pods.google.opensource.GTLQueryPlus;
import org.robovm.pods.google.opensource.GTLServiceCompletionHandler;
import org.robovm.pods.google.opensource.GTLServicePlus;
import org.robovm.pods.google.opensource.GTLServiceTicket;
import org.robovm.pods.google.plus.GPPSignIn;
import org.robovm.samples.robopods.google.games.typenumber.ios.Log;

@CustomClass("PeopleListTableViewController")
public class PeopleListTableViewController extends UITableViewController {
    private NSArray<GTLPlusPerson> myPeeps;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        GTLServicePlus plusService = GPPSignIn.getSharedInstance().getPlusService();
        // Let's just find my friends who have this app installed!

        GTLQueryPlus query = GTLQueryPlus.listPeople("me", "connected");
        query.setMaxResults(20);

        plusService.executeQuery(query, new GTLServiceCompletionHandler() {
            @SuppressWarnings("unchecked")
            @Override
            public void done(GTLServiceTicket ticket, NSObject object, NSError error) {
                if (error != null) {
                    Log.e("Error: %s", error);
                } else {
                    // Get an array of people from GTLPlusPeopleFeed
                    GTLPlusPeopleFeed peopleFeed = (GTLPlusPeopleFeed) object;
                    Log.d("Query results: %s", peopleFeed);
                    if (peopleFeed.getNextPageToken() != null) {
                        Log.d("Wow! There's more than our maxResults here. That's a lot of people!");
                    }
                    myPeeps = (NSArray<GTLPlusPerson>) peopleFeed.getItems();
                    Log.d("People list is %s", myPeeps);
                    getTableView().reloadData();
                }

            }
        });
    }

    @Override
    public void didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning();
        // Dispose of any resources that can be recreated.
        myPeeps = null;
    }

    @Override
    public long getNumberOfSections(UITableView tableView) {
        return 1;
    }

    @Override
    public long getNumberOfRowsInSection(UITableView tableView, long section) {
        return myPeeps.size();
    }

    @Override
    public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
        final String cellIdentifier = "personCell";
        GTLPlusPerson personToShow = myPeeps.get(indexPath.getRow());

        UITableViewCell cell = tableView.dequeueReusableCell(cellIdentifier);
        if (cell == null) {
            cell = new UITableViewCell(UITableViewCellStyle.Default, cellIdentifier);
        }
        if (personToShow.getImage() != null) {
            cell.getImageView().setImage(new UIImage(NSData.read(new NSURL(personToShow.getImage().getUrl()))));
        }

        Log.d("Person image is %s", personToShow.getImage().getUrl());

        cell.getTextLabel().setText(personToShow.getDisplayName());

        return cell;
    }
}
