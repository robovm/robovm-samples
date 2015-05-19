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
package org.robovm.samples.contractr.fx.core;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TreeView;
import javafx.stage.Window;

import org.robovm.samples.contractr.core.ClientModel;
import org.robovm.samples.contractr.core.Task;
import org.robovm.samples.contractr.core.TaskModel;
import org.robovm.samples.contractr.fx.core.TasksListHelper.TaskTreeItem;

/**
 * 
 */
public class SelectTaskController {
    
    @FXML
    private Parent rootPane;
    @FXML
    private Button cancelButton;
    @FXML
    private TreeView<String> tasksTreeView;
    private DialogPopup dialogPopup;
    private TasksListHelper tasksListHelper;
    private TaskSelectedCallback callback;
    
    public void init(ClientModel clientModel, TaskModel taskModel) {
        tasksListHelper = new TasksListHelper(clientModel, taskModel, tasksTreeView);
        
        tasksTreeView.getSelectionModel().selectedItemProperty().addListener((o, oldVal, newVal) -> {
            if (newVal instanceof TaskTreeItem) {
                callback.taskSelected(((TaskTreeItem) newVal).getTask());
                dialogPopup.hide();
            } else {
                tasksTreeView.getSelectionModel().clearSelection();
            }
        });
        
        dialogPopup = new DialogPopup();
        dialogPopup.getContent().addAll(rootPane);
        
        cancelButton.setOnAction((e) -> dialogPopup.hide());
    }

    public interface TaskSelectedCallback {
        void taskSelected(Task task);
    }
    
    public void select(Window window, TaskSelectedCallback c) {
        this.callback = c;
        tasksListHelper.reload();
        dialogPopup.show(window);
    }

}
