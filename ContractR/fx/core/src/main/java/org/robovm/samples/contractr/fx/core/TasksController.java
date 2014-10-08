/*
 * Copyright (C) 2014 Trillian Mobile AB
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

import java.util.AbstractList;
import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import net.engio.mbassy.listener.Handler;

import org.robovm.samples.contractr.core.Client;
import org.robovm.samples.contractr.core.ClientModel;
import org.robovm.samples.contractr.core.Task;
import org.robovm.samples.contractr.core.TaskModel;
import org.robovm.samples.contractr.core.TaskModel.SelectedTaskChangedEvent;

/**
 * 
 */
public class TasksController extends AbstractController {
    private ClientModel clientModel;
    private TaskModel taskModel;
    
    @FXML
    private TreeView<String> tasksTreeView;
    @FXML
    private Button addButton;
    @FXML
    private Button saveButton;
    @FXML
    private ComboBox<String> clientComboBox;
    @FXML
    private TextField titleTextField;
    @FXML
    private TextArea notesTextArea;
    @FXML
    private CheckBox finishedCheckBox;
    @FXML
    private BorderPane addEditPane;

    @Override
    public void beforeShow() {
        tasksTreeView.setRoot(buildTree(clientModel, taskModel));
        taskModel.selectTask(null);
        clientComboBox.setItems(FXCollections.observableList(new AbstractList<String>() {
            public String get(int index) {
                return clientModel.get(index).getName();
            }

            public int size() {
                return clientModel.count();
            }
        }));
        updateSaveButtonEnabled();
    }
    
    @Handler
    public void selectedTaskChanged(SelectedTaskChangedEvent event) {
        Task task = event.getNewTask();
        addEditPane.setVisible(task != null);
        titleTextField.setText(task == null ? "" : task.getTitle());
        clientComboBox.getSelectionModel().select(task == null ? -1 : clientModel.indexOf(task.getClient()));
        notesTextArea.setText(task == null ? "" : task.getNotes());
        finishedCheckBox.selectedProperty().set(task == null ? false : task.isFinished());
        updateSaveButtonEnabled();
    }
    
    private TreeItem<String> buildTree(ClientModel clientModel, TaskModel taskModel) {
        TreeItem<String> n = new TreeItem<String>("Root");
        for (int i = 0; i < clientModel.count(); i++) {
            n.getChildren().add(buildTree(clientModel.get(i), taskModel));
        }
        return n;
    }
    
    private TreeItem<String> buildTree(Client client, TaskModel taskModel) {
        TreeItem<String> n = new ClientTreeItem(client);
        n.setExpanded(true);
        for (Task task : taskModel.getForClient(client, false)) {
            n.getChildren().add(new TaskTreeItem(task));
        }
        return n;
    }
    
    private TreeItem<String> findTaskTreeItem(TreeItem<String> root, Task task) {
        for (TreeItem<String> child : root.getChildren()) {
            if (child instanceof TaskTreeItem) {
                if (((TaskTreeItem) child).getTask().equals(task)) {
                    return child;
                }
            } else {
                TreeItem<String> n = findTaskTreeItem(child, task);
                if (n != null) {
                    return n;
                }
            }
        }
        return null;
    }
    
    public void init(ClientModel clientModel, TaskModel taskModel) {
        this.clientModel = Objects.requireNonNull(clientModel, "clientModel");
        this.taskModel = Objects.requireNonNull(taskModel, "taskModel");

        taskModel.subscribe(this);
        
        tasksTreeView.getSelectionModel().selectedItemProperty().addListener((o, oldVal, newVal) -> {
            if (newVal instanceof TaskTreeItem) {
                taskModel.selectTask(((TaskTreeItem) newVal).getTask());
            } else {
                taskModel.selectTask(null);
                tasksTreeView.getSelectionModel().clearSelection();
            }
        });
        
        addButton.setOnAction(e -> {
            tasksTreeView.getSelectionModel().clearSelection();
            addEditPane.setVisible(true);
        });
        
        titleTextField.textProperty().addListener((o, oldVal, newVal) -> {
            updateSaveButtonEnabled();
        });
        clientComboBox.getSelectionModel().selectedIndexProperty().addListener((o, oldVal, newVal) -> {
            updateSaveButtonEnabled();
        });
        
        saveButton.setOnAction(e -> {
            Task task = taskModel.getSelectedTask();
            Client client = clientModel.get(clientComboBox.getSelectionModel().getSelectedIndex());
            if (task == null) {
                task = taskModel.create(client);
            } else {
                task.setClient(client);
            }
            task.setTitle(titleTextField.getText());
            task.setNotes(notesTextArea.getText());
            task.setFinished(finishedCheckBox.selectedProperty().get());
            taskModel.save(task);
            tasksTreeView.setRoot(buildTree(clientModel, taskModel));
            tasksTreeView.getSelectionModel().select(findTaskTreeItem(tasksTreeView.getRoot(), task));
        });
        
        addEditPane.setVisible(false);
    }

    protected void updateSaveButtonEnabled() {
        String title = titleTextField.getText();
        title = title == null ? "" : title.trim();
        saveButton.setDisable(title.isEmpty() 
                || clientComboBox.getSelectionModel().getSelectedIndex() == -1);
    }
    
    private static class ClientTreeItem extends TreeItem<String> {
        public ClientTreeItem(Client client) {
            super(client.getName());
        }
    }
    
    private static class TaskTreeItem extends TreeItem<String> {
        private final Task task;

        public TaskTreeItem(Task task) {
            super(task.getTitle());
            this.task = task;
        }
        
        public Task getTask() {
            return task;
        }
    }

}
