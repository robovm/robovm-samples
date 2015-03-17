/*
 * Copyright (C) 2014 RoboVM AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robovm.samples.contractr.ios;

import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.NSIndexPathExtensions;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonItem.OnClickListener;
import org.robovm.apple.uikit.UIBarButtonSystemItem;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellAccessoryType;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.samples.contractr.core.Client;
import org.robovm.samples.contractr.core.ClientModel;
import org.robovm.samples.contractr.core.Task;
import org.robovm.samples.contractr.core.TaskModel;

/**
 * 
 */
public class SelectTaskViewController extends UITableViewController {
    private final ClientModel clientModel;
    private final TaskModel taskModel;
    private final Runnable onDone;
    private Task selectedTask;

    public SelectTaskViewController(ClientModel clientModel, TaskModel taskModel, Runnable onDone) {
        this.clientModel = clientModel;
        this.taskModel = taskModel;
        this.onDone = onDone;
    }

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        getNavigationItem().setLeftBarButtonItem(
                new UIBarButtonItem(UIBarButtonSystemItem.Cancel, new OnClickListener() {
                    @Override
                    public void onClick(UIBarButtonItem barButtonItem) {
                        selectedTask = null;
                        dismissViewController(true, null);
                    }
                }));
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);
        selectedTask = null;
        getTableView().reloadData();
    }

    public Task getSelectedTask() {
        return selectedTask;
    }

    @Override
    public void didSelectRow(UITableView tableView, NSIndexPath indexPath) {
        int section = (int) NSIndexPathExtensions.getSection(indexPath);
        int row = (int) NSIndexPathExtensions.getRow(indexPath);
        Client client = clientModel.get(section);
        selectedTask = taskModel.getForClient(client, true).get(row);
        onDone.run();
        dismissViewController(true, null);
    }

    @Override
    public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
        int section = (int) NSIndexPathExtensions.getSection(indexPath);
        int row = (int) NSIndexPathExtensions.getRow(indexPath);
        UITableViewCell cell = tableView.dequeueReusableCell("cell");
        if (cell == null) {
            cell = new UITableViewCell(UITableViewCellStyle.Value1, "cell");
            cell.setAccessoryType(UITableViewCellAccessoryType.DisclosureIndicator);
        }
        Client client = clientModel.get(section);
        Task task = taskModel.getForClient(client, true).get(row);
        cell.getTextLabel().setText(task.getTitle());
        cell.getDetailTextLabel().setText(task.getTimeElapsed());
        return cell;
    }

    @Override
    public String getTitleForHeader(UITableView tableView, long section) {
        Client client = clientModel.get((int) section);
        return client.getName();
    }

    @Override
    public long getNumberOfSections(UITableView tableView) {
        return clientModel.count();
    }

    @Override
    public long getNumberOfRowsInSection(UITableView tableView, long section) {
        Client client = clientModel.get((int) section);
        return taskModel.getForClient(client, true).size();
    }
}
