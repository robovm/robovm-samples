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

import java.util.Objects;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIControl.OnEditingChangedListener;
import org.robovm.apple.uikit.UIPickerView;
import org.robovm.apple.uikit.UIPickerViewDataSourceAdapter;
import org.robovm.apple.uikit.UIPickerViewDelegateAdapter;
import org.robovm.apple.uikit.UISwitch;
import org.robovm.apple.uikit.UITextField;
import org.robovm.samples.contractr.core.Client;
import org.robovm.samples.contractr.core.ClientModel;
import org.robovm.samples.contractr.core.Task;
import org.robovm.samples.contractr.core.TaskModel;

/**
 * 
 */
public abstract class AbstractTaskViewController extends AbstractSettingsViewController {

    protected final ClientModel clientModel;
    protected final TaskModel taskModel;
    protected Client client;

    private UIPickerView clientPicker;
    private UITextField clientTextField;
    private UITextField titleTextField;
    private UITextField notesTextField;
    private UISwitch finishedSwitch;
    
    public AbstractTaskViewController(ClientModel clientModel, TaskModel taskModel) {
        this.clientModel = Objects.requireNonNull(clientModel, "clientModel");
        this.taskModel = Objects.requireNonNull(taskModel, "taskModel");
    }
    
    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        clientPicker = new UIPickerView(new CGRect(0, 0, 230, 44));
        clientPicker.setDataSource(new UIPickerViewDataSourceAdapter() {
            @Override
            public long getNumberOfComponents(UIPickerView pickerView) {
                return 1;
            }
            @Override
            public long getNumberOfRows(UIPickerView pickerView, long component) {
                return clientModel.count() + 1;
            }
        });
        clientPicker.setDelegate(new UIPickerViewDelegateAdapter() {
            @Override
            public String getRowTitle(UIPickerView pickerView, long row,
                    long component) {
                return row == 0 ? "" : clientModel.get((int) row - 1).getName();
            }
            @Override
            public void didSelectRow(UIPickerView pickerView, long row,
                    long component) {
                
                client = row == 0 ? null : clientModel.get((int) row - 1);
                clientTextField.setText(client == null ? "" : client.getName());
                clientTextField.resignFirstResponder();
            }
        });

        clientTextField = new UITextField();
        clientTextField.setTextAlignment(NSTextAlignment.Right);
        clientTextField.setPlaceholder("Select a client");
        clientTextField.setInputView(clientPicker);

        titleTextField = new UITextField();
        titleTextField.setTextAlignment(NSTextAlignment.Right);
        titleTextField.setPlaceholder("Enter task title");
        titleTextField.addOnEditingChangedListener(new OnEditingChangedListener() {
            public void onEditingChanged(UIControl control) {
                updateSaveButtonEnabled();
            }
        });
        notesTextField = new UITextField();
        notesTextField.setTextAlignment(NSTextAlignment.Right);
        notesTextField.setPlaceholder("No notes specified");

        finishedSwitch = new UISwitch();

        setCells(
            cell("Client", clientTextField),
            cell("Title", titleTextField),
            cell("Notes", notesTextField),
            cell("Finished", finishedSwitch)
        );
    }

    @Override
    public void viewWillAppear(boolean animated) {
        clientPicker.reloadAllComponents();
        super.viewWillAppear(animated);
        updateViewValuesWithTask(null);
    }

    @Override
    public void viewWillDisappear(boolean animated) {
        clientTextField.resignFirstResponder();
        titleTextField.resignFirstResponder();
        notesTextField.resignFirstResponder();
        
        super.viewWillDisappear(animated);
    }
    
    protected void onSave() {
        getNavigationController().popViewController(true);
    }
    
    protected void updateSaveButtonEnabled() {
        String title = titleTextField.getText();
        title = title == null ? "" : title.trim();
        boolean canSave = !title.isEmpty() && client != null;
        getNavigationItem().getRightBarButtonItem().setEnabled(canSave);
    }

    protected void updateViewValuesWithTask(Task task) {
        client = task == null ? null : task.getClient();
        int selectedRow = 0;
        if (client != null) {
            for (int i = 0; i < clientModel.count(); i++) {
                if (clientModel.get(i).equals(client)) {
                    selectedRow = i + 1;
                    break;
                }
            }
        }
        clientPicker.selectRow(selectedRow, 0, false);
        clientTextField.setText(task == null ? "" : task.getClient().getName());
        titleTextField.setText(task == null ? "" : task.getTitle());
        notesTextField.setText(task == null ? "" : task.getNotes());
        finishedSwitch.setOn(task == null ? false : task.isFinished());
        updateSaveButtonEnabled();
    }
    
    protected Task saveViewValuesToTask(Task task) {
        String title = titleTextField.getText();
        title = title == null ? "" : title.trim();
        String notes = notesTextField.getText();
        notes = notes == null ? "" : notes.trim();

        Client client = clientModel.get((int) clientPicker.getSelectedRow(0) - 1);
        task.setClient(client);
        task.setTitle(title);
        task.setNotes(notes);
        task.setFinished(finishedSwitch.isOn());
        
        return task;
    }

}
