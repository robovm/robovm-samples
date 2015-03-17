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

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.NSIndexPathExtensions;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellAccessoryType;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UITableViewRowAnimation;
import org.robovm.samples.contractr.core.Client;
import org.robovm.samples.contractr.core.ClientModel;

/**
 * 
 */
public class ClientsViewController extends ListViewController {

    private final AddClientViewController addClientViewController;
    private final EditClientViewController editClientViewController;
    private final ClientModel clientModel;

    public ClientsViewController(ClientModel clientModel, AddClientViewController addClientViewController,
            EditClientViewController editClientViewController) {

        this.clientModel = clientModel;
        this.addClientViewController = addClientViewController;
        this.editClientViewController = editClientViewController;
    }

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        getNavigationItem().setTitle("Clients");
    }

    @Override
    protected void onAdd() {
        getNavigationController().pushViewController(addClientViewController, true);
    }

    @Override
    protected void onEdit(int section, int row) {
        Client client = clientModel.get(row);
        editClientViewController.setClient(client);
        getNavigationController().pushViewController(editClientViewController, true);
    }

    @Override
    protected void onDelete(int section, int row) {
        Client client = clientModel.get(row);
        clientModel.delete(client);
        getTableView().deleteRows(
                new NSArray<>(NSIndexPathExtensions.createIndexPathForRowInSection(row, section)),
                UITableViewRowAnimation.Automatic);
    }

    @Override
    public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
        UITableViewCell cell = tableView.dequeueReusableCell("cell");
        if (cell == null) {
            cell = new UITableViewCell(UITableViewCellStyle.Value1, "cell");
            cell.setAccessoryType(UITableViewCellAccessoryType.DisclosureIndicator);
        }
        Client client = clientModel.get((int) indexPath.getRow());
        cell.getTextLabel().setText(client.getName());
        return cell;
    }

    @Override
    public long getNumberOfRowsInSection(UITableView tableView, long section) {
        return clientModel.count();
    }
}
