/*
 * Copyright (C) 2015 RoboVM AB
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
 */
package org.robovm.answerme.app;

import java.util.Collections;
import java.util.List;

import org.robovm.answerme.core.AnswerMeService;
import org.robovm.answerme.core.api.Topic;
import org.robovm.apple.dispatch.DispatchQueue;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.foundation.NSURLSession;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UISearchBar;
import org.robovm.apple.uikit.UISearchBarDelegateAdapter;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.apple.uikit.UIViewContentMode;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;

/**
 * The main {@link UIViewController}.
 */
@CustomClass("AnswerMeViewController")
public class AnswerMeViewController extends UITableViewController {

    /**
     * Represents a cell in the {@link UITableView}. The storyboard has a
     * prototype {@link UITableViewCell} which binds to this class.
     */
    @CustomClass("TopicCell")
    public static class TopicCell extends UITableViewCell {
        @IBOutlet UIImageView icon;
        @IBOutlet UILabel text;
    }

    @IBOutlet UISearchBar searchBar;

    private List<Topic> topics = Collections.emptyList();

    private AnswerMeService answerMeService;

    public AnswerMeViewController() {
        this.answerMeService = new AnswerMeService();
    }

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        searchBar.setDelegate(new UISearchBarDelegateAdapter() {
            @Override
            public void searchButtonClicked(UISearchBar searchBar) {
                String query = searchBar.getText();

                answerMeService.search(query, (l -> {
                    topics = l;
                    DispatchQueue.getMainQueue().async(() -> {
                        getTableView().reloadData();
                    });
                }), t -> {
                    // TODO: Error handling
                    });
            }
        });
    }

    @Override
    public UITableViewCell getCellForRow(UITableView uiTableView, NSIndexPath nsIndexPath) {
        TopicCell cell = (TopicCell) uiTableView.dequeueReusableCell("cell");
        Topic topic = topics.get(nsIndexPath.getRow());
        cell.text.setText(topic.getDisplayText());
        cell.icon.setImage(null);

        if (topic.icon != null && topic.icon.url != null && !topic.icon.url.isEmpty()) {
            NSURLSession.getSharedSession().newDataTask(new NSURL(topic.icon.url), (data, response, error) -> {
                DispatchQueue.getMainQueue().async(() -> {
                    cell.icon.setContentMode(UIViewContentMode.ScaleAspectFill);
                    cell.icon.setImage(new UIImage(data));
                });
            }).resume();
        }

        return cell;
    }

    @Override
    public long getNumberOfSections(UITableView uiTableView) {
        // We only have 1 section.
        return 1;
    }

    @Override
    public long getNumberOfRowsInSection(UITableView uiTableView, long l) {
        // Each topic in the returned result becomes a row in the table view.
        return topics.size();
    }
}
